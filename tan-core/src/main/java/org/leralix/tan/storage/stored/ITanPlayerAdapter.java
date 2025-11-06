package org.leralix.tan.storage.stored;

import com.google.gson.*;
import java.lang.reflect.Type;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.PlayerData;

public class ITanPlayerAdapter implements JsonSerializer<ITanPlayer>, JsonDeserializer<ITanPlayer> {

  @Override
  public ITanPlayer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    PlayerData playerData = context.deserialize(json, PlayerData.class);
    if (jsonObject.has("uuid")) {
      playerData.setUuid(jsonObject.get("uuid").getAsString());
    }
    return playerData;
  }

  @Override
  public JsonElement serialize(ITanPlayer src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject jsonObject = context.serialize(src, src.getClass()).getAsJsonObject();
    jsonObject.addProperty("uuid", src.getID());
    return jsonObject;
  }
}
