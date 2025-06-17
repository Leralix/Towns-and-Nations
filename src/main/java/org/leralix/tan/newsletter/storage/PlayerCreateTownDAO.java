package org.leralix.tan.newsletter.storage;

import org.leralix.tan.newsletter.news.TownCreatedNews;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerCreateTownDAO extends NewsletterSubDAO<TownCreatedNews> {

    public PlayerCreateTownDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS player_create_town_newsletter (" +
                "id UUID PRIMARY KEY, " +
                "playerID VARCHAR(36) NOT NULL, " +
                "townID VARCHAR(36) NOT NULL" +
                ")";

        try (Connection connection = dataSource.getConnection()) {
            try (var ps = dataSource.getConnection().prepareStatement(sql)) {
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to create player application newsletter table", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }


    }

    @Override
    public void save(TownCreatedNews newsletter) {
        String sql = "INSERT INTO player_create_town_newsletter (id, playerID, townID) VALUES (?, ?, ?)";

        try (Connection connection = dataSource.getConnection()) {
            try (var ps = dataSource.getConnection().prepareStatement(sql)) {
                ps.setObject(1, newsletter.getId());
                ps.setString(2, newsletter.getPlayerID());
                ps.setString(3, newsletter.getTownID());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to save player application newsletter", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }


    }

    @Override
    public TownCreatedNews load(UUID id, long date) {
        String sql = "SELECT playerID, townID FROM player_create_town_newsletter WHERE id = ?";
        try (Connection connection = dataSource.getConnection()) {
            try (var ps = dataSource.getConnection().prepareStatement(sql)) {
                ps.setObject(1, id);
                var rs = ps.executeQuery();
                if (rs.next()) {
                    String playerID = rs.getString("playerID");
                    String townID = rs.getString("townID");
                    return new TownCreatedNews(id, date, playerID, townID);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to load player application newsletter", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }

        return null;
    }
}
