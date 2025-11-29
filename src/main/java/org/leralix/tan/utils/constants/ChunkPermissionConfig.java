package org.leralix.tan.utils.constants;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.leralix.tan.dataclass.territory.permission.RelationPermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;

import java.util.EnumMap;
import java.util.List;

public class ChunkPermissionConfig {

    private final EnumMap<ChunkPermissionType, SpecificChunkConfig> permissionsForTowns;
    private final EnumMap<ChunkPermissionType, SpecificChunkConfig> permissionsForRegions;
    private final EnumMap<ChunkPermissionType, SpecificChunkConfig> permissionsForProperties;

    /**
     * Default permission if init fail to register a player
     */
    private final SpecificChunkConfig DEFAULT_PERMISSION = new SpecificChunkConfig(RelationPermission.TOWN, false);

    public ChunkPermissionConfig(ConfigurationSection configurationSection){
        this.permissionsForTowns = new EnumMap<>(ChunkPermissionType.class);
        this.permissionsForRegions = new EnumMap<>(ChunkPermissionType.class);
        this.permissionsForProperties = new EnumMap<>(ChunkPermissionType.class);


        if(configurationSection == null){
            return;
        }

        populatePermissions(permissionsForTowns, configurationSection.getConfigurationSection("townPermissions"));
        populatePermissions(permissionsForRegions, configurationSection.getConfigurationSection("regionPermissions"));
        populatePermissions(permissionsForProperties, configurationSection.getConfigurationSection("propertyPermissions"));

    }

    private void populatePermissions(EnumMap<ChunkPermissionType, SpecificChunkConfig> permissionMap, @Nullable ConfigurationSection townPermissions) {
        if(townPermissions == null){
            return;
        }
        for(String key : townPermissions.getKeys(false)){
            ChunkPermissionType chunkPermissionType = ChunkPermissionType.valueOf(key);
            List<String> information = townPermissions.getStringList(key);
            SpecificChunkConfig specificChunkConfig = getSpecificChunkConfig(information);
            permissionMap.put(chunkPermissionType, specificChunkConfig);
        }
    }

    private SpecificChunkConfig getSpecificChunkConfig(List<String> information) {
        if(information.isEmpty()){
            return DEFAULT_PERMISSION;
        }
        String relationPermissionString = information.getFirst();
        RelationPermission relationPermission;
        try {
            relationPermission = RelationPermission.valueOf(relationPermissionString);
        }
        catch (IllegalArgumentException e){
            relationPermission = RelationPermission.TOWN;
        }

        if(information.size() < 2){
            return new SpecificChunkConfig(relationPermission, false);
        }
        else {
            if(information.get(1).equals("locked")){
                return new SpecificChunkConfig(relationPermission, true);
            }
            else {
                return new SpecificChunkConfig(relationPermission, false);
            }
        }

    }


    public SpecificChunkConfig getTownPermission(ChunkPermissionType type){
        return permissionsForTowns.getOrDefault(type, DEFAULT_PERMISSION);
    }

    public SpecificChunkConfig getRegionPermission(ChunkPermissionType type){
        return permissionsForRegions.getOrDefault(type, DEFAULT_PERMISSION);
    }

    public SpecificChunkConfig getPropertiesPermission(ChunkPermissionType type){
        return permissionsForProperties.getOrDefault(type, DEFAULT_PERMISSION);
    }

}
