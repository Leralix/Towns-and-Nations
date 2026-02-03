package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.NationData;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.TownData;
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
    public boolean execute(Player player, ITanPlayer playerData, String newName) {

        int maxSize;
        if (territoryToRename instanceof TownData) {
            maxSize = Constants.getTownMaxNameSize();
        } else if (territoryToRename instanceof NationData) {
            maxSize = Constants.getNationMaxNameSize();
        } else {
            maxSize = Constants.getRegionMaxNameSize();
        }

        if(newName.length() > maxSize){
            TanChatUtils.message(player, Lang.MESSAGE_TOO_LONG.get(playerData, Integer.toString(maxSize)));
            return false;
        }

        if (territoryToRename.getBalance() < cost) {
            TanChatUtils.message(player, Lang.TERRITORY_NOT_ENOUGH_MONEY.get(
                    playerData,
                    territoryToRename.getColoredName(),
                    Double.toString(cost - territoryToRename.getBalance()))
            );
            return true;
        }

        TanChatUtils.message(player, Lang.CHANGE_MESSAGE_SUCCESS.get(
                playerData,
                territoryToRename.getName(),
                newName
        ), SoundEnum.GOOD);

        territoryToRename.rename(player, cost, newName);
        openGui(guiCallback, player);
        return true;
    }
}
