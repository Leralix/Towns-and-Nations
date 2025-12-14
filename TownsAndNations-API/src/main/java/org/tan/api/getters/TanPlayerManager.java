package org.tan.api.getters;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.tan.api.interfaces.TanPlayer;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * The TanPlayerManager interface provides methods for accessing and managing player-related content within the application.
 * It allows retrieval of players by their names, UUIDs or Player/OfflinePlayer objects and provides a collection of all registered players.
 * This interface is essential for handling player-related operations and ensuring proper management of player data.
 */
public interface TanPlayerManager {

    /**
     * Get a player by name
     * <br>
     * This method will use {@link org.bukkit.Bukkit#getOfflinePlayer(String)} to get the player
     * @param playerName the name of the player
     * @return the {@link TanPlayer} with the specified name, or {@link Optional#empty()} if not found.
     */
    Optional<TanPlayer> get(String playerName);

    /**
     * Get a player by UUID
     * @param playerUUID the UUID of the player
     * @return the {@link TanPlayer} with the specified UUID, or {@link Optional#empty()} if not found.
     */
    Optional<TanPlayer> get(UUID playerUUID);

    /**
     * Get a player by {@link Player}
     * <br>
     * If the player is not yet registered, it will be registered and returned.
     * @param player the player
     * @return the {@link TanPlayer} with the specified player.
     */
    TanPlayer get(Player player);

    /**
     * Get a player by {@link OfflinePlayer}
     * <br>
     * If the player is not yet registered, it will be registered and returned.
     * @param offlinePlayer the offline player
     * @return the {@link TanPlayer} with the specified offline player.
     */
    TanPlayer get(OfflinePlayer offlinePlayer);

    /**
     * @return all players registered
     */
    Collection<TanPlayer> getAll();
}
