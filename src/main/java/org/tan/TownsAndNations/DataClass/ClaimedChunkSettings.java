package org.tan.TownsAndNations.DataClass;

import org.tan.TownsAndNations.enums.TownChunkPermission;
import org.tan.TownsAndNations.enums.TownChunkPermissionType;

import java.util.EnumMap;
import java.util.Map;

public class ClaimedChunkSettings {
    private int numberOfClaimedChunk;
    private final Map<TownChunkPermissionType, TownChunkPermission> permissions;



    public ClaimedChunkSettings(){
        this.numberOfClaimedChunk = 0;
        this.permissions = new EnumMap<>(TownChunkPermissionType.class);
        for (TownChunkPermissionType type : TownChunkPermissionType.values()) {
            permissions.put(type, TownChunkPermission.TOWN);
        }
    }

    public TownChunkPermission getPermission(TownChunkPermissionType type) {
        return this.permissions.get(type);
    }

    public void nextPermission(TownChunkPermissionType type) {
        this.permissions.put(type, this.permissions.get(type).getNext());
    }
    //Old methods only here to not break old saves. Will be deleted in the future
    public int getNumberOfClaimedChunk() {
        return this.numberOfClaimedChunk;
    }
    //Old methods only here to not break old saves. Will be deleted in the future
    public void incrementNumberOfClaimedChunk() {
        this.numberOfClaimedChunk++;
    }
    //Old methods only here to not break old saves. Will be deleted in the future
    public void decreaseNumberOfClaimedChunk() {
        this.numberOfClaimedChunk--;
    }
}
