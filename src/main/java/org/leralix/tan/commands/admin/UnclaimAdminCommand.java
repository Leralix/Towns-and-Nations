package org.leralix.tan.commands.admin;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.RegionClaimedChunk;
import org.leralix.tan.dataclass.chunk.TownClaimedChunk;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;
import java.util.List;

public class UnclaimAdminCommand extends PlayerSubCommand {
    @Override
    public String getName() {
        return "unclaim";
    }


    @Override
    public String getDescription() {
        return Lang.ADMIN_UNCLAIM_DESC.getDefault();
    }

    public int getArguments() {
        return 1;
    }


    @Override
    public String getSyntax() {
        return "/tanadmin unclaim";
    }

    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void perform(Player player, String[] args) {
        LangType langType = LangType.of(player);
        if (args.length != 1) {
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType));
            return;
        }

        Chunk chunk = player.getLocation().getChunk();
        if (!NewClaimedChunkStorage.getInstance().isChunkClaimed(chunk)) {
            TanChatUtils.message(player, Lang.ADMIN_UNCLAIM_CHUNK_NOT_CLAIMED.get(langType));
            return;
        }

        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(chunk);

        NewClaimedChunkStorage.getInstance().unclaimChunkAndUpdate(claimedChunk);
        if (claimedChunk instanceof TownClaimedChunk townClaimedChunk) {
            TownData townData = townClaimedChunk.getTown();
            TanChatUtils.message(player, Lang.DEBUG_UNCLAIMED_CHUNK_SUCCESS_TOWN.get(player, townData.getName(), Integer.toString(townData.getNumberOfClaimedChunk()), Integer.toString(townData.getLevel().getChunkCap())));

        } else if (claimedChunk instanceof RegionClaimedChunk regionClaimedChunk) {
            RegionData regionData = regionClaimedChunk.getRegion();
            TanChatUtils.message(player, Lang.DEBUG_UNCLAIMED_CHUNK_SUCCESS_REGION.get(player, regionData.getName(), Integer.toString(regionData.getNumberOfClaimedChunk())));
        }
    }

}


