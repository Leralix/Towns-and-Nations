package org.leralix.tan.newsletter.storage;

import org.leralix.tan.newsletter.news.RegionCreationNews;
import org.leralix.tan.newsletter.news.TownCreatedNews;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerCreateRegionDAO extends NewsletterSubDAO<RegionCreationNews> {

    public PlayerCreateRegionDAO(Connection connection) {
        super(connection);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS player_create_region_newsletter (" +
                "id UUID PRIMARY KEY, " +
                "playerID VARCHAR(36) NOT NULL, " +
                "regionID VARCHAR(36) NOT NULL" +
                ")";

        try (var ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create player application newsletter table", e);
        }
    }

    @Override
    public void save(RegionCreationNews newsletter) {
        String sql = "INSERT INTO player_create_region_newsletter (id, playerID, regionID) VALUES (?, ?, ?)";

        try (var ps = connection.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getPlayerID());
            ps.setString(3, newsletter.getRegionID());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to save player application newsletter", e);
        }
    }

    @Override
    public RegionCreationNews load(UUID id, long date) {
        String sql = "SELECT playerID, regionID FROM player_create_region_newsletter WHERE id = ?";
        try (var ps = connection.prepareStatement(sql)) {
            ps.setObject(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) {
                String playerID = rs.getString("playerID");
                String regionID = rs.getString("regionID");
                return new RegionCreationNews(id, date, playerID, regionID);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load player application newsletter", e);
        }
        return null;
    }
}
