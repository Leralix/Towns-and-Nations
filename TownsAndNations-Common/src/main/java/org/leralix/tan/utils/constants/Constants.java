package org.leralix.tan.utils.constants;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.Range;
import org.leralix.tan.dataclass.chunk.ChunkType;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.enums.permissions.GeneralChunkSetting;
import org.leralix.tan.storage.MobChunkSpawnStorage;
import org.leralix.tan.upgrade.NewUpgradeStorage;
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
    private static int nbDaysBeforeClearningTransactions;
    private static int nbDaysBeforeClearningNewsletter;
    private static NewsletterScopeConfig newsletterScopeConfig;
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
    private static Range prefixSize;
    private static String townTagFormat;
    private static boolean allowColorCode;
    private static String baseCurrencyChar;
    private static boolean showCurrency;
    private static BoundaryParticle boundaryParticles;

    //Territory
    private static int townCost;
    private static int townMaxNameSize;
    private static int townMaxDescriptionSize;
    private static int regionCost;
    private static int regionMaxNameSize;
    private static int regionMaxDescriptionSize;
    private static int maxRankSize;
    private static int rankNameSize;
    private static boolean displayTerritoryColor;
    private static int territoryClaimBufferZone;
    private static int minimumNumberOfChunksUnclaimed;
    private static double percentageOfChunksUnclaimed;
    private static MobChunkSpawnStorage mobChunkSpawnStorage;

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
    private static int fortCaptureTime;
    private static boolean claimAllIfCaptured;

    //Properties
    private static int maxPropertyNameSize;
    private static int maxPropertyDescriptionSize;
    private static int maxPropertySignMargin;
    private static int maxPropertySize;
    private static Particle propertyBoundaryParticles;
    private static boolean payRentAtStart;

    //Wars
    private static boolean simpleWarMode;
    private static WarTimeSlot warTimeSlot;
    private static double warBoundaryRadius;
    private static boolean notifyWhenEnemyEnterTerritory;
    private static Map<TownRelation, RelationConstant> relationsConstants;
    private static Set<String> allRelationBlacklistedCommands;
    private static boolean adminApprovalForStartOfAttack;
    private static int capturePercentageToSurrender;
    private static int captureCapitalBonusPercentage;
    private static PermissionAtWars permissionAtWars;

    private static int attackDuration;
    private static int minTimeBeforeAttack;
    private static int maxTimeBeforeAttack;
    private static int chunkCaptureTime;
    private static List<String> blacklistedCommandsDuringAttacks;
    private static int nbChunkToCaptureMax;
    private static List<String> onceStartCommands;
    private static List<String> onceEndCommands;
    private static List<String> perPlayerStartCommands;
    private static List<String> perPlayerEndCommands;

    //Claims
    private static ChunkPermissionConfig chunkPermissionConfig;
    private static GeneralChunkSettings generalChunkSettings;
    private static WildernessRules wildernessRules;


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

    //Upgrades
    private static NewUpgradeStorage upgradeStorage;
    private static int townMaxLevel;
    private static int regionMaxLevel;

    public static void init(FileConfiguration config, FileConfiguration upgradeConfig) {


        onlineMode = config.getBoolean("onlineMode", true);
        databaseConstants = new DatabaseConstants(config.getConfigurationSection("database"));
        dailyTaskHour = config.getInt("taxHourTime", 0);
        dailyTaskMinute = config.getInt("taxMinuteTime", 0);
        nbDaysBeforeClearningTransactions = config.getInt("nbDaysBeforeTransactionDeletion", 90);
        nbDaysBeforeClearningNewsletter = config.getInt("TimeBeforeClearingNewsletter");
        newsletterScopeConfig = new NewsletterScopeConfig(config.getConfigurationSection("events"));
        //Economy
        useStandaloneEconomy = config.getBoolean("UseTanEconomy", false);
        startingBalance = config.getDouble("StartingMoney", 100.0);
        maxPayRange = config.getDouble("maxPayDistance", 15);
        //Cosmetic
        allowTownTag = config.getBoolean("enableTownTag", false);
        prefixSize = new Range(
                config.getInt("tagMinSize", 3),
                config.getInt("tagMaxSize", 4)
        );
        townTagFormat = config.getString("townTagFormat", "&f[&l{townColor}{townTag}&r]");
        allowColorCode = config.getBoolean("EnablePlayerColorCode", false);
        baseCurrencyChar = config.getString("moneyIcon", "$");
        showCurrency = config.getBoolean("showCurrency", true);

        boundaryParticles = new BoundaryParticle(config.getConfigurationSection("boundaryParticles"));
        //Territory
        townCost = config.getInt("townCost", 1000);
        townMaxNameSize = config.getInt("TownNameSize", 45);
        townMaxDescriptionSize = config.getInt("TownDescSize", 55);
        regionCost = config.getInt("regionCost", 7500);
        regionMaxNameSize = config.getInt("RegionNameSize", 45);
        regionMaxDescriptionSize = config.getInt("RegionDescSize", 55);
        maxRankSize = config.getInt("maxRanks", 9);
        rankNameSize = config.getInt("RankNameSize", 40);

        displayTerritoryColor = config.getBoolean("displayTerritoryNameWithOwnColor", false);
        territoryClaimBufferZone = config.getInt("TerritoryClaimBufferZone", 2);
        minimumNumberOfChunksUnclaimed = config.getInt("minimumNumberOfChunksUnclaimed", 5);
        percentageOfChunksUnclaimed = config.getDouble("percentageOfChunksUnclaimed", 10) / 100;
        mobChunkSpawnStorage = new MobChunkSpawnStorage(config.getConfigurationSection("CancelMobSpawnInTown"));

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
            fortCaptureTime = fortsSection.getInt("fortCaptureTime", 60);
            claimAllIfCaptured = fortsSection.getBoolean("claimAllIfCaptured", true);
        }

        maxPropertyNameSize = config.getInt("PropertyNameSize", 16);
        maxPropertyDescriptionSize = config.getInt("PropertyDescSize", 48);

        maxPropertySignMargin = config.getInt("maxPropertyMargin", 3);
        maxPropertySize = config.getInt("MaxPropertySize", 50000);
        payRentAtStart = config.getBoolean("payRentAtStart", true);
        propertyBoundaryParticles = getParticle(config, "propertyBoundaryParticles");

        //Attacks
        simpleWarMode = config.getBoolean("simpleWarMode");
        warTimeSlot = new WarTimeSlot(
                config.getStringList("allowedTimeSlotsWar"),
                config.getIntegerList("allowedDays")
        );
        warBoundaryRadius = config.getDouble("warBoundaryRadius", 16);
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
        adminApprovalForStartOfAttack = config.getBoolean("AdminApproval", false);

        capturePercentageToSurrender = config.getInt("capturePercentageToSurrender");
        captureCapitalBonusPercentage = config.getInt("captureCapitalBonusPercentage");
        permissionAtWars = new PermissionAtWars(config.getConfigurationSection("attackersPermissions"));
        attackDuration = config.getInt("WarDuration", 30);
        minTimeBeforeAttack = config.getInt("MinimumTimeBeforeAttack", 120);
        maxTimeBeforeAttack = config.getInt("MaximumTimeBeforeAttack", 4320);

        chunkCaptureTime = config.getInt("ChunkCaptureTime", 10);

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
        chunkPermissionConfig = new ChunkPermissionConfig(config.getConfigurationSection("chunkPermissionConfig"));
        generalChunkSettings = new GeneralChunkSettings(config.getConfigurationSection("chunkGeneralSettings"));
        wildernessRules = new WildernessRules(config.getConfigurationSection("wildernessRules"));

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

        //Upgrade
        upgradeStorage = new NewUpgradeStorage(upgradeConfig);
        townMaxLevel = upgradeConfig.getInt("TownMaxLevel", 10);
        regionMaxLevel = upgradeConfig.getInt("RegionMaxLevel", 10);

    }

    /**
     * Depending on the config value, return the Particle enum.
     * In case the config is wrong or the particles does not exist in the used version of Minecraft,
     * return DRAGON_BREATH as default.
     * @param config    The configuration file
     * @param key       The key to get the particle name from
     * @return  The Particle enum, or DRAGON_BREATH if not found
     */
    private static Particle getParticle(FileConfiguration config, String key) {
        String particleName = config.getString(key, "DRAGON_BREATH").toUpperCase();
        Particle particle;
        try {
            particle = Particle.valueOf(particleName);
        } catch (IllegalArgumentException e) {
            particle = Particle.DRAGON_BREATH;
            TownsAndNations.getPlugin().getLogger();
        }
        return particle;
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

    public static int getNbDaysBeforeClearningTransactions() {
        return nbDaysBeforeClearningTransactions;
    }

    public static int getNbDaysBeforeClearningNewsletter() {
        return nbDaysBeforeClearningNewsletter;
    }

    public static NewsletterScopeConfig getNewsletterScopeConfig() {
        return newsletterScopeConfig;
    }

    public static boolean displayTerritoryColor() {
        return displayTerritoryColor;
    }

    public static int territoryClaimBufferZone() {
        return territoryClaimBufferZone;
    }

    public static int getMinimumNumberOfChunksUnclaimed() {
        return minimumNumberOfChunksUnclaimed;
    }

    public static double getPercentageOfChunksUnclaimed() {
        return percentageOfChunksUnclaimed;
    }

    public static MobChunkSpawnStorage getMobChunkSpawnStorage() {
        return mobChunkSpawnStorage;
    }

    public static Range getPrefixSize() {
        return prefixSize;
    }

    public static String getTownTagFormat() {
        return townTagFormat;
    }

    public static BoundaryParticle getBoundaryParticles() {
        return boundaryParticles;
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

    public static int getFortCaptureTime() {
        return fortCaptureTime;
    }

    public static boolean isClaimAllIfCaptured() {
        return claimAllIfCaptured;
    }

    public static int getMaxPropertyNameSize() {
        return maxPropertyNameSize;
    }

    public static int getMaxPropertyDescriptionSize() {
        return maxPropertyDescriptionSize;
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

    public static boolean isSimpleWarMode() {
        return simpleWarMode;
    }

    public static WarTimeSlot getWarTimeSlot() {
        return warTimeSlot;
    }

    public static double getWarBoundaryRadius() {
        return warBoundaryRadius;
    }

    public static boolean notifyWhenEnemyEnterTerritory() {
        return notifyWhenEnemyEnterTerritory;
    }

    /**
     * @return Attack duration, in minutes
     */
    public static int getAttackDuration() {
        return attackDuration;
    }

    public static int getMaxTimeBeforeAttack() {
        return maxTimeBeforeAttack;
    }

    public static int getMinTimeBeforeAttack() {
        return minTimeBeforeAttack;
    }

    public static int getChunkCaptureTime(){
        return chunkCaptureTime;
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

    public static ChunkPermissionConfig getChunkPermissionConfig(){
        return chunkPermissionConfig;
    }

    public static WildernessRules getWildernessRules() {
        return wildernessRules;
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

    public static int getTownMaxNameSize() {
        return townMaxNameSize;
    }

    public static int getTownMaxDescriptionSize() {
        return townMaxDescriptionSize;
    }

    public static int getRegionCost() {
        return regionCost;
    }

    public static int getRegionMaxNameSize(){
        return regionMaxNameSize;
    }

    public static int getRegionMaxDescriptionSize(){
        return regionMaxDescriptionSize;
    }

    public static int getMaxRankSize() {
        return maxRankSize;
    }

    public static int getRankNameSize() {
        return rankNameSize;
    }

    public static RelationConstant getRelationConstants(TownRelation relation) {
        return relationsConstants.getOrDefault(relation, relationsConstants.get(TownRelation.NEUTRAL));
    }

    public static Set<String> getAllRelationBlacklistedCommands() {
        return allRelationBlacklistedCommands;
    }

    public static boolean adminApprovalForStartOfAttack() {
        return adminApprovalForStartOfAttack;
    }

    public static int getCapturePercentageToSurrender() {
        return capturePercentageToSurrender;
    }

    public static int getCaptureCapitalBonusPercentage() {
        return captureCapitalBonusPercentage;
    }

    public static PermissionAtWars getPermissionAtWars() {
        return permissionAtWars;
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

    public static NewUpgradeStorage getUpgradeStorage() {
        return upgradeStorage;
    }

    public static int getTerritoryMaxLevel(TerritoryData territoryData){
        if(territoryData instanceof TownData){
            return townMaxLevel;
        }
        return regionMaxLevel;
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
