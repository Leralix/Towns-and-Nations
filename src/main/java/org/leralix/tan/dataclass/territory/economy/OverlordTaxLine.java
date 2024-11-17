package org.leralix.tan.dataclass.territory.economy;

import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.lang.Lang;

public class OverlordTaxLine extends ProfitLine {

    double tax;

    public OverlordTaxLine(ITerritoryData territoryData){
        ITerritoryData overlordData = territoryData.getOverlord();
        if(overlordData == null)
            return;
        tax = - overlordData.getTax();
    }

    public String getLine() {
        return Lang.OVERLORD_TAX_LINE.get(getColoredMoney(tax));
    }



}
