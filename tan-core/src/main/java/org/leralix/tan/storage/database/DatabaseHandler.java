package org.leralix.tan.storage.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.sql.DataSource;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.newhistory.TransactionHistory;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.TerritoryData;

public abstract class DatabaseHandler {

  protected DataSource dataSource;

  // OPTIMIZATION: Query batch executor to reduce database load for high-player servers
  protected QueryBatchExecutor queryBatchExecutor;

  public abstract void connect() throws SQLException;

  /** Close the database connection and clean up resources Called during plugin shutdown */
  public abstract void close();

  /**
   * Check if the database connection is valid
   *
   * @return true if connection is valid, false otherwise
   */
  public boolean isConnectionValid() {
    try {
      if (dataSource == null) {
        return false;
      }
      try (Connection conn = dataSource.getConnection()) {
        return conn != null && !conn.isClosed() && conn.isValid(5);
      }
    } catch (SQLException e) {
      return false;
    }
  }

  public void addTransactionHistory(TransactionHistory transactionHistory) {
    org.leralix.tan.utils.FoliaScheduler.runTaskAsynchronously(
        TownsAndNations.getPlugin(),
        () -> {
          checkIfHistoryDbExists();
          String insertSQL =
              """
                INSERT INTO territoryTransactionHistory (date, type, territoryDataID, transactionParty, amount)
                VALUES (?, ?, ?, ?, ?)
            """;

          try (Connection conn = dataSource.getConnection();
              PreparedStatement preparedStatement = conn.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, transactionHistory.getDate());
            preparedStatement.setString(2, transactionHistory.getType().toString());
            preparedStatement.setString(3, transactionHistory.getTerritoryDataID());
            preparedStatement.setString(4, transactionHistory.getTransactionParty());
            preparedStatement.setDouble(5, transactionHistory.getAmount());

            preparedStatement.executeUpdate();
          } catch (SQLException e) {
            TownsAndNations.getPlugin()
                .getLogger()
                .severe("Error while adding transaction history : " + e.getMessage());
          }
        });
  }

  public List<List<TransactionHistory>> getTransactionHistory(
      TerritoryData territoryData, TransactionHistoryEnum type) {
    String selectSQL =
        """
        SELECT date, type, territoryDataID, transactionParty, amount
        FROM territoryTransactionHistory
        WHERE territoryDataID = ? AND type = ?
        ORDER BY date
    """;

    // Map pour regrouper les transactions par date
    Map<String, List<TransactionHistory>> groupedByDate = new HashMap<>();

    try (Connection conn = dataSource.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(selectSQL)) {
      preparedStatement.setString(1, territoryData.getID());
      preparedStatement.setString(2, type.toString());

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          String date = resultSet.getString("date");
          TransactionHistoryEnum transactionHistoryEnum =
              TransactionHistoryEnum.valueOf(resultSet.getString("type"));
          String territoryDataID = resultSet.getString("territoryDataID");
          String transactionParty = resultSet.getString("transactionParty");
          double amount = resultSet.getDouble("amount");

          TransactionHistory transactionHistory =
              transactionHistoryEnum.createTransactionHistory(
                  date, territoryDataID, transactionParty, amount);

          groupedByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(transactionHistory);
        }
      }
    } catch (SQLException e) {
      TownsAndNations.getPlugin().getLogger().severe("Error while getting transaction history");
    }
    return new ArrayList<>(groupedByDate.values());
  }

  /**
   * Get transaction history asynchronously P3.6: Async transaction history for non-blocking GUI
   * loading
   *
   * @param territoryData The territory data
   * @param type The transaction type
   * @return CompletableFuture that completes with the transaction history list
   */
  public CompletableFuture<List<List<TransactionHistory>>> getTransactionHistoryAsync(
      TerritoryData territoryData, TransactionHistoryEnum type) {
    return CompletableFuture.supplyAsync(() -> getTransactionHistory(territoryData, type));
  }

  public void deleteOldHistory(int nbDays, TransactionHistoryEnum type) {
    String deleteSQL;

    if (isMySQL()) {
      deleteSQL =
          """
            DELETE FROM territoryTransactionHistory
            WHERE date < DATE_SUB(NOW(), INTERVAL ? DAY)
            AND type = ?
        """;
    } else {
      deleteSQL =
          """
            DELETE FROM territoryTransactionHistory
            WHERE date < DATE('now', '-' || ? || ' days')
            AND type = ?
        """;
    }

    try (Connection conn = dataSource.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(deleteSQL)) {
      preparedStatement.setInt(1, nbDays);
      preparedStatement.setString(2, type.toString());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe("Error while deleting old history : " + e.getMessage());
    }
  }

  protected void checkIfHistoryDbExists() {
    try (Connection conn = dataSource.getConnection();
        Statement statement = conn.createStatement()) {
      statement.execute(
          """
                CREATE TABLE IF NOT EXISTS territoryTransactionHistory (
                date TEXT,
                type TEXT,
                territoryDataID TEXT,
                transactionParty TEXT,
                amount DOUBLE
            )
            """);
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe("Error while creating history table : " + e.getMessage());
    }
  }

  public void initialize() {
    checkIfHistoryDbExists();
  }

  public abstract void createMetadataTable();

  public abstract int getNextTownId();

  public abstract void updateNextTownId(int newId);

  public abstract int getNextRegionId();

  public abstract void updateNextRegionId(int newId);

  public DataSource getDataSource() {
    return dataSource;
  }

  /**
   * Check if the database is MySQL
   *
   * @return true if MySQL, false if SQLite
   */
  public boolean isMySQL() {
    return this instanceof MySqlHandler;
  }

  /**
   * Validate table name to prevent SQL injection
   *
   * @param tableName The table name to validate
   * @return true if valid, false otherwise
   */
  private boolean isValidTableName(String tableName) {
    // Table names should only contain alphanumeric characters and underscores
    return tableName != null && tableName.matches("^[a-zA-Z0-9_]+$");
  }

  /**
   * Get the appropriate UPSERT SQL statement based on database type
   *
   * @param tableName The table name
   * @return The UPSERT SQL statement
   * @throws IllegalArgumentException if table name is invalid
   */
  public String getUpsertSQL(String tableName) {
    if (!isValidTableName(tableName)) {
      throw new IllegalArgumentException("Invalid table name: " + tableName);
    }
    if (isMySQL()) {
      return "INSERT INTO "
          + tableName
          + " (id, data) VALUES (?, ?) ON DUPLICATE KEY UPDATE data = VALUES(data)";
    } else {
      return "INSERT OR REPLACE INTO " + tableName + " (id, data) VALUES (?, ?)";
    }
  }

  /**
   * Initialize the query batch executor. Call this during database connection setup.
   *
   * @param batchSize Number of queries to batch together (default: 50)
   * @param delayMs Maximum delay before flushing a batch in milliseconds (default: 100)
   */
  public void initializeQueryBatcher(int batchSize, int delayMs) {
    this.queryBatchExecutor = new QueryBatchExecutor(batchSize, delayMs);
    TownsAndNations.getPlugin()
        .getLogger()
        .info(
            "[TaN] Query batch executor initialized: batch="
                + batchSize
                + ", delay="
                + delayMs
                + "ms");
  }

  /**
   * Get the query batch executor (if initialized).
   *
   * @return QueryBatchExecutor or null if not initialized
   */
  public QueryBatchExecutor getQueryBatchExecutor() {
    return queryBatchExecutor;
  }

  /**
   * Shutdown the query batch executor. Call this during plugin shutdown to cleanly shut down the
   * batch executor thread.
   */
  public void shutdownQueryBatcher() {
    if (queryBatchExecutor != null) {
      queryBatchExecutor.shutdown();
      queryBatchExecutor = null;
      TownsAndNations.getPlugin().getLogger().info("[TaN] Query batch executor shutdown");
    }
  }
}
