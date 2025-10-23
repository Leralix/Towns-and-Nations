package org.leralix.tan.upgrade;


import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.gui.service.requirements.model.AllWoodScope;
import org.leralix.tan.gui.service.requirements.model.MaterialScope;
import org.leralix.tan.gui.service.requirements.upgrade.*;
import org.leralix.tan.upgrade.rewards.IndividualStat;
import org.leralix.tan.upgrade.rewards.bool.EnableMobBan;
import org.leralix.tan.upgrade.rewards.bool.EnableTownSpawn;
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
                        if (ressourcesKey.equals("ALL_WOOD")) {
                            newPrerequisites.add(new ItemRequirementBuilder(new AllWoodScope(), number));
                        }
                        newPrerequisites.add(new ItemRequirementBuilder(new MaterialScope(Material.valueOf(ressourcesKey)), number));
                    }
                }

                //Benefits
                ConfigurationSection benefitsSection = upgradesSection.getConfigurationSection(key + ".benefits");
                List<IndividualStat> rewards = new ArrayList<>();
                if (benefitsSection != null) {
                    for (String benefitKey : benefitsSection.getKeys(false)) {
                        switch (benefitKey) {
                            case "PROPERTY_CAP" ->
                                    rewards.add(new PropertyCap(benefitsSection.getInt(benefitKey), false));
                            case "PLAYER_CAP" ->
                                    rewards.add(new TownPlayerCap(benefitsSection.getInt(benefitKey), false));
                            case "CHUNK_CAP" -> rewards.add(new ChunkCap(benefitsSection.getInt(benefitKey), false));
                            case "MAX_LANDMARKS" ->
                                    rewards.add(new LandmarkCap(benefitsSection.getInt(benefitKey), false));
                            case "UNLOCK_TOWN_SPAWN" -> rewards.add(new EnableTownSpawn(true));
                            case "UNLOCK_MOB_BAN" -> rewards.add(new EnableMobBan(true));
                            case "LANDMARK_BONUS" ->
                                    rewards.add(new LandmarkBonus(benefitsSection.getDouble(benefitKey) / 100));

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

    public Upgrade getUpgradeByName(String name) {
        return upgrades.get(name);
    }

    public Collection<Upgrade> getUpgrades() {
        return upgrades.values();
    }
}
