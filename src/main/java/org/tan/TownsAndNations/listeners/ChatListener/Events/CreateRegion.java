package org.tan.TownsAndNations.listeners.ChatListener.Events;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.listeners.ChatListener.ChatListenerEvent;
import org.tan.TownsAndNations.storage.DataStorage.RegionDataStorage;

public class CreateRegion extends ChatListenerEvent {

    @Override
    public void execute(Player player, String message) {
        RegionDataStorage.createNewRegion(player, message);
    }
}
