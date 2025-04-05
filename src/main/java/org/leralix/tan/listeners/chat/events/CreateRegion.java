package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;

public class CreateRegion extends ChatListenerEvent {

    @Override
    public void execute(Player player, String message) {
        RegionDataStorage.getInstance().createNewRegion(player, message);
        PlayerChatListenerStorage.removePlayer(player);
    }
}
