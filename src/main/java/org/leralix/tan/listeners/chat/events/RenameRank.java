package org.leralix.tan.listeners.chat.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.ChatUtils;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import static org.leralix.tan.listeners.chat.PlayerChatListenerStorage.removePlayer;

public class RenameRank extends ChatListenerEvent {

    private final TerritoryData territoryConcerned;
    private final RankData rankData;
    public RenameRank(TerritoryData territoryData, RankData rankData) {
        this.territoryConcerned = territoryData;
        this.rankData = rankData;
    }

    @Override
    public void execute(Player player, String message) {
        int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("RankNameSize");

        if(message.length() > maxSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        rankData.setName(message);
        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> PlayerGUI.openRankManager(player, territoryConcerned, rankData));
        removePlayer(player);
    }
}
