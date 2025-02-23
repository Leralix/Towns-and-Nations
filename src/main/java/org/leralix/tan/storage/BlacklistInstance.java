package org.leralix.tan.storage;

import org.bukkit.Chunk;

import java.util.List;

public class BlacklistInstance {
    String worldName;
    int xMin;
    int zMin;
    int xMax;
    int zMax;

    public BlacklistInstance(String name, List<Integer> coordinates) {
        worldName = name;

        int x1 = coordinates.get(0);
        int z1 = coordinates.get(1);
        int x2 = coordinates.get(2);
        int z2 = coordinates.get(3);

        xMin = Math.min(x1, x2);
        xMax = Math.max(x1, x2);
        zMin = Math.min(z1, z2);
        zMax = Math.max(z1, z2);
    }

    public boolean isChunkInArea(Chunk chunk) {
        return chunk.getWorld().getName().equals(worldName) && chunk.getX() >= xMin && chunk.getX() <= xMax && chunk.getZ() >= zMin && chunk.getZ() <= zMax;
    }
}
