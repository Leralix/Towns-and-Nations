package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.TerritoryIndependentNews;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.UUID;

public class TerritoryVassalIndependentDAO extends NewsletterSubDAO<TerritoryIndependentNews> {


    public TerritoryVassalIndependentDAO(DataSource connection) {
        super(connection);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS territory_vassal_independent_newsletter (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "independentTerritoryID VARCHAR(36) NOT NULL, " +
                "formerMasterID VARCHAR(36) NOT NULL" +
                ")";

        try (var ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create player diplomacy accepted newsletter table", e);
        }
    }

    @Override
    public void save(TerritoryIndependentNews newsletter) {
        String sql = "INSERT INTO territory_vassal_independent_newsletter (id, independentTerritoryID, formerMasterID) VALUES (?, ?, ?)";

        try (var ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getFormerMasterID());
            ps.setString(3, newsletter.getIndependentTerritoryID());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to save player application newsletter", e);
        }
    }

    @Override
    public TerritoryIndependentNews load(UUID id, long date) {
        String sql = "SELECT independentTerritoryID, formerMasterID FROM territory_vassal_independent_newsletter WHERE id = ?";
        try (var ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.setObject(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) {
                String independentTerritoryID = rs.getString("independentTerritoryID");
                String formerMasterID = rs.getString("formerMasterID");
                return new TerritoryIndependentNews(id, date, independentTerritoryID, formerMasterID);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load player application newsletter", e);
        }
        return null;
    }
}
