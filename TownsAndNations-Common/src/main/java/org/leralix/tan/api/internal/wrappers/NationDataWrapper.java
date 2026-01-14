package org.leralix.tan.api.internal.wrappers;

import org.leralix.tan.dataclass.territory.NationData;
import org.tan.api.interfaces.TanNation;
import org.tan.api.interfaces.TanTerritory;

public class NationDataWrapper extends TerritoryDataWrapper implements TanNation {

    private final NationData nationData;

    private NationDataWrapper(NationData nationData) {
        super(nationData);
        this.nationData = nationData;
    }

    public static NationDataWrapper of(NationData nationData) {
        if (nationData == null) {
            return null;
        }
        return new NationDataWrapper(nationData);
    }

    @Override
    public TanTerritory getCapital() {
        return TerritoryDataWrapper.of(nationData.getCapital());
    }
}
