package org.leralix.tan.storage.database;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class DatabaseReplicaManager {

  private static final Logger logger = Logger.getLogger(DatabaseReplicaManager.class.getName());

  private final HikariDataSource primaryDataSource;

  private final List<HikariDataSource> readReplicas;

  private final AtomicInteger replicaIndex;

  private final List<Boolean> replicaHealth;

  public DatabaseReplicaManager(HikariDataSource primaryDataSource) {
    this.primaryDataSource = primaryDataSource;
    this.readReplicas = new ArrayList<>();
    this.replicaIndex = new AtomicInteger(0);
    this.replicaHealth = new ArrayList<>();

    logger.info("[TaN-Replicas] Initialized with primary database");
  }

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

  public Connection getReadConnection() throws SQLException {
    if (readReplicas.isEmpty()) {
      return primaryDataSource.getConnection();
    }

    int attempts = readReplicas.size();
    while (attempts > 0) {
      int index = Math.abs(replicaIndex.getAndIncrement() % readReplicas.size());

      if (!replicaHealth.get(index)) {
        attempts--;
        continue;
      }

      try {
        HikariDataSource replica = readReplicas.get(index);
        Connection conn = replica.getConnection();

        if (!replicaHealth.get(index)) {
          replicaHealth.set(index, true);
          logger.info("[TaN-Replicas] Replica #" + (index + 1) + " is healthy again");
        }

        return conn;

      } catch (SQLException e) {
        if (replicaHealth.get(index)) {
          replicaHealth.set(index, false);
          logger.warning(
              "[TaN-Replicas] Replica #" + (index + 1) + " is unhealthy: " + e.getMessage());
        }
        attempts--;
      }
    }

    logger.warning("[TaN-Replicas] All replicas unavailable, falling back to primary");
    return primaryDataSource.getConnection();
  }

  public Connection getWriteConnection() throws SQLException {
    return primaryDataSource.getConnection();
  }

  public void healthCheck() {
    for (int i = 0; i < readReplicas.size(); i++) {
      HikariDataSource replica = readReplicas.get(i);

      try (Connection conn = replica.getConnection()) {
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

  public int getReplicaCount() {
    return readReplicas.size();
  }

  public int getHealthyReplicaCount() {
    return (int) replicaHealth.stream().filter(healthy -> healthy).count();
  }

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
