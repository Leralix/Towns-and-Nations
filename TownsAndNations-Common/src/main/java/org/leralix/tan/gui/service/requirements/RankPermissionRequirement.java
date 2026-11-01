package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.Placeholder;

public class RankPermissionRequirement extends IndividualRequirement {

    private final Territory territoryData;
    private final ITanPlayer tanPlayer;
    private final RolePermission requiredPermission;

    public RankPermissionRequirement(Territory territoryData, ITanPlayer tanPlayer, RolePermission requiredPermission) {
        this.territoryData = territoryData;
        this.tanPlayer = tanPlayer;
        this.requiredPermission = requiredPermission;
    }


    @Override
    public Placeholder getLine() {
        if (isInvalid()) {
            return Lang.REQUIREMENT_RANK_PERMISSION_NEGATIVE.get(requiredPermission.getName());
        } else {
            return Lang.REQUIREMENT_RANK_PERMISSION_POSITIVE.get(requiredPermission.getName());
        }
    }

    @Override
    public boolean isInvalid() {
        return !territoryData.doesPlayerHavePermission(tanPlayer, requiredPermission);
    }
}
