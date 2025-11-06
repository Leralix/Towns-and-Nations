package org.leralix.tan.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.tan.api.interfaces.TanTown;

/** Called when a player creates a town. */
public class TownCreateEvent extends Event implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();
  private boolean isCancelled;

  private final Player player;
  private final TanTown town;

  public TownCreateEvent(Player player, TanTown town) {
    this.player = player;
    this.town = town;
  }

  public Player getPlayer() {
    return player;
  }

  public TanTown getTown() {
    return town;
  }

  @Override
  public boolean isCancelled() {
    return isCancelled;
  }

  @Override
  public void setCancelled(boolean cancel) {
    isCancelled = cancel;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}
