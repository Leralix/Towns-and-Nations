package org.leralix.tan.data.building.property.owner;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.tan.api.interfaces.TanPlayer;

public class TerritoryOwned extends AbstractOwner {

    private final String territoryID;

    public TerritoryOwned(TerritoryData territoryData) {
        super(OwnerType.TERRITORY);
        this.territoryID = territoryData.getID();
    }

    @Override
    public String getName() {
        TerritoryData territoryData = TerritoryUtil.getTerritory(territoryID);
        return territoryData.getName();
    }

    @Override
    public String getColoredName() {
        TerritoryData territoryData = TerritoryUtil.getTerritory(territoryID);
        return territoryData.getColoredName();
    }

    @Override
    public boolean canAccess(TanPlayer tanPlayer) {
       return canAccess(PlayerDataStorage.getInstance().get(tanPlayer.getID()));
    }

    private boolean canAccess(ITanPlayer tanPlayer) {
        TerritoryData territoryData = TerritoryUtil.getTerritory(territoryID);
        if(territoryData == null){
            return false;
        }
        if(!territoryData.isPlayerIn(tanPlayer)){
            return false;
        }
        return territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.MANAGE_PROPERTY);
    }

    @Override
    public void addToBalance(double amount) {
        TerritoryData territoryData = TerritoryUtil.getTerritory(territoryID);
        if(territoryData == null){
            return;
        }
        territoryData.addToBalance(amount);
    }

    @Override
    public String getID() {
        return territoryID;
    }
}
