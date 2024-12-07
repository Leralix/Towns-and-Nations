package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;

import static org.leralix.tan.utils.ChatUtils.getTANString;

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
