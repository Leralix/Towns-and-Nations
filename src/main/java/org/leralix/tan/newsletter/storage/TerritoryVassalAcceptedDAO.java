package org.leralix.tan.newsletter.storage;

import org.leralix.tan.newsletter.news.TerritoryVassalAcceptedNews;
import org.leralix.tan.newsletter.news.TerritoryVassalProposalNews;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class TerritoryVassalAcceptedDAO extends NewsletterSubDAO<TerritoryVassalAcceptedNews> {


    public TerritoryVassalAcceptedDAO(Connection connection) {
        super(connection);
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS territory_vassal_accepted_newsletter (" +
                "id UUID PRIMARY KEY, " +
                "proposingTerritoryID VARCHAR(36) NOT NULL, " +
                "receivingTerritoryID VARCHAR(36) NOT NULL" +
                ")";

        try (var ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create player diplomacy accepted newsletter table", e);
        }
    }

    @Override
    public void save(TerritoryVassalAcceptedNews newsletter) {
        String sql = "INSERT INTO territory_vassal_accepted_newsletter (id, proposingTerritoryID, receivingTerritoryID) VALUES (?, ?, ?)";

        try (var ps = connection.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getProposingTerritoryID());
            ps.setString(3, newsletter.getReceivingTerritoryID());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to save player application newsletter", e);
        }
    }

    @Override
    public TerritoryVassalAcceptedNews load(UUID id, long date) {
        String sql = "SELECT proposingTerritoryID, receivingTerritoryID FROM territory_vassal_accepted_newsletter WHERE id = ?";
        try (var ps = connection.prepareStatement(sql)) {
            ps.setObject(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) {
                String proposingTerritoryID = rs.getString("proposingTerritoryID");
                String receivingTerritoryID = rs.getString("receivingTerritoryID");
                return new TerritoryVassalAcceptedNews(id, date, proposingTerritoryID, receivingTerritoryID);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load player application newsletter", e);
        }
        return null;
    }
}
