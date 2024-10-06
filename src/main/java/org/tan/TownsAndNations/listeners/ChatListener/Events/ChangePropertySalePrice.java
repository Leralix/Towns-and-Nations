package org.tan.TownsAndNations.listeners.ChatListener.Events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.tan.TownsAndNations.DataClass.PropertyData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.listeners.ChatListener.ChatListenerEvent;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import static org.tan.TownsAndNations.enums.MessageKey.PROPERTY_ID;
import static org.tan.TownsAndNations.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class ChangePropertySalePrice extends ChatListenerEvent {
    PropertyData propertyData;
    public ChangePropertySalePrice(@NotNull PropertyData propertyData) {
        this.propertyData = propertyData;
    }

    @Override
    public void execute(Player player, String message) {
        int amount;
        try{
            amount = Integer.parseInt(message);

        } catch (NumberFormatException e) {
            player.sendMessage(getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
            return;
        }
        propertyData.setSalePrice(amount);
    }
}
