package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.KingdomData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.cosmetic.ICustomIcon;
import org.leralix.tan.storage.typeadapter.IconAdapter;

import java.util.LinkedHashMap;

public class KingdomDataStorage extends JsonStorage<KingdomData> {

    private int nextID;
    private static KingdomDataStorage instance;

    public static KingdomDataStorage getInstance() {
        if (instance == null) {
            instance = new KingdomDataStorage();
        }
        return instance;
    }

    private KingdomDataStorage() {
        super("TAN - Kingdoms.json",
                new TypeToken<LinkedHashMap<String, KingdomData>>() {}.getType(),
                new GsonBuilder()
                        .registerTypeAdapter(ICustomIcon.class, new IconAdapter())
                        .setPrettyPrinting()
                        .create());
    }

    public KingdomData createNewKingdom(String name, RegionData capital) {
        ITanPlayer leader = capital == null ? null : capital.getLeaderData();

        String kingdomID = generateNextID();
        KingdomData kingdomData = new KingdomData(kingdomID, name, leader, capital);

        put(kingdomID, kingdomData);

        if (capital != null) {
            capital.setOverlord(kingdomData);
        }

        return kingdomData;
    }

    public boolean isNameUsed(String name) {
        for (KingdomData kingdom : getAll().values()) {
            if (kingdom.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private @NotNull String generateNextID() {
        String kingdomID = "K" + nextID;
        nextID++;
        return kingdomID;
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
            }
        }
        nextID = id + 1;
    }

    @Override
    public void reset() {
        instance = null;
    }
}
