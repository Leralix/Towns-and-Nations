package org.leralix.tan.storage.stored.database;

import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.player.PlayerData;
import org.leralix.tan.data.player.PlayerDatabase;
import org.leralix.tan.data.player.PlayerDbManager;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.database.RedisConfig;

import java.util.Collection;
import java.util.List;

public class PlayerDatabaseStorage extends DatabaseStorage<PlayerDatabase, PlayerData> implements PlayerDataStorage {

    public PlayerDatabaseStorage(RedisConfig redisConfig) {
        super(new PlayerDbManager(redisConfig));
    }

    @Override
    public ITanPlayer get(String playerID) {
        return getOrLoad(playerID, this::load);
    }

    @Override
    public Collection<ITanPlayer> getAllPlayers() {
        return List.copyOf(databaseManager.getAll());
    }

    @Override
    public void save() {
        // No automatic save for the Full DB storage.
    }

    private @NotNull PlayerDatabase load(String uuid) {
        PlayerData data = databaseManager.load(uuid);
        return new PlayerDatabase(data, databaseManager);
    }
}
