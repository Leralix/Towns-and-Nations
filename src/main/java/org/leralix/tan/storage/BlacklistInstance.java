package org.leralix.tan.storage;

import org.bukkit.Chunk;

import java.util.List;

public class BlacklistInstance {
    String worldName;
    int x1;
    int z1;
    int x2;
    int z2;

    public BlacklistInstance(String name, List<Integer> coordinates) {
        worldName = name;
        x1 = coordinates.get(0);
        z1 = coordinates.get(1);
        x2 = coordinates.get(2);
        z2 = coordinates.get(3);
    }

    public boolean isChunkInArea(Chunk chunk) {
        return chunk.getWorld().getName().equals(worldName) && chunk.getX() >= x1 && chunk.getX() <= x2 && chunk.getZ() >= z1 && chunk.getZ() <= z2;
    }
}
