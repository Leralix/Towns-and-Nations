package org.leralix.tan.storage.stored;

import com.google.gson.*;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.PlayerData;

import java.lang.reflect.Type;

public class ITanPlayerAdapter implements JsonSerializer<ITanPlayer>, JsonDeserializer<ITanPlayer> {

    @Override
    public ITanPlayer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return context.deserialize(json, PlayerData.class);
    }

    @Override
    public JsonElement serialize(ITanPlayer src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src, PlayerData.class);
    }
}
