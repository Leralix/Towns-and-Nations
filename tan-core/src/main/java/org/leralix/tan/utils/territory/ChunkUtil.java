package org.leralix.tan.utils.territory;

import java.util.*;
import java.util.function.Predicate;
import org.bukkit.Chunk;
import org.leralix.lib.position.Vector2D;
import org.leralix.tan.building.Building;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.utils.constants.Constants;

public class ChunkUtil {

  private static final NewClaimedChunkStorage claimedChunkStorage =
      NewClaimedChunkStorage.getInstance();

  private ChunkUtil() {
    throw new AssertionError("Utility class");
  }

  public static boolean isChunkEncirecledBy(
      ClaimedChunk2 center, Predicate<ClaimedChunk2> predicate) {
    for (ClaimedChunk2 neighbor : claimedChunkStorage.getEightAjacentChunks(center)) {
      if (!predicate.test(neighbor)) {
        return false;
      }
    }
    return true;
  }

  public static List<ClaimedChunk2> getBorderChunks(TerritoryData territoryData) {

    List<ClaimedChunk2> res = new ArrayList<>();

    for (TerritoryChunk territoryChunk : claimedChunkStorage.getAllChunkFrom(territoryData)) {
      if (!isChunkEncirecledBy(
          territoryChunk, chunk -> territoryData.getID().equals(chunk.getOwnerID()))) {
        res.add(territoryChunk);
      }
    }

    return res;
  }

  public static void unclaimIfNoLongerSupplied(TerritoryChunk unclaimedChunk) {

    List<ChunkPolygon> polygonsAnalysed = new ArrayList<>();

    for (ClaimedChunk2 claimedChunk2 : claimedChunkStorage.getEightAjacentChunks(unclaimedChunk)) {

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

  private static ChunkPolygon getPolygon(TerritoryChunk startChunk) {

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

      if (!territoryChunk.getOwnerID().equals(ownerID)) {
        continue; // Belongs to another territory
      }

      result.add(current);

      // Get adjacent chunks (4 directions)
      List<ClaimedChunk2> adjacentChunks = claimedChunkStorage.getFourAjacentChunks(current);
      for (ClaimedChunk2 adj : adjacentChunks) {
        if (adj != null
            && !visited.contains(adj.getX() + "," + adj.getZ() + "," + adj.getWorldUUID())) {
          toVisit.add(adj);
        }
      }
    }

    return new ChunkPolygon(startChunk.getOwner(), result);
  }

  private static boolean alreadyAnalysed(
      ClaimedChunk2 claimedChunk2, List<ChunkPolygon> polygonsAnalysed) {
    for (ChunkPolygon chunkPolygon : polygonsAnalysed) {
      if (chunkPolygon.contains(claimedChunk2)) {
        return true;
      }
    }
    return false;
  }

  public static boolean chunkContainsBuildings(
      TerritoryChunk townClaimedChunk, TerritoryData territoryData) {
    for (Building building : territoryData.getBuildings()) {
      if (building.getPosition().getLocation().getChunk().equals(townClaimedChunk.getChunk())) {
        return true;
      }
    }

    if (territoryData instanceof TownData townData) {

      var optionalLocation = townData.getCapitalLocation();

      if (optionalLocation.isPresent()) {
        Vector2D location = optionalLocation.get();
        return location
            .getWorld()
            .getChunkAt(location.getX(), location.getZ())
            .equals(townClaimedChunk.getChunk());
      }
    }

    return false;
  }

  /**
   * Get all claimed chunks in a radius around a center chunk The radius is in chunks and is
   * circular.
   *
   * @param center The chunk at the center of the radius
   * @param radius The radius in chunks
   * @return A list of claimed chunks in the radius
   */
  public static List<ClaimedChunk2> getChunksInRadius(Chunk center, double radius) {
    return getChunksInRadius(center, (int) Math.ceil(radius));
  }

  /**
   * Get all claimed chunks in a radius around a center chunk The radius is in chunks and is
   * circular.
   *
   * @param center The chunk at the center of the radius
   * @param radius The radius in chunks
   * @return A list of claimed chunks in the radius
   */
  public static List<ClaimedChunk2> getChunksInRadius(Chunk center, int radius) {
    return getChunksInRadius(NewClaimedChunkStorage.getInstance().get(center), radius);
  }

  /**
   * Get all claimed chunks in a radius around a center chunk The radius is in chunks and is
   * circular.
   *
   * @param center The chunk at the center of the radius
   * @param radius The radius in chunks
   * @return A list of claimed chunks in the radius
   */
  public static List<ClaimedChunk2> getChunksInRadius(ClaimedChunk2 center, double radius) {
    return getChunksInRadius(center, (int) Math.ceil(radius));
  }

  /**
   * Get all claimed chunks in a radius around a center chunk The radius is in chunks and is
   * circular.
   *
   * @param center The chunk at the center of the radius
   * @param radius The radius in chunks
   * @return A list of claimed chunks in the radius
   */
  public static List<ClaimedChunk2> getChunksInRadius(ClaimedChunk2 center, int radius) {
    List<ClaimedChunk2> chunksInRadius = new ArrayList<>();
    int centerX = center.getX();
    int centerZ = center.getZ();
    String worldUUID = center.getWorldUUID();

    Vector2D centerPos = new Vector2D(center.getX(), center.getZ(), worldUUID);

    for (int dx = -radius; dx <= radius; dx++) {
      for (int dz = -radius; dz <= radius; dz++) {
        int chunkX = centerX + dx;
        int chunkZ = centerZ + dz;
        ClaimedChunk2 chunk = claimedChunkStorage.get(chunkX, chunkZ, worldUUID);
        if (chunk != null
            && centerPos.getDistance(new Vector2D(chunk.getX(), chunk.getZ(), worldUUID))
                <= radius) {
          chunksInRadius.add(chunk);
        }
      }
    }

    return chunksInRadius;
  }

  public static boolean isInBufferZone(
      ClaimedChunk2 chunkToAnalyse, TerritoryData territoryToAllow) {

    List<ClaimedChunk2> claimedChunkToAnalyse =
        getChunksInRadius(chunkToAnalyse, Constants.territoryClaimBufferZone());

    for (ClaimedChunk2 claimedChunk2 : claimedChunkToAnalyse) {
      if (claimedChunk2 instanceof TerritoryChunk territoryChunk
          && !territoryToAllow.canAccessBufferZone(territoryChunk)) {
        return true;
      }
    }
    return false;
  }
}
