package org.leralix.tan.storage.stored;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.storage.stored.json.JsonStorage;

import java.lang.reflect.Type;

public abstract class TerritoryStorage<T extends Territory> extends JsonStorage<T> implements IterritoryStorage<T> {

    private int nextID;

    protected TerritoryStorage(String fileName, Type type, Gson gson) {
        super(fileName, type, gson);
        this.nextID = getNextID();
    }

    protected @NotNull String generateNextID(String prefix) {
        String regionID = prefix + nextID;
        nextID++;
        return regionID;
    }

}
