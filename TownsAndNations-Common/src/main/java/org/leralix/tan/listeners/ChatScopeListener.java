package org.leralix.tan.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.storage.LocalChatStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class ChatScopeListener implements Listener {

    PlayerDataStorage playerDataStorage;

    public ChatScopeListener(PlayerDataStorage playerDataStorage) {
        this.playerDataStorage = playerDataStorage;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event){

        Player player = event.getPlayer();
        String playerUUID = player.getUniqueId().toString();

        //If player has better commands to do
        if(PlayerChatListenerStorage.contains(player))
            return;

        if(!LocalChatStorage.isPlayerInChatScope(playerUUID))
            return;


        LocalChatStorage.broadcastInScope(player, event.getMessage());
        event.setCancelled(true);
    }
}
