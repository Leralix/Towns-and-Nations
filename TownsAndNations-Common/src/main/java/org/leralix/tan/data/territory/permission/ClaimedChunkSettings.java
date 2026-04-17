package org.leralix.tan.data.territory.permission;

import org.leralix.tan.data.territory.upgrade.UpgradeStatus;
import org.leralix.tan.storage.PermissionManager;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ClaimedChunkSettings implements IClaimedChunkSettings{

    /**
     * The permission manager to handle permissions related to players.
     */
    private PermissionManager newPermissionManager;

    /**
     * The mob spawn settings for the territory.
     */
    private Map<String, UpgradeStatus> mobSpawnStorage;

    /**
     * The general settings unrelated to players.
     */
    private Map<GeneralChunkSetting, Boolean> generalSettings;

    public ClaimedChunkSettings(PermissionGiven permissionGiven) {
        this.newPermissionManager = new PermissionManager(permissionGiven);
        this.mobSpawnStorage = new HashMap<>();
        this.generalSettings = new EnumMap<>(GeneralChunkSetting.class);


        for (GeneralChunkSetting setting : GeneralChunkSetting.values()) {
            generalSettings.put(setting, false);
        }
    }

    @Override
    public PermissionManager getChunkPermissions() {
        return newPermissionManager;
    }

    @Override
    public Map<GeneralChunkSetting, Boolean> getChunkSetting() {
        return generalSettings;
    }

    @Override
    public UpgradeStatus getSpawnControl(String mobType) {
        mobSpawnStorage.putIfAbsent(mobType, new UpgradeStatus(false, false));
        return mobSpawnStorage.get(mobType);
    }
}
