package org.tan.api.interfaces.buildings;

import org.bukkit.Location;
import org.leralix.lib.position.Vector3D;
import org.tan.api.interfaces.TanPlayer;

import java.util.Optional;

public interface TanProperty extends TanBuilding {

    /**
     * @return the description of the property
     */
    String getDescription();

    /**
     * @return the first corner of the property
     */
    Vector3D getFirstCorner();

    /**
     * @return the second corner of the property
     */
    Vector3D getSecondCorner();

    /**
     * @return the owner of the property
     */
    TanOwner getOwner();

    /**
     * @return True if the property is for sale, false otherwise
     */
    boolean isForSale();

    /**
     * @return True if the property is for rent, false otherwise
     */
    boolean isForRent();

    /**
     * @return True if the property is currently rented, false otherwise
     */
    boolean isRented();

    /**
     * @return the player who is currently renting the property if it is rented,
     * {@link Optional#empty()} otherwise
     */
    Optional<TanPlayer> getRenter();

    /**
     * @return the price to rent the property.
     * Even if the property is not for rent every property has a rent price
     */
    double getRentPrice();

    /**
     * @return the price to buy the property.
     * Even if the property is not for sale every property has a sale price
     */
    double getSalePrice();

    @Override
    default boolean isLocationInside(Location location){
        var p1 = getFirstCorner().getLocation();
        var p2 =  getSecondCorner().getLocation();

        var minX = Math.min(p1.getX(), p2.getX());
        var maxX = Math.min(p1.getX(), p2.getX());
        var minY = Math.min(p1.getY(), p2.getY());
        var maxY = Math.min(p1.getY(), p2.getY());
        var minZ = Math.min(p1.getZ(), p2.getZ());
        var maxZ = Math.min(p1.getZ(), p2.getZ());


        return minX <= location.getX() && location.getX() <= maxX &&
                minY <= location.getY() && location.getY() <= maxY &&
                minZ <= location.getZ() && location.getZ() <= maxZ;
    }
}
