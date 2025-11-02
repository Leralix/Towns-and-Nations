package org.leralix.tan.storage.database;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.SQLException;

public class MySqlHandler extends DatabaseHandler {

    private final String host;
    private final int port;
    private final String databaseName;
    private final String user;
    private final String password;

    public MySqlHandler(String host, int port, String database, String username, String password) {
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
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Build JDBC URL manually to force connection to the correct database
        String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                host, port, databaseName);

        MysqlDataSource ds = new MysqlDataSource();
        ds.setURL(jdbcUrl);
        ds.setUser(user);
        ds.setPassword(password);

        this.dataSource = ds;
        initialize();
    }

}
