package org.tan.TownsAndNations.DataClass;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.tan.TownsAndNations.storage.UpgradeStorage;
import org.tan.TownsAndNations.utils.ConfigUtil;

import java.util.HashMap;
import java.util.Map;

import static org.tan.TownsAndNations.storage.UpgradeStorage.loadIntoMap;

public class TownLevel {
    private int townLevel;
    private int playerCapLevel;
    private int chunkCapUpgrade;


    Map<String,Integer> levelMap;

    //for json
    public TownLevel(){
        //Will soon be deleted
        this.townLevel = 1;
        this.playerCapLevel = 0;
        this.chunkCapUpgrade = 0;
        //Will soon be deleted

        levelMap = new HashMap<>();
        levelMap.put("townLevel",1);
        loadIntoMap(levelMap);
        levelMap.put("CITY_HALL",1);
    }
    //for SQL, need to be update to the new system
    public TownLevel(int townLevel, int playerCapLevel, int chunkCapUpgrade, boolean townSpawnUnlocked){
        this.townLevel = townLevel;
        this.playerCapLevel = playerCapLevel;
        this.chunkCapUpgrade = chunkCapUpgrade;
    }

    public int getUpgradeLevel(String upgradeName){
        if (upgradeName == null) {
            return 0; // Retourne 0 si upgradeName est null
        }
        if(upgradeName.equals("TOWN_LEVEL")){
            return this.townLevel;
        }
        Integer level = levelMap.get(upgradeName);
        if (level == null) {
            levelMap.put(upgradeName, 0); // Initialise la valeur si elle n'existe pas
            return 0;
        }
        return level; // Retourne la valeur existante
    }


    public int getTownLevel() {
        return this.townLevel;
    }
    public void TownLevelUp(){
        this.townLevel = this.townLevel + 1;
    }

    //Will soon be deleted
    public int getPlayerCapLevel() {
        return this.playerCapLevel;
    }
    //Will soon be deleted
    public int getChunkCapLevel() {
        return this.chunkCapUpgrade;
    }

    public int getPlayerCap() {
        return getTotalBenefits().get("PLAYER_CAP");
    }

    public int getChunkCap() {
        return getTotalBenefits().get("CHUNK_CAP");
    }

    public boolean isTownSpawnUnlocked() {
        return getTotalBenefits().get("TOWN_SPAWN_UNLOCKED") > 1;
    }




    public int getMoneyRequiredTownLevel() {
        return getRequiredMoney("townLevelUpRequirement.yml", "TownExpression", getTownLevel());
    }



    private int getRequiredMoney(String configFileName, String expressionKey, int level) {
        FileConfiguration fg = ConfigUtil.getCustomConfig(configFileName);
        ConfigurationSection section = fg.getConfigurationSection("default");

        String expressionString = section.getString(expressionKey);

        double squareMultiplier = section.getDouble("squareMultiplier");
        double flatMultiplier = section.getDouble("flatMultiplier");
        double base = section.getDouble("base");

        Expression expression = new ExpressionBuilder(expressionString)
                .variable("level")
                .variable("squareMultiplier")
                .variable("flatMultiplier")
                .variable("base")
                .build()
                .setVariable("level", level)
                .setVariable("squareMultiplier", squareMultiplier)
                .setVariable("flatMultiplier", flatMultiplier)
                .setVariable("base", base);
        return (int) expression.evaluate();
    }

    public void levelUp(TownUpgrade townUpgrade) {
        this.levelMap.put(townUpgrade.getName(), this.levelMap.get(townUpgrade.getName()) + 1);
    }

    public Map<String, Integer> getTotalBenefits() {
        Map<String, Integer> benefits = new HashMap<>();

        for(TownUpgrade townUpgrade : UpgradeStorage.getUpgrades()){

            String name = townUpgrade.getName();
            HashMap<String, Integer> map = townUpgrade.getBenefits();

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

}
