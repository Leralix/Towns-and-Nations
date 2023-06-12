package org.tan.towns_and_nations.utils;

import org.bukkit.Chunk;

import java.util.Objects;

class ClaimedChunk {
    private final int x, z;
    private final String worldUUID, townUUID;

    ClaimedChunk(Chunk chunk) {
        this(chunk, null);
    }

    ClaimedChunk(Chunk chunk, String owner) {
        this.x = chunk.getX();
        this.z = chunk.getZ();
        this.worldUUID = chunk.getWorld().getUID().toString();
        this.townUUID = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClaimedChunk)) return false;
        ClaimedChunk that = (ClaimedChunk) o;
        return x == that.x && z == that.z && worldUUID.equals(that.worldUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z, worldUUID);
    }
}