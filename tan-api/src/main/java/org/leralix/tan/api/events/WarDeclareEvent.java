package org.leralix.tan.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.tan.api.interfaces.TanTown;

/** Called when a town declares war on another town. */
public class WarDeclareEvent extends Event implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();
  private boolean isCancelled;

  private final TanTown declarer;
  private final TanTown declared;

  public WarDeclareEvent(TanTown declarer, TanTown declared) {
    this.declarer = declarer;
    this.declared = declared;
  }

  public TanTown getDeclarer() {
    return declarer;
  }

  public TanTown getDeclared() {
    return declared;
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
