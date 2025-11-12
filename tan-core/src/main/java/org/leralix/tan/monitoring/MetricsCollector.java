package org.leralix.tan.monitoring;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.HTTPServer;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * AMÉLIORATION #7: Metrics Collector (Prometheus Integration)
 *
 * <p>Collects and exposes metrics for monitoring plugin performance in production.
 *
 * <p><b>Metrics Collected:</b>
 *
 * <ul>
 *   <li><b>Database:</b> Query latency, connection pool, errors
 *   <li><b>Redis:</b> Cache hits/misses, latency, errors
 *   <li><b>Territories:</b> Load times, total count, memory usage
 *   <li><b>Server:</b> Player count, heap memory, GUI render times
 * </ul>
 *
 * <p><b>Integration with Prometheus:</b>
 *
 * <ol>
 *   <li>Plugin exposes metrics on port 9000 (configurable)
 *   <li>Prometheus scrapes metrics every 15s
 *   <li>Grafana visualizes metrics in dashboards
 *   <li>Alerts trigger on anomalies
 * </ol>
 *
 * <p><b>Configuration (config.yml):</b>
 *
 * <pre>
 * monitoring:
 *   enabled: true
 *   prometheus:
 *     enabled: true
 *     port: 9000
 *     path: "/metrics"
 *   metrics:
 *     database: true
 *     redis: true
 *     memory: true
 *     gui: true
 * </pre>
 *
 * <p><b>Usage Examples:</b>
 *
 * <pre>
 * // Record database query
 * long start = System.currentTimeMillis();
 * results = databaseHandler.executeQuery();
 * MetricsCollector.recordDatabaseQueryLatency(System.currentTimeMillis() - start);
 *
 * // Record cache hit/miss
 * if (cacheHit) {
 *     MetricsCollector.recordCacheHit();
 * } else {
 *     MetricsCollector.recordCacheMiss();
 * }
 *
 * // Record territory load
 * MetricsCollector.recordTerritoryLoadTime(loadTimeMs);
 * </pre>
 *
 * @author Leralix (with AI assistance)
 * @version 0.16.0
 * @since 2025-11-12
 */
public class MetricsCollector {

  private static final Logger logger = Logger.getLogger(MetricsCollector.class.getName());

  // HTTP server for Prometheus scraping
  private static HTTPServer prometheusServer;

  // ========== DATABASE METRICS ==========

  /** Database query latency histogram (milliseconds) */
  private static final Histogram dbQueryLatency =
      Histogram.build()
          .name("tan_db_query_latency_ms")
          .help("Database query latency in milliseconds")
          .buckets(1, 5, 10, 25, 50, 100, 250, 500, 1000, 2500, 5000)
          .register();

  /** Database connection pool gauge */
  private static final Gauge dbConnectionPool =
      Gauge.build()
          .name("tan_db_connection_pool_active")
          .help("Active database connections in pool")
          .register();

  /** Database error counter */
  private static final Counter dbErrors =
      Counter.build()
          .name("tan_db_errors_total")
          .help("Total database errors")
          .labelNames("error_type")
          .register();

  // ========== REDIS METRICS ==========

  /** Redis cache hit counter */
  private static final Counter redisCacheHits =
      Counter.build().name("tan_redis_cache_hits_total").help("Total Redis cache hits").register();

  /** Redis cache miss counter */
  private static final Counter redisCacheMisses =
      Counter.build()
          .name("tan_redis_cache_misses_total")
          .help("Total Redis cache misses")
          .register();

  /** Redis latency histogram (milliseconds) */
  private static final Histogram redisLatency =
      Histogram.build()
          .name("tan_redis_latency_ms")
          .help("Redis operation latency in milliseconds")
          .buckets(0.1, 0.5, 1, 2, 5, 10, 25, 50, 100)
          .register();

  /** Redis connection errors */
  private static final Counter redisErrors =
      Counter.build()
          .name("tan_redis_errors_total")
          .help("Total Redis connection errors")
          .register();

  // ========== TERRITORY METRICS ==========

  /** Territory load time histogram (milliseconds) */
  private static final Histogram territoryLoadTime =
      Histogram.build()
          .name("tan_territory_load_time_ms")
          .help("Territory load time in milliseconds")
          .buckets(1, 5, 10, 25, 50, 100, 250, 500, 1000)
          .register();

  /** Total territories gauge */
  private static final Gauge territoryCount =
      Gauge.build()
          .name("tan_territory_count")
          .help("Total number of territories loaded")
          .register();

  /** Territory memory usage gauge (MB) */
  private static final Gauge territoryMemory =
      Gauge.build()
          .name("tan_territory_memory_mb")
          .help("Territory memory usage in megabytes")
          .register();

  // ========== SERVER METRICS ==========

  /** Player count gauge */
  private static final Gauge playerCount =
      Gauge.build().name("tan_player_count").help("Current online player count").register();

  /** Heap memory usage gauge (MB) */
  private static final Gauge heapMemory =
      Gauge.build()
          .name("tan_heap_memory_usage_mb")
          .help("JVM heap memory usage in megabytes")
          .register();

  /** GUI render time histogram (milliseconds) */
  private static final Histogram guiRenderTime =
      Histogram.build()
          .name("tan_gui_render_time_ms")
          .help("GUI render time in milliseconds")
          .buckets(1, 5, 10, 25, 50, 100, 250, 500, 1000)
          .register();

  // ========== INITIALIZATION ==========

  /**
   * Initializes the metrics collector and starts Prometheus HTTP server.
   *
   * <p>Should be called once during plugin initialization.
   *
   * @param port The port for Prometheus metrics endpoint (default: 9000)
   * @throws IOException if the HTTP server cannot start
   */
  public static void initialize(int port) throws IOException {
    if (prometheusServer != null) {
      logger.warning("[TaN-Metrics] Already initialized");
      return;
    }

    prometheusServer = new HTTPServer(port);
    logger.info("[TaN-Metrics] Prometheus metrics server started on port " + port);
    logger.info("[TaN-Metrics] Metrics available at http://localhost:" + port + "/metrics");
  }

  /** Initializes with default port 9000. */
  public static void initialize() throws IOException {
    initialize(9000);
  }

  /** Shuts down the Prometheus HTTP server. */
  public static void shutdown() {
    if (prometheusServer != null) {
      prometheusServer.stop();
      prometheusServer = null;
      logger.info("[TaN-Metrics] Prometheus server stopped");
    }
  }

  // ========== DATABASE RECORDING METHODS ==========

  public static void recordDatabaseQueryLatency(long latencyMs) {
    dbQueryLatency.observe(latencyMs);
  }

  public static void setDatabaseConnectionPoolActive(int activeConnections) {
    dbConnectionPool.set(activeConnections);
  }

  public static void recordDatabaseError(String errorType) {
    dbErrors.labels(errorType).inc();
  }

  // ========== REDIS RECORDING METHODS ==========

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

  // ========== TERRITORY RECORDING METHODS ==========

  public static void recordTerritoryLoadTime(long loadTimeMs) {
    territoryLoadTime.observe(loadTimeMs);
  }

  public static void setTerritoryCount(int count) {
    territoryCount.set(count);
  }

  public static void setTerritoryMemoryUsage(double memoryMB) {
    territoryMemory.set(memoryMB);
  }

  // ========== SERVER RECORDING METHODS ==========

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

  // ========== UTILITY METHODS ==========

  /**
   * Gets a summary of current metrics (for debug commands).
   *
   * @return A formatted string with key metrics
   */
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
