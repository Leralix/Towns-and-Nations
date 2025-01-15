package org.leralix.tan.listeners.chat.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;

public class CreateRank extends ChatListenerEvent {
    TerritoryData territoryData;
    public CreateRank(TerritoryData townData){
        this.territoryData = townData;
    }
    @Override
    public void execute(Player player, String message) {
        int maxNameSize = ConfigUtil.getCustomConfig(ConfigTag.TAN).getInt("RankNameSize");

        if(message.length() > maxNameSize){
            player.sendMessage(TanChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxNameSize));
            return;
        }
        if(territoryData.isRankNameUsed(message)){
            player.sendMessage(TanChatUtils.getTANString() + Lang.NAME_ALREADY_USED.get());
            return;
        }

        PlayerChatListenerStorage.removePlayer(player);
        RankData newRank = territoryData.registerNewRank(message);
        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> PlayerGUI.openRankManager(player, territoryData, newRank));
    }
}
