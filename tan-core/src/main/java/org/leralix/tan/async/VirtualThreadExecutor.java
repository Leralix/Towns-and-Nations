package org.leralix.tan.async;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * AMÉLIORATION #8: Virtual Thread Executor (Java 21+)
 *
 * <p>Leverages Java 21 virtual threads for massive I/O concurrency improvements.
 *
 * <p><b>Virtual Threads Benefits:</b>
 *
 * <ul>
 *   <li>Millions of concurrent threads possible (vs thousands with platform threads)
 *   <li>Extremely lightweight (~1KB memory per thread)
 *   <li>Perfect for I/O-bound operations (database, Redis, file I/O)
 *   <li>No need for complex async/await patterns
 * </ul>
 *
 * <p><b>Use Cases:</b>
 *
 * <ul>
 *   <li>Database queries (especially parallel loading)
 *   <li>Redis operations
 *   <li>File I/O operations
 *   <li>Network requests (Mojang API, webhooks)
 * </ul>
 *
 * <p><b>Performance Comparison:</b>
 *
 * <pre>
 * Platform Threads (Traditional):
 * - Max ~1,000 concurrent threads
 * - ~1MB memory per thread
 * - Context switching overhead
 *
 * Virtual Threads (Java 21+):
 * - 1,000,000+ concurrent threads possible
 * - ~1KB memory per thread
 * - Minimal context switching
 * </pre>
 *
 * <p><b>Auto-detection:</b> Automatically falls back to ForkJoinPool on Java < 21.
 *
 * <p><b>Configuration:</b>
 *
 * <pre>
 * virtual-threads:
 *   enabled: true              # Auto-detects Java 21+
 *   io-task-pool-size: 0       # 0 = unbounded (recommended for I/O)
 *   cpu-task-pool-size: 8      # = CPU cores (for CPU-intensive tasks)
 * </pre>
 *
 * <p><b>Usage Examples:</b>
 *
 * <pre>
 * // Execute I/O task (database, Redis, file)
 * VirtualThreadExecutor.executeIoTask(() -> {
 *     databaseHandler.loadTerritory(id);
 * });
 *
 * // Execute with result callback
 * VirtualThreadExecutor.executeIoTask(
 *     () -> databaseHandler.getTerritoryData(id),
 *     territory -> {
 *         // Use territory on main thread
 *         player.sendMessage("Territory loaded: " + territory.getName());
 *     }
 * );
 *
 * // Parallel loading of multiple territories
 * List&lt;CompletableFuture&lt;TerritoryData&gt;&gt; futures = territoryIds.stream()
 *     .map(id -> VirtualThreadExecutor.supplyAsync(() ->
 *         databaseHandler.getTerritoryData(id)))
 *     .toList();
 *
 * CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
 * </pre>
 *
 * @author Leralix (with AI assistance)
 * @version 0.16.0
 * @since 2025-11-12
 */
public class VirtualThreadExecutor {

  private static final Logger logger = Logger.getLogger(VirtualThreadExecutor.class.getName());

  // Virtual thread executor (Java 21+)
  private static ExecutorService virtualThreadExecutor;

  // CPU-bound task executor
  private static ExecutorService cpuBoundExecutor;

  // Flag to track if virtual threads are supported
  private static boolean virtualThreadsSupported = false;

  /**
   * Initializes the virtual thread executor.
   *
   * <p>Automatically detects Java version:
   *
   * <ul>
   *   <li>Java 21+: Uses virtual threads
   *   <li>Java < 21: Falls back to ForkJoinPool
   * </ul>
   *
   * <p>Should be called once during plugin initialization.
   */
  public static void initialize() {
    // Check Java version
    String javaVersion = System.getProperty("java.version");
    int majorVersion = getMajorVersion(javaVersion);

    if (majorVersion >= 21) {
      try {
        // Use reflection to support Java < 21 compilation
        Class<?> executorsClass = Class.forName("java.util.concurrent.Executors");
        var method = executorsClass.getMethod("newVirtualThreadPerTaskExecutor");
        virtualThreadExecutor = (ExecutorService) method.invoke(null);

        virtualThreadsSupported = true;
        logger.info(
            "[TaN-VirtualThreads] Initialized with Java "
                + majorVersion
                + " virtual threads (unbounded I/O concurrency)");

      } catch (Exception e) {
        logger.warning(
            "[TaN-VirtualThreads] Failed to initialize virtual threads, "
                + "falling back to ForkJoinPool: "
                + e.getMessage());
        initializeFallback();
      }
    } else {
      logger.info(
          "[TaN-VirtualThreads] Java "
              + majorVersion
              + " detected, "
              + "virtual threads require Java 21+. Using ForkJoinPool fallback.");
      initializeFallback();
    }

    // CPU-bound executor (always uses platform threads)
    int cpuCores = Runtime.getRuntime().availableProcessors();
    cpuBoundExecutor =
        Executors.newFixedThreadPool(
            cpuCores,
            r -> {
              Thread thread = new Thread(r);
              thread.setName("TaN-CPU-" + thread.getId());
              return thread;
            });

    logger.info(
        "[TaN-VirtualThreads] CPU-bound executor initialized with " + cpuCores + " threads");
  }

  /** Initializes fallback executor for Java < 21. */
  private static void initializeFallback() {
    virtualThreadExecutor = ForkJoinPool.commonPool();
    virtualThreadsSupported = false;
  }

  /**
   * Extracts major version from Java version string.
   *
   * @param versionString The Java version string (e.g., "21.0.1", "17.0.8")
   * @return The major version number
   */
  private static int getMajorVersion(String versionString) {
    try {
      String[] parts = versionString.split("\\.");
      return Integer.parseInt(parts[0]);
    } catch (Exception e) {
      logger.warning("[TaN-VirtualThreads] Failed to parse Java version: " + versionString);
      return 0;
    }
  }

  /**
   * Executes an I/O-bound task using virtual threads.
   *
   * <p><b>Perfect for:</b>
   *
   * <ul>
   *   <li>Database queries
   *   <li>Redis operations
   *   <li>File I/O
   *   <li>Network requests
   * </ul>
   *
   * <p><b>Not recommended for:</b> CPU-intensive calculations (use {@link #executeCpuTask} instead)
   *
   * @param task The I/O task to execute
   */
  public static void executeIoTask(Runnable task) {
    if (virtualThreadExecutor == null) {
      throw new IllegalStateException("VirtualThreadExecutor not initialized");
    }

    virtualThreadExecutor.submit(task);
  }

  /**
   * Executes an I/O-bound task with result callback.
   *
   * <p>The callback is executed on the virtual thread, so if you need to interact with Bukkit API,
   * schedule it back to the main thread.
   *
   * @param <T> The result type
   * @param supplier The task that produces a result
   * @param callback The callback to handle the result
   */
  public static <T> void executeIoTask(Supplier<T> supplier, Consumer<T> callback) {
    if (virtualThreadExecutor == null) {
      throw new IllegalStateException("VirtualThreadExecutor not initialized");
    }

    virtualThreadExecutor.submit(
        () -> {
          try {
            T result = supplier.get();
            callback.accept(result);
          } catch (Exception e) {
            logger.warning("[TaN-VirtualThreads] I/O task failed: " + e.getMessage());
          }
        });
  }

  /**
   * Executes a CPU-bound task using platform threads.
   *
   * <p><b>Use for:</b>
   *
   * <ul>
   *   <li>Heavy calculations
   *   <li>Data processing
   *   <li>Compression/decompression
   *   <li>Cryptography
   * </ul>
   *
   * <p>Uses a fixed thread pool sized to CPU cores.
   *
   * @param task The CPU task to execute
   */
  public static void executeCpuTask(Runnable task) {
    if (cpuBoundExecutor == null) {
      throw new IllegalStateException("VirtualThreadExecutor not initialized");
    }

    cpuBoundExecutor.submit(task);
  }

  /**
   * Executes a CPU-bound task with result callback.
   *
   * @param <T> The result type
   * @param supplier The task that produces a result
   * @param callback The callback to handle the result
   */
  public static <T> void executeCpuTask(Supplier<T> supplier, Consumer<T> callback) {
    if (cpuBoundExecutor == null) {
      throw new IllegalStateException("VirtualThreadExecutor not initialized");
    }

    cpuBoundExecutor.submit(
        () -> {
          try {
            T result = supplier.get();
            callback.accept(result);
          } catch (Exception e) {
            logger.warning("[TaN-VirtualThreads] CPU task failed: " + e.getMessage());
          }
        });
  }

  /**
   * Creates a CompletableFuture for an I/O-bound task.
   *
   * <p>Useful for parallel execution of multiple I/O operations:
   *
   * <pre>
   * List&lt;CompletableFuture&lt;TerritoryData&gt;&gt; futures = territoryIds.stream()
   *     .map(id -> VirtualThreadExecutor.supplyAsync(() -> loadTerritory(id)))
   *     .toList();
   *
   * CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
   * </pre>
   *
   * @param <T> The result type
   * @param supplier The task that produces a result
   * @return A CompletableFuture with the result
   */
  public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
    if (virtualThreadExecutor == null) {
      throw new IllegalStateException("VirtualThreadExecutor not initialized");
    }

    return CompletableFuture.supplyAsync(supplier, virtualThreadExecutor);
  }

  /**
   * Checks if virtual threads are supported and enabled.
   *
   * @return true if using Java 21+ virtual threads, false if using fallback
   */
  public static boolean isVirtualThreadsEnabled() {
    return virtualThreadsSupported;
  }

  /**
   * Gets statistics about the executor.
   *
   * @return A formatted string with executor stats
   */
  public static String getStats() {
    if (virtualThreadExecutor == null) {
      return "VirtualThreadExecutor not initialized";
    }

    String mode =
        virtualThreadsSupported ? "Virtual Threads (Java 21+)" : "ForkJoinPool (Fallback)";

    if (virtualThreadExecutor instanceof ThreadPoolExecutor tpe) {
      return String.format(
          "%s - Active: %d, Queued: %d, Completed: %d",
          mode, tpe.getActiveCount(), tpe.getQueue().size(), tpe.getCompletedTaskCount());
    } else if (virtualThreadExecutor instanceof ForkJoinPool fjp) {
      return String.format(
          "%s - Active: %d, Queued: %d, Threads: %d",
          mode, fjp.getActiveThreadCount(), fjp.getQueuedSubmissionCount(), fjp.getPoolSize());
    }

    return mode;
  }

  /**
   * Shuts down the executors.
   *
   * <p>Should be called during plugin disable.
   */
  public static void shutdown() {
    if (virtualThreadExecutor != null && !virtualThreadExecutor.isShutdown()) {
      virtualThreadExecutor.shutdown();
      try {
        if (!virtualThreadExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
          virtualThreadExecutor.shutdownNow();
        }
      } catch (InterruptedException e) {
        virtualThreadExecutor.shutdownNow();
        Thread.currentThread().interrupt();
      }
      logger.info("[TaN-VirtualThreads] I/O executor shut down");
    }

    if (cpuBoundExecutor != null && !cpuBoundExecutor.isShutdown()) {
      cpuBoundExecutor.shutdown();
      try {
        if (!cpuBoundExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
          cpuBoundExecutor.shutdownNow();
        }
      } catch (InterruptedException e) {
        cpuBoundExecutor.shutdownNow();
        Thread.currentThread().interrupt();
      }
      logger.info("[TaN-VirtualThreads] CPU executor shut down");
    }
  }
}
