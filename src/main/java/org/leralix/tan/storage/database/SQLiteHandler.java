package org.leralix.tan.storage.database;

import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.newhistory.SalaryTransactionHistory;
import org.leralix.tan.dataclass.newhistory.TransactionHistory;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.ITerritoryData;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteHandler extends DatabaseHandler {

    public SQLiteHandler(String databasePath) {
        super(databasePath);
    }


    @Override
    public void connect() throws SQLException {
        File dbFile = new File(databasePath);

        if (!dbFile.exists()) {
            try {
                if (dbFile.getParentFile() != null && !dbFile.getParentFile().exists()) {
                    dbFile.getParentFile().mkdirs();
                }
                dbFile.createNewFile();
                TownsAndNations.getPlugin().getLogger().info("SQLite database created");
            } catch (IOException e) {
                throw new SQLException("Error while creating SQLite database", e);
            }
        }
        connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
        initialize();
    }

    @Override
    public void disconnect() throws SQLException {
        if(connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Override
    public void initialize() throws SQLException {
        checkIfHistoryDbExists();
    }

    @Override
    public void addTransactionHistory(TransactionHistory transactionHistory) {
        checkIfHistoryDbExists();
        String insertSQL = """
        INSERT INTO territoryTransactionHistory (date, type, territoryDataID, transactionParty, amount)
        VALUES (?, ?, ?, ?, ?)
    """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, transactionHistory.getDate());
            preparedStatement.setString(2, transactionHistory.getType().toString());
            preparedStatement.setString(3, transactionHistory.getTransactionParty());
            preparedStatement.setString(4, transactionHistory.getTerritoryDataID());
            preparedStatement.setDouble(5, transactionHistory.getAmount());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<TransactionHistory> getTransactionHistory(ITerritoryData territoryData, TransactionHistoryEnum type) {
        String selectSQL = """
        SELECT date, type, transactionParty, territoryDataID, amount
        FROM territoryTransactionHistory
        WHERE territoryDataID = ? AND type = ?
    """;
        List<TransactionHistory> transactionHistories = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, territoryData.getID());
            preparedStatement.setString(2, type.toString());
            System.out.println("Getting transaction history for territoryDataID: " + territoryData.getID() + " and type: " + type.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Lire les colonnes du résultat
                    String date = resultSet.getString("date");
                    TransactionHistoryEnum transactionHistoryEnum = TransactionHistoryEnum.valueOf(resultSet.getString("type"));
                    String transactionParty = resultSet.getString("transactionParty");
                    String territoryDataID = resultSet.getString("territoryDataID");
                    double amount = resultSet.getDouble("amount");

                    // Créer un objet TransactionHistory
                    if(transactionHistoryEnum == TransactionHistoryEnum.SALARY){
                        transactionHistories.add(new SalaryTransactionHistory(date, transactionParty, territoryDataID, amount));
                        System.out.println("Added salary transaction history");
                        continue;
                    }
                    System.out.println("Unknown transaction history type: " + transactionHistoryEnum);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactionHistories; // Retourner la liste
    }


    private void checkIfHistoryDbExists() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS territoryTransactionHistory (
                date TEXT,
                type TEXT,
                territoryDataID TEXT,
                transactionParty TEXT,
                amount DOUBLE
            )
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
