package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.TerritoryVassalForcedNews;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.UUID;

public class TerritoryVassalForcedDAO extends NewsletterSubDAO<TerritoryVassalForcedNews> {


    public TerritoryVassalForcedDAO(DataSource connection) {
        super(connection);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS territory_vassal_forced_newsletter (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "proposingTerritoryID VARCHAR(36) NOT NULL, " +
                "forcedTerritoryID VARCHAR(36) NOT NULL" +
                ")";

        try (var ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create player diplomacy accepted newsletter table", e);
        }
    }

    @Override
    public void save(TerritoryVassalForcedNews newsletter) {
        String sql = "INSERT INTO territory_vassal_forced_newsletter (id, proposingTerritoryID, forcedTerritoryID) VALUES (?, ?, ?)";

        try (var ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getProposingTerritoryID());
            ps.setString(3, newsletter.getForcedTerritoryID());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to save player application newsletter", e);
        }
    }

    @Override
    public TerritoryVassalForcedNews load(UUID id, long date) {
        String sql = "SELECT proposingTerritoryID, forcedTerritoryID FROM territory_vassal_forced_newsletter WHERE id = ?";
        try (var ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.setObject(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) {
                String proposingTerritoryID = rs.getString("proposingTerritoryID");
                String forcedTerritoryID = rs.getString("forcedTerritoryID");
                return new TerritoryVassalForcedNews(id, date, proposingTerritoryID, forcedTerritoryID);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load player application newsletter", e);
        }
        return null;
    }
}
