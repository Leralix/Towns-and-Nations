package org.leralix.tan.newsletter.storage;

import org.leralix.tan.newsletter.news.PlayerJoinRequestNews;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerApplicationDAO implements NewsletterSubDAO<PlayerJoinRequestNews> {

    Connection connection;

    public PlayerApplicationDAO(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS player_application_newsletter (" +
                "id UUID PRIMARY KEY, " +
                "playerID VARCHAR(36) NOT NULL, " +
                "townID VARCHAR(36) NOT NULL" +
                ")";

        try (var ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create player application newsletter table", e);
        }
    }

    @Override
    public void save(PlayerJoinRequestNews newsletter) {
        String sql = "INSERT INTO player_application_newsletter (id, playerID, townID) VALUES (?, ?, ?)";

        try (var ps = connection.prepareStatement(sql)) {
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
        try (var ps = connection.prepareStatement(sql)) {
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
