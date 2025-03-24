package org.leralix.tan.api.wrappers;

import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.tan.api.interfaces.TanClaimedChunk;
import org.tan.api.interfaces.TanRegion;
import org.tan.api.interfaces.TanTerritory;
import org.tan.api.interfaces.TanTown;

import java.util.Optional;
import java.util.UUID;

public class ClaimedChunkWrapper implements TanClaimedChunk {


    private final ClaimedChunk2 claimedChunk;

    private ClaimedChunkWrapper(ClaimedChunk2 claimedChunk) {
        this.claimedChunk = claimedChunk;
    }

    public static TanClaimedChunk of(ClaimedChunk2 claimedChunk) {
        if (claimedChunk == null) {
            return null;
        }
        return new ClaimedChunkWrapper(claimedChunk);
    }

    @Override
    public int getX() {
        return claimedChunk.getX();
    }

    @Override
    public int getZ() {
        return claimedChunk.getZ();
    }

    @Override
    public UUID getWorldUUID() {
        return UUID.fromString(claimedChunk.getWorldUUID());
    }

    @Override
    public String getworldName() {
        return claimedChunk.getWorld().getName();
    }

    @Override
    public Boolean isClaimed() {
        return claimedChunk.isClaimed();
    }

    @Override
    public Optional<UUID> getOwnerID() {
        if(claimedChunk.getOwnerID() == null){
            return Optional.empty();
        }
        return Optional.of(UUID.fromString(claimedChunk.getOwnerID()));
    }

    @Override
    public void unclaim() {
        NewClaimedChunkStorage.getInstance().unclaimChunk(claimedChunk);
    }

    @Override
    public boolean canClaim(TanTerritory tanTerritory) {
        return false;
    }

    @Override
    public void claim(TanTerritory tanTerritory) {
        if(tanTerritory == null){
            return;
        }
        if(tanTerritory instanceof TanTown){
            NewClaimedChunkStorage.getInstance().claimTownChunk(claimedChunk.getChunk(), tanTerritory.getID());
        }
        if(tanTerritory instanceof TanRegion){
            NewClaimedChunkStorage.getInstance().claimRegionChunk(claimedChunk.getChunk(), tanTerritory.getID());
        }
    }

    @Override
    public boolean canBeGriefByExplosion() {
        return claimedChunk.canExplosionGrief();
    }

    @Override
    public boolean canBeGriefByFire() {
        return claimedChunk.canFireGrief();
    }

    @Override
    public boolean canPvpHappen() {
        return claimedChunk.canPVPHappen();
    }
}
