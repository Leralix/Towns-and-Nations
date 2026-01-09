package org.leralix.tan.events.newsletter.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public final class NewsletterDaoSqlUtil {

    private NewsletterDaoSqlUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void createTableIfNotExists(DataSource dataSource, String tableName, String columnsSql) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                columnsSql + ")";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new NewsletterDaoException("Failed to create " + tableName + " table", e);
        }
    }

    public static void deleteById(DataSource dataSource, String tableName, UUID id) {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new NewsletterDaoException("Failed to delete newsletter from " + tableName, e);
        }
    }
}
