package org.leralix.tan.storage.database;

import javax.sql.DataSource;
import java.sql.SQLException;

public abstract class DatabaseHandler {

    protected DataSource dataSource;

    public abstract void connect() throws SQLException;

    public DataSource getDataSource() {
        return dataSource;
    }

}
