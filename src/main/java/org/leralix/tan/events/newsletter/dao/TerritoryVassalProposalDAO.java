package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.TerritoryVassalProposalNews;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class TerritoryVassalProposalDAO extends NewsletterSubDAO<TerritoryVassalProposalNews> {

    private static final String TABLE_NAME = "territory_vassal_proposal_newsletter";

    public TerritoryVassalProposalDAO(DataSource connection) {
        super(connection);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "proposingTerritoryID VARCHAR(36) NOT NULL, " +
                "receivingTerritoryID VARCHAR(36) NOT NULL" +
                ")";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create table " + TABLE_NAME, e);
        }
    }

    @Override
    public void save(TerritoryVassalProposalNews newsletter) {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, proposingTerritoryID, receivingTerritoryID) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getProposingTerritoryID());
            ps.setString(3, newsletter.getReceivingTerritoryID());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save newsletter to " + TABLE_NAME, e);
        }
    }

    @Override
    public TerritoryVassalProposalNews load(UUID id, long date) {
        String sql = "SELECT proposingTerritoryID, receivingTerritoryID FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String proposingTerritoryID = rs.getString("proposingTerritoryID");
                    String receivingTerritoryID = rs.getString("receivingTerritoryID");
                    return new TerritoryVassalProposalNews(id, date, proposingTerritoryID, receivingTerritoryID);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load newsletter from " + TABLE_NAME, e);
        }
        return null;
    }

    @Override
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
