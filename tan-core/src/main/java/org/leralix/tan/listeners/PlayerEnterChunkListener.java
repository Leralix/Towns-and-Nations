package org.leralix.tan.listeners;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.dataclass.chunk.WildernessChunk;
import org.leralix.tan.enums.ChunkType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.PlayerAutoClaimStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

public class PlayerEnterChunkListener implements Listener {

  private final boolean displayTerritoryNamewithColor;
  private final NewClaimedChunkStorage newClaimedChunkStorage;
  private final PlayerDataStorage playerDataStorage;

  public PlayerEnterChunkListener() {
    displayTerritoryNamewithColor =
        ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("displayTerritoryNameWithOwnColor");
    newClaimedChunkStorage = NewClaimedChunkStorage.getInstance();
    playerDataStorage = PlayerDataStorage.getInstance();
  }

  @EventHandler
  public void playerMoveEvent(final @NotNull PlayerMoveEvent event) {

    Chunk currentChunk = event.getFrom().getChunk();
    Chunk nextChunk = event.getTo().getChunk();

    if (currentChunk.equals(nextChunk)) {
      return;
    }

    Player player = event.getPlayer();

    // If both chunks are not claimed, no need to display anything
    if (!newClaimedChunkStorage.isChunkClaimed(currentChunk)
        && !newClaimedChunkStorage.isChunkClaimed(nextChunk)) {

      if (PlayerAutoClaimStorage.containsPlayer(event.getPlayer())) {
        autoClaimChunk(event, nextChunk, player);
      }
      return;
    }

    ClaimedChunk2 currentClaimedChunk = newClaimedChunkStorage.get(currentChunk);
    ClaimedChunk2 nextClaimedChunk = newClaimedChunkStorage.get(nextChunk);

    // Both chunks have the same owner, no need to change
    if (sameOwner(currentClaimedChunk, nextClaimedChunk)) {
      return;
    }
    // If territory deny access to players with a certain relation.
    if (nextClaimedChunk instanceof TerritoryChunk territoryChunk) {
      playerDataStorage
          .get(player)
          .thenAccept(
              tanPlayer -> {
                territoryChunk
                    .getOwner()
                    .getWorstRelationWith(tanPlayer)
                    .thenAccept(
                        worstRelation -> {
                          // CRITICAL: Event modification MUST happen synchronously before event
                          // completes
                          // We cannot cancel event after the handler returns, so we do it
                          // immediately
                          if (!Constants.getRelationConstants(worstRelation).canAccessTerritory()) {
                            // Event is already processed, too late to cancel
                            // We need to handle this differently - teleport player back
                            org.leralix.tan.utils.FoliaScheduler.runTask(
                                org.leralix.tan.TownsAndNations.getPlugin(),
                                () -> {
                                  // Use async teleport for Folia/Paper compatibility
                                  player.teleportAsync(event.getFrom());
                                  LangType lang = tanPlayer.getLang();
                                  TanChatUtils.message(
                                      player,
                                      Lang.PLAYER_CANNOT_ENTER_CHUNK_WITH_RELATION.get(
                                          lang,
                                          territoryChunk.getOwner().getColoredName(),
                                          worstRelation.getColoredName(lang)));
                                });
                          } else {
                            org.leralix.tan.utils.FoliaScheduler.runTask(
                                org.leralix.tan.TownsAndNations.getPlugin(),
                                () -> {
                                  nextClaimedChunk.playerEnterClaimedArea(
                                      player, displayTerritoryNamewithColor);
                                });
                          }
                        });
              });
    } else {
      nextClaimedChunk.playerEnterClaimedArea(player, displayTerritoryNamewithColor);
    }

    if (nextClaimedChunk instanceof WildernessChunk
        && PlayerAutoClaimStorage.containsPlayer(event.getPlayer())) {
      autoClaimChunk(event, nextChunk, player);
    }
  }

  private void autoClaimChunk(
      final @NotNull PlayerMoveEvent e,
      final @NotNull Chunk nextChunk,
      final @NotNull Player player) {
    ChunkType chunkType = PlayerAutoClaimStorage.getChunkType(e.getPlayer());
    ITanPlayer playerStat =
        PlayerDataStorage.getInstance().getSync(player.getUniqueId().toString());

    if (chunkType == ChunkType.TOWN) {
      if (!playerStat.hasTown()) {
        TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(player));
        return;
      }
      playerStat
          .getTown()
          .thenAccept(
              townData -> {
                if (townData != null) {
                  townData.claimChunk(player, nextChunk);
                }
              });
    }
    if (chunkType == ChunkType.REGION) {
      if (!playerStat.hasRegion()) {
        TanChatUtils.message(player, Lang.PLAYER_NO_REGION.get(player));
        return;
      }
      playerStat
          .getRegion()
          .thenAccept(
              regionData -> {
                if (regionData != null) {
                  regionData.claimChunk(player, nextChunk);
                }
              });
    }
  }

  public static boolean sameOwner(final ClaimedChunk2 a, final ClaimedChunk2 b) {
    if (a == b) return true;
    return a.getOwnerID().equals(b.getOwnerID());
  }
}
