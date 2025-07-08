package org.leralix.tan.dataclass;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.wars.CurrentAttack;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.timezone.TimeZoneEnum;

import java.util.List;
import java.util.UUID;

public interface ITanPlayer {

    String getID();

    String getNameStored();

    void setNameStored(String name);

    void clearName();

    double getBalance();

    void setBalance(double balance);

    String getTownId();

    TownData getTown();

    boolean hasTown();

    boolean isTownOverlord();

    RankData getTownRank();

    RankData getRegionRank();

    void addToBalance(double amount);

    void removeFromBalance(double amount);

    boolean hasRegion();

    RegionData getRegion();

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

    void notifyDeath(Player killer);

    void addWar(CurrentAttack currentAttacks);

    void updateCurrentAttack();

    boolean isAtWarWith(TerritoryData territoryData);

    void removeWar(@NotNull CurrentAttack currentAttacks);

    TownRelation getRelationWithPlayer(Player playerToAdd);

    Integer getRegionRankID();

    void setRegionRankID(Integer rankID);

    Integer getRankID(TerritoryData territoryData);

    /**
     * @return  A list of all territory a player is in, starting from the lowest level.
     */
    List<TerritoryData> getAllTerritoriesPlayerIsIn();

    OfflinePlayer getOfflinePlayer();

    LangType getLang();

    void setLang(LangType lang);

    void clearAllTownApplications();

    void setRankID(TerritoryData territoryData, Integer defaultRankID);

    TimeZoneEnum getTimeZone();

    void setTimeZone(TimeZoneEnum timeZone);

}
