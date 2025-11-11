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

  // OPTIMIZATION #1: Cache player's last chunk to detect chunk changes
  private final Map<UUID, Chunk> playerLastChunk = new ConcurrentHashMap<>();

  // OPTIMIZATION #2: Cache player's last territory to detect territory changes
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

    // OPTIMIZATION #1: Early exit if player is in same chunk
    // This covers ~80% of move events and avoids all expensive checks
    Chunk lastChunk = playerLastChunk.get(playerUuid);
    if (nextChunk.equals(lastChunk)) {
      return;
    }
    playerLastChunk.put(playerUuid, nextChunk);

    // Single lookup instead of checking from chunk
    ClaimedChunk2 nextClaimedChunk = newClaimedChunkStorage.get(nextChunk);

    // Handle wilderness
    if (nextClaimedChunk == null) {
      handleWilderness(event, nextChunk, player, playerUuid);
      return;
    }

    // OPTIMIZATION #2: Cache territory info to avoid redundant checks
    String nextTerritory = nextClaimedChunk.getOwnerID();
    String lastTerritory = playerLastTerritory.get(playerUuid);

    if (nextTerritory.equals(lastTerritory)) {
      return; // Same owner, skip expensive relation checks
    }
    playerLastTerritory.put(playerUuid, nextTerritory);

    // Handle territory chunk with relation checks
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

  /**
   * Handle territory chunk access with relation checks. OPTIMIZATION #3: Use sync check first,
   * async fallback for better performance
   */
  private void handleTerritoryChunk(
      final @NotNull PlayerMoveEvent event,
      final @NotNull TerritoryChunk territoryChunk,
      final @NotNull Player player,
      final @NotNull UUID playerUuid) {

    // Try sync check first for connected players
    ITanPlayer cachedPlayer = playerDataStorage.getSync(playerUuid.toString());
    if (cachedPlayer != null) {
      checkRelationAndExecute(event, territoryChunk, cachedPlayer, player);
    } else {
      // Fallback to async for uncached players
      playerDataStorage
          .get(player)
          .thenAccept(
              tanPlayer -> {
                checkRelationAndExecute(event, territoryChunk, tanPlayer, player);
              });
    }
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

  /** Clean up player cache on quit to prevent memory leaks */
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
  }

  public static boolean sameOwner(final ClaimedChunk2 a, final ClaimedChunk2 b) {
    if (a == b) return true;
    return a.getOwnerID().equals(b.getOwnerID());
  }
}
