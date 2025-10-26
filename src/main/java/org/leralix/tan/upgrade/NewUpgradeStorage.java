package org.leralix.tan.upgrade;


import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.gui.service.requirements.model.*;
import org.leralix.tan.gui.service.requirements.upgrade.*;
import org.leralix.tan.upgrade.rewards.IndividualStat;
import org.leralix.tan.upgrade.rewards.bool.EnableMobBan;
import org.leralix.tan.upgrade.rewards.bool.EnableTownSpawn;
import org.leralix.tan.upgrade.rewards.list.BiomeStat;
import org.leralix.tan.upgrade.rewards.list.PermissionList;
import org.leralix.tan.upgrade.rewards.numeric.ChunkCap;
import org.leralix.tan.upgrade.rewards.numeric.LandmarkCap;
import org.leralix.tan.upgrade.rewards.numeric.PropertyCap;
import org.leralix.tan.upgrade.rewards.numeric.TownPlayerCap;
import org.leralix.tan.upgrade.rewards.percentage.LandmarkBonus;

import java.util.*;

public class NewUpgradeStorage {

    private final Map<String, Upgrade> upgrades;

    public NewUpgradeStorage() {

        this.upgrades = new HashMap<>();

        FileConfiguration upgradeConfig = ConfigUtil.getCustomConfig(ConfigTag.UPGRADE);
        ConfigurationSection upgradesSection = upgradeConfig.getConfigurationSection("upgrades");

        if (upgradesSection != null) {
            for (String key : upgradesSection.getKeys(false)) {
                int col = upgradesSection.getInt(key + ".col");
                int row = upgradesSection.getInt(key + ".row");
                String itemCode = upgradesSection.getString(key + ".itemCode", "BARRIER");
                Material icon = Material.valueOf(itemCode.toUpperCase());
                int maxLevel = upgradesSection.getInt(key + ".maxLevel");

                // Prerequisites
                List<UpgradeRequirement> newPrerequisites = new ArrayList<>();

                List<Integer> cost = upgradesSection.getIntegerList(key + ".cost");
                newPrerequisites.add(new UpgradeCostRequirement(cost));

                ConfigurationSection prerequisiteSection = upgradesSection.getConfigurationSection(key + ".prerequisites");
                if (prerequisiteSection != null) {
                    for (String prerequisiteKey : prerequisiteSection.getKeys(false)) {
                        int requiredLevel = prerequisiteSection.getInt(prerequisiteKey);
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
                        int number = resourcesSection.getInt(ressourcesKey);
                        switch (ressourcesKey) {
                            case "ANY_WOOD" ->
                                    newPrerequisites.add(new ItemRequirementBuilder(new AnyWoodScope(), number));
                            case "ANY_LOG" ->
                                    newPrerequisites.add(new ItemRequirementBuilder(new AnyLogScope(), number));
                            case "ANY_PLANK" ->
                                    newPrerequisites.add(new ItemRequirementBuilder(new AnyPlankScope(), number));
                            case "ANY_STONE" ->
                                    newPrerequisites.add(new ItemRequirementBuilder(new AnyStoneScope(), number));
                            default ->
                                    newPrerequisites.add(new ItemRequirementBuilder(new MaterialScope(Material.valueOf(ressourcesKey)), number));
                        }
                    }
                }

                //Benefits
                ConfigurationSection benefitsSection = upgradesSection.getConfigurationSection(key + ".benefits");
                List<IndividualStat> rewards = new ArrayList<>();
                if (benefitsSection != null) {
                    for (String benefitKey : benefitsSection.getKeys(false)) {

                        boolean isUnlimited = isInfiniteValue(benefitsSection, benefitKey);
                        int intValue = parseIntValue(benefitsSection, benefitKey);

                        switch (benefitKey) {
                            case "PROPERTY_CAP" -> rewards.add(new PropertyCap(intValue, isUnlimited));
                            case "PLAYER_CAP" -> rewards.add(new TownPlayerCap(intValue, isUnlimited));
                            case "CHUNK_CAP" -> rewards.add(new ChunkCap(intValue, isUnlimited));
                            case "LANDMARKS_CAP" -> rewards.add(new LandmarkCap(intValue, isUnlimited));
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

                upgrades.put(
                        key,
                        new Upgrade(
                                row,
                                col,
                                key,
                                icon,
                                maxLevel,
                                newPrerequisites,
                                rewards
                        )
                );
            }
        }
    }

    private static boolean isInfiniteValue(ConfigurationSection section, String path) {
        String raw = section.getString(path);
        if (raw == null) return false;

        raw = raw.trim().toLowerCase();
        return raw.equals("infinity") || raw.equals("inf") || raw.equals("∞") || raw.equals("unlimited");
    }

    private static int parseIntValue(ConfigurationSection section, String path) {
        String raw = section.getString(path);
        if (raw == null) return 0;

        raw = raw.trim().toLowerCase();
        if (raw.equals("infinity") || raw.equals("inf") || raw.equals("∞") || raw.equals("unlimited")) {
            return 0; // valeur ignorée, on gère via isUnlimited = true
        }

        try {
            // On supporte aussi les valeurs écrites comme "+3"
            return Integer.parseInt(raw.replace("+", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    public Upgrade getUpgradeByName(String name) {
        return upgrades.get(name);
    }

    public Collection<Upgrade> getUpgrades() {
        return upgrades.values();
    }
}
