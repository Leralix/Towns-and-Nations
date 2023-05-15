package org.tan.towns_and_nations.listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.tan.towns_and_nations.PlayerData.PlayerDataClass;
import org.tan.towns_and_nations.TownsAndNations;
import org.tan.towns_and_nations.utils.PlayerStatStorage;

import java.io.IOException;


public class OnPlayerFirstJoin implements Listener {

    TownsAndNations PluginInstance;

    @EventHandler
    public void onPlayerFirstJoin(PlayerJoinEvent event) throws IOException {
        Player player = event.getPlayer();

        if (PlayerStatStorage.findStatUUID(player.getUniqueId().toString()) == null) {

            PlayerStatStorage.createPlayerDataClass(player);

            PluginInstance = TownsAndNations.getPlugin();
            PluginInstance.getServer().broadcastMessage(player.getName() + " a rejoint le serveur pour la premiere fois");
        }
        else{
            System.out.println("Player already joined.");
        }

    }
}
