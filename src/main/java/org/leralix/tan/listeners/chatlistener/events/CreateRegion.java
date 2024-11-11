package org.leralix.tan.listeners.chatlistener.events;

import org.bukkit.entity.Player;
import org.leralix.tan.listeners.chatlistener.ChatListenerEvent;
import org.leralix.tan.storage.stored.RegionDataStorage;

public class CreateRegion extends ChatListenerEvent {

    @Override
    public void execute(Player player, String message) {
        RegionDataStorage.createNewRegion(player, message);
    }
}
