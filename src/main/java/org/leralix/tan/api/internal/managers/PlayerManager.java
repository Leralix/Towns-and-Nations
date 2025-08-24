package org.leralix.tan.api.internal.managers;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.api.internal.wrappers.TanPlayerWrapper;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.tan.api.getters.TanPlayerManager;
import org.tan.api.interfaces.TanPlayer;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class PlayerManager implements TanPlayerManager {

    private final PlayerDataStorage playerDataStorage;

    public PlayerManager() {
        this.playerDataStorage = PlayerDataStorage.getInstance();
    }

    @Override
    public Optional<TanPlayer> get(String name) {
        return Optional.ofNullable(TanPlayerWrapper.of(playerDataStorage.get(name)));
    }

    @Override
    public Optional<TanPlayer> get(UUID uuid) {
        return Optional.ofNullable(TanPlayerWrapper.of(playerDataStorage.get(uuid)));
    }

    @Override
    public TanPlayer get(Player player) {
        return TanPlayerWrapper.of(playerDataStorage.get(player));
    }

    @Override
    public TanPlayer get(OfflinePlayer offlinePlayer) {
        return TanPlayerWrapper.of(playerDataStorage.get(offlinePlayer));
    }

    @Override
    public Collection<TanPlayer> getAll() {
        return playerDataStorage.getAll().values().stream()
                .map(TanPlayerWrapper::of)
                .toList();
    }
}
