package org.leralix.tan.storage.stored.database;

import org.leralix.tan.data.DbManager;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.utils.constants.database.RedisConfig;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegionDbManager extends DbManager<Region> {

    public RegionDbManager(RedisConfig redisConfig) {
        super(redisConfig, "region");
    }

    @Override
    public Region load(String id) {
        try (Jedis jedis = pool.getResource()) {

            String key = keyPrefix + ":" + id;

            Map<String, String> map = jedis.hgetAll(key);

            if (map.isEmpty()) {
                return null;
            }

            return GSON.fromJson(map.get("data"), RegionData.class);
        }
    }

    @Override
    public void save(Region data) {
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
