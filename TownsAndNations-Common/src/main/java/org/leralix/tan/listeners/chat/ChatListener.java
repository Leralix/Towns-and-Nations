package org.leralix.tan.listeners.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.interact.RightClickListener;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

public class ChatListener implements Listener {

    private final PlayerDataStorage playerDataStorage;

    public ChatListener (PlayerDataStorage playerDataStorage) {
        this.playerDataStorage = playerDataStorage;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void checkForCancelWord(AsyncPlayerChatEvent event) {

        String message = event.getMessage();
        Player player = event.getPlayer();
        ITanPlayer playerData = playerDataStorage.get(player);
        if (message.equalsIgnoreCase(Lang.CANCEL_WORD.get(playerData))) {
            TanChatUtils.message(player, Lang.CANCELLED_ACTION.get(playerData));
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
            ITanPlayer playerData = playerDataStorage.get(player);
            PlayerChatListenerStorage.execute(player, playerData, event.getMessage());
        }
    }

}


