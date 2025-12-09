package org.leralix.tan.upgrade;


import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.service.requirements.model.*;
import org.leralix.tan.gui.service.requirements.upgrade.*;
import org.leralix.tan.upgrade.rewards.IndividualStat;
import org.leralix.tan.upgrade.rewards.StatsType;
import org.leralix.tan.upgrade.rewards.bool.EnableMobBan;
import org.leralix.tan.upgrade.rewards.bool.EnableTownSpawn;
import org.leralix.tan.upgrade.rewards.list.BiomeStat;
import org.leralix.tan.upgrade.rewards.list.PermissionList;
import org.leralix.tan.upgrade.rewards.numeric.*;
import org.leralix.tan.upgrade.rewards.percentage.LandmarkBonus;

import java.util.*;

public class NewUpgradeStorage {

    private final Map<String, Upgrade> townUpgrades;
    private final Map<String, Upgrade> regionUpgrades;


    public NewUpgradeStorage() {

        this.townUpgrades = new HashMap<>();
        this.regionUpgrades = new HashMap<>();

        FileConfiguration upgradeConfig = ConfigUtil.getCustomConfig(ConfigTag.UPGRADE);
        setUpUpgrades(townUpgrades, upgradeConfig.getConfigurationSection("upgrades"));
        setUpUpgrades(regionUpgrades, upgradeConfig.getConfigurationSection("region_upgrades"));

    }

    private void setUpUpgrades(Map<String, Upgrade> upgradeMap, ConfigurationSection upgradesSection) {
        if (upgradesSection != null) {
            for (String key : upgradesSection.getKeys(false)) {
                int col = upgradesSection.getInt(key + ".col");
                int row = upgradesSection.getInt(key + ".row");
                String itemCode = upgradesSection.getString(key + ".itemCode", "BARRIER");
                Material icon = Material.valueOf(itemCode.toUpperCase());
                int maxLevel = upgradesSection.getInt(key + ".maxLevel");

                // Prerequisites
                List<UpgradeRequirement> prerequisites = getPrerequisites(upgradesSection, key);

                //Benefits
                ConfigurationSection benefitsSection = upgradesSection.getConfigurationSection(key + ".benefits");
                List<IndividualStat> rewards = getRewards(benefitsSection);

                upgradeMap.put(
                        key,
                        new Upgrade(
                                row,
                                col,
                                key,
                                icon,
                                maxLevel,
                                prerequisites,
                                rewards
                        )
                );
            }
        }
    }

    private static @NotNull List<UpgradeRequirement> getPrerequisites(ConfigurationSection upgradesSection, String key) {
        List<UpgradeRequirement> newPrerequisites = new ArrayList<>();

        List<Integer> cost = upgradesSection.getIntegerList(key + ".cost");
        newPrerequisites.add(new UpgradeCostRequirement(cost));

        ConfigurationSection upgradePrerequisites = upgradesSection.getConfigurationSection(key + ".prerequisites");
        if (upgradePrerequisites != null) {
            for (String prerequisiteKey : upgradePrerequisites.getKeys(false)) {
                int requiredLevel = upgradePrerequisites.getInt(prerequisiteKey);
                if (prerequisiteKey.equals("TOWN_LEVEL")) {
                    newPrerequisites.add(new LevelUpgradeRequirement(requiredLevel));
                } else {
                    newPrerequisites.add(new OtherUpgradeRequirementBuilder(prerequisiteKey, requiredLevel));
                }
            }
        }

        ConfigurationSection resourcesSection = upgradesSection.getConfigurationSection(key + ".resources");
        if (resourcesSection != null) {
            for (String ressourcesKey : resourcesSection.getKeys(false)) {

                ConfigurationSection singleRessourceConfig = resourcesSection.getConfigurationSection(ressourcesKey);

                if(singleRessourceConfig != null){
                    ItemScope itemScope = getItemScope(ressourcesKey);

                    List<Integer> quantity = singleRessourceConfig.getIntegerList("quantity");
                    String customName = singleRessourceConfig.getString("custom_name");
                    int customID = singleRessourceConfig.getInt("custom_id", Integer.MIN_VALUE);
                    newPrerequisites.add(new ItemRequirementBuilder(itemScope, quantity, customName, customID));
                }
            }
        }
        return newPrerequisites;
    }

    private static ItemScope getItemScope(String ressourcesKey) {
        return switch (ressourcesKey) {
            case "ANY_WOOD" ->
                    new AnyWoodScope();
            case "ANY_LOG" ->
                    new AnyLogScope();
            case "ANY_PLANK" ->
                    new AnyPlankScope();
            case "ANY_STONE" ->
                    new AnyStoneScope();
            default -> new MaterialScope(Material.valueOf(ressourcesKey));
        };
    }

    private static @NotNull List<IndividualStat> getRewards(ConfigurationSection benefitsSection) {
        List<IndividualStat> rewards = new ArrayList<>();
        if (benefitsSection != null) {
            for (String benefitKey : benefitsSection.getKeys(false)) {

                boolean isUnlimited = isInfiniteValue(benefitsSection, benefitKey);
                double value = parseDoubleValue(benefitsSection, benefitKey);

                switch (benefitKey) {
                    case "PROPERTY_CAP" -> rewards.add(new PropertyCap((int) value, isUnlimited));
                    case "PLAYER_CAP" -> rewards.add(new TownPlayerCap((int) value, isUnlimited));
                    case "CHUNK_CAP" -> rewards.add(new ChunkCap((int) value, isUnlimited));
                    case "CHUNK_COST" -> rewards.add(new ChunkCost((int) value, isUnlimited));
                    case "CHUNK_UPKEEP_COST" -> rewards.add(new ChunkUpkeepCost(value));
                    case "LANDMARKS_CAP" -> rewards.add(new LandmarkCap((int) value, isUnlimited));
                    case "UNLOCK_TOWN_SPAWN" -> rewards.add(new EnableTownSpawn(true));
                    case "UNLOCK_MOB_BAN" -> rewards.add(new EnableMobBan(true));
                    case "LANDMARK_BONUS" -> rewards.add(new LandmarkBonus(
                            benefitsSection.getDouble(benefitKey) / 100
                    ));
                    case "UNLOCK_NEW_PERMISSIONS" -> {
                        List<String> permissions = benefitsSection.getStringList(benefitKey);
                        rewards.add(new PermissionList(permissions));
                    }
                    case "AUTHORIZED_BIOMES" -> {
                        List<String> biomeKeys = benefitsSection.getStringList(benefitKey);
                        rewards.add(BiomeStat.fromStrings(biomeKeys));
                    }
                }
            }
        }
        return rewards;
    }

    private static boolean isInfiniteValue(ConfigurationSection section, String path) {
        String raw = section.getString(path);
        if (raw == null) return false;

        raw = raw.trim().toLowerCase();
        return raw.equals("infinity") || raw.equals("inf") || raw.equals("∞") || raw.equals("unlimited");
    }

    private static double parseDoubleValue(ConfigurationSection section, String path) {
        String raw = section.getString(path);
        if (raw == null) return 0.;

        raw = raw.trim().toLowerCase();
        if (raw.equals("infinity") || raw.equals("inf") || raw.equals("∞") || raw.equals("unlimited")) {
            return 0.;
        }

        try {
            return Double.parseDouble(raw.replace("+", ""));
        } catch (NumberFormatException e) {
            return 0.;
        }
    }

    public Upgrade getUpgrade(TerritoryData territoryData, String id) {
        if(territoryData instanceof RegionData){
            return regionUpgrades.get(id);
        }
        return townUpgrades.get(id);
    }

    public Collection<Upgrade> getUpgrades(StatsType statsType) {
        return switch (statsType){
            case REGION -> regionUpgrades.values();
            case TOWN -> townUpgrades.values();
            case null -> townUpgrades.values();
        };
    }

    public Collection<Upgrade> getUpgrades(TerritoryData territoryData) {
        if(territoryData instanceof RegionData){
            return getUpgrades(StatsType.REGION);
        }
        return getUpgrades(StatsType.TOWN);
    }
}
