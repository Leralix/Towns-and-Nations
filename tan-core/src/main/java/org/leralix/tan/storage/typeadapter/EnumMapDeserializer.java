package org.leralix.tan.storage.typeadapter;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.leralix.tan.TownsAndNations;

public class EnumMapDeserializer<E extends Enum<E>, V> implements JsonDeserializer<Map<E, V>> {

  private final Class<E> enumClass;
  private final Type valueType;

  public EnumMapDeserializer(Class<E> enumClass, Type valueType) {
    this.enumClass = enumClass;
    this.valueType = valueType;
  }

  @Override
  public Map<E, V> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    Map<E, V> resultMap = new HashMap<>();
    JsonObject jsonObject = json.getAsJsonObject();

    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
      String key = entry.getKey();
      E enumValue;

      try {
        enumValue = Enum.valueOf(enumClass, key);
      } catch (IllegalArgumentException e) {
        TownsAndNations.getPlugin()
            .getLogger()
            .warning("Invalid key (probably older version) deleted: " + key);
        continue;
      }

      // Désérialise la valeur associée
      V value = context.deserialize(entry.getValue(), valueType);
      resultMap.put(enumValue, value);
    }

    return resultMap;
  }
}
