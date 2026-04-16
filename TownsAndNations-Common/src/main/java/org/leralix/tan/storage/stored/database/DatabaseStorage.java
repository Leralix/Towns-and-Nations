package org.leralix.tan.storage.stored.database;

import org.leralix.tan.data.DbManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class DatabaseStorage<T extends DatabaseData<U>, U> {

    protected final Map<String, T> cache = new ConcurrentHashMap<>();
    protected final DbManager<U> databaseManager;

    protected DatabaseStorage(DbManager<U> databaseManager) {
        this.databaseManager = databaseManager;

        Thread pubSubThread = new Thread(() -> {
            try (Jedis jedis = databaseManager.getPool()) {
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        String[] parts = message.split(":");
                        if (parts.length < 2) return;
                        String id = parts[1];

                        cache.computeIfPresent(id, (k, v) -> {
                            v.setData(databaseManager.load(id));
                            return v;
                        });
                    }
                }, databaseManager.getChannelName());
            }
        });
        pubSubThread.setDaemon(true);
        pubSubThread.start();
    }

    protected T getOrLoad(String id, Function<String, T> loader) {
        if(id == null){
            return null;
        }
        T cached = cache.get(id);
        if (cached != null) {
            return cached;
        }

        T loaded = loader.apply(id);
        if (loaded == null) {
            return null;
        }

        T previous = cache.putIfAbsent(id, loaded);
        return previous != null ? previous : loaded;
    }

}
