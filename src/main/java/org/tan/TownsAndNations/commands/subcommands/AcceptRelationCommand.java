package org.tan.TownsAndNations.commands.subcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.storage.Invitation.TownRelationConfirmStorage;
import org.tan.TownsAndNations.utils.RelationUtil;

import java.util.ArrayList;
import java.util.List;

import static org.tan.TownsAndNations.enums.SoundEnum.GOOD;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

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
            player.sendMessage(getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        } else if (args.length == 2){

            String OtherTownID = args[1];
            TownData otherTown = TownDataStorage.get(OtherTownID);
            TownData town = TownDataStorage.get(player);
            if(otherTown == null){
                return;
            }
            if(TownRelationConfirmStorage.checkInvitation(player.getUniqueId().toString(),OtherTownID)){

                TownRelation newRelation = TownRelationConfirmStorage.getRelation(player.getUniqueId().toString(),otherTown.getID());
                TownRelationConfirmStorage.removeInvitation(player.getUniqueId().toString(), otherTown.getID());

                if(newRelation == null){ // From negative to neutral
                    town.broadCastMessageWithSound(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.get(otherTown.getName(),"neutral"),
                            GOOD);
                    otherTown.broadCastMessageWithSound(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.get(town.getName(),"neutral"),
                            GOOD);
                    RelationUtil.removeTownRelation(town,otherTown);
                }
                else { // from neutral to positive
                    town.broadCastMessageWithSound(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.get(otherTown.getName(),newRelation.getColoredName()),
                            GOOD);
                    otherTown.broadCastMessageWithSound(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.get(town.getName(),newRelation.getColoredName()),
                            GOOD);

                    RelationUtil.addTownRelation(town,otherTown,newRelation);
                }
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


