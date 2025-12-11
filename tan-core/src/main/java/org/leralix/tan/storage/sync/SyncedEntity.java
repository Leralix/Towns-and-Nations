package org.leralix.tan.storage.sync;

import java.io.Serializable;

/**
 * Interface for entities that require synchronization tracking across MySQL and Redis.
 * Provides optimistic locking via version numbers and timestamp tracking.
 * 
 * <p>This interface enables:
 * <ul>
 *   <li>Conflict detection for concurrent modifications</li>
 *   <li>Stale data identification (comparing lastModified timestamps)</li>
 *   <li>Reconciliation between MySQL (source of truth) and Redis (cache)</li>
 * </ul>
 * 
 * <p>Implementations MUST:
 * <ul>
 *   <li>Call {@link #touch()} before persisting changes</li>
 *   <li>Store version and lastModified in serialized form</li>
 *   <li>Handle {@link StaleDataException} when version mismatch detected</li>
 * </ul>
 * 
 * @since 0.18.0
 * @see org.leralix.tan.storage.stored.DatabaseStorage
 * @see org.leralix.tan.redis.QueryCacheManager
 */
public interface SyncedEntity extends Serializable {

  /**
   * Gets the entity's version number for optimistic locking.
   * Incremented on each modification via {@link #touch()}.
   *
   * @return version number (starts at 0, increments on each write)
   */
  int getVersion();

  /**
   * Sets the entity's version number.
   * <p><b>WARNING:</b> Should only be called during deserialization.
   * Use {@link #touch()} for normal updates.
   *
   * @param version new version number
   */
  void setVersion(int version);

  /**
   * Gets the last modification timestamp in milliseconds since epoch.
   * Used for reconciliation and stale data detection.
   *
   * @return Unix timestamp (milliseconds)
   */
  long getLastModified();

  /**
   * Sets the last modification timestamp.
   * <p><b>WARNING:</b> Should only be called during deserialization.
   * Use {@link #touch()} for normal updates.
   *
   * @param timestamp Unix timestamp (milliseconds)
   */
  void setLastModified(long timestamp);

  /**
   * Updates version and lastModified timestamp.
   * <p><b>CRITICAL:</b> MUST be called before every write operation to:
   * <ul>
   *   <li>Increment version (enables conflict detection)</li>
   *   <li>Update lastModified (enables stale data detection)</li>
   * </ul>
   *
   * <p>Example usage:
   * <pre>{@code
   * public void setBalance(int newBalance) {
   *     this.balance = newBalance;
   *     touch(); // ← REQUIRED before save
   * }
   * }</pre>
   */
  default void touch() {
    setVersion(getVersion() + 1);
    setLastModified(System.currentTimeMillis());
  }

  /**
   * Checks if this entity is newer than another based on lastModified timestamp.
   * Used during reconciliation to determine which copy to keep.
   *
   * @param other entity to compare against
   * @return true if this entity was modified more recently
   */
  default boolean isNewerThan(SyncedEntity other) {
    if (other == null) {
      return true;
    }
    return this.getLastModified() > other.getLastModified();
  }

  /**
   * Checks if this entity has a version conflict with another.
   * Indicates concurrent modifications that require manual resolution.
   *
   * @param other entity to compare against
   * @return true if versions diverged (concurrent writes detected)
   */
  default boolean hasVersionConflict(SyncedEntity other) {
    if (other == null) {
      return false;
    }
    // Different versions but same timestamp = conflict
    return this.getVersion() != other.getVersion()
        && Math.abs(this.getLastModified() - other.getLastModified()) < 1000; // 1sec window
  }

  /**
   * Gets unique identifier for this entity.
   * Used for cache keys and synchronization.
   *
   * @return unique ID (UUID, townId, etc.)
   */
  String getEntityId();

  /**
   * Exception thrown when attempting to overwrite newer data with older data.
   * Indicates a synchronization conflict requiring reconciliation.
   */
  class StaleDataException extends RuntimeException {
    private final int localVersion;
    private final int remoteVersion;
    private final long localTimestamp;
    private final long remoteTimestamp;

    public StaleDataException(
        String entityId,
        int localVersion,
        int remoteVersion,
        long localTimestamp,
        long remoteTimestamp) {
      super(
          String.format(
              "Stale data conflict for %s: local (v%d @ %d) vs remote (v%d @ %d)",
              entityId, localVersion, localTimestamp, remoteVersion, remoteTimestamp));
      this.localVersion = localVersion;
      this.remoteVersion = remoteVersion;
      this.localTimestamp = localTimestamp;
      this.remoteTimestamp = remoteTimestamp;
    }

    public int getLocalVersion() {
      return localVersion;
    }

    public int getRemoteVersion() {
      return remoteVersion;
    }

    public long getLocalTimestamp() {
      return localTimestamp;
    }

    public long getRemoteTimestamp() {
      return remoteTimestamp;
    }
  }

  /**
   * Policy for resolving conflicts between MySQL and Redis versions.
   */
  enum ConflictResolutionPolicy {
    /** Always prefer MySQL (source of truth) */
    PREFER_MYSQL,
    /** Always prefer Redis (cache) */
    PREFER_REDIS,
    /** Prefer newest based on lastModified timestamp */
    PREFER_NEWEST,
    /** Throw exception and require manual resolution */
    MANUAL,
    /** Last writer wins (default Java behavior) */
    LAST_WRITE_WINS
  }
}
