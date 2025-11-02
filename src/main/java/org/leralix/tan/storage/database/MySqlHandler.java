package org.leralix.tan.storage.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.leralix.tan.TownsAndNations;

public class MySqlHandler extends DatabaseHandler {

    private final String host;
    private final int port;
    private final String databaseName;
    private final String user;
    private final String password;
    private final TownsAndNations plugin;
    private HikariDataSource hikariDataSource;

    public MySqlHandler(TownsAndNations plugin, String host, int port, String database, String username, String password) {
        this.plugin = plugin;
        this.host = host;
        this.port = port;
        this.databaseName = database;
        this.user = username;
        this.password = password;
    }

    @Override
    public void connect() throws SQLException {

        if (host == null || databaseName == null) {
            return;
        }

        HikariConfig config = new HikariConfig();
        boolean sslEnabled = plugin.getConfig().getBoolean("database.ssl.enabled", false);
        boolean sslRequired = plugin.getConfig().getBoolean("database.ssl.require", false);
        boolean verifyServerCert = plugin.getConfig().getBoolean("database.ssl.verify-server-certificate", false);

        String sslParams = "useSSL=" + sslEnabled;
        if (sslEnabled) {
            sslParams += "&requireSSL=" + sslRequired;
            sslParams += "&verifyServerCertificate=" + verifyServerCert;
        }

        config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?%s&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                host, port, databaseName, sslParams));
        config.setUsername(user);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("maintainTimeStats", "false");
        config.addDataSourceProperty("alwaysSendSetIsolation", "false");
        config.addDataSourceProperty("enableQueryTimeouts", "false");
        config.setPoolName("TownsAndNations-MySql-Pool");

        // Connection pool configuration
        config.setMaximumPoolSize(plugin.getConfig().getInt("database.pool-size", 10));
        config.setMinimumIdle(plugin.getConfig().getInt("database.min-idle", 2));
        config.setConnectionTimeout(plugin.getConfig().getLong("database.connection-timeout", 30000L));
        config.setIdleTimeout(plugin.getConfig().getLong("database.idle-timeout", 600000L));
        config.setMaxLifetime(plugin.getConfig().getLong("database.max-lifetime", 1800000L));

        this.dataSource = new HikariDataSource(config);

        createMetadataTable();
        initialize();
    }

    @Override
    public void createMetadataTable() {
        String createTableSQL = """
             CREATE TABLE IF NOT EXISTS tan_metadata (
                meta_key VARCHAR(255) PRIMARY KEY,
                meta_value VARCHAR(255) NOT NULL
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
        String upsertSQL = "INSERT INTO tan_metadata (meta_key, meta_value) VALUES ('next_town_id', ?) ON DUPLICATE KEY UPDATE meta_value = VALUES(meta_value)";
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
        String upsertSQL = "INSERT INTO tan_metadata (meta_key, meta_value) VALUES ('next_region_id', ?) ON DUPLICATE KEY UPDATE meta_value = VALUES(meta_value)";
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
