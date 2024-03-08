package org.tan.TownsAndNations.commands.subcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.storage.Invitation.TownInviteDataStorage;

import java.util.ArrayList;
import java.util.List;

import static org.tan.TownsAndNations.enums.SoundEnum.GOOD;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;
import static org.tan.TownsAndNations.utils.TeamUtils.updateAllScoreboardColor;

public class JoinTownCommand extends SubCommand {
    @Override
    public String getName() {
        return "join";
    }


    @Override
    public String getDescription() {
        return Lang.TOWN_ACCEPT_INVITE_DESC.get();
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
            player.sendMessage(getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        } else if (args.length == 2){

            String townID = args[1];
            List<String> townInvited = TownInviteDataStorage.checkInvitation(player.getUniqueId().toString());

            if(townInvited == null){
                player.sendMessage(getTANString() + Lang.TOWN_INVITATION_NO_INVITATION.get());
                return;
            }

            for (String town : townInvited){

                if(town.equals(townID)){ //if the player is invited to the town

                    TownData townData = TownDataStorage.get(townID);
                    PlayerData playerData = PlayerDataStorage.get(player);

                    if(!townData.canAddMorePlayer()){
                        player.sendMessage(getTANString() + Lang.INVITATION_TOWN_FULL.get());
                        return;
                    }

                    townData.addPlayer(playerData);

                    player.sendMessage(getTANString() + Lang.TOWN_INVITATION_ACCEPTED_MEMBER_SIDE.get(townData.getName()));
                    townData.broadCastMessageWithSound(Lang.TOWN_INVITATION_ACCEPTED_TOWN_SIDE.get(player.getName()),
                            GOOD);

                    TownInviteDataStorage.removeInvitation(player,townData.getID());

                    updateAllScoreboardColor();
                    return;
                }

            }
            player.sendMessage(getTANString() + Lang.TOWN_INVITATION_NO_INVITATION.get());


        }
        else{
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }
}



