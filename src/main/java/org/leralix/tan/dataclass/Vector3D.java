package org.leralix.tan.dataclass;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;
import java.util.UUID;

public class Vector3D {
    private int x;
    private int y;
    private int z;
    private String worldID;

    public Vector3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D(int x, int y, int z, String worldID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldID = worldID;
    }

    public Vector3D(Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        if(location.getWorld() != null)
            this.worldID = location.getWorld().getUID().toString();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
    public UUID getWorldID(){
        return UUID.fromString(worldID);
    }
    public World getWorld(){
        return Bukkit.getWorld(getWorldID());
    }
    public Location getLocation(){
        return new Location(getWorld(), getX(), getY(), getZ());
    }
    @Override
    public String toString(){
        return "[" + this.x + ", " + this.y + ", " + this.z + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3D vector3D = (Vector3D) o;
        return x == vector3D.x && y == vector3D.y && z == vector3D.z && Objects.equals(worldID, vector3D.worldID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, worldID);
    }
}
