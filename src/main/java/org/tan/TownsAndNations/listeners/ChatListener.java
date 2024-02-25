package org.tan.TownsAndNations.listeners;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.DataClass.TownRank;
import org.tan.TownsAndNations.GUI.GuiManager2;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.*;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.ConfigUtil;
import org.tan.TownsAndNations.utils.RegionUtil;
import org.tan.TownsAndNations.utils.TownUtil;

import java.util.*;

import static org.tan.TownsAndNations.TownsAndNations.isSqlEnable;
import static org.tan.TownsAndNations.enums.ChatCategory.*;
import static org.tan.TownsAndNations.enums.MessageKey.*;
import static org.tan.TownsAndNations.storage.PlayerChatListenerStorage.removePlayer;
import static org.tan.TownsAndNations.utils.StringUtil.hexColorToInt;
import static org.tan.TownsAndNations.utils.StringUtil.isValidColorCode;
import static org.tan.TownsAndNations.utils.TownUtil.DonateToTown;


public class ChatListener implements Listener {

    @EventHandler
    public void OnPlayerChat(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        String playerUUID = player.getUniqueId().toString();

        PlayerChatListenerStorage.PlayerChatData chatData = PlayerChatListenerStorage.getPlayerData(playerUUID);

        if(chatData == null)
            return;


        if(chatData.getCategory() == CREATE_CITY){
            int townPrice = Integer.parseInt(chatData.getData().get(COST));
            String townName = event.getMessage();

            TownUtil.CreateTown(player, townPrice, townName);

            event.setCancelled(true);
        }

        if(chatData.getCategory() == TOWN_DONATION){

            String stringAmount = event.getMessage();

            int amount;
            try {amount = Integer.parseInt(stringAmount);}
            catch (NumberFormatException e) {
                player.sendMessage(ChatUtils.getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
                throw new RuntimeException(e);
            }

            DonateToTown(player, amount);

            event.setCancelled(true);
        }

            if(chatData.getCategory() == RANK_CREATION){
            removePlayer(player);
            String rankName = event.getMessage();

            FileConfiguration config =  ConfigUtil.getCustomConfig("config.yml");
            int maxSize = config.getInt("RankNameSize");
            if(rankName.length() > maxSize){
                player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
                event.setCancelled(true);
            }

            TownData playerTown = TownDataStorage.get(player);
            playerTown.addRank(rankName);
            Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> GuiManager2.OpenTownMenuRoleManager(player, rankName));
            event.setCancelled(true);

        }

        if(chatData.getCategory() == RANK_RENAME){
            PlayerChatListenerStorage.PlayerChatData ChatData = PlayerChatListenerStorage.getPlayerData(playerUUID);

            String newRankName = event.getMessage();

            FileConfiguration config =  ConfigUtil.getCustomConfig("config.yml");
            int maxSize = config.getInt("RankNameSize");
            if(newRankName.length() > maxSize){
                player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
                event.setCancelled(true);
            }

            TownData playerTown = TownDataStorage.get(player);
            String rankName = ChatData.getData().get(RANK_NAME);
            TownRank playerTownRank = playerTown.getRank(rankName);

            List<String> playerList = playerTownRank.getPlayers(playerTown.getID());
            for(String playerWithRoleUUID : playerList){
                Objects.requireNonNull(PlayerDataStorage.get(playerWithRoleUUID)).setRank(newRankName);
            }


            if(Objects.equals(playerTownRank.getName(), playerTown.getTownDefaultRank())){
                playerTown.setTownDefaultRank(newRankName);
            }

            playerTownRank.setName(playerTown.getID(),newRankName);

            if(!isSqlEnable()){ //Needed to update the Hashmap, not for the DB
                playerTown.addRankForRename(newRankName,playerTownRank);
                playerTown.removeRank(rankName);
            }


            Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> GuiManager2.OpenTownMenuRoleManager(player, newRankName));

            removePlayer(player);
            event.setCancelled(true);

        }

        if(chatData.getCategory() == CHANGE_DESCRIPTION){

            String newDesc = event.getMessage();
            String townId = chatData.getData().get(TOWN_ID);

            FileConfiguration config =  ConfigUtil.getCustomConfig("config.yml");
            int maxSize = config.getInt("TownDescSize");
            if(newDesc.length() > maxSize){
                player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
                event.setCancelled(true);
            }


            TownDataStorage.get(townId).setDescription(newDesc);
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_MESSAGE_IN_CHAT_SUCCESS.get());
            removePlayer(player);
            event.setCancelled(true);

        }

        if(chatData.getCategory() == CHANGE_TOWN_NAME){
            event.setCancelled(true);
            TownData town = TownDataStorage.get(chatData.getData().get(TOWN_ID));
            int townCost = Integer.parseInt(chatData.getData().get(COST));

            String newName = event.getMessage();

            FileConfiguration config =  ConfigUtil.getCustomConfig("config.yml");
            int maxSize = config.getInt("TownNameSize");

            if(newName.length() > maxSize){
                player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));

            }

            if(town.getBalance() <= townCost){
                player.sendMessage(ChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get());
            }

            TownUtil.renameTown(player, townCost, newName, town);
            removePlayer(player);
        }

        if(chatData.getCategory() == CHANGE_CHUNK_COLOR){
            event.setCancelled(true);
            removePlayer(player);

            TownData town = TownDataStorage.get(chatData.getData().get(TOWN_ID));

            String newColorCode = event.getMessage();
            if(!isValidColorCode(newColorCode)){
                player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT_ERROR.get());
                return;
            }
            int hexColorCode = hexColorToInt(newColorCode);
            town.setChunkColor(hexColorCode);
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT_SUCCESS.get());
        }
        if(chatData.getCategory() == CREATE_REGION){
            event.setCancelled(true);
            removePlayer(player);
            String regionName = event.getMessage();
            RegionUtil.createNewRegion(player, regionName);
        }
        if(chatData.getCategory() == REGION_DONATION){
            event.setCancelled(true);
            removePlayer(player);
            String stringAmount = event.getMessage();

            int amount;
            try {amount = Integer.parseInt(stringAmount);}
            catch (NumberFormatException e) {
                player.sendMessage(ChatUtils.getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
                throw new RuntimeException(e);
            }

            RegionUtil.donateToRegion(player, amount);
        }
    }
}
