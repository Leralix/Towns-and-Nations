package org.leralix.tan.utils.constants;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.leralix.tan.data.territory.permission.ChunkPermissionType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class WildernessRules {

    private final Map<String, Map<ChunkPermissionType, Boolean>> rules;


    public WildernessRules(ConfigurationSection wildernessRules) {
        rules = new HashMap<>();

        if(wildernessRules == null) {
            EnumMap<ChunkPermissionType, Boolean> defaultRules = new EnumMap<>(ChunkPermissionType.class);
            for(ChunkPermissionType permissionType : ChunkPermissionType.values()) {
                defaultRules.put(permissionType, true);
            }
            rules.put("default", defaultRules);
            return;
        }

        registerWorld(wildernessRules, "default");

        for(World world : Bukkit.getWorlds()) {
            String worldName = world.getName();
            if(!wildernessRules.contains(worldName))
                continue;

            registerWorld(wildernessRules, worldName);
        }
    }

    private void registerWorld(ConfigurationSection config, String worldName) {
        ConfigurationSection worldConfig = config.getConfigurationSection(worldName);
        Map<ChunkPermissionType, Boolean> worldRules = new EnumMap<>(ChunkPermissionType.class);

        for(ChunkPermissionType permissionType : ChunkPermissionType.values()) {
            worldRules.put(permissionType, worldConfig.getBoolean(permissionType.toString(), true));
        }
        rules.put(worldName, worldRules);
    }

    public Boolean canPlayerDoInWilderness(World world, ChunkPermissionType permissionType) {
        if(!rules.containsKey(world.getName())) {
            return rules.get("default").get(permissionType);
        }
        return rules.get(world.getName()).get(permissionType);
    }
}
