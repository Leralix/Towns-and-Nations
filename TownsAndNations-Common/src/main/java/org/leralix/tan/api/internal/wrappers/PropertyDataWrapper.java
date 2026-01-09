package org.leralix.tan.api.internal.wrappers;

import org.leralix.lib.position.Vector3D;
import org.leralix.tan.dataclass.PropertyData;
import org.tan.api.interfaces.TanOwner;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanProperty;

import java.util.Optional;

public class PropertyDataWrapper implements TanProperty {
    private final PropertyData propertyData;

    private PropertyDataWrapper(PropertyData propertyData) {
        this.propertyData = propertyData;
    }

    public static TanProperty of(PropertyData propertyData) {
        if (propertyData == null) {
            return null;
        }
        return new PropertyDataWrapper(propertyData);
    }

    @Override
    public String getID() {
        return propertyData.getPropertyID();
    }

    @Override
    public String getName() {
        return propertyData.getName();
    }

    @Override
    public String getDescription() {
        return propertyData.getDescription();
    }

    @Override
    public Vector3D getFirstCorner() {
        return propertyData.getFirstCorner();
    }

    @Override
    public Vector3D getSecondCorner() {
        return propertyData.getSecondCorner();
    }

    @Override
    public TanOwner getOwner() {
        return TanOwnerWrapper.of(propertyData.getOwner());
    }

    @Override
    public boolean isForSale() {
        return propertyData.isForSale();
    }

    @Override
    public boolean isForRent() {
        return propertyData.isForRent();
    }

    @Override
    public boolean isRented() {
        return propertyData.isRented();
    }

    @Override
    public Optional<TanPlayer> getRenter() {
        return Optional.ofNullable(TanPlayerWrapper.of(propertyData.getRenter()));
    }

    @Override
    public double getRentPrice() {
        return propertyData.getRentPriceWithTax();
    }

    @Override
    public double getSalePrice() {
        return propertyData.getPriceWithTax();
    }
}
