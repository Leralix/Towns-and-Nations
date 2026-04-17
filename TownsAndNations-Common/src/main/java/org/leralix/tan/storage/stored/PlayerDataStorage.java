package org.leralix.tan.storage.stored;

import org.bukkit.OfflinePlayer;
import org.leralix.tan.data.player.ITanPlayer;

import java.util.Collection;
import java.util.UUID;

/**
 * Public interface of the playerDataStorage. Used for obtaining up-to-date data about each player.
 */
public interface PlayerDataStorage {

    ITanPlayer get(String playerID);

    default ITanPlayer get(OfflinePlayer player){
        return get(player.getUniqueId());
    }

    default ITanPlayer get(UUID playerID){
        return get(playerID.toString());
    }

    Collection<ITanPlayer> getAllPlayers();

    /**
     * Save all data in a secure file
     * @deprecated Saving periodically is only used by {@link org.leralix.tan.storage.stored.json.JsonStorage}.
     * It needs to be separated.
     */
    @Deprecated
    void save();
}
