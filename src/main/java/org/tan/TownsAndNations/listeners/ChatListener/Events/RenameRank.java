package org.tan.TownsAndNations.listeners.ChatListener.Events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.TownRank;
import org.tan.TownsAndNations.GUI.playerGUI;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.listeners.ChatListener.ChatListenerEvent;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;

import static org.tan.TownsAndNations.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;

public class RenameRank extends ChatListenerEvent {

    private final TownRank townRank;
    public RenameRank(TownRank townRank) {
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
        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> playerGUI.OpenTownRankManager(player, townRank.getID()));
        removePlayer(player);
    }
}
