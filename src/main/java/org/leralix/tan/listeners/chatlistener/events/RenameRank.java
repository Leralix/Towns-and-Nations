package org.leralix.tan.listeners.chatlistener.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.listeners.chatlistener.ChatListenerEvent;
import org.leralix.tan.utils.ChatUtils;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import static org.leralix.tan.listeners.chatlistener.PlayerChatListenerStorage.removePlayer;

public class RenameRank extends ChatListenerEvent {

    private final ITerritoryData territoryConcerned;
    private final RankData townRank;
    public RenameRank(ITerritoryData territoryData, RankData townRank) {
        this.territoryConcerned = territoryData;
        this.townRank = townRank;
    }

    @Override
    public void execute(Player player, String message) {
        int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("RankNameSize");

        if(message.length() > maxSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        townRank.setName(message);
        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> PlayerGUI.openTownRankManager(player, territoryConcerned, townRank.getID()));
        removePlayer(player);
    }
}
