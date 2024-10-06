package org.tan.TownsAndNations.listeners.ChatListener.Events;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.listeners.ChatListener.ChatListenerEvent;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;

import java.util.function.Consumer;

import static org.tan.TownsAndNations.enums.MessageKey.TOWN_ID;
import static org.tan.TownsAndNations.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;

public class ChangeTownTag extends ChatListenerEvent {

    private TownData townData;
    private Consumer<Player> guiCallback;
    public ChangeTownTag(TownData townData, Consumer<Player> guiCallback) {
        this.townData = townData;
        this.guiCallback = guiCallback;
    }

    @Override
    public void execute(Player player, String message) {

        int size = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("prefixSize");
        if(message.length() != size){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_NOT_RIGHT_SIZE.get(size));
            return;
        }

        townData.setTownTag(message);
        player.sendMessage(ChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get());
        removePlayer(player);
        openGui(guiCallback, player);

    }
}
