package org.tan.api.interfaces.buildings;

import org.bukkit.Location;
import org.tan.api.interfaces.territory.TanTerritory;

public interface TanFort extends TanBuilding {

    TanTerritory getOwner();

    TanTerritory getOccupier();

    @Override
    default boolean isLocationInside(Location location){
        return getPosition().getLocation().getChunk().equals(location.getChunk());
    }

}
