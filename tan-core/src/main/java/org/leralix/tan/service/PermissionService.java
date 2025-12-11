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

    if (EnabledPermissions.getInstance().isPermissionDisabled(permissionType)) {
      return CompletableFuture.completedFuture(true);
    }

    if (SudoPlayerStorage.isSudoPlayer(player)) return CompletableFuture.completedFuture(true);

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

  public CompletableFuture<Boolean> canPvpHappenAsync(Player player1, Player player2) {
    if (!NewClaimedChunkStorage.getInstance()
        .get(player2.getLocation().getChunk())
        .canPVPHappen()) {
      return CompletableFuture.completedFuture(false);
    }

    CompletableFuture<ITanPlayer> tanPlayer1Future = PlayerDataStorage.getInstance().get(player1);
    CompletableFuture<ITanPlayer> tanPlayer2Future = PlayerDataStorage.getInstance().get(player2);

    return CompletableFuture.allOf(tanPlayer1Future, tanPlayer2Future)
        .thenApply(
            v -> {
              ITanPlayer tanPlayer = tanPlayer1Future.join();
              ITanPlayer tanPlayer2 = tanPlayer2Future.join();
              TownRelation relation = tanPlayer.getRelationWithPlayerSync(tanPlayer2);
              return Constants.getRelationConstants(relation).canPvP();
            });
  }

  @Deprecated
  public boolean canPvpHappen(Player player1, Player player2) {
    if (!NewClaimedChunkStorage.getInstance()
        .get(player2.getLocation().getChunk())
        .canPVPHappen()) {
      return false;
    }

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player1).join();
    ITanPlayer tanPlayer2 = PlayerDataStorage.getInstance().get(player2).join();
    TownRelation relation = tanPlayer.getRelationWithPlayerSync(tanPlayer2);

    return Constants.getRelationConstants(relation).canPvP();
  }
}
