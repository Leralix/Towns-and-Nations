package org.leralix.tan.storage.database;

import org.leralix.tan.dataclass.newhistory.TransactionHistory;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.TerritoryData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public abstract class DatabaseHandler {

    protected final String databasePath;
    protected Connection connection;

    protected DatabaseHandler(String databasePath) {
        this.databasePath = databasePath;
        this.connection = null;
    }

    public abstract void addTransactionHistory(TransactionHistory transactionHistory);

    public abstract List<List<TransactionHistory>> getTransactionHistory(TerritoryData territoryData, TransactionHistoryEnum type);
    public abstract void deleteOldHistory(int nbDays, TransactionHistoryEnum type);

    public abstract void connect() throws SQLException;
    public abstract void disconnect() throws SQLException;
    public abstract void initialize() throws SQLException;

    public Connection getConnection() {
        return connection;
    }

}
