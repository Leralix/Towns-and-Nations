package org.leralix.tan.dataclass.property;

import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

public class TerritoryOwned extends AbstractOwner {

  private final String territoryID;

  public TerritoryOwned(TerritoryData territoryData) {
    super(OwnerType.TERRITORY);
    this.territoryID = territoryData.getID();
  }

  @Override
  public String getName() {
    TerritoryData territoryData = TerritoryUtil.getTerritory(territoryID);
    return territoryData.getColoredName();
  }

  @Override
  public boolean canAccess(ITanPlayer tanPlayer) {
    TerritoryData territoryData = TerritoryUtil.getTerritory(territoryID);
    if (territoryData == null) {
      return false;
    }
    if (!territoryData.isPlayerIn(tanPlayer)) {
      return false;
    }
    return territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.MANAGE_PROPERTY);
  }

  @Override
  public void addToBalance(double amount) {
    TerritoryData territoryData = TerritoryUtil.getTerritory(territoryID);
    if (territoryData == null) {
      return;
    }
    territoryData.addToBalance(amount);
  }
}
