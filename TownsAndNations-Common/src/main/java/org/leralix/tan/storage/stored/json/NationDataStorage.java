package org.leralix.tan.storage.stored.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.territory.Nation;
import org.leralix.tan.data.territory.NationData;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.data.territory.cosmetic.ICustomIcon;
import org.leralix.tan.storage.stored.NationStorage;
import org.leralix.tan.storage.typeadapter.IconAdapter;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

import java.util.LinkedHashMap;

public class NationDataStorage extends JsonStorage<Nation> implements NationStorage {

    private int nextID;

    public NationDataStorage() {
        super("TAN - Nations.json",
                new TypeToken<LinkedHashMap<String, NationData>>() {}.getType(),
                new GsonBuilder()
                        .registerTypeAdapter(ICustomIcon.class, new IconAdapter())
                        .setPrettyPrinting()
                        .create());
        this.nextID = getNextId();
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

    @Override
    public Nation newNation(String name, @NotNull Region capital) {
        String nationID = generateNextID();
        NationData nationData = new NationData(nationID, name, capital.getLeaderData(), capital);

        put(nationID, nationData);
        capital.setOverlord(nationData);

        return nationData;
    }

    @Override
    public boolean isNameUsed(String name) {
        return TerritoryUtil.isNameUsed(name, dataMap.values());
    }

    private @NotNull String generateNextID() {
        String nationID = "N" + nextID;
        nextID++;
        return nationID;
    }

}
