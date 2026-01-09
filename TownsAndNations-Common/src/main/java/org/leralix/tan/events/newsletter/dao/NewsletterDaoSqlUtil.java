package org.leralix.tan.events.newsletter.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public final class NewsletterDaoSqlUtil {

    private static final String SAFE_IDENTIFIER_PATTERN = "^[A-Za-z0-9_]+$";
    private static final String SAFE_COLUMNS_PATTERN = "^[A-Za-z0-9_(),\\s]+$";

    private NewsletterDaoSqlUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static String requireSafeIdentifier(String identifier, String label) {
        if (identifier == null || identifier.isBlank() || !identifier.matches(SAFE_IDENTIFIER_PATTERN)) {
            throw new IllegalArgumentException("Unsafe " + label);
        }
        return identifier;
    }

    private static String requireSafeColumnsSql(String columnsSql) {
        if (columnsSql == null || columnsSql.isBlank()) {
            throw new IllegalArgumentException("Unsafe columnsSql");
        }
        if (!columnsSql.matches(SAFE_COLUMNS_PATTERN)) {
            throw new IllegalArgumentException("Unsafe columnsSql");
        }
        String lower = columnsSql.toLowerCase();
        if (lower.contains(";") || lower.contains("--") || lower.contains("/*") || lower.contains("*/") || lower.contains("\\\"") || lower.contains("'")) {
            throw new IllegalArgumentException("Unsafe columnsSql");
        }
        return columnsSql;
    }

    @SuppressWarnings("java:S2077")
    public static void createTableIfNotExists(DataSource dataSource, String tableName, String columnsSql) {
        tableName = requireSafeIdentifier(tableName, "tableName");
        columnsSql = requireSafeColumnsSql(columnsSql);
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

    @SuppressWarnings("java:S2077")
    public static void deleteById(DataSource dataSource, String tableName, UUID id) {
        tableName = requireSafeIdentifier(tableName, "tableName");
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
