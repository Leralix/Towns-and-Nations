package org.leralix.tan.storage.stored;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;

import java.util.Collection;
import java.util.UUID;

public interface PlayerDataStorage {


    ITanPlayer get(String playerID);

    ITanPlayer register(Player player);

    ITanPlayer get(OfflinePlayer player);

    ITanPlayer get(Player player);

    ITanPlayer get(UUID playerID);

    Collection<ITanPlayer> getAllPlayers();

    void save();
}
