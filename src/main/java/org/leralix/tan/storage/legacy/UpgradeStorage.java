package org.leralix.tan.storage.legacy;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.TownUpgrade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeStorage {

    private static final HashMap<String, TownUpgrade> UpgradeMap = new HashMap<>();

    public static void init() {
        FileConfiguration upgradeConfig = ConfigUtil.getCustomConfig(ConfigTag.TAN_UPGRADE);
        ConfigurationSection upgradesSection = upgradeConfig.getConfigurationSection("upgrades");

        if (upgradesSection != null) {
            for (String key : upgradesSection.getKeys(false)) {
                String name = key;
                int col = upgradesSection.getInt(key + ".col");
                int row = upgradesSection.getInt(key + ".row");
                String itemCode = upgradesSection.getString(key + ".itemCode");
                int maxLevel = upgradesSection.getInt(key + ".maxLevel");
                List<Integer> cost = upgradesSection.getIntegerList(key + ".cost");

                // Modification here for prerequisites
                ConfigurationSection prerequisiteSection = upgradesSection.getConfigurationSection(key + ".prerequisites");
                HashMap<String, Integer> prerequisites = new HashMap<>();
                if (prerequisiteSection != null) {
                    for (String prerequisiteKey : prerequisiteSection.getKeys(false)) {
                        prerequisites.put(prerequisiteKey, prerequisiteSection.getInt(prerequisiteKey));
                    }
                }

                ConfigurationSection benefitsSection = upgradesSection.getConfigurationSection(key + ".benefits");
                HashMap<String, Integer> benefits = new HashMap<>();
                if (benefitsSection != null) {
                    for (String benefitKey : benefitsSection.getKeys(false)) {
                        benefits.put(benefitKey, benefitsSection.getInt(benefitKey));
                    }
                }

                UpgradeMap.put(name,new TownUpgrade(
                        name,
                        col,
                        row,
                        itemCode,
                        maxLevel,
                        cost,
                        prerequisites,
                        benefits
                ));
            }
        }
    }

    public static TownUpgrade getUpgrade(String name) {
        return UpgradeMap.get(name);
    }

    public static List<TownUpgrade> getUpgrades() {
        return new ArrayList<>(UpgradeMap.values());
    }


    public static  void loadIntoMap(Map<String,Integer> map){
        for(TownUpgrade upgrade : UpgradeStorage.getUpgrades()){
            map.put(upgrade.getName(),0);
        }
    }

}
