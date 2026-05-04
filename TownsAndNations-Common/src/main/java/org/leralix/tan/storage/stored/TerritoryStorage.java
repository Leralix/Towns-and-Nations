package org.leralix.tan.storage.stored;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.storage.stored.json.JsonStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

import java.lang.reflect.Type;

public abstract class TerritoryStorage<T extends Territory> extends JsonStorage<T> implements IterritoryStorage {

    private int nextID;

    protected TerritoryStorage(String fileName, Type type, Gson gson) {
        super(fileName, type, gson);
        System.out.println("TerritoryStorage");
        this.nextID = getNextID();
    }

    public boolean isNameUsed(String name) {
        return TerritoryUtil.isNameUsed(name, getAll().values());
    }

    protected @NotNull String generateNextID(String prefix) {
        String regionID = prefix + nextID;
        nextID++;
        return regionID;
    }

}
