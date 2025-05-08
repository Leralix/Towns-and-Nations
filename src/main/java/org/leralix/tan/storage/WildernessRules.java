package org.leralix.tan.storage;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.enums.permissions.ChunkPermissionType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class WildernessRules {

    private final Map<String, Map<ChunkPermissionType, Boolean>> rules;

    private static WildernessRules instance;

    private WildernessRules() {
        rules = new HashMap<>();
        init();
    }

    public static WildernessRules getInstance() {
        if(instance == null) {
            instance = new WildernessRules();
        }
        return instance;
    }

    public void init() {
        ConfigurationSection config = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getConfigurationSection("wildernessRules");

        if(config == null) {
            EnumMap<ChunkPermissionType, Boolean> defaultRules = new EnumMap<>(ChunkPermissionType.class);
            for(ChunkPermissionType permissionType : ChunkPermissionType.values()) {
                defaultRules.put(permissionType, true);
            }
            rules.put("default", defaultRules);
            return;
        }

        registerWorld(config, "default");

        for(World world : Bukkit.getWorlds()) {
            String worldName = world.getName();
            if(!config.contains(worldName))
                continue;

            registerWorld(config, worldName);
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
