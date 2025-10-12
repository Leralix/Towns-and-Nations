package org.leralix.tan.utils.constants;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.leralix.tan.dataclass.chunk.ChunkType;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.enums.permissions.GeneralChunkSetting;
import org.leralix.tan.war.WarTimeSlot;
import org.leralix.tan.war.legacy.InteractionStatus;

import java.util.*;

public class Constants {

    private Constants() {
        throw new AssertionError("Static class");
    }

    //Config
    private static boolean onlineMode;
    private static DatabaseConstants databaseConstants;
    private static int dailyTaskHour;
    private static int dailyTaskMinute;
    //Economy
    private static boolean useStandaloneEconomy;
    private static double startingBalance;
    private static double maxPayRange;
    private static int nbDigits;
    //Cosmetic
    /**
     * If Enabled, player username will have a 3 letter prefix of their town name.
     * This option cannot be enabled with allowColorCodes.
     */
    private static boolean allowTownTag;
    private static boolean allowColorCode;
    private static String baseCurrencyChar;
    private static boolean showCurrency;
    private static int prefixSize;

    //Territory
    private static int townCost;
    private static int regionCost;
    private static double townChunkUpkeepCost;
    private static double regionChunkUpkeepCost;
    private static boolean displayTerritoryColor;
    private static int territoryClaimBufferZone;
    private static int territoryClaimTownCost;
    private static int territoryClaimRegionCost;
    private static int minimumNumberOfChunksUnclaimed;
    private static double percentageOfChunksUnclaimed;

    private static boolean enableNation;
    private static boolean enableRegion;
    private static int changeTownNameCost;
    private static int changeRegionNameCost;
    private static boolean worldGuardOverrideWilderness;
    private static boolean worldGuardOverrideTown;
    private static boolean worldGuardOverrideRegion;
    private static boolean worldGuardOverrideLandmark;

    //Buildings
    private static double fortCost;
    private static double fortProtectionRadius;
    private static double fortCaptureRadius;
    private static boolean useAsOutpost;

    //Properties
    private static int maxPropertySignMargin;
    private static int maxPropertySize;
    private static Particle propertyBoundaryParticles;
    private static boolean payRentAtStart;

    //Wars
    private static WarTimeSlot warTimeSlot;
    private static double warBoundaryRadius;
    private static Particle warBoundaryParticle;
    private static boolean notifyWhenEnemyEnterTerritory;
    private static Map<TownRelation, RelationConstant> relationsConstants;
    private static Set<String> allRelationBlacklistedCommands;

    private static long attackDuration;
    private static int minTimeBeforeAttack;
    private static int maxTimeBeforeAttack;
    private static List<String> blacklistedCommandsDuringAttacks;
    private static int nbChunkToCaptureMax;
    private static List<String> onceStartCommands;
    private static List<String> onceEndCommands;
    private static List<String> perPlayerStartCommands;
    private static List<String> perPlayerEndCommands;

    //Claims
    private static GeneralChunkSettings generalChunkSettings;
    private static WildernessRules wildernessRules;
    private static InteractionStatus explosionGriefStatus;
    private static InteractionStatus fireGriefStatus;
    private static InteractionStatus pvpEnabledStatus;
    private static InteractionStatus mobGriefStatus;


    private static boolean allowNonAdjacentChunksForTown;
    private static boolean allowNonAdjacentChunksForRegion;
    private static double claimLandmarkCost;
    private static boolean landmarkClaimRequiresEncirclement;

    private static int landmarkStorageCapacity;
    private static int landmarkMaxNameSize;

    //Teleportation
    private static int timeBeforeTeleport;
    private static boolean cancelTeleportOnMoveHead;
    private static boolean cancelTeleportOnMovePosition;
    private static boolean cancelTeleportOnDamage;

    private static final String ALWAYS = "ALWAYS";

    public static void init(FileConfiguration config) {


        onlineMode = config.getBoolean("onlineMode", true);
        databaseConstants = new DatabaseConstants(config.getConfigurationSection("database"));
        dailyTaskHour = config.getInt("taxHourTime", 0);
        dailyTaskMinute = config.getInt("taxMinuteTime", 0);

        //Economy
        useStandaloneEconomy = config.getBoolean("UseTanEconomy", false);
        startingBalance = config.getDouble("StartingMoney", 100.0);
        maxPayRange = config.getDouble("maxPayDistance", 15);
        //Cosmetic
        allowTownTag = config.getBoolean("EnablePlayerPrefix", false);
        allowColorCode = config.getBoolean("EnablePlayerColorCode", false);
        baseCurrencyChar = config.getString("moneyIcon", "$");
        showCurrency = config.getBoolean("showCurrency", true);
        prefixSize = config.getInt("prefixSize", 3);
        //Territory
        townCost = config.getInt("townCost", 1000);
        regionCost = config.getInt("regionCost", 7500);
        townChunkUpkeepCost = config.getDouble("TownChunkUpkeepCost", 0);
        regionChunkUpkeepCost = config.getDouble("RegionChunkUpkeepCost", 0);

        displayTerritoryColor = config.getBoolean("displayTerritoryNameWithOwnColor", false);
        territoryClaimBufferZone = config.getInt("TerritoryClaimBufferZone", 2);
        territoryClaimTownCost = config.getInt("CostOfTownChunk", 1);
        territoryClaimRegionCost = config.getInt("CostOfRegionChunk", 5);
        minimumNumberOfChunksUnclaimed = config.getInt("minimumNumberOfChunksUnclaimed", 5);
        percentageOfChunksUnclaimed = config.getDouble("percentageOfChunksUnclaimed", 10) / 100;

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

        maxPropertySignMargin = config.getInt("maxPropertyMargin", 3);
        maxPropertySize = config.getInt("MaxPropertySize", 50000);
        payRentAtStart = config.getBoolean("payRentAtStart", true);
        propertyBoundaryParticles = Particle.valueOf(config.getString("propertyBoundaryParticles"));

        //Attacks

        warTimeSlot = new WarTimeSlot(config.getStringList("allowedTimeSlotsWar"));
        warBoundaryRadius = config.getDouble("warBoundaryRadius", 16);
        warBoundaryParticle = Particle.valueOf(config.getString("warBoundaryParticle", "DRAGON_BREATH").toUpperCase());
        notifyWhenEnemyEnterTerritory = config.getBoolean("notifyEnemyEnterTown", true);

        relationsConstants = new EnumMap<>(TownRelation.class);
        allRelationBlacklistedCommands = new HashSet<>();
        ConfigurationSection relationsSection = config.getConfigurationSection("relationConstants");
        if (relationsSection != null) {
            for (TownRelation relation : TownRelation.values()) {
                ConfigurationSection relationSection = relationsSection.getConfigurationSection(relation.name().toLowerCase());
                if (relationSection != null) {
                    relationsConstants.put(relation, new RelationConstant(relationSection));
                    allRelationBlacklistedCommands.addAll(relationSection.getStringList("blockedCommands"));
                }
            }
        }

        attackDuration = config.getLong("WarDuration", 30);
        minTimeBeforeAttack = config.getInt("MinimumTimeBeforeAttack", 120);
        maxTimeBeforeAttack = config.getInt("MaximumTimeBeforeAttack", 4320);

        blacklistedCommandsDuringAttacks = config.getStringList("BlacklistedCommandsDuringAttacks");
        nbChunkToCaptureMax = config.getInt("MaximumChunkConquer", 0);
        if (nbChunkToCaptureMax == 0) {
            nbChunkToCaptureMax = Integer.MAX_VALUE;
        }
        onceStartCommands = config.getStringList("commandToExecuteOnceWhenAttackStart");
        onceEndCommands = config.getStringList("commandToExecuteOnceWhenAttackEnd");
        perPlayerStartCommands = config.getStringList("commandToExecutePerPlayerWhenAttackStart");
        perPlayerEndCommands = config.getStringList("commandToExecutePerPlayerWhenAttackEnd");

        //Claims
        generalChunkSettings = new GeneralChunkSettings(config.getConfigurationSection("chunkGeneralSettings"));
        wildernessRules = new WildernessRules(config.getConfigurationSection("wildernessRules"));
        explosionGriefStatus = InteractionStatus.valueOf(config.getString("explosionGrief", ALWAYS));
        fireGriefStatus = InteractionStatus.valueOf(config.getString("fireGrief", ALWAYS));
        pvpEnabledStatus = InteractionStatus.valueOf(config.getString("pvpEnabledInClaimedChunks", ALWAYS));
        mobGriefStatus = InteractionStatus.valueOf(config.getString("mobGrief", ALWAYS));

        allowNonAdjacentChunksForRegion = config.getBoolean("RegionAllowNonAdjacentChunks", false);
        allowNonAdjacentChunksForTown = config.getBoolean("TownAllowNonAdjacentChunks", false);

        //Landmarks
        landmarkStorageCapacity = config.getInt("landmarkStorageCapacity", 7);
        landmarkMaxNameSize = config.getInt("landmarkNameMaxSize", 25);

        //Teleportation
        timeBeforeTeleport = config.getInt("timeBeforeTeleport", 5);
        cancelTeleportOnMoveHead = config.getBoolean("cancelTeleportOnMoveHead", false);
        cancelTeleportOnMovePosition = config.getBoolean("cancelTeleportOnMovePosition", true);
        cancelTeleportOnDamage = config.getBoolean("cancelTeleportOnDamage", true);
    }

    public static boolean onlineMode() {
        return onlineMode;
    }

    public static DatabaseConstants databaseConstants() {
        return databaseConstants;
    }

    public static int getDailyTaskHour() {
        return dailyTaskHour;
    }

    public static int getDailyTaskMinute() {
        return dailyTaskMinute;
    }

    public static boolean displayTerritoryColor() {
        return displayTerritoryColor;
    }

    public static int territoryClaimBufferZone() {
        return territoryClaimBufferZone;
    }

    public static int territoryClaimTownCost() {
        return territoryClaimTownCost;
    }

    public static int territoryClaimRegionCost() {
        return territoryClaimRegionCost;
    }

    public static int getMinimumNumberOfChunksUnclaimed() {
        return minimumNumberOfChunksUnclaimed;
    }

    public static double getPercentageOfChunksUnclaimed() {
        return percentageOfChunksUnclaimed;
    }

    public static int getPrefixSize() {
        return prefixSize;
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

    public static int getMaxPropertySignMargin() {
        return maxPropertySignMargin;
    }

    public static int getMaxPropertySize() {
        return maxPropertySize;
    }

    public static Particle getPropertyBoundaryParticles() {
        return propertyBoundaryParticles;
    }

    public static boolean shouldPayRentAtStart() {
        return payRentAtStart;
    }

    public static WarTimeSlot getWarTimeSlot() {
        return warTimeSlot;
    }

    public static double getWarBoundaryRadius() {
        return warBoundaryRadius;
    }

    public static Particle getWarBoundaryParticle() {
        return warBoundaryParticle;
    }

    public static boolean notifyWhenEnemyEnterTerritory() {
        return notifyWhenEnemyEnterTerritory;
    }

    public static long getAttackDuration() {
        return attackDuration;
    }

    public static int getMaxTimeBeforeAttack() {
        return maxTimeBeforeAttack;
    }

    public static int getMinTimeBeforeAttack() {
        return minTimeBeforeAttack;
    }

    public static List<String> getBlacklistedCommandsDuringAttacks() {
        return blacklistedCommandsDuringAttacks;
    }

    public static int getNbChunkToCaptureMax() {
        return nbChunkToCaptureMax;
    }

    public static InteractionStatus getChunkSettings(GeneralChunkSetting generalChunkSetting) {
        return generalChunkSettings.getAction(generalChunkSetting);
    }

    public static InteractionStatus getMobGriefStatus() {
        return mobGriefStatus;
    }

    public static InteractionStatus getPvpStatus() {
        return pvpEnabledStatus;
    }

    public static InteractionStatus getFireGriefStatus() {
        return fireGriefStatus;
    }

    public static WildernessRules getWildernessRules() {
        return wildernessRules;
    }

    public static InteractionStatus getExplosionGriefStatus() {
        return explosionGriefStatus;
    }

    public static boolean allowNonAdjacentChunksForTown() {
        return allowNonAdjacentChunksForTown;
    }

    public static boolean allowNonAdjacentChunksForRegion() {
        return allowNonAdjacentChunksForRegion;
    }

    public static boolean allowNonAdjacentChunksFor(TerritoryData territoryData) {
        if (territoryData instanceof TownData) {
            return allowNonAdjacentChunksForTown;
        }
        if (territoryData instanceof RegionData) {
            return allowNonAdjacentChunksForRegion;
        }
        return false;
    }

    public static boolean useStandaloneEconomy() {
        return useStandaloneEconomy;
    }

    public static double getStartingBalance() {
        return startingBalance;
    }

    public static double getMaxPayRange() {
        return maxPayRange;
    }

    public static boolean enableTownTag() {
        return allowTownTag;
    }

    public static boolean enableColorUsernames() {
        return allowColorCode;
    }

    public static String getBaseCurrencyChar() {
        return baseCurrencyChar;
    }

    public static boolean shouldShowCurrency() {
        return showCurrency;
    }

    public static int getTownCost() {
        return townCost;
    }

    public static int getRegionCost() {
        return regionCost;
    }

    public static double getUpkeepCost(TerritoryData territoryData) {
        if (territoryData instanceof TownData) {
            return townChunkUpkeepCost;
        } else if (territoryData instanceof RegionData) {
            return regionChunkUpkeepCost;
        }
        return townChunkUpkeepCost;
    }

    public static double getRegionChunkUpkeepCost() {
        return regionChunkUpkeepCost;
    }

    public static RelationConstant getRelationConstants(TownRelation relation) {
        return relationsConstants.getOrDefault(relation, relationsConstants.get(TownRelation.NEUTRAL));
    }

    public static Set<String> getAllRelationBlacklistedCommands() {
        return allRelationBlacklistedCommands;
    }

    public static List<String> getPerPlayerEndCommands() {
        return perPlayerEndCommands;
    }

    public static List<String> getPerPlayerStartCommands() {
        return perPlayerStartCommands;
    }

    public static List<String> getOnceEndCommands() {
        return onceEndCommands;
    }

    public static List<String> getOnceStartCommands() {
        return onceStartCommands;
    }

    public static int getLandmarkStorageCapacity() {
        return landmarkStorageCapacity;
    }

    public static int getLandmarkMaxNameSize() {
        return landmarkMaxNameSize;
    }

    public static boolean isCancelTeleportOnDamage() {
        return cancelTeleportOnDamage;
    }

    public static boolean isCancelTeleportOnMovePosition() {
        return cancelTeleportOnMovePosition;
    }

    public static boolean isCancelTeleportOnMoveHead() {
        return cancelTeleportOnMoveHead;
    }

    public static int getTimeBeforeTeleport() {
        return timeBeforeTeleport;
    }
}
