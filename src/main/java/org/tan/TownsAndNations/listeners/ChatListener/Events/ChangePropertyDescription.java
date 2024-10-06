package org.tan.TownsAndNations.listeners.ChatListener.Events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.tan.TownsAndNations.DataClass.PropertyData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.listeners.ChatListener.ChatListenerEvent;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;

import java.util.function.Consumer;

import static org.tan.TownsAndNations.enums.MessageKey.PROPERTY_ID;
import static org.tan.TownsAndNations.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;

public class ChangePropertyDescription extends ChatListenerEvent {
    private PropertyData propertyData;
    private Consumer<Player> guiCallback;
    public ChangePropertyDescription(@NotNull PropertyData propertyData, Consumer<Player> guiCallback) {
        this.propertyData = propertyData;
        this.guiCallback = guiCallback;
    }

    @Override
    public void execute(Player player, String message) {

        int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("PropertyDescSize");

        if(message.length() > maxSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }
        removePlayer(player);

        propertyData.setDescription(message);
        player.sendMessage(ChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get());

    }
}
