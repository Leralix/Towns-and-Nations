package org.leralix.tan.dataclass;

import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.dataclass.territory.permission.RelationPermission;
import org.leralix.tan.enums.MobChunkSpawnEnum;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.enums.permissions.GeneralChunkSetting;
import org.leralix.tan.storage.PermissionManager;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ClaimedChunkSettings {

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

    public ClaimedChunkSettings() {
        this.newPermissionManager = new PermissionManager(RelationPermission.TOWN);
        this.mobSpawnStorage = new HashMap<>();
        this.generalSettings = new EnumMap<>(GeneralChunkSetting.class);


        for (GeneralChunkSetting setting : GeneralChunkSetting.values()) {
            generalSettings.put(setting, false);
        }
    }

    public PermissionManager getChunkPermissions() {
        // Migrate old permission system if necessary
        if (newPermissionManager == null){
            newPermissionManager = new PermissionManager(RelationPermission.TOWN);
        }
        return newPermissionManager;
    }

    public Map<GeneralChunkSetting, Boolean> getChunkSetting() {
        return generalSettings;
    }

    public ChunkPermission getPermission(ChunkPermissionType type) {
        return getChunkPermissions().get(type);
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
