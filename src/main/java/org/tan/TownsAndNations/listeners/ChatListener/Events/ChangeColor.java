package org.tan.TownsAndNations.listeners.ChatListener.Events;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.listeners.ChatListener.ChatListenerEvent;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.StringUtil;

import java.util.function.Consumer;

import static org.tan.TownsAndNations.enums.MessageKey.TOWN_ID;
import static org.tan.TownsAndNations.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;
import static org.tan.TownsAndNations.utils.StringUtil.hexColorToInt;

public class ChangeColor extends ChatListenerEvent {
    private final ITerritoryData territoryData;
    private final Consumer<Player> guiCallback;
    public ChangeColor(ITerritoryData territoryData, Consumer<Player> guiCallback) {
        this.territoryData = territoryData;
        this.guiCallback = guiCallback;
    }

    @Override
    public void execute(Player player, String message) {
        removePlayer(player);

        if(!StringUtil.isValidColorCode(message)){
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT_ERROR.get());
            return;
        }
        territoryData.setChunkColor(hexColorToInt(message));
        player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT_SUCCESS.get());
        openGui(guiCallback,player);
    }
}
