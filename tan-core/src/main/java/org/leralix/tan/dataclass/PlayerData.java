package org.leralix.tan.dataclass;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.invitation.TownInviteDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.timezone.TimeZoneEnum;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.wars.legacy.CurrentAttack;

public class PlayerData implements ITanPlayer {

  private String uuid;
  private String storedName;
  private Double Balance;
  private String TownId;
  private Integer townRankID;
  private Integer regionRankID;
  private List<String> propertiesListID;
  private List<String> attackInvolvedIn;
  private LangType lang;
  private TimeZoneEnum timeZone;

  public PlayerData(Player player) {
    this.uuid = player.getUniqueId().toString();
    this.storedName = player.getName();
    this.Balance = Constants.getStartingBalance();
    this.TownId = null;
    this.townRankID = null;
    this.regionRankID = null;
    this.propertiesListID = new ArrayList<>();
    this.attackInvolvedIn = new ArrayList<>();
  }

  public String getID() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getNameStored() {
    if (storedName == null) {
      OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(java.util.UUID.fromString(uuid));
      storedName = offlinePlayer.getName();
      if (storedName == null) {
        storedName = "Unknown name";
      }
    }
    return storedName;
  }

  public void setNameStored(String name) {
    this.storedName = name;
  }

  public void clearName() {
    this.storedName = null;
  }

  public double getBalance() {
    return this.Balance;
  }

  public void setBalance(double balance) {
    this.Balance = balance;
  }

  public String getTownId() {
    return this.TownId;
  }

  @Override
  public String getTownName() {
    if (hasTown()) {
      // Bloquant ici, mais getName() est une opération rapide sur l'objet TownData
      // Idéalement, getTownName() devrait aussi retourner CompletableFuture<String>
      return getTown().join().getName();
    }
    return null;
  }

  public CompletableFuture<TownData> getTown() {
    if (this.TownId == null) {
      return CompletableFuture.completedFuture(null);
    }
    return TownDataStorage.getInstance().get(this.TownId);
  }

  public boolean hasTown() {
    return this.TownId != null;
  }

  public boolean isTownOverlord() {
    if (!hasTown()) return false;
    // Bloquant ici, mais isLeader() est une opération rapide sur l'objet TownData
    return getTown().join().isLeader(this.uuid);
  }

  public RankData getTownRank() {
    if (!hasTown()) return null;
    // Bloquant ici, mais getRank() est une opération rapide sur l'objet TownData
    return getTown().join().getRank(getTownRankID());
  }

  public RankData getRegionRank() {
    if (!hasRegion()) return null;
    // Bloquant ici, mais getRank() est une opération rapide sur l'objet RegionData
    return getRegion().join().getRank(getRegionRankID());
  }

  public void addToBalance(double amount) {
    this.Balance = this.Balance + amount;
  }

  public void removeFromBalance(double amount) {
    this.Balance = this.Balance - amount;
  }

  public boolean hasRegion() {
    if (!this.hasTown()) {
      return false;
    }
    // Bloquant ici, mais haveOverlord() est une opération rapide sur l'objet TownData
    return getTown().join().haveOverlord();
  }

  public CompletableFuture<RegionData> getRegion() {
    if (!hasRegion()) return CompletableFuture.completedFuture(null);
    return getTown()
        .thenApply(
            town -> {
              if (town == null) return null;
              Optional<TerritoryData> overlord = town.getOverlord();
              return overlord.map(territoryData -> (RegionData) territoryData).orElse(null);
            });
  }

  @Override
  public String getNationName() {
    if (hasRegion()) {
      // Bloquant ici, mais getName() est une opération rapide sur l'objet RegionData
      // Idéalement, getNationName() devrait aussi retourner CompletableFuture<String>
      return getRegion().join().getName();
    }
    return null;
  }

  public UUID getUUID() {
    return java.util.UUID.fromString(uuid);
  }

  public void joinTown(TownData townData) {
    this.TownId = townData.getID();
    setTownRankID(townData.getDefaultRankID());
  }

  public void leaveTown() {
    this.TownId = null;
    this.townRankID = null;
  }

  public void setTownRankID(int townRankID) {
    this.townRankID = townRankID;
  }

  public Integer getTownRankID() {
    return this.townRankID;
  }

  public List<String> getPropertiesListID() {
    if (this.propertiesListID == null) this.propertiesListID = new ArrayList<>();
    return propertiesListID;
  }

  public void addProperty(PropertyData propertyData) {
    getPropertiesListID().add(propertyData.getTotalID());
  }

  public List<PropertyData> getProperties() {
    List<PropertyData> propertyDataList = new ArrayList<>();

    for (String propertyID : getPropertiesListID()) {
      String[] parts = propertyID.split("_");
      String tID = parts[0];
      String pID = parts[1];

      // Bloquant ici, mais nécessaire pour construire la liste de PropertyData
      PropertyData nextProperty = TownDataStorage.getInstance().getSync(tID).getProperty(pID);

      propertyDataList.add(nextProperty);
    }

    return propertyDataList;
  }

  public void removeProperty(PropertyData propertyData) {
    this.propertiesListID.remove(propertyData.getTotalID());
  }

  public Player getPlayer() {
    return Bukkit.getPlayer(getUUID());
  }

  public List<String> getAttackInvolvedIn() {
    if (attackInvolvedIn == null) attackInvolvedIn = new ArrayList<>();
    return attackInvolvedIn;
  }

  public void addWar(CurrentAttack currentAttacks) {
    if (getAttackInvolvedIn().contains(currentAttacks.getAttackData().getID())) {
      return;
    }
    getAttackInvolvedIn().add(currentAttacks.getAttackData().getID());
  }

  public void updateCurrentAttack() {
    Iterator<String> iterator = getAttackInvolvedIn().iterator();
    while (iterator.hasNext()) {
      String attackID = iterator.next();
      CurrentAttack currentAttack = CurrentAttacksStorage.get(attackID);
      if (currentAttack == null || !currentAttack.containsPlayer(this)) {
        iterator.remove();
      } else {
        currentAttack.addPlayer(this);
      }
    }
  }

  public boolean isAtWarWith(TerritoryData territoryData) {
    if (territoryData == null) {
      return false;
    }
    for (String attackID : getAttackInvolvedIn()) {
      CurrentAttack currentAttack = CurrentAttacksStorage.get(attackID);
      if (currentAttack == null) {
        getAttackInvolvedIn().remove(attackID);
        continue;
      }
      if (currentAttack.getAttackData().getDefendingTerritories().contains(territoryData)) {
        return true;
      }
    }
    return false;
  }

  public void removeWar(@NotNull CurrentAttack currentAttacks) {
    getAttackInvolvedIn().remove(currentAttacks.getAttackData().getID());
  }

  @Override
  public CompletableFuture<TownRelation> getRelationWithPlayer(Player otherPlayer) {
    ITanPlayer otherPlayerData = PlayerDataStorage.getInstance().getSync(otherPlayer);
    return getRelationWithPlayer(otherPlayerData);
  }

  public CompletableFuture<TownRelation> getRelationWithPlayer(ITanPlayer otherPlayer) {
    if (!hasTown() || !otherPlayer.hasTown())
      return CompletableFuture.completedFuture(TownRelation.NEUTRAL);

    return getTown()
        .thenCombine(
            otherPlayer.getTown(),
            (playerTown, otherPlayerTown) -> {
              if (playerTown == null || otherPlayerTown == null) {
                return TownRelation.NEUTRAL;
              }
              return playerTown.getRelationWith(otherPlayerTown);
            });
  }

  public TownRelation getRelationWithPlayerSync(ITanPlayer otherPlayer) {
    if (!hasTown() || !otherPlayer.hasTown()) return TownRelation.NEUTRAL;

    TownData playerTown = getTownSync();
    TownData otherPlayerTown = otherPlayer.getTownSync();

    if (playerTown == null || otherPlayerTown == null) {
      return TownRelation.NEUTRAL;
    }
    return playerTown.getRelationWith(otherPlayerTown);
  }

  public Integer getRegionRankID() {
    if (!hasRegion()) {
      return null;
    }
    if (regionRankID == null)
      // Bloquant ici, mais getDefaultRankID() est une opération rapide sur l'objet RegionData
      regionRankID = getRegion().join().getDefaultRankID();
    return regionRankID;
  }

  public void setRegionRankID(Integer rankID) {
    this.regionRankID = rankID;
  }

  public Integer getRankID(TerritoryData territoryData) {
    if (territoryData instanceof TownData) {
      return getTownRankID();
    } else if (territoryData instanceof RegionData) {
      return getRegionRankID();
    }
    return null;
  }

  @Override
  public RankData getRank(TerritoryData territoryData) {
    return territoryData.getRank(getRankID(territoryData));
  }

  public CompletableFuture<List<TerritoryData>> getAllTerritoriesPlayerIsIn() {
    CompletableFuture<TownData> townFuture = getTown();
    CompletableFuture<RegionData> regionFuture = getRegion();

    return CompletableFuture.allOf(townFuture, regionFuture)
        .thenApply(
            v -> {
              List<TerritoryData> territories = new ArrayList<>();
              TownData town = townFuture.join();
              RegionData region = regionFuture.join();

              if (town != null) {
                territories.add(town);
              }
              if (region != null) {
                territories.add(region);
              }
              return territories;
            });
  }

  public OfflinePlayer getOfflinePlayer() {
    return Bukkit.getServer().getOfflinePlayer(getUUID());
  }

  public LangType getLang() {
    if (lang == null) return Lang.getServerLang();
    return lang;
  }

  public void setLang(LangType lang) {
    this.lang = lang;
  }

  public void clearAllTownApplications() {
    TownInviteDataStorage.removeInvitation(uuid); // Remove town invitation
    for (TownData allTown : TownDataStorage.getInstance().getAllSync().values()) {
      allTown.removePlayerJoinRequest(uuid); // Remove applications
    }
  }

  public void setRankID(TerritoryData territoryData, Integer defaultRankID) {
    if (territoryData instanceof TownData) {
      setTownRankID(defaultRankID);
    }
    if (territoryData instanceof RegionData) {
      setRegionRankID(defaultRankID);
    }
  }

  public TimeZoneEnum getTimeZone() {
    if (timeZone == null) {
      return TimeZoneManager.getInstance().getTimezoneEnum();
    }
    return timeZone;
  }

  public void setTimeZone(TimeZoneEnum timeZone) {
    this.timeZone = timeZone;
  }

  @Override
  public CompletableFuture<List<CurrentAttack>> getCurrentAttacks() {
    return getAllTerritoriesPlayerIsIn()
        .thenApply(
            territories -> {
              List<CurrentAttack> res = new ArrayList<>();
              for (TerritoryData territoryData : territories) {
                res.addAll(territoryData.getCurrentAttacks());
              }
              return res;
            });
  }
}
