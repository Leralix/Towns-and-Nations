package org.leralix.tan.storage.typeadapter;

import com.google.gson.*;
import java.lang.reflect.Type;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.property.AbstractOwner;

/**
 * Custom deserializer for PropertyData to handle backward compatibility with the deprecated
 * owningPlayerID field.
 *
 * <p>This adapter migrates from the old owningPlayerID field to the new owner (AbstractOwner)
 * field.
 */
public class PropertyDataDeserializer implements JsonDeserializer<PropertyData> {

  @Override
  public PropertyData deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {

    JsonObject jsonObject = json.getAsJsonObject();

    // Check if we need to migrate from old format (owningPlayerID) to new format (owner)
    if (jsonObject.has("owningPlayerID")
        && jsonObject.get("owningPlayerID") != null
        && !jsonObject.get("owningPlayerID").isJsonNull()
        && (!jsonObject.has("owner") || jsonObject.get("owner").isJsonNull())) {

      // Migrate: create owner object from owningPlayerID
      String owningPlayerID = jsonObject.get("owningPlayerID").getAsString();
      JsonObject ownerObject = new JsonObject();
      ownerObject.addProperty("type", "PLAYER");
      ownerObject.addProperty("playerID", owningPlayerID);
      jsonObject.add("owner", ownerObject);
    }

    // Use default Gson to deserialize the rest
    return new GsonBuilder()
        .registerTypeAdapter(AbstractOwner.class, new OwnerDeserializer())
        .create()
        .fromJson(jsonObject, PropertyData.class);
  }
}
