package org.tan.TownsAndNations.DataClass;

import org.bukkit.Chunk;

import java.util.Objects;

public class ClaimedChunk {
    private final int x, z;
    private final String worldUUID, townUUID;

    public ClaimedChunk(Chunk chunk, String owner) {
        this.x = chunk.getX();
        this.z = chunk.getZ();
        this.worldUUID = chunk.getWorld().getUID().toString();
        this.townUUID = owner;
    }
    public ClaimedChunk(int x, int z, String worldUUID , String owner) {
        this.x = x;
        this.z = z;
        this.worldUUID = worldUUID;
        this.townUUID = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClaimedChunk that)) return false;
        return x == that.x && z == that.z && worldUUID.equals(that.worldUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z, worldUUID);
    }

    public String getTownID() {
        return this.townUUID;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public String getWorldUUID() {
        return this.worldUUID;
    }

}