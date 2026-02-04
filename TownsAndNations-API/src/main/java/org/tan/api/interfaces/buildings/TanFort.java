package org.tan.api.interfaces.buildings;

import org.tan.api.interfaces.territory.TanTerritory;

public interface TanFort extends TanBuilding {

    TanTerritory getOwner();

    TanTerritory getOccupier();

}
