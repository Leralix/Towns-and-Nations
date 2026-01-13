package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.NationData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.storage.typeadapter.IconAdapter;
import org.leralix.tan.dataclass.territory.cosmetic.ICustomIcon;

import java.util.LinkedHashMap;

public class NationDataStorage extends JsonStorage<NationData> {

    private static NationDataStorage instance;
    private int nextID;

    private NationDataStorage() {
        super("TAN - Nations.json",
                new TypeToken<LinkedHashMap<String, NationData>>() {}.getType(),
                new GsonBuilder()
                        .registerTypeAdapter(ICustomIcon.class, new IconAdapter())
                        .setPrettyPrinting()
                        .create());
    }

    public static NationDataStorage getInstance() {
        if (instance == null) {
            instance = new NationDataStorage();
        }
        return instance;
    }

    @Override
    public void load() {
        super.load();
        int id = 0;
        for (String keys : getAll().keySet()) {
            try {
                int newID = Integer.parseInt(keys.substring(1));
                if (newID > id) {
                    id = newID;
                }
            } catch (NumberFormatException ignored) {
                // Ignore malformed nation IDs when computing nextID.
            }
        }
        nextID = id + 1;
    }

    @Override
    public void reset() {
        resetInstance();
    }

    private static synchronized void resetInstance() {
        instance = null;
    }

    public NationData createNewNation(String name, RegionData capital) {
        if (capital == null) {
            throw new IllegalArgumentException("Capital region cannot be null");
        }
        ITanPlayer newLeader = capital.getLeaderData();
        if (newLeader == null) {
            throw new IllegalArgumentException("Capital region must have a leader");
        }
        String nationID = generateNextID();

        NationData newNation = new NationData(nationID, name, newLeader);
        put(nationID, newNation);

        capital.setOverlord(newNation);

        return newNation;
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
}
