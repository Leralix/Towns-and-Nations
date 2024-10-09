package org.leralix.tan.listeners.ChatListener.Events;

import org.bukkit.entity.Player;
import org.leralix.tan.listeners.ChatListener.ChatListenerEvent;
import org.leralix.tan.storage.DataStorage.RegionDataStorage;

public class CreateRegion extends ChatListenerEvent {

    @Override
    public void execute(Player player, String message) {
        RegionDataStorage.createNewRegion(player, message);
    }
}
