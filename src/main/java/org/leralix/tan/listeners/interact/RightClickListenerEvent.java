package org.leralix.tan.listeners.interact;


import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.utils.FoliaScheduler;

import java.util.function.Consumer;

public abstract class RightClickListenerEvent {

    public abstract ListenerState execute(PlayerInteractEvent event);

    protected void openGui(Consumer<Player> playerConsumer, Player player){
        FoliaScheduler.runTask(TownsAndNations.getPlugin(), () -> playerConsumer.accept(player));
    }
}
