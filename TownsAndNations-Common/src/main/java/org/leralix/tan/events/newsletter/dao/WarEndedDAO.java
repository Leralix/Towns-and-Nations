
package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.WarEndedNewsletter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class WarEndedDAO extends NewsletterSubDAO<WarEndedNewsletter> {

    private static final String TABLE_NAME = "attack_ended_newsletter";

    public WarEndedDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "winningTerritoryID VARCHAR(36) NOT NULL, " +
                "surrenderedTerritoryID VARCHAR(36) NOT NULL," +
                "nbAppliedWargoals INT NOT NULL" +
                ")";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create player diplomacy accepted newsletter table", e);
        }
    }


    @Override
    public void save(WarEndedNewsletter newsletter, Connection conn) {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, winningTerritoryID, surrenderedTerritoryID, nbAppliedWargoals) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getWinningTerritoryID());
            ps.setString(3, newsletter.getDefeatedTerritoryID());
            ps.setInt(4, newsletter.getNbAppliedWargoals());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save player application newsletter", e);
        }
    }

    @Override
    public WarEndedNewsletter load(UUID id, long date, Connection conn) {
        String sql = "SELECT winningTerritoryID, surrenderedTerritoryID FROM " + TABLE_NAME + " WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String winningTerritoryID = rs.getString("winningTerritoryID");
                    String surrenderedTerritoryID = rs.getString("surrenderedTerritoryID");
                    int nbAppliedWargoals = rs.getInt("nbAppliedWargoals");
                    return new WarEndedNewsletter(id, date, winningTerritoryID, surrenderedTerritoryID, nbAppliedWargoals);
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
            ps.setObject(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete from table " + TABLE_NAME, e);
        }
    }
}
