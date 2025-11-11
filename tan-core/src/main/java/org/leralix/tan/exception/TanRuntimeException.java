package org.leralix.tan.exception;

/**
 * Base unchecked exception for all Towns and Nations plugin runtime exceptions.
 *
 * <p>Use this for exceptional situations that are not expected to be recovered from in normal
 * operation.
 */
public class TanRuntimeException extends RuntimeException {

  public TanRuntimeException(String message) {
    super(message);
  }

  public TanRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public TanRuntimeException(Throwable cause) {
    super(cause);
  }
}
