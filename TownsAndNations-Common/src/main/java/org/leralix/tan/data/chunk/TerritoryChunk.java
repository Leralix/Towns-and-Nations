package org.leralix.tan.data.chunk;

import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.constants.Constants;
import org.tan.api.interfaces.chunk.TanTerritoryChunk;

import java.util.Optional;

public interface TerritoryChunk extends IClaimedChunk, TanTerritoryChunk {

    default Territory getOwner(){
        return getOwnerInternal();
    }

    Territory getOwnerInternal();

    Territory getOccupierInternal();

    default Optional<Fort> getFortProtecting(){
        for (Fort fort : TownsAndNations.getPlugin().getFortStorage().getAllControlledFort(getOccupierInternal())) {
            if (fort.getPosition().getDistance(getMiddleVector2D()) <= Constants.getFortProtectionRadius()) {
                return Optional.of(fort);
            }
        }
        return Optional.empty();
    }

    String getOccupierID();

    default void setOccupier(Territory occupier){
        setOccupierID(occupier.getID());
    }

    void setOccupierID(String occupierID);

    void liberate();

    boolean isOccupied();

    void unclaimChunk(Player player, ITanPlayer tanPlayer, LangType langType);

    /**
     * Defines if this territory can bypass buffer zone restrictions and claim a chunk in the radius of the buffer zone
     *
     * @param territoryToAllow The territory wishing to claim a chunk in the buffer zone
     * @return True if the territory can bypass buffer zone restrictions, false otherwise
     */
    default boolean canBypassBufferZone(Territory territoryToAllow){
        // This chunks is held by the same territory
        if (getOwnerID().equals(territoryToAllow.getID())) {
            return true;
        }

        //This chunk is held by a vassal of the territory
        return territoryToAllow.getVassalsID().contains(getOwnerID());
    }
}
