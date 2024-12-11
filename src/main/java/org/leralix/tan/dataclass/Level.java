package org.leralix.tan.dataclass;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.leralix.tan.storage.legacy.UpgradeStorage;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import java.util.HashMap;
import java.util.Map;

import static org.leralix.tan.storage.legacy.UpgradeStorage.loadIntoMap;

public class Level {
    private int townLevel;

    Map<String,Integer> levelMap;

    //for json
    public Level(){
        levelMap = new HashMap<>();
        levelMap.put("townLevel",1);
        loadIntoMap(levelMap);
        levelMap.put("CITY_HALL",1);
    }

    public int getUpgradeLevel(String upgradeName){
        if(upgradeName.equals("TOWN_LEVEL")){
            return this.townLevel;
        }
        return levelMap.computeIfAbsent(upgradeName, k -> 0);
    }


    public int getTownLevel() {
        return this.townLevel;
    }
    public void townLevelUp(){
        this.townLevel++;
    }

    public int getPlayerCap() {
        return getTotalBenefits().get("PLAYER_CAP");
    }
    public int getChunkCap() {
        return getTotalBenefits().get("CHUNK_CAP");
    }
    public int getPropertyCap() {
        Integer propertyCap = getTotalBenefits().get("PROPERTY_CAP");
        if (propertyCap == null) {
            return 0;
        }
        return propertyCap;
    }


    public boolean isTownSpawnUnlocked() {
        return getTotalBenefits().get("TOWN_SPAWN_UNLOCKED") > 1;
    }




    public int getMoneyRequiredForLevelUp() {
        return getRequiredMoney(getTownLevel());
    }



    private int getRequiredMoney(int level) {
        FileConfiguration fg = ConfigUtil.getCustomConfig(ConfigTag.UPGRADES);
        ConfigurationSection section = fg.getConfigurationSection("townUpgrades");

        String expressionString = section.getString("TownLevelExpression");

        String squareMultName = "squareMultiplier";
        String flatMultName = "flatMultiplier";

        double squareMultiplier = section.getDouble(squareMultName);
        double flatMultiplier = section.getDouble(flatMultName);
        double base = section.getDouble("base");

        Expression expression = new ExpressionBuilder(expressionString)
                .variable("level")
                .variable(squareMultName)
                .variable(flatMultName)
                .variable("base")
                .build()
                .setVariable("level", level)
                .setVariable(squareMultName, squareMultiplier)
                .setVariable(flatMultName, flatMultiplier)
                .setVariable("base", base);
        return (int) expression.evaluate();
    }

    public void levelUp(TownUpgrade townUpgrade) {
        int currentLevel = this.getUpgradeLevel(townUpgrade.getName());
        this.levelMap.put(townUpgrade.getName(), currentLevel + 1);
    }

    public Map<String, Integer> getTotalBenefits() {
        Map<String, Integer> benefits = new HashMap<>();

        for(TownUpgrade townUpgrade : UpgradeStorage.getUpgrades()){

            String name = townUpgrade.getName();
            Map<String, Integer> map = townUpgrade.getBenefits();

            for(final Map.Entry<String, Integer> entry : map.entrySet()) {
                String benefitName = entry.getKey();
                Integer benefitValue = entry.getValue() * this.getUpgradeLevel(name);

                if (benefits.containsKey(benefitName)) {
                    benefits.put(benefitName, benefits.get(benefitName) + benefitValue);
                } else {
                    benefits.put(benefitName, benefitValue);
                }
            }
        }
        return benefits;
    }

    public int getBenefitsLevel(String benefitName) {
        return getTotalBenefits().get(benefitName);
    }

}
