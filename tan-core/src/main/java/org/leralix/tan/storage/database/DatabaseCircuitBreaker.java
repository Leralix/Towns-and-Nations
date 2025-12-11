package org.leralix.tan.storage.database;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class DatabaseCircuitBreaker {

  private static final Logger logger = Logger.getLogger(DatabaseCircuitBreaker.class.getName());

  private static CircuitBreaker circuitBreaker;

  public static void initialize() {
    CircuitBreakerConfig config =
        CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .slowCallRateThreshold(50)
            .slowCallDurationThreshold(Duration.ofSeconds(5))
            .waitDurationInOpenState(Duration.ofSeconds(20))
            .permittedNumberOfCallsInHalfOpenState(5)
            .minimumNumberOfCalls(10)
            .slidingWindowSize(100)
            .build();

    CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
    circuitBreaker = registry.circuitBreaker("databaseCircuitBreaker");

    circuitBreaker
        .getEventPublisher()
        .onStateTransition(
            event -> {
              logger.warning(
                  "[TaN-CircuitBreaker] State transition: "
                      + event.getStateTransition()
                      + " at "
                      + event.getCreationTime());
            })
        .onFailureRateExceeded(
            event -> {
              logger.severe(
                  "[TaN-CircuitBreaker] Failure rate exceeded: " + event.getFailureRate() + "%");
            })
        .onSlowCallRateExceeded(
            event -> {
              logger.warning(
                  "[TaN-CircuitBreaker] Slow call rate exceeded: " + event.getSlowCallRate() + "%");
            });

    logger.info("[TaN-CircuitBreaker] Initialized successfully");
  }

  public static <T> T execute(Supplier<T> operation) throws Exception {
    if (circuitBreaker == null) {
      throw new IllegalStateException("Circuit breaker not initialized. Call initialize() first.");
    }

    return circuitBreaker.decorateSupplier(operation).get();
  }

  public static void execute(Runnable operation) throws Exception {
    if (circuitBreaker == null) {
      throw new IllegalStateException("Circuit breaker not initialized. Call initialize() first.");
    }

    circuitBreaker.decorateRunnable(operation).run();
  }

  public static <T> T executeWithFallback(
      Supplier<T> operation, T fallbackValue, String operationName) {

    if (circuitBreaker == null) {
      logger.warning("[TaN-CircuitBreaker] Not initialized, executing without protection");
      return operation.get();
    }

    try {
      return circuitBreaker.decorateSupplier(operation).get();
    } catch (Exception e) {
      if (circuitBreaker.getState() == CircuitBreaker.State.OPEN) {
        logger.warning("[TaN-CircuitBreaker] Circuit OPEN, using fallback for: " + operationName);
      } else {
        logger.warning(
            "[TaN-CircuitBreaker] Operation failed: " + operationName + " - " + e.getMessage());
      }
      return fallbackValue;
    }
  }

  public static CircuitBreaker.State getState() {
    if (circuitBreaker == null) {
      return null;
    }
    return circuitBreaker.getState();
  }

  public static String getStats() {
    if (circuitBreaker == null) {
      return "Circuit breaker not initialized";
    }

    CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();

    return String.format(
        "State: %s | Calls: %d | Failures: %d (%.1f%%) | Slow: %d (%.1f%%)",
        circuitBreaker.getState(),
        metrics.getNumberOfSuccessfulCalls() + metrics.getNumberOfFailedCalls(),
        metrics.getNumberOfFailedCalls(),
        metrics.getFailureRate(),
        metrics.getNumberOfSlowCalls(),
        metrics.getSlowCallRate());
  }

  public static void transitionToClosed() {
    if (circuitBreaker != null) {
      circuitBreaker.transitionToClosedState();
      logger.info("[TaN-CircuitBreaker] Manually transitioned to CLOSED state");
    }
  }

  public static void transitionToOpen() {
    if (circuitBreaker != null) {
      circuitBreaker.transitionToOpenState();
      logger.warning("[TaN-CircuitBreaker] Manually transitioned to OPEN state");
    }
  }
}
