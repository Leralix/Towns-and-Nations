package org.leralix.tan.data.territory.economy;

import org.leralix.tan.data.territory.NationData;
import org.leralix.tan.data.territory.Territory;

public class NationTaxLine extends TaxProfitLine {

    public NationTaxLine(NationData nationData) {
        super(nationData);
        double tax = nationData.getTax();
        for (Territory regionData : nationData.getVassalsInternal()) {
            if (regionData.getBalance() > tax)
                actualTaxes += tax;
            else
                missingTaxes += tax;
        }
    }
}
