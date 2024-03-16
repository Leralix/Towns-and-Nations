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

        String message = event.getMessage();
        event.setCancelled(true);


        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(ChatUtils.getTANString() + Lang.CANCELLED_ACTION.get());
            removePlayer(player);
            return;
        }

        switch (chatData.getCategory()) {

            case CREATE_CITY:
                int townPrice = Integer.parseInt(chatData.getData().get(COST));
                TownUtil.CreateTown(player, townPrice, message);
                break;

            case TOWN_DONATION:
                TownDonation(player, message);
                break;

            case RANK_CREATION:
                RankCreation(player, message);
                break;
            case RANK_RENAME:
                RenameRank(player, chatData, message);
                break;
            case CHANGE_TOWN_DESCRIPTION:
                ChangeTownDescription(player,chatData, message);
                break;
            case CHANGE_TOWN_NAME:
                ChangeTownName(player, chatData, message);
                break;
            case CHANGE_REGION_NAME:
                ChangeRegionName(player, chatData, message);
                break;
            case CHANGE_CHUNK_COLOR:
                ChangeChunkColor(player, chatData, message);
                break;
            case CREATE_REGION:
                RegionDataStorage.createNewRegion(player, message);
                break;
            case REGION_DONATION:
                RegionDonation(player, message);
                break;
            case CHANGE_REGION_DESCRIPTION:
                ChangeRegionDescription(player, chatData, message);
                break;    
        }
    }

    private void RankCreation(Player player, String message) {
        int maxNameSize = ConfigUtil.getCustomConfig("config.yml").getInt("RankNameSize");

        if(message.length() > maxNameSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxNameSize));
            return;
        }

        removePlayer(player);
        TownDataStorage.get(player).addRank(message);
        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> GuiManager2.OpenTownMenuRoleManager(player, message));
    }
    private void ChangeRegionDescription(Player player, PlayerChatListenerStorage.PlayerChatData chatData, String newDesc) {
        String regionID = chatData.getData().get(REGION_ID);

        FileConfiguration config =  ConfigUtil.getCustomConfig("config.yml");
        int maxSize = config.getInt("TownDescSize");
        if(newDesc.length() > maxSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        removePlayer(player);
        FileUtil.addLineToHistory(Lang.HISTORY_REGION_MESSAGE_CHANGED.get(player.getName(),RegionDataStorage.get(regionID).getName(),newDesc));
        RegionDataStorage.get(regionID).setDescription(newDesc);
        player.sendMessage(ChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get());
    }
    private void RegionDonation(Player player, String stringAmount) {

        Integer amount = parseStringToInt(stringAmount);
        if(amount == null){
            player.sendMessage(ChatUtils.getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
            return;
        }

        removePlayer(player);
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
    private void ChangeChunkColor(Player player, PlayerChatListenerStorage.PlayerChatData chatData, String newColorCode) {
        removePlayer(player);

        TownData town = TownDataStorage.get(chatData.getData().get(TOWN_ID));

        if(!isValidColorCode(newColorCode)){
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT_ERROR.get());
            return;
        }
        removePlayer(player);
        int hexColorCode = hexColorToInt(newColorCode);
        town.setChunkColor(hexColorCode);
        player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT_SUCCESS.get());
    }
    private void ChangeRegionName(Player player, PlayerChatListenerStorage.PlayerChatData chatData, String newName) {
        RegionData regionData = RegionDataStorage.get(chatData.getData().get(REGION_ID));
        int regionCost = Integer.parseInt(chatData.getData().get(COST));

        int maxSize = ConfigUtil.getCustomConfig("config.yml").getInt("RegionNameSize");

        if(newName.length() > maxSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        if(regionData.getBalance() <= regionCost){
            player.sendMessage(ChatUtils.getTANString() + Lang.REGION_NOT_ENOUGH_MONEY.get());
            return;
        }

        removePlayer(player);
        FileUtil.addLineToHistory(Lang.HISTORY_REGION_NAME_CHANGED.get(player.getName(),regionData.getName(),newName));
        regionData.renameRegion(regionCost, newName);
        player.sendMessage(ChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get());
    }
    private void ChangeTownName(Player player, PlayerChatListenerStorage.PlayerChatData chatData, String newName) {

        TownData town = TownDataStorage.get(chatData.getData().get(TOWN_ID));
        int townCost = Integer.parseInt(chatData.getData().get(COST));

        int maxSize = ConfigUtil.getCustomConfig("config.yml").getInt("TownNameSize");

        if(newName.length() > maxSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        if(town.getBalance() <= townCost){
            player.sendMessage(ChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get());
            return;
        }

        removePlayer(player);
        TownUtil.renameTown(player, townCost, newName, town);
        removePlayer(player);
    }
    private void ChangeTownDescription(Player player, PlayerChatListenerStorage.PlayerChatData chatData, String newDesc) {
        String townId = chatData.getData().get(TOWN_ID);
        int maxSize = ConfigUtil.getCustomConfig("config.yml").getInt("TownDescSize");

        if(newDesc.length() > maxSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        removePlayer(player);
        FileUtil.addLineToHistory(Lang.HISTORY_TOWN_MESSAGE_CHANGED.get(player.getName(),TownDataStorage.get(townId).getName(),newDesc));
        TownDataStorage.get(townId).setDescription(newDesc);
        player.sendMessage(ChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get());
    }
    private void TownDonation(Player player, String message) {
        Integer amount = parseStringToInt(message);
        if (amount == null) {
            player.sendMessage(ChatUtils.getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
            return;
        }
        removePlayer(player);
        DonateToTown(player, amount);
    }
    private void RenameRank(Player player, PlayerChatListenerStorage.PlayerChatData chatData, String newRankName) {

        FileConfiguration config =  ConfigUtil.getCustomConfig("config.yml");
        int maxSize = config.getInt("RankNameSize");
        if(newRankName.length() > maxSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        removePlayer(player);
        TownData playerTown = TownDataStorage.get(player);
        String rankName = chatData.getData().get(RANK_NAME);
        TownRank playerTownRank = playerTown.getRank(rankName);

        List<String> playerList = playerTownRank.getPlayers(playerTown.getID());
        for(String playerWithRoleUUID : playerList){
            PlayerDataStorage.get(playerWithRoleUUID).setTownRank(newRankName);
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
    }

    public static Integer parseStringToInt(String stringAmount) {
        if (stringAmount != null && stringAmount.matches("-?\\d+")) {
            return Integer.valueOf(stringAmount);
        } else {
            return null;
        }
    }

}


