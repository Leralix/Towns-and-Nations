package org.leralix.tan.api.internal.managers;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.leralix.tan.data.chunk.IClaimedChunk;
import org.leralix.tan.data.chunk.LandmarkClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunkData;
import org.leralix.tan.data.chunk.WildernessChunkData;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.ChunkClaimValidator;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.storage.stored.ClaimStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.tan.api.getters.TanClaimManager;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.chunk.TanClaimedChunk;
import org.tan.api.interfaces.chunk.TanTerritoryChunk;
import org.tan.api.interfaces.territory.TanTerritory;

import java.util.Optional;

public class ClaimManager implements TanClaimManager {

    private final ClaimStorage claimStorage;
    private final PlayerDataStorage playerDataStorage;
    private final ChunkClaimValidator chunkClaimValidator;

    public ClaimManager(
            ClaimStorage claimStorage,
            PlayerDataStorage playerDataStorage,
            ChunkClaimValidator chunkClaimValidator
    ) {
        this.claimStorage = claimStorage;
        this.playerDataStorage = playerDataStorage;
        this.chunkClaimValidator = chunkClaimValidator;
    }

    @Override
    public boolean isBlockClaimed(Block block) {
        IClaimedChunk claimedChunk = claimStorage.get(block.getChunk());
        return claimedChunk.isClaimed();
    }

    @Override
    public TanClaimedChunk getClaimedChunk(Chunk chunk) {
        return claimStorage.get(chunk);
    }

    @Override
    public Optional<TanTerritory> getTerritoryOfBlock(Block block) {
        return getTerritoryOfChunk(block.getChunk());
    }

    public Optional<TanTerritory> getTerritoryOfChunk(Chunk chunk) {
        IClaimedChunk claimedChunk = claimStorage.get(chunk);

        return switch (claimedChunk){
            case WildernessChunkData ignored -> Optional.empty();
            case LandmarkClaimedChunk landmarkClaimedChunk -> Optional.ofNullable(landmarkClaimedChunk.getLandMark().getOwner());
            case TerritoryChunkData territoryChunk -> Optional.ofNullable(territoryChunk.getOwner());
            default -> throw new IllegalStateException("Unexpected chunk type : " + claimedChunk);
        };
    }

    @Override
    public Optional<TanTerritoryChunk> claimChunk(TanClaimedChunk chunk, TanPlayer tanPlayer, TanTerritory territory) {
        ITanPlayer playerData = playerDataStorage.get(tanPlayer.getID());
        Territory internalTerritory = (Territory) territory;

        var result = chunkClaimValidator.validate(internalTerritory, playerData, chunk.getChunk(), Constants.allowNonAdjacentChunksFor(territory));
        if(result.isSuccess()){
            return Optional.of(chunk.claim(territory));
        }
        return Optional.empty();
    }

    @Override
    public TanTerritoryChunk forceClaim(TanClaimedChunk chunk, TanTerritory territory) {
        return chunk.claim(territory);
    }
}
