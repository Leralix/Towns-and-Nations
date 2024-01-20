package org.tan.TownsAndNations.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.*;

import java.util.Objects;

import static org.tan.TownsAndNations.TownsAndNations.isSqlEnable;
import static org.tan.TownsAndNations.enums.SoundEnum.*;
import static org.tan.TownsAndNations.enums.TownRolePermission.KICK_PLAYER;
import static org.tan.TownsAndNations.utils.EconomyUtil.getBalance;
import static org.tan.TownsAndNations.utils.EconomyUtil.removeFromBalance;
import static org.tan.TownsAndNations.utils.TeamUtils.setIndividualScoreBoard;
import static org.tan.TownsAndNations.utils.TeamUtils.updateAllScoreboardColor;

public class TownUtil {

    public static void CreateTown(Player player, int townCost, String townName){


        PlayerData playerData = PlayerDataStorage.get(player);
        assert playerData != null;


        int playerBalance = getBalance(player);

        if(playerBalance < townCost){
            player.sendMessage(Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(townCost - playerBalance));
            PlayerChatListenerStorage.removePlayer(player);
            return;
        }

        FileConfiguration config =  ConfigUtil.getCustomConfig("config.yml");
        int maxSize = config.getInt("TownNameSize");

        if(townName.length() > maxSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }


        Bukkit.broadcastMessage(ChatUtils.getTANString() + Lang.TOWN_CREATE_SUCCESS_BROADCAST.get(player.getName(),townName));

        PlayerChatListenerStorage.removePlayer(player);


        removeFromBalance(player,townCost);
        TownData newTown = TownDataStorage.newTown(townName,player);

        playerData.setRank(newTown.getTownDefaultRank());
        playerData.setTownId(newTown.getID());

        for (TownData otherTown : TownDataStorage.getTownList().values()) {
            if(otherTown == TownDataStorage.get(townName)){
                continue;
            }
            TownInviteDataStorage.removeInvitation(player,otherTown.getID());
        }
        SoundUtil.playSound(player, LEVEL_UP);

        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> setIndividualScoreBoard(player));
    }

    public static void DonateToTown(Player player, int amountDonated){

        int playerBalance = getBalance(player);

        if(playerBalance < amountDonated ){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NOT_ENOUGH_MONEY.get());
            PlayerChatListenerStorage.removePlayer(player);
            return;
        }
        if(amountDonated <= 0 ){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NEED_1_OR_ABOVE.get());
            PlayerChatListenerStorage.removePlayer(player);
            return;
        }

        TownData playerTown = TownDataStorage.get(player);

        removeFromBalance(player, amountDonated);

        playerTown.addToBalance(amountDonated);
        if(!isSqlEnable())
            playerTown.getTreasury().addDonation(player.getName(),player.getUniqueId().toString(),amountDonated);

        player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_SEND_MONEY_TO_TOWN.get(amountDonated));
        PlayerChatListenerStorage.removePlayer(player);
        SoundUtil.playSound(player, MINOR_LEVEL_UP);
    }

    public static void deleteTown(TownData townToDelete){

        ClaimedChunkStorage.unclaimAllChunkFrom(townToDelete.getID());
        townToDelete.cancelAllRelation();
        TownDataStorage.removeTown(townToDelete.getID());
        removeAllPlayerFromTown(townToDelete);

        updateAllScoreboardColor();
    }
    public static void removeAllPlayerFromTown(TownData townToDelete){
        for(String playerID : townToDelete.getPlayerList()){
            if(isSqlEnable())
                TownDataStorage.removePlayerFromTownDatabase(playerID);
            else
                PlayerDataStorage.get(playerID).leaveTown();
        }
    }

    public static void kickPlayer(Player player, OfflinePlayer kickedPlayer) {
        PlayerData playerData = PlayerDataStorage.get(player);
        PlayerData kickedPlayerData = PlayerDataStorage.get(kickedPlayer);
        TownData townData = TownDataStorage.get(playerData);


        if(playerData.hasPermission(KICK_PLAYER)){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            return;
        }
        int playerLevel = townData.getRank(playerData).getLevel();
        int kickedPlayerLevel = townData.getRank(kickedPlayerData).getLevel();
        if(playerLevel >= kickedPlayerLevel){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get());
            return;
        }
        if(kickedPlayerData.isTownLeader()){
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_LEADER.get());
            return;
        }
        if(playerData.getUuid().equals(kickedPlayerData.getUuid())){
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_YOURSELF.get());
            return;
        }
        TownData town = TownDataStorage.get(playerData);
        town.getRank(kickedPlayerData.getTownRankID()).removePlayer(kickedPlayerData.getUuid());
        town.removePlayer(kickedPlayerData.getUuid());
        kickedPlayerData.leaveTown();
        town.broadCastMessageWithSound(Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS.get(kickedPlayer.getName()),
                BAD);
        if(kickedPlayer.isOnline())
            Objects.requireNonNull(kickedPlayer.getPlayer()).sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS_PLAYER.get());
    }

    public static void renameTown(Player player, int townCost, String newName, TownData town) {
        PlayerChatListenerStorage.removePlayer(player);
        player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_WRITE_NEW_NAME_IN_CHAT_SUCCESS.get(town.getName(),newName));
        if(!isSqlEnable())
            town.getTreasury().addMiscellaneousPurchase(Lang.GUI_TOWN_SETTINGS_NEW_TOWN_NAME_HISTORY.get(town.getName() ,newName),townCost);
        town.removeToBalance(townCost);
        town.setName(newName);
    }

}
