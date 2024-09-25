package org.tan.TownsAndNations.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.DataStorage.RegionDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChunkUtil;

import java.util.ArrayList;
import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class ClaimCommand extends SubCommand {
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
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            suggestions.add("town");
            suggestions.add("region");
        }
        return suggestions;
    }
    @Override
    public void perform(Player player, String[] args){


        if (args.length < 2 || args.length == 3 || args.length > 4) {
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()) );
            return;
        }


        ITerritoryData territoryData;

        if(args[1].equals("town")){
            territoryData = TownDataStorage.get(player);
            if(territoryData == null){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.get());
                return;
            }
        }
        else if(args[1].equals("region")){
            territoryData = RegionDataStorage.get(player);
            if(territoryData == null){
                player.sendMessage(getTANString() + Lang.TOWN_NO_REGION.get());
                return;
            }
        }
        else{
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()) );
            return;
        }

        if (args.length == 4) {
            int x = Integer.parseInt(args[2]);
            int z = Integer.parseInt(args[3]);
            Chunk chunk = Bukkit.getWorld(player.getWorld().getUID()).getChunkAt(x, z);
            territoryData.claimChunk(player,chunk);
            MapCommand.openMap(player);
        }
        else {
            territoryData.claimChunk(player);
        }
    }


}


