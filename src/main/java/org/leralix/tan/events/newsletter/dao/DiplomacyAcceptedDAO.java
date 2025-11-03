package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.events.newsletter.news.DiplomacyAcceptedNews;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DiplomacyAcceptedDAO extends NewsletterSubDAO<DiplomacyAcceptedNews> {

    private static final String TABLE_NAME = "diplomacy_accepted_newsletter";

    public DiplomacyAcceptedDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "proposingTerritoryID VARCHAR(36) NOT NULL, " +
                "receivingTerritoryID VARCHAR(36) NOT NULL, " +
                "wantedRelation VARCHAR(36) NOT NULL, " +
                "isWorseRelation BOOLEAN NOT NULL" +
                ")";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create player diplomacy accepted newsletter table", e);
        }
    }

    @Override
    public void save(DiplomacyAcceptedNews newsletter, Connection conn) {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, proposingTerritoryID, receivingTerritoryID, wantedRelation, isWorseRelation) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getProposingTerritoryID());
            ps.setString(3, newsletter.getReceivingTerritoryID());
            ps.setString(4, newsletter.getWantedRelation().toString());
            ps.setBoolean(5, newsletter.isRelationWorse());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save player application newsletter", e);
        }
    }

    @Override
    public DiplomacyAcceptedNews load(UUID id, long date, Connection conn) {
        String sql = "SELECT proposingTerritoryID, receivingTerritoryID, wantedRelation, isWorseRelation FROM " + TABLE_NAME + " WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String proposingTerritoryID = rs.getString("proposingTerritoryID");
                    String receivingTerritoryID = rs.getString("receivingTerritoryID");
                    TownRelation wantedRelation = TownRelation.valueOf(rs.getString("wantedRelation"));
                    boolean isRelationWorse = rs.getBoolean("isWorseRelation");
                    return new DiplomacyAcceptedNews(id, date, proposingTerritoryID, receivingTerritoryID, wantedRelation, isRelationWorse);
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
