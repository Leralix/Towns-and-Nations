package org.leralix.tan.newsletter.storage;

import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.newsletter.news.DiplomacyAcceptedNews;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class DiplomacyAcceptedDAO extends NewsletterSubDAO<DiplomacyAcceptedNews> {


    public DiplomacyAcceptedDAO(Connection connection) {
        super(connection);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS diplomacy_accepted_newsletter (" +
                "id UUID PRIMARY KEY, " +
                "proposingTerritoryID VARCHAR(36) NOT NULL, " +
                "receivingTerritoryID VARCHAR(36) NOT NULL, " +
                "wantedRelation VARCHAR(36) NOT NULL, " +
                "isWorseRelation BOOLEAN NOT NULL" +
                ")";

        try (var ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create player diplomacy accepted newsletter table", e);
        }
    }

    @Override
    public void save(DiplomacyAcceptedNews newsletter) {
        String sql = "INSERT INTO diplomacy_accepted_newsletter (id, proposingTerritoryID, receivingTerritoryID, wantedRelation, isWorseRelation) VALUES (?, ?, ?, ?, ?)";

        try (var ps = connection.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getProposingTerritoryID());
            ps.setString(3, newsletter.getReceivingTerritoryID());
            ps.setString(4, newsletter.getWantedRelation().toString());
            ps.setBoolean(5, newsletter.isRelationWorse());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to save player application newsletter", e);
        }
    }

    @Override
    public DiplomacyAcceptedNews load(UUID id, long date) {
        String sql = "SELECT proposingTerritoryID, receivingTerritoryID, wantedRelation, isWorseRelation FROM diplomacy_accepted_newsletter WHERE id = ?";
        try (var ps = connection.prepareStatement(sql)) {
            ps.setObject(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) {
                String proposingTerritoryID = rs.getString("playerID");
                String receivingTerritoryID = rs.getString("townID");
                TownRelation wantedRelation = TownRelation.valueOf(rs.getString("wantedRelation"));
                boolean isRelationWorse = rs.getBoolean("isWorseRelation");
                return new DiplomacyAcceptedNews(id, date, proposingTerritoryID, receivingTerritoryID, wantedRelation, isRelationWorse);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load player application newsletter", e);
        }
        return null;
    }
}
