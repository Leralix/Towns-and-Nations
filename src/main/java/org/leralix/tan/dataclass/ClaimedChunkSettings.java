package org.leralix.tan.dataclass;

import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.enums.ChunkPermissionType;
import org.leralix.tan.enums.MobChunkSpawnEnum;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ClaimedChunkSettings {
    private Map<ChunkPermissionType, ChunkPermission> newPermission;
    private Map<String, UpgradeStatus> mobSpawnStorage;

    public ClaimedChunkSettings(){
        this.newPermission = new EnumMap<>(ChunkPermissionType.class);
        this.mobSpawnStorage = new HashMap<>();

        for (ChunkPermissionType type : ChunkPermissionType.values()) {
            newPermission.put(type, new ChunkPermission());
        }
    }

    public Map<ChunkPermissionType, ChunkPermission> getNewPermission() {
        if(newPermission == null)
            newPermission = new EnumMap<>(ChunkPermissionType.class);
        return newPermission;
    }

    public ChunkPermission getPermission(ChunkPermissionType type) {
        var map =  getNewPermission();

        if(map.containsKey(type))
            map.put(type, new ChunkPermission());

        return map.get(type);
    }
    public void nextPermission(ChunkPermissionType type) {
        getNewPermission().get(type).nextPermission();
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
