package org.leralix.tan.upgrade;


import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.gui.service.requirements.upgrade.LevelUpgradeRequirement;
import org.leralix.tan.gui.service.requirements.upgrade.OtherUpgradeRequirementBuilder;
import org.leralix.tan.gui.service.requirements.upgrade.UpgradeCostRequirement;
import org.leralix.tan.gui.service.requirements.upgrade.UpgradeRequirement;

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
                List<Integer> cost = upgradesSection.getIntegerList(key + ".cost");

                // Modification here for prerequisites

                List<UpgradeRequirement> newPrerequisites = new ArrayList<>();
                newPrerequisites.add(new UpgradeCostRequirement(cost));


                ConfigurationSection prerequisiteSection = upgradesSection.getConfigurationSection(key + ".prerequisites");
                if (prerequisiteSection != null) {
                    for (String prerequisiteKey : prerequisiteSection.getKeys(false)) {
                        int requiredLevel = prerequisiteSection.getInt(prerequisiteKey);
                        if(prerequisiteKey.equals("TOWN_LEVEL")){
                            newPrerequisites.add(new LevelUpgradeRequirement(requiredLevel));
                        }
                        else {
                            newPrerequisites.add(new OtherUpgradeRequirementBuilder(prerequisiteKey, requiredLevel));
                        }
                    }
                }

                ConfigurationSection benefitsSection = upgradesSection.getConfigurationSection(key + ".benefits");
                HashMap<String, Integer> benefits = new HashMap<>();
                if (benefitsSection != null) {
                    for (String benefitKey : benefitsSection.getKeys(false)) {
                        benefits.put(benefitKey, benefitsSection.getInt(benefitKey));
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
                                cost,
                                Collections.emptyList()
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
