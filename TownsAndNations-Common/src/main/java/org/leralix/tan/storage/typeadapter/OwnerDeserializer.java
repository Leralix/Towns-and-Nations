package org.leralix.tan.storage.typeadapter;

import com.google.gson.*;
import org.leralix.tan.data.building.property.owner.AbstractOwner;
import org.leralix.tan.data.building.property.owner.PlayerOwned;
import org.leralix.tan.data.building.property.owner.TerritoryOwned;

import java.lang.reflect.Type;

public class OwnerDeserializer implements JsonDeserializer<AbstractOwner> {

    @Override
    public AbstractOwner deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String ownerType = obj.get("type").getAsString();

        return switch (ownerType) {
            case "PLAYER" -> context.deserialize(json, PlayerOwned.class);
            case "TERRITORY" -> context.deserialize(json, TerritoryOwned.class);
            default -> throw new JsonParseException("Unknown owner type: " + ownerType);
        };
    }
}
