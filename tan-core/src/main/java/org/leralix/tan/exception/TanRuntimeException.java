package org.leralix.tan.exception;

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
