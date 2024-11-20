package org.leralix.tan.storage.database;

import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.newhistory.*;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.lang.Lang;

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
            preparedStatement.setString(3, transactionHistory.getTerritoryDataID());
            preparedStatement.setString(4, transactionHistory.getTransactionParty());
            preparedStatement.setDouble(5, transactionHistory.getAmount());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<TransactionHistory> getTransactionHistory(ITerritoryData territoryData, TransactionHistoryEnum type) {
        String selectSQL = """
        SELECT date, type, territoryDataID, transactionParty, amount
        FROM territoryTransactionHistory
        WHERE territoryDataID = ? AND type = ?
    """;
        List<TransactionHistory> transactionHistories = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, territoryData.getID());
            preparedStatement.setString(2, type.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String date = resultSet.getString("date");
                    TransactionHistoryEnum transactionHistoryEnum = TransactionHistoryEnum.valueOf(resultSet.getString("type"));
                    String territoryDataID = resultSet.getString("territoryDataID");
                    String transactionParty = resultSet.getString("transactionParty");
                    double amount = resultSet.getDouble("amount");

                    if(transactionHistoryEnum == TransactionHistoryEnum.SALARY){
                        transactionHistories.add(new SalaryPaymentHistory(date, territoryDataID, transactionParty, amount));
                    }
                    if(transactionHistoryEnum == TransactionHistoryEnum.PLAYER_TAX){
                        transactionHistories.add(new PlayerTaxHistory(date, territoryDataID, transactionParty, amount));
                    }
                    if(transactionHistoryEnum == TransactionHistoryEnum.SUBJECT_TAX) {
                        transactionHistories.add(new SubjectTaxHistory(date, territoryDataID, transactionParty, amount));
                    }
                    if(transactionHistoryEnum == TransactionHistoryEnum.MISCELLANEOUS) {
                        transactionHistories.add(new MiscellaneousHistory(date, territoryDataID, amount));
                    }
                    if (transactionHistoryEnum == TransactionHistoryEnum.CHUNK_SPENDING) {
                        transactionHistories.add(new ChunkPaymentHistory(date, territoryDataID, amount));
                    }
                    if(transactionHistoryEnum == TransactionHistoryEnum.DONATION) {
                        transactionHistories.add(new PlayerDonationHistory(date, territoryDataID, transactionParty, amount));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactionHistories; // Retourner la liste
    }

    @Override
    public void deleteOldHistory(int nbDays, TransactionHistoryEnum type) {
        String deleteSQL = """
        DELETE FROM territoryTransactionHistory
        WHERE date < DATE('now', '-' || ? || ' days')
        AND type != ?
    """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setInt(1, nbDays);
            preparedStatement.setString(1, type.toString());
            int rowsAffected = preparedStatement.executeUpdate();
            TownsAndNations.getPlugin().getLogger().info(Lang.DATABASE_SUCCESSFULLY_DELETED_OLD_ROWS.get(rowsAffected));
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
