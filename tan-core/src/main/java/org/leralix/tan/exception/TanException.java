package org.leralix.tan.exception;

/**
 * Base exception for all Towns and Nations plugin exceptions.
 *
 * <p>This is the root of the custom exception hierarchy for the plugin. All plugin-specific
 * exceptions should extend this class.
 */
public class TanException extends Exception {

  public TanException(String message) {
    super(message);
  }

  public TanException(String message, Throwable cause) {
    super(message, cause);
  }

  public TanException(Throwable cause) {
    super(cause);
  }
}
