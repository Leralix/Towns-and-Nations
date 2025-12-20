package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.text.NumberUtil;

public class MoneyRequirement extends IndividualRequirementWithCost {

    private final TerritoryData territoryData;
    private final double amount;

    public MoneyRequirement(TerritoryData territoryData, double amount) {
        this.territoryData = territoryData;
        this.amount = amount;
    }

   @Override
    public String getLine(LangType langType) {
        double cost = getCost();
        if(isInvalid()){
            return Lang.REQUIREMENT_COST_NEGATIVE.get(langType, Double.toString(cost));
        } else {
            return Lang.REQUIREMENT_COST_POSITIVE.get(langType, Double.toString(cost));
        }
    }

    @Override
    public boolean isInvalid() {
        return territoryData.getBalance() < getCost();
    }

    public double getCost() {
        return NumberUtil.roundWithDigits(amount);
    }

    @Override
    public void actionDone() {
        double cost = getCost();
        territoryData.removeFromBalance(cost);
    }
}
