package org.leralix.tan.listeners.ChatListener.Events;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.Lang.Lang;
import org.leralix.tan.listeners.ChatListener.ChatListenerEvent;
import org.leralix.tan.utils.ChatUtils;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import java.util.function.Consumer;

import static org.leralix.tan.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;

public class ChangePropertyName extends ChatListenerEvent {
    PropertyData propertyToRename;
    Consumer<Player> guiCallback;

    public ChangePropertyName(PropertyData propertyData, Consumer<Player> guiCallback) {
        this.propertyToRename = propertyData;
        this.guiCallback = guiCallback;
    }

    @Override
    public void execute(Player player, String message) {
        removePlayer(player);

        int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("PropertyNameSize");

        if(message.length() > maxSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        propertyToRename.setName(message);
        player.sendMessage(ChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get());
        openGui(guiCallback, player);
    }
}
