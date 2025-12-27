package org.leralix.tan.utils.constants;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.leralix.tan.enums.permissions.ChunkPermissionType;

import java.util.EnumMap;
import java.util.Map;

public class PermissionAtWars {

    /**
     * Defines what permission players get when inside a chunk held by their side
     */
    private final Map<ChunkPermissionType, Boolean> permissionWhenAlly;
    /**
     * Defines what permission players get when inside a chunk held by the enemy
     */
    private final Map<ChunkPermissionType, Boolean> permissionWhenEnemy;


    public PermissionAtWars(ConfigurationSection attackersPermissions) {
        permissionWhenAlly = new EnumMap<>(ChunkPermissionType.class);
        permissionWhenEnemy = new EnumMap<>(ChunkPermissionType.class);

        for(ChunkPermissionType type : ChunkPermissionType.values()){
            permissionWhenAlly.put(type, attackersPermissions.getBoolean("heldByAlly." + type, true));
            permissionWhenEnemy.put(type, attackersPermissions.getBoolean("heldByEnemy." + type, true));
        }
    }

    public boolean canAllyDoAction(ChunkPermissionType type){
        return permissionWhenAlly.getOrDefault(type, true);
    }

    public boolean canEnemyDoAction(ChunkPermissionType type){
        return permissionWhenEnemy.getOrDefault(type, true);
    }
}
