package org.leralix.tan.dataclass;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeleportationPosition {

    private final int x;
    private final int y;
    private final int z;
    private final String world;
    private final float pitch;
    private final float yaw;

    public TeleportationPosition(Location location){
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.world = location.getWorld().getUID().toString();
        this.pitch = location.getPitch();
        this.yaw = location.getYaw();
    }

    public void teleport(Player player){
        player.teleport(new Location(Bukkit.getWorld(UUID.fromString(world)), x, y, z, yaw, pitch));
    }






}
