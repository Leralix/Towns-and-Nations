package org.leralix.tan.listeners.ChatListener.Events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.TownRank;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.listeners.ChatListener.ChatListenerEvent;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.ChatUtils;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import static org.leralix.tan.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;

public class CreateRank extends ChatListenerEvent {
    TownData townData;
    public CreateRank(TownData townData){
        this.townData = townData;
    }
    @Override
    public void execute(Player player, String message) {
        int maxNameSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("RankNameSize");

        if(message.length() > maxNameSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxNameSize));
            return;
        }
        TownData townData = TownDataStorage.get(player);
        if(townData.isRankNameUsed(message)){
            player.sendMessage(ChatUtils.getTANString() + Lang.NAME_ALREADY_USED.get());
            return;
        }

        removePlayer(player);
        TownRank newRank = townData.newRank(message);
        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> PlayerGUI.openTownRankManager(player, newRank.getID()));
    }
}
