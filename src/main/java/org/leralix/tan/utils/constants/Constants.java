package org.leralix.tan.utils.constants;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.chunk.ChunkType;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.war.legacy.GriefAllowed;

import java.util.List;

public class Constants {

    //Config
    private static boolean onlineMode;

    //Economy
    private static double startingBalance;

    //Cosmetic
    /**
     * If Enabled, player username will have a 3 letter prefix of their town name.
     * This option cannot be enabled with allowColorCodes.
     */
    private static boolean allowTownTag;

    //Territory
    private static boolean displayTerritoryColor;
    private static boolean enableNation;
    private static boolean enableRegion;
    private static int changeTownNameCost;
    private static int changeRegionNameCost;
    private static int nbDigits;
    private static boolean worldGuardOverrideWilderness;
    private static boolean worldGuardOverrideTown;
    private static boolean worldGuardOverrideRegion;
    private static boolean worldGuardOverrideLandmark;

    private static double fortCost;
    private static double fortProtectionRadius;
    private static double fortCaptureRadius;
    private static boolean useAsOutpost;

    private static int propertySignMargin;

    //Wars
    private static long warDuration;
    private static List<String> blacklistedCommandsDuringAttacks;
    private static int nbChunkToCaptureMax;

    //Claims
    private static GriefAllowed explosionGriefStatus;
    private static GriefAllowed fireGriefStatus;
    private static GriefAllowed pvpEnabledStatus;
    private static GriefAllowed mobGriefStatus;


    private static boolean allowNonAdjacentChunksForTown;
    private static boolean allowNonAdjacentChunksForRegion;
    private static double claimLandmarkCost;
    private static boolean landmarkClaimRequiresEncirclement;

    private static final String ALWAYS = "ALWAYS";

    public static void init() {

        FileConfiguration config = ConfigUtil.getCustomConfig(ConfigTag.MAIN);

        onlineMode = config.getBoolean("onlineMode", true);

        //Economy
        startingBalance = config.getDouble("StartingMoney", 100.0);

        //Cosmetic
        allowTownTag = config.getBoolean("EnablePlayerPrefix",false);

        //Territory
        displayTerritoryColor = config.getBoolean("displayTerritoryNameWithOwnColor", false);
        enableNation = config.getBoolean("EnableKingdom", true);
        enableRegion = config.getBoolean("EnableRegion", true);
        changeTownNameCost = config.getInt("ChangeTownNameCost", 1000);
        changeRegionNameCost = config.getInt("ChangeRegionNameCost", 1000);
        nbDigits = config.getInt("DecimalDigits", 2);
        worldGuardOverrideWilderness = config.getBoolean("worldguard_override_wilderness", true);
        worldGuardOverrideTown = config.getBoolean("worldguard_override_town", true);
        worldGuardOverrideRegion = config.getBoolean("worldguard_override_region", true);
        worldGuardOverrideLandmark = config.getBoolean("worldguard_override_landmark", true);
        claimLandmarkCost = config.getDouble("claimLandmarkCost", 500.0);
        if (claimLandmarkCost < 0.0) {
            claimLandmarkCost = 0.0;
        }
        landmarkClaimRequiresEncirclement = config.getBoolean("landmarkEncircleToCapture", true);

        //forts
        ConfigurationSection fortsSection = config.getConfigurationSection("Forts");
        if (fortsSection != null) {
            fortCost = fortsSection.getDouble("fortCost", 1000.0);
            fortProtectionRadius = fortsSection.getDouble("fortProtectionRadius", 50.0);
            fortCaptureRadius = fortsSection.getDouble("fortCaptureRadius", 10.0);
            useAsOutpost = fortsSection.getBoolean("useAsOutpost", true);
        }

        //Attacks
        blacklistedCommandsDuringAttacks = config.getStringList("BlacklistedCommandsDuringAttacks");
        nbChunkToCaptureMax = config.getInt("MaximumChunkConquer", 0);
        if (nbChunkToCaptureMax == 0) {
            nbChunkToCaptureMax = Integer.MAX_VALUE;
        }
        //Claims

        explosionGriefStatus = GriefAllowed.valueOf(config.getString("explosionGrief", ALWAYS));
        fireGriefStatus = GriefAllowed.valueOf(config.getString("fireGrief", ALWAYS));
        pvpEnabledStatus = GriefAllowed.valueOf(config.getString("pvpEnabledInClaimedChunks", ALWAYS));
        mobGriefStatus = GriefAllowed.valueOf(config.getString("mobGrief", ALWAYS));

        allowNonAdjacentChunksForRegion = config.getBoolean("RegionAllowNonAdjacentChunks", false);
        allowNonAdjacentChunksForTown = config.getBoolean("TownAllowNonAdjacentChunks", false);

    }

    public static boolean onlineMode() {
        return onlineMode;
    }

    public static boolean displayTerritoryColor() {
        return displayTerritoryColor;
    }

    public static boolean enableNation() {
        return enableNation;
    }

    public static boolean enableRegion() {
        return enableRegion;
    }

    public static int getChangeTerritoryNameCost(TerritoryData territoryData) {
        if (territoryData instanceof TownData) {
            return changeTownNameCost;
        }
        if (territoryData instanceof RegionData) {
            return changeRegionNameCost;
        }
        return changeTownNameCost;
    }

    public static int getNbDigits() {
        return nbDigits;
    }

    public static boolean isWorldGuardEnabledFor(ChunkType chunkType) {
        return switch (chunkType) {
            case WILDERNESS -> worldGuardOverrideWilderness;
            case TOWN -> worldGuardOverrideTown;
            case REGION -> worldGuardOverrideRegion;
            case LANDMARK -> worldGuardOverrideLandmark;
        };
    }

    public static double getClaimLandmarkCost() {
        return claimLandmarkCost;
    }

    public static boolean isLandmarkClaimRequiresEncirclement() {
        return landmarkClaimRequiresEncirclement;
    }

    public static double getFortCost() {
        return fortCost;
    }

    public static double getFortProtectionRadius() {
        return fortProtectionRadius;
    }

    public static double getFortCaptureRadius() {
        return fortCaptureRadius;
    }

    public static boolean enableFortOutpost() {
        return useAsOutpost;
    }

    public static int getPropertySignMargin() {
        return propertySignMargin;
    }

    public static long getWarDuration() {
        return warDuration;
    }

    public static List<String> getBlacklistedCommandsDuringAttacks() {
        return blacklistedCommandsDuringAttacks;
    }

    public static int getNbChunkToCaptureMax() {
        return nbChunkToCaptureMax;
    }


    public static GriefAllowed getMobGriefStatus() {
        return mobGriefStatus;
    }

    public static GriefAllowed getPvpStatus() {
        return pvpEnabledStatus;
    }

    public static GriefAllowed getFireGriefStatus() {
        return fireGriefStatus;
    }

    public static GriefAllowed getExplosionGriefStatus() {
        return explosionGriefStatus;
    }

    public static boolean allowNonAdjacentChunksForRegion() {
        return allowNonAdjacentChunksForRegion;
    }

    public static boolean allowNonAdjacentChunksForTown() {
        return allowNonAdjacentChunksForTown;
    }

    public static double getStartingBalance() {
        return startingBalance;
    }

    public static boolean enableTownTag() {
        return allowTownTag;
    }
}
