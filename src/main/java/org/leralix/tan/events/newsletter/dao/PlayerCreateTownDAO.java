package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.TownCreatedNews;

import javax.sql.DataSource;
import java.sql.*;
import java.util.UUID;

public class PlayerCreateTownDAO extends NewsletterSubDAO<TownCreatedNews> {

    private static final String TABLE_NAME = "player_create_town_newsletter";

    public PlayerCreateTownDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "playerID VARCHAR(36) NOT NULL, " +
                "townID VARCHAR(36) NOT NULL)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create " + TABLE_NAME + " table", e);
        }
    }

    @Override
    public void save(TownCreatedNews newsletter) {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, playerID, townID) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getPlayerID());
            ps.setString(3, newsletter.getTownID());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save newsletter in " + TABLE_NAME, e);
        }
    }

    @Override
    public TownCreatedNews load(UUID id, long date) {
        String sql = "SELECT playerID, townID FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String playerID = rs.getString("playerID");
                    String townID = rs.getString("townID");
                    return new TownCreatedNews(id, date, playerID, townID);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load newsletter from " + TABLE_NAME, e);
        }
        return null;
    }

    public void delete(UUID id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete newsletter from " + TABLE_NAME, e);
        }
    }
}
