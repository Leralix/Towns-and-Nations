package org.leralix.tan.dataclass;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.KingdomData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.timezone.TimeZoneEnum;
import org.leralix.tan.war.info.SideStatus;
import org.leralix.tan.war.legacy.CurrentAttack;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class NoPlayerData implements ITanPlayer {

    private Integer kingdomRankId;

    @Override
    public String getID() {
        return null;
    }

    @Override
    public String getNameStored() {
        return Lang.NO_LEADER.getDefault();
    }

    @Override
    public void setNameStored(String name) {
        // singleton class, no need to set name
    }

    @Override
    public void clearName() {
        // singleton class, no need to set name
    }

    @Override
    public double getBalance() {
        return 0;
    }

    @Override
    public void setBalance(double balance) {
        // singleton class, no need to set name
    }

    @Override
    public String getTownId() {
        return "";
    }

    @Override
    public TownData getTown() {
        return null;
    }

    @Override
    public boolean hasTown() {
        return false;
    }

    @Override
    public boolean isTownOverlord() {
        return false;
    }

    @Override
    public RankData getTownRank() {
        return null;
    }

    @Override
    public RankData getRegionRank() {
        return null;
    }

    @Override
    public void addToBalance(double amount) {
        // singleton class, no need to set name
    }

    @Override
    public void removeFromBalance(double amount) {
        // singleton class, no need to set name
    }

    @Override
    public boolean hasRegion() {
        return false;
    }

    @Override
    public RegionData getRegion() {
        return null;
    }

    @Override
    public boolean hasKingdom() {
        return false;
    }

    @Override
    public KingdomData getKingdom() {
        return null;
    }

    @Override
    public RankData getKingdomRank() {
        return null;
    }

    @Override
    public Integer getKingdomRankID() {
        return kingdomRankId;
    }

    @Override
    public void setKingdomRankID(Integer rankID) {
        this.kingdomRankId = rankID;
    }

    @Override
    public UUID getUUID() {
        return null;
    }

    @Override
    public void joinTown(TownData townData) {
        // singleton class, no need to set name
    }

    @Override
    public void leaveTown() {
        // singleton class, no need to set name
    }

    @Override
    public void setTownRankID(int townRankID) {
        // singleton class, no need to set name
    }

    @Override
    public Integer getTownRankID() {
        return 0;
    }

    @Override
    public List<String> getPropertiesListID() {
        return List.of();
    }

    @Override
    public void addProperty(PropertyData propertyData) {
        // singleton class, no need to set name
    }

    @Override
    public List<PropertyData> getProperties() {
        return List.of();
    }

    @Override
    public void removeProperty(PropertyData propertyData) {
        // singleton class, no need to set name
    }

    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public List<String> getAttackInvolvedIn() {
        return List.of();
    }

    @Override
    public void updateCurrentAttack() {
        // singleton class, no need to set name
    }

    @Override
    public SideStatus getWarSideWith(TerritoryData territoryData) {
        return SideStatus.NEUTRAL;
    }

    @Override
    public void removeWar(@NotNull CurrentAttack currentAttacks) {
        // singleton class, no need to set name
    }

    @Override
    public TownRelation getRelationWithPlayer(ITanPlayer otherPlayer) {
        return TownRelation.NEUTRAL;
    }

    @Override
    public TownRelation getRelationWithPlayer(Player otherPlayer) {
        return TownRelation.NEUTRAL;
    }

    @Override
    public Integer getRegionRankID() {
        return 0;
    }

    @Override
    public void setRegionRankID(Integer rankID) {
        // singleton class, no need to set name
    }

    @Override
    public Integer getRankID(TerritoryData territoryData) {
        return 0;
    }

    @Override
    public RankData getRank(TerritoryData territoryData) {
        return territoryData.getRank(territoryData.getDefaultRankID());
    }

    @Override
    public List<TerritoryData> getAllTerritoriesPlayerIsIn() {
        return List.of();
    }

    @Override
    public OfflinePlayer getOfflinePlayer() {
        return null;
    }

    @Override
    public LangType getLang() {
        return null;
    }

    @Override
    public void setLang(LangType lang) {
        // singleton class, no need to set name
    }

    @Override
    public void clearAllTownApplications() {
        // singleton class, no need to set name
    }

    @Override
    public void setRankID(TerritoryData territoryData, Integer defaultRankID) {
        // singleton class, no need to set name
    }

    @Override
    public TimeZoneEnum getTimeZone() {
        return null;
    }

    @Override
    public void setTimeZone(TimeZoneEnum timeZone) {
        // singleton class, no need to set name
    }

    @Override
    public Set<CurrentAttack> getCurrentAttacks() {
        return Set.of();
    }

    @Override
    public String getNationID() {
        return null;
    }

    @Override
    public org.leralix.tan.dataclass.territory.NationData getNation() {
        return null;
    }

    @Override
    public boolean hasNation() {
        return false;
    }

    @Override
    public RankData getNationRank() {
        return null;
    }

    @Override
    public Integer getNationRankID() {
        return 0;
    }

    @Override
    public void setNationRankID(Integer rankID) {
    }
}
