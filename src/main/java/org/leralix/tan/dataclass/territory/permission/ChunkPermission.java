package org.leralix.tan.dataclass.territory.permission;

import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChunkPermission {

    private RelationPermission overallPermission;
    private final List<String> specificPlayerPermissions;

    public ChunkPermission() {
        this.overallPermission = RelationPermission.TOWN;
        this.specificPlayerPermissions = new ArrayList<>();
    }

    public void nextPermission() {
        this.overallPermission = this.overallPermission.getNext();
    }

    public RelationPermission getOverallPermission() {
        return this.overallPermission;
    }

    public void addSpecificPlayerPermission(String playerName) {
        this.specificPlayerPermissions.add(playerName);
    }

    public void removeSpecificPlayerPermission(String playerName) {
        this.specificPlayerPermissions.remove(playerName);
    }

    private boolean isPlayerAllowed(String playerName) {
        return this.specificPlayerPermissions.contains(playerName);
    }


    public boolean isAllowed(TerritoryData territoryToCheck, ITanPlayer tanPlayer) {
        if(this.overallPermission.isAllowed(territoryToCheck, tanPlayer)) {
            return true;
        }
        return isPlayerAllowed(tanPlayer.getID());
    }

    public Collection<String> getAuthorizedPlayers() {
        return this.specificPlayerPermissions;
    }
}
