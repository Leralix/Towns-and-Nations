package org.leralix.tan.api.internal.wrappers;

import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanProperty;
import org.tan.api.interfaces.TanRegion;
import org.tan.api.interfaces.TanTown;

import java.util.*;

public class TanPlayerWrapper implements TanPlayer {

    private final ITanPlayer tanPlayer;

    private TanPlayerWrapper(ITanPlayer tanPlayer){
        this.tanPlayer = tanPlayer;
    }

    public static TanPlayer of(ITanPlayer tanPlayer) {
        if(tanPlayer == null){
            return null;
        }
        return new TanPlayerWrapper(tanPlayer);
    }

    @Override
    public String getNameStored() {
        return tanPlayer.getNameStored();
    }

    @Override
    public void setNameStored(String s) {
        tanPlayer.setNameStored(s);
    }

    @Override
    public UUID getUUID() {
        return tanPlayer.getUUID();
    }

    @Override
    public boolean hasTown() {
        return tanPlayer.hasTown();
    }

    @Override
    public Optional<TanTown> getTown() {
        return Optional.ofNullable(TownDataWrapper.of(tanPlayer.getTownSync()));
    }

    @Override
    public boolean hasRegion() {
        return tanPlayer.hasRegion();
    }

    @Override
    public TanRegion getRegion() {
        return RegionDataWrapper.of(tanPlayer.getRegionSync());
    }

    @Override
    public Collection<TanProperty> getPropertiesOwned() {
        return tanPlayer.getProperties().stream()
                .map(PropertyDataWrapper::of)
                .map(p -> (TanProperty) p)
                .toList();
    }

    @Override
    public Collection<TanProperty> getPropertiesRented() {
        List<TanProperty> properties = new ArrayList<>();

        for(TownData town : TownDataStorage.getInstance().getAllSync().values()){
            for(PropertyData property : town.getProperties()){
                ITanPlayer renter = property.getRenter();
                if(renter == null){
                    continue;
                }
                if(getUUID().equals(renter.getUUID())){
                    properties.add(PropertyDataWrapper.of(property));
                }
            }
        }
        return properties;
    }

    @Override
    public Collection<TanProperty> getPropertiesForSale() {
        return null; //TODO : update TAN-API
    }
}
