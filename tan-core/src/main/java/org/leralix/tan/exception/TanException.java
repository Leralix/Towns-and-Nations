package org.leralix.tan.exception;

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
