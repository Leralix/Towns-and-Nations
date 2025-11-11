package org.leralix.tan.exception;

/**
 * Exception thrown when a player lacks required permissions.
 *
 * <p>This is used for authorization failures in chunk operations, town management, etc.
 */
public class PermissionException extends TanException {

  private final String requiredPermission;

  public PermissionException(String message, String requiredPermission) {
    super(message);
    this.requiredPermission = requiredPermission;
  }

  public String getRequiredPermission() {
    return requiredPermission;
  }
}
