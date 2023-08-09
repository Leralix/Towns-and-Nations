package org.tan.towns_and_nations.commands.subcommands;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.DataClass.ClaimedChunkSettings;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.Lang.Lang;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.enums.TownRelation;
import org.tan.towns_and_nations.storage.*;

import java.util.ArrayList;

import static org.tan.towns_and_nations.utils.ChatUtils.getTANString;
import static org.tan.towns_and_nations.utils.RelationUtil.addTownRelation;
import static org.tan.towns_and_nations.utils.RelationUtil.removeRelation;

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
    public void perform(Player player, String[] args){

        if (args.length == 1) {
            player.sendMessage(getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.getTranslation());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.getTranslation(getSyntax()));
        } else if (args.length == 2){

            String OtherTownID = args[1];
            TownDataClass otherTown = TownDataStorage.getTown(OtherTownID);
            TownDataClass town = TownDataStorage.getTown(player);
            System.out.println("1");
            if(otherTown == null){
                return;
            }
            System.out.println("2");
            if(TownRelationConfirmStorage.checkInvitation(player.getUniqueId().toString(),OtherTownID)){
                System.out.println("3");

                TownRelation newRelation = TownRelationConfirmStorage.getRelation(player.getUniqueId().toString(),otherTown.getTownId());
                TownRelationConfirmStorage.removeInvitation(player.getUniqueId().toString(), otherTown.getTownId());

                if(newRelation == null){
                    System.out.println("4");
                    town.broadCastMessage(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.getTranslation(otherTown.getTownName(),"neutral"));
                    otherTown.broadCastMessage(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.getTranslation(town.getTownName(),"neutral"));
                    removeRelation(town,otherTown);
                }
                else {
                    town.broadCastMessage(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.getTranslation(otherTown.getTownName(),newRelation.getColor() + newRelation.getName()));
                    otherTown.broadCastMessage(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.getTranslation(town.getTownName(),newRelation.getColor() + newRelation.getName()));

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


