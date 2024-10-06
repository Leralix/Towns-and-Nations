package org.tan.TownsAndNations.listeners.ChatListener.Events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.TownRank;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.GUI.playerGUI;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.listeners.ChatListener.ChatListenerEvent;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;

import static org.tan.TownsAndNations.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;

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
        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> playerGUI.OpenTownRankManager(player, newRank.getID()));
    }
}
