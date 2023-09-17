package org.tan.TownsAndNations.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.DataClass.TownRank;
import org.tan.TownsAndNations.GUI.GuiManager2;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.PlayerChatListenerStorage;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;

import java.time.LocalDate;
import java.util.*;


public class ChatListener implements Listener {

    @EventHandler
    public void OnPlayerChat(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        String playerUUID = player.getUniqueId().toString();

        PlayerChatListenerStorage.PlayerChatData chatData = PlayerChatListenerStorage.getPlayerData(playerUUID);

        if(chatData == null)
            return;

        //Listener: Player create his city
        if(chatData.getCategory() == PlayerChatListenerStorage.ChatCategory.CREATE_CITY){

            int townPrice = Integer.parseInt(chatData.getData().get("town cost"));
            PlayerData playerData = PlayerDataStorage.get(player);
            assert playerData != null;
            if(playerData.getBalance() < townPrice){
                player.sendMessage(Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.getTranslation(townPrice - playerData.getBalance()));
                PlayerChatListenerStorage.removePlayer(player);
                return;
            }

            String townName = event.getMessage();
            Bukkit.broadcastMessage(ChatUtils.getTANString() + Lang.TOWN_CREATE_SUCCESS_BROADCAST.getTranslation(player.getName(),townName));

            PlayerChatListenerStorage.removePlayer(player);
            PlayerData sender = PlayerDataStorage.get(player.getUniqueId().toString());


            assert sender != null;
            sender.removeFromBalance(townPrice);
            TownDataStorage.newTown(townName,player);
            sender.setRank(TownDataStorage.get(sender).getTownDefaultRank());
            event.setCancelled(true);
        }

        if(chatData.getCategory() == PlayerChatListenerStorage.ChatCategory.DONATION){

            String stringAmount = event.getMessage();
            PlayerData sender = PlayerDataStorage.get(player.getUniqueId().toString());
            int amount;
            try {
                amount = Integer.parseInt(stringAmount);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatUtils.getTANString() + Lang.PAY_INVALID_SYNTAX.getTranslation());
                throw new RuntimeException(e);
            }
            assert sender != null;
            if(sender.getBalance() < amount ){
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NOT_ENOUGH_MONEY.getTranslation());
                event.setCancelled(true);
                return;
            }
            if(amount <= 0 ){
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NEED_1_OR_ABOVE.getTranslation());
                event.setCancelled(true);
                return;
            }
            TownData playerTown = TownDataStorage.get(player);

            sender.removeFromBalance(amount);
            playerTown.getTreasury().addToBalance(amount);

            playerTown.getTreasury().addDonation(player.getName(),playerUUID,amount);
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_SEND_MONEY_TO_TOWN.getTranslation(amount));
            PlayerChatListenerStorage.removePlayer(player);

            event.setCancelled(true);
        }

        if(chatData.getCategory() == PlayerChatListenerStorage.ChatCategory.RANK_CREATION){
            PlayerChatListenerStorage.removePlayer(player);
            String rankName = event.getMessage();
            TownData playerTown = TownDataStorage.get(player);
            playerTown.createTownRank(rankName);
            Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> GuiManager2.OpenTownMenuRoleManager(player, rankName));
            event.setCancelled(true);

        }

        if(chatData.getCategory() == PlayerChatListenerStorage.ChatCategory.RANK_RENAME){
            PlayerChatListenerStorage.PlayerChatData ChatData = PlayerChatListenerStorage.getPlayerData(playerUUID);

            String newRankName = event.getMessage();
            TownData playerTown = TownDataStorage.get(player);
            String rankName = ChatData.getData().get("rankName");
            TownRank playerTownRank = playerTown.getRank(rankName);

            List<String> playerList = playerTownRank.getPlayers();
            for(String playerWithRoleUUID : playerList){
                Objects.requireNonNull(PlayerDataStorage.get(playerWithRoleUUID)).setRank(newRankName);
            }


            if(Objects.equals(playerTownRank.getName(), playerTown.getTownDefaultRank())){
                playerTown.setTownDefaultRank(newRankName);
            }

            playerTownRank.setName(newRankName);

            playerTown.addRank(newRankName,playerTownRank);
            playerTown.removeRank(rankName);

            Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> GuiManager2.OpenTownMenuRoleManager(player, newRankName));

            PlayerChatListenerStorage.removePlayer(player);
            event.setCancelled(true);

        }


        if(chatData.getCategory() == PlayerChatListenerStorage.ChatCategory.CHANGE_DESCRIPTION){

            String newDesc = event.getMessage();

            TownDataStorage.get(player).setDescription(newDesc);
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_MESSAGE_IN_CHAT_SUCCESS.getTranslation());
            event.setCancelled(true);

        }


    }
}
