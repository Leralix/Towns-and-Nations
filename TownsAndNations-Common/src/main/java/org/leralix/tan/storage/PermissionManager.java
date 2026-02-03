package org.leralix.tan.storage;

import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.permission.ChunkPermission;
import org.leralix.tan.data.territory.permission.ChunkPermissionType;
import org.leralix.tan.data.territory.permission.PermissionGiven;
import org.leralix.tan.data.territory.permission.RelationPermission;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.utils.constants.Constants;

import java.util.EnumMap;

public class PermissionManager {

    private final EnumMap<ChunkPermissionType, ChunkPermission> chunkPermissions;


    public PermissionManager(PermissionGiven permissionGiven) {
        this.chunkPermissions = new EnumMap<>(ChunkPermissionType.class);
        setAll(permissionGiven);
    }

    public void setAll(PermissionGiven permissionGiven) {
        this.chunkPermissions.clear();
        for (ChunkPermissionType type : ChunkPermissionType.values()) {


            RelationPermission relationPermission = switch (permissionGiven){
                case TOWN -> Constants.getChunkPermissionConfig().getTownPermission(type).defaultRelation();
                case REGION -> Constants.getChunkPermissionConfig().getRegionPermission(type).defaultRelation();
                case NATION -> Constants.getChunkPermissionConfig().getRegionPermission(type).defaultRelation();
                case PROPERTY -> Constants.getChunkPermissionConfig().getPropertiesPermission(type).defaultRelation();
            };

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
