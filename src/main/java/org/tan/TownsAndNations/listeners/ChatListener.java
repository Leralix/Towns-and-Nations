package org.tan.TownsAndNations.listeners;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.tan.TownsAndNations.DataClass.RegionData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.DataClass.TownRank;
import org.tan.TownsAndNations.GUI.GuiManager2;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.*;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.RegionDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.*;

import java.util.*;

import static org.tan.TownsAndNations.TownsAndNations.isSqlEnable;
import static org.tan.TownsAndNations.enums.ChatCategory.*;
import static org.tan.TownsAndNations.enums.MessageKey.*;
import static org.tan.TownsAndNations.enums.SoundEnum.MINOR_LEVEL_UP;
import static org.tan.TownsAndNations.storage.PlayerChatListenerStorage.removePlayer;
import static org.tan.TownsAndNations.utils.EconomyUtil.getBalance;
import static org.tan.TownsAndNations.utils.EconomyUtil.removeFromBalance;
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

        else if(chatData.getCategory() == TOWN_DONATION){

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

        else if(chatData.getCategory() == RANK_CREATION){
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

        else if(chatData.getCategory() == RANK_RENAME){
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
                Objects.requireNonNull(PlayerDataStorage.get(playerWithRoleUUID)).setTownRank(newRankName);
            }


            if(Objects.equals(playerTownRank.getName(), playerTown.getTownDefaultRankName())){
                playerTown.setTownDefaultRank(newRankName);
            }

            playerTownRank.setName(playerTown.getID(),newRankName);

            if(!isSqlEnable()){ //Needed to update the Hashmap, not for the DB
                playerTown.addRank(newRankName,playerTownRank);
                playerTown.removeRank(rankName);
            }


            Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> GuiManager2.OpenTownMenuRoleManager(player, newRankName));

            removePlayer(player);
            event.setCancelled(true);

        }

        else if(chatData.getCategory() == CHANGE_TOWN_DESCRIPTION){

            String newDesc = event.getMessage();
            String townId = chatData.getData().get(TOWN_ID);

            FileConfiguration config =  ConfigUtil.getCustomConfig("config.yml");
            int maxSize = config.getInt("TownDescSize");
            if(newDesc.length() > maxSize){
                player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
                event.setCancelled(true);
            }


            TownDataStorage.get(townId).setDescription(newDesc);
            player.sendMessage(ChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get());
            removePlayer(player);
            event.setCancelled(true);

        }

        else if(chatData.getCategory() == CHANGE_TOWN_NAME){
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

        else if(chatData.getCategory() == CHANGE_REGION_NAME){
            event.setCancelled(true);
            RegionData regionData = RegionDataStorage.get(chatData.getData().get(REGION_ID));
            int regionCost = Integer.parseInt(chatData.getData().get(COST));

            String newName = event.getMessage();

            FileConfiguration config =  ConfigUtil.getCustomConfig("config.yml");
            int maxSize = config.getInt("RegionNameSize");

            if(newName.length() > maxSize){
                player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            }

            if(regionData.getBalance() <= regionCost){
                player.sendMessage(ChatUtils.getTANString() + Lang.REGION_NOT_ENOUGH_MONEY.get());
            }

            regionData.renameRegion(regionCost, newName);
            player.sendMessage(ChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get());
            removePlayer(player);
        }

        else if(chatData.getCategory() == CHANGE_CHUNK_COLOR){
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
        else if(chatData.getCategory() == CREATE_REGION){
            event.setCancelled(true);
            removePlayer(player);
            String regionName = event.getMessage();
            RegionDataStorage.createNewRegion(player, regionName);
        }
        else if(chatData.getCategory() == REGION_DONATION){
            event.setCancelled(true);
            removePlayer(player);
            String stringAmount = event.getMessage();

            int amount;
            try {amount = Integer.parseInt(stringAmount);}
            catch (NumberFormatException e) {
                player.sendMessage(ChatUtils.getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
                throw new RuntimeException(e);
            }

            int playerBalance = getBalance(player);
            PlayerChatListenerStorage.removePlayer(player);

            if(playerBalance < amount ){
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NOT_ENOUGH_MONEY.get());
                return;
            }
            if(amount <= 0 ){
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NEED_1_OR_ABOVE.get());
                return;
            }

            RegionData playerRegion = RegionDataStorage.get(player);

            removeFromBalance(player, amount);
            playerRegion.addBalance(amount);

            playerRegion.getDonationHistory().add(player.getName(),player.getUniqueId().toString(),amount);

            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_SEND_MONEY_TO_REGION.get(amount));
            SoundUtil.playSound(player, MINOR_LEVEL_UP);
        }
        else if(chatData.getCategory() == CHANGE_REGION_DESCRIPTION){
            String newDesc = event.getMessage();
            String regionID = chatData.getData().get(REGION_ID);

            FileConfiguration config =  ConfigUtil.getCustomConfig("config.yml");
            int maxSize = config.getInt("TownDescSize");
            if(newDesc.length() > maxSize){
                player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
                event.setCancelled(true);
            }


            RegionDataStorage.get(regionID).setDescription(newDesc);
            player.sendMessage(ChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get());
            removePlayer(player);
            event.setCancelled(true);
        }

    }
}
