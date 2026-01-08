package org.leralix.tan.api.internal.wrappers;

import org.leralix.tan.dataclass.territory.KingdomData;
import org.tan.api.interfaces.TanKingdom;
import org.tan.api.interfaces.TanTerritory;

public class KingdomDataWrapper extends TerritoryDataWrapper implements TanKingdom {

    private final KingdomData kingdomData;

    private KingdomDataWrapper(KingdomData kingdomData) {
        super(kingdomData);
        this.kingdomData = kingdomData;
    }

    public static KingdomDataWrapper of(KingdomData kingdomData) {
        if (kingdomData == null) {
            return null;
        }
        return new KingdomDataWrapper(kingdomData);
    }

    @Override
    public TanTerritory getCapital() {
        return TerritoryDataWrapper.of(kingdomData.getCapital());
    }
}
