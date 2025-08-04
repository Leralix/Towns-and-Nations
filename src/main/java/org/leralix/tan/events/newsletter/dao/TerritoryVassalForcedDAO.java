package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.TerritoryVassalForcedNews;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class TerritoryVassalForcedDAO extends NewsletterSubDAO<TerritoryVassalForcedNews> {

    private static final String TABLE_NAME = "territory_vassal_forced_newsletter";

    public TerritoryVassalForcedDAO(DataSource connection) {
        super(connection);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "proposingTerritoryID VARCHAR(36) NOT NULL, " +
                "forcedTerritoryID VARCHAR(36) NOT NULL" +
                ")";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create table " + TABLE_NAME, e);
        }
    }

    @Override
    public void save(TerritoryVassalForcedNews newsletter) {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, proposingTerritoryID, forcedTerritoryID) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getProposingTerritoryID());
            ps.setString(3, newsletter.getForcedTerritoryID());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save to table " + TABLE_NAME, e);
        }
    }

    @Override
    public TerritoryVassalForcedNews load(UUID id, long date) {
        String sql = "SELECT proposingTerritoryID, forcedTerritoryID FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String proposingTerritoryID = rs.getString("proposingTerritoryID");
                    String forcedTerritoryID = rs.getString("forcedTerritoryID");
                    return new TerritoryVassalForcedNews(id, date, proposingTerritoryID, forcedTerritoryID);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load from table " + TABLE_NAME, e);
        }
        return null;
    }

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
