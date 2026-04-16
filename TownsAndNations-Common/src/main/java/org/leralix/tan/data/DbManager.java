package org.leralix.tan.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.building.property.owner.AbstractOwner;
import org.leralix.tan.data.chunk.ChunkData;
import org.leralix.tan.data.territory.cosmetic.ICustomIcon;
import org.leralix.tan.data.territory.permission.ChunkPermissionType;
import org.leralix.tan.data.territory.permission.RelationPermission;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.storage.typeadapter.*;
import org.leralix.tan.utils.constants.database.RedisConfig;
import org.leralix.tan.war.info.AttackResult;
import org.leralix.tan.war.wargoals.WarGoal;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DbManager<T> {

    protected final JedisPool pool;
    private final String channelName;
    protected final String keyPrefix;
    protected static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(ChunkData.class, new ClaimedChunkDeserializer())
            .registerTypeAdapter(new TypeToken<Map<ChunkPermissionType, RelationPermission>>() {}.getType(), new EnumMapKeyValueDeserializer<>(ChunkPermissionType.class, RelationPermission.class))
            .registerTypeAdapter(new TypeToken<Map<TownRelation, List<String>>>() {}.getType(),new EnumMapDeserializer<>(TownRelation.class, new TypeToken<List<String>>(){}.getType()))
            .registerTypeAdapter(new TypeToken<List<RelationPermission>>() {}.getType(),new EnumMapDeserializer<>(RelationPermission.class, new TypeToken<List<String>>(){}.getType()))
            .registerTypeAdapter(AbstractOwner.class, new OwnerDeserializer())
            .registerTypeAdapter(WarGoal.class, new WargoalTypeAdapter())
            .registerTypeAdapter(AttackResult.class, new AttackResultAdapter())
            .registerTypeAdapter(ICustomIcon.class, new IconAdapter())
            .create();

    protected DbManager(RedisConfig redisConfig, String keyPrefix) {
        this.pool = new JedisPool(
                redisConfig.host(),
                redisConfig.port(),
                redisConfig.username(),
                redisConfig.password()
        );
        this.channelName = "tan:" + keyPrefix + ":update";
        this.keyPrefix = keyPrefix;
    }

    public abstract T load(String id);

    public abstract void save(T data);

    protected static <E extends Enum<E>> E getEnum(Class<E> enumClass, Map<String, String> map, String key) {
        String value = map.get(key);
        if (value == null) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    protected static @NotNull List<String> getStringList(Map<String, String> map, String key) {
        String list = map.get(key);
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        return List.of(list.split(","));
    }

    protected static void put(Map<String, String> map, String key, Object value) {
        if (value != null) {
            map.put(key, String.valueOf(value));
        }
    }

    protected static Integer getIntOrNull(String value) {
        if (value == null) {
            return null;
        }
        return Integer.parseInt(value);
    }

    public Jedis getPool() {
        return pool.getResource();
    }

    public String getChannelName() {
        return channelName;
    }

    public void publishUpdate(Jedis jedis, String uuid) {
        jedis.publish(getChannelName(), uuid);
    }

    public void delete(String id) {
        try (Jedis jedis = pool.getResource()) {
            String key = "fort:" + id;
            jedis.del(key);
            publishUpdate(jedis, key);
        }
    }

    public List<T> getAll() {
        List<T> forts = new ArrayList<>();

        try (Jedis jedis = pool.getResource()) {

            String cursor = "0";

            do {
                ScanResult<String> scanResult = jedis.scan(cursor, new ScanParams().match(keyPrefix + ":*"));

                for (String key : scanResult.getResult()) {
                    String id = key.substring((keyPrefix + ":").length());
                    T data = load(id);
                    if (data != null) {
                        forts.add(data);
                    }
                }
                cursor = scanResult.getCursor();
            } while (!cursor.equals("0"));
        }
        return forts;
    }

    public Map<String,T> getMap() {
        Map<String,T> forts = new HashMap<>();

        try (Jedis jedis = pool.getResource()) {

            String cursor = "0";

            do {
                ScanResult<String> scanResult = jedis.scan(cursor, new ScanParams().match(keyPrefix + ":*"));

                for (String key : scanResult.getResult()) {
                    String id = key.substring((keyPrefix + ":").length());
                    T data = load(id);
                    if (data != null) {
                        forts.put(id, data);
                    }
                }
                cursor = scanResult.getCursor();
            } while (!cursor.equals("0"));
        }
        return forts;
    }
}
