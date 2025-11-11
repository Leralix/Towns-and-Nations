package org.tan.api.interfaces;

import java.util.Optional;
import java.util.UUID;
import org.bukkit.Location;
import org.tan.api.enums.EChunkPermission;

public interface TanClaimedChunk {
  int getX();

  int getZ();

  UUID getWorldUUID();

  String getworldName();

  Boolean isClaimed();

  Optional<UUID> getOwnerID();

  void unclaim();

  boolean canClaim(TanTerritory territory);

  void claim(TanTerritory territory);

  boolean canBeGriefByExplosion();

  boolean canBeGriefByFire();

  boolean canPvpHappen();

  boolean canPlayerDoAction(TanPlayer player, EChunkPermission permission, Location location);
}
