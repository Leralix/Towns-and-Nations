package org.leralix.tan.data.territory.permission;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChunkPermission {

    private RelationPermission overallPermission;
    private Set<Integer> specificRankIDPermissions;
    private final Set<UUID> specificPlayerPermissions;

    public ChunkPermission(RelationPermission defaultRelation) {
        this.overallPermission = defaultRelation;
        this.specificRankIDPermissions = new HashSet<>();
        this.specificPlayerPermissions = new HashSet<>();
    }

    public void nextPermission() {
        this.overallPermission = this.overallPermission.getNext();
    }

    public RelationPermission getOverallPermission() {
        return this.overallPermission;
    }

    public void addSpecificPlayerPermission(UUID playerID) {
        this.specificPlayerPermissions.add(playerID);
    }

    public void removeSpecificPlayerPermission(UUID playerID) {
        this.specificPlayerPermissions.remove(playerID);
    }

    public Set<Integer> getAuthorizedRanks() {
        if (this.specificRankIDPermissions == null) {
            this.specificRankIDPermissions = new HashSet<>();
        }
        return this.specificRankIDPermissions;
    }

    public void addSpecificRankPermission(int rankID) {
        getAuthorizedRanks().add(rankID);
    }

    public void removeSpecificRankPermission(int rankID) {
        getAuthorizedRanks().remove(rankID);
    }


    private boolean isPlayerAllowed(UUID playerID) {
        return this.specificPlayerPermissions.contains(playerID);
    }


    public boolean isAllowed(TerritoryData territoryToCheck, ITanPlayer tanPlayer) {
        if (this.overallPermission.isAllowed(territoryToCheck, tanPlayer)) {
            return true;
        }
        if (isPlayerRankAllowed(tanPlayer, territoryToCheck)) {
            return true;
        }

        return isPlayerAllowed(tanPlayer.getID());
    }

    private boolean isPlayerRankAllowed(ITanPlayer tanPlayer, TerritoryData territoryToCheck) {
        if (!territoryToCheck.isPlayerIn(tanPlayer)) {
            return false;
        }
        return specificRankIDPermissions.contains(tanPlayer.getRankID(territoryToCheck));
    }

    public Set<UUID> getAuthorizedPlayers() {
        return this.specificPlayerPermissions;
    }
}
