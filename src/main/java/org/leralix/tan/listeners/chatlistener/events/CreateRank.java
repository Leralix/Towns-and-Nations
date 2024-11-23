package org.leralix.tan.listeners.chatlistener.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.listeners.chatlistener.ChatListenerEvent;
import org.leralix.tan.utils.ChatUtils;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import static org.leralix.tan.listeners.chatlistener.PlayerChatListenerStorage.removePlayer;

public class CreateRank extends ChatListenerEvent {
    TerritoryData territoryData;
    public CreateRank(TerritoryData townData){
        this.territoryData = townData;
    }
    @Override
    public void execute(Player player, String message) {
        int maxNameSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("RankNameSize");

        if(message.length() > maxNameSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxNameSize));
            return;
        }
        if(territoryData.isRankNameUsed(message)){
            player.sendMessage(ChatUtils.getTANString() + Lang.NAME_ALREADY_USED.get());
            return;
        }

        removePlayer(player);
        RankData newRank = territoryData.registerNewRank(message);
        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> PlayerGUI.openRankManager(player, territoryData, newRank.getID()));
    }
}
