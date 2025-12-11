package org.leralix.tan.gui.monitoring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.bukkit.entity.Player;

public class GuiPerformanceMonitor {

  private static final GuiPerformanceMonitor INSTANCE = new GuiPerformanceMonitor();

  private final Map<String, GuiMetrics> metricsMap = new ConcurrentHashMap<>();
  private final AtomicLong totalGuiOpens = new AtomicLong(0);
  private final AtomicLong totalErrors = new AtomicLong(0);
  private boolean enabled = true;

  private GuiPerformanceMonitor() {}

  public static GuiPerformanceMonitor getInstance() {
    return INSTANCE;
  }

  public GuiTrackingContext startTracking(Player player, String guiName) {
    if (!enabled) {
      return new GuiTrackingContext(this, guiName, 0, false);
    }
    totalGuiOpens.incrementAndGet();
    return new GuiTrackingContext(this, guiName, System.nanoTime(), true);
  }

  void recordSuccess(String guiName, long startTimeNanos) {
    if (!enabled) return;

    long durationNanos = System.nanoTime() - startTimeNanos;
    metricsMap.computeIfAbsent(guiName, k -> new GuiMetrics()).recordOpen(durationNanos);
  }

  public void recordError(String guiName, Throwable error) {
    if (!enabled) return;

    totalErrors.incrementAndGet();
    metricsMap.computeIfAbsent(guiName, k -> new GuiMetrics()).recordError();
  }

  public void recordCacheHit(String guiName) {
    if (!enabled) return;
    metricsMap.computeIfAbsent(guiName, k -> new GuiMetrics()).recordCacheHit();
  }

  public void recordCacheMiss(String guiName) {
    if (!enabled) return;
    metricsMap.computeIfAbsent(guiName, k -> new GuiMetrics()).recordCacheMiss();
  }

  public GuiMetrics getMetrics(String guiName) {
    return metricsMap.get(guiName);
  }

  public Map<String, GuiMetrics> getAllMetrics() {
    return new ConcurrentHashMap<>(metricsMap);
  }

  public long getTotalGuiOpens() {
    return totalGuiOpens.get();
  }

  public long getTotalErrors() {
    return totalErrors.get();
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void reset() {
    metricsMap.clear();
    totalGuiOpens.set(0);
    totalErrors.set(0);
  }

  public String generateReport() {
    StringBuilder report = new StringBuilder();
    report.append("=== GUI Performance Report ===\n");
    report.append(String.format("Total GUI Opens: %,d\n", totalGuiOpens.get()));
    report.append(String.format("Total Errors: %,d\n", totalErrors.get()));
    report.append(
        String.format(
            "Error Rate: %.2f%%\n",
            totalGuiOpens.get() > 0 ? (totalErrors.get() * 100.0 / totalGuiOpens.get()) : 0.0));
    report.append("\n--- Per-GUI Statistics ---\n");

    metricsMap.entrySet().stream()
        .sorted((a, b) -> Long.compare(b.getValue().getTotalOpens(), a.getValue().getTotalOpens()))
        .forEach(
            entry -> {
              String name = entry.getKey();
              GuiMetrics metrics = entry.getValue();
              report.append(
                  String.format(
                      "\n%s:\n"
                          + "  Opens: %,d | Errors: %,d (%.2f%%)\n"
                          + "  Avg Time: %.2fms | Min: %.2fms | Max: %.2fms\n"
                          + "  Cache: %,d hits / %,d misses (%.2f%% hit rate)\n",
                      name,
                      metrics.getTotalOpens(),
                      metrics.getTotalErrors(),
                      metrics.getTotalOpens() > 0
                          ? (metrics.getTotalErrors() * 100.0 / metrics.getTotalOpens())
                          : 0.0,
                      metrics.getAverageTimeMs(),
                      metrics.getMinTimeMs(),
                      metrics.getMaxTimeMs(),
                      metrics.getCacheHits(),
                      metrics.getCacheMisses(),
                      metrics.getCacheHitRate() * 100.0));
            });

    return report.toString();
  }

  public static class GuiTrackingContext implements AutoCloseable {
    private final GuiPerformanceMonitor monitor;
    private final String guiName;
    private final long startTimeNanos;
    private final boolean enabled;

    GuiTrackingContext(
        GuiPerformanceMonitor monitor, String guiName, long startTimeNanos, boolean enabled) {
      this.monitor = monitor;
      this.guiName = guiName;
      this.startTimeNanos = startTimeNanos;
      this.enabled = enabled;
    }

    @Override
    public void close() {
      if (enabled) {
        monitor.recordSuccess(guiName, startTimeNanos);
      }
    }
  }
}
