package org.leralix.tan.dataclass.territory.economy;

import org.leralix.tan.dataclass.territory.NationData;
import org.leralix.tan.dataclass.territory.TerritoryData;

public class NationTaxLine extends TaxProfitLine {

    public NationTaxLine(NationData nationData) {
        super(nationData);
        double tax = nationData.getTax();
        for (TerritoryData regionData : nationData.getVassals()) {
            if (regionData.getBalance() > tax)
                actualTaxes += tax;
            else
                missingTaxes += tax;
        }
    }
}
