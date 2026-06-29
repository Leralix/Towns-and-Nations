package org.leralix.tan.storage.stored;

import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.wargoals.Tribute;

import java.util.Optional;
import java.util.Set;

public interface TributeStorage {

    void registerTribute(Tribute tribute);

    void deleteTribute(Tribute tribute);

    Set<Tribute> getTributeOfMaster(Territory territory);

    Set<Tribute> getTributeOfTributary(Territory territory);

    default void deleteAllTributeOfTerritory(Territory deletedTerritory) {
        getTributeOfMaster(deletedTerritory).forEach(this::deleteTribute);
    }

    void save();

    default Optional<Tribute> getTribute(Territory firstTerritory, Territory secondTerritory){

        for(Tribute tribute : getTributeOfMaster(firstTerritory)) {
            if (tribute.getTributaryID().equals(secondTerritory.getID())) {
                return Optional.of(tribute);
            }
        }
        for(Tribute tribute : getTributeOfTributary(firstTerritory)) {
            if (tribute.getMasterID().equals(secondTerritory.getID())) {
                return Optional.of(tribute);
            }
        }
        return Optional.empty();
    }
}
