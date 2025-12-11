package org.leralix.tan.tasks;

import org.leralix.tan.TownsAndNations;
import org.leralix.tan.redis.RedisSyncManager;
import org.leralix.tan.utils.CocoLogger;
import org.leralix.tan.utils.FoliaScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReconciliationTask {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReconciliationTask.class);

  private static final long INTERVAL_TICKS = 3600L;

  public void start() {
    FoliaScheduler.runTaskTimer(
        TownsAndNations.getPlugin(),
        () -> {
          try {
            RedisSyncManager sync = TownsAndNations.getPlugin().getRedisSyncManager();
            if (sync != null) {
              sync.publishCacheInvalidation("global:periodic-reconcile");
              LOGGER.info(
                  CocoLogger.network("⇄ Réconciliation périodique: invalidation globale publiée"));
            }
          } catch (Exception ex) {
            LOGGER.warn(CocoLogger.warning("Échec réconciliation périodique: " + ex.getMessage()));
          }
        },
        200L,
        INTERVAL_TICKS);
  }
}
