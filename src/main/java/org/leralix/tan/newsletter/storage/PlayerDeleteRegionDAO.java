package org.leralix.tan.newsletter.storage;

import org.leralix.tan.newsletter.news.RegionDeletedNews;
import org.leralix.tan.newsletter.news.TownDeletedNews;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerDeleteRegionDAO extends NewsletterSubDAO<RegionDeletedNews> {


    public PlayerDeleteRegionDAO(Connection connection) {
        super(connection);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS player_delete_region_newsletter (" +
                "id UUID PRIMARY KEY, " +
                "playerID VARCHAR(36) NOT NULL, " +
                "oldRegionName VARCHAR(36) NOT NULL" +
                ")";

        try (var ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create player application newsletter table", e);
        }
    }

    @Override
    public void save(RegionDeletedNews newsletter) {
        String sql = "INSERT INTO player_delete_region_newsletter (id, playerID, oldRegionName) VALUES (?, ?, ?)";

        try (var ps = connection.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getPlayerID());
            ps.setString(3, newsletter.getRegionName());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to save player application newsletter", e);
        }
    }

    @Override
    public RegionDeletedNews load(UUID id, long date) {
        String sql = "SELECT playerID, oldRegionName FROM player_delete_region_newsletter WHERE id = ?";
        try (var ps = connection.prepareStatement(sql)) {
            ps.setObject(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) {
                String playerID = rs.getString("playerID");
                String oldRegionName = rs.getString("oldRegionName");
                return new RegionDeletedNews(id, date, playerID, oldRegionName);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load player application newsletter", e);
        }
        return null;
    }
}
