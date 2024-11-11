package org.leralix.tan.listeners.chatlistener.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chatlistener.ChatListenerEvent;
import org.leralix.tan.utils.ChatUtils;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import java.util.function.Consumer;

import static org.leralix.tan.listeners.chatlistener.PlayerChatListenerStorage.removePlayer;

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
