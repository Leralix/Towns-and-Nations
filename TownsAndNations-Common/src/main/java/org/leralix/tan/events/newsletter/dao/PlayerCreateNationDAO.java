package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.NationCreationNews;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerCreateNationDAO extends NewsletterSubDAO<NationCreationNews> {

    private static final String TABLE_NAME = "player_create_nation_newsletter";

    public PlayerCreateNationDAO(DataSource connection) {
        super(connection);
    }

    @Override
    protected void createTableIfNotExists() {
        NewsletterDaoSqlUtil.createTableIfNotExists(
                dataSource,
                TABLE_NAME,
                "playerID VARCHAR(36) NOT NULL, nationID VARCHAR(36) NOT NULL"
        );
    }

    @Override
    public void save(NationCreationNews newsletter, Connection conn) {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, playerID, nationID) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getPlayerID());
            ps.setString(3, newsletter.getNationID());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new NewsletterDaoException("Failed to save newsletter to " + TABLE_NAME, e);
        }
    }

    @Override
    public NationCreationNews load(UUID id, long date, Connection conn) {
        String sql = "SELECT playerID, nationID FROM " + TABLE_NAME + " WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String playerID = rs.getString("playerID");
                    String nationID = rs.getString("nationID");
                    return new NationCreationNews(id, date, playerID, nationID);
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
