package org.leralix.tan.listeners.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.interact.RightClickListener;
import org.leralix.tan.utils.text.TanChatUtils;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        ChatListenerEvent chatListenerEvent = PlayerChatListenerStorage.getPlayer(player);

        String message = event.getMessage();

        if(chatListenerEvent == null)
            return;

        event.setCancelled(true);

        if (message.equalsIgnoreCase(Lang.CANCEL_WORD.get())) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.CANCELLED_ACTION.get());
            RightClickListener.removePlayer(player);
            PlayerChatListenerStorage.removePlayer(player);
            return;
        }

        chatListenerEvent.execute(player, message);
    }

}


