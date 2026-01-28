package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.function.Consumer;

public class ChangePropertySalePrice extends ChatListenerEvent {

    private final PropertyData propertyData;
    private final Consumer<Player> guiCallback;

    public ChangePropertySalePrice(@NotNull PropertyData propertyData, Consumer<Player> guiCallback) {
        this.propertyData = propertyData;
        this.guiCallback = guiCallback;
    }

    @Override
    public boolean execute(Player player, String message) {
        int amount;
        try {
            amount = Integer.parseInt(message);
            if (amount < 0) {
                amount = 0;
            }
        } catch (NumberFormatException e) {
            TanChatUtils.message(player, Lang.SYNTAX_ERROR_AMOUNT.get(player));
            return false;
        }
        propertyData.setSalePrice(amount);
        openGui(guiCallback, player);
        return true;
    }
}
