package org.leralix.tan.commands.player;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.MapSettings;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public class ClaimCommand extends AbstractTerritoryClaimCommand {
    @Override
    public String getName() {
        return "claim";
    }

    @Override
    public String getDescription() {
        return Lang.CLAIM_CHUNK_COMMAND_DESC.getDefault();
    }

    public int getArguments() {
        return 1;
    }

    @Override
    public String getSyntax() {
        return "/tan claim <town/region/nation>";
    }

    @Override
    protected void onNoCoordinates(Player player, TerritoryData territoryData, LangType langType, String territoryArg, String[] args) {
        territoryData.claimChunk(player);
    }

    @Override
    protected void onCoordinates(Player player, TerritoryData territoryData, Chunk chunk, LangType langType, String territoryArg, String[] args) {
        territoryData.claimChunk(player, chunk);
        MapCommand.openMap(player, new MapSettings(args[0], territoryArg));
    }


}


