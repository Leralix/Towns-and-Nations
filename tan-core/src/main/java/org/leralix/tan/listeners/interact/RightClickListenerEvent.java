package org.leralix.tan.listeners.interact;

import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.utils.FoliaScheduler;

public abstract class RightClickListenerEvent {

  public abstract ListenerState execute(PlayerInteractEvent event);

  protected void openGui(Consumer<Player> playerConsumer, Player player) {
    FoliaScheduler.runTask(TownsAndNations.getPlugin(), () -> playerConsumer.accept(player));
  }
}
