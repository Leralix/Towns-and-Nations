package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.chunk.*;
import org.leralix.tan.dataclass.territory.TerritoryData;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class NewClaimedChunkStorage {

    private Map<String, ClaimedChunk2> claimedChunksMap = new HashMap<>();

    private static NewClaimedChunkStorage instance;

    private NewClaimedChunkStorage() {
        loadStats();
    }

    public static synchronized NewClaimedChunkStorage getInstance() {
        if (instance == null) {
            instance = new NewClaimedChunkStorage();
        }
        return instance;
    }

    private static String getChunkKey(Chunk chunk) {
        return getChunkKey(chunk.getX(), chunk.getZ(), chunk.getWorld().getUID().toString());
    }

    private static String getChunkKey(ClaimedChunk2 chunk) {
        return getChunkKey(chunk.getX(), chunk.getZ(), chunk.getWorldUUID());
    }

    private static String getChunkKey(int x, int z, String chunkWorldUID) {
        return x + "," + z + "," + chunkWorldUID;
    }


    public Map<String, ClaimedChunk2> getClaimedChunksMap() {
        return claimedChunksMap;
    }

    public boolean isChunkClaimed(Chunk chunk) {
        return claimedChunksMap.containsKey(getChunkKey(chunk));
    }

    public Collection<TerritoryChunk> getAllChunkFrom(TerritoryData territoryData) {
        List<TerritoryChunk> chunks = new ArrayList<>();
        for (ClaimedChunk2 chunk : claimedChunksMap.values()) {
            if (chunk instanceof TerritoryChunk territoryChunk && territoryChunk.getOwnerID().equals(territoryData.getID())) {
                chunks.add(territoryChunk);
            }
        }
        return Collections.unmodifiableCollection(chunks);
    }

    public TownClaimedChunk claimTownChunk(Chunk chunk, String ownerID) {
        TownClaimedChunk townClaimedChunk = new TownClaimedChunk(chunk, ownerID);
        claimedChunksMap.put(getChunkKey(chunk), townClaimedChunk);
        save();
        return townClaimedChunk;
    }

    public void claimRegionChunk(Chunk chunk, String ownerID) {
        claimedChunksMap.put(getChunkKey(chunk), new RegionClaimedChunk(chunk, ownerID));
        save();
    }

    public void claimLandmarkChunk(Chunk chunk, String ownerID) {
        claimedChunksMap.put(getChunkKey(chunk), new LandmarkClaimedChunk(chunk, ownerID));
        save();
    }

    public boolean isAllAdjacentChunksClaimedBySameTerritory(Chunk chunk, String territoryID) {
        List<String> adjacentChunkKeys = Arrays.asList(
                getChunkKey(chunk.getX() + 1, chunk.getZ(), chunk.getWorld().getUID().toString()),
                getChunkKey(chunk.getX() - 1, chunk.getZ(), chunk.getWorld().getUID().toString()),
                getChunkKey(chunk.getX(), chunk.getZ() + 1, chunk.getWorld().getUID().toString()),
                getChunkKey(chunk.getX(), chunk.getZ() - 1, chunk.getWorld().getUID().toString())
        );

        for (String adjacentChunkKey : adjacentChunkKeys) {
            ClaimedChunk2 adjacentClaimedChunk = claimedChunksMap.get(adjacentChunkKey);

            if (adjacentClaimedChunk == null) {
                return false;
            }

            if (adjacentClaimedChunk instanceof TerritoryChunk territoryChunk) {
                if (!territoryChunk.getOccupierID().equals(territoryID)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isOneAdjacentChunkClaimedBySameTown(Chunk chunk, String townID) {

        List<String> adjacentChunkKeys = Arrays.asList(
                getChunkKey(chunk.getX() + 1, chunk.getZ(), chunk.getWorld().getUID().toString()),
                getChunkKey(chunk.getX() - 1, chunk.getZ(), chunk.getWorld().getUID().toString()),
                getChunkKey(chunk.getX(), chunk.getZ() + 1, chunk.getWorld().getUID().toString()),
                getChunkKey(chunk.getX(), chunk.getZ() - 1, chunk.getWorld().getUID().toString())
        );

        for (String adjacentChunkKey : adjacentChunkKeys) {
            ClaimedChunk2 adjacentClaimedChunk = claimedChunksMap.get(adjacentChunkKey);
            if (adjacentClaimedChunk != null && adjacentClaimedChunk.getOwnerID().equals(townID)) {
                return true;
            }
        }
        return false;
    }

    public void unclaimChunkAndUpdate(ClaimedChunk2 claimedChunk) {
        unclaimChunk(claimedChunk);
        claimedChunk.notifyUpdate();
    }

    public void unclaimChunk(ClaimedChunk2 claimedChunk) {
        claimedChunksMap.remove(getChunkKey(claimedChunk));
        save();
    }

    public void unclaimChunk(Chunk chunk) {
        unclaimChunk(get(chunk));
    }


    public @NotNull List<ClaimedChunk2> getFourAjacentChunks(ClaimedChunk2 chunk) {
        return Arrays.asList(
                get(chunk.getX(), chunk.getZ() - 1, chunk.getWorld().getUID().toString()), // NORTH
                get(chunk.getX() + 1, chunk.getZ(), chunk.getWorld().getUID().toString()), // EAST
                get(chunk.getX(), chunk.getZ() + 1, chunk.getWorld().getUID().toString()), // SOUTH
                get(chunk.getX() - 1, chunk.getZ(), chunk.getWorld().getUID().toString())  // WEST
        );
    }

    public @NotNull List<ClaimedChunk2> getEightAjacentChunks(ClaimedChunk2 chunk) {
        return Arrays.asList(
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


    public void unclaimAllChunksFromTerritory(TerritoryData territoryData) {
        unclaimAllChunkFromID(territoryData.getID());
    }

    public void unclaimAllChunkFromID(String id) {
        Iterator<Map.Entry<String, ClaimedChunk2>> iterator = claimedChunksMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ClaimedChunk2> entry = iterator.next();
            ClaimedChunk2 chunk = entry.getValue();
            if (chunk.getOwnerID().equals(id)) {
                iterator.remove();
            }
        }
    }

    public ClaimedChunk2 get(int x, int z, String worldID) {
        ClaimedChunk2 claimedChunk = claimedChunksMap.get(getChunkKey(x, z, worldID));
        if (claimedChunk == null) {
            return new WildernessChunk(x, z, worldID);
        }
        return claimedChunk;
    }

    public @NotNull ClaimedChunk2 get(Chunk chunk) {
        ClaimedChunk2 claimedChunk = claimedChunksMap.get(getChunkKey(chunk));
        if (claimedChunk == null) {
            return new WildernessChunk(chunk);
        }
        return claimedChunk;
    }

    private void loadStats() {
        Gson gson = new Gson();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Claimed Chunks.json");
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type type = new TypeToken<Map<String, JsonObject>>() {}.getType();
                Map<String, JsonObject> jsonData = gson.fromJson(reader, type);

                for (Map.Entry<String, JsonObject> entry : jsonData.entrySet()) {
                    JsonObject chunkData = entry.getValue();
                    JsonObject vector2D = chunkData.getAsJsonObject("vector2D");
                    int x = vector2D.get("x").getAsInt();
                    int z = vector2D.get("z").getAsInt();
                    String worldUUID = vector2D.get("worldID").getAsString();

                    String ownerID = chunkData.get("ownerID").getAsString();

                    if (ownerID.startsWith("T")) {
                        TownClaimedChunk townChunk = new TownClaimedChunk(x, z, worldUUID, ownerID);
                        String occupierID = chunkData.has("occupierID") ? chunkData.get("occupierID").getAsString() : ownerID;
                        townChunk.setOccupierID(occupierID);
                        claimedChunksMap.put(entry.getKey(), townChunk);
                    } else if (ownerID.startsWith("R")) {
                        RegionClaimedChunk regionChunk = new RegionClaimedChunk(x, z, worldUUID, ownerID);
                        String occupierID = chunkData.has("occupierID") ? chunkData.get("occupierID").getAsString() : ownerID;
                        regionChunk.setOccupierID(occupierID);
                        claimedChunksMap.put(entry.getKey(), regionChunk);
                    } else if (ownerID.startsWith("L")) {
                        LandmarkClaimedChunk landmarkClaimedChunk = new LandmarkClaimedChunk(x, z, worldUUID, ownerID);
                        claimedChunksMap.put(entry.getKey(), landmarkClaimedChunk);
                    }
                }
            } catch (IOException e) {
                TownsAndNations.getPlugin().getLogger().severe("Error while loading claimed chunks stats");
            }
        }
    }


    public void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Claimed Chunks.json");
        file.getParentFile().mkdirs();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            TownsAndNations.getPlugin().getLogger().severe("Error while creating claimed chunks file");
        }
        try (FileWriter writer = new FileWriter(file, false);) {
            gson.toJson(claimedChunksMap, writer);
        } catch (IOException e) {
            TownsAndNations.getPlugin().getLogger().severe("Error while storing claimed chunks in file");
        }
    }

    public void reset() {
        this.claimedChunksMap = new HashMap<>();
    }
}
