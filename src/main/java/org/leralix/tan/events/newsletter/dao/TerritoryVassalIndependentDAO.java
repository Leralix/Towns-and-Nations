package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.TerritoryIndependentNews;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class TerritoryVassalIndependentDAO extends NewsletterSubDAO<TerritoryIndependentNews> {

    private static final String TABLE_NAME = "territory_vassal_independent_newsletter";

    public TerritoryVassalIndependentDAO(DataSource connection) {
        super(connection);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "independentTerritoryID VARCHAR(36) NOT NULL, " +
                "formerMasterID VARCHAR(36) NOT NULL" +
                ")";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create table " + TABLE_NAME, e);
        }
    }

    @Override
    public void save(TerritoryIndependentNews newsletter) {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, independentTerritoryID, formerMasterID) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getIndependentTerritoryID());
            ps.setString(3, newsletter.getFormerMasterID());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save to table " + TABLE_NAME, e);
        }
    }

    @Override
    public TerritoryIndependentNews load(UUID id, long date) {
        String sql = "SELECT independentTerritoryID, formerMasterID FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String independentTerritoryID = rs.getString("independentTerritoryID");
                    String formerMasterID = rs.getString("formerMasterID");
                    return new TerritoryIndependentNews(id, date, independentTerritoryID, formerMasterID);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load from table " + TABLE_NAME, e);
        }
        return null;
    }

    public void delete(UUID id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete from table " + TABLE_NAME, e);
        }
    }
}
