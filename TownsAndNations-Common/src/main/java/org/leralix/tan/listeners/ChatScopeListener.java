package org.leralix.tan.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.storage.LocalChatStorage;

public class ChatScopeListener implements Listener {

    private final LocalChatStorage localChatStorage;

    public ChatScopeListener(
            LocalChatStorage localChatStorage
    ) {
        this.localChatStorage = localChatStorage;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event){

        Player player = event.getPlayer();
        String playerUUID = player.getUniqueId().toString();

        //If player has better commands to do
        if(PlayerChatListenerStorage.contains(player))
            return;

        if(!localChatStorage.isPlayerInChatScope(playerUUID))
            return;


        localChatStorage.broadcastInScope(player, event.getMessage());
        event.setCancelled(true);
    }
}
