package org.tan.TownsAndNations.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.DataClass.TownLevel;
import org.tan.TownsAndNations.DataClass.TownUpgrade;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.enums.MessageKey;
import org.tan.TownsAndNations.enums.TownRolePermission;
import org.tan.TownsAndNations.storage.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.tan.TownsAndNations.TownsAndNations.isSqlEnable;
import static org.tan.TownsAndNations.enums.ChatCategory.CREATE_CITY;
import static org.tan.TownsAndNations.enums.MessageKey.COST;
import static org.tan.TownsAndNations.enums.SoundEnum.*;
import static org.tan.TownsAndNations.enums.TownRolePermission.KICK_PLAYER;
import static org.tan.TownsAndNations.storage.TownDataStorage.*;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;
import static org.tan.TownsAndNations.utils.EconomyUtil.getBalance;
import static org.tan.TownsAndNations.utils.EconomyUtil.removeFromBalance;
import static org.tan.TownsAndNations.utils.TeamUtils.setIndividualScoreBoard;
import static org.tan.TownsAndNations.utils.TeamUtils.updateAllScoreboardColor;

public class TownUtil {

    public static void CreateTown(Player player, int townCost, String townName){

        PlayerData playerData = PlayerDataStorage.get(player);

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


        TownData newTown = TownDataStorage.newTown(townName,player);
        playerData.setRank(newTown.getTownDefaultRank()); //2. Set player rank to default rank
        playerData.setTownId(newTown.getID()); //3. Set player town to the new town
        removeFromBalance(player,townCost); //1. Remove money from player


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
            playerTown.getDonationHistory().add(player.getName(),player.getUniqueId().toString(),amountDonated);

        player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_SEND_MONEY_TO_TOWN.get(amountDonated));
        PlayerChatListenerStorage.removePlayer(player);
        SoundUtil.playSound(player, MINOR_LEVEL_UP);
    }

    public static void deleteTown(TownData townToDelete){

        NewClaimedChunkStorage.unclaimAllChunkFromTown(townToDelete.getID()); //Unclaim all chunk from town

        townToDelete.cancelAllRelation();   //Cancel all Relation between the deleted town and other town
        removeAllPlayerFromTown(townToDelete); //Kick all Players from the deleted town

        if(isSqlEnable()) { //if SQL is enabled, some data need to be removed manually
            removeAllChunkPermissionsForTown(townToDelete.getID()); //Remove all chunk permission from the deleted town
            deleteAllRole(townToDelete.getID()); //Delete all role from the deleted town
            deleteRolePermissionFromTown(townToDelete.getID()); //Delete all role permission from the deleted town
            NewClaimedChunkStorage.unclaimAllChunkFromTown(townToDelete.getID());  //Unclaim all chunk from the deleted town NOT WORKING RN
            removeTownUpgradeFromDB(townToDelete.getID()); //Delete all town upgrade from the deleted town
        }

        TownDataStorage.removeTown(townToDelete.getID()); //Delete the main town class.


        updateAllScoreboardColor();
    }
    public static void removeAllPlayerFromTown(TownData townToDelete){
        for(String playerID : townToDelete.getPlayerList()){
            PlayerDataStorage.get(playerID).leaveTown();
            if(isSqlEnable())
                TownDataStorage.removePlayerFromTownDatabase(playerID); //Small link database that will be deleted later
        }
    }

    public static void kickPlayer(Player player, OfflinePlayer kickedPlayer) {
        PlayerData playerData = PlayerDataStorage.get(player);
        PlayerData kickedPlayerData = PlayerDataStorage.get(kickedPlayer);
        TownData townData = TownDataStorage.get(playerData);


        if(!playerData.hasPermission(KICK_PLAYER)){
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
            town.getMiscellaneousHistory().add(Lang.GUI_TOWN_SETTINGS_NEW_TOWN_NAME_HISTORY.get(town.getName() ,newName),townCost);
        town.removeToBalance(townCost);
        town.setName(newName);
    }

    public static void upgradeTown(Player player, TownData townData){
        PlayerData playerData = PlayerDataStorage.get(player);
        TownLevel townLevel = townData.getTownLevel();
        if(!playerData.hasPermission(TownRolePermission.UPGRADE_TOWN)){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }
        if(townData.getBalance() < townLevel.getMoneyRequiredTownLevel()) {
            player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get());
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }

        townData.removeToBalance(townLevel.getMoneyRequiredTownLevel());
        townLevel.TownLevelUp();
        if(isSqlEnable())
            TownDataStorage.updateTownUpgradeFromDatabase(townData.getID(),townLevel);
        SoundUtil.playSound(player,LEVEL_UP);
        player.sendMessage(getTANString() + Lang.BASIC_LEVEL_UP.get());
    }

    public static void upgradeTown(Player player, TownUpgrade townUpgrade, TownData townData){
        PlayerData playerData = PlayerDataStorage.get(player);

        TownLevel townLevel = townData.getTownLevel();
        if(!playerData.hasPermission(TownRolePermission.UPGRADE_TOWN)){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }
        int cost = townUpgrade.getCost(townLevel.getUpgradeLevel(townUpgrade.getName()));
        if(townData.getBalance() < cost ) {
            player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY_EXTENDED.get(cost - townData.getBalance()));
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }
        if(townLevel.getUpgradeLevel(townUpgrade.getName()) >= townUpgrade.getMaxLevel()){
            player.sendMessage(getTANString() + Lang.TOWN_UPGRADE_MAX_LEVEL.get());
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }

        townData.removeToBalance(townLevel.getMoneyRequiredTownLevel());
        townLevel.levelUp(townUpgrade);
        if(isSqlEnable())
            TownDataStorage.updateTownUpgradeFromDatabase(townData.getID(),townLevel);
        SoundUtil.playSound(player,LEVEL_UP);
        player.sendMessage(getTANString() + Lang.BASIC_LEVEL_UP.get());
    }


    public static void registerNewTown(Player player, int townPrice) {

        int playerMoney = EconomyUtil.getBalance(player);
        if (playerMoney < townPrice) {
            player.sendMessage(getTANString() + Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(townPrice - playerMoney));
        }
        else {
            player.sendMessage(getTANString() + Lang.PLAYER_WRITE_TOWN_NAME_IN_CHAT.get());
            player.closeInventory();

            Map<MessageKey,String> data = new HashMap<>();
            data.put(COST,Integer.toString(townPrice));
            PlayerChatListenerStorage.addPlayer(CREATE_CITY,player,data);
        }
    }
}
