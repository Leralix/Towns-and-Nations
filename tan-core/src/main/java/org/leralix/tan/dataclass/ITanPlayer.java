package org.leralix.tan.dataclass;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.timezone.TimeZoneEnum;
import org.leralix.tan.wars.legacy.CurrentAttack;

public interface ITanPlayer {

  String getID();

  void setUuid(String uuid);

  String getNameStored();

  void setNameStored(String name);

  void clearName();

  double getBalance();

  void setBalance(double balance);

  String getTownId();

  String getTownName();

  CompletableFuture<TownData> getTown();

  default TownData getTownSync() {
    try {
      return getTown().join();
    } catch (Exception e) {
      return null;
    }
  }

  boolean hasTown();

  boolean isTownOverlord();

  RankData getTownRank();

  RankData getRegionRank();

  void addToBalance(double amount);

  void removeFromBalance(double amount);

  boolean hasRegion();

  CompletableFuture<RegionData> getRegion();

  default RegionData getRegionSync() {
    try {
      return getRegion().join();
    } catch (Exception e) {
      return null;
    }
  }

  String getNationName();

  UUID getUUID();

  void joinTown(TownData townData);

  void leaveTown();

  void setTownRankID(int townRankID);

  Integer getTownRankID();

  List<String> getPropertiesListID();

  void addProperty(PropertyData propertyData);

  List<PropertyData> getProperties();

  void removeProperty(PropertyData propertyData);

  Player getPlayer();

  List<String> getAttackInvolvedIn();

  void addWar(CurrentAttack currentAttacks);

  void updateCurrentAttack();

  boolean isAtWarWith(TerritoryData territoryData);

  void removeWar(@NotNull CurrentAttack currentAttacks);

  CompletableFuture<TownRelation> getRelationWithPlayer(ITanPlayer otherPlayer);

  CompletableFuture<TownRelation> getRelationWithPlayer(Player otherPlayer);

  TownRelation getRelationWithPlayerSync(ITanPlayer otherPlayer);

  Integer getRegionRankID();

  void setRegionRankID(Integer rankID);

  Integer getRankID(TerritoryData territoryData);

  RankData getRank(TerritoryData territoryData);

  /**
   * @return A list of all territory a player is in, starting from the lowest level.
   */
  CompletableFuture<List<TerritoryData>> getAllTerritoriesPlayerIsIn();

  default List<TerritoryData> getAllTerritoriesPlayerIsInSync() {
    try {
      return getAllTerritoriesPlayerIsIn().join();
    } catch (Exception e) {
      return null;
    }
  }

  OfflinePlayer getOfflinePlayer();

  LangType getLang();

  void setLang(LangType lang);

  void clearAllTownApplications();

  void setRankID(TerritoryData territoryData, Integer defaultRankID);

  TimeZoneEnum getTimeZone();

  void setTimeZone(TimeZoneEnum timeZone);

  CompletableFuture<List<CurrentAttack>> getCurrentAttacks();
}
