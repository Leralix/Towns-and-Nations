package org.leralix.tan.data.upgrade;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.data.upgrade.rewards.AggregatableStat;
import org.leralix.tan.data.upgrade.rewards.IndividualStat;
import org.leralix.tan.data.upgrade.rewards.StatsType;
import org.leralix.tan.data.upgrade.rewards.bool.EnableMobBan;
import org.leralix.tan.data.upgrade.rewards.bool.EnableTownSpawn;
import org.leralix.tan.data.upgrade.rewards.list.BiomeStat;
import org.leralix.tan.data.upgrade.rewards.list.PermissionList;
import org.leralix.tan.data.upgrade.rewards.numeric.*;
import org.leralix.tan.data.upgrade.rewards.percentage.LandmarkBonus;
import org.leralix.tan.utils.constants.Constants;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class TerritoryStats {

    private int mainLevel;
    private Map<String, Integer> level;
    private final StatsType statsType;

    public TerritoryStats(StatsType statsType){
        this.mainLevel = 1;
        this.level = new HashMap<>();
        this.level.put("CITY_HALL", 1); // Default upgrade
        this.statsType = statsType;
    }

    public int getLevel(Upgrade upgrade){
        if(level == null){
            level = new HashMap<>();
        }
        if(!level.containsKey(upgrade.getID())){
            return 0;
        }
        return level.get(upgrade.getID());
    }

    public void levelUp(Upgrade townUpgrade) {
        String key = townUpgrade.getID();
        if(!level.containsKey(key)){
            level.put(key, 1);
            return;
        }
        level.put(key, level.get(key) + 1);
    }

    public int getMainLevel() {
        return mainLevel;
    }

    public void levelUpMain(){
        mainLevel++;
    }

    public <T extends IndividualStat & AggregatableStat<T>> T getStat(Class<T> rewardClass) {
        List<T> stats = new ArrayList<>();
        for (Upgrade upgrade : Constants.getUpgradeStorage().getUpgrades(statsType)) {
            int currentLevel = getLevel(upgrade);
            if (currentLevel == 0) continue;

            for (IndividualStat reward : upgrade.getRewards()) {
                if (rewardClass.isInstance(reward)) {
                    stats.add(rewardClass.cast(reward).scale(currentLevel));
                }
            }
        }

        if (stats.isEmpty()) {
            try {
                return rewardClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException("Failed to create default instance of " + rewardClass.getName(), e);
            }
        }

        return stats.getFirst().aggregate(stats);
    }


    public Collection<IndividualStat> getAllStats() {

        List<IndividualStat> allStats = new ArrayList<>();
        allStats.add(getStat(ChunkCap.class));
        allStats.add(getStat(ChunkCost.class));
        allStats.add(getStat(ChunkUpkeepCost.class));
        // Town only stats.
        if(statsType == StatsType.TOWN){
            allStats.add(getStat(LandmarkCap.class));
            allStats.add(getStat(PropertyCap.class));
            allStats.add(getStat(TownPlayerCap.class));
            allStats.add(getStat(LandmarkBonus.class));
            allStats.add(getStat(EnableTownSpawn.class));
            allStats.add(getStat(EnableMobBan.class));
        }
        allStats.add(getStat(PermissionList.class));
        allStats.add(getStat(BiomeStat.class));
        return allStats;
    }


    public int getMoneyRequiredForLevelUp() {
        return Constants.getUpgradeExpression().getRequiredMoney(statsType, mainLevel);
    }

    private int getRequiredMoney(int level) {
        FileConfiguration fg = ConfigUtil.getCustomConfig(ConfigTag.UPGRADE);
        String sectionName = switch (statsType) {
            case REGION -> "regionLevelExpression";
            case NATION -> "nationLevelExpression";
            case TOWN -> "townLevelExpression";
        };
        ConfigurationSection section = fg.getConfigurationSection(sectionName);
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
}
