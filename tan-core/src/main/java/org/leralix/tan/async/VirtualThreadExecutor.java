package org.leralix.tan.async;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class VirtualThreadExecutor {

  private static final Logger logger = Logger.getLogger(VirtualThreadExecutor.class.getName());

  private static ExecutorService virtualThreadExecutor;

  private static ExecutorService cpuBoundExecutor;

  private static boolean virtualThreadsSupported = false;

  public static void initialize() {
    String javaVersion = System.getProperty("java.version");
    int majorVersion = getMajorVersion(javaVersion);

    if (majorVersion >= 21) {
      try {
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

  private static void initializeFallback() {
    virtualThreadExecutor = ForkJoinPool.commonPool();
    virtualThreadsSupported = false;
  }

  private static int getMajorVersion(String versionString) {
    try {
      String[] parts = versionString.split("\\.");
      return Integer.parseInt(parts[0]);
    } catch (Exception e) {
      logger.warning("[TaN-VirtualThreads] Failed to parse Java version: " + versionString);
      return 0;
    }
  }

  public static void executeIoTask(Runnable task) {
    if (virtualThreadExecutor == null) {
      throw new IllegalStateException("VirtualThreadExecutor not initialized");
    }

    virtualThreadExecutor.submit(task);
  }

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

  public static void executeCpuTask(Runnable task) {
    if (cpuBoundExecutor == null) {
      throw new IllegalStateException("VirtualThreadExecutor not initialized");
    }

    cpuBoundExecutor.submit(task);
  }

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

  public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
    if (virtualThreadExecutor == null) {
      throw new IllegalStateException("VirtualThreadExecutor not initialized");
    }

    return CompletableFuture.supplyAsync(supplier, virtualThreadExecutor);
  }

  public static boolean isVirtualThreadsEnabled() {
    return virtualThreadsSupported;
  }

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
