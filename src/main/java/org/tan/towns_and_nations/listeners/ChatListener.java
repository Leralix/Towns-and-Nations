package org.tan.towns_and_nations.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.utils.PlayerChatListenerStorage;
import org.tan.towns_and_nations.utils.PlayerStatStorage;
import org.tan.towns_and_nations.utils.TownDataStorage;

public class ChatListener implements Listener {

    @EventHandler
    public void OnPlayerChat(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        if(PlayerChatListenerStorage.checkIfPlayerIn(player.getUniqueId())){
            Bukkit.broadcastMessage(player.getName() + " has created his city: " + event.getMessage());

            PlayerChatListenerStorage.removePlayer(player);
            PlayerDataClass sender = PlayerStatStorage.findStatUUID(player.getUniqueId().toString());
            sender.removeFromBalance(100);

            TownDataClass newTown = new TownDataClass(player.getUniqueId().toString(),event.getMessage());
            TownDataStorage.addTown(newTown);

            event.setCancelled(true);
        }
    }


}
