package org.leralix.tan.service;

import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.storage.SudoPlayerStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.constants.EnabledPermissions;

public class PermissionService {

  public CompletableFuture<Boolean> canPlayerDoAction(
      Location location, Player player, ChunkPermissionType permissionType) {

    // Admins disabled the specific permission
    if (EnabledPermissions.getInstance().isPermissionDisabled(permissionType)) {
      return CompletableFuture.completedFuture(true);
    }

    // Player in admin mode
    if (SudoPlayerStorage.isSudoPlayer(player)) return CompletableFuture.completedFuture(true);

    // PERFORMANCE FIX: Use cache-only lookup to avoid blocking DB calls
    // Chunks should be preloaded in cache, so this is almost always instant
    ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(location.getChunk());

    return PlayerDataStorage.getInstance()
        .get(player)
        .thenApply(
            tanPlayer -> {
              boolean isAtWar = tanPlayer.isAtWarWith(claimedChunk.getOwner());
              if (isAtWar) return true;

              return claimedChunk.canPlayerDo(player, permissionType, location);
            });
  }

  public boolean canPvpHappen(Player player1, Player player2) {
    if (!NewClaimedChunkStorage.getInstance()
        .get(player2.getLocation().getChunk())
        .canPVPHappen()) {
      return false;
    }

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player1);
    ITanPlayer tanPlayer2 = PlayerDataStorage.getInstance().getSync(player2);
    TownRelation relation = tanPlayer.getRelationWithPlayerSync(tanPlayer2);

    return Constants.getRelationConstants(relation).canPvP();
  }
}
