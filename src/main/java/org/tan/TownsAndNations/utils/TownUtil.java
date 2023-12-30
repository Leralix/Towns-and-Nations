package org.tan.TownsAndNations.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.storage.*;

import java.util.Objects;

import static org.tan.TownsAndNations.enums.SoundEnum.*;
import static org.tan.TownsAndNations.enums.TownRolePermission.KICK_PLAYER;
import static org.tan.TownsAndNations.utils.EconomyUtil.getBalance;
import static org.tan.TownsAndNations.utils.EconomyUtil.removeFromBalance;

public class TownUtil {

    public static void CreateTown(Player player, int townCost, String townName){


        PlayerData playerData = PlayerDataStorage.get(player);
        assert playerData != null;


        int playerBalance = getBalance(player);

        if(playerBalance < townCost){
            player.sendMessage(Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.getTranslation(townCost - playerBalance));
            PlayerChatListenerStorage.removePlayer(player);
            return;
        }

        FileConfiguration config =  ConfigUtil.getCustomConfig("config.yml");
        int maxSize = config.getInt("TownNameSize");

        if(townName.length() > maxSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.getTranslation(maxSize));
            return;
        }


        Bukkit.broadcastMessage(ChatUtils.getTANString() + Lang.TOWN_CREATE_SUCCESS_BROADCAST.getTranslation(player.getName(),townName));

        PlayerChatListenerStorage.removePlayer(player);


        removeFromBalance(player,townCost);
        TownDataStorage.newTown(townName,player);
        playerData.setRank(TownDataStorage.get(playerData).getTownDefaultRank());


        for (TownData otherTown : TownDataStorage.getTownList().values()) {
            if(otherTown == TownDataStorage.get(townName)){
                continue;
            }
            TownInviteDataStorage.removeInvitation(player,otherTown.getID());
        }
        SoundUtil.playSound(player, LEVEL_UP);

    }

    public static void DonateToTown(Player player, int amountDonated){

        int playerBalance = getBalance(player);

        if(playerBalance < amountDonated ){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NOT_ENOUGH_MONEY.getTranslation());
            PlayerChatListenerStorage.removePlayer(player);
            return;
        }
        if(amountDonated <= 0 ){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NEED_1_OR_ABOVE.getTranslation());
            PlayerChatListenerStorage.removePlayer(player);
            return;
        }

        TownData playerTown = TownDataStorage.get(player);

        removeFromBalance(player, amountDonated);

        playerTown.getTreasury().addToBalance(amountDonated);
        playerTown.getTreasury().addDonation(player.getName(),player.getUniqueId().toString(),amountDonated);

        player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_SEND_MONEY_TO_TOWN.getTranslation(amountDonated));
        PlayerChatListenerStorage.removePlayer(player);
        SoundUtil.playSound(player, MINOR_LEVEL_UP);
    }

    public static void deleteTown(TownData townToDelete){

        TownData playerTown = TownDataStorage.get(townToDelete.getID());

        ClaimedChunkStorage.unclaimAllChunkFrom(townToDelete.getID());

        playerTown.cancelAllRelation();
        TownDataStorage.removeTown(townToDelete.getID());

        for(String memberUUID : playerTown.getPlayerList()){
            PlayerData memberStat = PlayerDataStorage.get(memberUUID);
            assert memberStat != null;
            memberStat.leaveTown();
        }
    }

    public static void kickPlayer(Player player, OfflinePlayer kickedPlayer) {
        PlayerData playerData = PlayerDataStorage.get(player);
        PlayerData kickedPlayerData = PlayerDataStorage.get(kickedPlayer);
        TownData townData = TownDataStorage.get(playerData);


        if(playerData.hasPermission(KICK_PLAYER)){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
            return;
        }
        int playerLevel = townData.getRank(playerData).getLevel();
        int kickedPlayerLevel = townData.getRank(kickedPlayerData).getLevel();
        if(playerLevel >= kickedPlayerLevel){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.getTranslation());
            return;
        }
        if(kickedPlayerData.isTownLeader()){
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_LEADER.getTranslation());
            return;
        }
        if(playerData.getUuid().equals(kickedPlayerData.getUuid())){
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_YOURSELF.getTranslation());
            return;
        }
        TownData town = TownDataStorage.get(playerData);
        town.getRank(kickedPlayerData.getTownRankID()).removePlayer(kickedPlayerData.getUuid());
        town.removePlayer(kickedPlayerData.getUuid());
        kickedPlayerData.leaveTown();
        town.broadCastMessageWithSound(Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS.getTranslation(kickedPlayer.getName()),
                BAD);
        if(kickedPlayer.isOnline())
            Objects.requireNonNull(kickedPlayer.getPlayer()).sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS_PLAYER.getTranslation());
    }
}
