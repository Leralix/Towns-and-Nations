package org.leralix.tan.data.territory;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.rank.RankData;
import org.tan.api.interfaces.territory.TanTown;

import java.util.*;

public interface Town extends Territory, TanTown {

    Collection<PropertyData> getPropertiesInternal();

    void addPlayer(ITanPlayer tanNewPlayer);

    void removePlayer(ITanPlayer tanPlayer);

    RankData getTownDefaultRank();

    boolean isFull();

    void addPlayerJoinRequest(Player player);

    void removePlayerJoinRequest(UUID playerID);

    boolean isPlayerAlreadyRequested(UUID playerUUID);

    default boolean isPlayerAlreadyRequested(Player player) {
        return isPlayerAlreadyRequested(player.getUniqueId());
    }

    Set<UUID> getPlayerJoinRequestSet();

    boolean isRecruiting();

    void swapRecruiting();

    void setCapitalLocation(Vector2D vector2D);

    Optional<Region> getRegion();

    @Nullable String getRegionID();

    Map<String, PropertyData> getPropertyDataMap();

    String nextPropertyID();

    PropertyData registerNewProperty(Vector3D p1, Vector3D p2);

    PropertyData registerNewProperty(Vector3D p1, Vector3D p2, ITanPlayer owner);

    PropertyData getProperty(String id);

    PropertyData getProperty(Location location);

    void removeProperty(PropertyData propertyData);

    String getTownTag();

    void setTownTag(String townTag);

    String getFormatedTag();

    void kickPlayer(OfflinePlayer kickedPlayer);

    void removeAllLandmark();

    void removeAllProperty();

    boolean isTownCapitalOccupied();
}
