package org.leralix.tan.listeners.interact;


import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;

import java.util.function.Consumer;

public abstract class RightClickListenerEvent {

    public abstract boolean execute(PlayerInteractEvent event);

    protected void openGui(Consumer<Player> playerConsumer, Player player){
        new BukkitRunnable() {
            @Override
            public void run() {
                playerConsumer.accept(player);
            }
        }.runTask(TownsAndNations.getPlugin());
    }
}
