package org.leralix.tan.storage.stored;


import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.chunk.*;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.utils.territory.ChunkUtil;

import java.util.*;

public interface ClaimStorage {

    default IClaimedChunk get(int x, int z, String worldID){
        IClaimedChunk claimedChunk = get(ChunkUtil.getChunkKey(x, z, worldID));
        if (claimedChunk == null) {
            return new WildernessChunkData(x, z, worldID);
        }
        return claimedChunk;
    }

    default IClaimedChunk get(Chunk chunk){
        IClaimedChunk claimedChunk = get(ChunkUtil.getChunkKey(chunk));
        if (claimedChunk == null) {
            return new WildernessChunkData(chunk);
        }
        return claimedChunk;
    }

    IClaimedChunk get(String chunkID);

    default boolean isChunkClaimed(Chunk chunk) {
        return isChunkClaimed(ChunkUtil.getChunkKey(chunk));
    }

    default boolean isChunkClaimed(IClaimedChunk chunk) {
        return isChunkClaimed(ChunkUtil.getChunkKey(chunk));
    }

    boolean isChunkClaimed(String chunkKey);

    /**
     * @return The chunk on the player current position
     */
    default IClaimedChunk get(Player player){
        return get(player.getLocation().getChunk());
    }

    default void unclaimAllChunkFromID(String id) {
        for(TerritoryChunk chunk : new ArrayList<>(getAllChunkFrom(id))){
            if(chunk.getOwnerID().equals(id)){
                unclaimChunk(chunk);
            }
        }
    }

    Collection<IClaimedChunk> getAllChunks();

    void unclaimChunk(IClaimedChunk claimedChunk);

    default void unclaimChunk(Chunk chunk){
        unclaimChunk(get(chunk));
    }

    default void checkValidWorlds() {
        for (IClaimedChunk chunk : new ArrayList<>(getAllChunks())) {
            if (chunk.getWorld() == null) {
                unclaimChunk(chunk);
                TownsAndNations.getPlugin().getLogger().warning("Deleted claimed chunk " + chunk.getX() + "," + chunk.getZ() + " due to invalid world."); //TODO : only one log at the end of the loop
            }
        }
    }

    default @NotNull Collection<IClaimedChunk> getFourAjacentChunks(IClaimedChunk chunk) {
        return List.of(
                get(chunk.getX(), chunk.getZ() - 1, chunk.getWorld().getUID().toString()), // NORTH
                get(chunk.getX() + 1, chunk.getZ(), chunk.getWorld().getUID().toString()), // EAST
                get(chunk.getX(), chunk.getZ() + 1, chunk.getWorld().getUID().toString()), // SOUTH
                get(chunk.getX() - 1, chunk.getZ(), chunk.getWorld().getUID().toString())  // WEST
        );
    }

    default @NotNull List<IClaimedChunk> getEightAjacentChunks(IClaimedChunk chunk) {
        return List.of(
                get(chunk.getX(), chunk.getZ() - 1, chunk.getWorld().getUID().toString()), // Haut
                get(chunk.getX() + 1, chunk.getZ() - 1, chunk.getWorld().getUID().toString()), // Haut-droite
                get(chunk.getX() + 1, chunk.getZ(), chunk.getWorld().getUID().toString()), // Droite
                get(chunk.getX() + 1, chunk.getZ() + 1, chunk.getWorld().getUID().toString()), // Bas-droite
                get(chunk.getX(), chunk.getZ() + 1, chunk.getWorld().getUID().toString()), // Bas
                get(chunk.getX() - 1, chunk.getZ() + 1, chunk.getWorld().getUID().toString()), // Bas-gauche
                get(chunk.getX() - 1, chunk.getZ(), chunk.getWorld().getUID().toString()), // Gauche
                get(chunk.getX() - 1, chunk.getZ() - 1, chunk.getWorld().getUID().toString())  // Haut-gauche
        );
    }

    void claimLandmarkChunk(Chunk chunk, String ownerID);

    default boolean isAllAdjacentChunksClaimedBySameTerritory(Chunk chunk, String territoryID) {
        for (String adjacentChunkKey : getAdjacentChunkKeys(chunk)) {
            IClaimedChunk adjacentIClaimedChunk = get(adjacentChunkKey);

            if (adjacentIClaimedChunk == null) {
                return false;
            }

            if (adjacentIClaimedChunk instanceof TerritoryChunk territoryChunk) {
                if (!territoryChunk.getOccupierID().equals(territoryID)) {
                    return false;
                }
            }
        }
        return true;
    }
    default boolean isOneAdjacentChunkClaimedBySameTerritory(Chunk chunk, String territoryID){
        for (String adjacentChunkKey : getAdjacentChunkKeys(chunk)) {
            IClaimedChunk adjacentIClaimedChunk = get(adjacentChunkKey);
            if (adjacentIClaimedChunk instanceof TerritoryChunk territoryChunk &&
                    territoryChunk.getOwnerID().equals(territoryID)
            ) {
                return true;
            }
        }
        return false;
    }

    default void unclaimAllChunksFromTerritory(Territory territoryData) {
        unclaimAllChunkFromID(territoryData.getID());
    }
    default void unclaimChunkAndUpdate(IClaimedChunk claimedChunk) {
        unclaimChunk(claimedChunk);
        for(IClaimedChunk adjacentChunk : getEightAjacentChunks(claimedChunk)) {
            adjacentChunk.notifyUpdate();
        }
    }

    default Collection<TerritoryChunk> getAllChunkFrom(Territory territoryData) {
        return getAllChunkFrom(territoryData.getID());
    }

    default Collection<TerritoryChunk> getAllChunkFrom(String territoryDataID){
        List<TerritoryChunk> chunks = new ArrayList<>();
        for (IClaimedChunk chunk : getAllChunks()) {
            if (chunk instanceof TerritoryChunk territoryChunk && territoryChunk.getOwnerID().equals(territoryDataID)) {
                chunks.add(territoryChunk);
            }
        }
        return Collections.unmodifiableCollection(chunks);
    }

    TownClaimedChunk claimTownChunk(Chunk chunk, String id);

    RegionClaimedChunk claimRegionChunk(Chunk chunk, String id);

    NationClaimedChunk claimNationChunk(Chunk chunk, String id);

    void save();

    default List<String> getAdjacentChunkKeys(Chunk chunk) {
        return Arrays.asList(
                ChunkUtil.getChunkKey(chunk.getX() + 1, chunk.getZ(), chunk.getWorld().getUID().toString()),
                ChunkUtil.getChunkKey(chunk.getX() - 1, chunk.getZ(), chunk.getWorld().getUID().toString()),
                ChunkUtil.getChunkKey(chunk.getX(), chunk.getZ() + 1, chunk.getWorld().getUID().toString()),
                ChunkUtil.getChunkKey(chunk.getX(), chunk.getZ() - 1, chunk.getWorld().getUID().toString())
        );
    }
}
