package org.tan.TownsAndNations.commands.subcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;
import org.tan.TownsAndNations.storage.TownInviteDataStorage;

import java.util.ArrayList;
import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

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
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            suggestions.add("<Town ID>");
        }
        return suggestions;
    }

    @Override
    public void perform(Player player, String[] args) {


        if (args.length == 1) {
            player.sendMessage(getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.getTranslation());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.getTranslation(getSyntax()));
        } else if (args.length == 2){

            String townID = args[1];
            List<String> townInvited = TownInviteDataStorage.checkInvitation(player.getUniqueId().toString());

            if(townInvited == null){
                player.sendMessage(getTANString() + Lang.TOWN_INVITATION_NO_INVITATION.getTranslation());
                return;
            }

            for (String town : townInvited){

                if(town.equals(townID)){

                    TownData townClass = TownDataStorage.getTown(townID);
                    PlayerData playerStat = PlayerDataStorage.getStat(player);

                    townClass.addPlayer(player.getUniqueId().toString());
                    townClass.getRank(townClass.getTownDefaultRank()).addPlayer(player);

                    playerStat.setTownId(townID);
                    playerStat.setRank(townClass.getTownDefaultRank());

                    player.sendMessage(getTANString() + Lang.TOWN_INVITATION_ACCEPTED_MEMBER_SIDE.getTranslation(townClass.getName()));
                    townClass.broadCastMessage(Lang.TOWN_INVITATION_ACCEPTED_TOWN_SIDE.getTranslation(player.getName()));

                    TownInviteDataStorage.removeInvitation(player.getUniqueId().toString(),townClass.getID());

                    return;
                }

            }
            player.sendMessage(getTANString() + Lang.TOWN_INVITATION_NO_INVITATION.getTranslation());


        }
        else{
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.getTranslation());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.getTranslation(getSyntax()));
        }
    }
}



