package org.leralix.tan.redis;

import com.google.gson.Gson;
import java.time.Instant;
import java.util.UUID;
import java.util.logging.Logger;
import org.leralix.tan.utils.CocoLogger;

public class RedisSyncManager {

  private static final Logger logger = Logger.getLogger(RedisSyncManager.class.getName());
  private static final Gson gson = new Gson();

  private final JedisManager jedisManager;
  private final String serverName;

  private static final String PLAYER_DATA_CHANNEL = "tan:sync:player_data";
  private static final String TERRITORY_DATA_CHANNEL = "tan:sync:territory_data";
  private static final String TRANSACTION_CHANNEL = "tan:sync:transactions";
  private static final String CACHE_INVALIDATION_CHANNEL = "tan:sync:cache_invalidation";

  public enum SyncType {
    PLAYER_BALANCE_UPDATE,
    PLAYER_JOIN_TOWN,
    PLAYER_LEAVE_TOWN,

    TERRITORY_CREATED,
    TERRITORY_DELETED,
    TERRITORY_UPDATED,

    TOWN_LEVEL_UP,
    TOWN_LEVEL_DOWN,
    TOWN_DATA_FULL_SYNC, // Synchronisation complète des données d'un town

    TOWN_RELATION_CHANGED,
    TOWN_ALLIANCE_CREATED,
    TOWN_ALLIANCE_BROKEN,

    CHUNK_CLAIMED,
    CHUNK_UNCLAIMED,
    TOWN_SPAWN_SET,

    TOWN_RANK_CREATED,
    TOWN_RANK_DELETED,
    TOWN_RANK_UPDATED,
    PLAYER_RANK_CHANGED,
    TOWN_LEADER_CHANGED,

    TOWN_TREASURY_DEPOSIT,
    TOWN_TREASURY_WITHDRAW,
    TOWN_TAX_CHANGED,
    TRANSACTION_COMPLETED,

    TOWN_SETTINGS_UPDATED,
    TOWN_NAME_CHANGED,
    TOWN_TAG_CHANGED,
    TOWN_DESCRIPTION_CHANGED,
    TOWN_ICON_CHANGED,
    TOWN_RECRUITING_TOGGLED,

    LANDMARK_CLAIMED,
    LANDMARK_UNCLAIMED,
    LANDMARK_UPGRADED,

    TOWN_UPGRADE_PURCHASED,
    TOWN_BUILDING_PLACED,
    TOWN_BUILDING_REMOVED,

    WAR_DECLARED,
    WAR_ENDED,
    WAR_CHUNK_CAPTURED,

    CACHE_INVALIDATE,
    FULL_TOWN_SYNC_REQUEST,
    FULL_TOWN_SYNC_RESPONSE
  }

  public static class SyncMessage {
    public String serverName;
    public SyncType type;
    public String data;
    public long timestamp;
    public String messageId;

    public SyncMessage(String serverName, SyncType type, String data) {
      this.serverName = serverName;
      this.type = type;
      this.data = data;
      this.timestamp = Instant.now().toEpochMilli();
      this.messageId = UUID.randomUUID().toString();
    }
  }

  public RedisSyncManager(JedisManager jedisManager, String serverName) {
    this.jedisManager = jedisManager;
    this.serverName = serverName;

    initializeTopics();
    logger.info(CocoLogger.network("⇄ Sync Redis initialisé (serveur: " + serverName + ")"));
  }

  private void initializeTopics() {
    jedisManager.subscribe(
        PLAYER_DATA_CHANNEL,
        message -> {
          SyncMessage msg = gson.fromJson(message, SyncMessage.class);
          handlePlayerDataSync(msg);
        });

    jedisManager.subscribe(
        TERRITORY_DATA_CHANNEL,
        message -> {
          SyncMessage msg = gson.fromJson(message, SyncMessage.class);
          handleTerritoryDataSync(msg);
        });

    jedisManager.subscribe(
        TRANSACTION_CHANNEL,
        message -> {
          SyncMessage msg = gson.fromJson(message, SyncMessage.class);
          handleTransactionSync(msg);
        });

    jedisManager.subscribe(
        CACHE_INVALIDATION_CHANNEL,
        message -> {
          SyncMessage msg = gson.fromJson(message, SyncMessage.class);
          handleCacheInvalidation(msg);
        });

    logger.info(CocoLogger.success("✓ Topics pub/sub Redis initialisés (4 canaux)"));
  }

  public void publishPlayerDataChange(SyncType type, String data) {
    SyncMessage message = new SyncMessage(serverName, type, data);
    jedisManager.publishJson(PLAYER_DATA_CHANNEL, message);

    logger.info(
        CocoLogger.syncLog(
            serverName, "EN_COURS", 0, String.format("SEND → %s | %s", type, truncateData(data))));
  }

  public void publishTerritoryDataChange(SyncType type, String data) {
    SyncMessage message = new SyncMessage(serverName, type, data);
    jedisManager.publishJson(TERRITORY_DATA_CHANNEL, message);

    logger.info(
        CocoLogger.syncLog(
            serverName, "EN_COURS", 0, String.format("SEND → %s | %s", type, truncateData(data))));
  }

  public void publishTransaction(SyncType type, String data) {
    SyncMessage message = new SyncMessage(serverName, type, data);
    jedisManager.publishJson(TRANSACTION_CHANNEL, message);

    logger.info(
        CocoLogger.syncLog(
            serverName, "EN_COURS", 0, String.format("SEND → %s | %s", type, truncateData(data))));
  }

  public void publishCacheInvalidation(String cacheKey) {
    SyncMessage message = new SyncMessage(serverName, SyncType.CACHE_INVALIDATE, cacheKey);
    jedisManager.publishJson(CACHE_INVALIDATION_CHANNEL, message);

    logger.info(
        CocoLogger.syncLog(serverName, "EN_COURS", 0, String.format("SEND → Cache: %s", cacheKey)));
  }

  private void handlePlayerDataSync(SyncMessage message) {
    if (message.serverName.equals(serverName)) {
      return;
    }

    long lag = System.currentTimeMillis() - message.timestamp;
    logger.info(
        CocoLogger.syncLog(
            message.serverName,
            "REUSSI",
            lag,
            String.format("RECV ← %s | %s", message.type, truncateData(message.data))));

    switch (message.type) {
      case PLAYER_BALANCE_UPDATE:
        handlePlayerBalanceUpdate(message.data);
        break;
      case PLAYER_JOIN_TOWN:
        handlePlayerJoinTown(message.data);
        break;
      case PLAYER_LEAVE_TOWN:
        handlePlayerLeaveTown(message.data);
        break;
      default:
        logger.warning(CocoLogger.warning("⚠ Type sync inconnu: " + message.type));
    }
  }

  private void handleTerritoryDataSync(SyncMessage message) {
    if (message.serverName.equals(serverName)) {
      return;
    }

    long lag = System.currentTimeMillis() - message.timestamp;
    logger.info(
        CocoLogger.syncLog(
            message.serverName,
            "REUSSI",
            lag,
            String.format("RECV ← %s | %s", message.type, truncateData(message.data))));

    org.leralix.tan.sync.TownSyncHandler handler =
        org.leralix.tan.TownsAndNations.getPlugin().getTownSyncHandler();

    switch (message.type) {
      case TERRITORY_CREATED:
        handleTerritoryCreated(message.data);
        break;
      case TERRITORY_DELETED:
        handleTerritoryDeleted(message.data);
        break;
      case TERRITORY_UPDATED:
        handleTerritoryUpdated(message.data);
        break;
      case CHUNK_CLAIMED:
        handleChunkClaimed(message.data);
        break;
      case CHUNK_UNCLAIMED:
        handleChunkUnclaimed(message.data);
        break;

      case TOWN_LEVEL_UP:
      case TOWN_LEVEL_DOWN:

      case TOWN_RELATION_CHANGED:
      case TOWN_ALLIANCE_CREATED:
      case TOWN_ALLIANCE_BROKEN:

      case TOWN_TREASURY_DEPOSIT:
      case TOWN_TREASURY_WITHDRAW:
      case TOWN_TAX_CHANGED:

      case TOWN_SPAWN_SET:

      case TOWN_RANK_CREATED:
      case TOWN_RANK_DELETED:
      case TOWN_RANK_UPDATED:
      case PLAYER_RANK_CHANGED:
      case TOWN_LEADER_CHANGED:

      case TOWN_SETTINGS_UPDATED:
      case TOWN_NAME_CHANGED:
      case TOWN_TAG_CHANGED:
      case TOWN_DESCRIPTION_CHANGED:
      case TOWN_ICON_CHANGED:
      case TOWN_RECRUITING_TOGGLED:

      case LANDMARK_CLAIMED:
      case LANDMARK_UNCLAIMED:
      case LANDMARK_UPGRADED:

      case TOWN_UPGRADE_PURCHASED:
      case TOWN_BUILDING_PLACED:
      case TOWN_BUILDING_REMOVED:

      case WAR_DECLARED:
      case WAR_ENDED:
      case WAR_CHUNK_CAPTURED:

      case FULL_TOWN_SYNC_REQUEST:
      case FULL_TOWN_SYNC_RESPONSE:
        if (handler != null) handler.handleTerritorySync(message);
        break;

      default:
        logger.warning(CocoLogger.warning("⚠ Type sync territoire inconnu: " + message.type));
    }
  }

  private void handleTransactionSync(SyncMessage message) {
    if (message.serverName.equals(serverName)) {
      return;
    }

    long lag = System.currentTimeMillis() - message.timestamp;
    logger.info(
        CocoLogger.syncLog(
            message.serverName,
            "REUSSI",
            lag,
            String.format("RECV ← TRANSACTION | %s", truncateData(message.data))));

    handleTransactionCompleted(message.data);
  }

  private void handleCacheInvalidation(SyncMessage message) {
    if (message.serverName.equals(serverName)) {
      return;
    }

    long lag = System.currentTimeMillis() - message.timestamp;
    logger.info(
        CocoLogger.syncLog(
            message.serverName,
            "REUSSI",
            lag,
            String.format("RECV ← CACHE INVALIDATION | %s", message.data)));

    QueryCacheManager.clearAllCaches();
  }

  private void handlePlayerBalanceUpdate(String data) {
    logger.fine("[TaN-Redis-Sync] Processing player balance update: " + data);

    try {
      com.google.gson.JsonObject json =
          com.google.gson.JsonParser.parseString(data).getAsJsonObject();
      String playerId = json.get("playerId").getAsString();

      QueryCacheManager.invalidatePlayerBalance(java.util.UUID.fromString(playerId));

      logger.info("[TaN-Redis-Sync] Invalidated balance cache for player: " + playerId);
    } catch (Exception e) {
      logger.warning("[TaN-Redis-Sync] Failed to process balance update: " + e.getMessage());
    }
  }

  private void handlePlayerJoinTown(String data) {
    logger.fine("[TaN-Redis-Sync] Processing player join town: " + data);

    try {
      com.google.gson.JsonObject json =
          com.google.gson.JsonParser.parseString(data).getAsJsonObject();
      String playerId = json.get("playerId").getAsString();
      String townId = json.get("townId").getAsString();

      QueryCacheManager.clearAllCaches();

      logger.info("[TaN-Redis-Sync] Player " + playerId + " joined town " + townId);
    } catch (Exception e) {
      logger.warning("[TaN-Redis-Sync] Failed to process player join: " + e.getMessage());
    }
  }

  private void handlePlayerLeaveTown(String data) {
    logger.fine("[TaN-Redis-Sync] Processing player leave town: " + data);

    try {
      com.google.gson.JsonObject json =
          com.google.gson.JsonParser.parseString(data).getAsJsonObject();
      String playerId = json.get("playerId").getAsString();
      String townId = json.get("townId").getAsString();

      QueryCacheManager.clearAllCaches();

      logger.info("[TaN-Redis-Sync] Player " + playerId + " left town " + townId);
    } catch (Exception e) {
      logger.warning("[TaN-Redis-Sync] Failed to process player leave: " + e.getMessage());
    }
  }

  private void handleTerritoryCreated(String data) {
    logger.fine("[TaN-Redis-Sync] Processing territory created: " + data);

    try {
      com.google.gson.JsonObject json =
          com.google.gson.JsonParser.parseString(data).getAsJsonObject();
      String territoryId = json.get("territoryId").getAsString();

      logger.info("[TaN-Redis-Sync] New territory created: " + territoryId);
    } catch (Exception e) {
      logger.warning("[TaN-Redis-Sync] Failed to process territory creation: " + e.getMessage());
    }
  }

  private void handleTerritoryDeleted(String data) {
    logger.fine("[TaN-Redis-Sync] Processing territory deleted: " + data);

    try {
      com.google.gson.JsonObject json =
          com.google.gson.JsonParser.parseString(data).getAsJsonObject();
      String territoryId = json.get("territoryId").getAsString();

      QueryCacheManager.invalidateTerritory(territoryId);

      logger.info("[TaN-Redis-Sync] Territory deleted: " + territoryId);
    } catch (Exception e) {
      logger.warning("[TaN-Redis-Sync] Failed to process territory deletion: " + e.getMessage());
    }
  }

  private void handleTerritoryUpdated(String data) {
    logger.fine("[TaN-Redis-Sync] Processing territory updated: " + data);

    try {
      com.google.gson.JsonObject json =
          com.google.gson.JsonParser.parseString(data).getAsJsonObject();
      String territoryId = json.get("territoryId").getAsString();

      QueryCacheManager.invalidateTerritory(territoryId);

      logger.info("[TaN-Redis-Sync] Territory updated: " + territoryId);
    } catch (Exception e) {
      logger.warning("[TaN-Redis-Sync] Failed to process territory update: " + e.getMessage());
    }
  }

  private void handleChunkClaimed(String data) {
    logger.fine("[TaN-Redis-Sync] Processing chunk claimed: " + data);

    try {
      com.google.gson.JsonObject json =
          com.google.gson.JsonParser.parseString(data).getAsJsonObject();
      String ownerId = json.get("ownerId").getAsString();

      QueryCacheManager.invalidateTerritory(ownerId);

      logger.info("[TaN-Redis-Sync] Chunk claimed by: " + ownerId);
    } catch (Exception e) {
      logger.warning("[TaN-Redis-Sync] Failed to process chunk claim: " + e.getMessage());
    }
  }

  private void handleChunkUnclaimed(String data) {
    logger.fine("[TaN-Redis-Sync] Processing chunk unclaimed: " + data);

    try {
      com.google.gson.JsonObject json =
          com.google.gson.JsonParser.parseString(data).getAsJsonObject();
      String ownerId = json.get("ownerId").getAsString();

      QueryCacheManager.invalidateTerritory(ownerId);

      logger.info("[TaN-Redis-Sync] Chunk unclaimed from: " + ownerId);
    } catch (Exception e) {
      logger.warning("[TaN-Redis-Sync] Failed to process chunk unclaim: " + e.getMessage());
    }
  }

  private void handleTransactionCompleted(String data) {
    logger.fine("[TaN-Redis-Sync] Processing transaction: " + data);

    try {
      com.google.gson.JsonObject json =
          com.google.gson.JsonParser.parseString(data).getAsJsonObject();

      String territoryId = null;
      if (json.has("townId")) {
        territoryId = json.get("townId").getAsString();
      } else if (json.has("territoryId")) {
        territoryId = json.get("territoryId").getAsString();
      }

      if (territoryId != null) {
        QueryCacheManager.invalidateTransactionHistory(territoryId);
        logger.info("[TaN-Redis-Sync] Transaction completed for territory: " + territoryId);
      } else {
        logger.warning("[TaN-Redis-Sync] Transaction data missing both townId and territoryId");
      }
    } catch (Exception e) {
      logger.warning("[TaN-Redis-Sync] Failed to process transaction: " + e.getMessage());
    }
  }

  private String truncateData(String data) {
    if (data == null) return "null";
    if (data.length() <= 100) return data;
    return data.substring(0, 97) + "...";
  }

  public void shutdown() {
    logger.info("[TaN-Redis-Sync] Shutting down Redis sync manager for server: " + serverName);

    // Les subscriptions sont gérées par JedisManager.shutdown()
    // Pas besoin de removeAllListeners ici

    logger.info("[TaN-Redis-Sync] Sync manager shutdown complete");
  }
}
