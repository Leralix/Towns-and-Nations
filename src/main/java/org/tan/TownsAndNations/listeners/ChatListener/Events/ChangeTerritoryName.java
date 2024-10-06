package org.tan.TownsAndNations.listeners.ChatListener.Events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.listeners.ChatListener.ChatListenerEvent;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;

import java.util.function.Consumer;

import static org.tan.TownsAndNations.enums.MessageKey.COST;
import static org.tan.TownsAndNations.enums.MessageKey.TOWN_ID;
import static org.tan.TownsAndNations.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;

public class ChangeTerritoryName extends ChatListenerEvent {

    private ITerritoryData territoryToRename;
    int cost;
    private Consumer<Player> guiCallback;
    public ChangeTerritoryName(@NotNull ITerritoryData territoryToRename, int cost, Consumer<Player> guiCallback) {
        this.territoryToRename = territoryToRename;
        this.cost = cost;
        this.guiCallback = guiCallback;
    }

    @Override
    public void execute(Player player, String message) {

        int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TownNameSize");

        if(message.length() > maxSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        removePlayer(player);
        territoryToRename.rename(player, cost, message);
        openGui(guiCallback, player);
    }
}
