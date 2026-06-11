package org.leralix.tan.data.territory.teleportation;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.leralix.lib.position.Vector3DWithOrientation;
import org.leralix.tan.data.territory.relation.TownRelation;



public class TeleportationData {

    private Vector3DWithOrientation position;

    private TownRelation relationTeleportationAllowed;

    public TeleportationData() {
        relationTeleportationAllowed = TownRelation.ALLIANCE;
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

    public Vector3DWithOrientation getPosition(){
        return position;
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
}
