package org.leralix.tan.utils.constants;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.enums.permissions.ChunkPermissionType;

public class EnabledPermissions {

  private final Set<ChunkPermissionType> enabledPermissions;

  private static EnabledPermissions instance;

  public static EnabledPermissions getInstance() {
    if (instance == null) {
      instance = new EnabledPermissions();
    }
    return instance;
  }

  private EnabledPermissions() {
    this.enabledPermissions = new HashSet<>();
    init();
  }

  public void init() {
    ConfigurationSection config =
        ConfigUtil.getCustomConfig(ConfigTag.MAIN).getConfigurationSection("enabledPermissions");

    if (config == null) {
      throw new IllegalStateException(
          "Configuration section 'enabledPermissions' is missing in the config file.");
    }

    for (ChunkPermissionType permission : ChunkPermissionType.values()) {
      if (!config.getBoolean(permission.name(), true)) {
        enabledPermissions.add(permission);
      }
    }
  }

  public boolean isPermissionDisabled(ChunkPermissionType permission) {
    return enabledPermissions.contains(permission);
  }
}
