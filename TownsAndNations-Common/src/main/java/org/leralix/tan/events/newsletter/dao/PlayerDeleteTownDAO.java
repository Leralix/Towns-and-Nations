package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.TownDeletedNews;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerDeleteTownDAO extends NewsletterSubDAO<TownDeletedNews> {

    private static final String TABLE_NAME = "player_delete_town_newsletter";

    public PlayerDeleteTownDAO(DataSource connection) {
        super(connection);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "playerID VARCHAR(36) NOT NULL, " +
                "oldTownName VARCHAR(36) NOT NULL" +
                ")";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create " + TABLE_NAME + " table", e);
        }
    }

    @Override
    public void save(TownDeletedNews newsletter, Connection conn) {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, playerID, oldTownName) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getPlayerID());
            ps.setString(3, newsletter.getOldTownName());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save newsletter to " + TABLE_NAME, e);
        }
    }

    @Override
    public TownDeletedNews load(UUID id, long date, Connection conn) {
        String sql = "SELECT playerID, oldTownName FROM " + TABLE_NAME + " WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String playerID = rs.getString("playerID");
                    String oldTownName = rs.getString("oldTownName");
                    return new TownDeletedNews(id, date, playerID, oldTownName);
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
            ps.setObject(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete newsletter from " + TABLE_NAME, e);
        }
    }
}
