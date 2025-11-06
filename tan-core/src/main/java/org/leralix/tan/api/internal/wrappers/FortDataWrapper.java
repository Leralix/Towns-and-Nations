package org.leralix.tan.api.internal.wrappers;

import org.leralix.lib.position.Vector3D;
import org.leralix.tan.wars.fort.Fort;
import org.tan.api.interfaces.TanFort;
import org.tan.api.interfaces.TanTerritory;

public class FortDataWrapper implements TanFort {

  private final Fort fort;

  private FortDataWrapper(Fort fort) {
    this.fort = fort;
  }

  public static FortDataWrapper of(Fort fort) {
    if (fort == null) {
      return null;
    }
    return new FortDataWrapper(fort);
  }

  @Override
  public String getID() {
    return fort.getID();
  }

  @Override
  public String getName() {
    return fort.getName();
  }

  @Override
  public Vector3D getFlagPosition() {
    return fort.getPosition();
  }

  @Override
  public TanTerritory getOwner() {
    return TerritoryDataWrapper.of(fort.getOwner());
  }

  @Override
  public TanTerritory getOccupier() {
    return TerritoryDataWrapper.of(fort.getOccupier());
  }
}
