package org.tan.TownsAndNations.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.tan.TownsAndNations.enums.ChatScope;
import org.tan.TownsAndNations.storage.LocalChatStorage;
import org.tan.TownsAndNations.storage.PlayerChatListenerStorage;

public class ChatScopeListener implements Listener {

    @EventHandler
    public void OnPlayerChat(AsyncPlayerChatEvent event){



        Player player = event.getPlayer();
        String playerUUID = player.getUniqueId().toString();

        //If player has better commands to do
        if(PlayerChatListenerStorage.getPlayerData(playerUUID) != null)
            return;

        if(!LocalChatStorage.isPlayerInChatScope(playerUUID))
            return;


        ChatScope scope = LocalChatStorage.getPlayerChatScope(playerUUID);

        if(scope == ChatScope.CITY){
            LocalChatStorage.broadcastInTownScope(player, event.getMessage());
            event.setCancelled(true);
            return;
        }
        if(scope == ChatScope.ALLIANCE){

        }



        event.setCancelled(true);



    }



}
