package org.tan.api.getters;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.tan.api.interfaces.TanPlayer;

public interface TanPlayerManager {
  Optional<TanPlayer> get(String name);

  Optional<TanPlayer> get(UUID uuid);

  TanPlayer get(Player player);

  TanPlayer get(OfflinePlayer offlinePlayer);

  Collection<TanPlayer> getAll();
}
