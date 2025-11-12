package org.leralix.tan.storage.database;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Query rate limiter to prevent database pool saturation.
 *
 * <p>Limits concurrent queries to a configurable maximum and queues additional requests. This
 * prevents 800 simultaneous players from overwhelming the database.
 *
 * @author Auto-generated optimization
 * @since 0.16.0
 */
public class QueryLimiter {

  private static final Logger LOGGER = LoggerFactory.getLogger(QueryLimiter.class);

  private final Semaphore queryPermits;
  private final long timeoutMs;
  private final AtomicInteger queuedQueries = new AtomicInteger(0);
  private final AtomicInteger deniedQueries = new AtomicInteger(0);

  public QueryLimiter(int maxConcurrentQueries, long timeoutMs) {
    this.queryPermits = new Semaphore(maxConcurrentQueries, true);
    this.timeoutMs = timeoutMs;
  }

  /**
   * Acquire permit for a query. Blocks if limit reached until timeout.
   *
   * @return true if permit acquired, false if timeout
   */
  public boolean acquirePermit() {
    try {
      int queued = queuedQueries.incrementAndGet();

      // Warn if queue is getting deep
      if (queued > 50) {
        LOGGER.warn(
            "Query queue depth: " + queued + " (max: " + queryPermits.availablePermits() + ")");
      }

      boolean acquired = queryPermits.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS);
      queuedQueries.decrementAndGet();

      if (!acquired) {
        deniedQueries.incrementAndGet();
        LOGGER.error("Query timeout after " + timeoutMs + "ms - denying request");
      }

      return acquired;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    }
  }

  /** Release permit for a query. */
  public void releasePermit() {
    queryPermits.release();
  }

  /** Get statistics for monitoring. */
  public String getStats() {
    return String.format(
        "Queries - Available: %d, Queued: %d, Denied: %d",
        queryPermits.availablePermits(), queuedQueries.get(), deniedQueries.get());
  }

  /** Reset statistics counters. */
  public void resetStats() {
    deniedQueries.set(0);
  }
}
