package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.PlayerJoinRequestNews;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerApplicationDAO extends NewsletterSubDAO<PlayerJoinRequestNews> {

    public PlayerApplicationDAO(DataSource connection) {
        super(connection);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS player_application_newsletter (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "playerID VARCHAR(36) NOT NULL, " +
                "townID VARCHAR(36) NOT NULL" +
                ")";

        try (var ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create player application newsletter table", e);
        }
    }

    @Override
    public void save(PlayerJoinRequestNews newsletter) {
        String sql = "INSERT INTO player_application_newsletter (id, playerID, townID) VALUES (?, ?, ?)";

        try (var ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getPlayerID());
            ps.setString(3, newsletter.getTownID());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to save player application newsletter", e);
        }
    }

    @Override
    public PlayerJoinRequestNews load(UUID id, long date) {
        String sql = "SELECT playerID, townID FROM player_application_newsletter WHERE id = ?";
        try (var ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.setObject(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) {
                String playerID = rs.getString("playerID");
                String townID = rs.getString("townID");
                return new PlayerJoinRequestNews(id, date, playerID, townID);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load player application newsletter", e);
        }
        return null;
    }
}
