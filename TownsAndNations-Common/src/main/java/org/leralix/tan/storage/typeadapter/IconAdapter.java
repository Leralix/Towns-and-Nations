package org.leralix.tan.storage.typeadapter;

import com.google.gson.*;
import org.leralix.tan.data.territory.cosmetic.ICustomIcon;
import org.leralix.tan.data.territory.cosmetic.PlayerHeadIcon;
import org.leralix.tan.data.territory.cosmetic.CustomIcon;

import java.lang.reflect.Type;

public class IconAdapter implements JsonDeserializer<ICustomIcon>{

    @Override
    public ICustomIcon deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        boolean isPlayerHead = jsonObject.get("playerUUID") != null;
        Gson gson = new Gson();
        if (isPlayerHead) {
            return gson.fromJson(jsonObject, PlayerHeadIcon.class);
        } else {
            return gson.fromJson(jsonObject, CustomIcon.class);
        }
    }

}
