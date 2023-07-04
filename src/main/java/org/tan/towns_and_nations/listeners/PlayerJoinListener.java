package org.tan.towns_and_nations.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.tan.towns_and_nations.Lang.Lang;
import org.tan.towns_and_nations.utils.ChatUtils;


public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();


        // get the welcome message translation and send it to the player
        player.sendMessage(ChatUtils.getTANDebugString() + Lang.WELCOME.getTranslation());
    }
}