package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.events.newsletter.news.DiplomacyProposalNews;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DiplomacyProposalDAO extends NewsletterSubDAO<DiplomacyProposalNews> {

    public DiplomacyProposalDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS diplomacy_proposal_newsletter (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "proposingTerritoryID VARCHAR(36) NOT NULL, " +
                "receivingTerritoryID VARCHAR(36) NOT NULL, " +
                "wantedRelation VARCHAR(36) NOT NULL" +
                ")";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create diplomacy proposal newsletter table", e);
        }
    }

    @Override
    public void save(DiplomacyProposalNews newsletter) {
        String sql = "INSERT INTO diplomacy_proposal_newsletter (id, proposingTerritoryID, receivingTerritoryID, wantedRelation) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getProposingTerritoryID());
            ps.setString(3, newsletter.getReceivingTerritoryID());
            ps.setString(4, newsletter.getWantedRelation().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save diplomacy proposal newsletter", e);
        }
    }

    @Override
    public DiplomacyProposalNews load(UUID id, long date) {
        String sql = "SELECT proposingTerritoryID, receivingTerritoryID, wantedRelation FROM diplomacy_proposal_newsletter WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String proposingTerritoryID = rs.getString("proposingTerritoryID");
                    String receivingTerritoryID = rs.getString("receivingTerritoryID");
                    TownRelation wantedRelation = TownRelation.valueOf(rs.getString("wantedRelation"));
                    return new DiplomacyProposalNews(id, date, proposingTerritoryID, receivingTerritoryID, wantedRelation);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load diplomacy proposal newsletter", e);
        }
        return null;
    }
}
