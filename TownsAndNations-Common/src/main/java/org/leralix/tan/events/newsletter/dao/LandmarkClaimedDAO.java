package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.LandmarkClaimedNewsletter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LandmarkClaimedDAO extends NewsletterSubDAO<LandmarkClaimedNewsletter> {

    private static final String TABLE_NAME = "landmark_claimed_newsletter";

    public LandmarkClaimedDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "landmarkID VARCHAR(36) NOT NULL, " +
                "newOwnerID VARCHAR(36) NOT NULL " +
                ")";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create " + TABLE_NAME, e);
        }
    }

    @Override
    public void save(LandmarkClaimedNewsletter newsletter, Connection conn) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, landmarkID, newOwnerID) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getLandmarkID());
            ps.setString(3, newsletter.getNewOwnerID());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save diplomacy proposal newsletter", e);
        }
    }

    @Override
    public LandmarkClaimedNewsletter load(UUID id, long date, Connection conn) throws SQLException {
        String sql = "SELECT landmarkID, newOwnerID FROM " + TABLE_NAME + " WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String landmarkID = rs.getString("landmarkID");
                    String newOwnerID = rs.getString("newOwnerID");
                    return new LandmarkClaimedNewsletter(id, date, landmarkID, newOwnerID);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load diplomacy proposal newsletter", e);
        }
        return null;
    }

    @Override
    public void delete(UUID uuid) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete from table " + TABLE_NAME, e);
        }
    }
}
