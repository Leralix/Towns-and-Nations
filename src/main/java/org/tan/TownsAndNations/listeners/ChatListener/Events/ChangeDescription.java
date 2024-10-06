package org.tan.TownsAndNations.listeners.ChatListener.Events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.listeners.ChatListener.ChatListenerEvent;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.FileUtil;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;

import java.util.function.Consumer;

import static org.tan.TownsAndNations.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;

public class ChangeDescription extends ChatListenerEvent {

    private final ITerritoryData territoryData;
    Consumer<Player> guiCallback;

    public ChangeDescription(@NotNull ITerritoryData territoryData, Consumer<Player> guiCallback) {
        this.territoryData = territoryData;
        this.guiCallback = guiCallback;
    }

    @Override
    public void execute(Player player, String message) {
        int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TownDescSize");

        if(message.length() > maxSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        FileUtil.addLineToHistory(Lang.HISTORY_TOWN_MESSAGE_CHANGED.get(player.getName(), territoryData.getName(),message));
        territoryData.setDescription(message);
        player.sendMessage(ChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get());
        removePlayer(player);
        openGui(guiCallback,player);
    }
}
