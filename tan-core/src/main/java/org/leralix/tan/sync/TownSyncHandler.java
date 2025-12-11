package org.leralix.tan.sync;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.redis.QueryCacheManager;
import org.leralix.tan.redis.RedisSyncManager;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.CocoLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TownSyncHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(TownSyncHandler.class);
  private static final Gson GSON = new Gson();

  private final String serverName;

  public TownSyncHandler(String serverName) {
    this.serverName = serverName;
    LOGGER.info(CocoLogger.success("✓ TownSyncHandler initialisé pour serveur: " + serverName));
  }

  public void handleTerritorySync(RedisSyncManager.SyncMessage msg) {
    if (msg.serverName.equals(serverName)) {
      return;
    }

    try {
      JsonObject data = JsonParser.parseString(msg.data).getAsJsonObject();
      long lag = System.currentTimeMillis() - msg.timestamp;

      LOGGER.info(
          CocoLogger.network(
              String.format("⬅ RECV [%s] from %s (lag=%dms)", msg.type, msg.serverName, lag)));

      switch (msg.type) {
        case TOWN_LEVEL_UP:
        case TOWN_LEVEL_DOWN:
          handleLevelChange(data);
          break;

        case TOWN_DATA_FULL_SYNC:
          handleFullDataSync(data);
          break;

        case TOWN_RELATION_CHANGED:
          handleRelationChange(data);
          break;
        case TOWN_ALLIANCE_CREATED:
        case TOWN_ALLIANCE_BROKEN:
          handleAllianceChange(data);
          break;

        case CHUNK_CLAIMED:
        case CHUNK_UNCLAIMED:
          handleChunkChange(data);
          break;
        case TOWN_SPAWN_SET:
          handleSpawnChange(data);
          break;

        case TOWN_RANK_CREATED:
        case TOWN_RANK_DELETED:
        case TOWN_RANK_UPDATED:
          handleRankChange(data);
          break;
        case PLAYER_RANK_CHANGED:
          handlePlayerRankChange(data);
          break;
        case TOWN_LEADER_CHANGED:
          handleLeaderChange(data);
          break;

        case TOWN_TREASURY_DEPOSIT:
        case TOWN_TREASURY_WITHDRAW:
          handleTreasuryChange(data);
          break;
        case TOWN_TAX_CHANGED:
          handleTaxChange(data);
          break;

        case TOWN_SETTINGS_UPDATED:
        case TOWN_NAME_CHANGED:
        case TOWN_TAG_CHANGED:
        case TOWN_DESCRIPTION_CHANGED:
        case TOWN_ICON_CHANGED:
        case TOWN_RECRUITING_TOGGLED:
          handleSettingsChange(data);
          break;

        case LANDMARK_CLAIMED:
        case LANDMARK_UNCLAIMED:
        case LANDMARK_UPGRADED:
          handleLandmarkChange(data);
          break;

        case TOWN_UPGRADE_PURCHASED:
        case TOWN_BUILDING_PLACED:
        case TOWN_BUILDING_REMOVED:
          handleBuildingChange(data);
          break;

        case WAR_DECLARED:
        case WAR_ENDED:
        case WAR_CHUNK_CAPTURED:
          handleWarChange(data);
          break;

        case FULL_TOWN_SYNC_REQUEST:
          handleFullSyncRequest(data);
          break;
        case FULL_TOWN_SYNC_RESPONSE:
          handleFullSyncResponse(data);
          break;

        default:
          LOGGER.warn(CocoLogger.warning("Type sync inconnu: " + msg.type));
      }
    } catch (Exception ex) {
      LOGGER.error(
          CocoLogger.error("Erreur traitement sync " + msg.type + ": " + ex.getMessage()), ex);
    }
  }

  private void handleLevelChange(JsonObject data) {
    String townId = data.get("townId").getAsString();
    int oldLevel = data.get("oldLevel").getAsInt();
    int newLevel = data.get("newLevel").getAsInt();

    LOGGER.info(
        CocoLogger.network(
            String.format(
                "↕ Applying level change: Town %s: %d → %d", townId, oldLevel, newLevel)));

    QueryCacheManager.invalidateTerritory(townId);
  }

  /**
   * Gère la synchronisation complète des données d'un town
   * Recharge les données depuis la base de données après invalidation du cache
   */
  private void handleFullDataSync(JsonObject data) {
    String townId = data.get("townId").getAsString();
    int townLevel = data.get("townLevel").getAsInt();

    LOGGER.info(
        CocoLogger.network(
            String.format("⟳ Applying full data sync: Town %s (level %d)", townId, townLevel)));

    // Invalide le cache local et Redis pour forcer un reload depuis la DB
    QueryCacheManager.invalidateTerritory(townId);
    
    // Le prochain accès aux données rechargera automatiquement depuis la DB
    LOGGER.debug(CocoLogger.success("✓ Cache invalidated for town " + townId));
  }

  private void handleRelationChange(JsonObject data) {
    String townId1 = data.get("townId1").getAsString();
    String townId2 = data.get("townId2").getAsString();
    String newRelation = data.get("newRelation").getAsString();

    LOGGER.info(
        CocoLogger.network(
            String.format(
                "⚖ Applying relation change: %s ↔ %s: %s", townId1, townId2, newRelation)));

    QueryCacheManager.invalidateTerritory(townId1);
    QueryCacheManager.invalidateTerritory(townId2);
  }

  private void handleAllianceChange(JsonObject data) {
    String townId1 = data.get("townId1").getAsString();
    String townId2 = data.get("townId2").getAsString();

    LOGGER.info(
        CocoLogger.network(
            String.format("🤝 Applying alliance change: %s ↔ %s", townId1, townId2)));

    QueryCacheManager.invalidateTerritory(townId1);
    QueryCacheManager.invalidateTerritory(townId2);
  }

  private void handleChunkChange(JsonObject data) {
    String townId = data.get("townId").getAsString();
    String chunkId = data.get("chunkId").getAsString();

    LOGGER.info(
        CocoLogger.network(
            String.format("🗺 Applying chunk change: Town %s, chunk %s", townId, chunkId)));

    QueryCacheManager.invalidateTerritory(townId);
  }

  private void handleSpawnChange(JsonObject data) {
    String townId = data.get("townId").getAsString();
    double x = data.get("x").getAsDouble();
    double y = data.get("y").getAsDouble();
    double z = data.get("z").getAsDouble();

    LOGGER.info(
        CocoLogger.network(
            String.format("📍 Applying spawn change: Town %s → %.1f,%.1f,%.1f", townId, x, y, z)));

    QueryCacheManager.invalidateTerritory(townId);
  }

  private void handleRankChange(JsonObject data) {
    String townId = data.get("townId").getAsString();

    LOGGER.info(CocoLogger.network(String.format("👑 Applying rank change: Town %s", townId)));

    QueryCacheManager.invalidateTerritory(townId);
  }

  private void handlePlayerRankChange(JsonObject data) {
    String townId = data.get("townId").getAsString();
    String playerId = data.get("playerId").getAsString();
    int newRankId = data.get("newRankId").getAsInt();

    LOGGER.info(
        CocoLogger.network(
            String.format(
                "👤 Applying player rank change: Player %s in town %s → rank %d",
                playerId, townId, newRankId)));

    QueryCacheManager.invalidateTerritory(townId);
  }

  private void handleLeaderChange(JsonObject data) {
    String townId = data.get("townId").getAsString();
    String newLeaderId = data.get("newLeaderId").getAsString();

    LOGGER.info(
        CocoLogger.network(
            String.format("👑 Applying leader change: Town %s → %s", townId, newLeaderId)));

    QueryCacheManager.invalidateTerritory(townId);
  }

  private void handleTreasuryChange(JsonObject data) {
    String townId = data.get("townId").getAsString();
    double amount = data.get("amount").getAsDouble();

    LOGGER.info(
        CocoLogger.network(
            String.format("💰 Applying treasury change: Town %s, amount %.2f", townId, amount)));

    QueryCacheManager.invalidateTerritory(townId);
  }

  private void handleTaxChange(JsonObject data) {
    String townId = data.get("townId").getAsString();
    double newTaxRate = data.get("newTaxRate").getAsDouble();

    LOGGER.info(
        CocoLogger.network(
            String.format("💵 Applying tax change: Town %s → %.2f%%", townId, newTaxRate)));

    QueryCacheManager.invalidateTerritory(townId);
  }

  private void handleSettingsChange(JsonObject data) {
    String townId = data.get("townId").getAsString();

    LOGGER.info(CocoLogger.network(String.format("⚙ Applying settings change: Town %s", townId)));

    QueryCacheManager.invalidateTerritory(townId);
  }

  private void handleLandmarkChange(JsonObject data) {
    String townId = data.get("townId").getAsString();
    String landmarkId = data.get("landmarkId").getAsString();

    LOGGER.info(
        CocoLogger.network(
            String.format(
                "🏛 Applying landmark change: Town %s, landmark %s", townId, landmarkId)));

    QueryCacheManager.invalidateTerritory(townId);
  }

  private void handleBuildingChange(JsonObject data) {
    String townId = data.get("townId").getAsString();

    LOGGER.info(CocoLogger.network(String.format("🔧 Applying building change: Town %s", townId)));

    QueryCacheManager.invalidateTerritory(townId);
  }

  private void handleWarChange(JsonObject data) {
    if (data.has("attackerTownId")) {
      String attackerTownId = data.get("attackerTownId").getAsString();
      String defenderTownId = data.get("defenderTownId").getAsString();

      LOGGER.info(
          CocoLogger.network(
              String.format("⚔ Applying war change: %s ↔ %s", attackerTownId, defenderTownId)));

      QueryCacheManager.invalidateTerritory(attackerTownId);
      QueryCacheManager.invalidateTerritory(defenderTownId);
    } else if (data.has("warId")) {
      String warId = data.get("warId").getAsString();

      LOGGER.info(CocoLogger.network(String.format("⚔ Applying war change: %s", warId)));

      QueryCacheManager.clearAllCaches();
    }
  }

  private void handleFullSyncRequest(JsonObject data) {
    String townId = data.get("townId").getAsString();
    String requestingServer = data.get("requestingServer").getAsString();

    if (requestingServer.equals(serverName)) {
      return;
    }

    LOGGER.info(
        CocoLogger.network(
            String.format(
                "🔄 Received full sync request for town %s from %s", townId, requestingServer)));

    TownSyncService syncService = getTownSyncService();
    if (syncService != null) {
      syncService.sendFullTownSync(townId);
    }
  }

  private void handleFullSyncResponse(JsonObject data) {
    String townId = data.get("townId").getAsString();
    String townDataJson = data.get("townData").getAsString();

    LOGGER.info(
        CocoLogger.network(
            String.format(
                "🔄 Received full sync response for town %s (%d bytes)",
                townId, townDataJson.length())));

    try {
      TownData townData = GSON.fromJson(townDataJson, TownData.class);

      TownDataStorage.getInstance().putSync(townId, townData);

      LOGGER.info(CocoLogger.success(String.format("✓ Full sync applied for town %s", townId)));
    } catch (Exception ex) {
      LOGGER.error(
          CocoLogger.error(
              "Échec application full sync pour town " + townId + ": " + ex.getMessage()),
          ex);
    }
  }

  private TownSyncService getTownSyncService() {
    return TownsAndNations.getPlugin().getTownSyncService();
  }
}
