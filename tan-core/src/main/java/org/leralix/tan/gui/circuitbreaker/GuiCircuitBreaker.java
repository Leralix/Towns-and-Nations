package org.leralix.tan.gui.circuitbreaker;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiCircuitBreaker {

  private static final Logger LOGGER = LoggerFactory.getLogger(GuiCircuitBreaker.class);

  private enum State {
    CLOSED,
    OPEN,
    HALF_OPEN
  }

  private volatile State state = State.CLOSED;
  private final AtomicInteger failureCount = new AtomicInteger(0);
  private final int failureThreshold;
  private final long timeoutMs;
  private volatile long lastFailureTime = 0;

  public GuiCircuitBreaker(int failureThreshold, long timeoutMs) {
    this.failureThreshold = failureThreshold;
    this.timeoutMs = timeoutMs;
  }

  public void execute(
      Runnable operation, Consumer<Throwable> onSuccess, Consumer<Throwable> onFailure) {

    if (state == State.OPEN) {
      if (System.currentTimeMillis() - lastFailureTime > timeoutMs) {
        state = State.HALF_OPEN;
        LOGGER.info("Circuit breaker entering HALF_OPEN state");
      } else {
        onFailure.accept(new CircuitBreakerException("Circuit breaker is OPEN for GUI operations"));
        return;
      }
    }

    try {
      operation.run();
      onSuccess.accept(null);

      if (failureCount.get() > 0) {
        failureCount.set(0);
        if (state == State.HALF_OPEN) {
          state = State.CLOSED;
          LOGGER.info("Circuit breaker recovered to CLOSED state");
        }
      }
    } catch (Exception ex) {
      int failures = failureCount.incrementAndGet();
      lastFailureTime = System.currentTimeMillis();

      LOGGER.warn(
          "GUI operation failed (failure "
              + failures
              + "/"
              + failureThreshold
              + "): "
              + ex.getMessage());

      if (failures >= failureThreshold) {
        state = State.OPEN;
        LOGGER.error("Circuit breaker OPENED after " + failures + " failures");
      }

      onFailure.accept(ex);
    }
  }

  public String getState() {
    return state.name() + " (failures: " + failureCount.get() + "/" + failureThreshold + ")";
  }

  public static class CircuitBreakerException extends RuntimeException {
    public CircuitBreakerException(String message) {
      super(message);
    }
  }
}
