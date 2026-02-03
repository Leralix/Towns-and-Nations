package org.leralix.tan.listeners.chat.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.rank.RankData;
import org.leralix.tan.gui.user.ranks.RankManagerMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.NameFilter;
import org.leralix.tan.utils.text.TanChatUtils;

public class RenameRank extends ChatListenerEvent {

    private final TerritoryData territoryConcerned;
    private final RankData rankData;

    public RenameRank(TerritoryData territoryData, RankData rankData) {
        this.territoryConcerned = territoryData;
        this.rankData = rankData;
    }

    @Override
    public boolean execute(Player player, ITanPlayer playerData, String message) {
        String rankName = message == null ? "" : message.trim();
        int maxSize = Constants.getRankNameSize();

        if (!NameFilter.validateOrWarn(player, rankName)) {
            return false;
        }

        if (rankName.length() > maxSize) {
            TanChatUtils.message(player, Lang.MESSAGE_TOO_LONG.get(playerData, Integer.toString(maxSize)));
            return false;
        }

        rankData.setName(rankName);
        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> new RankManagerMenu(player, territoryConcerned, rankData).open());
        return true;
    }
}
