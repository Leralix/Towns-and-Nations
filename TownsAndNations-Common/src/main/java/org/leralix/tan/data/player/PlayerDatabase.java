package org.leralix.tan.data.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.DbManager;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.data.territory.*;
import org.leralix.tan.data.territory.rank.RankData;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.data.timezone.TimeZoneEnum;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.database.DatabaseData;
import org.leralix.tan.war.attack.CurrentAttack;
import org.leralix.tan.war.info.SideStatus;
import org.tan.api.interfaces.buildings.TanProperty;
import org.tan.api.interfaces.war.TanWar;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerDatabase implements DatabaseData<ITanPlayer>, ITanPlayer {

    private final DbManager<ITanPlayer> manager;

    private ITanPlayer data;

    public PlayerDatabase(ITanPlayer data, DbManager<ITanPlayer> manager) {
        this.data = data;
        this.manager = manager;
    }

    @Override
    public UUID getID() {
        return data.getID();
    }

    @Override
    public String getNameStored() {
        return data.getNameStored();
    }

    @Override
    public void setNameStored(String name) {
        mutate(p -> p.setNameStored(name));
    }

    @Override
    public void clearName() {
        mutate(ITanPlayer::clearName);
    }

    @Override
    public double getBalance() {
        return data.getBalance();
    }

    @Override
    public void setBalance(double balance) {
        mutate(p -> p.setBalance(balance));
    }

    @Override
    public String getTownId() {
        return data.getTownId();
    }

    @Override
    public Town getTown() {
        return data.getTown();
    }

    @Override
    public boolean hasTown() {
        return data.hasTown();
    }

    @Override
    public boolean isTownOverlord() {
        return data.isTownOverlord();
    }

    @Override
    public RankData getTownRank() {
        return data.getTownRank();
    }

    @Override
    public RankData getRegionRank() {
        return data.getRegionRank();
    }

    @Override
    public void addToBalance(double amount) {
        mutate(p -> p.addToBalance(amount));
    }

    @Override
    public void removeFromBalance(double amount) {
        mutate(p -> p.removeFromBalance(amount));
    }

    @Override
    public boolean hasRegion() {
        return data.hasRegion();
    }

    @Override
    public Region getRegion() {
        return data.getRegion();
    }

    @Override
    public void joinTown(TownData townData) {
        mutate(p -> p.joinTown(townData));
    }

    @Override
    public void leaveTown() {
        mutate(ITanPlayer::leaveTown);
    }

    @Override
    public void setTownRankID(int townRankID) {
        mutate(p -> p.setTownRankID(townRankID));
    }

    @Override
    public Integer getTownRankID() {
        return data.getTownRankID();
    }

    @Override
    public List<String> getPropertiesListID() {
        return data.getPropertiesListID();
    }

    @Override
    public void addProperty(PropertyData propertyData) {
        mutate(p -> p.addProperty(propertyData));
    }

    @Override
    public List<PropertyData> getProperties() {
        return data.getProperties();
    }

    @Override
    public void removeProperty(PropertyData propertyData) {
        mutate(p -> p.removeProperty(propertyData));
    }

    @Override
    public Player getPlayer() {
        return data.getPlayer();
    }

    @Override
    public List<String> getAttackInvolvedIn() {
        return data.getAttackInvolvedIn();
    }

    @Override
    public void updateCurrentAttack() {
        data.updateCurrentAttack();
    }

    @Override
    public SideStatus getWarSideWith(Territory territoryData) {
        return data.getWarSideWith(territoryData);
    }

    @Override
    public void removeWar(@NotNull CurrentAttack currentAttacks) {
        mutate(p -> p.removeWar(currentAttacks));
    }

    @Override
    public TownRelation getRelationWithPlayer(ITanPlayer otherPlayer) {
        return data.getRelationWithPlayer(otherPlayer);
    }

    @Override
    public Integer getRegionRankID() {
        return data.getRegionRankID();
    }

    @Override
    public void setRegionRankID(Integer rankID) {
        mutate(p -> p.setRegionRankID(rankID));
    }

    @Override
    public Nation getNation() {
        return data.getNation();
    }

    @Override
    public boolean hasNation() {
        return data.hasNation();
    }

    @Override
    public Collection<TanProperty> getPropertiesOwned() {
        return data.getPropertiesOwned();
    }

    @Override
    public Collection<TanProperty> getPropertiesRented() {
        return data.getPropertiesRented();
    }

    @Override
    public Collection<TanProperty> getPropertiesForSale() {
        return data.getPropertiesForSale();
    }

    @Override
    public Collection<TanWar> getWarsParticipatingIn() {
        return data.getWarsParticipatingIn();
    }

    @Override
    public RankData getNationRank() {
        return data.getNationRank();
    }

    @Override
    public Integer getNationRankID() {
        return data.getNationRankID();
    }

    @Override
    public void setNationRankID(Integer rankID) {
        mutate(p -> p.setNationRankID(rankID));
    }

    @Override
    public Integer getRankID(Territory territoryData) {
        return data.getRankID(territoryData);
    }

    @Override
    public RankData getRank(Territory territoryData) {
        return data.getRank(territoryData);
    }

    @Override
    public List<Territory> getAllTerritoriesPlayerIsIn() {
        return data.getAllTerritoriesPlayerIsIn();
    }

    @Override
    public OfflinePlayer getOfflinePlayer() {
        return data.getOfflinePlayer();
    }

    @Override
    public @NotNull LangType getLang() {
        return data.getLang();
    }

    @Override
    public void setLang(LangType lang) {
        mutate(p -> p.setLang(lang));
    }

    @Override
    public void clearAllTownApplications() {
        mutate(ITanPlayer::clearAllTownApplications);
    }

    @Override
    public void setRankID(TerritoryData territoryData, Integer defaultRankID) {
        mutate(p -> p.setRankID(territoryData, defaultRankID));
    }

    @Override
    public @NotNull TimeZoneEnum getTimeZone() {
        return data.getTimeZone();
    }

    @Override
    public void setTimeZone(TimeZoneEnum timeZone) {
        mutate(p -> p.setTimeZone(timeZone));
    }

    @Override
    public Set<CurrentAttack> getCurrentAttacks() {
        return data.getCurrentAttacks();
    }

    @Override
    public Player getOnlinePlayer() {
        return data.getOnlinePlayer();
    }

    @Override
    public void setData(ITanPlayer fresh) {
        this.data = fresh;
    }

    private synchronized void mutate(Consumer<ITanPlayer> action) {
        action.accept(data);
        manager.save(data);
    }
}
