package org.leralix.tan.data.territory.economy;

import org.leralix.tan.data.territory.TerritoryData;

public class SubjectTaxLine extends TaxProfitLine {

    public SubjectTaxLine(TerritoryData territoryData) {
        super(territoryData);

        double tax = territoryData.getTax();
        for (TerritoryData vassal : territoryData.getVassalsInternal()) {
            if (vassal == null) {
                continue;
            }
            if (vassal.getBalance() > tax) {
                actualTaxes += tax;
            } else {
                missingTaxes += tax;
            }
        }
    }
}
