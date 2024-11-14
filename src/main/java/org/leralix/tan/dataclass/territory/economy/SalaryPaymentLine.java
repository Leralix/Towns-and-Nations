package org.leralix.tan.dataclass.territory.economy;

import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.lang.Lang;

public class SalaryPaymentLine extends ProfitLine {
    double totalSalaries;
    public SalaryPaymentLine(ITerritoryData territoryData) {
        super();
        totalSalaries = 0;

        for(RankData rankData : territoryData.getAllRanks()){
            totalSalaries += rankData.getSalary() * rankData.getPlayersID().size();
        }
    }

    @Override
    public String getLine() {
        return Lang.PLAYER_SALARY_LINE.get(getColoredMoney(totalSalaries));
    }
}
