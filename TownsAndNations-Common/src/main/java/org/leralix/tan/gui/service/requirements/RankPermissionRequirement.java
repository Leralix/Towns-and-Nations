package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public class RankPermissionRequirement extends IndividualRequirement {

    private final TerritoryData territoryData;
    private final ITanPlayer tanPlayer;
    private final RolePermission requiredPermission;

    public RankPermissionRequirement(TerritoryData territoryData, ITanPlayer tanPlayer, RolePermission requiredPermission) {
        this.territoryData = territoryData;
        this.tanPlayer = tanPlayer;
        this.requiredPermission = requiredPermission;
    }


    @Override
    public String getLine(LangType langType) {
        if (isInvalid()) {
            return Lang.REQUIREMENT_RANK_PERMISSION_NEGATIVE.get(langType, requiredPermission.getName(langType));
        } else {
            return Lang.REQUIREMENT_RANK_PERMISSION_POSITIVE.get(langType, requiredPermission.getName(langType));
        }
    }

    @Override
    public boolean isInvalid() {
        return !territoryData.doesPlayerHavePermission(tanPlayer, requiredPermission);
    }
}
