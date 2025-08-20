package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.lang.Lang;

import java.util.function.Consumer;

public class ChangePropertySalePrice extends ChatListenerEvent {

    private final PropertyData propertyData;
    private final Consumer<Player> guiCallback;

    public ChangePropertySalePrice(@NotNull PropertyData propertyData, Consumer<Player> guiCallback) {
        this.propertyData = propertyData;
        this.guiCallback = guiCallback;
    }

    @Override
    public void execute(Player player, String message) {
        int amount;
        try{
            amount = Integer.parseInt(message);
            if(amount < 0) {
                amount = 0;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
            return;
        }
        propertyData.setSalePrice(amount);
        PlayerChatListenerStorage.removePlayer(player);
        openGui(guiCallback, player);
    }
}
