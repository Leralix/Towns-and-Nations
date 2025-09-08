package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.function.Consumer;

public class ChangeTerritoryName extends ChatListenerEvent {

    private final TerritoryData territoryToRename;
    private final int cost;
    private final Consumer<Player> guiCallback;

    public ChangeTerritoryName(@NotNull TerritoryData territoryToRename, int cost, Consumer<Player> guiCallback) {
        this.territoryToRename = territoryToRename;
        this.cost = cost;
        this.guiCallback = guiCallback;
    }

    @Override
    public boolean execute(Player player, String message) {

        int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TownNameSize");

        if(message.length() > maxSize){
            player.sendMessage(TanChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return false;
        }

        territoryToRename.rename(player, cost, message);
        openGui(guiCallback, player);
        return true;
    }
}
