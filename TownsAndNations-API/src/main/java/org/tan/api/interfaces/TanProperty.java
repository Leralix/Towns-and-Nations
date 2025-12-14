package org.tan.api.interfaces;

import org.leralix.lib.position.Vector3D;

import java.util.Optional;

public interface TanProperty {

    /**
     * @return the ID of the property
     */
    String getID();

    /**
     * @return the name of the property
     */
    String getName();

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
}
