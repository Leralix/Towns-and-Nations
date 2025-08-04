package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.RegionCreationNews;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerCreateRegionDAO extends NewsletterSubDAO<RegionCreationNews> {

    private static final String TABLE_NAME = "player_create_region_newsletter";

    public PlayerCreateRegionDAO(DataSource connection) {
        super(connection);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "playerID VARCHAR(36) NOT NULL, " +
                "regionID VARCHAR(36) NOT NULL)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create " + TABLE_NAME + " table", e);
        }
    }

    @Override
    public void save(RegionCreationNews newsletter) {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, playerID, regionID) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getPlayerID());
            ps.setString(3, newsletter.getRegionID());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save newsletter to " + TABLE_NAME, e);
        }
    }

    @Override
    public RegionCreationNews load(UUID id, long date) {
        String sql = "SELECT playerID, regionID FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String playerID = rs.getString("playerID");
                    String regionID = rs.getString("regionID");
                    return new RegionCreationNews(id, date, playerID, regionID);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load newsletter from " + TABLE_NAME, e);
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
            throw new RuntimeException("Failed to delete newsletter from " + TABLE_NAME, e);
        }
    }
}
