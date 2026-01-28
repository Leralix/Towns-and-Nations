package org.leralix.tan.utils.territory;

import org.bukkit.Chunk;
import org.leralix.lib.position.Vector2D;
import org.leralix.tan.building.Building;
import org.leralix.tan.dataclass.chunk.ClaimedChunk;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;

import java.util.*;
import java.util.function.Predicate;

public class ChunkUtil {

    private ChunkUtil() {
        throw new AssertionError("Utility class");
    }

    public static boolean isChunkEncirecledBy(ClaimedChunk center, Predicate<ClaimedChunk> predicate) {
        for (ClaimedChunk neighbor : NewClaimedChunkStorage.getInstance().getEightAjacentChunks(center)) {
            if (!predicate.test(neighbor)) {
                return false;
            }
        }
        return true;
    }

    public static List<ClaimedChunk> getBorderChunks(TerritoryData territoryData) {

        List<ClaimedChunk> res = new ArrayList<>();

        for (TerritoryChunk territoryChunk : NewClaimedChunkStorage.getInstance().getAllChunkFrom(territoryData)) {

            if (!isChunkEncirecledBy(
                    territoryChunk,
                    chunk -> {
                        if(chunk instanceof TerritoryChunk territoryChunk1){
                            return territoryChunk1.getOwnerID().equals(territoryData.getID());
                        }
                        return false;
            })) {
                res.add(territoryChunk);
            }
        }

        return res;

    }

    /**
     * This method iterate over all chunks around a newly unclaimed chunk and verify is they should be unclaimed.
     *
     * @param unclaimedChunk the newly unclaimed chunk
     */
    public static void unclaimIfNoLongerSupplied(TerritoryChunk unclaimedChunk) {

        List<ChunkPolygon> polygonsAnalysed = new ArrayList<>();

        for (ClaimedChunk claimedChunk : NewClaimedChunkStorage.getInstance().getEightAjacentChunks(unclaimedChunk)) {

            if (claimedChunk instanceof TerritoryChunk territoryChunk) {
                if (alreadyAnalysed(territoryChunk, polygonsAnalysed)) {
                    continue;
                }

                ChunkPolygon chunkPolygon = ChunkUtil.getPolygon(territoryChunk);

                if (!chunkPolygon.isSupplied()) {
                    chunkPolygon.unclaimAll();
                }
                polygonsAnalysed.add(chunkPolygon);
            }
        }
    }

    /**
     * This method iterate over all chunks around a chunk to determine if unclaiming it will cause other chunks to
     * be deleted.
     * This check is only done if allowNonAdjacentChunksForTown/Region is enabled.
     * @param chunkToPotentiallyUnclaim the chunk that should be unclaimed
     * @return true if at least one chunk will be deleted in the chunk in parameters in deleted, false otherwise
     */
    public static boolean doesUnclaimCauseOrphan(TerritoryChunk chunkToPotentiallyUnclaim) {


        for (ClaimedChunk claimedChunk : NewClaimedChunkStorage.getInstance().getEightAjacentChunks(chunkToPotentiallyUnclaim)) {

            if (claimedChunk instanceof TerritoryChunk territoryChunk) {

                ChunkPolygon chunkPolygon = ChunkUtil.getPolygon(territoryChunk, chunkToPotentiallyUnclaim);

                if (!chunkPolygon.isSupplied()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static ChunkPolygon getPolygon(TerritoryChunk startChunk) {
        return getPolygon(startChunk, null);
    }

    /**
     * @param startChunk        The chunk creating the polygon
     * @param blacklistedChunk  A chunk that should not be counted while creating the polygon
     * @return the polygon of all chunks linked to the start chunk. ignoring blacklisted chunk
     */
    private static ChunkPolygon getPolygon(TerritoryChunk startChunk, TerritoryChunk blacklistedChunk) {

        String ownerID = startChunk.getOwnerID();
        Set<String> visited = new HashSet<>();
        Set<ClaimedChunk> result = new HashSet<>();
        Queue<ClaimedChunk> toVisit = new LinkedList<>();

        toVisit.add(startChunk);

        while (!toVisit.isEmpty()) {
            ClaimedChunk current = toVisit.poll();
            String key = current.getX() + "," + current.getZ() + "," + current.getWorldID();

            if (visited.contains(key)) {
                continue;
            }
            visited.add(key);

            if (!(current instanceof TerritoryChunk territoryChunk)) {
                continue; // Ignore wilderness or other non-territory chunks
            }

            if(current.equals(blacklistedChunk)){
                continue; // Ignore blacklisted chunk
            }

            if (!territoryChunk.getOwnerID().equals(ownerID)) {
                continue; // Belongs to another territory
            }

            result.add(current);

            // Get adjacent chunks (4 directions)
            List<ClaimedChunk> adjacentChunks = NewClaimedChunkStorage.getInstance().getFourAjacentChunks(current);
            for (ClaimedChunk adj : adjacentChunks) {
                if (adj != null && !visited.contains(adj.getX() + "," + adj.getZ() + "," + adj.getWorldID())) {
                    toVisit.add(adj);
                }
            }
        }

        return new ChunkPolygon(startChunk.getOwnerInternal(), result);
    }

    private static boolean alreadyAnalysed(ClaimedChunk claimedChunk, List<ChunkPolygon> polygonsAnalysed) {
        for (ChunkPolygon chunkPolygon : polygonsAnalysed) {
            if (chunkPolygon.contains(claimedChunk)) {
                return true;
            }
        }
        return false;
    }

    public static boolean chunkContainsBuildings(TerritoryChunk townClaimedChunk, TerritoryData territory) {
        for (Building building : territory.getBuildings()) {
            if (building.getPosition().getLocation().getChunk().equals(townClaimedChunk.getChunk())) {
                return true;
            }
        }

        if (territory instanceof TownData townData) {

            var optionalLocation = townData.getCapitalLocation();

            if (optionalLocation.isPresent()) {
                Vector2D location = optionalLocation.get();
                return location.getWorld().getChunkAt(location.getX(), location.getZ()).equals(townClaimedChunk.getChunk());
            }
        }

        return false;
    }

    /**
     * Get all claimed chunks in a radius around a center chunk
     * The radius is in chunks and is circular.
     *
     * @param center The chunk at the center of the radius
     * @param radius The radius in chunks
     * @return A list of claimed chunks in the radius
     */
    public static List<ClaimedChunk> getChunksInRadius(Chunk center, double radius) {
        return getChunksInRadius(center, radius, claimedChunk2 -> true);
    }

    /**
     * Get all claimed chunks in a radius around a center chunk
     * The radius is in chunks and is circular.
     *
     * @param center The chunk at the center of the radius
     * @param radius The radius in chunks
     * @return A list of claimed chunks in the radius
     */
    public static List<ClaimedChunk> getChunksInRadius(Chunk center, double radius, Predicate<ClaimedChunk> filter) {
        return getChunksInRadius(center, (int) Math.ceil(radius), filter);
    }

    /**
     * Get all claimed chunks in a radius around a center chunk
     * The radius is in chunks and is circular.
     *
     * @param center The chunk at the center of the radius
     * @param radius The radius in chunks
     * @return A list of claimed chunks in the radius
     */
    public static List<ClaimedChunk> getChunksInRadius(Chunk center, int radius) {
        return getChunksInRadius(NewClaimedChunkStorage.getInstance().get(center), radius, claimedChunk2 -> true);
    }


    /**
     * Get all claimed chunks in a radius around a center chunk
     * The radius is in chunks and is circular.
     *
     * @param center The chunk at the center of the radius
     * @param radius The radius in chunks
     * @return A list of claimed chunks in the radius
     */
    public static List<ClaimedChunk> getChunksInRadius(Chunk center, int radius, Predicate<ClaimedChunk> filter) {
        return getChunksInRadius(NewClaimedChunkStorage.getInstance().get(center), radius, filter);
    }

    /**
     * Get all claimed chunks in a radius around a center chunk
     * The radius is in chunks and is circular.
     *
     * @param center The chunk at the center of the radius
     * @param radius The radius in chunks
     * @return A list of claimed chunks in the radius
     */
    public static List<ClaimedChunk> getChunksInRadius(ClaimedChunk center, double radius) {
        return getChunksInRadius(center, radius, claimedChunk -> true);
    }

    /**
     * Get all claimed chunks in a radius around a center chunk
     * The radius is in chunks and is circular.
     *
     * @param center The chunk at the center of the radius
     * @param radius The radius in chunks
     * @return A list of claimed chunks in the radius
     */
    public static List<ClaimedChunk> getChunksInRadius(ClaimedChunk center, double radius, Predicate<ClaimedChunk> filter) {
        return getChunksInRadius(center, (int) Math.ceil(radius), filter);
    }

    /**
     * Get all claimed chunks in a radius using a custom filter.
     * The chunk is added only if it is within the radius AND satisfies the predicate.
     *
     * @param center The center chunk
     * @param radius Radius in chunks
     * @param filter Additional filter to validate chunks
     * @return A list of claimed chunks matching the filter
     */
    public static List<ClaimedChunk> getChunksInRadius(ClaimedChunk center, int radius, Predicate<ClaimedChunk> filter) {
        List<ClaimedChunk> chunksInRadius = new ArrayList<>();
        int centerX = center.getX();
        int centerZ = center.getZ();
        String worldUUID = center.getWorldID();

        Vector2D centerPos = new Vector2D(centerX, centerZ, worldUUID);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int chunkX = centerX + dx;
                int chunkZ = centerZ + dz;
                ClaimedChunk chunk = NewClaimedChunkStorage.getInstance().get(chunkX, chunkZ, worldUUID);
                double distance = centerPos.getDistance(new Vector2D(chunk.getX(), chunk.getZ(), worldUUID));

                // Distance AND user filter
                if (distance <= radius && filter.test(chunk)) {
                    chunksInRadius.add(chunk);
                }
            }
        }

        return chunksInRadius;
    }

    public static boolean isInBufferZone(ClaimedChunk chunkToAnalyse, TerritoryData territoryToAllow, int bufferZone) {

        List<ClaimedChunk> claimedChunkToAnalyse = getChunksInRadius(
                chunkToAnalyse,
                bufferZone,
                chunkToWatch ->
                    chunkToWatch instanceof TerritoryChunk territoryChunk &&
                            !territoryChunk.canBypassBufferZone(territoryToAllow)
                );

        return !claimedChunkToAnalyse.isEmpty();
    }
}
