package org.leralix.tan.storage.TypeAdapter;

import com.google.gson.*;
import org.leralix.tan.dataclass.wars.wargoals.*;

import java.lang.reflect.Type;

public class WargoalTypeAdapter implements JsonDeserializer<WarGoal>, JsonSerializer<WarGoal> {

    @Override
    public WarGoal deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        return switch (type) {
            case "ConquerWarGoal" -> context.deserialize(jsonObject, ConquerWarGoal.class);
            case "SubjugateWarGoal" -> context.deserialize(jsonObject, SubjugateWarGoal.class);
            case "NoWarGoal" -> context.deserialize(jsonObject, NoWarGoal.class);
            case "LiberateWarGoal" -> context.deserialize(jsonObject, LiberateWarGoal.class);
            default -> throw new JsonParseException("Unknown type: " + type);
        };
    }

    @Override
    public JsonElement serialize(WarGoal src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = context.serialize(src, src.getClass()).getAsJsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
        return result;
    }

}
