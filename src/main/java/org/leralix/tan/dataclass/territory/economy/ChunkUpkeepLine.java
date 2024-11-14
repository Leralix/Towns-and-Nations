package org.leralix.tan.dataclass.territory.economy;

import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.lang.Lang;

public class ChunkUpkeepLine extends ProfitLine {
    private final double totalUpkeep;

    public ChunkUpkeepLine(ITerritoryData territoryData) {
        super();
        totalUpkeep = territoryData.getNumberOfClaimedChunk() * territoryData.getChunkUpkeepCost();
    }

    @Override
    public String getLine() {
        return Lang.TERRITORY_UPKEEP_LINE.get(getColoredMoney(totalUpkeep));
    }
}
