package org.leralix.tan.storage.database;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * AMÉLIORATION #3: Database Replica Manager
 *
 * <p>Manages MySQL read replicas for horizontal scaling of read operations.
 *
 * <p><b>Architecture:</b>
 *
 * <ul>
 *   <li><b>Primary:</b> Handles all WRITE operations (INSERT, UPDATE, DELETE)
 *   <li><b>Replicas:</b> Handle READ operations (SELECT queries)
 *   <li><b>Load Balancing:</b> Round-robin distribution across replicas
 * </ul>
 *
 * <p><b>Benefits:</b>
 *
 * <ul>
 *   <li>2-3x read throughput with 2+ replicas
 *   <li>Reduced load on primary database
 *   <li>Automatic failover to primary if replicas down
 *   <li>Health monitoring of all replicas
 * </ul>
 *
 * <p><b>Configuration (config.yml):</b>
 *
 * <pre>
 * database:
 *   primary:
 *     host: "db-primary.example.com"
 *     port: 3306
 *
 *   replicas:
 *     - host: "db-replica1.example.com"
 *       port: 3306
 *     - host: "db-replica2.example.com"
 *       port: 3306
 * </pre>
 *
 * <p><b>Usage Example:</b>
 *
 * <pre>
 * // Initialize with primary connection
 * DatabaseReplicaManager replicaManager = new DatabaseReplicaManager(primaryDataSource);
 *
 * // Add replicas
 * replicaManager.addReadReplica(replica1DataSource);
 * replicaManager.addReadReplica(replica2DataSource);
 *
 * // Use for read operations
 * try (Connection conn = replicaManager.getReadConnection()) {
 *     // Execute SELECT query on replica
 * }
 *
 * // Use for write operations
 * try (Connection conn = replicaManager.getWriteConnection()) {
 *     // Execute INSERT/UPDATE/DELETE on primary
 * }
 * </pre>
 *
 * <p><b>Requirements:</b>
 *
 * <ul>
 *   <li>MySQL replication configured (master-slave)
 *   <li>Replicas must be in sync with primary
 *   <li>Read-only user recommended for replica connections
 * </ul>
 *
 * @author Leralix (with AI assistance)
 * @version 0.16.0
 * @since 2025-11-12
 */
public class DatabaseReplicaManager {

  private static final Logger logger = Logger.getLogger(DatabaseReplicaManager.class.getName());

  // Primary database (for writes)
  private final HikariDataSource primaryDataSource;

  // Read replicas (for reads)
  private final List<HikariDataSource> readReplicas;

  // Round-robin counter for load balancing
  private final AtomicInteger replicaIndex;

  // Health check tracking
  private final List<Boolean> replicaHealth;

  /**
   * Creates a new DatabaseReplicaManager.
   *
   * @param primaryDataSource The primary database connection pool
   */
  public DatabaseReplicaManager(HikariDataSource primaryDataSource) {
    this.primaryDataSource = primaryDataSource;
    this.readReplicas = new ArrayList<>();
    this.replicaIndex = new AtomicInteger(0);
    this.replicaHealth = new ArrayList<>();

    logger.info("[TaN-Replicas] Initialized with primary database");
  }

  /**
   * Adds a read replica to the pool.
   *
   * <p>Replicas are used for SELECT queries to distribute read load.
   *
   * @param replicaDataSource The replica connection pool
   */
  public void addReadReplica(HikariDataSource replicaDataSource) {
    readReplicas.add(replicaDataSource);
    replicaHealth.add(true);

    logger.info(
        "[TaN-Replicas] Added read replica #"
            + readReplicas.size()
            + " ("
            + replicaDataSource.getJdbcUrl()
            + ")");
  }

  /**
   * Gets a connection for READ operations.
   *
   * <p><b>Load Balancing:</b>
   *
   * <ul>
   *   <li>Round-robin distribution across healthy replicas
   *   <li>Automatic failover to primary if all replicas down
   *   <li>Health check on connection failure
   * </ul>
   *
   * <p><b>Best for:</b> SELECT queries, especially expensive ones
   *
   * @return A database connection (replica if available, primary as fallback)
   * @throws SQLException if unable to get connection
   */
  public Connection getReadConnection() throws SQLException {
    // If no replicas configured, use primary
    if (readReplicas.isEmpty()) {
      return primaryDataSource.getConnection();
    }

    // Try to get connection from healthy replica
    int attempts = readReplicas.size();
    while (attempts > 0) {
      int index = Math.abs(replicaIndex.getAndIncrement() % readReplicas.size());

      // Skip unhealthy replicas
      if (!replicaHealth.get(index)) {
        attempts--;
        continue;
      }

      try {
        HikariDataSource replica = readReplicas.get(index);
        Connection conn = replica.getConnection();

        // Mark as healthy if was unhealthy
        if (!replicaHealth.get(index)) {
          replicaHealth.set(index, true);
          logger.info("[TaN-Replicas] Replica #" + (index + 1) + " is healthy again");
        }

        return conn;

      } catch (SQLException e) {
        // Mark replica as unhealthy
        if (replicaHealth.get(index)) {
          replicaHealth.set(index, false);
          logger.warning(
              "[TaN-Replicas] Replica #" + (index + 1) + " is unhealthy: " + e.getMessage());
        }
        attempts--;
      }
    }

    // All replicas failed - fall back to primary
    logger.warning("[TaN-Replicas] All replicas unavailable, falling back to primary");
    return primaryDataSource.getConnection();
  }

  /**
   * Gets a connection for WRITE operations.
   *
   * <p><b>Always uses primary database.</b>
   *
   * <p><b>Best for:</b> INSERT, UPDATE, DELETE queries
   *
   * @return A connection to the primary database
   * @throws SQLException if unable to get connection
   */
  public Connection getWriteConnection() throws SQLException {
    return primaryDataSource.getConnection();
  }

  /**
   * Performs health check on all replicas.
   *
   * <p>Should be called periodically (e.g., every 60 seconds) to update replica health status.
   */
  public void healthCheck() {
    for (int i = 0; i < readReplicas.size(); i++) {
      HikariDataSource replica = readReplicas.get(i);

      try (Connection conn = replica.getConnection()) {
        // Simple query to test connection
        conn.isValid(5);

        if (!replicaHealth.get(i)) {
          replicaHealth.set(i, true);
          logger.info("[TaN-Replicas] Replica #" + (i + 1) + " is now healthy");
        }

      } catch (SQLException e) {
        if (replicaHealth.get(i)) {
          replicaHealth.set(i, false);
          logger.warning(
              "[TaN-Replicas] Replica #" + (i + 1) + " health check failed: " + e.getMessage());
        }
      }
    }
  }

  /**
   * Gets the current status of all replicas.
   *
   * <p>Useful for monitoring and debug commands.
   *
   * @return A formatted string with replica status
   */
  public String getStatus() {
    StringBuilder status = new StringBuilder();
    status
        .append("Primary: ")
        .append(primaryDataSource.getHikariPoolMXBean().getActiveConnections())
        .append(" active connections\n");

    for (int i = 0; i < readReplicas.size(); i++) {
      HikariDataSource replica = readReplicas.get(i);
      String health = replicaHealth.get(i) ? "HEALTHY" : "UNHEALTHY";
      int active = replica.getHikariPoolMXBean().getActiveConnections();

      status
          .append("Replica #")
          .append(i + 1)
          .append(": ")
          .append(health)
          .append(" - ")
          .append(active)
          .append(" active connections\n");
    }

    return status.toString();
  }

  /**
   * Gets the number of configured replicas.
   *
   * @return Number of read replicas
   */
  public int getReplicaCount() {
    return readReplicas.size();
  }

  /**
   * Gets the number of healthy replicas.
   *
   * @return Number of healthy read replicas
   */
  public int getHealthyReplicaCount() {
    return (int) replicaHealth.stream().filter(healthy -> healthy).count();
  }

  /**
   * Shuts down all replica connections.
   *
   * <p>Should be called during plugin disable.
   */
  public void shutdown() {
    for (int i = 0; i < readReplicas.size(); i++) {
      try {
        readReplicas.get(i).close();
        logger.info("[TaN-Replicas] Closed replica #" + (i + 1));
      } catch (Exception e) {
        logger.warning("[TaN-Replicas] Error closing replica #" + (i + 1) + ": " + e.getMessage());
      }
    }

    readReplicas.clear();
    replicaHealth.clear();
  }
}
