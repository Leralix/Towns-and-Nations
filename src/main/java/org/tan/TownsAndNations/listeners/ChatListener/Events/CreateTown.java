package org.tan.TownsAndNations.listeners.ChatListener.Events;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.Economy.EconomyUtil;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.listeners.ChatListener.ChatListenerEvent;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.FileUtil;
import org.tan.TownsAndNations.utils.SoundUtil;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;

import static org.tan.TownsAndNations.enums.SoundEnum.LEVEL_UP;
import static org.tan.TownsAndNations.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;
import static org.tan.TownsAndNations.utils.TeamUtils.setIndividualScoreBoard;

public class CreateTown extends ChatListenerEvent {
    int cost;
    public CreateTown(int cost) {
        super();
        this.cost = cost;
    }

    @Override
    public void execute(Player player, String message) {
        PlayerData playerData = PlayerDataStorage.get(player);
        int playerBalance = EconomyUtil.getBalance(player);

        if(playerBalance < cost){
            player.sendMessage(Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(cost - playerBalance));
            return;
        }

        FileConfiguration config =  ConfigUtil.getCustomConfig(ConfigTag.MAIN);
        int maxSize = config.getInt("TownNameSize");

        if(message.length() > maxSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        if(TownDataStorage.isNameUsed(message)){
            player.sendMessage(ChatUtils.getTANString() + Lang.NAME_ALREADY_USED.get());
            return;
        }

        TownData newTown = TownDataStorage.newTown(message,player);
        EconomyUtil.removeFromBalance(player,cost);
        playerData.joinTown(newTown);



        Bukkit.broadcastMessage(ChatUtils.getTANString() + Lang.TOWN_CREATE_SUCCESS_BROADCAST.get(player.getName(),message));
        SoundUtil.playSound(player, LEVEL_UP);
        removePlayer(player);
        FileUtil.addLineToHistory(Lang.HISTORY_TOWN_CREATED.get(player.getName(),message));

        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> setIndividualScoreBoard(player));

    }
}
