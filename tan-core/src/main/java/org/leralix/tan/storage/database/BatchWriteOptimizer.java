package org.leralix.tan.storage.database;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.bukkit.plugin.Plugin;
import org.leralix.tan.utils.CocoLogger;
import org.leralix.tan.utils.FoliaScheduler;

public class BatchWriteOptimizer {

  private static final Logger logger = Logger.getLogger(BatchWriteOptimizer.class.getName());

  private final Plugin plugin;
  private final DataSource dataSource;
  private final int batchSize;
  private final long flushIntervalMs;

  private final Map<String, ConcurrentLinkedQueue<WriteOperation>> writeQueues =
      new ConcurrentHashMap<>();

  private ScheduledTask scheduledTask;
  private volatile boolean running = true;

  private static class WriteOperation {
    final String id;
    final String jsonData;
    final CompletableFuture<Void> future;

    WriteOperation(String id, String jsonData, CompletableFuture<Void> future) {
      this.id = id;
      this.jsonData = jsonData;
      this.future = future;
    }
  }

  public BatchWriteOptimizer(
      Plugin plugin, DataSource dataSource, int batchSize, long flushIntervalMs) {
    this.plugin = plugin;
    this.dataSource = dataSource;
    this.batchSize = batchSize;
    this.flushIntervalMs = flushIntervalMs;

    long flushIntervalTicks = flushIntervalMs / 50;

    FoliaScheduler.runTaskTimer(
        plugin, this::flushAllQueues, flushIntervalTicks, flushIntervalTicks);

    logger.info(
        CocoLogger.database(
            String.format(
                "⚙ BatchWrite initialisé (Folia): batch=%d, flush=%dms",
                batchSize, flushIntervalMs)));
  }

  public CompletableFuture<Void> queueWrite(String tableName, String id, String jsonData) {
    if (!running) {
      CompletableFuture<Void> future = new CompletableFuture<>();
      future.completeExceptionally(new IllegalStateException("BatchWriteOptimizer is shut down"));
      return future;
    }

    CompletableFuture<Void> future = new CompletableFuture<>();
    WriteOperation op = new WriteOperation(id, jsonData, future);

    ConcurrentLinkedQueue<WriteOperation> queue =
        writeQueues.computeIfAbsent(tableName, k -> new ConcurrentLinkedQueue<>());

    queue.offer(op);

    if (queue.size() >= batchSize) {
      FoliaScheduler.runTaskAsynchronously(plugin, () -> flushQueue(tableName));
    }

    return future;
  }

  private void flushAllQueues() {
    for (String tableName : writeQueues.keySet()) {
      flushQueue(tableName);
    }
  }

  private void flushQueue(String tableName) {
    ConcurrentLinkedQueue<WriteOperation> queue = writeQueues.get(tableName);
    if (queue == null || queue.isEmpty()) {
      return;
    }

    List<WriteOperation> batch = new ArrayList<>(batchSize);
    WriteOperation op;
    while (batch.size() < batchSize && (op = queue.poll()) != null) {
      batch.add(op);
    }

    if (batch.isEmpty()) {
      return;
    }

    long startTime = System.currentTimeMillis();
    executeBatch(tableName, batch);
    long duration = System.currentTimeMillis() - startTime;

    if (batch.size() > 10) {
      double writesPerSec = batch.size() * 1000.0 / Math.max(duration, 1);
      logger.info(
          CocoLogger.database(
              String.format(
                  "✓ Flush %d écritures vers %s en "
                      + CocoLogger.formatTime(duration)
                      + " (%.1f écr/sec)",
                  batch.size(),
                  tableName,
                  writesPerSec)));
    }
  }

  private void executeBatch(String tableName, List<WriteOperation> batch) {
    String upsertSQL = getUpsertSQL(tableName);

    Connection conn = null;
    try {
      conn = dataSource.getConnection();
      conn.setAutoCommit(false);

      try (PreparedStatement ps = conn.prepareStatement(upsertSQL)) {
        for (WriteOperation op : batch) {
          ps.setString(1, op.id);
          ps.setString(2, op.jsonData);
          ps.addBatch();
        }

        ps.executeBatch();
        conn.commit();

        for (WriteOperation op : batch) {
          op.future.complete(null);
        }

      } catch (SQLException e) {
        try {
          conn.rollback();
        } catch (SQLException rollbackEx) {
          logger.severe(CocoLogger.error("❌ Rollback échoué: " + rollbackEx.getMessage()));
        }

        for (WriteOperation op : batch) {
          op.future.completeExceptionally(e);
        }

        logger.severe(
            CocoLogger.error(
                String.format(
                    "❌ Batch write échoué pour %s (%d opérations): %s",
                    tableName, batch.size(), e.getMessage())));
        throw e;
      } finally {
        conn.setAutoCommit(true);
      }

    } catch (SQLException e) {
      logger.severe(CocoLogger.error("❌ Erreur connexion BDD: " + e.getMessage()));

      for (WriteOperation op : batch) {
        op.future.completeExceptionally(e);
      }
    } finally {
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          logger.warning(CocoLogger.warning("⚠ Erreur fermeture connexion: " + e.getMessage()));
        }
      }
    }
  }

  private String getUpsertSQL(String tableName) {
    boolean isMySQL = isMySQL();

    if (isMySQL) {
      return "INSERT INTO "
          + tableName
          + " (id, data) VALUES (?, ?) ON DUPLICATE KEY UPDATE data = VALUES(data)";
    } else {
      return "INSERT OR REPLACE INTO " + tableName + " (id, data) VALUES (?, ?)";
    }
  }

  private boolean isMySQL() {
    try (Connection conn = dataSource.getConnection()) {
      String dbProductName = conn.getMetaData().getDatabaseProductName().toLowerCase();
      return dbProductName.contains("mysql") || dbProductName.contains("mariadb");
    } catch (SQLException e) {
      return false;
    }
  }

  public void flushAll() {
    logger.info(CocoLogger.loading("flush forcé de toutes les écritures en attente"));
    flushAllQueues();
    logger.info(CocoLogger.success("✓ Flush terminé"));
  }

  public void shutdown() {
    running = false;
    logger.info(CocoLogger.loading("arrêt BatchWrite"));

    flushAll();

    if (scheduledTask != null && !scheduledTask.isCancelled()) {
      scheduledTask.cancel();
    }

    logger.info(CocoLogger.success("✓ BatchWrite arrêté"));
  }

  public String getStats() {
    int totalPending = writeQueues.values().stream().mapToInt(ConcurrentLinkedQueue::size).sum();

    return String.format(
        "BatchWrite - Tables: %d, Pending: %d, Batch Size: %d, Flush Interval: %dms",
        writeQueues.size(), totalPending, batchSize, flushIntervalMs);
  }
}
