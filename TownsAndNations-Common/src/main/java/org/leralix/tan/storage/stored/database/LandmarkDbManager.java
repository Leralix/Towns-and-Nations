package org.leralix.tan.storage.stored.database;

import org.leralix.tan.data.DbManager;
import org.leralix.tan.data.building.landmark.LandmarkData;
import org.leralix.tan.utils.constants.database.RedisConfig;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LandmarkDbManager extends DbManager<LandmarkData> {

    public LandmarkDbManager(RedisConfig redisConfig) {
        super(redisConfig, "landmark");
    }


    @Override
    public LandmarkData load(String id) {
        try (Jedis jedis = pool.getResource()) {

            String key = keyPrefix + ":" + id;

            Map<String, String> map = jedis.hgetAll(key);

            if (map.isEmpty()) {
                return null;
            }

            return GSON.fromJson(map.get("data"), LandmarkData.class);
        }
    }

    @Override
    public void save(LandmarkData data) {
        try (Jedis jedis = pool.getResource()) {

            String key = keyPrefix + ":" + data.getID();

            Map<String, String> map = new HashMap<>();

            put(map, "data", GSON.toJson(data));

            // Remove null values first
            map.values().removeIf(Objects::isNull);

            if (!map.isEmpty()) {
                jedis.hset(key, map);
            }
            publishUpdate(jedis, key);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
