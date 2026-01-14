package org.leralix.tan.dataclass.territory.economy;

import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;

public class SubjectTaxLine extends TaxProfitLine {

    public SubjectTaxLine(RegionData regionData) {
        super(regionData);
        double tax = regionData.getTax();
        for (TerritoryData townData : regionData.getVassals()) {
            if (townData.getBalance() > tax)
                actualTaxes += tax;
            else
                missingTaxes += tax;
        }
    }
}
