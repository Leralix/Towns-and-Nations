package org.leralix.tan.commands.adminsubcommand;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.RegionClaimedChunk;
import org.leralix.tan.dataclass.chunk.TownClaimedChunk;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.commands.SubCommand;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;

import java.util.List;

import static org.leralix.tan.utils.ChatUtils.getTANString;

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
        return "/tanadmin unclaim";
    }
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
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


