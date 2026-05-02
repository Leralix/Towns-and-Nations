package org.leralix.tan.storage.stored;

import org.jetbrains.annotations.Nullable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.tan.api.interfaces.buildings.TanProperty;

import java.util.ArrayList;
import java.util.Map;

public interface TownStorage {

    Town newTown(String townName, @Nullable ITanPlayer tanPlayer);

    default Town newTown(String townName){
        return newTown(townName, null);
    }

    void deleteTown(Town townData);

    default Town get(ITanPlayer tanPlayer){
        return get(tanPlayer.getTownId());
    }

    Town get(String townId);

    Map<String, Town> getAll();

    default int getNumberOfTown() {
        return getAll().size();
    }

    default boolean isNameUsed(String townName){
        return TerritoryUtil.isNameUsed(townName, getAll().values());
    }

    default void checkValidWorlds(){
        for (Town town : new ArrayList<>(getAll().values())) {
            for (TanProperty property : town.getProperties()) {
                if (property.getPosition() == null || property.getPosition().getWorld() == null) {
                    property.delete();
                    TownsAndNations.getPlugin().getLogger().warning("Deleted property " + property.getName() + " due to invalid world.");
                }
            }
            var optCapital = town.getCapitalLocation();
            if (optCapital.isPresent() && optCapital.get().getWorld() == null) {
                town.setCapitalLocation(null);
                TownsAndNations.getPlugin().getLogger().warning("Removed capital location for town " + town.getName() + " due to invalid world.");
            }
        }
    }

    void save();

    default Town getByName(String townName){
        for(Town town: getAll().values()){
            if(town.getName().replace(" ", "-").equals(townName)){
                return town;
            }
        }
        return null;
    }
}
