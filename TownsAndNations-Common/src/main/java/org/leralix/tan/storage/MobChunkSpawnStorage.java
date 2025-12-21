package org.leralix.tan.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.leralix.tan.enums.MobChunkSpawnEnum;

import java.util.EnumMap;
import java.util.Map;

public class MobChunkSpawnStorage {


    private final Map<MobChunkSpawnEnum, Integer> mobSpawnStorage;



    public MobChunkSpawnStorage(ConfigurationSection configurationSection) {

        this.mobSpawnStorage = new EnumMap<>(MobChunkSpawnEnum.class);

        for (MobChunkSpawnEnum mob : MobChunkSpawnEnum.values()) {
            int value = configurationSection.getInt(mob.name(), -1);
            mobSpawnStorage.put(mob, value);
        }
    }

    public int getMobSpawnCost(MobChunkSpawnEnum mob) {
        return mobSpawnStorage.get(mob);
    }

}
