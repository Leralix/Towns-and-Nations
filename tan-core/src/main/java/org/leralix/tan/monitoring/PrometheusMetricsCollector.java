package org.leralix.tan.monitoring;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.HTTPServer;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prometheus metrics collector for Towns & Nations plugin.
 *
 * <p>Collects key performance metrics for monitoring: - Database query count and duration - Redis
 * cache operations - Active wars and territories - Player counts
 *
 * <p>Metrics exposed on HTTP port 9090 at /metrics endpoint.
 *
 * @author Auto-generated optimization
 * @since 0.16.0
 */
public class PrometheusMetricsCollector {

  private static final Logger LOGGER = LoggerFactory.getLogger(PrometheusMetricsCollector.class);

  // Counters
  private static final Counter queriesTotal =
      Counter.build()
          .name("tan_database_queries_total")
          .help("Total database queries executed")
          .labelNames("type", "status")
          .register();

  private static final Counter cacheHits =
      Counter.build()
          .name("tan_cache_hits_total")
          .help("Total cache hits")
          .labelNames("type")
          .register();

  private static final Counter cacheMisses =
      Counter.build()
          .name("tan_cache_misses_total")
          .help("Total cache misses")
          .labelNames("type")
          .register();

  // Histograms (latency)
  private static final Histogram queryDuration =
      Histogram.build()
          .name("tan_database_query_duration_seconds")
          .help("Database query execution time")
          .labelNames("type")
          .buckets(0.01, 0.05, 0.1, 0.5, 1.0, 5.0)
          .register();

  private static final Histogram cacheLatency =
      Histogram.build()
          .name("tan_cache_latency_seconds")
          .help("Cache operation latency")
          .labelNames("type")
          .buckets(0.001, 0.005, 0.01, 0.05, 0.1)
          .register();

  // Gauges
  private static final Gauge cachedTerritories =
      Gauge.build().name("tan_cached_territories").help("Number of cached territories").register();

  private static final Gauge activeWars =
      Gauge.build().name("tan_active_wars").help("Number of active wars").register();

  private static final Gauge playerCount =
      Gauge.build()
          .name("tan_players_online")
          .help("Number of players online with TAN data")
          .register();

  private static final Gauge redisConnectionPoolSize =
      Gauge.build()
          .name("tan_redis_pool_size")
          .help("Redis connection pool current size")
          .register();

  private static final Gauge mysqlConnectionPoolSize =
      Gauge.build()
          .name("tan_mysql_pool_size")
          .help("MySQL connection pool current size")
          .register();

  private HTTPServer httpServer;

  /** Start Prometheus HTTP server on specified port. */
  public void startServer(int port) {
    try {
      httpServer = new HTTPServer(port);
      LOGGER.info("Prometheus metrics server started on port " + port);
      LOGGER.info("Metrics available at http://localhost:" + port + "/metrics");
    } catch (IOException e) {
      LOGGER.error("Failed to start Prometheus server on port " + port, e);
    }
  }

  /** Stop Prometheus HTTP server. */
  @SuppressWarnings("deprecation") // HTTPServer.stop() is deprecated but necessary
  public void stopServer() {
    if (httpServer != null) {
      httpServer.stop();
      LOGGER.info("Prometheus metrics server stopped");
    }
  }

  // ===== QUERY METRICS =====

  public static void recordQueryExecution(String type, long durationMs, boolean success) {
    queriesTotal.labels(type, success ? "success" : "failed").inc();
    queryDuration.labels(type).observe(durationMs / 1000.0);
  }

  // ===== CACHE METRICS =====

  public static void recordCacheHit(String type, long latencyMs) {
    cacheHits.labels(type).inc();
    cacheLatency.labels(type).observe(latencyMs / 1000.0);
  }

  public static void recordCacheMiss(String type, long latencyMs) {
    cacheMisses.labels(type).inc();
    cacheLatency.labels(type).observe(latencyMs / 1000.0);
  }

  // ===== GAUGE SETTERS =====

  public static void setCachedTerritories(int count) {
    cachedTerritories.set(count);
  }

  public static void setActiveWars(int count) {
    activeWars.set(count);
  }

  public static void setPlayerCount(int count) {
    playerCount.set(count);
  }

  public static void setRedisPoolSize(int size) {
    redisConnectionPoolSize.set(size);
  }

  public static void setMysqlPoolSize(int size) {
    mysqlConnectionPoolSize.set(size);
  }
}
