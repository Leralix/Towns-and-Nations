package org.leralix.tan.exception;

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
