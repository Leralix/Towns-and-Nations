package org.leralix.tan.storage.typeadapter;

import com.google.gson.*;
import java.lang.reflect.Type;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.property.AbstractOwner;

public class PropertyDataDeserializer implements JsonDeserializer<PropertyData> {

  @Override
  public PropertyData deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {

    JsonObject jsonObject = json.getAsJsonObject();

    if (jsonObject.has("owningPlayerID")
        && jsonObject.get("owningPlayerID") != null
        && !jsonObject.get("owningPlayerID").isJsonNull()
        && (!jsonObject.has("owner") || jsonObject.get("owner").isJsonNull())) {

      String owningPlayerID = jsonObject.get("owningPlayerID").getAsString();
      JsonObject ownerObject = new JsonObject();
      ownerObject.addProperty("type", "PLAYER");
      ownerObject.addProperty("playerID", owningPlayerID);
      jsonObject.add("owner", ownerObject);
    }

    return new GsonBuilder()
        .registerTypeAdapter(AbstractOwner.class, new OwnerDeserializer())
        .create()
        .fromJson(jsonObject, PropertyData.class);
  }
}
