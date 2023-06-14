package org.tan.towns_and_nations.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.storage.PlayerChatListenerStorage;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;


public class ChatListener implements Listener {

    @EventHandler
    public void OnPlayerChat(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();


        //Listener: Player create his city
        if(PlayerChatListenerStorage.checkIfPlayerIn(player.getUniqueId())){
            String townName = event.getMessage();

            Bukkit.broadcastMessage(ChatColor.GOLD + "[TAN]" + ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " has created his city: " + ChatColor.YELLOW + "" + ChatColor.BOLD + townName);

            PlayerChatListenerStorage.removePlayer(player);
            PlayerDataClass sender = PlayerStatStorage.getStat(player.getUniqueId().toString());
            sender.removeFromBalance(100);
            TownDataStorage.newTown(townName,player);

            event.setCancelled(true);
        }
    }


}
