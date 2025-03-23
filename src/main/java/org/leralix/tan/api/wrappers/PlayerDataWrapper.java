package org.leralix.tan.api.wrappers;

import org.leralix.tan.dataclass.PlayerData;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanProperty;
import org.tan.api.interfaces.TanRegion;
import org.tan.api.interfaces.TanTown;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class PlayerDataWrapper implements TanPlayer {

    private final PlayerData playerData;

    private PlayerDataWrapper(PlayerData playerData){
        this.playerData = playerData;
    }

    public static TanPlayer of(PlayerData playerData) {
        if(playerData == null){
            return null;
        }
        return new PlayerDataWrapper(playerData);
    }

    @Override
    public String getNameStored() {
        return null;
    }

    @Override
    public void setNameStored(String s) {

    }

    @Override
    public UUID getUUID() {
        return null;
    }

    @Override
    public boolean hasTown() {
        return false;
    }

    @Override
    public Optional<TanTown> getTown() {
        return Optional.empty();
    }

    @Override
    public boolean hasRegion() {
        return false;
    }

    @Override
    public TanRegion getRegion() {
        return null;
    }

    @Override
    public Collection<TanProperty> getPropertiesOwned() {
        return null;
    }

    @Override
    public Collection<TanProperty> getPropertiesRented() {
        return null;
    }

    @Override
    public Collection<TanProperty> getPropertiesForSale() {
        return null;
    }
}
