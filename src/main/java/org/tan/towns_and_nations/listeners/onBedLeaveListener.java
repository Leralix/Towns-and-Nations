package org.tan.towns_and_nations.listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class onBedLeaveListener implements Listener {

    @EventHandler
    public void onLeaveBed(PlayerBedEnterEvent event){
        Player player = event.getPlayer();


        if (player.isSleeping()) {
            double currentHealth = player.getHealth();
            double newHealth = Math.min(currentHealth + 4, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
            player.setHealth(newHealth);
            player.sendMessage("Votre santé a été restaurée !");
        }



    }


}
