package org.leralix.tan.storage.exceptions;

/**
 * Exception thrown when the database connection is not ready (null, closed, or temporarily
 * unavailable). This is a recoverable error that should trigger a retry mechanism.
 */
public class DatabaseNotReadyException extends RuntimeException {

  public DatabaseNotReadyException(String message) {
    super(message);
  }

  public DatabaseNotReadyException(String message, Throwable cause) {
    super(message, cause);
  }
}
