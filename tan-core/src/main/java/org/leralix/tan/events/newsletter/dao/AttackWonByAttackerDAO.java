package org.leralix.tan.events.newsletter.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import javax.sql.DataSource;
import org.leralix.tan.events.newsletter.news.AttackWonByAttackerNewsletter;

public class AttackWonByAttackerDAO extends NewsletterSubDAO<AttackWonByAttackerNewsletter> {

  private static final String TABLE_NAME = "attack_won_by_attackers_newsletter";

  public AttackWonByAttackerDAO(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  protected void createTableIfNotExists() {
    String sql =
        "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME
            + " ("
            + "id VARCHAR(36) PRIMARY KEY, "
            + "attackingTerritoryID VARCHAR(36) NOT NULL, "
            + "defendingTerritoryID VARCHAR(36) NOT NULL"
            + ")";

    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create player diplomacy accepted newsletter table", e);
    }
  }

  @Override
  public void save(AttackWonByAttackerNewsletter newsletter) {
    String sql =
        "INSERT INTO "
            + TABLE_NAME
            + " (id, attackingTerritoryID, defendingTerritoryID) VALUES (?, ?, ?)";

    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, newsletter.getId().toString());
      ps.setString(2, newsletter.getAttackingTerritoryID());
      ps.setString(3, newsletter.getDefendingTerritoryID());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to save player application newsletter", e);
    }
  }

  @Override
  public AttackWonByAttackerNewsletter load(UUID id, long date) {
    String sql =
        "SELECT attackingTerritoryID, defendingTerritoryID FROM " + TABLE_NAME + " WHERE id = ?";

    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, id.toString());
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          String attackingTerritoryID = rs.getString("attackingTerritoryID");
          String defendingTerritoryID = rs.getString("defendingTerritoryID");
          return new AttackWonByAttackerNewsletter(
              id, date, attackingTerritoryID, defendingTerritoryID);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to load player application newsletter", e);
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
      throw new RuntimeException("Failed to delete from table " + TABLE_NAME, e);
    }
  }
}
