package org.leralix.tan.dataclass.territory.economy;

import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;

public class SubjectTaxLine extends TaxProfitLine {

    public SubjectTaxLine(TerritoryData territoryData) {
        super(territoryData);

        double tax = territoryData.getTax();
        for (TerritoryData vassal : territoryData.getVassals()) {
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
