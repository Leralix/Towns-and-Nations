package org.leralix.tan.api.wrappers;

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

    private final ITanPlayer ITanPlayer;

    private TanPlayerWrapper(ITanPlayer ITanPlayer){
        this.ITanPlayer = ITanPlayer;
    }

    public static TanPlayer of(ITanPlayer ITanPlayer) {
        if(ITanPlayer == null){
            return null;
        }
        return new TanPlayerWrapper(ITanPlayer);
    }

    @Override
    public String getNameStored() {
        return ITanPlayer.getNameStored();
    }

    @Override
    public void setNameStored(String s) {
        ITanPlayer.setNameStored(s);
    }

    @Override
    public UUID getUUID() {
        return ITanPlayer.getUUID();
    }

    @Override
    public boolean hasTown() {
        return ITanPlayer.hasTown();
    }

    @Override
    public Optional<TanTown> getTown() {
        return Optional.ofNullable(TownDataWrapper.of(ITanPlayer.getTown()));
    }

    @Override
    public boolean hasRegion() {
        return ITanPlayer.hasRegion();
    }

    @Override
    public TanRegion getRegion() {
        return RegionDataWrapper.of(ITanPlayer.getRegion());
    }

    @Override
    public Collection<TanProperty> getPropertiesOwned() {
        return ITanPlayer.getProperties().stream()
                .map(PropertyDataWrapper::of)
                .map(p -> (TanProperty) p)
                .toList();
    }

    @Override
    public Collection<TanProperty> getPropertiesRented() {
        List<TanProperty> properties = new ArrayList<>();

        for(TownData town : TownDataStorage.getInstance().getAll()){
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
        List<TanProperty> properties = new ArrayList<>();
        for(TownData town : TownDataStorage.getInstance().getAll()){
            for(PropertyData property : town.getProperties()){
                ITanPlayer owner = property.getOwner();
                if(getUUID().equals(owner.getUUID()) && property.isForSale()){
                    properties.add(PropertyDataWrapper.of(property));
                }
            }
        }
        return properties;
    }
}
