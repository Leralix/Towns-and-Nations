package org.tan.TownsAndNations.listeners.ChatListener.Events;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PropertyData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.listeners.ChatListener.ChatListenerEvent;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;

import java.util.function.Consumer;

import static org.tan.TownsAndNations.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;

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
