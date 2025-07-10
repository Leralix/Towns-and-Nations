package org.leralix.tan.dataclass.chunk;

import org.bukkit.Chunk;
import org.leralix.tan.dataclass.territory.TerritoryData;

public abstract class TerritoryChunk extends ClaimedChunk2 {

    private String occupierID;

    protected TerritoryChunk(Chunk chunk, String owner) {
        super(chunk, owner);
        this.occupierID = owner;
    }

    protected TerritoryChunk(int x, int z, String worldUUID, String owner) {
        super(x, z, worldUUID, owner);
        this.occupierID = owner;
    }

    public String getOccupierID() {
        if(occupierID == null) {
            return occupierID = ownerID;
        }
        return occupierID;
    }

    public void setOccupierID(TerritoryData occupier) {
        setOccupierID(occupier.getID());
    }

    public void setOccupierID(String occupierID) {
        this.occupierID = occupierID;
    }

    public void liberate() {
        this.occupierID = getOwnerID();
    }

    public boolean isOccupied() {
        return !ownerID.equals(occupierID);
    }
}
