package org.leralix.tan.storage.typeadapter;

import com.google.gson.*;
import org.leralix.tan.war.wargoals.*;

import java.lang.reflect.Type;

public class WargoalTypeAdapter implements JsonDeserializer<WarGoal>, JsonSerializer<WarGoal> {

    @Override
    public WarGoal deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        return switch (type) {
            case "ConquerWarGoal" -> context.deserialize(jsonObject, ConquerWarGoal.class);
            case "SubjugateWarGoal" -> context.deserialize(jsonObject, SubjugateWarGoal.class);
            case "LiberateWarGoal" -> context.deserialize(jsonObject, LiberateWarGoal.class);
            case "CaptureLandmarkWarGoal" -> {
                if (!jsonObject.has("landmarkToCaptureID") || jsonObject.get("landmarkToCaptureID").isJsonNull()) {
                    yield new ConquerWarGoal(1);
                }
                String landmarkID = jsonObject.get("landmarkToCaptureID").getAsString();
                if (landmarkID == null || landmarkID.isEmpty()) {
                    yield new ConquerWarGoal(1);
                }
                yield context.deserialize(jsonObject, CaptureLandmarkWarGoal.class);
            }
            case "CaptureFortWarGoal" -> {
                if (!jsonObject.has("fortToCaptureID") || jsonObject.get("fortToCaptureID").isJsonNull()) {
                    yield new ConquerWarGoal(1);
                }
                String fortID = jsonObject.get("fortToCaptureID").getAsString();
                if (fortID == null || fortID.isEmpty()) {
                    yield new ConquerWarGoal(1);
                }
                yield context.deserialize(jsonObject, CaptureFortWarGoal.class);
            }
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
