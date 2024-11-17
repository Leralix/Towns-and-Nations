package org.leralix.tan.dataclass.territory.economy;

import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.lang.Lang;

public class SubjectTaxLine extends ProfitLine{

    double totalTaxes = 0;
    double missingTaxes = 0;

    public SubjectTaxLine(RegionData regionData){
        double tax = regionData.getTax();
        for(ITerritoryData townData : regionData.getVassals()){
            if(townData.getBalance() > tax)
                totalTaxes += tax;
            else
                missingTaxes += tax;
        }
    }

    @Override
    public String getLine() {
        if(missingTaxes > 0)
            return Lang.PLAYER_TAX_MISSING_LINE.get(getColoredMoney(totalTaxes), missingTaxes);
        else
            return Lang.PLAYER_TAX_LINE.get(getColoredMoney(totalTaxes));
    }
}
