package org.tan.api.interfaces;

import org.leralix.lib.position.Vector3D;

public interface TanFort {

    String getID();

    String getName();

    Vector3D getFlagPosition();

    TanTerritory getOwner();

    TanTerritory getOccupier();

}
