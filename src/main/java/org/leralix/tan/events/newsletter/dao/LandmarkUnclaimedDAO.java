package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.LandmarkUnclaimedNewsletter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LandmarkUnclaimedDAO extends NewsletterSubDAO<LandmarkUnclaimedNewsletter> {

    private static final String TABLE_NAME = "landmark_unclaimed_newsletter";

    public LandmarkUnclaimedDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "landmarkID VARCHAR(36) NOT NULL, " +
                "oldOwnerID VARCHAR(36) NOT NULL " +
                ")";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create diplomacy proposal newsletter table", e);
        }
    }

    @Override
    public void save(LandmarkUnclaimedNewsletter newsletter) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, landmarkID, oldOwnerID) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getLandmarkID());
            ps.setString(3, newsletter.getOldOwnerID());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save diplomacy proposal newsletter", e);
        }
    }

    @Override
    public LandmarkUnclaimedNewsletter load(UUID id, long date) throws SQLException {
        String sql = "SELECT landmarkID, oldOwnerID FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String landmarkID = rs.getString("landmarkID");
                    String oldOwnerID = rs.getString("oldOwnerID");
                    return new LandmarkUnclaimedNewsletter(id, date, landmarkID, oldOwnerID);
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
