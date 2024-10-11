package org.leralix.tan.commands.playersubcommand;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.commands.SubCommand;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.storage.invitation.RegionInviteDataStorage;
import org.leralix.tan.utils.SoundUtil;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.tan.enums.SoundEnum.GOOD;
import static org.leralix.tan.utils.ChatUtils.getTANString;

public class AcceptRegionSubjugationCommand extends SubCommand {

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
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
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

                town.setOverlord(newRegionID);
                region.addSubject(town.getID());
                town.broadCastMessage(getTANString() + Lang.TOWN_ACCEPTED_REGION_DIPLOMATIC_INVITATION.get(town.getName(), region.getName()));
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
