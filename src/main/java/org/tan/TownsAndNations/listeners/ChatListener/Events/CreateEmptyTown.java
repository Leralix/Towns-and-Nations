package org.tan.TownsAndNations.listeners.ChatListener.Events;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.listeners.ChatListener.ChatListenerEvent;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.FileUtil;
import org.tan.TownsAndNations.utils.SoundUtil;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;

import static org.tan.TownsAndNations.enums.SoundEnum.LEVEL_UP;
import static org.tan.TownsAndNations.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;

public class CreateEmptyTown extends ChatListenerEvent {
    @Override
    public void execute(Player player, String townName) {
        FileConfiguration config =  ConfigUtil.getCustomConfig(ConfigTag.MAIN);
        int maxSize = config.getInt("TownNameSize");

        if(townName.length() > maxSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        if(TownDataStorage.isNameUsed(townName)){
            player.sendMessage(ChatUtils.getTANString() + Lang.NAME_ALREADY_USED.get());
            return;
        }

        TownDataStorage.newTown(townName);
        Bukkit.broadcastMessage(ChatUtils.getTANString() + Lang.TOWN_CREATE_SUCCESS_BROADCAST.get(player.getName(),townName));
        SoundUtil.playSound(player, LEVEL_UP);
        removePlayer(player);
        FileUtil.addLineToHistory(Lang.HISTORY_TOWN_CREATED.get(player.getName(),townName));
    }
}
