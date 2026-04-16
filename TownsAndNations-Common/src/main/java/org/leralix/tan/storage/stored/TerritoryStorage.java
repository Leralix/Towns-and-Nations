package org.leralix.tan.storage.stored;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.storage.stored.json.JsonStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

import java.lang.reflect.Type;

public abstract class TerritoryStorage<T extends Territory> extends JsonStorage<T> {

    private int nextID;

    protected TerritoryStorage(String fileName, Type type, Gson gson) {
        super(fileName, type, gson);
        this.nextID = getNextId();
    }

    protected boolean isNameUsed(String townName) {
        return TerritoryUtil.isNameUsed(townName, getAll().values());
    }

    private int getNextId() {
        int id = 0;
        for (String keys : getAll().keySet()) {
            if (keys != null && keys.length() >= 2) {
                String suffix = keys.substring(1);
                boolean isNumeric = suffix.chars().allMatch(Character::isDigit);
                if (isNumeric) {
                    int newID = Integer.parseInt(suffix);
                    if (newID > id) {
                        id = newID + 1;
                    }
                }
            }
        }
        return id;
    }

    protected @NotNull String generateNextID(String prefix) {
        String regionID = prefix + nextID;
        nextID++;
        return regionID;
    }

}
