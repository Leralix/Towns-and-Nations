package org.leralix.tan.events.newsletter.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import javax.sql.DataSource;
import org.leralix.tan.events.newsletter.news.PlayerJoinRequestNews;

public class PlayerApplicationDAO extends NewsletterSubDAO<PlayerJoinRequestNews> {

  private static final String TABLE_NAME = "player_application_newsletter";

  public PlayerApplicationDAO(DataSource connection) {
    super(connection);
  }

  @Override
  protected void createTableIfNotExists() {
    String sql =
        "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME
            + " ("
            + "id VARCHAR(36) PRIMARY KEY, "
            + "playerID VARCHAR(36) NOT NULL, "
            + "townID VARCHAR(36) NOT NULL)";

    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create " + TABLE_NAME + " table", e);
    }
  }

  @Override
  public void save(PlayerJoinRequestNews newsletter) {
    String sql = "INSERT INTO " + TABLE_NAME + " (id, playerID, townID) VALUES (?, ?, ?)";

    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, newsletter.getId().toString());
      ps.setString(2, newsletter.getPlayerID());
      ps.setString(3, newsletter.getTownID());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to save newsletter to " + TABLE_NAME, e);
    }
  }

  @Override
  public PlayerJoinRequestNews load(UUID id, long date) {
    String sql = "SELECT playerID, townID FROM " + TABLE_NAME + " WHERE id = ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, id.toString());
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          String playerID = rs.getString("playerID");
          String townID = rs.getString("townID");
          return new PlayerJoinRequestNews(id, date, playerID, townID);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to load newsletter from " + TABLE_NAME, e);
    }
    return null;
  }

  @Override
  public void delete(UUID id) {
    String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, id.toString());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to delete newsletter from " + TABLE_NAME, e);
    }
  }
}
