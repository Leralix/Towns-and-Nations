package org.tan.TownsAndNations.commands.subcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.RegionData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.DataStorage.RegionDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.storage.Invitation.RegionInviteDataStorage;
import org.tan.TownsAndNations.utils.SoundUtil;

import java.util.ArrayList;
import java.util.List;

import static org.tan.TownsAndNations.enums.SoundEnum.GOOD;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class AcceptRegionRelationCommand extends SubCommand {

    @Override
    public String getName() {
        return "acceptregion";
    }

    @Override
    public String getDescription() {
        return Lang.TOWN_ACCEPT_RELATION_DESC.get();
    }
    public int getArguments(){ return 1;}


    @Override
    public String getSyntax() {
        return "/tan acceptregion <region name>";
    }
    @Override
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            suggestions.add("<region name>");
        }
        return suggestions;
    }

    @Override
    public void perform(Player player, String[] args){

        if (args.length == 1) {
            player.sendMessage(getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        } else if (args.length == 2){

            String newRegionID = args[1];
            RegionData region = RegionDataStorage.get(newRegionID);
            TownData town = TownDataStorage.get(player);
            if(region == null){
                return;
            }
            String playerID = player.getUniqueId().toString();
            if(RegionInviteDataStorage.checkInvitation(playerID,newRegionID)){

                RegionInviteDataStorage.removeInvitation(playerID, newRegionID);

                town.setRegion(newRegionID);
                region.addTown(town.getID());
                town.broadCastMessage(Lang.TOWN_ACCEPTED_REGION_DIPLOMATIC_INVITATION.get(town.getName(), region.getName()));
                SoundUtil.playSound(player,GOOD);
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
