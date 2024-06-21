package org.tan.TownsAndNations.DataClass;

import org.tan.TownsAndNations.enums.ChunkPermissionType;
import org.tan.TownsAndNations.enums.MobChunkSpawnEnum;
import org.tan.TownsAndNations.enums.TownChunkPermission;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ClaimedChunkSettings {
    private final Map<ChunkPermissionType, TownChunkPermission> permissions;
    private final Map<String, UpgradeStatus> mobSpawnStorage = new HashMap<>();

    public ClaimedChunkSettings(){
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



    public UpgradeStatus getSpawnControl(MobChunkSpawnEnum mobType) {
        return getSpawnControl(mobType.name());
    }
    public UpgradeStatus getSpawnControl(String mobType) {
        if(!mobSpawnStorage.containsKey(mobType))
            mobSpawnStorage.put(mobType, new UpgradeStatus(false, false));
        return mobSpawnStorage.get(mobType);
    }
}
