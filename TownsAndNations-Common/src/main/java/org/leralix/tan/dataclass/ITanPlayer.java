package org.leralix.tan.dataclass;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.timezone.TimeZoneEnum;
import org.leralix.tan.war.info.SideStatus;
import org.leralix.tan.war.legacy.CurrentAttack;

import java.util.List;
import java.util.Set;
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

    void updateCurrentAttack();

    /**
     * Check all wars between the player's territories and the selected territory.
     * @param territoryData The territory to check
     * @return The worst role the player has with the territory
     */
    SideStatus getWarSideWith(TerritoryData territoryData);

    void removeWar(@NotNull CurrentAttack currentAttacks);

    TownRelation getRelationWithPlayer(ITanPlayer otherPlayer);

    TownRelation getRelationWithPlayer(Player otherPlayer);

    Integer getRegionRankID();

    void setRegionRankID(Integer rankID);

    Integer getRankID(TerritoryData territoryData);

    RankData getRank(TerritoryData territoryData);

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

    Set<CurrentAttack> getCurrentAttacks();
}
