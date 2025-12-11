package org.leralix.tan.storage.exceptions;

public class PlayerNotFoundException extends RuntimeException {

  private final String playerId;

  public PlayerNotFoundException(String playerId) {
    super("Player with ID " + playerId + " not found in database");
    this.playerId = playerId;
  }

  public PlayerNotFoundException(String playerId, String message) {
    super(message);
    this.playerId = playerId;
  }

  public String getPlayerId() {
    return playerId;
  }
}
