package org.leralix.tan.utils.constants;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.leralix.tan.data.upgrade.rewards.StatsType;

public class UpgradeExpression {

    private static final String LEVEL = "level";
    private static final String SQUARE_MULT_NAME = "squareMultiplier";
    private static final String FLAT_MULT_NAME = "flatMultiplier";
    private static final String BASE_VALUE_NAME = "base";


    private final Expression townLevelCalculator;
    private final Expression regionLevelCalculator;
    private final Expression nationLevelCalculator;

    public UpgradeExpression(ConfigurationSection section){
        this.townLevelCalculator = createExpression(section.getConfigurationSection("townLevelExpression"));
        this.regionLevelCalculator = createExpression(section.getConfigurationSection("regionLevelExpression"));
        this.nationLevelCalculator = createExpression(section.getConfigurationSection("nationLevelExpression"));

    }

    public int getRequiredMoney(StatsType type, int level){
        return (int) switch (type) {
            case TOWN -> townLevelCalculator.setVariable(LEVEL, level).evaluate();
            case REGION -> regionLevelCalculator.setVariable(LEVEL, level).evaluate();
            case NATION -> nationLevelCalculator.setVariable(LEVEL, level).evaluate();
        };
    }

    public static Expression createExpression(ConfigurationSection section) {
        String expressionString = section.getString("LevelExpression", "(squareMultiplier * level ^ 2 + flatMultiplier * level + base) * 10");

        double squareMultiplier = section.getDouble(SQUARE_MULT_NAME);
        double flatMultiplier = section.getDouble(FLAT_MULT_NAME);
        double base = section.getDouble("base");

        return new ExpressionBuilder(expressionString)
                .variable(LEVEL)
                .variable(SQUARE_MULT_NAME)
                .variable(FLAT_MULT_NAME)
                .variable(BASE_VALUE_NAME)
                .build()
                .setVariable(SQUARE_MULT_NAME, squareMultiplier)
                .setVariable(FLAT_MULT_NAME, flatMultiplier)
                .setVariable(BASE_VALUE_NAME, base);
    }

}
