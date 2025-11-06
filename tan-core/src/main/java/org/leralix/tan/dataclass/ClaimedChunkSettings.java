package org.leralix.tan.dataclass;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.dataclass.territory.permission.RelationPermission;
import org.leralix.tan.enums.MobChunkSpawnEnum;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.enums.permissions.GeneralChunkSetting;

public class ClaimedChunkSettings {
  private Map<ChunkPermissionType, ChunkPermission> newPermission;
  private Map<String, UpgradeStatus> mobSpawnStorage;
  private Map<GeneralChunkSetting, Boolean> generalSettings;

  public ClaimedChunkSettings() {
    this.newPermission = new EnumMap<>(ChunkPermissionType.class);
    this.mobSpawnStorage = new HashMap<>();
    this.generalSettings = new EnumMap<>(GeneralChunkSetting.class);

    for (ChunkPermissionType type : ChunkPermissionType.values()) {
      newPermission.put(type, new ChunkPermission(RelationPermission.TOWN));
    }
    for (GeneralChunkSetting setting : GeneralChunkSetting.values()) {
      generalSettings.put(setting, false);
    }
  }

  public Map<ChunkPermissionType, ChunkPermission> getChunkPermissions() {
    if (newPermission == null) newPermission = new EnumMap<>(ChunkPermissionType.class);
    return newPermission;
  }

  public Map<GeneralChunkSetting, Boolean> getChunkSetting() {
    return generalSettings;
  }

  public ChunkPermission getPermission(ChunkPermissionType type) {
    var map = getChunkPermissions();
    map.putIfAbsent(type, new ChunkPermission(RelationPermission.TOWN));
    return map.get(type);
  }

  public void nextPermission(ChunkPermissionType type) {
    getChunkPermissions().get(type).nextPermission();
  }

  public UpgradeStatus getSpawnControl(MobChunkSpawnEnum mobType) {
    return getSpawnControl(mobType.name());
  }

  public UpgradeStatus getSpawnControl(String mobType) {
    mobSpawnStorage.putIfAbsent(mobType, new UpgradeStatus(false, false));
    return mobSpawnStorage.get(mobType);
  }
}
