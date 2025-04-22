package org.leralix.tan.dataclass;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.leralix.lib.position.Vector3DWithOrientation;

import java.util.UUID;


public class TeleportationPosition {

    /**
     * Legacy code, to be removed in the future. TODO remove in v1.0
     */
    private int x;
    private int y;
    private int z;
    private String world;
    private float pitch;
    private float yaw;
    private Vector3DWithOrientation position;

    public TeleportationPosition(Location location){
        position = new Vector3DWithOrientation(location);
    }

    public void teleport(Player player){
        if(position == null){
            position = new Vector3DWithOrientation(new Location(Bukkit.getWorld(UUID.fromString(world)), x, y, z, yaw, pitch));
        }
        player.teleport(position.getLocation());
    }






}
