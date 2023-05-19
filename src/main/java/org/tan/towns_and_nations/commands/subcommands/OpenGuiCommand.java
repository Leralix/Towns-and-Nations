package org.tan.towns_and_nations.commands.subcommands;


import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.tan.towns_and_nations.GUI.GuiManager;
import org.tan.towns_and_nations.commands.SubCommand;
import com.mojang.authlib.GameProfile;


import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.UUID;


public class OpenGuiCommand extends SubCommand  {
    @Override
    public String getName() {
        return "gui";
    }


    @Override
    public String getDescription() {
        return "open the Town and Nation's gui";
    }
    public int getArguments(){ return 2;}


    @Override
    public String getSyntax() {
        return "/tan gui";
    }

    @Override
    public void perform(Player player, String[] args){
        if (args.length == 1){

            getOpeningGui(player);
        }else if(args.length > 1){
            player.sendMessage("Too many arguments");
            player.sendMessage("Correct Syntax: /tan gui");
        }

    }

    private void getOpeningGui(Player player) {
        GuiManager.OpenMainMenu(player);
    }



}



