package org.leralix.tan.dataclass;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.wars.CurrentAttack;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.timezone.TimeZoneEnum;

import java.util.List;
import java.util.UUID;

public class NoPlayerData implements ITanPlayer {
    @Override
    public String getID() {
        return null;
    }

    @Override
    public String getNameStored() {
        return Lang.NO_LEADER.get();
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
    public void notifyDeath(Player killer) {
        // singleton class, no need to set name
    }

    @Override
    public void addWar(CurrentAttack currentAttacks) {
        // singleton class, no need to set name
    }

    @Override
    public void updateCurrentAttack() {
        // singleton class, no need to set name
    }

    @Override
    public boolean isAtWarWith(TerritoryData territoryData) {
        return false;
    }

    @Override
    public void removeWar(@NotNull CurrentAttack currentAttacks) {
        // singleton class, no need to set name
    }

    @Override
    public TownRelation getRelationWithPlayer(Player playerToAdd) {
        return null;
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
}
