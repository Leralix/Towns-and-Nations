package org.leralix.tan.storage.sync;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Base implementation of {@link SyncedEntity} providing version tracking and timestamps.
 * Extend this class for entities requiring MySQL/Redis synchronization with optimistic locking.
 *
 * <p><b>Usage Example:</b>
 * <pre>{@code
 * public class TownData extends BaseSyncedEntity {
 *     private int balance;
 *
 *     public void setBalance(int newBalance) {
 *         this.balance = newBalance;
 *         touch(); // ← Updates version and timestamp
 *     }
 *
 *     @Override
 *     public String getEntityId() {
 *         return this.townId;
 *     }
 * }
 * }</pre>
 *
 * @since 0.18.0
 * @see SyncedEntity
 */
public abstract class BaseSyncedEntity implements SyncedEntity {

  /** Serialization version UID for compatibility */
  private static final long serialVersionUID = 1L;

  /**
   * Optimistic locking version number.
   * Incremented on each modification to detect concurrent writes.
   */
  @Expose
  @SerializedName("_version")
  private int version = 0;

  /**
   * Last modification timestamp in milliseconds since epoch.
   * Used for conflict resolution (prefer newest) and stale data detection.
   */
  @Expose
  @SerializedName("_lastModified")
  private long lastModified = System.currentTimeMillis();

  @Override
  public int getVersion() {
    return version;
  }

  @Override
  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public long getLastModified() {
    return lastModified;
  }

  @Override
  public void setLastModified(long timestamp) {
    this.lastModified = timestamp;
  }

  /**
   * Checks if this entity is significantly stale compared to another.
   * "Significantly stale" means older than threshold (default: 5 seconds).
   *
   * @param other entity to compare against
   * @param thresholdMs staleness threshold in milliseconds
   * @return true if this entity is stale beyond threshold
   */
  public boolean isStale(SyncedEntity other, long thresholdMs) {
    if (other == null) {
      return false;
    }
    long ageDiff = other.getLastModified() - this.getLastModified();
    return ageDiff > thresholdMs;
  }

  /**
   * Checks if this entity is significantly stale (5+ seconds older).
   *
   * @param other entity to compare against
   * @return true if this entity is 5+ seconds older
   */
  public boolean isStale(SyncedEntity other) {
    return isStale(other, 5000); // 5 second threshold
  }

  /**
   * Creates a snapshot of version/timestamp for conflict detection.
   * Use this before starting a long-running operation to detect if data changed.
   *
   * @return snapshot of current version/timestamp
   */
  public Snapshot createSnapshot() {
    return new Snapshot(version, lastModified);
  }

  /**
   * Validates that entity hasn't changed since snapshot was taken.
   * Throws {@link StaleDataException} if version mismatch detected.
   *
   * @param snapshot previously taken snapshot
   * @throws StaleDataException if entity was modified after snapshot
   */
  public void validateSnapshot(Snapshot snapshot) {
    if (this.version != snapshot.version) {
      throw new StaleDataException(
          getEntityId(), snapshot.version, this.version, snapshot.timestamp, this.lastModified);
    }
  }

  /**
   * Immutable snapshot of entity version and timestamp.
   * Used for optimistic locking in long-running transactions.
   */
  public static class Snapshot {
    private final int version;
    private final long timestamp;

    public Snapshot(int version, long timestamp) {
      this.version = version;
      this.timestamp = timestamp;
    }

    public int getVersion() {
      return version;
    }

    public long getTimestamp() {
      return timestamp;
    }
  }

  @Override
  public String toString() {
    return String.format(
        "%s{id=%s, version=%d, lastModified=%d}",
        getClass().getSimpleName(), getEntityId(), version, lastModified);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof SyncedEntity)) return false;
    SyncedEntity other = (SyncedEntity) obj;
    return getEntityId().equals(other.getEntityId());
  }

  @Override
  public int hashCode() {
    return getEntityId().hashCode();
  }
}
