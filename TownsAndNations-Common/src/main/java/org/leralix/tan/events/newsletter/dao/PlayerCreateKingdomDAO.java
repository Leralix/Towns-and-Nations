package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.KingdomCreationNews;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerCreateKingdomDAO extends NewsletterSubDAO<KingdomCreationNews> {

    private static final String TABLE_NAME = "player_create_kingdom_newsletter";

    public PlayerCreateKingdomDAO(DataSource connection) {
        super(connection);
    }

    @Override
    protected void createTableIfNotExists() {
        NewsletterDaoSqlUtil.createTableIfNotExists(
                dataSource,
                TABLE_NAME,
                "playerID VARCHAR(36) NOT NULL, kingdomID VARCHAR(36) NOT NULL"
        );
    }

    @Override
    public void save(KingdomCreationNews newsletter, Connection conn) {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, playerID, kingdomID) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getPlayerID());
            ps.setString(3, newsletter.getKingdomID());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new NewsletterDaoException("Failed to save newsletter to " + TABLE_NAME, e);
        }
    }

    @Override
    public KingdomCreationNews load(UUID id, long date, Connection conn) {
        String sql = "SELECT playerID, kingdomID FROM " + TABLE_NAME + " WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String playerID = rs.getString("playerID");
                    String kingdomID = rs.getString("kingdomID");
                    return new KingdomCreationNews(id, date, playerID, kingdomID);
                }
            }
        } catch (SQLException e) {
            throw new NewsletterDaoException("Failed to load newsletter from " + TABLE_NAME, e);
        }
        return null;
    }

    @Override
    public void delete(UUID id) {
        NewsletterDaoSqlUtil.deleteById(dataSource, TABLE_NAME, id);
    }
}
