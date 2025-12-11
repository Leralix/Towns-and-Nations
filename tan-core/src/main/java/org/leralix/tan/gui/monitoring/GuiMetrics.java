package org.leralix.tan.gui.monitoring;

import java.util.concurrent.atomic.AtomicLong;

public class GuiMetrics {
  private final AtomicLong totalOpens = new AtomicLong(0);
  private final AtomicLong totalErrors = new AtomicLong(0);
  private final AtomicLong totalTimeNanos = new AtomicLong(0);
  private final AtomicLong minTimeNanos = new AtomicLong(Long.MAX_VALUE);
  private final AtomicLong maxTimeNanos = new AtomicLong(0);
  private final AtomicLong cacheHits = new AtomicLong(0);
  private final AtomicLong cacheMisses = new AtomicLong(0);

  void recordOpen(long durationNanos) {
    totalOpens.incrementAndGet();
    totalTimeNanos.addAndGet(durationNanos);

    long currentMin;
    do {
      currentMin = minTimeNanos.get();
      if (durationNanos >= currentMin) break;
    } while (!minTimeNanos.compareAndSet(currentMin, durationNanos));

    long currentMax;
    do {
      currentMax = maxTimeNanos.get();
      if (durationNanos <= currentMax) break;
    } while (!maxTimeNanos.compareAndSet(currentMax, durationNanos));
  }

  void recordError() {
    totalErrors.incrementAndGet();
  }

  void recordCacheHit() {
    cacheHits.incrementAndGet();
  }

  void recordCacheMiss() {
    cacheMisses.incrementAndGet();
  }

  public long getTotalOpens() {
    return totalOpens.get();
  }

  public long getTotalErrors() {
    return totalErrors.get();
  }

  public double getAverageTimeMs() {
    long opens = totalOpens.get();
    return opens > 0 ? (totalTimeNanos.get() / 1_000_000.0 / opens) : 0.0;
  }

  public double getMinTimeMs() {
    long min = minTimeNanos.get();
    return min == Long.MAX_VALUE ? 0.0 : min / 1_000_000.0;
  }

  public double getMaxTimeMs() {
    return maxTimeNanos.get() / 1_000_000.0;
  }

  public long getCacheHits() {
    return cacheHits.get();
  }

  public long getCacheMisses() {
    return cacheMisses.get();
  }

  public double getCacheHitRate() {
    long hits = cacheHits.get();
    long misses = cacheMisses.get();
    long total = hits + misses;
    return total > 0 ? (hits / (double) total) : 0.0;
  }
}
