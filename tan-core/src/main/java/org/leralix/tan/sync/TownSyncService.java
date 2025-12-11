package org.leralix.tan.sync;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.redis.RedisSyncManager;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.CocoLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TownSyncService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TownSyncService.class);
  private static final Gson GSON = new Gson();

  private final RedisSyncManager syncManager;
  private final String serverName;

  public TownSyncService(RedisSyncManager syncManager, String serverName) {
    this.syncManager = syncManager;
    this.serverName = serverName;
    LOGGER.info(CocoLogger.success("✓ TownSyncService initialisé pour serveur: " + serverName));
  }

  /**
   * Synchronise les données complètes d'un town sur tous les serveurs
   * Utilisé après un changement important (level up, upgrade, etc.)
   */
  public void publishFullTownDataSync(TownData townData) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townData.getID());
      payload.addProperty("townLevel", townData.getNewLevel().getMainLevel());
      payload.addProperty("timestamp", System.currentTimeMillis());
      // Ajouter d'autres données importantes si nécessaire

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_DATA_FULL_SYNC, payload.toString());

      LOGGER.debug(
          CocoLogger.network(
              String.format("⟳ FULL SYNC: Town %s (level %d)", townData.getID(), townData.getNewLevel().getMainLevel())));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec full sync town data: " + ex.getMessage()), ex);
    }
  }

  public void publishTownLevelUp(String townId, int oldLevel, int newLevel) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("oldLevel", oldLevel);
      payload.addProperty("newLevel", newLevel);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_LEVEL_UP, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format("↑ SYNC LEVEL: Town %s: %d → %d", townId, oldLevel, newLevel)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync level up: " + ex.getMessage()), ex);
    }
  }

  public void publishTownLevelDown(String townId, int oldLevel, int newLevel) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("oldLevel", oldLevel);
      payload.addProperty("newLevel", newLevel);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_LEVEL_DOWN, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format("↓ SYNC LEVEL: Town %s: %d → %d", townId, oldLevel, newLevel)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync level down: " + ex.getMessage()), ex);
    }
  }

  public void publishRelationChange(
      String townId1, String townId2, String oldRelation, String newRelation) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId1", townId1);
      payload.addProperty("townId2", townId2);
      payload.addProperty("oldRelation", oldRelation);
      payload.addProperty("newRelation", newRelation);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_RELATION_CHANGED, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format(
                  "⚖ SYNC DIPLO: %s ↔ %s: %s → %s", townId1, townId2, oldRelation, newRelation)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync relation: " + ex.getMessage()), ex);
    }
  }

  public void publishAllianceCreated(String townId1, String townId2) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId1", townId1);
      payload.addProperty("townId2", townId2);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_ALLIANCE_CREATED, payload.toString());

      LOGGER.info(
          CocoLogger.network(String.format("🤝 SYNC ALLIANCE: %s ↔ %s CRÉÉE", townId1, townId2)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync alliance: " + ex.getMessage()), ex);
    }
  }

  public void publishAllianceBroken(String townId1, String townId2) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId1", townId1);
      payload.addProperty("townId2", townId2);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_ALLIANCE_BROKEN, payload.toString());

      LOGGER.info(
          CocoLogger.network(String.format("💔 SYNC ALLIANCE: %s ↔ %s ROMPUE", townId1, townId2)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync rupture alliance: " + ex.getMessage()), ex);
    }
  }

  public void publishChunkClaimed(String townId, String chunkId) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("chunkId", chunkId);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.CHUNK_CLAIMED, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format("🗺 SYNC CLAIM: Town %s claimed chunk %s", townId, chunkId)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync chunk claim: " + ex.getMessage()), ex);
    }
  }

  public void publishChunkUnclaimed(String townId, String chunkId) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("chunkId", chunkId);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.CHUNK_UNCLAIMED, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format("🗺 SYNC UNCLAIM: Town %s unclaimed chunk %s", townId, chunkId)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync chunk unclaim: " + ex.getMessage()), ex);
    }
  }

  public void publishTownSpawnSet(String townId, double x, double y, double z, String world) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("x", x);
      payload.addProperty("y", y);
      payload.addProperty("z", z);
      payload.addProperty("world", world);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_SPAWN_SET, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format("📍 SYNC SPAWN: Town %s spawn → %.1f,%.1f,%.1f", townId, x, y, z)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync spawn: " + ex.getMessage()), ex);
    }
  }

  public void publishRankCreated(String townId, int rankId, String rankName) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("rankId", rankId);
      payload.addProperty("rankName", rankName);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_RANK_CREATED, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format(
                  "👑 SYNC RANK: Town %s créé rank '%s' (ID:%d)", townId, rankName, rankId)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync rank created: " + ex.getMessage()), ex);
    }
  }

  public void publishRankDeleted(String townId, int rankId) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("rankId", rankId);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_RANK_DELETED, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format("👑 SYNC RANK: Town %s supprimé rank ID:%d", townId, rankId)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync rank deleted: " + ex.getMessage()), ex);
    }
  }

  public void publishRankUpdated(String townId, int rankId) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("rankId", rankId);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_RANK_UPDATED, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format("👑 SYNC RANK: Town %s modifié rank ID:%d", townId, rankId)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync rank updated: " + ex.getMessage()), ex);
    }
  }

  public void publishPlayerRankChanged(
      String townId, String playerId, int oldRankId, int newRankId) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("playerId", playerId);
      payload.addProperty("oldRankId", oldRankId);
      payload.addProperty("newRankId", newRankId);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishPlayerDataChange(
          RedisSyncManager.SyncType.PLAYER_RANK_CHANGED, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format(
                  "👤 SYNC RANK: Joueur %s dans town %s: rank %d → %d",
                  playerId, townId, oldRankId, newRankId)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync player rank: " + ex.getMessage()), ex);
    }
  }

  public void publishLeaderChanged(String townId, String oldLeaderId, String newLeaderId) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("oldLeaderId", oldLeaderId);
      payload.addProperty("newLeaderId", newLeaderId);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_LEADER_CHANGED, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format("👑 SYNC LEADER: Town %s: %s → %s", townId, oldLeaderId, newLeaderId)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync leader: " + ex.getMessage()), ex);
    }
  }

  public void publishTreasuryDeposit(String townId, double amount, String playerId) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("amount", amount);
      if (playerId != null) {
        payload.addProperty("playerId", playerId);
      }
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_TREASURY_DEPOSIT, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format(
                  "💰 SYNC TREASURY: Town %s +%.2f (par %s)",
                  townId, amount, playerId != null ? playerId : "système")));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync treasury deposit: " + ex.getMessage()), ex);
    }
  }

  public void publishTreasuryWithdraw(String townId, double amount, String playerId) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("amount", amount);
      if (playerId != null) {
        payload.addProperty("playerId", playerId);
      }
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_TREASURY_WITHDRAW, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format(
                  "💰 SYNC TREASURY: Town %s -%.2f (par %s)",
                  townId, amount, playerId != null ? playerId : "système")));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync treasury withdraw: " + ex.getMessage()), ex);
    }
  }

  public void publishTaxChanged(String townId, double newTaxRate) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("newTaxRate", newTaxRate);

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_TAX_CHANGED, payload.toString());

      LOGGER.info(
          CocoLogger.network(String.format("💵 SYNC TAX: Town %s → %.2f%%", townId, newTaxRate)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync tax: " + ex.getMessage()), ex);
    }
  }

  public void publishSettingsUpdated(String townId) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_SETTINGS_UPDATED, payload.toString());

      LOGGER.info(
          CocoLogger.network(String.format("⚙ SYNC SETTINGS: Town %s settings modifiés", townId)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync settings: " + ex.getMessage()), ex);
    }
  }

  public void publishNameChanged(String townId, String oldName, String newName) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("oldName", oldName);
      payload.addProperty("newName", newName);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_NAME_CHANGED, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format("📝 SYNC NAME: Town %s: '%s' → '%s'", townId, oldName, newName)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync name: " + ex.getMessage()), ex);
    }
  }

  public void publishTagChanged(String townId, String oldTag, String newTag) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("oldTag", oldTag);
      payload.addProperty("newTag", newTag);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_TAG_CHANGED, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format("🏷 SYNC TAG: Town %s: [%s] → [%s]", townId, oldTag, newTag)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync tag: " + ex.getMessage()), ex);
    }
  }

  public void publishDescriptionChanged(String townId) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_DESCRIPTION_CHANGED, payload.toString());

      LOGGER.info(
          CocoLogger.network(String.format("📄 SYNC DESC: Town %s description modifiée", townId)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync description: " + ex.getMessage()), ex);
    }
  }

  public void publishRecruitingToggled(String townId, boolean isRecruiting) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("isRecruiting", isRecruiting);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_RECRUITING_TOGGLED, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format(
                  "📢 SYNC RECRUITING: Town %s: %s", townId, isRecruiting ? "OUVERT" : "FERMÉ")));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync recruiting: " + ex.getMessage()), ex);
    }
  }

  public void publishLandmarkClaimed(String townId, String landmarkId) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("landmarkId", landmarkId);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.LANDMARK_CLAIMED, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format("🏛 SYNC LANDMARK: Town %s claimed landmark %s", townId, landmarkId)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync landmark claim: " + ex.getMessage()), ex);
    }
  }

  public void publishLandmarkUnclaimed(String townId, String landmarkId) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("landmarkId", landmarkId);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.LANDMARK_UNCLAIMED, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format(
                  "🏛 SYNC LANDMARK: Town %s unclaimed landmark %s", townId, landmarkId)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync landmark unclaim: " + ex.getMessage()), ex);
    }
  }

  public void publishUpgradePurchased(String townId, String upgradeId) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("upgradeId", upgradeId);

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.TOWN_UPGRADE_PURCHASED, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format("🔧 SYNC UPGRADE: Town %s purchased upgrade %s", townId, upgradeId)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync upgrade: " + ex.getMessage()), ex);
    }
  }

  public void publishWarDeclared(String attackerTownId, String defenderTownId, String warId) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("attackerTownId", attackerTownId);
      payload.addProperty("defenderTownId", defenderTownId);
      payload.addProperty("warId", warId);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.WAR_DECLARED, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format(
                  "⚔ SYNC WAR: %s déclare guerre à %s (ID:%s)",
                  attackerTownId, defenderTownId, warId)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync war declared: " + ex.getMessage()), ex);
    }
  }

  public void publishWarEnded(String warId, String winnerId) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("warId", warId);
      payload.addProperty("winnerId", winnerId);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.WAR_ENDED, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format("⚔ SYNC WAR: Guerre %s terminée, gagnant: %s", warId, winnerId)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec sync war ended: " + ex.getMessage()), ex);
    }
  }

  public void requestFullTownSync(String townId) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("requestingServer", serverName);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.FULL_TOWN_SYNC_REQUEST, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format("🔄 SYNC REQUEST: Demande sync complète town %s", townId)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec request full sync: " + ex.getMessage()), ex);
    }
  }

  public void sendFullTownSync(String townId) {
    try {
      TownData town = TownDataStorage.getInstance().get(townId).join();
      if (town == null) {
        LOGGER.warn(CocoLogger.warning("Town " + townId + " non trouvée pour full sync"));
        return;
      }

      String townJson = GSON.toJson(town);

      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("townData", townJson);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishTerritoryDataChange(
          RedisSyncManager.SyncType.FULL_TOWN_SYNC_RESPONSE, payload.toString());

      LOGGER.info(
          CocoLogger.network(
              String.format(
                  "🔄 SYNC RESPONSE: Envoi sync complète town %s (%d bytes)",
                  townId, townJson.length())));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec send full sync: " + ex.getMessage()), ex);
    }
  }

  public void invalidateTownCache(String townId) {
    try {
      JsonObject payload = new JsonObject();
      payload.addProperty("townId", townId);
      payload.addProperty("timestamp", System.currentTimeMillis());

      syncManager.publishCacheInvalidation("town:" + townId);

      LOGGER.info(
          CocoLogger.network(String.format("🗑 SYNC CACHE: Invalidation cache town %s", townId)));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec invalidate cache: " + ex.getMessage()), ex);
    }
  }
}
