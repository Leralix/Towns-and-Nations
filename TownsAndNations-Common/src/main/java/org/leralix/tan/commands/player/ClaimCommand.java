package org.leralix.tan.commands.player;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.gui.scope.MapSettings;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class ClaimCommand extends AbstractTerritoryClaimCommand {

    public ClaimCommand(PlayerDataStorage playerDataStorage) {
        super(playerDataStorage);
    }

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
        var mapCommand = new MapCommand(TownsAndNations.getPlugin().getPlayerDataStorage());
        mapCommand.openMap(player, new MapSettings(args[0], territoryArg));
    }

    @Override
    protected void onEnd(Player player, TerritoryData territoryData, LangType langType, String territoryArg, String[] args) {

    }


}


