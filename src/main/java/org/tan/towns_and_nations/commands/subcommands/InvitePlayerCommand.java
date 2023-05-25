package org.tan.towns_and_nations.commands.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.GUI.GuiManager;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.util.ChatUtil;
import org.tan.towns_and_nations.utils.PlayerStatStorage;
import org.tan.towns_and_nations.utils.TownDataStorage;
import org.tan.towns_and_nations.utils.TownInviteDataStorage;

public class InvitePlayerCommand extends SubCommand {
    @Override
    public String getName() {
        return "invite";
    }


    @Override
    public String getDescription() {
        return "invite to the town";
    }
    public int getArguments(){ return 2;}


    @Override
    public String getSyntax() {
        return "/tan invite <playerList>";
    }

    @Override
    public void perform(Player player, String[] args){

        if (args.length == 1){
            player.sendMessage("Not enough arguments");
            player.sendMessage("Correct Syntax: /tan invite <playerList>");

            getOpeningGui(player);
        }else if(args.length == 2){
            Player invite = Bukkit.getPlayer(args[1]);
            if(invite == null){
                player.sendMessage("Invalid name");
            }
            else{
                System.out.println("test");
                TownDataClass town = TownDataStorage.getTown(PlayerStatStorage.findStatUUID(player.getUniqueId().toString()).getTownId());
                System.out.println("test");
                TownInviteDataStorage.addInvitation(invite.getUniqueId().toString(),town.getTownId() );
                System.out.println("test");

                player.sendMessage("Invitation send");
                invite.sendMessage("You have been invited by "+ player.getName() + " to his town: " + town.getTownName());
                invite.sendMessage("To join his town, type /tan join "  + town.getTownId() + " to join the town: " + town.getTownName());

                ChatUtil.sendClickableCommand(invite,  "Or click here",  "tan join "  + town.getTownId());



            }
        }else {
            player.sendMessage("Too many arguments");
            player.sendMessage("Correct Syntax: /tan invite <playerList>");
        }

    }

    private void getOpeningGui(Player player) {
        GuiManager.OpenMainMenu(player);
    }





}


