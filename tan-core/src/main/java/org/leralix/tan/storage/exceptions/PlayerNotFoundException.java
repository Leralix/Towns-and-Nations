package org.leralix.tan.storage.exceptions;

/**
 * Exception thrown when a player is not found in the database. This indicates the player is truly
 * new and has never been registered before. This is NOT a recoverable error - a new player profile
 * should be created.
 */
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
