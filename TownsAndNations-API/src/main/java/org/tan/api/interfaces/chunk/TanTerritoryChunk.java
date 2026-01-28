package org.tan.api.interfaces.chunk;

import org.tan.api.interfaces.TanTerritory;

public interface TanTerritoryChunk extends TanClaimedChunk{

    /**
     * @return the owner of the territory
     */
    TanTerritory getOwner();

    /**
     * @return the ID of the chunk owner.
     */
    String getOwnerID();

    /**
     * @return the territory occupying the chunk.
     * It can be different from the owner in case the chunk is occupied due to war.
     */
    TanTerritory getOccupier();

}
