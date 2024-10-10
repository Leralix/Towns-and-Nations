package org.leralix.tan.commands.playersubcommand;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.Lang.Lang;
import org.leralix.tan.commands.SubCommand;
import org.leralix.tan.storage.DataStorage.PlayerDataStorage;
import org.leralix.tan.storage.DataStorage.TownDataStorage;
import org.leralix.tan.storage.invitation.TownInviteDataStorage;
import org.leralix.tan.utils.TeamUtils;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.tan.enums.SoundEnum.GOOD;
import static org.leralix.tan.utils.ChatUtils.getTANString;
import static org.leralix.tan.utils.TeamUtils.updateAllScoreboardColor;

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
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
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

            if(!TownInviteDataStorage.isInvited(player.getUniqueId().toString(),townID)){
                player.sendMessage(getTANString() + Lang.TOWN_INVITATION_NO_INVITATION.get());
                return;
            }

            TownData townData = TownDataStorage.get(townID);
            PlayerData playerData = PlayerDataStorage.get(player);

            if(townData.isFull()){
                player.sendMessage(getTANString() + Lang.INVITATION_TOWN_FULL.get());
                return;
            }

            townData.addPlayer(playerData);

            player.sendMessage(getTANString() + Lang.TOWN_INVITATION_ACCEPTED_MEMBER_SIDE.get(townData.getName()));
            townData.broadCastMessageWithSound(Lang.TOWN_INVITATION_ACCEPTED_TOWN_SIDE.get(playerData.getName()),
                    GOOD);

            TownInviteDataStorage.removeInvitation(player,townData.getID());

            TeamUtils.updateAllScoreboardColor();

        }
        else{
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }
}



