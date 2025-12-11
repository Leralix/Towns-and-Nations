package org.leralix.tan.exception;

public class EconomyException extends TanException {

  public EconomyException(String message) {
    super(message);
  }

  public EconomyException(String message, Throwable cause) {
    super(message, cause);
  }
}
