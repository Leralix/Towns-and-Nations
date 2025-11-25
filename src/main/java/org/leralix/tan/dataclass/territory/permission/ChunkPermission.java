package org.leralix.tan.dataclass.territory.permission;

import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChunkPermission {

    private RelationPermission overallPermission;
    private List<String> specificRankIDPermissions;
    private final List<String> specificPlayerPermissions;

    public ChunkPermission(RelationPermission defaultRelation) {
        this.overallPermission = defaultRelation;
        this.specificRankIDPermissions = new ArrayList<>();
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

    public List<String> getAuthorizedRanks() {
        if (this.specificRankIDPermissions == null) {
            this.specificRankIDPermissions = new ArrayList<>();
        }
        return this.specificRankIDPermissions;
    }

    public void addSpecificRankPermission(String rankID) {
        getAuthorizedRanks().add(rankID);
    }

    public void removeSpecificRankPermission(String rankID) {
        getAuthorizedRanks().remove(rankID);
    }


    private boolean isPlayerAllowed(String playerName) {
        return this.specificPlayerPermissions.contains(playerName);
    }


    public boolean isAllowed(TerritoryData territoryToCheck, ITanPlayer tanPlayer) {
        if (this.overallPermission.isAllowed(territoryToCheck, tanPlayer)) {
            return true;
        }
        return isPlayerAllowed(tanPlayer.getID());
    }

    public Collection<String> getAuthorizedPlayers() {
        return this.specificPlayerPermissions;
    }
}
