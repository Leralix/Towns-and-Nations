package org.leralix.tan.storage;

import java.util.HashMap;
import java.util.Map;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.enums.MobChunkSpawnEnum;

public class MobChunkSpawnStorage {

  private static final Map<String, MobChunkSpawnEnum> mobSpawnStorage = new HashMap<>();

  public static void init() {
    // Only load the enum values if they have been written in the config file
    for (MobChunkSpawnEnum mob : MobChunkSpawnEnum.values()) {

      if (ConfigUtil.getCustomConfig(ConfigTag.MAIN).get("CancelMobSpawnInTown." + mob.name())
          == null) continue;
      if (ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("CancelMobSpawnInTown." + mob.name())
          < 0) continue;
      mobSpawnStorage.put(mob.name(), mob);
    }
  }

  public static MobChunkSpawnEnum getMobSpawn(String name) {
    return mobSpawnStorage.get(name);
  }

  public static Map<String, MobChunkSpawnEnum> getMobSpawnStorage() {
    return mobSpawnStorage;
  }

  public static int getMobSpawnCost(MobChunkSpawnEnum mob) {
    return ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("CancelMobSpawnInTown." + mob.name());
  }
}
