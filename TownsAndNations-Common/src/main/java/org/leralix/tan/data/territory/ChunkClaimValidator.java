package org.leralix.tan.data.territory;

import org.bukkit.Chunk;
import org.leralix.tan.data.chunk.IClaimedChunk;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.data.upgrade.TerritoryStats;
import org.leralix.tan.data.upgrade.rewards.list.BiomeStat;
import org.leralix.tan.data.upgrade.rewards.numeric.ChunkCap;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.ClaimBlacklistStorage;
import org.leralix.tan.storage.stored.ClaimStorage;

public class ChunkClaimValidator {

    private final ClaimStorage claimStorage;

    public ChunkClaimValidator(ClaimStorage claimStorage) {
        this.claimStorage = claimStorage;
    }

    public ClaimChunkValidationResult validate(Territory territory, ITanPlayer tanPlayer, Chunk chunk, boolean ignoreAdjacent) {
        if (ClaimBlacklistStorage.cannotBeClaimed(chunk)) {
            return ClaimChunkValidationResult.failure(Lang.CHUNK_IS_BLACKLISTED.get());
        }

        if (!territory.doesPlayerHavePermission(tanPlayer, RolePermission.CLAIM_CHUNK)) {
            return ClaimChunkValidationResult.failure(Lang.PLAYER_NO_PERMISSION.get());
        }

        TerritoryStats territoryStats = territory.getNewLevel();
        int nbOfClaimedChunks = territory.getNumberOfClaimedChunk();

        if (!territoryStats.getStat(BiomeStat.class).canClaimBiome(chunk)) {
            return ClaimChunkValidationResult.failure(Lang.CHUNK_BIOME_NOT_ALLOWED.get());
        }

        if (!territoryStats.getStat(ChunkCap.class).canDoAction(nbOfClaimedChunks)) {
            return ClaimChunkValidationResult.failure(Lang.MAX_CHUNK_LIMIT_REACHED.get());
        }

        int cost = territory.getClaimCost();
        if (territory.getBalance() < cost) {
            return ClaimChunkValidationResult.failure(Lang.TERRITORY_NOT_ENOUGH_MONEY.get(territory.getColoredName(), Double.toString(cost - territory.getBalance())));
        }

        IClaimedChunk chunkData = claimStorage.get(chunk);
        if (!chunkData.canTerritoryClaim(territory)) {
            return ClaimChunkValidationResult.failure(Lang.CHUNK_ALREADY_CLAIMED_WARNING.get());
        }

        if(ignoreAdjacent){
            return ClaimChunkValidationResult.success();
        }
        else {
            return territory.isPositionClaimable(chunk, chunkData);
        }
    }
}
