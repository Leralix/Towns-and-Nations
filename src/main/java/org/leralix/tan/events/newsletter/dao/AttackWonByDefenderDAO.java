package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.AttackWonByDefenderNewsletter;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.UUID;

public class AttackWonByDefenderDAO extends NewsletterSubDAO<AttackWonByDefenderNewsletter> {


    public AttackWonByDefenderDAO(DataSource connection) {
        super(connection);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS attack_won_by_defender_newsletter (" +
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
    public void save(AttackWonByDefenderNewsletter newsletter) {
        String sql = "INSERT INTO attack_won_by_defender_newsletter (id, attackingTerritoryID, defendingTerritoryID) VALUES (?, ?, ?)";

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
    public AttackWonByDefenderNewsletter load(UUID id, long date) {
        String sql = "SELECT attackingTerritoryID, defendingTerritoryID,FROM attack_won_by_defender_newsletter WHERE id = ?";
        try (var ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.setObject(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) {
                String attackingTerritoryID = rs.getString("attackingTerritoryID");
                String defendingTerritoryID = rs.getString("defendingTerritoryID");
                return new AttackWonByDefenderNewsletter(id, date, attackingTerritoryID, defendingTerritoryID);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load player application newsletter", e);
        }
        return null;
    }
}
