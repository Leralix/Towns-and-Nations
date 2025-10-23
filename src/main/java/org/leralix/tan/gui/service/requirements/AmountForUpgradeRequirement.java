package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.upgrade.Upgrade;
import org.leralix.tan.utils.text.NumberUtil;

import java.util.List;

public class AmountForUpgradeRequirement extends IndividualRequirementWithCost {

    private final TerritoryData territoryData;
    private final Upgrade upgrade;
    private final List<Integer> costs;

    public AmountForUpgradeRequirement(TerritoryData territoryData, Upgrade upgrade, List<Integer> costs) {
        this.territoryData = territoryData;
        this.upgrade = upgrade;
        this.costs = costs;
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
        int level = territoryData.getNewLevel().getLevel(upgrade);
        if (costs.size() <= level)
            return NumberUtil.roundWithDigits(costs.getLast());
        return NumberUtil.roundWithDigits(costs.get(level));
    }

    @Override
    public void actionDone() {
        territoryData.addToBalance(-getCost());
    }
}
