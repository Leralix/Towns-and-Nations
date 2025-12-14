package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.function.Consumer;

public class ChangePropertyDescription extends ChatListenerEvent {
    private final PropertyData propertyData;
    private final Consumer<Player> guiCallback;

    public ChangePropertyDescription(@NotNull PropertyData propertyData, Consumer<Player> guiCallback) {
        this.propertyData = propertyData;
        this.guiCallback = guiCallback;
    }

    @Override
    public boolean execute(Player player, String message) {
        int maxSize = Constants.getMaxPropertyDescriptionSize();

        if (message.length() > maxSize) {
            TanChatUtils.message(player, Lang.MESSAGE_TOO_LONG.get(player, Integer.toString(maxSize)));
            return false;
        }

        propertyData.setDescription(message);
        TanChatUtils.message(player, Lang.CHANGE_MESSAGE_SUCCESS.get(player));
        openGui(guiCallback, player);
        return true;
    }
}
