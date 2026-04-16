package org.leralix.tan.utils.territory;

import org.bukkit.Chunk;
import org.leralix.lib.position.Vector2D;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.Building;
import org.leralix.tan.data.chunk.ChunkData;
import org.leralix.tan.data.chunk.IClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunk;
import org.leralix.tan.data.chunk.TerritoryChunkData;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.Town;

import java.util.*;
import java.util.function.Predicate;

public class ChunkUtil {

    private ChunkUtil() {
        throw new AssertionError("Utility class");
    }

    public static String getChunkKey(Chunk chunk) {
        return getChunkKey(chunk.getX(), chunk.getZ(), chunk.getWorld().getUID().toString());
    }

    public static String getChunkKey(IClaimedChunk chunk) {
        return getChunkKey(chunk.getX(), chunk.getZ(), chunk.getWorldID());
    }

    public static String getChunkKey(int x, int z, String chunkWorldUID) {
        return x + "," + z + "," + chunkWorldUID;
    }

    public static boolean isChunkEncirecledBy(IClaimedChunk center, Predicate<IClaimedChunk> predicate) {
        for (IClaimedChunk neighbor : TownsAndNations.getPlugin().getClaimStorage().getEightAjacentChunks(center)) {
            if (!predicate.test(neighbor)) {
                return false;
            }
        }
        return true;
    }

    public static List<TerritoryChunk> getBorderChunks(Territory territoryData) {

        List<TerritoryChunk> res = new ArrayList<>();

        for (TerritoryChunk territoryChunk : TownsAndNations.getPlugin().getClaimStorage().getAllChunkFrom(territoryData)) {

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
     * Check all chunk around the unclaimed chunk to see if they are still supplied.
     *
     * @param chunkToAnalyse the newly unclaimed chunk
     */
    public static void unclaimIfNoLongerSupplied(TerritoryChunk chunkToAnalyse) {

        // It is possible that this chunk has already been unclaimed. If so, do not analyse
        if(!TownsAndNations.getPlugin().getClaimStorage().isChunkClaimed(chunkToAnalyse)){
            return;
        }
        ChunkPolygon chunkPolygon = ChunkUtil.getPolygon(chunkToAnalyse);
        if (!chunkPolygon.isSupplied()) {
            chunkPolygon.unclaimAll();
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


        for (IClaimedChunk claimedChunk : TownsAndNations.getPlugin().getClaimStorage().getEightAjacentChunks(chunkToPotentiallyUnclaim)) {
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
        Set<IClaimedChunk> result = new HashSet<>();
        Queue<IClaimedChunk> toVisit = new LinkedList<>();

        toVisit.add(startChunk);

        while (!toVisit.isEmpty()) {
            IClaimedChunk current = toVisit.poll();
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
            for (IClaimedChunk adj : TownsAndNations.getPlugin().getClaimStorage().getFourAjacentChunks(current)) {
                if (adj != null && !visited.contains(adj.getX() + "," + adj.getZ() + "," + adj.getWorldID())) {
                    toVisit.add(adj);
                }
            }
        }

        return new ChunkPolygon(startChunk.getOwnerInternal(), result);
    }

    private static boolean alreadyAnalysed(ChunkData claimedChunk, List<ChunkPolygon> polygonsAnalysed) {
        for (ChunkPolygon chunkPolygon : polygonsAnalysed) {
            if (chunkPolygon.contains(claimedChunk)) {
                return true;
            }
        }
        return false;
    }

    public static boolean chunkContainsBuildings(TerritoryChunkData townClaimedChunk, Territory territory) {
        for (Building building : territory.getBuildings()) {
            if (building.getPosition().getLocation().getChunk().equals(townClaimedChunk.getChunk())) {
                return true;
            }
        }

        if (territory instanceof Town townData) {

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
    public static List<IClaimedChunk> getChunksInRadius(Chunk center, double radius) {
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
    public static List<IClaimedChunk> getChunksInRadius(Chunk center, double radius, Predicate<IClaimedChunk> filter) {
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
    public static List<IClaimedChunk> getChunksInRadius(Chunk center, int radius) {
        return getChunksInRadius(TownsAndNations.getPlugin().getClaimStorage().get(center), radius, claimedChunk2 -> true);
    }


    /**
     * Get all claimed chunks in a radius around a center chunk
     * The radius is in chunks and is circular.
     *
     * @param center The chunk at the center of the radius
     * @param radius The radius in chunks
     * @return A list of claimed chunks in the radius
     */
    public static List<IClaimedChunk> getChunksInRadius(Chunk center, int radius, Predicate<IClaimedChunk> filter) {
        return getChunksInRadius(TownsAndNations.getPlugin().getClaimStorage().get(center), radius, filter);
    }

    /**
     * Get all claimed chunks in a radius around a center chunk
     * The radius is in chunks and is circular.
     *
     * @param center The chunk at the center of the radius
     * @param radius The radius in chunks
     * @return A list of claimed chunks in the radius
     */
    public static List<IClaimedChunk> getChunksInRadius(IClaimedChunk center, double radius) {
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
    public static List<IClaimedChunk> getChunksInRadius(IClaimedChunk center, double radius, Predicate<IClaimedChunk> filter) {
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
    public static List<IClaimedChunk> getChunksInRadius(IClaimedChunk center, int radius, Predicate<IClaimedChunk> filter) {
        List<IClaimedChunk> chunksInRadius = new ArrayList<>();
        int centerX = center.getX();
        int centerZ = center.getZ();
        String worldUUID = center.getWorldID();

        Vector2D centerPos = new Vector2D(centerX, centerZ, worldUUID);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int chunkX = centerX + dx;
                int chunkZ = centerZ + dz;
                IClaimedChunk chunk = TownsAndNations.getPlugin().getClaimStorage().get(chunkX, chunkZ, worldUUID);
                double distance = centerPos.getDistance(new Vector2D(chunk.getX(), chunk.getZ(), worldUUID));

                // Distance AND user filter
                if (distance <= radius && filter.test(chunk)) {
                    chunksInRadius.add(chunk);
                }
            }
        }

        return chunksInRadius;
    }

    public static boolean isInBufferZone(IClaimedChunk chunkToAnalyse, Territory territoryToAllow, int bufferZone) {

        List<IClaimedChunk> claimedChunkToAnalyse = getChunksInRadius(
                chunkToAnalyse,
                bufferZone,
                chunkToWatch ->
                    chunkToWatch instanceof TerritoryChunk territoryChunk &&
                            !territoryChunk.canBypassBufferZone(territoryToAllow)
                );

        return !claimedChunkToAnalyse.isEmpty();
    }
}
