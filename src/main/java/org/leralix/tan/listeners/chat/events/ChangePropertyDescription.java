package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.lang.Lang;

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

        int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("PropertyDescSize");

        if(message.length() > maxSize){
            player.sendMessage(TanChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return false;
        }
        PlayerChatListenerStorage.removePlayer(player);

        propertyData.setDescription(message);
        player.sendMessage(TanChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get());
        openGui(guiCallback,player);
        return true;
    }
}
