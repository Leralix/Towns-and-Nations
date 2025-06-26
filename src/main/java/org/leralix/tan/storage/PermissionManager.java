package org.leralix.tan.storage;

import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;

import java.util.EnumMap;

public class PermissionManager {

    private final EnumMap<ChunkPermissionType, ChunkPermission> chunkPermissions;


    public PermissionManager() {
        this.chunkPermissions = new EnumMap<>(ChunkPermissionType.class);
        reset();
    }

    public void reset() {
        this.chunkPermissions.clear();
        for (ChunkPermissionType type : ChunkPermissionType.values()) {
            this.chunkPermissions.put(type, new ChunkPermission());
        }
    }

    public boolean canPlayerDo(TerritoryData territoryToCheck, ChunkPermissionType action, ITanPlayer tanPlayer) {
        return chunkPermissions.getOrDefault(action, new ChunkPermission())
                .isAllowed(territoryToCheck, tanPlayer);
    }

    public ChunkPermission get(ChunkPermissionType type) {
        return chunkPermissions.getOrDefault(type, new ChunkPermission());
    }

    public void nextPermission(ChunkPermissionType type) {
        get(type).nextPermission();
    }

    public void clear() {
        for (ChunkPermissionType type : ChunkPermissionType.values()) {
            chunkPermissions.put(type, new ChunkPermission());
        }
    }
}
