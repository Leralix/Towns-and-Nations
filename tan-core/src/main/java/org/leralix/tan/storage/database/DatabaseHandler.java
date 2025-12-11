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

  protected QueryBatchExecutor queryBatchExecutor;

  protected BatchWriteOptimizer batchWriteOptimizer;

  public abstract void connect() throws SQLException;

  public abstract void close();

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
    int maxTransactions =
        TownsAndNations.getPlugin().getConfig().getInt("database.max-transaction-history", 1000);

    String selectSQL =
        """
        SELECT date, type, territoryDataID, transactionParty, amount
        FROM territoryTransactionHistory
        WHERE territoryDataID = ? AND type = ?
        ORDER BY date DESC
        LIMIT ?
    """;

    Map<String, List<TransactionHistory>> groupedByDate = new HashMap<>();

    try (Connection conn = dataSource.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(selectSQL)) {
      preparedStatement.setString(1, territoryData.getID());
      preparedStatement.setString(2, type.toString());
      preparedStatement.setInt(3, maxTransactions);

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
      TownsAndNations.getPlugin()
          .getLogger()
          .severe("Error while getting transaction history: " + e.getMessage());
    }
    return new ArrayList<>(groupedByDate.values());
  }

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
    String createTableSQL;
    if (isMySQL()) {
      createTableSQL =
          """
                CREATE TABLE IF NOT EXISTS territoryTransactionHistory (
                date VARCHAR(255),
                type VARCHAR(100),
                territoryDataID VARCHAR(255),
                transactionParty VARCHAR(255),
                amount DOUBLE,
                INDEX idx_territory_type_date (territoryDataID, type, date),
                INDEX idx_date (date)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
            """;
    } else {
      createTableSQL =
          """
                CREATE TABLE IF NOT EXISTS territoryTransactionHistory (
                date TEXT,
                type TEXT,
                territoryDataID TEXT,
                transactionParty TEXT,
                amount DOUBLE
            )
            """;
    }

    try (Connection conn = dataSource.getConnection();
        Statement statement = conn.createStatement()) {
      statement.execute(createTableSQL);

      if (!isMySQL()) {
        try {
          statement.execute(
              "CREATE INDEX IF NOT EXISTS idx_territory_type_date ON territoryTransactionHistory (territoryDataID, type, date)");
          statement.execute(
              "CREATE INDEX IF NOT EXISTS idx_date ON territoryTransactionHistory (date)");
        } catch (SQLException e) {
          TownsAndNations.getPlugin()
              .getLogger()
              .fine("Indexes already exist on territoryTransactionHistory: " + e.getMessage());
        }
      }
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

  public boolean isMySQL() {
    return this instanceof MySqlHandler;
  }

  private boolean isValidTableName(String tableName) {
    return tableName != null && tableName.matches("^[a-zA-Z0-9_]+$");
  }

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

  public QueryBatchExecutor getQueryBatchExecutor() {
    return queryBatchExecutor;
  }

  public BatchWriteOptimizer getBatchWriteOptimizer() {
    return batchWriteOptimizer;
  }

  public void initializeBatchWriter(int batchSize, long flushIntervalMs) {
    if (batchWriteOptimizer != null) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("[TaN] Batch write optimizer already initialized");
      return;
    }

    this.batchWriteOptimizer =
        new BatchWriteOptimizer(
            TownsAndNations.getPlugin(), dataSource, batchSize, flushIntervalMs);

    TownsAndNations.getPlugin()
        .getLogger()
        .info(
            "[TaN] Batch write optimizer initialized (Folia-compatible): batch="
                + batchSize
                + ", flush="
                + flushIntervalMs
                + "ms");
  }

  public void shutdownQueryBatcher() {
    if (queryBatchExecutor != null) {
      queryBatchExecutor.shutdown();
      queryBatchExecutor = null;
      TownsAndNations.getPlugin().getLogger().info("[TaN] Query batch executor shutdown");
    }
  }

  public void shutdownBatchWriter() {
    if (batchWriteOptimizer != null) {
      batchWriteOptimizer.shutdown();
      batchWriteOptimizer = null;
      TownsAndNations.getPlugin().getLogger().info("[TaN] Batch write optimizer shutdown");
    }
  }
}
