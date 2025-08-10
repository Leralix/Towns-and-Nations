package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.AttackDeclaredNewsletter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class AttackDeclaredDAO extends NewsletterSubDAO<AttackDeclaredNewsletter> {

    private static final String TABLE_NAME = "attack_declared_newsletter";

    public AttackDeclaredDAO(DataSource connection) {
        super(connection);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "attackingTerritoryID VARCHAR(36) NOT NULL, " +
                "defendingTerritoryID VARCHAR(36) NOT NULL" +
                ")";

        try (Connection conn = dataSource.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create player diplomacy accepted newsletter table", e);
        }
    }

    @Override
    public void save(AttackDeclaredNewsletter newsletter) {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, attackingTerritoryID, defendingTerritoryID) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getAttackingTerritoryID());
            ps.setString(3, newsletter.getDefendingTerritoryID());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save player application newsletter", e);
        }
    }


    @Override
    public AttackDeclaredNewsletter load(UUID id, long date) {
        String sql = "SELECT attackingTerritoryID, defendingTerritoryID FROM " + TABLE_NAME + " WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    String attackingTerritoryID = rs.getString("attackingTerritoryID");
                    String defendingTerritoryID = rs.getString("defendingTerritoryID");
                    return new AttackDeclaredNewsletter(id, date, attackingTerritoryID, defendingTerritoryID);
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
