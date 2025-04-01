package org.leralix.tan.commands.player;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.MapSettings;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public class ClaimCommand extends PlayerSubCommand {
    @Override
    public String getName() {
        return "claim";
    }

    @Override
    public String getDescription() {
        return Lang.CLAIM_CHUNK_COMMAND_DESC.get();
    }
    public int getArguments(){ return 1;}

    @Override
    public String getSyntax() {
        return "/tan claim <town/region>";
    }
    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            suggestions.add("town");
            suggestions.add("region");
        }
        return suggestions;
    }
    @Override
    public void perform(Player player, String[] args){


        if (!(args.length == 2 || args.length == 4)) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.SYNTAX_ERROR.get());
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()) );
            return;
        }

        TerritoryData territoryData;

        if(args[1].equals("town")){
            territoryData = TownDataStorage.getInstance().get(player);
            if(territoryData == null){
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_TOWN.get());
                return;
            }
        }
        else if(args[1].equals("region")){
            territoryData = RegionDataStorage.getInstance().get(player);
            if(territoryData == null){
                player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_NO_REGION.get());
                return;
            }
        }
        else{
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()) );
            return;
        }

        if (args.length == 4) {
            int x = Integer.parseInt(args[2]);
            int z = Integer.parseInt(args[3]);
            Chunk chunk = player.getWorld().getChunkAt(x, z);
            territoryData.claimChunk(player,chunk);
            MapCommand.openMap(player, new MapSettings(args[0], args[1]));
        }
        else {
            territoryData.claimChunk(player);
        }
    }


}


