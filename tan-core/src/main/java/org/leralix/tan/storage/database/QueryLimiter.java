package org.leralix.tan.storage.database;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  public boolean acquirePermit() {
    try {
      int queued = queuedQueries.incrementAndGet();

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

  public void releasePermit() {
    queryPermits.release();
  }

  public String getStats() {
    return String.format(
        "Queries - Available: %d, Queued: %d, Denied: %d",
        queryPermits.availablePermits(), queuedQueries.get(), deniedQueries.get());
  }

  public void resetStats() {
    deniedQueries.set(0);
  }
}
