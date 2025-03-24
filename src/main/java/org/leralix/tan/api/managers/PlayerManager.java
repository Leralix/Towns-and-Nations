package org.leralix.tan.api.managers;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.api.wrappers.PlayerDataWrapper;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.tan.api.getters.TanPlayerManager;
import org.tan.api.interfaces.TanPlayer;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerManager implements TanPlayerManager {

    private final PlayerDataStorage playerDataStorage;

    public PlayerManager() {
        playerDataStorage = PlayerDataStorage.getInstance();
    }

    @Override
    public Optional<TanPlayer> get(String name) {
        return Optional.ofNullable(PlayerDataWrapper.of(playerDataStorage.get(name)));
    }

    @Override
    public Optional<TanPlayer> get(UUID uuid) {
        return Optional.ofNullable(PlayerDataWrapper.of(playerDataStorage.get(uuid)));
    }

    @Override
    public TanPlayer get(Player player) {
        return PlayerDataWrapper.of(playerDataStorage.get(player));
    }

    @Override
    public TanPlayer get(OfflinePlayer offlinePlayer) {
        return PlayerDataWrapper.of(playerDataStorage.get(offlinePlayer));
    }

    @Override
    public Collection<TanPlayer> getAll() {
        return playerDataStorage.getAll().stream()
                .map(PlayerDataWrapper::of)
                .toList();
    }
}
