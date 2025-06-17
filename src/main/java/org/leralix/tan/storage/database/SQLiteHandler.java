package org.leralix.tan.storage.database;

import org.leralix.tan.TownsAndNations;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLiteHandler extends DatabaseHandler {

    private final String databasePath;

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

        SQLiteDataSource sqLiteDataSource = new SQLiteDataSource();
        sqLiteDataSource.setUrl("jdbc:sqlite:" + databasePath);
        this.dataSource = sqLiteDataSource;
        initialize();
    }
}
