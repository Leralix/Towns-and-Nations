package org.leralix.tan.data.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.tan.data.DbManager;
import org.leralix.tan.data.timezone.TimeZoneEnum;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.constants.database.RedisConfig;
import redis.clients.jedis.Jedis;

import java.util.*;

public class PlayerDbManager extends DbManager<ITanPlayer> {

    public PlayerDbManager(RedisConfig redisConfig) {
        super(redisConfig, "player");
    }

    @Override
    public ITanPlayer load(String id) {
        return load(UUID.fromString(id));
    }

    public ITanPlayer load(UUID uuid) {
        try (Jedis jedis = pool.getResource()) {

            String key = keyPrefix + ":" + uuid;

            Map<String, String> map = jedis.hgetAll(key);

            if (map.isEmpty()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    return null;
                }
                return registerNewPlayer(uuid);
            }

            List<String> properties = getStringList(map, "properties");
            List<String> attackInvolvedIn = getStringList(map, "attackInvolvedIn");

            return new PlayerData(
                    uuid,
                    map.get("name"),
                    Double.parseDouble(map.get("balance")),
                    map.get("townId"),
                    getIntOrNull(map.get("townRankId")),
                    getIntOrNull(map.get("regionRankId")),
                    getIntOrNull(map.get("nationRankId")),
                    properties,
                    attackInvolvedIn,
                    getEnum(LangType.class, map, "langType"),
                    getEnum(TimeZoneEnum.class, map, "timeZone")
            );
        }
    }


    private ITanPlayer registerNewPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null) {
            return null;
        }

        ITanPlayer data = new PlayerData(Bukkit.getOfflinePlayer(uuid));

        save(data);

        return data;
    }

    @Override
    public void save(ITanPlayer data) {
        try (Jedis jedis = pool.getResource()) {

            String key = keyPrefix + ":" + data.getID();

            Map<String, String> map = new HashMap<>();

            put(map, "name", data.getNameStored());
            put(map, "balance", data.getBalance());
            put(map, "townId", data.getTownId());
            put(map, "townRankId", data.getTownRankID());
            put(map, "regionRankId", data.getRegionRankID());
            put(map, "nationRankId", data.getNationRankID());

            put(map, "properties",
                    data.getPropertiesListID().isEmpty() ? null : String.join(",", data.getPropertiesListID()));

            put(map, "attackInvolvedIn",
                    data.getAttackInvolvedIn().isEmpty() ? null : String.join(",", data.getAttackInvolvedIn()));

            put(map, "langType", data.getLang().name());
            put(map, "timeZone", data.getTimeZone().name());

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
