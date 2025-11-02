package org.leralix.tan.storage.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.leralix.tan.TownsAndNations;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLiteHandler extends DatabaseHandler {

    private final String databasePath;
    private HikariDataSource hikariDataSource;

    public SQLiteHandler(String databasePath) {
        this.databasePath = databasePath;
    }

    @Override
    public void connect() throws SQLException {
        File dbFile = new File(databasePath);

        if (!dbFile.exists()) {
            try {
                if (dbFile.getParentFile() != null && !dbFile.getParentFile().exists()) {
                    dbFile.getParentFile().mkdirs();
                }
                if (dbFile.createNewFile()) {
                    TownsAndNations.getPlugin().getLogger().info("SQLite database created");
                }
            } catch (IOException e) {
                throw new SQLException("Error while creating SQLite database", e);
            }
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + databasePath);
        config.setPoolName("TownsAndNations-SQLite-Pool");
        this.hikariDataSource = new HikariDataSource(config);

        this.dataSource = hikariDataSource;
        createMetadataTable();
        initialize();
    }

    @Override
    public void createMetadataTable() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS tan_metadata (
                meta_key TEXT PRIMARY KEY,
                meta_value TEXT NOT NULL
            )
        """;

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            TownsAndNations.getPlugin().getLogger().severe(
                "Error creating table tan_metadata: " + e.getMessage()
            );
        }
    }

    @Override
    public int getNextTownId() {
        String selectSQL = "SELECT meta_value FROM tan_metadata WHERE meta_key = 'next_town_id'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSQL)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Integer.parseInt(rs.getString("meta_value"));
                }
            }
        } catch (SQLException | NumberFormatException e) {
            // Ignore, we'll insert the default value
        }
        return 1;
    }

    @Override
    public void updateNextTownId(int newId) {
        String upsertSQL = "INSERT OR REPLACE INTO tan_metadata (meta_key, meta_value) VALUES ('next_town_id', ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(upsertSQL)) {
            ps.setString(1, String.valueOf(newId));
            ps.executeUpdate();
        } catch (SQLException e) {
            TownsAndNations.getPlugin().getLogger().severe(
                "Error updating next_town_id: " + e.getMessage()
            );
        }
    }

    @Override
    public int getNextRegionId() {
        String selectSQL = "SELECT meta_value FROM tan_metadata WHERE meta_key = 'next_region_id'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSQL)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Integer.parseInt(rs.getString("meta_value"));
                }
            }
        } catch (SQLException | NumberFormatException e) {
            // Ignore, we'll insert the default value
        }
        return 1;
    }

    @Override
    public void updateNextRegionId(int newId) {
        String upsertSQL = "INSERT OR REPLACE INTO tan_metadata (meta_key, meta_value) VALUES ('next_region_id', ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(upsertSQL)) {
            ps.setString(1, String.valueOf(newId));
            ps.executeUpdate();
        } catch (SQLException e) {
            TownsAndNations.getPlugin().getLogger().severe(
                "Error updating next_region_id: " + e.getMessage()
            );
        }
    }
}
