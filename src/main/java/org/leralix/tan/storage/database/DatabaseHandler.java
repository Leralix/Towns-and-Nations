package org.leralix.tan.storage.database;

import org.bukkit.Bukkit;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.newhistory.TransactionHistory;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.TerritoryData;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DatabaseHandler {

    protected DataSource dataSource;


    public abstract void connect() throws SQLException;


    public void addTransactionHistory(TransactionHistory transactionHistory) {
        Bukkit.getScheduler().runTaskAsynchronously(TownsAndNations.getPlugin(), () -> {
            checkIfHistoryDbExists();
            String insertSQL = """
                INSERT INTO territoryTransactionHistory (date, type, territoryDataID, transactionParty, amount)
                VALUES (?, ?, ?, ?, ?)
            """;

            try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(insertSQL)) {
                preparedStatement.setString(1, transactionHistory.getDate());
                preparedStatement.setString(2, transactionHistory.getType().toString());
                preparedStatement.setString(3, transactionHistory.getTerritoryDataID());
                preparedStatement.setString(4, transactionHistory.getTransactionParty());
                preparedStatement.setDouble(5, transactionHistory.getAmount());

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                TownsAndNations.getPlugin().getLogger().severe("Error while adding transaction history");
            }
        });
    }

    public List<List<TransactionHistory>> getTransactionHistory(TerritoryData territoryData, TransactionHistoryEnum type) {
        String selectSQL = """
        SELECT date, type, territoryDataID, transactionParty, amount
        FROM territoryTransactionHistory
        WHERE territoryDataID = ? AND type = ?
        ORDER BY date
    """;

        // Map pour regrouper les transactions par date
        Map<String, List<TransactionHistory>> groupedByDate = new HashMap<>();

        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(selectSQL)) {
            preparedStatement.setString(1, territoryData.getID());
            preparedStatement.setString(2, type.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String date = resultSet.getString("date");
                    TransactionHistoryEnum transactionHistoryEnum = TransactionHistoryEnum.valueOf(resultSet.getString("type"));
                    String territoryDataID = resultSet.getString("territoryDataID");
                    String transactionParty = resultSet.getString("transactionParty");
                    double amount = resultSet.getDouble("amount");

                    TransactionHistory transactionHistory = transactionHistoryEnum.createTransactionHistory(
                            date, territoryDataID, transactionParty, amount
                    );

                    groupedByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(transactionHistory);
                }
            }
        } catch (SQLException e) {
            TownsAndNations.getPlugin().getLogger().severe("Error while getting transaction history");
        }
        return new ArrayList<>(groupedByDate.values());
    }

    public void deleteOldHistory(int nbDays, TransactionHistoryEnum type) {
        String deleteSQL = """
        DELETE FROM territoryTransactionHistory
        WHERE date < DATE('now', '-' || ? || ' days')
        AND type != ?
    """;

        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(deleteSQL)) {
            preparedStatement.setInt(1, nbDays);
            preparedStatement.setString(1, type.toString());
        } catch (SQLException e) {
            TownsAndNations.getPlugin().getLogger().severe("Error while deleting old history");
        }
    }

    protected void checkIfHistoryDbExists() {
        try (Statement statement = dataSource.getConnection().createStatement()) {
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
            TownsAndNations.getPlugin().getLogger().severe("Error while creating history table");
        }
    }

    public void initialize() {
        checkIfHistoryDbExists();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

}
