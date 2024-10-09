package org.leralix.tan.commands.playersubcommand;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.Lang.Lang;
import org.leralix.tan.commands.SubCommand;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.storage.DataStorage.TownDataStorage;
import org.leralix.tan.storage.invitation.TownRelationConfirmStorage;
import org.leralix.tan.utils.TerritoryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.leralix.tan.utils.ChatUtils.getTANString;

public class AcceptRelationCommand extends SubCommand {
    @Override
    public String getName() {
        return "accept";
    }

    @Override
    public String getDescription() {
        return Lang.TOWN_ACCEPT_RELATION_DESC.get();
    }
    public int getArguments(){ return 1;}


    @Override
    public String getSyntax() {
        return  "/tan accept <town name>";
    }
    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            suggestions.add("<town name>");
        }
        return suggestions;
    }

    @Override
    public void perform(Player player, String[] args){

        if (args.length == 1) {
            player.sendMessage(getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        } else if (args.length == 2){

            String territoryID = args[1];
            ITerritoryData otherTerritory = TerritoryUtil.getTerritory(territoryID);
            TownData town = TownDataStorage.get(player);
            if(otherTerritory == null){
                return;
            }
            if(TownRelationConfirmStorage.checkInvitation(player.getUniqueId().toString(),territoryID)){
                TownRelation newRelation = TownRelationConfirmStorage.getRelation(player.getUniqueId().toString(),otherTerritory.getID());
                TownRelationConfirmStorage.removeInvitation(player.getUniqueId().toString(), otherTerritory.getID());

                town.setRelation(otherTerritory, Objects.requireNonNullElse(newRelation, TownRelation.NEUTRAL));
            }
            else{
                player.sendMessage(getTANString() + Lang.TOWN_INVITATION_NO_INVITATION.get());
            }

        }
        else{
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }

}


