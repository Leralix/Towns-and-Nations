package org.leralix.tan.dataclass;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.storage.legacy.UpgradeStorage;
import org.leralix.tan.upgrade.Upgrade;

import java.util.HashMap;
import java.util.Map;

@Deprecated(since = "0.16.0", forRemoval = true)
public class Level {
    private int townLevel;

    Map<String,Integer> levelMap;

    //for json
    public Level(){
        levelMap = new HashMap<>();
        levelMap.put("townLevel",1);
        // UpgradeStorage.loadIntoMap(levelMap);
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
        return getBenefitsLevel("CHUNK_CAP");
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
        FileConfiguration fg = ConfigUtil.getCustomConfig(ConfigTag.UPGRADE);
        ConfigurationSection section = fg.getConfigurationSection("townLevelExpression");
        String expressionString = section.getString("LevelExpression");
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

    public void levelUp(Upgrade upgrade) {
        int currentLevel = this.getUpgradeLevel(upgrade.getID());
        this.levelMap.put(upgrade.getID(), currentLevel + 1);
    }

    public Map<String, Integer> getTotalBenefits() {
        Map<String, Integer> benefits = new HashMap<>();

        for(Upgrade upgrade : UpgradeStorage.getUpgrades()){

            String name = upgrade.getID();
            // The new 'Upgrade' class uses 'rewards' (List<IndividualStat>) instead of a direct 'benefits' map.
            // A more significant refactoring is needed here to correctly calculate total benefits from IndividualStat objects.
            // For now, we'll skip processing benefits to allow compilation.
            // Map<String, Integer> map = upgrade.getBenefits(); // This method no longer exists
            // For now, we'll assume no benefits are added from this upgrade.
            // If benefits are needed, the logic here must be updated to process 'upgrade.getRewards()'
            // and extract benefit name/value from 'IndividualStat' implementations.

        }
        return benefits;
    }

    public int getBenefitsLevel(String benefitName) {
        Map<String, Integer> benefits = this.getTotalBenefits();
        return benefits.getOrDefault(benefitName,0);
    }

}
