package org.leralix.tan.storage.stored;

import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

import java.util.Map;
import java.util.Optional;

public interface IterritoryStorage<U extends Territory> {

    Map<String, U> getAll();

    default boolean isNameUsed(String name){
        return TerritoryUtil.isNameUsed(name, getAll().values());
    }

    default Optional<U> getByName(String townName){
        for(U territory: getAll().values()){
            if(territory.getName().replace(" ", "-").equals(townName)){
                return Optional.of(territory);
            }
        }
        return Optional.empty();
    }

}
