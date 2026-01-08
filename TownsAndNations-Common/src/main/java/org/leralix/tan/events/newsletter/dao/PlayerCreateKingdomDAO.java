package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.KingdomCreationNews;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerCreateKingdomDAO extends NewsletterSubDAO<KingdomCreationNews> {

    private static final String TABLE_NAME = "player_create_kingdom_newsletter";

    public PlayerCreateKingdomDAO(DataSource connection) {
        super(connection);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "playerID VARCHAR(36) NOT NULL, " +
                "kingdomID VARCHAR(36) NOT NULL)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new NewsletterDaoException("Failed to create " + TABLE_NAME + " table", e);
        }
    }

    @Override
    public void save(KingdomCreationNews newsletter, Connection conn) {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, playerID, kingdomID) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getPlayerID());
            ps.setString(3, newsletter.getKingdomID());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new NewsletterDaoException("Failed to save newsletter to " + TABLE_NAME, e);
        }
    }

    @Override
    public KingdomCreationNews load(UUID id, long date, Connection conn) {
        String sql = "SELECT playerID, kingdomID FROM " + TABLE_NAME + " WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String playerID = rs.getString("playerID");
                    String kingdomID = rs.getString("kingdomID");
                    return new KingdomCreationNews(id, date, playerID, kingdomID);
                }
            }
        } catch (SQLException e) {
            throw new NewsletterDaoException("Failed to load newsletter from " + TABLE_NAME, e);
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
            throw new NewsletterDaoException("Failed to delete newsletter from " + TABLE_NAME, e);
        }
    }
}
