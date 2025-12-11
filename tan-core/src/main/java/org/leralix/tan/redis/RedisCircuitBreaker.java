package org.leralix.tan.redis;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Circuit breaker for Redis operations to prevent cascading failures.
 * 
 * <p>Protects the system when Redis is unavailable by:
 * <ul>
 *   <li>Detecting consecutive failures (5 threshold)</li>
 *   <li>Opening circuit to prevent further Redis calls</li>
 *   <li>Automatically recovering after wait duration (30s)</li>
 *   <li>Falling back to MySQL queries when circuit is OPEN</li>
 * </ul>
 * 
 * <p><b>Circuit States:</b>
 * <ul>
 *   <li><b>CLOSED</b>: Normal operation, Redis calls allowed</li>
 *   <li><b>OPEN</b>: Redis unavailable, all calls fail-fast with fallback</li>
 *   <li><b>HALF_OPEN</b>: Testing recovery, limited Redis calls allowed</li>
 * </ul>
 * 
 * <p><b>Configuration:</b>
 * <pre>
 * Failure Rate Threshold: 50% (5 failures out of 10 calls)
 * Slow Call Threshold: 3 seconds
 * Wait Duration in OPEN: 30 seconds
 * Permitted Calls in HALF_OPEN: 3
 * Sliding Window Size: 10 calls
 * </pre>
 * 
 * <p><b>Example usage:</b>
 * <pre>{@code
 * String value = RedisCircuitBreaker.execute(
 *     () -> jedisManager.hashGet("tan:cache", key),
 *     () -> {
 *       logger.warning("Redis circuit OPEN - falling back to MySQL");
 *       return fetchFromMySQL(key);
 *     }
 * );
 * }</pre>
 * 
 * @since 0.18.0
 * @see QueryCacheManager
 * @see JedisManager
 */
public class RedisCircuitBreaker {

  private static final Logger logger = Logger.getLogger(RedisCircuitBreaker.class.getName());
  private static CircuitBreaker circuitBreaker;

  /**
   * Initializes the circuit breaker with production-ready configuration.
   * <p><b>CRITICAL:</b> Must be called during plugin startup BEFORE any Redis operations.
   */
  public static void initialize() {
    CircuitBreakerConfig config =
        CircuitBreakerConfig.custom()
            .failureRateThreshold(50) // Open circuit if 50% of calls fail
            .slowCallRateThreshold(50) // Consider slow if 50% of calls exceed duration
            .slowCallDurationThreshold(Duration.ofSeconds(3)) // Slow call = >3s
            .waitDurationInOpenState(Duration.ofSeconds(30)) // Wait 30s before HALF_OPEN
            .permittedNumberOfCallsInHalfOpenState(3) // Test with 3 calls in HALF_OPEN
            .slidingWindowSize(10) // Track last 10 calls for failure rate
            .minimumNumberOfCalls(5) // Need 5 calls before calculating failure rate
            .automaticTransitionFromOpenToHalfOpenEnabled(true) // Auto-recover after wait
            .recordExceptions(Exception.class) // Count all exceptions as failures
            .build();

    CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
    circuitBreaker = registry.circuitBreaker("redis-cache");

    // Register event listeners for debugging and monitoring
    circuitBreaker
        .getEventPublisher()
        .onStateTransition(
            event -> {
              logger.warning(
                  String.format(
                      "[TaN-CIRCUIT-BREAKER] State transition: %s → %s | Reason: %s",
                      event.getStateTransition().getFromState(),
                      event.getStateTransition().getToState(),
                      event.getStateTransition().name()));
            });

    circuitBreaker
        .getEventPublisher()
        .onError(
            event -> {
              logger.warning(
                  String.format(
                      "[TaN-CIRCUIT-BREAKER] Error recorded: %s | Duration: %dms | State: %s",
                      event.getThrowable().getMessage(),
                      event.getElapsedDuration().toMillis(),
                      circuitBreaker.getState()));
            });

    circuitBreaker
        .getEventPublisher()
        .onSuccess(
            event -> {
              logger.fine(
                  String.format(
                      "[TaN-CIRCUIT-BREAKER] Success recorded: Duration=%dms | State=%s",
                      event.getElapsedDuration().toMillis(), circuitBreaker.getState()));
            });

    circuitBreaker
        .getEventPublisher()
        .onCallNotPermitted(
            event -> {
              logger.info(
                  String.format(
                      "[TaN-CIRCUIT-BREAKER] Call blocked (circuit OPEN) | State: %s",
                      circuitBreaker.getState()));
            });

    logger.info("[TaN-CIRCUIT-BREAKER] Initialized with config: " + config);
  }

  /**
   * Executes a Redis operation with circuit breaker protection and automatic fallback.
   * 
   * <p><b>Circuit behavior:</b>
   * <ul>
   *   <li><b>CLOSED</b>: Executes redisOperation normally</li>
   *   <li><b>OPEN</b>: Skips redisOperation, executes fallback immediately</li>
   *   <li><b>HALF_OPEN</b>: Tests redisOperation, fallback on failure</li>
   * </ul>
   * 
   * @param <T> return type
   * @param redisOperation Redis call to execute (e.g., jedis.get())
   * @param fallback Fallback operation when circuit is OPEN (e.g., MySQL query)
   * @return result from Redis or fallback
   */
  public static <T> T execute(Supplier<T> redisOperation, Supplier<T> fallback) {
    if (circuitBreaker == null) {
      logger.severe(
          "[TaN-CIRCUIT-BREAKER] Not initialized! Call initialize() during plugin startup.");
      return fallback.get();
    }

    try {
      return circuitBreaker.executeSupplier(redisOperation);
    } catch (Exception e) {
      // Circuit breaker recorded the failure - now execute fallback
      logger.warning(
          String.format(
              "[TaN-CIRCUIT-BREAKER] Redis operation failed, executing fallback | State: %s | Error: %s",
              circuitBreaker.getState(), e.getMessage()));
      return fallback.get();
    }
  }

  /**
   * Executes a void Redis operation (write) with circuit breaker protection.
   * 
   * @param redisOperation Redis write operation (e.g., jedis.set())
   * @param fallback Fallback action when circuit is OPEN (e.g., log warning)
   */
  public static void executeVoid(Runnable redisOperation, Runnable fallback) {
    if (circuitBreaker == null) {
      logger.severe(
          "[TaN-CIRCUIT-BREAKER] Not initialized! Call initialize() during plugin startup.");
      fallback.run();
      return;
    }

    try {
      circuitBreaker.executeRunnable(redisOperation);
    } catch (Exception e) {
      logger.warning(
          String.format(
              "[TaN-CIRCUIT-BREAKER] Redis write failed, executing fallback | State: %s | Error: %s",
              circuitBreaker.getState(), e.getMessage()));
      fallback.run();
    }
  }

  /**
   * Gets current circuit breaker state for monitoring.
   * 
   * @return CLOSED, OPEN, HALF_OPEN, DISABLED, FORCED_OPEN, or METRICS_ONLY
   */
  public static CircuitBreaker.State getState() {
    return circuitBreaker != null
        ? circuitBreaker.getState()
        : CircuitBreaker.State.METRICS_ONLY;
  }

  /**
   * Gets failure rate percentage (0-100) for monitoring.
   * 
   * @return failure rate or -1 if not initialized
   */
  public static float getFailureRate() {
    return circuitBreaker != null ? circuitBreaker.getMetrics().getFailureRate() : -1;
  }

  /**
   * Gets total number of calls for monitoring.
   * 
   * @return number of calls or 0 if not initialized
   */
  public static int getNumberOfCalls() {
    return circuitBreaker != null ? circuitBreaker.getMetrics().getNumberOfBufferedCalls() : 0;
  }

  /**
   * Manually transitions circuit to CLOSED state (recovery).
   * <p><b>WARNING:</b> Only use for manual recovery after Redis is confirmed healthy.
   */
  public static void transitionToClosedState() {
    if (circuitBreaker != null) {
      circuitBreaker.transitionToClosedState();
      logger.info("[TaN-CIRCUIT-BREAKER] Manually transitioned to CLOSED state");
    }
  }

  /**
   * Manually transitions circuit to OPEN state (force failure mode).
   * <p><b>WARNING:</b> Only use for testing or emergency maintenance.
   */
  public static void transitionToOpenState() {
    if (circuitBreaker != null) {
      circuitBreaker.transitionToOpenState();
      logger.warning("[TaN-CIRCUIT-BREAKER] Manually transitioned to OPEN state");
    }
  }
}
