package org.tan.towns_and_nations.DataClass;

import net.objecthunter.exp4j.Expression;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.tan.towns_and_nations.TownsAndNations;
import org.tan.towns_and_nations.utils.YAMLutil;

public class TownLevel {

    private int townLevel;
    private int playerCapLevel;
    private int chunkCapUpgrade;

    public TownLevel(){
        this.townLevel = 1;
        this.playerCapLevel = 0;
        this.chunkCapUpgrade = 0;

    }
    public int getTownLevel() {
        return this.townLevel;
    }

    public void setTownLevel(int townLevel) {
        this.townLevel = townLevel;
    }

    public int getPlayerCapLevel() {
        return this.playerCapLevel;
    }
    public int getPlayerCap() {
        FileConfiguration config =  TownsAndNations.getCustomConfig("config.yml");
        int basePlayerCap = config.getInt("TownStartingMembersCap");
        int multiplierPlayerCap = config.getInt("UpgradeMembers");

        return basePlayerCap + getPlayerCapLevel() * multiplierPlayerCap;
    }

    public void setPlayerCapLevel(int playerCapLevel) {
        this.playerCapLevel = playerCapLevel;
    }

    public int getChunkCapLevel() {
        return this.chunkCapUpgrade;
    }
    public int getChunkCap() {
        FileConfiguration config =  TownsAndNations.getCustomConfig("config.yml");
        int baseChunkCap = config.getInt("TownStartingChunksCap");
        int multiplierChunkCap = config.getInt("UpgradeMaxChunk");
        return baseChunkCap + getPlayerCapLevel() * multiplierChunkCap;
    }

    public void setChunkCapLevel(int chunkCapUpgrade) {
        this.chunkCapUpgrade = chunkCapUpgrade;
    }

    public int getMoneyRequiredTownLevel() {
        return getRequiredMoney("TownLevelUpRequirement.yml", "TownExpression", getTownLevel());
    }

    public int getMoneyRequiredPlayerCap() {
        return getRequiredMoney("TownLevelUpRequirement.yml", "expression", getPlayerCapLevel());
    }

    public int getMoneyRequiredChunkCap() {
        return getRequiredMoney("TownLevelUpRequirement.yml", "expression", getChunkCapLevel());
    }

    private int getRequiredMoney(String configFileName, String expressionKey, int level) {
        FileConfiguration fg = TownsAndNations.getPlugin().getCustomConfig(configFileName);
        ConfigurationSection section = fg.getConfigurationSection("default");
        Expression price = YAMLutil.getExpression(section, expressionKey);
        price.setVariable("level", level);
        return (int) price.evaluate();
    }

}
