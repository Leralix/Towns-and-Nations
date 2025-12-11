package org.leralix.tan.storage.database;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryBatchExecutor {

  private static final Logger LOGGER = LoggerFactory.getLogger(QueryBatchExecutor.class);

  private final Queue<BatchedQuery<?>> batchQueue = new LinkedList<>();
  private final int BATCH_SIZE;
  private final int BATCH_DELAY_MS;
  private final ScheduledExecutorService scheduler;
  private final AtomicBoolean flushScheduled = new AtomicBoolean(false);

  public QueryBatchExecutor(int batchSize, int delayMs) {
    this.BATCH_SIZE = batchSize;
    this.BATCH_DELAY_MS = delayMs;
    this.scheduler =
        Executors.newScheduledThreadPool(
            1,
            r -> {
              Thread t = new Thread(r, "TAN-QueryBatchExecutor");
              t.setDaemon(true);
              return t;
            });
  }

  public <T> CompletableFuture<T> queueQuery(Query<T> query) {
    CompletableFuture<T> future = new CompletableFuture<>();
    BatchedQuery<T> batchedQuery = new BatchedQuery<>(query, future);

    synchronized (batchQueue) {
      batchQueue.offer(batchedQuery);

      if (batchQueue.size() >= BATCH_SIZE) {
        flushBatch();
      } else if (batchQueue.size() == 1) {
        scheduleBatchFlush();
      }
    }

    return future;
  }

  private void flushBatch() {
    List<BatchedQuery<?>> batch = new ArrayList<>();
    synchronized (batchQueue) {
      while (!batchQueue.isEmpty() && batch.size() < BATCH_SIZE) {
        batch.add(batchQueue.poll());
      }
      flushScheduled.set(false);
    }

    if (!batch.isEmpty()) {
      executeBatch(batch);
    }
  }

  private void scheduleBatchFlush() {
    if (flushScheduled.compareAndSet(false, true)) {
      scheduler.schedule(this::flushBatch, BATCH_DELAY_MS, TimeUnit.MILLISECONDS);
    }
  }

  private void executeBatch(List<BatchedQuery<?>> batch) {
    long startTime = System.currentTimeMillis();

    try {
      for (BatchedQuery<?> batchedQuery : batch) {
        try {
          Object result = batchedQuery.query.execute();
          batchedQuery.future.complete((Object) result);
        } catch (Exception ex) {
          batchedQuery.future.completeExceptionally(ex);
        }
      }

      long duration = System.currentTimeMillis() - startTime;
      if (batch.size() > 1) {
        LOGGER.debug("Batch executed: " + batch.size() + " queries in " + duration + "ms");
      }
    } catch (Exception ex) {
      LOGGER.error("Error executing batch of " + batch.size() + " queries", ex);
      for (BatchedQuery<?> batchedQuery : batch) {
        batchedQuery.future.completeExceptionally(ex);
      }
    }
  }

  public void shutdown() {
    scheduler.shutdown();
    try {
      if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
        scheduler.shutdownNow();
      }
    } catch (InterruptedException ex) {
      scheduler.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  public interface Query<T> {
    T execute() throws Exception;
  }

  private static class BatchedQuery<T> {
    Query<T> query;
    CompletableFuture<Object> future;

    @SuppressWarnings("unchecked")
    BatchedQuery(Query<T> query, CompletableFuture<T> future) {
      this.query = query;
      this.future = (CompletableFuture<Object>) (Object) future;
    }
  }
}
