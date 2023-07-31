package org.tan.towns_and_nations.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.DataClass.TownRank;
import org.tan.towns_and_nations.GUI.GuiManager2;
import org.tan.towns_and_nations.TownsAndNations;
import org.tan.towns_and_nations.storage.PlayerChatListenerStorage;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;
import org.tan.towns_and_nations.utils.ChatUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.Dictionary;
import java.util.Map;


public class ChatListener implements Listener {

    @EventHandler
    public void OnPlayerChat(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        String playerUUID = player.getUniqueId().toString();

        //Listener: Player create his city
        if(PlayerChatListenerStorage.checkIfPlayerIn("creationVille",playerUUID)){
            String townName = event.getMessage();

            Bukkit.broadcastMessage(ChatColor.GOLD + "[TAN]" + ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " has created his city: " + ChatColor.YELLOW + "" + ChatColor.BOLD + townName);

            PlayerChatListenerStorage.removePlayer("creationVille",player);
            PlayerDataClass sender = PlayerStatStorage.getStat(player.getUniqueId().toString());
            sender.removeFromBalance(100);
            TownDataStorage.newTown(townName,player);
            sender.setRank(TownDataStorage.getTown(sender).getTownDefaultRank());
            event.setCancelled(true);
        }

        if(PlayerChatListenerStorage.checkIfPlayerIn("donation",playerUUID)){

            String stringAmount = event.getMessage();
            PlayerDataClass sender = PlayerStatStorage.getStat(player.getUniqueId().toString());
            int amount = 0;
            try {
                amount = Integer.parseInt(stringAmount);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid Syntax for the amount of money");
                throw new RuntimeException(e);
            }
            if(sender.getBalance() < amount ){
                player.sendMessage("Not enough money");
                event.setCancelled(true);
                return;
            }
            if(amount <= 0 ){
                player.sendMessage("Money need to 1 or above");
                event.setCancelled(true);
                return;
            }
            TownDataClass playerTown = TownDataStorage.getTown(player);

            sender.removeFromBalance(amount);
            playerTown.getTreasury().addToBalance(amount);

            playerTown.getTreasury().addDonation(LocalDate.now(),player.getName(),amount);
            player.sendMessage(ChatUtils.getTANString() + "You sent " + ChatColor.YELLOW + amount + "$"+ ChatColor.WHITE +" to your town");
            PlayerChatListenerStorage.removePlayer("donation",player);

            event.setCancelled(true);
        }

        if(PlayerChatListenerStorage.checkIfPlayerIn("rankCreation",playerUUID)){
            PlayerChatListenerStorage.removePlayer("rankCreation",player);
            String rankName = event.getMessage();
            TownDataClass playerTown = TownDataStorage.getTown(player);
            playerTown.createTownRank(rankName);
            Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> GuiManager2.OpenTownMenuRoleManager(player, rankName));
            event.setCancelled(true);

        }

        if(PlayerChatListenerStorage.checkIfPlayerIn("rankRename",playerUUID)){
            Map<String,String> datas = PlayerChatListenerStorage.getPlayerData("rankRename",playerUUID);

            String newRankName = event.getMessage();
            TownDataClass playerTown = TownDataStorage.getTown(player);
            TownRank playerTownRank = playerTown.getRank(datas.get("rankName"));
            playerTownRank.setName(newRankName);

            playerTown.addTownRank(newRankName,playerTownRank);
            playerTown.removeTownRank(datas.get("rankName"));

            Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> GuiManager2.OpenTownMenuRoleManager(player, newRankName));

            PlayerChatListenerStorage.removePlayer("rankRename",player);
            event.setCancelled(true);

        }


    }
}
