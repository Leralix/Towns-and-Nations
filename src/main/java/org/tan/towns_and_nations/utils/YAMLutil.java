package org.tan.towns_and_nations.utils;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.configuration.ConfigurationSection;

import java.math.BigDecimal;

public class YAMLutil {



    public static Expression getExpression(ConfigurationSection section, String expressionKey) {
        String expressionString = section.getString(expressionKey);
        net.objecthunter.exp4j.Expression expression = new ExpressionBuilder(expressionString).variables(section.getKeys(false)).build();

        // Set variables
        for (String variable : section.getKeys(false)) {
            if (variable.equals(expressionKey)) continue;
            double variableValue = section.getDouble(variable);
            expression.setVariable(variable, variableValue);
        }

        return expression;
    }


}
