package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.constants.Constants;
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

        int maxSize = territoryToRename instanceof TownData ?
                Constants.getTownMaxDescriptionSize() :
                Constants.getRegionMaxDescriptionSize();

        if(message.length() > maxSize){
            TanChatUtils.message(player, Lang.MESSAGE_TOO_LONG.get(player, Integer.toString(maxSize)));
            return false;
        }

        territoryToRename.rename(player, cost, message);
        openGui(guiCallback, player);
        return true;
    }
}
