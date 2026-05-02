package org.leralix.tan.data.territory;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.data.DbManager;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.rank.RankData;
import org.leralix.tan.storage.stored.database.DatabaseData;
import org.tan.api.interfaces.buildings.TanLandmark;

import java.util.*;

public class TownDataBase extends TerritoryDatabase<TownData> implements DatabaseData<TownData>, Town {

    private final DbManager<TownData> manager;

    private TownData data;

    public TownDataBase(TownData data, DbManager<TownData> manager){
        super(manager, data);
        this.data = data;
        this.manager = manager;
    }

    @Override
    public void setData(TownData data) {
        setTerritoryData(data);
        this.data = data;
    }

    @Override
    public Collection<PropertyData> getPropertiesInternal() {
        return data.getPropertiesInternal();
    }

    @Override
    public void addPlayer(ITanPlayer tanNewPlayer) {
        mutate(p -> p.addPlayer(tanNewPlayer));
    }

    @Override
    public void removePlayer(ITanPlayer tanPlayer) {
        mutate(p -> p.removePlayer(tanPlayer));
    }

    @Override
    public RankData getTownDefaultRank() {
        return data.getTownDefaultRank();
    }

    @Override
    public boolean isFull() {
        return data.isFull();
    }

    @Override
    public void addPlayerJoinRequest(Player player) {
        mutate(p -> p.addPlayerJoinRequest(player));
    }

    @Override
    public void removePlayerJoinRequest(UUID playerID) {
        mutate(p -> p.removePlayerJoinRequest(playerID));
    }

    @Override
    public boolean isPlayerAlreadyRequested(UUID playerUUID) {
        return data.isPlayerAlreadyRequested(playerUUID);
    }

    @Override
    public Set<UUID> getPlayerJoinRequestSet() {
        return data.getPlayerJoinRequestSet();
    }

    @Override
    public boolean isRecruiting() {
        return data.isRecruiting();
    }

    @Override
    public void swapRecruiting() {
        mutate(Town::swapRecruiting);
    }

    @Override
    public void setCapitalLocation(Vector2D vector2D) {
        mutate(p -> p.setCapitalLocation(vector2D));
    }

    @Override
    public Optional<Region> getRegion() {
        return data.getRegion();
    }

    @Override
    public @Nullable String getRegionID() {
        return data.getRegionID();
    }

    @Override
    public Map<String, PropertyData> getPropertyDataMap() {
        return data.getPropertyDataMap();
    }

    @Override
    public String nextPropertyID() {
        return data.nextPropertyID();
    }

    @Override
    public PropertyData registerNewProperty(Vector3D p1, Vector3D p2) {
        PropertyData result = data.registerNewProperty(p1, p2);
        manager.save(data);
        return result;
    }

    @Override
    public PropertyData registerNewProperty(Vector3D p1, Vector3D p2, ITanPlayer owner) {
        PropertyData result = data.registerNewProperty(p1, p2, owner);
        manager.save(data);
        return result;
    }

    @Override
    public PropertyData getProperty(String id) {
        return data.getProperty(id);
    }

    @Override
    public PropertyData getProperty(Location location) {
        return data.getProperty(location);
    }

    @Override
    public void removeProperty(PropertyData propertyData) {
        mutate(p -> p.removeProperty(propertyData));
    }

    @Override
    public String getTownTag() {
        return data.getTownTag();
    }

    @Override
    public void setTownTag(String townTag) {
        mutate(p -> p.setTownTag(townTag));
    }

    @Override
    public String getFormatedTag() {
        return data.getFormatedTag();
    }

    @Override
    public void kickPlayer(OfflinePlayer kickedPlayer) {
        mutate(p -> p.kickPlayer(kickedPlayer));
    }

    @Override
    public void removeAllLandmark() {
        mutate(Town::removeAllLandmark);
    }

    @Override
    public void removeAllProperty() {
        mutate(Town::removeAllProperty);
    }

    @Override
    public boolean isTownCapitalOccupied() {
        return data.isTownCapitalOccupied();
    }

    @Override
    public Collection<TanLandmark> getLandmarksOwned() {
        return data.getLandmarksOwned();
    }

    @Override
    public Optional<Vector2D> getCapitalLocation() {
        return data.getCapitalLocation();
    }

    @Override
    public List<Territory> getSubjects() {
        return data.getSubjects();
    }

    @Override
    public void registerPlayer(ITanPlayer tanPlayer) {
        mutate(p -> p.registerPlayer(tanPlayer));
    }

    @Override
    public void unregisterPlayer(ITanPlayer tanPlayer) {
        mutate(p -> p.unregisterPlayer(tanPlayer));
    }

    @Override
    public void removeVassal(Territory territoryData) {
        mutate(p -> p.removeVassal(territoryData));
    }
}
