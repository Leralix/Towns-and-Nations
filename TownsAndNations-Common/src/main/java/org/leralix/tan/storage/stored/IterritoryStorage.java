package org.leralix.tan.storage.stored;

import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

import java.util.Map;

public interface IterritoryStorage<U extends Territory> {

    Map<String, U> getAll();

    default boolean isNameUsed(String name){
        return TerritoryUtil.isNameUsed(name, getAll().values());
    }

}
