package org.leralix.tan.storage.stored;

import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.wargoals.Tribute;

import java.util.Set;

public interface TributeStorage {

    void registerTribute(Tribute tribute);

    void deleteTribute(Tribute tribute);

    Set<Tribute> getTributeOfMaster(Territory territory);

    Set<Tribute> getTributeOfTributary(Territory territory);

    default void deleteAllTributeOfTerritory(Territory deletedTerritory){
        getTributeOfMaster(deletedTerritory).forEach(this::deleteTribute);
    }

    void save();
}
