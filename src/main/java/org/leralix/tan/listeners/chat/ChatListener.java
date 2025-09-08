package org.leralix.tan.listeners.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.interact.RightClickListener;
import org.leralix.tan.utils.text.TanChatUtils;

public class ChatListener implements Listener {


    @EventHandler(priority = EventPriority.LOW)
    public void checkForCancelWord(AsyncPlayerChatEvent event) {

        String message = event.getMessage();
        if (message.equalsIgnoreCase(Lang.CANCEL_WORD.get())) {

            Player player = event.getPlayer();
            player.sendMessage(TanChatUtils.getTANString() + Lang.CANCELLED_ACTION.get());
            RightClickListener.removePlayer(player);
            PlayerChatListenerStorage.removePlayer(player);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();

        if(PlayerChatListenerStorage.contains(player)){
            event.setCancelled(true);
            PlayerChatListenerStorage.execute(player, event.getMessage());
        }
    }

}


