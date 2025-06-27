package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.AttackCancelledByDefenderNewsletter;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.UUID;

public class AttackCancelledDAO extends NewsletterSubDAO<AttackCancelledByDefenderNewsletter> {


    public AttackCancelledDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS attack_cancelled_newsletter (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "attackingTerritoryID VARCHAR(36) NOT NULL, " +
                "defendingTerritoryID VARCHAR(36) NOT NULL" +
                ")";

        try (var ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create player diplomacy accepted newsletter table", e);
        }
    }

    @Override
    public void save(AttackCancelledByDefenderNewsletter newsletter) {
        String sql = "INSERT INTO attack_cancelled_newsletter (id, attackingTerritoryID, defendingTerritoryID) VALUES (?, ?, ?)";

        try (var ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getAttackingTerritoryID());
            ps.setString(3, newsletter.getDefendingTerritoryID());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to save player application newsletter", e);
        }
    }

    @Override
    public AttackCancelledByDefenderNewsletter load(UUID id, long date) {
        String sql = "SELECT attackingTerritoryID, defendingTerritoryID,FROM attack_cancelled_newsletter WHERE id = ?";
        try (var ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.setObject(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) {
                String attackingTerritoryID = rs.getString("attackingTerritoryID");
                String defendingTerritoryID = rs.getString("defendingTerritoryID");
                return new AttackCancelledByDefenderNewsletter(id, date, attackingTerritoryID, defendingTerritoryID);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load player application newsletter", e);
        }
        return null;
    }
}
