package org.leralix.tan.listeners;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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

  private final Map<UUID, Chunk> playerLastChunk = new ConcurrentHashMap<>();

  private final Map<UUID, String> playerLastTerritory = new ConcurrentHashMap<>();

  public PlayerEnterChunkListener() {
    displayTerritoryNamewithColor =
        ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("displayTerritoryNameWithOwnColor");
    newClaimedChunkStorage = NewClaimedChunkStorage.getInstance();
    playerDataStorage = PlayerDataStorage.getInstance();
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void playerMoveEvent(final @NotNull PlayerMoveEvent event) {

    Chunk nextChunk = event.getTo().getChunk();
    Player player = event.getPlayer();
    UUID playerUuid = player.getUniqueId();

    Chunk lastChunk = playerLastChunk.get(playerUuid);
    if (nextChunk.equals(lastChunk)) {
      return;
    }
    playerLastChunk.put(playerUuid, nextChunk);

    ClaimedChunk2 nextClaimedChunk = newClaimedChunkStorage.get(nextChunk);

    if (nextClaimedChunk == null) {
      handleWilderness(event, nextChunk, player, playerUuid);
      return;
    }

    String nextTerritory = nextClaimedChunk.getOwnerID();
    String lastTerritory = playerLastTerritory.get(playerUuid);

    if (nextTerritory.equals(lastTerritory)) {
      return;
    }
    playerLastTerritory.put(playerUuid, nextTerritory);

    if (nextClaimedChunk instanceof TerritoryChunk territoryChunk) {
      handleTerritoryChunk(event, territoryChunk, player, playerUuid);
    } else {
      nextClaimedChunk.playerEnterClaimedArea(player, displayTerritoryNamewithColor);
    }

    if (nextClaimedChunk instanceof WildernessChunk
        && PlayerAutoClaimStorage.containsPlayer(event.getPlayer())) {
      autoClaimChunk(event, nextChunk, player);
    }
  }

  private void handleTerritoryChunk(
      final @NotNull PlayerMoveEvent event,
      final @NotNull TerritoryChunk territoryChunk,
      final @NotNull Player player,
      final @NotNull UUID playerUuid) {

    playerDataStorage
        .get(player)
        .thenAccept(
            tanPlayer -> {
              if (tanPlayer != null) {
                checkRelationAndExecute(event, territoryChunk, tanPlayer, player);
              }
            });
  }

  private void checkRelationAndExecute(
      final @NotNull PlayerMoveEvent event,
      final @NotNull TerritoryChunk territoryChunk,
      final @NotNull ITanPlayer tanPlayer,
      final @NotNull Player player) {

    territoryChunk
        .getOwner()
        .getWorstRelationWith(tanPlayer)
        .thenAccept(
            worstRelation -> {
              if (!Constants.getRelationConstants(worstRelation).canAccessTerritory()) {
                org.leralix.tan.utils.FoliaScheduler.runTask(
                    org.leralix.tan.TownsAndNations.getPlugin(),
                    () -> {
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
                      territoryChunk.playerEnterClaimedArea(player, displayTerritoryNamewithColor);
                    });
              }
            });
  }

  private void handleWilderness(
      final @NotNull PlayerMoveEvent event,
      final @NotNull Chunk nextChunk,
      final @NotNull Player player,
      final @NotNull UUID playerUuid) {

    if (PlayerAutoClaimStorage.containsPlayer(player)) {
      autoClaimChunk(event, nextChunk, player);
    }
  }

  @EventHandler
  public void onPlayerQuit(final @NotNull PlayerQuitEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    playerLastChunk.remove(uuid);
    playerLastTerritory.remove(uuid);
  }

  private void autoClaimChunk(
      final @NotNull PlayerMoveEvent e,
      final @NotNull Chunk nextChunk,
      final @NotNull Player player) {
    ChunkType chunkType = PlayerAutoClaimStorage.getChunkType(e.getPlayer());

    playerDataStorage
        .get(player.getUniqueId().toString())
        .thenAccept(
            playerStat -> {
              if (playerStat == null) {
                return;
              }

              if (chunkType == ChunkType.TOWN) {
                if (!playerStat.hasTown()) {
                  org.leralix.tan.utils.FoliaScheduler.runTask(
                      org.leralix.tan.TownsAndNations.getPlugin(),
                      () -> TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(player)));
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
            });
  }

  public static boolean sameOwner(final ClaimedChunk2 a, final ClaimedChunk2 b) {
    if (a == b) return true;
    return a.getOwnerID().equals(b.getOwnerID());
  }
}
