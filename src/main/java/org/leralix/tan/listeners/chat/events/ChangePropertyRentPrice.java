package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;

import java.util.function.Consumer;

import static org.leralix.tan.listeners.chat.PlayerChatListenerStorage.removePlayer;
import static org.leralix.tan.utils.ChatUtils.getTANString;

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
            if(amount < 0) {
                amount = 0;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
            return;
        }

        propertyData.setRentPrice(amount);
    }
}
