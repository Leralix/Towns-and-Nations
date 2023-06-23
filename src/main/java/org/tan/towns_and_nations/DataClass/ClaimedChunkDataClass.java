package org.tan.towns_and_nations.DataClass;

import org.bukkit.Chunk;

import java.util.Objects;

public class ClaimedChunkDataClass {
    private final int x, z;
    private final String worldUUID, townUUID;

    public ClaimedChunkDataClass(Chunk chunk) {
        this(chunk, null);
    }

    public ClaimedChunkDataClass(Chunk chunk, String owner) {
        this.x = chunk.getX();
        this.z = chunk.getZ();
        this.worldUUID = chunk.getWorld().getUID().toString();
        this.townUUID = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClaimedChunkDataClass)) return false;
        ClaimedChunkDataClass that = (ClaimedChunkDataClass) o;
        return x == that.x && z == that.z && worldUUID.equals(that.worldUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z, worldUUID);
    }

    public String getTownID() {
        return this.townUUID;
    }

}