package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.Placeholder;

public class LeaderRequirement extends IndividualRequirement {

    private final Territory territoryData;
    private final ITanPlayer tanPlayer;

    public LeaderRequirement(Territory territoryData, ITanPlayer tanPlayer) {
        this.territoryData = territoryData;
        this.tanPlayer = tanPlayer;
    }


    @Override
    public Placeholder getLine() {
        if (isInvalid()) {
            return Lang.REQUIREMENT_TERRITORY_LEADER_NEGATIVE;
        } else {
            return Lang.REQUIREMENT_TERRITORY_LEADER_POSITIVE;
        }
    }

    @Override
    public boolean isInvalid() {
        return !territoryData.isLeader(tanPlayer);
    }
}
