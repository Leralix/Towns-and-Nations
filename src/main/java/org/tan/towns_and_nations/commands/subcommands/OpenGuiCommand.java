package org.tan.towns_and_nations.commands.subcommands;


import org.bukkit.ChatColor;
import org.tan.towns_and_nations.GUI.GuiManager2;
import org.tan.towns_and_nations.commands.SubCommand;


import org.bukkit.entity.Player;


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
            player.sendMessage(ChatColor.GOLD + "[TAN]" + ChatColor.WHITE +  " Too many arguments");
            player.sendMessage(ChatColor.GOLD + "[TAN]" + ChatColor.WHITE +  " Correct Syntax: " + getSyntax());
        }

    }

    private void getOpeningGui(Player player) {
        GuiManager2.OpenMainMenu(player);
    }



}



