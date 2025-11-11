package org.leralix.tan.exception;

/**
 * Exception thrown when an economy operation fails.
 *
 * <p>This includes insufficient funds, invalid transactions, tax collection errors, etc.
 */
public class EconomyException extends TanException {

  public EconomyException(String message) {
    super(message);
  }

  public EconomyException(String message, Throwable cause) {
    super(message, cause);
  }
}
