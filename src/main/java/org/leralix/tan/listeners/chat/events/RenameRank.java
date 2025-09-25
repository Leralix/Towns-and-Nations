package org.leralix.tan.listeners.chat.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.user.ranks.RankManagerMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;

public class RenameRank extends ChatListenerEvent {

    private final TerritoryData territoryConcerned;
    private final RankData rankData;
    public RenameRank(TerritoryData territoryData, RankData rankData) {
        this.territoryConcerned = territoryData;
        this.rankData = rankData;
    }

    @Override
    public boolean execute(Player player, String message) {
        int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("RankNameSize");

        if(message.length() > maxSize){
            player.sendMessage(Lang.MESSAGE_TOO_LONG.get(player, maxSize));
            return false;
        }

        rankData.setName(message);
        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> new RankManagerMenu(player, territoryConcerned, rankData).open());
        return false;
    }
}
