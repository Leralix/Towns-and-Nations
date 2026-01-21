package org.leralix.tan.commands.player;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.text.ChatChunkMapRenderer;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;

public class ClaimAreaCommand extends AbstractTerritoryClaimCommand {

    @Override
    public String getName() {
        return "claimarea";
    }

    @Override
    public String getDescription() {
        return Lang.CLAIM_CHUNK_COMMAND_DESC.getDefault();
    }

    public int getArguments() {
        return 2;
    }

    @Override
    public String getSyntax() {
        return "/tan claimarea <town/region/nation|kingdom>";
    }

    @Override
    protected void onNoCoordinates(Player player, TerritoryData territoryData, LangType langType, String territoryArg, String[] args) {
        TanChatUtils.message(player, Lang.LEFT_CLICK_TO_CLAIM.get(langType), SoundEnum.MINOR_GOOD);
    }

    @Override
    protected void onCoordinates(Player player, TerritoryData territoryData, Chunk chunk, LangType langType, String territoryArg, String[] args) {
        territoryData.claimChunk(player, chunk);
    }

    @Override
    protected void onEnd(Player player, TerritoryData territoryData, LangType langType, String territoryArg, String[] args) {
        openClaimAreaMap(player, territoryArg);
    }

    public static void openClaimAreaMap(Player player, String territoryArg) {
        LangType langType = LangType.of(player);
        int radius = 4;

        ChatChunkMapRenderer.sendChunkMap(
                player,
                radius,
                langType,
                (chunkX, chunkZ) -> "/tan claimarea " + territoryArg + " " + chunkX + " " + chunkZ,
                Collections.emptyMap()
        );
    }
}
