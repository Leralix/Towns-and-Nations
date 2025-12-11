package org.leralix.tan.storage.database;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EventSourcingManager {

  private static final Logger logger = Logger.getLogger(EventSourcingManager.class.getName());

  private final HikariDataSource dataSource;

  public static class Event {
    private final long eventId;
    private final String aggregateId;
    private final String eventType;
    private final String eventData;
    private final Timestamp createdAt;

    public Event(
        long eventId, String aggregateId, String eventType, String eventData, Timestamp createdAt) {
      this.eventId = eventId;
      this.aggregateId = aggregateId;
      this.eventType = eventType;
      this.eventData = eventData;
      this.createdAt = createdAt;
    }

    public long getEventId() {
      return eventId;
    }

    public String getAggregateId() {
      return aggregateId;
    }

    public String getEventType() {
      return eventType;
    }

    public String getEventData() {
      return eventData;
    }

    public Timestamp getCreatedAt() {
      return createdAt;
    }
  }

  public EventSourcingManager(HikariDataSource dataSource) {
    this.dataSource = dataSource;
    initializeSchema();
  }

  private void initializeSchema() {
    String createTableSql;

    if (isDatabaseMySQL()) {
      createTableSql =
          """
            CREATE TABLE IF NOT EXISTS transaction_events (
                event_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                aggregate_id VARCHAR(36) NOT NULL,
                event_type VARCHAR(50) NOT NULL,
                event_data TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                INDEX idx_aggregate (aggregate_id),
                INDEX idx_type (event_type),
                INDEX idx_created (created_at)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
            """;
    } else {
      createTableSql =
          """
            CREATE TABLE IF NOT EXISTS transaction_events (
                event_id INTEGER PRIMARY KEY AUTOINCREMENT,
                aggregate_id VARCHAR(36) NOT NULL,
                event_type VARCHAR(50) NOT NULL,
                event_data TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
    }

    try (Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement()) {

      stmt.execute(createTableSql);
      logger.info("[TaN-EventSourcing] Schema initialized");

    } catch (SQLException e) {
      logger.severe("[TaN-EventSourcing] Failed to initialize schema: " + e.getMessage());
    }
  }

  private boolean isDatabaseMySQL() {
    try (Connection conn = dataSource.getConnection()) {
      String dbProductName = conn.getMetaData().getDatabaseProductName().toLowerCase();
      return dbProductName.contains("mysql") || dbProductName.contains("mariadb");
    } catch (SQLException e) {
      return false;
    }
  }

  public long createEvent(String aggregateId, String eventType, String eventData)
      throws SQLException {

    String sql =
        "INSERT INTO transaction_events (aggregate_id, event_type, event_data) VALUES (?, ?, ?)";

    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      stmt.setString(1, aggregateId);
      stmt.setString(2, eventType);
      stmt.setString(3, eventData);

      stmt.executeUpdate();

      try (ResultSet rs = stmt.getGeneratedKeys()) {
        if (rs.next()) {
          long eventId = rs.getLong(1);
          logger.fine(
              "[TaN-EventSourcing] Created event #"
                  + eventId
                  + " for "
                  + aggregateId
                  + ": "
                  + eventType);
          return eventId;
        }
      }
    }

    throw new SQLException("Failed to create event - no ID generated");
  }

  public List<Event> getEvents(String aggregateId) throws SQLException {
    String sql =
        "SELECT event_id, aggregate_id, event_type, event_data, created_at "
            + "FROM transaction_events WHERE aggregate_id = ? ORDER BY event_id ASC";

    List<Event> events = new ArrayList<>();

    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, aggregateId);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          events.add(
              new Event(
                  rs.getLong("event_id"),
                  rs.getString("aggregate_id"),
                  rs.getString("event_type"),
                  rs.getString("event_data"),
                  rs.getTimestamp("created_at")));
        }
      }
    }

    logger.fine("[TaN-EventSourcing] Retrieved " + events.size() + " events for " + aggregateId);

    return events;
  }

  public List<Event> getEventsByType(String eventType, int limit) throws SQLException {
    String sql =
        "SELECT event_id, aggregate_id, event_type, event_data, created_at "
            + "FROM transaction_events WHERE event_type = ? "
            + "ORDER BY event_id DESC LIMIT ?";

    List<Event> events = new ArrayList<>();

    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, eventType);
      stmt.setInt(2, limit);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          events.add(
              new Event(
                  rs.getLong("event_id"),
                  rs.getString("aggregate_id"),
                  rs.getString("event_type"),
                  rs.getString("event_data"),
                  rs.getTimestamp("created_at")));
        }
      }
    }

    return events;
  }

  public List<Event> getEventsInTimeRange(String aggregateId, Instant startTime, Instant endTime)
      throws SQLException {

    String sql =
        "SELECT event_id, aggregate_id, event_type, event_data, created_at "
            + "FROM transaction_events "
            + "WHERE aggregate_id = ? AND created_at BETWEEN ? AND ? "
            + "ORDER BY event_id ASC";

    List<Event> events = new ArrayList<>();

    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, aggregateId);
      stmt.setTimestamp(2, Timestamp.from(startTime));
      stmt.setTimestamp(3, Timestamp.from(endTime));

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          events.add(
              new Event(
                  rs.getLong("event_id"),
                  rs.getString("aggregate_id"),
                  rs.getString("event_type"),
                  rs.getString("event_data"),
                  rs.getTimestamp("created_at")));
        }
      }
    }

    return events;
  }

  public int purgeOldEvents(int retentionDays) throws SQLException {
    String sql =
        "DELETE FROM transaction_events WHERE created_at < DATE_SUB(NOW(), INTERVAL ? DAY)";

    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, retentionDays);
      int deleted = stmt.executeUpdate();

      logger.info(
          "[TaN-EventSourcing] Purged "
              + deleted
              + " events older than "
              + retentionDays
              + " days");

      return deleted;
    }
  }

  public String getStats() throws SQLException {
    String sql =
        """
            SELECT
                COUNT(*) as total_events,
                COUNT(DISTINCT aggregate_id) as unique_aggregates,
                MIN(created_at) as oldest_event,
                MAX(created_at) as newest_event
            FROM transaction_events
            """;

    try (Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      if (rs.next()) {
        return String.format(
            "Total Events: %d | Unique Entities: %d | Oldest: %s | Newest: %s",
            rs.getLong("total_events"),
            rs.getLong("unique_aggregates"),
            rs.getTimestamp("oldest_event"),
            rs.getTimestamp("newest_event"));
      }
    }

    return "No events in store";
  }
}
