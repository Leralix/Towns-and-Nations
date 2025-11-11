package org.leralix.tan.exception;

/**
 * Exception thrown when a territory operation fails.
 *
 * <p>This includes town/region creation, deletion, claim operations, etc.
 */
public class TerritoryException extends TanException {

  public TerritoryException(String message) {
    super(message);
  }

  public TerritoryException(String message, Throwable cause) {
    super(message, cause);
  }
}
