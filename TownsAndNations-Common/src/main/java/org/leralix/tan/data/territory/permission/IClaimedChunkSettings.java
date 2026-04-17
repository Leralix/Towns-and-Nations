package org.leralix.tan.data.territory.permission;

import org.leralix.tan.data.territory.upgrade.UpgradeStatus;
import org.leralix.tan.storage.PermissionManager;

import java.util.Map;

public interface IClaimedChunkSettings {
    PermissionManager getChunkPermissions();

    Map<GeneralChunkSetting, Boolean> getChunkSetting();

    default UpgradeStatus getSpawnControl(MobChunkSpawnEnum mobType) {
        return getSpawnControl(mobType.name());
    }

    UpgradeStatus getSpawnControl(String mobType);
}
