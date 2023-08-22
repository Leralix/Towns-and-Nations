package org.tan.TownsAndNations.commands.subcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.*;

import java.util.ArrayList;
import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;
import static org.tan.TownsAndNations.utils.RelationUtil.addTownRelation;
import static org.tan.TownsAndNations.utils.RelationUtil.removeRelation;

public class AcceptRelationCommand extends SubCommand {
    @Override
    public String getName() {
        return "accept";
    }

    @Override
    public String getDescription() {
        return Lang.TOWN_ACCEPT_RELATION_DESC.getTranslation();
    }
    public int getArguments(){ return 1;}


    @Override
    public String getSyntax() {
        return "/tan accept <town name>";
    }
    @Override
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            suggestions.add("<town name>");
        }
        return suggestions;
    }

    @Override
    public void perform(Player player, String[] args){

        if (args.length == 1) {
            player.sendMessage(getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.getTranslation());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.getTranslation(getSyntax()));
        } else if (args.length == 2){

            String OtherTownID = args[1];
            TownData otherTown = TownDataStorage.getTown(OtherTownID);
            TownData town = TownDataStorage.getTown(player);
            System.out.println("1");
            if(otherTown == null){
                return;
            }
            System.out.println("2");
            if(TownRelationConfirmStorage.checkInvitation(player.getUniqueId().toString(),OtherTownID)){
                System.out.println("3");

                TownRelation newRelation = TownRelationConfirmStorage.getRelation(player.getUniqueId().toString(),otherTown.getID());
                TownRelationConfirmStorage.removeInvitation(player.getUniqueId().toString(), otherTown.getID());

                if(newRelation == null){
                    System.out.println("4");
                    town.broadCastMessage(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.getTranslation(otherTown.getName(),"neutral"));
                    otherTown.broadCastMessage(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.getTranslation(town.getName(),"neutral"));
                    removeRelation(town,otherTown);
                }
                else {
                    town.broadCastMessage(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.getTranslation(otherTown.getName(),newRelation.getColor() + newRelation.getName()));
                    otherTown.broadCastMessage(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.getTranslation(town.getName(),newRelation.getColor() + newRelation.getName()));

                    addTownRelation(town,otherTown,newRelation);
                }
            }
            else{
                player.sendMessage(getTANString() + Lang.TOWN_INVITATION_NO_INVITATION.getTranslation());
            }

        }
        else{
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.getTranslation());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.getTranslation(getSyntax()));
        }
    }

}


