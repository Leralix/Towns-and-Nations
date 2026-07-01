package org.leralix.tan.commands.player.claim;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.commands.player.MapCommand;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.gui.scope.MapSettings;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.tan.api.TanAPI;
import org.tan.api.getters.TanClaimManager;
import org.tan.api.interfaces.chunk.TanTerritoryChunk;

import java.util.Optional;

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
    protected void onNoCoordinates(Player player, ITanPlayer tanPlayer, Territory territoryData, LangType langType, String territoryArg, String[] args) {

        TanClaimManager claimManager = TanAPI.getInstance().getClaimManager();
        var tanChunk = claimManager.getClaimedChunk(player.getChunk());
        Optional<TanTerritoryChunk> potentialClaim =  claimManager.claimChunk(tanChunk, territoryData);
        potentialClaim.ifPresent(claim -> player.sendMessage(claim.getOwner().getName() + " has claimed the land"));

        territoryData.claimChunk(player, tanPlayer);
    }

    @Override
    protected void onCoordinates(Player player, ITanPlayer tanPlayer, Territory territoryData, Chunk chunk, LangType langType, String territoryArg, String[] args) {
        territoryData.claimChunk(player, tanPlayer, chunk);
        var mapCommand = new MapCommand(TownsAndNations.getPlugin().getPlayerDataStorage());
        mapCommand.openMap(player, new MapSettings(args[0], territoryArg));
    }

    @Override
    protected void onEnd(Player player, Territory territoryData, LangType langType, String territoryArg, String[] args) {

    }


}


