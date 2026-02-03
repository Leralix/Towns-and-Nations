package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.NameFilter;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.function.Consumer;

public class CreateRank extends ChatListenerEvent {
    private final TerritoryData territoryData;
    private final Consumer<Player> guiCallback;

    public CreateRank(TerritoryData townData, Consumer<Player> guiCallback) {
        this.territoryData = townData;
        this.guiCallback = guiCallback;
    }

    @Override
    public boolean execute(Player player, ITanPlayer playerData, String message) {
        String rankName = message == null ? "" : message.trim();
        int maxNameSize = Constants.getRankNameSize();

        if (!NameFilter.validateOrWarn(player, rankName)) {
            return false;
        }

        if (rankName.length() > maxNameSize) {
            TanChatUtils.message(player, Lang.MESSAGE_TOO_LONG.get(playerData, Integer.toString(maxNameSize)));
            return false;
        }
        if (territoryData.isRankNameUsed(rankName)) {
            TanChatUtils.message(player, Lang.NAME_ALREADY_USED.get(playerData));
            return false;
        }

        territoryData.registerNewRank(rankName);
        openGui(guiCallback, player);
        return true;
    }
}
