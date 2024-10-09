package org.leralix.tan.dataclass;

import org.leralix.tan.enums.ChunkPermissionType;
import org.leralix.tan.enums.MobChunkSpawnEnum;
import org.leralix.tan.enums.TownChunkPermission;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ClaimedChunkSettings {
    private final Map<ChunkPermissionType, TownChunkPermission> permissions;
    private final Map<String, UpgradeStatus> mobSpawnStorage;

    public ClaimedChunkSettings(){
        this.permissions = new EnumMap<>(ChunkPermissionType.class);
        this.mobSpawnStorage = new HashMap<>();

        for (ChunkPermissionType type : ChunkPermissionType.values()) {
            permissions.put(type, TownChunkPermission.TOWN);
        }
    }
    public TownChunkPermission getPermission(ChunkPermissionType type) {
        if(!permissions.containsKey(type))
            permissions.put(type, TownChunkPermission.TOWN);
        return permissions.get(type);
    }
    public void nextPermission(ChunkPermissionType type) {
        this.permissions.put(type, permissions.get(type).getNext());
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
