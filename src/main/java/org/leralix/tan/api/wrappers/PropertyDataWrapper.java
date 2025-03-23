package org.leralix.tan.api.wrappers;

import org.leralix.lib.position.Vector3D;
import org.leralix.tan.dataclass.PropertyData;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanProperty;

import java.util.Optional;

public class PropertyDataWrapper implements TanProperty {
    private final PropertyData propertyData;

    private PropertyDataWrapper(PropertyData propertyData){
        this.propertyData = propertyData;
    }

    public static PropertyDataWrapper of(PropertyData propertyData) {
        if(propertyData == null){
            return null;
        }
        return new PropertyDataWrapper(propertyData);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Vector3D getFirstCorner() {
        return null;
    }

    @Override
    public Vector3D getSecondCorner() {
        return null;
    }

    @Override
    public TanPlayer getOwner() {
        return null;
    }

    @Override
    public boolean isForSale() {
        return false;
    }

    @Override
    public boolean isForRent() {
        return false;
    }

    @Override
    public boolean isRented() {
        return false;
    }

    @Override
    public Optional<TanPlayer> getRenter() {
        return Optional.empty();
    }

    @Override
    public double getRentPrice() {
        return 0;
    }

    @Override
    public double getSalePrice() {
        return 0;
    }
}
