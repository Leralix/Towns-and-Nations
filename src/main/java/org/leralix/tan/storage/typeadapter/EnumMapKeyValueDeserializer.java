package org.leralix.tan.storage.typeadapter;

import com.google.gson.*;
import org.leralix.tan.TownsAndNations;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class EnumMapKeyValueDeserializer<K extends Enum<K>, V extends Enum<V>> implements JsonDeserializer<Map<K, V>> {

    private final Class<K> keyEnumClass;
    private final Class<V> valueEnumClass;

    public EnumMapKeyValueDeserializer(Class<K> keyEnumClass, Class<V> valueEnumClass) {
        this.keyEnumClass = keyEnumClass;
        this.valueEnumClass = valueEnumClass;
    }

    @Override
    public Map<K, V> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<K, V> resultMap = new HashMap<>();
        JsonObject jsonObject = json.getAsJsonObject();
        Logger logger = TownsAndNations.getPlugin().getPluginLogger();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String keyString = entry.getKey();
            String valueString = entry.getValue().getAsString();

            K enumKey;
            V enumValue;

            try {
                enumKey = Enum.valueOf(keyEnumClass, keyString);
                enumValue = Enum.valueOf(valueEnumClass, valueString);
            } catch (IllegalArgumentException e) {
                logger.warning("Invalid key (probably older version) deleted: " + keyString);
                continue;
            }

            resultMap.put(enumKey, enumValue);
        }

        return resultMap;
    }
}
