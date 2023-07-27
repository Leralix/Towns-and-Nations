package org.tan.towns_and_nations.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.Lang.Lang;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;
import org.tan.towns_and_nations.storage.TownInviteDataStorage;

import java.util.ArrayList;

import static org.tan.towns_and_nations.utils.ChatUtils.getTANString;

public class JoinTownCommand extends SubCommand {
    @Override
    public String getName() {
        return "join";
    }


    @Override
    public String getDescription() {
        return Lang.TOWN_ACCEPT_INVITE_DESC.getTranslation();
    }

    public int getArguments() {
        return 99;
    }


    @Override
    public String getSyntax() {
        return "/tan join <Town ID>";
    }

    @Override
    public void perform(Player player, String[] args) {


        if (args.length == 1) {
            player.sendMessage(getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.getTranslation());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.getTranslation(getSyntax()));
        } else if (args.length == 2){

            String townID = args[1];

            ArrayList<String> townInvited = TownInviteDataStorage.checkInvitation(player.getUniqueId().toString());

            for (String town : townInvited){

                if(town.equals(townID)){
                    TownDataClass townClass = TownDataStorage.getTown(townID);
                    townClass.addPlayer(player.getUniqueId().toString());
                    PlayerDataClass playerStat = PlayerStatStorage.getStat(player);
                    playerStat.setTownId(townID);
                    playerStat.setRank(townClass.getTownDefaultRank());
                    player.sendMessage(getTANString() + Lang.TOWN_INVITATION_ACCEPTED_MEMBER_SIDE.getTranslation());
                    townClass.broadCastMessage(Lang.TOWN_INVITATION_ACCEPTED_TOWN_SIDE.getTranslation(player.getName()));
                    return;
                }

            }
        }
        else{
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.getTranslation());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.getTranslation(getSyntax()));
        }
    }
}



