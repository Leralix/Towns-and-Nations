package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.lang.Lang;

import java.util.function.Consumer;

public class ChangePropertyName extends ChatListenerEvent {
    PropertyData propertyToRename;
    Consumer<Player> guiCallback;

    public ChangePropertyName(PropertyData propertyData, Consumer<Player> guiCallback) {
        this.propertyToRename = propertyData;
        this.guiCallback = guiCallback;
    }

    @Override
    public void execute(Player player, String message) {
        PlayerChatListenerStorage.removePlayer(player);

        int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("PropertyNameSize");

        if(message.length() > maxSize){
            player.sendMessage(TanChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        propertyToRename.setName(message);
        player.sendMessage(TanChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get());
        openGui(guiCallback, player);
    }
}
