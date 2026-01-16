package org.leralix.tan.war;

import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.utils.constants.Constants;

public final class WarPowerBalance {

    private WarPowerBalance() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isWarAllowed(TerritoryData attacker, TerritoryData defender) {
        if (attacker == null || defender == null) {
            return false;
        }
        if (!Constants.isWarPowerBalanceEnabled()) {
            return true;
        }

        double attackerPower = computePower(attacker);
        double defenderPower = computePower(defender);

        if (defenderPower <= 0.0) {
            return true;
        }

        return attackerPower / defenderPower <= Constants.getMaxWarPowerRatio();
    }

    public static double computePower(TerritoryData territoryData) {
        if (territoryData == null) {
            return 0.0;
        }

        int level = territoryData.getNewLevel().getMainLevel();
        int claimedChunks = territoryData.getNumberOfClaimedChunk();
        int members = territoryData.getPlayerIDList() == null ? 0 : territoryData.getPlayerIDList().size();

        return level * 100.0 + claimedChunks + members * 10.0;
    }
}
