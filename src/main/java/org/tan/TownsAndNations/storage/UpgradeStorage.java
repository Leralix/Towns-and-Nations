package org.tan.TownsAndNations.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.tan.TownsAndNations.DataClass.TownUpgrade;
import org.tan.TownsAndNations.utils.ConfigUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeStorage {

    private static final HashMap<String, TownUpgrade> UpgradeMap = new HashMap<>();

    public static void initialize() {
        FileConfiguration upgradeConfig = ConfigUtil.getCustomConfig("townUpgrades.yml");
        ConfigurationSection upgradesSection = upgradeConfig.getConfigurationSection("upgrades");

        if (upgradesSection != null) {
            for (String key : upgradesSection.getKeys(false)) {
                String name = key;
                int col = upgradesSection.getInt(key + ".col");
                int row = upgradesSection.getInt(key + ".row");
                String itemCode = upgradesSection.getString(key + ".itemCode");
                int maxLevel = upgradesSection.getInt(key + ".maxLevel");
                List<Integer> cost = upgradesSection.getIntegerList(key + ".cost");
                List<String> prerequisites = upgradesSection.getStringList(key + ".prerequisites");

                ConfigurationSection benefitsSection = upgradesSection.getConfigurationSection(key + ".benefits");
                Map<String, Integer> benefits = new HashMap<>();
                if (benefitsSection != null) {
                    benefits = new HashMap<>();
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
