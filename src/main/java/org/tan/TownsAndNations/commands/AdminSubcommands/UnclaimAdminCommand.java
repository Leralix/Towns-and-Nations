package org.tan.TownsAndNations.commands.AdminSubcommands;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.territoryData.RegionData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.DataClass.newChunkData.RegionClaimedChunk;
import org.tan.TownsAndNations.DataClass.newChunkData.TownClaimedChunk;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;

import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class UnclaimAdminCommand extends SubCommand {
    @Override
    public String getName() {
        return "unclaim";
    }


    @Override
    public String getDescription() {
        return Lang.ADMIN_UNCLAIM_DESC.get();
    }
    public int getArguments(){ return 1;}


    @Override
    public String getSyntax() {
        return "/tandebug unclaim";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args){
        if (args.length != 1){
            player.sendMessage(getTANString() +  Lang.CORRECT_SYNTAX_INFO.get());
            return;
        }

        Chunk chunk = player.getLocation().getChunk();
        if(!NewClaimedChunkStorage.isChunkClaimed(chunk)) {
            player.sendMessage(getTANString() + Lang.ADMIN_UNCLAIM_CHUNK_NOT_CLAIMED.get());
            return;
        }

        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.get(chunk);

        if(claimedChunk instanceof TownClaimedChunk townClaimedChunk){
            UnclaimChunkTown(player, chunk, townClaimedChunk);
        }
        else if (claimedChunk instanceof RegionClaimedChunk regionClaimedChunk){
            UnclaimChunkRegion(player, chunk, regionClaimedChunk);
        }


    }

    private void UnclaimChunkTown(Player player, Chunk chunk, TownClaimedChunk claimedChunk) {

        TownData townData = claimedChunk.getTown();
        NewClaimedChunkStorage.unclaimChunk(chunk);

        player.sendMessage(getTANString() + Lang.DEBUG_UNCLAIMED_CHUNK_SUCCESS_TOWN.get(townData.getName(),
                townData.getNumberOfClaimedChunk(),townData.getTownLevel().getChunkCap()));
    }

    private void UnclaimChunkRegion(Player player, Chunk chunk, RegionClaimedChunk regionClaimedChunk) {
        RegionData regionData = regionClaimedChunk.getRegion();

        NewClaimedChunkStorage.unclaimChunk(chunk);

        player.sendMessage(getTANString() + Lang.DEBUG_UNCLAIMED_CHUNK_SUCCESS_REGION.get(regionData.getName(),
                regionData.getNumberOfClaimedChunk()));

    }
}


