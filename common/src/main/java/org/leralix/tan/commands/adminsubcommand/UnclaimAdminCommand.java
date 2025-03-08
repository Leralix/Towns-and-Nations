package org.leralix.tan.commands.adminsubcommand;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.RegionClaimedChunk;
import org.leralix.tan.dataclass.chunk.TownClaimedChunk;
import org.leralix.tan.lang.Lang;

import java.util.Collections;
import java.util.List;

public class UnclaimAdminCommand extends PlayerSubCommand {
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
        return Collections.emptyList();
    }
    @Override
    public void perform(Player player, String[] args){
        if (args.length != 1){
            player.sendMessage(TanChatUtils.getTANString() +  Lang.CORRECT_SYNTAX_INFO.get());
            return;
        }

        Chunk chunk = player.getLocation().getChunk();
        if(!NewClaimedChunkStorage.isChunkClaimed(chunk)) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.ADMIN_UNCLAIM_CHUNK_NOT_CLAIMED.get());
            return;
        }

        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.get(chunk);

        if(claimedChunk instanceof TownClaimedChunk townClaimedChunk){
            unclaimChunkTown(player, chunk, townClaimedChunk);
        }
        else if (claimedChunk instanceof RegionClaimedChunk regionClaimedChunk){
            unclaimChunkRegion(player, chunk, regionClaimedChunk);
        }
    }

    private void unclaimChunkTown(Player player, Chunk chunk, TownClaimedChunk claimedChunk) {
        TownData townData = claimedChunk.getTown();
        NewClaimedChunkStorage.unclaimChunk(chunk);

        player.sendMessage(TanChatUtils.getTANString() + Lang.DEBUG_UNCLAIMED_CHUNK_SUCCESS_TOWN.get(townData.getName(), townData.getNumberOfClaimedChunk(),townData.getLevel().getChunkCap()));
    }

    private void unclaimChunkRegion(Player player, Chunk chunk, RegionClaimedChunk regionClaimedChunk) {
        RegionData regionData = regionClaimedChunk.getRegion();
        NewClaimedChunkStorage.unclaimChunk(chunk);

        player.sendMessage(TanChatUtils.getTANString() + Lang.DEBUG_UNCLAIMED_CHUNK_SUCCESS_REGION.get(regionData.getName(), regionData.getNumberOfClaimedChunk()));

    }
}


