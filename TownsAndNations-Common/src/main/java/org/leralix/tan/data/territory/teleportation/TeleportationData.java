package org.leralix.tan.data.territory.teleportation;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.leralix.lib.position.Vector3DWithOrientation;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.relation.TownRelation;

import java.util.HashSet;
import java.util.Set;


public class TeleportationData {

    private Vector3DWithOrientation position;

    private TownRelation relationTeleportationAllowed;

    private Set<String> authorizedTerritoryIds;

    public TeleportationData() {
        relationTeleportationAllowed = TownRelation.ALLIANCE;
        authorizedTerritoryIds = new HashSet<>();
    }

    public void setPosition(Location position) {
        setPosition(new Vector3DWithOrientation(position));
    }

    public void setPosition(Vector3DWithOrientation position) {
        this.position = position;
    }

    public void teleport(Player player) {
        player.teleport(position.getLocation());
    }

    public TownRelation getRelationTeleportationAllowed() {
        if (relationTeleportationAllowed == null) {
            relationTeleportationAllowed = TownRelation.ALLIANCE;
        }
        return relationTeleportationAllowed;
    }

    public void setRelationTeleportationAllowed(TownRelation relationTeleportationAllowed) {
        this.relationTeleportationAllowed = relationTeleportationAllowed;
    }

    public boolean isTeleportationAllowed(TownRelation relation) {
        return relation.isSuperiorOrEqualsTo(getRelationTeleportationAllowed());
    }

    public boolean isSpawnSet() {
        return position != null;
    }

    private Set<String> getAuthorizedTerritoryIds() {
        if (authorizedTerritoryIds == null) {
            authorizedTerritoryIds = new HashSet<>();
        }
        return authorizedTerritoryIds;
    }

    private boolean isTerritoryAuthorized(String territoryId) {
        return getAuthorizedTerritoryIds().contains(territoryId);
    }

    public boolean addAuthorizedTerritory(TerritoryData territoryData) {
        return getAuthorizedTerritoryIds().add(territoryData.getID());
    }
}
