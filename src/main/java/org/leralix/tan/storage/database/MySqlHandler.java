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

        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName(host);
        ds.setPort(port);
        ds.setDatabaseName(databaseName);
        ds.setUser(user);
        ds.setPassword(password);
        ds.setUseSSL(true);

        this.dataSource = ds;
        initialize();
    }

}
