package org.tan.TownsAndNations.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.Economy.EconomyUtil;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.listeners.ChatListener.Events.CreateTown;
import org.tan.TownsAndNations.listeners.ChatListener.PlayerChatListenerStorage;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;

import static org.tan.TownsAndNations.Economy.EconomyUtil.getBalance;
import static org.tan.TownsAndNations.Economy.EconomyUtil.removeFromBalance;
import static org.tan.TownsAndNations.enums.SoundEnum.LEVEL_UP;
import static org.tan.TownsAndNations.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;
import static org.tan.TownsAndNations.utils.TeamUtils.setIndividualScoreBoard;

public class TownUtil {

    public static void createTown(final @NotNull Player player, final int townCost, final @NotNull String townName){

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
            player.sendMessage(getTANString() + Lang.PLAYER_WRITE_TOWN_NAME_IN_CHAT.get());
            PlayerChatListenerStorage.register(player, new CreateTown(townPrice));
        }
    }

    public static void createAdminTown(Player player, String townName) {

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
