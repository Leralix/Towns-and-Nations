package org.leralix.tan.storage.database;

import org.leralix.tan.TownsAndNations;
import org.leralix.tan.utils.FoliaScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseHealthCheck {

  private static final Logger logger = LoggerFactory.getLogger(DatabaseHealthCheck.class);
  private static final long CHECK_INTERVAL_TICKS = 400L;

  private final DatabaseHandler databaseHandler;
  private final TownsAndNations plugin;
  private boolean wasConnected = true;
  private volatile boolean isRunning = false;

  public DatabaseHealthCheck(DatabaseHandler databaseHandler, TownsAndNations plugin) {
    this.databaseHandler = databaseHandler;
    this.plugin = plugin;
  }

  public void start() {
    if (isRunning) {
      return;
    }

    isRunning = true;
    logger.info("[TaN] Starting database health check (interval: 20 seconds)");
    scheduleNextCheck();
  }

  public void stop() {
    isRunning = false;
    logger.info("[TaN] Database health check stopped");
  }

  private void scheduleNextCheck() {
    if (!isRunning) {
      return;
    }

    FoliaScheduler.runTaskLaterAsynchronously(
        plugin,
        () -> {
          checkDatabaseHealth();
          scheduleNextCheck();
        },
        CHECK_INTERVAL_TICKS);
  }

  private void checkDatabaseHealth() {
    try {
      boolean isConnected = databaseHandler.isConnectionValid();

      if (!isConnected && wasConnected) {
        logger.error("[TaN] ⚠️  DATABASE CONNECTION LOST!");
        wasConnected = false;
        attemptReconnection();
      } else if (isConnected && !wasConnected) {
        logger.info("[TaN] ✅ DATABASE CONNECTION RESTORED!");
        wasConnected = true;
      }

      if (isConnected && databaseHandler instanceof MySqlHandler) {
        logPoolMetrics();
      }
    } catch (Exception e) {
      logger.error("[TaN] Error in health check: " + e.getMessage(), e);
    }
  }

  private void attemptReconnection() {
    try {
      if (databaseHandler instanceof MySqlHandler) {
        MySqlHandler mySqlHandler = (MySqlHandler) databaseHandler;
        logger.warn("[TaN] Attempting automatic database reconnection...");
        mySqlHandler.reconnect();

        if (databaseHandler.isConnectionValid()) {
          logger.info("[TaN] ✅ Automatic reconnection successful!");
          wasConnected = true;
        } else {
          logger.error("[TaN] Automatic reconnection failed, will retry in 20 seconds");
        }
      }
    } catch (Exception e) {
      logger.error("[TaN] Error during automatic reconnection: " + e.getMessage(), e);
    }
  }

  private void logPoolMetrics() {
    try {
      if (databaseHandler.getDataSource() instanceof com.zaxxer.hikari.HikariDataSource) {
        com.zaxxer.hikari.HikariDataSource hikari =
            (com.zaxxer.hikari.HikariDataSource) databaseHandler.getDataSource();
        var mxBean = hikari.getHikariPoolMXBean();

        int active = mxBean.getActiveConnections();
        int idle = mxBean.getIdleConnections();
        int total = mxBean.getTotalConnections();

        if (active > 20) {
          logger.warn(
              String.format(
                  "[TaN] DB Pool HIGH LOAD: %d active, %d idle, %d total connections",
                  active, idle, total));
        } else if (active > 10) {
          logger.debug(
              String.format(
                  "[TaN] DB Pool: %d active, %d idle, %d total connections", active, idle, total));
        }
      }
    } catch (Exception e) {
      logger.trace("[TaN] Error logging pool metrics: " + e.getMessage());
    }
  }

  public boolean isConnected() {
    return wasConnected && databaseHandler.isConnectionValid();
  }
}
