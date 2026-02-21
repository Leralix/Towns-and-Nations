package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.NationData;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.data.territory.cosmetic.ICustomIcon;
import org.leralix.tan.storage.typeadapter.IconAdapter;

import java.util.LinkedHashMap;

public class NationDataStorage extends JsonStorage<NationData> {

    private int nextID;
    private static NationDataStorage instance;

    public static NationDataStorage getInstance() {
        if (instance == null) {
            instance = new NationDataStorage();
        }
        return instance;
    }

    private NationDataStorage() {
        super("TAN - Nations.json",
                new TypeToken<LinkedHashMap<String, NationData>>() {}.getType(),
                new GsonBuilder()
                        .registerTypeAdapter(ICustomIcon.class, new IconAdapter())
                        .setPrettyPrinting()
                        .create());
    }

    public NationData get(RegionData regionData){
        if(regionData == null){
            return null;
        }
        return dataMap.get(regionData.getNationID());
    }

    public NationData get(ITanPlayer playerData){
        if(playerData == null){
            return null;
        }
        return get(playerData.getRegion());
    }

    public NationData createNewNation(String name, @NotNull RegionData capital) {
        ITanPlayer leader = capital.getLeaderData();

        String nationID = generateNextID();
        NationData nationData = new NationData(nationID, name, leader, capital);

        put(nationID, nationData);
        capital.setOverlord(nationData);

        return nationData;
    }

    public boolean isNameUsed(String name) {
        for (NationData nation : getAll().values()) {
            if (nation.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private @NotNull String generateNextID() {
        String nationID = "N" + nextID;
        nextID++;
        return nationID;
    }

    @Override
    public void load() {
        super.load();
        int id = 0;
        for (String keys : getAll().keySet()) {
            if (keys != null && keys.length() >= 2) {
                String suffix = keys.substring(1);
                boolean isNumeric = suffix.chars().allMatch(Character::isDigit);
                if (isNumeric) {
                    int newID = Integer.parseInt(suffix);
                    if (newID > id) {
                        id = newID;
                    }
                }
            }
        }
        nextID = id + 1;
    }

    public static void resetInstance() {
        instance = null;
    }

    @Override
    public void reset() {
        resetInstance();
    }
}
