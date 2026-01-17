package org.leralix.tan.utils.territory;

import org.bukkit.Chunk;
import org.leralix.lib.position.Vector2D;
import org.leralix.tan.building.Building;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
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

    public static boolean isChunkEncirecledBy(ClaimedChunk2 center, Predicate<ClaimedChunk2> predicate) {
        for (ClaimedChunk2 neighbor : NewClaimedChunkStorage.getInstance().getEightAjacentChunks(center)) {
            if (!predicate.test(neighbor)) {
                return false;
            }
        }
        return true;
    }

    public static List<ClaimedChunk2> getBorderChunks(TerritoryData territoryData) {

        List<ClaimedChunk2> res = new ArrayList<>();

        for (TerritoryChunk territoryChunk : NewClaimedChunkStorage.getInstance().getAllChunkFrom(territoryData)) {
            if (!isChunkEncirecledBy(territoryChunk, chunk -> territoryData.getID().equals(chunk.getOwnerID()))) {
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

        for (ClaimedChunk2 claimedChunk2 : NewClaimedChunkStorage.getInstance().getEightAjacentChunks(unclaimedChunk)) {

            if (claimedChunk2 instanceof TerritoryChunk territoryChunk) {
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


        for (ClaimedChunk2 claimedChunk2 : NewClaimedChunkStorage.getInstance().getEightAjacentChunks(chunkToPotentiallyUnclaim)) {

            if (claimedChunk2 instanceof TerritoryChunk territoryChunk) {

                ChunkPolygon chunkPolygon = ChunkUtil.getPolygon(territoryChunk, chunkToPotentiallyUnclaim);

                if (!chunkPolygon.isSupplied()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean wouldClaimCauseEncirclement(Chunk chunkToClaim, TerritoryData claimingTerritory) {
        if (chunkToClaim == null || claimingTerritory == null) {
            return false;
        }

        ClaimedChunk2 toClaim = NewClaimedChunkStorage.getInstance().get(chunkToClaim);
        String worldId = chunkToClaim.getWorld().getUID().toString();
        String claimingId = claimingTerritory.getID();

        List<ClaimedChunk2> neighbors = List.of(
                NewClaimedChunkStorage.getInstance().get(chunkToClaim.getX() + 1, chunkToClaim.getZ(), worldId),
                NewClaimedChunkStorage.getInstance().get(chunkToClaim.getX() - 1, chunkToClaim.getZ(), worldId),
                NewClaimedChunkStorage.getInstance().get(chunkToClaim.getX(), chunkToClaim.getZ() + 1, worldId),
                NewClaimedChunkStorage.getInstance().get(chunkToClaim.getX(), chunkToClaim.getZ() - 1, worldId)
        );

        for (ClaimedChunk2 neighbor : neighbors) {
            if (!(neighbor instanceof TerritoryChunk territoryChunk)) {
                continue;
            }
            if (claimingId.equals(territoryChunk.getOwnerID())) {
                continue;
            }

            int nx = territoryChunk.getX();
            int nz = territoryChunk.getZ();

            boolean surrounded = true;
            surrounded &= isOwnedOrWillBeOwnedBy(nx + 1, nz, worldId, claimingId, toClaim);
            surrounded &= isOwnedOrWillBeOwnedBy(nx - 1, nz, worldId, claimingId, toClaim);
            surrounded &= isOwnedOrWillBeOwnedBy(nx, nz + 1, worldId, claimingId, toClaim);
            surrounded &= isOwnedOrWillBeOwnedBy(nx, nz - 1, worldId, claimingId, toClaim);

            if (surrounded) {
                return true;
            }
        }

        return false;
    }

    private static boolean isOwnedOrWillBeOwnedBy(int x, int z, String worldId, String claimingId, ClaimedChunk2 toClaim) {
        ClaimedChunk2 chk = NewClaimedChunkStorage.getInstance().get(x, z, worldId);
        if (chk.getX() == toClaim.getX() && chk.getZ() == toClaim.getZ() && chk.getWorldUUID().equals(toClaim.getWorldUUID())) {
            return true;
        }
        if (chk instanceof TerritoryChunk territoryChunk) {
            return claimingId.equals(territoryChunk.getOwnerID());
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
        Set<ClaimedChunk2> result = new HashSet<>();
        Queue<ClaimedChunk2> toVisit = new LinkedList<>();

        toVisit.add(startChunk);

        while (!toVisit.isEmpty()) {
            ClaimedChunk2 current = toVisit.poll();
            String key = current.getX() + "," + current.getZ() + "," + current.getWorldUUID();

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
            List<ClaimedChunk2> adjacentChunks = NewClaimedChunkStorage.getInstance().getFourAjacentChunks(current);
            for (ClaimedChunk2 adj : adjacentChunks) {
                if (adj != null && !visited.contains(adj.getX() + "," + adj.getZ() + "," + adj.getWorldUUID())) {
                    toVisit.add(adj);
                }
            }
        }

        return new ChunkPolygon(startChunk.getOwner(), result);
    }

    private static boolean alreadyAnalysed(ClaimedChunk2 claimedChunk2, List<ChunkPolygon> polygonsAnalysed) {
        for (ChunkPolygon chunkPolygon : polygonsAnalysed) {
            if (chunkPolygon.contains(claimedChunk2)) {
                return true;
            }
        }
        return false;
    }

    public static boolean chunkContainsBuildings(TerritoryChunk townClaimedChunk, TerritoryData territoryData) {
        for (Building building : territoryData.getBuildings()) {
            if (building.getPosition().getLocation().getChunk().equals(townClaimedChunk.getChunk())) {
                return true;
            }
        }

        if (territoryData instanceof TownData townData) {

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
    public static List<ClaimedChunk2> getChunksInRadius(Chunk center, double radius) {
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
    public static List<ClaimedChunk2> getChunksInRadius(Chunk center, double radius, Predicate<ClaimedChunk2> filter) {
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
    public static List<ClaimedChunk2> getChunksInRadius(Chunk center, int radius) {
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
    public static List<ClaimedChunk2> getChunksInRadius(Chunk center, int radius, Predicate<ClaimedChunk2> filter) {
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
    public static List<ClaimedChunk2> getChunksInRadius(ClaimedChunk2 center, double radius) {
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
    public static List<ClaimedChunk2> getChunksInRadius(ClaimedChunk2 center, double radius, Predicate<ClaimedChunk2> filter) {
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
    public static List<ClaimedChunk2> getChunksInRadius(ClaimedChunk2 center, int radius, Predicate<ClaimedChunk2> filter) {
        List<ClaimedChunk2> chunksInRadius = new ArrayList<>();
        int centerX = center.getX();
        int centerZ = center.getZ();
        String worldUUID = center.getWorldUUID();

        Vector2D centerPos = new Vector2D(centerX, centerZ, worldUUID);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int chunkX = centerX + dx;
                int chunkZ = centerZ + dz;
                ClaimedChunk2 chunk = NewClaimedChunkStorage.getInstance().get(chunkX, chunkZ, worldUUID);
                double distance = centerPos.getDistance(new Vector2D(chunk.getX(), chunk.getZ(), worldUUID));

                // Distance AND user filter
                if (distance <= radius && filter.test(chunk)) {
                    chunksInRadius.add(chunk);
                }
            }
        }

        return chunksInRadius;
    }

    public static boolean isInBufferZone(ClaimedChunk2 chunkToAnalyse, TerritoryData territoryToAllow, int bufferZone) {

        List<ClaimedChunk2> claimedChunkToAnalyse = getChunksInRadius(
                chunkToAnalyse,
                bufferZone,
                chunkToWatch ->
                    chunkToWatch instanceof TerritoryChunk territoryChunk &&
                            !territoryChunk.canBypassBufferZone(territoryToAllow)
                );

        return !claimedChunkToAnalyse.isEmpty();
    }
}
