package org.leralix.tan.dataclass;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.UUID;

public class ChunkCoordinates {
    private final int x;
    private final int z;
    private final String worldID;


    private ChunkCoordinates(int x, int y, String worldID) {
        this.x = x;
        this.z = y;
        this.worldID = worldID;
    }

    public ChunkCoordinates(Chunk chunk) {
        this(chunk.getX(), chunk.getZ(), chunk.getWorld().getUID().toString());
    }


    public Chunk getChunk() {
        World world = Bukkit.getWorld(UUID.fromString(worldID));
        if(world == null) {
            return null;
        }
        return world.getChunkAt(x, z);
    }
}
