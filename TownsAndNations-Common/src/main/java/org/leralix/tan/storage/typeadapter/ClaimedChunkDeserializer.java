package org.leralix.tan.storage.typeadapter;

import com.google.gson.*;
import org.leralix.tan.data.chunk.*;

import java.lang.reflect.Type;

public class ClaimedChunkDeserializer implements JsonDeserializer<ChunkData> {

    @Override
    public ChunkData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject chunkData = json.getAsJsonObject();
        JsonObject vector2D = chunkData.getAsJsonObject("vector2D");

        int x = vector2D.get("x").getAsInt();
        int z = vector2D.get("z").getAsInt();
        String worldUUID = vector2D.get("worldID").getAsString();
        String ownerID = chunkData.get("ownerID").getAsString();

        ChunkData chunk;

        if (ownerID.startsWith("T")) {
            chunk = new TownClaimedChunk(x, z, worldUUID, ownerID);
        } else if (ownerID.startsWith("R")) {
            chunk = new RegionClaimedChunk(x, z, worldUUID, ownerID);
        } else if (ownerID.startsWith("N")) {
            chunk = new NationClaimedChunk(x, z, worldUUID, ownerID);
        } else if (ownerID.startsWith("L")) {
            chunk = new LandmarkClaimedChunk(x, z, worldUUID, ownerID);
        } else {
            throw new JsonParseException("Unknown ownerID: " + ownerID);
        }

        if (chunkData.has("occupierID") && chunk instanceof TerritoryChunk territoryChunk) {
            territoryChunk.setOccupierID(chunkData.get("occupierID").getAsString());
        }

        return chunk;
    }
}