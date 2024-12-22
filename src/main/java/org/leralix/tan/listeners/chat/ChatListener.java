package org.leralix.tan.listeners.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.PlayerSelectPropertyPositionStorage;
import org.leralix.tan.utils.ChatUtils;

import static org.leralix.tan.listeners.chat.PlayerChatListenerStorage.removePlayer;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        ChatListenerEvent chatListenerEvent = PlayerChatListenerStorage.getPlayer(player);

        if(chatListenerEvent == null)
            return;

        String message = event.getMessage();
        event.setCancelled(true);

        if (message.equalsIgnoreCase(Lang.CANCEL_WORD.get())) {
            player.sendMessage(ChatUtils.getTANString() + Lang.CANCELLED_ACTION.get());
            PlayerSelectPropertyPositionStorage.removePlayer(player);
            removePlayer(player);
            return;
        }

        chatListenerEvent.execute(player, message);
    }

}


