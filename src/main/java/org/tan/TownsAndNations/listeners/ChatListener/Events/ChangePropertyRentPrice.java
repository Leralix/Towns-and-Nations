package org.tan.TownsAndNations.listeners.ChatListener.Events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.tan.TownsAndNations.DataClass.PropertyData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.listeners.ChatListener.ChatListenerEvent;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import java.util.function.Consumer;

import static org.tan.TownsAndNations.enums.MessageKey.PROPERTY_ID;
import static org.tan.TownsAndNations.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class ChangePropertyRentPrice extends ChatListenerEvent {

    PropertyData propertyData;
    Consumer<Player> guiCallback;

    public ChangePropertyRentPrice(@NotNull PropertyData propertyData, Consumer<Player> guiCallback) {
        this.propertyData = propertyData;
        this.guiCallback = guiCallback;
    }

    @Override
    public void execute(Player player, String message) {
        removePlayer(player);
        int amount;
        try{
            amount = Integer.parseInt(message);

        } catch (NumberFormatException e) {
            player.sendMessage(getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
            return;
        }

        propertyData.setRentPrice(amount);
    }
}
