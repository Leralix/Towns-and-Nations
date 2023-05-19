package org.tan.towns_and_nations.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.tan.towns_and_nations.GUI.GuiManager;
import org.tan.towns_and_nations.commands.subcommands.OpenGuiCommand;

public class GuiListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event){
        //Gui menu intro
        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Debug Item Menu")){
            if(event.getCurrentItem().getType().equals(Material.PLAYER_HEAD) && event.getCurrentItem().getItemMeta().getDisplayName().equals("Kingdom"))
                event.getWhoClicked().sendMessage("Encore en dev");

            if(event.getCurrentItem().getType().equals(Material.PLAYER_HEAD) && event.getCurrentItem().getItemMeta().getDisplayName().equals("Region"))
                event.getWhoClicked().sendMessage("Encore en dev");

            if(event.getCurrentItem().getType().equals(Material.PLAYER_HEAD) && event.getCurrentItem().getItemMeta().getDisplayName().equals("Town"))
                event.getWhoClicked().sendMessage("Encore en dev");

            if(event.getCurrentItem().getType().equals(Material.PLAYER_HEAD) && event.getCurrentItem().getItemMeta().getDisplayName().equals("Profil"))
                GuiManager.OpenProfileMenu((Player) event.getWhoClicked());


            event.setCancelled(true);
        }

    }




}
