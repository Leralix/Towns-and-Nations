package org.leralix.tan.exception;

/**
 * Exception thrown when a storage operation fails.
 *
 * <p>This includes database operations, file I/O, caching errors, etc.
 */
public class StorageException extends TanException {

  public StorageException(String message) {
    super(message);
  }

  public StorageException(String message, Throwable cause) {
    super(message, cause);
  }

  public StorageException(Throwable cause) {
    super(cause);
  }
}
