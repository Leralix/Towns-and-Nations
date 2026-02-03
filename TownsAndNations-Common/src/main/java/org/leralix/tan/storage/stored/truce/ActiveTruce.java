package org.leralix.tan.storage.stored.truce;

import org.leralix.tan.data.territory.TerritoryData;

import java.time.Instant;

public class ActiveTruce {

    private final String territoryID1;
    private final String territoryID2;
    private final long endOfTruce;

    public ActiveTruce(TerritoryData territoryData, TerritoryData secondTerritory, int nbHours){
        this.territoryID1 = territoryData.getID();
        this.territoryID2 = secondTerritory.getID();

        endOfTruce = Instant.now().plusSeconds((long) nbHours * 60 * 60).toEpochMilli();
    }

    public String getTerritoryID1() {
        return territoryID1;
    }

    public String getTerritoryID2() {
        return territoryID2;
    }

    public long getEndOfTruce() {
        return endOfTruce;
    }
}
