package org.leralix.tan.newsletter.storage;

import org.leralix.tan.newsletter.news.TownCreatedNews;
import org.leralix.tan.newsletter.news.TownDeletedNews;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerDeleteTownDAO extends NewsletterSubDAO<TownDeletedNews> {

    public PlayerDeleteTownDAO(DataSource connection) {
        super(connection);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS player_delete_town_newsletter (" +
                "id UUID PRIMARY KEY, " +
                "playerID VARCHAR(36) NOT NULL, " +
                "oldTownName VARCHAR(36) NOT NULL" +
                ")";

        try (var ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create player application newsletter table", e);
        }
    }

    @Override
    public void save(TownDeletedNews newsletter) {
        String sql = "INSERT INTO player_delete_town_newsletter (id, playerID, oldTownName) VALUES (?, ?, ?)";

        try (var ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getPlayerID());
            ps.setString(3, newsletter.getOldTownName());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to save player application newsletter", e);
        }
    }

    @Override
    public TownDeletedNews load(UUID id, long date) {
        String sql = "SELECT playerID, oldTownName FROM player_delete_town_newsletter WHERE id = ?";
        try (var ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.setObject(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) {
                String playerID = rs.getString("playerID");
                String oldTownName = rs.getString("oldTownName");
                return new TownDeletedNews(id, date, playerID, oldTownName);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load player application newsletter", e);
        }
        return null;
    }
}
