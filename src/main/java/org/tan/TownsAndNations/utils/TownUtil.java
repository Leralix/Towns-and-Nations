package org.tan.TownsAndNations.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.tan.TownsAndNations.DataClass.*;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.enums.ChatCategory;
import org.tan.TownsAndNations.enums.MessageKey;
import org.tan.TownsAndNations.storage.*;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;

import java.util.HashMap;
import java.util.Map;

import static org.tan.TownsAndNations.enums.MessageKey.COST;
import static org.tan.TownsAndNations.enums.SoundEnum.*;
import static org.tan.TownsAndNations.storage.PlayerChatListenerStorage.removePlayer;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;
import static org.tan.TownsAndNations.utils.EconomyUtil.getBalance;
import static org.tan.TownsAndNations.utils.EconomyUtil.removeFromBalance;
import static org.tan.TownsAndNations.utils.TeamUtils.setIndividualScoreBoard;

public class TownUtil {

    public static void CreateTown(final @NotNull Player player, final int townCost, final @NotNull String townName){

        PlayerData playerData = PlayerDataStorage.get(player);
        int playerBalance = getBalance(player);

        if(playerBalance < townCost){
            player.sendMessage(Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(townCost - playerBalance));
            return;
        }

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

        TownData newTown = TownDataStorage.newTown(townName,player);
        removeFromBalance(player,townCost);
        playerData.joinTown(newTown);



        Bukkit.broadcastMessage(ChatUtils.getTANString() + Lang.TOWN_CREATE_SUCCESS_BROADCAST.get(player.getName(),townName));
        SoundUtil.playSound(player, LEVEL_UP);
        removePlayer(player);
        FileUtil.addLineToHistory(Lang.HISTORY_TOWN_CREATED.get(player.getName(),townName));

        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> setIndividualScoreBoard(player));
    }

    public static void registerNewTown(Player player, int townPrice) {

        int playerMoney = EconomyUtil.getBalance(player);
        if (playerMoney < townPrice) {
            player.sendMessage(getTANString() + Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(townPrice - playerMoney));
        }
        else {
            Map<MessageKey,String> data = new HashMap<>();
            data.put(COST,Integer.toString(townPrice));
            PlayerChatListenerStorage.addPlayer(ChatCategory.CREATE_CITY,player,data);

            player.sendMessage(getTANString() + Lang.PLAYER_WRITE_TOWN_NAME_IN_CHAT.get());
            player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
            player.closeInventory();
        }
    }

    public static void CreateAdminTown(Player player, String townName) {

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

        TownData newTown = TownDataStorage.newTown(townName);


        Bukkit.broadcastMessage(ChatUtils.getTANString() + Lang.TOWN_CREATE_SUCCESS_BROADCAST.get(player.getName(),townName));
        SoundUtil.playSound(player, LEVEL_UP);
        removePlayer(player);
        FileUtil.addLineToHistory(Lang.HISTORY_TOWN_CREATED.get(player.getName(),townName));

    }
}
