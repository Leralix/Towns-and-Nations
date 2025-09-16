package org.leralix.tan.storage;

import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.dataclass.territory.permission.RelationPermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;

import java.util.EnumMap;

public class PermissionManager {

    private final EnumMap<ChunkPermissionType, ChunkPermission> chunkPermissions;


    public PermissionManager(RelationPermission relationPermission) {
        this.chunkPermissions = new EnumMap<>(ChunkPermissionType.class);
        setAll(relationPermission);
    }

    public void setAll(RelationPermission relationPermission) {
        this.chunkPermissions.clear();
        for (ChunkPermissionType type : ChunkPermissionType.values()) {
            chunkPermissions.put(type, new ChunkPermission(relationPermission));
        }
    }

    public boolean canPlayerDo(TerritoryData territoryToCheck, ChunkPermissionType action, ITanPlayer tanPlayer) {
        return get(action).isAllowed(territoryToCheck, tanPlayer);
    }

    public ChunkPermission get(ChunkPermissionType type) {
        return chunkPermissions.getOrDefault(type, new ChunkPermission(RelationPermission.TOWN));
    }

    public void nextPermission(ChunkPermissionType type) {
        get(type).nextPermission();
    }
}
