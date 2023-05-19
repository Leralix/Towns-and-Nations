package org.tan.towns_and_nations.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.GUI.GuiManager;
import org.tan.towns_and_nations.commands.subcommands.OpenGuiCommand;
import org.tan.towns_and_nations.utils.PlayerChatListenerStorage;
import org.tan.towns_and_nations.utils.PlayerStatStorage;
import org.tan.towns_and_nations.utils.TownDataStorage;

public class GuiListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event){
        Material item = event.getCurrentItem().getType();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        Player player = (Player) event.getWhoClicked();
        boolean back = item.equals(Material.ARROW) && itemName.equals("Back");

        //Gui menu intro //////////
        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Towns and Nations")){

            //Kingdom
            if(item.equals(Material.PLAYER_HEAD) && itemName.equals("Kingdom"))
                event.getWhoClicked().sendMessage("Encore en dev");
            //Region
            if(item.equals(Material.PLAYER_HEAD) && itemName.equals("Region"))
                event.getWhoClicked().sendMessage("Encore en dev");
            //Town
            if(item.equals(Material.PLAYER_HEAD) && itemName.equals("Town"))
               GuiManager.OpenTownMenu((Player) event.getWhoClicked());
            //Profil
            if(item.equals(Material.PLAYER_HEAD) && itemName.equals("Profil"))
                GuiManager.OpenProfileMenu((Player) event.getWhoClicked());

            if(item.equals(Material.ARROW) && itemName.equals("Quit"))
                player.closeInventory();



            event.setCancelled(true);
        }
        //Gui menu Profil //////////
        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Profil")){

            if(back)
                GuiManager.OpenMainMenu((Player) event.getWhoClicked());

            event.setCancelled(true);
        }
        //Gui menu NoTown //////////
        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Town")){


            if(item.equals(Material.GRASS_BLOCK) && itemName.equals("Create new Town")){
                if(PlayerStatStorage.findStatUUID(player.getUniqueId().toString()).getBalance() < 100){
                    player.sendMessage("You don't have enough money");
                }
                else {
                    player.sendMessage("Write the name of the town in the chat");
                    player.closeInventory();
                    PlayerChatListenerStorage.addPlayer(player);
                }
            }

            if(item.equals(Material.ANVIL) && itemName.equals("Join a Town"))
                event.getWhoClicked().sendMessage("Encore en dev");

            if(back)
                GuiManager.OpenMainMenu((Player) event.getWhoClicked());

            event.setCancelled(true);
        }

        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Region")){
            event.setCancelled(true);
        }

        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Kingdom")){
            event.setCancelled(true);
        }


    }




}
