package org.tan.TownsAndNations.commands.subcommands;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;
import org.tan.TownsAndNations.storage.TownInviteDataStorage;
import org.tan.TownsAndNations.utils.TeamUtils;

import java.util.ArrayList;
import java.util.List;

import static org.tan.TownsAndNations.enums.SoundEnum.GOOD;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;
import static org.tan.TownsAndNations.utils.TeamUtils.setScoreBoard;

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
        return 2;
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

                if(town.equals(townID)){ //if the player is invited to the town

                    TownData townData = TownDataStorage.get(townID);
                    PlayerData playerStat = PlayerDataStorage.get(player);

                    if(!townData.canAddMorePlayer()){
                        player.sendMessage(getTANString() + Lang.INVITATION_ERROR_PLAYER_TOWN_FULL.getTranslation());
                        return;
                    }


                    townData.addPlayer(player.getUniqueId().toString());
                    townData.getRank(townData.getTownDefaultRank()).addPlayer(player);

                    playerStat.setTownId(townID);
                    playerStat.setRank(townData.getTownDefaultRank());

                    player.sendMessage(getTANString() + Lang.TOWN_INVITATION_ACCEPTED_MEMBER_SIDE.getTranslation(townData.getName()));
                    townData.broadCastMessageWithSound(Lang.TOWN_INVITATION_ACCEPTED_TOWN_SIDE.getTranslation(player.getName()),
                            GOOD);

                    TownInviteDataStorage.removeInvitation(player,townData.getID());

                    setScoreBoard(player);
                    TeamUtils.updateColor();

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



