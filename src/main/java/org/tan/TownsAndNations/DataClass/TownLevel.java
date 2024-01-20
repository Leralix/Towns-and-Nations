package org.tan.TownsAndNations.DataClass;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.tan.TownsAndNations.storage.TownDataStorage;
import org.tan.TownsAndNations.utils.ConfigUtil;

import static org.tan.TownsAndNations.TownsAndNations.isSqlEnable;

public class TownLevel {

    private int townLevel;
    private int playerCapLevel;
    private int chunkCapUpgrade;
    private boolean townSpawnUnlocked;

    //for json
    public TownLevel(){
        this.townLevel = 1;
        this.playerCapLevel = 0;
        this.chunkCapUpgrade = 0;
        this.townSpawnUnlocked = false;
    }
    //for SQL
    public TownLevel(int townLevel, int playerCapLevel, int chunkCapUpgrade, boolean townSpawnUnlocked){
        this.townLevel = townLevel;
        this.playerCapLevel = playerCapLevel;
        this.chunkCapUpgrade = chunkCapUpgrade;
        this.townSpawnUnlocked = townSpawnUnlocked;
    }

    public int getTownLevel() {
        return this.townLevel;
    }
    public void TownLevelUp(){
        this.townLevel = this.townLevel + 1;
    }
    public void setTownSpawnUnlocked(boolean townSpawnUnlocked) {
        this.townSpawnUnlocked = townSpawnUnlocked;
    }
    public boolean isTownSpawnUnlocked() {
        return this.townSpawnUnlocked;
    }

    public int getPlayerCapLevel() {
        return this.playerCapLevel;
    }
    public void PlayerCapLevelUp(){
        this.playerCapLevel = this.playerCapLevel + 1;
    }
    public int getPlayerCap() {
        FileConfiguration config =  ConfigUtil.getCustomConfig("config.yml");
        int basePlayerCap = config.getInt("TownStartingMembersCap");
        int multiplierPlayerCap = config.getInt("UpgradeMembers");

        return basePlayerCap + getPlayerCapLevel() * multiplierPlayerCap;
    }


    public int getChunkCapLevel() {
        return this.chunkCapUpgrade;
    }
    public void chunkCapLevelUp(){
        this.chunkCapUpgrade = this.chunkCapUpgrade + 1;
    }

    public int getChunkCap() {
        FileConfiguration config =  ConfigUtil.getCustomConfig("config.yml");
        int baseChunkCap = config.getInt("TownStartingChunksCap");
        return baseChunkCap + getChunkCapLevel() * getMultiplierChunkCap();
    }

    public int getMultiplierPlayerCap() {
        FileConfiguration config =  ConfigUtil.getCustomConfig("config.yml");
        int multiplier = config.getInt("UpgradeMembers");
        return multiplier;
    }

    public int getMultiplierChunkCap() {
        FileConfiguration config =  ConfigUtil.getCustomConfig("config.yml");
        int multiplier = config.getInt("TownUpgradeChunk");
        return multiplier;
    }


    public void setChunkCapLevel(int chunkCapUpgrade) {
        this.chunkCapUpgrade = chunkCapUpgrade;
    }

    public int getMoneyRequiredTownLevel() {
        return getRequiredMoney("townLevelUpRequirement.yml", "TownExpression", getTownLevel());
    }

    public int getMoneyRequiredPlayerCap() {
        return getRequiredMoney("townLevelUpRequirement.yml", "expression", getPlayerCapLevel());
    }

    public int getMoneyRequiredChunkCap() {
        return getRequiredMoney("townLevelUpRequirement.yml", "expression", getChunkCapLevel());
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

}
