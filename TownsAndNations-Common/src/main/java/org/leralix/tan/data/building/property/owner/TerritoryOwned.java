package org.leralix.tan.data.building.property.owner;

import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.tan.api.interfaces.TanPlayer;

public class TerritoryOwned extends AbstractOwner {

    private final String territoryID;

    public TerritoryOwned(Territory territoryData) {
        super(OwnerType.TERRITORY);
        this.territoryID = territoryData.getID();
    }

    @Override
    public String getName() {
        Territory territoryData = TerritoryUtil.getTerritory(territoryID);
        return territoryData.getName();
    }

    @Override
    public String getColoredName() {
        Territory territoryData = TerritoryUtil.getTerritory(territoryID);
        return territoryData.getColoredName();
    }

    @Override
    public boolean canAccess(TanPlayer tanPlayer) {
       return canAccess(TownsAndNations.getPlugin().getPlayerDataStorage().get(tanPlayer.getID()));
    }

    private boolean canAccess(ITanPlayer tanPlayer) {
        Territory territoryData = TerritoryUtil.getTerritory(territoryID);
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
        Territory territoryData = TerritoryUtil.getTerritory(territoryID);
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
