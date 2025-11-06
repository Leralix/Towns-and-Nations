package org.leralix.tan.storage.typeadapter;

import com.google.gson.*;
import java.lang.reflect.Type;
import org.leralix.tan.dataclass.territory.cosmetic.CustomIcon;
import org.leralix.tan.dataclass.territory.cosmetic.ICustomIcon;
import org.leralix.tan.dataclass.territory.cosmetic.PlayerHeadIcon;

public class IconAdapter implements JsonDeserializer<ICustomIcon> {

  @Override
  public ICustomIcon deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
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
