package org.tan.TownsAndNations.DataClass;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SpawnPosition {

    private int x,y,z;
    private String world;
    private float pitch, yaw;

    public SpawnPosition(Location location){
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.world = location.getWorld().getUID().toString();
        this.pitch = location.getPitch();
        this.yaw = location.getYaw();
    }

    public SpawnPosition(int x, int y, int z, String world, float pitch, float yaw){
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public void teleportPlayerToSpawn(Player player){
        player.teleport(new Location(Bukkit.getWorld(UUID.fromString(world)), x, y, z, yaw, pitch));
    }






}
