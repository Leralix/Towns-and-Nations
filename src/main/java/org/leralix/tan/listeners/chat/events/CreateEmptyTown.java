package org.leralix.tan.listeners.chat.events;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.ChatUtils;
import org.leralix.tan.utils.FileUtil;
import org.leralix.tan.utils.SoundUtil;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import static org.leralix.tan.enums.SoundEnum.LEVEL_UP;
import static org.leralix.tan.listeners.chat.PlayerChatListenerStorage.removePlayer;

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
