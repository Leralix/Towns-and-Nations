package org.leralix.tan.storage.typeadapter;

import com.google.gson.*;
import org.leralix.tan.war.info.AttackNotYetStarted;
import org.leralix.tan.war.info.AttackResult;
import org.leralix.tan.war.info.AttackResultCancelled;
import org.leralix.tan.war.info.AttackResultCompleted;
import org.leralix.tan.war.legacy.wargoals.*;

import java.lang.reflect.Type;

public class AttackResultAdapter implements JsonDeserializer<AttackResult>, JsonSerializer<AttackResult> {

    @Override
    public AttackResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        return switch (type) {
            case "AttackNotYetStarted" -> context.deserialize(jsonObject, AttackNotYetStarted.class);
            case "AttackResultCancelled" -> context.deserialize(jsonObject, AttackResultCancelled.class);
            case "AttackResultCompleted" -> context.deserialize(jsonObject, AttackResultCompleted.class);
            default -> throw new JsonParseException("Unknown type: " + type);
        };
    }

    @Override
    public JsonElement serialize(AttackResult src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = context.serialize(src, src.getClass()).getAsJsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
        return result;
    }

}
