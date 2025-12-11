package org.leralix.tan.monitoring;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.HTTPServer;
import java.io.IOException;
import java.util.logging.Logger;

public class MetricsCollector {

  private static final Logger logger = Logger.getLogger(MetricsCollector.class.getName());

  private static HTTPServer prometheusServer;

  private static final Histogram dbQueryLatency =
      Histogram.build()
          .name("tan_db_query_latency_ms")
          .help("Database query latency in milliseconds")
          .buckets(1, 5, 10, 25, 50, 100, 250, 500, 1000, 2500, 5000)
          .register();

  private static final Gauge dbConnectionPool =
      Gauge.build()
          .name("tan_db_connection_pool_active")
          .help("Active database connections in pool")
          .register();

  private static final Counter dbErrors =
      Counter.build()
          .name("tan_db_errors_total")
          .help("Total database errors")
          .labelNames("error_type")
          .register();

  private static final Counter redisCacheHits =
      Counter.build().name("tan_redis_cache_hits_total").help("Total Redis cache hits").register();

  private static final Counter redisCacheMisses =
      Counter.build()
          .name("tan_redis_cache_misses_total")
          .help("Total Redis cache misses")
          .register();

  private static final Histogram redisLatency =
      Histogram.build()
          .name("tan_redis_latency_ms")
          .help("Redis operation latency in milliseconds")
          .buckets(0.1, 0.5, 1, 2, 5, 10, 25, 50, 100)
          .register();

  private static final Counter redisErrors =
      Counter.build()
          .name("tan_redis_errors_total")
          .help("Total Redis connection errors")
          .register();

  private static final Histogram territoryLoadTime =
      Histogram.build()
          .name("tan_territory_load_time_ms")
          .help("Territory load time in milliseconds")
          .buckets(1, 5, 10, 25, 50, 100, 250, 500, 1000)
          .register();

  private static final Gauge territoryCount =
      Gauge.build()
          .name("tan_territory_count")
          .help("Total number of territories loaded")
          .register();

  private static final Gauge territoryMemory =
      Gauge.build()
          .name("tan_territory_memory_mb")
          .help("Territory memory usage in megabytes")
          .register();

  private static final Gauge playerCount =
      Gauge.build().name("tan_player_count").help("Current online player count").register();

  private static final Gauge heapMemory =
      Gauge.build()
          .name("tan_heap_memory_usage_mb")
          .help("JVM heap memory usage in megabytes")
          .register();

  private static final Histogram guiRenderTime =
      Histogram.build()
          .name("tan_gui_render_time_ms")
          .help("GUI render time in milliseconds")
          .buckets(1, 5, 10, 25, 50, 100, 250, 500, 1000)
          .register();

  public static void initialize(int port) throws IOException {
    if (prometheusServer != null) {
      logger.warning("[TaN-Metrics] Already initialized");
      return;
    }

    prometheusServer = new HTTPServer(port);
    logger.info("[TaN-Metrics] Prometheus metrics server started on port " + port);
    logger.info("[TaN-Metrics] Metrics available at http://localhost:" + port + "/metrics");
  }

  public static void initialize() throws IOException {
    initialize(9000);
  }

  public static void shutdown() {
    if (prometheusServer != null) {
      prometheusServer.stop();
      prometheusServer = null;
      logger.info("[TaN-Metrics] Prometheus server stopped");
    }
  }

  public static void recordDatabaseQueryLatency(long latencyMs) {
    dbQueryLatency.observe(latencyMs);
  }

  public static void setDatabaseConnectionPoolActive(int activeConnections) {
    dbConnectionPool.set(activeConnections);
  }

  public static void recordDatabaseError(String errorType) {
    dbErrors.labels(errorType).inc();
  }

  public static void recordCacheHit() {
    redisCacheHits.inc();
  }

  public static void recordCacheMiss() {
    redisCacheMisses.inc();
  }

  public static void recordRedisLatency(long latencyMs) {
    redisLatency.observe(latencyMs);
  }

  public static void recordRedisError() {
    redisErrors.inc();
  }

  public static void recordTerritoryLoadTime(long loadTimeMs) {
    territoryLoadTime.observe(loadTimeMs);
  }

  public static void setTerritoryCount(int count) {
    territoryCount.set(count);
  }

  public static void setTerritoryMemoryUsage(double memoryMB) {
    territoryMemory.set(memoryMB);
  }

  public static void setPlayerCount(int count) {
    playerCount.set(count);
  }

  public static void updateHeapMemoryUsage() {
    Runtime runtime = Runtime.getRuntime();
    long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
    heapMemory.set(usedMemory);
  }

  public static void recordGuiRenderTime(long renderTimeMs) {
    guiRenderTime.observe(renderTimeMs);
  }

  public static String getMetricsSummary() {
    double cacheHitRate = 0.0;
    double totalCacheOps = redisCacheHits.get() + redisCacheMisses.get();
    if (totalCacheOps > 0) {
      cacheHitRate = (redisCacheHits.get() / totalCacheOps) * 100;
    }

    Runtime runtime = Runtime.getRuntime();
    long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);

    return String.format(
        "=== Towns & Nations Metrics ===\n"
            + "Cache Hit Rate: %.1f%%\n"
            + "Territory Count: %.0f\n"
            + "Player Count: %.0f\n"
            + "Heap Memory: %d MB\n"
            + "DB Connection Pool: %.0f active\n"
            + "Metrics endpoint: http://localhost:%d/metrics",
        cacheHitRate,
        territoryCount.get(),
        playerCount.get(),
        usedMemory,
        dbConnectionPool.get(),
        prometheusServer != null ? 9000 : 0);
  }
}
