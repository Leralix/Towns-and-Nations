package org.tan.TownsAndNations.DataClass;

import org.tan.TownsAndNations.enums.MobChunkSpawnEnum;
import org.tan.TownsAndNations.enums.TownChunkPermission;
import org.tan.TownsAndNations.enums.ChunkPermissionType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ClaimedChunkSettings {
    private int numberOfClaimedChunk;
    private final Map<ChunkPermissionType, TownChunkPermission> permissions;
    private final Map<String, UpgradeStatus> mobSpawnStorage = new HashMap<>();

    public ClaimedChunkSettings(){
        this.numberOfClaimedChunk = 0;
        this.permissions = new EnumMap<>(ChunkPermissionType.class);
        for (ChunkPermissionType type : ChunkPermissionType.values()) {
            permissions.put(type, TownChunkPermission.TOWN);
        }
    }

    public TownChunkPermission getPermission(ChunkPermissionType type) {
        return this.permissions.get(type);
    }

    public void nextPermission(ChunkPermissionType type) {
        this.permissions.put(type, this.permissions.get(type).getNext());
    }
    //Old methods only here to not break old saves. Will be deleted in the future
    public int getNumberOfClaimedChunk() {
        return this.numberOfClaimedChunk;
    }


    public UpgradeStatus getSpawnControl(MobChunkSpawnEnum mobType) {
        return getSpawnControl(mobType.name());
    }
    public UpgradeStatus getSpawnControl(String mobType) {
        if(!mobSpawnStorage.containsKey(mobType))
            mobSpawnStorage.put(mobType, new UpgradeStatus(false, false));
        return mobSpawnStorage.get(mobType);
    }
}
