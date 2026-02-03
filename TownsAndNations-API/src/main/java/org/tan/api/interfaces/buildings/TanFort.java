package org.tan.api.interfaces.buildings;

import org.leralix.lib.position.Vector3D;
import org.tan.api.interfaces.territory.TanTerritory;

public interface TanFort {

    Vector3D getFlagPosition();

    TanTerritory getOwner();

    TanTerritory getOccupier();

}
