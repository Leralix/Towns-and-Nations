package org.leralix.tan.storage.stored.truce;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.storage.stored.json.JsonStorage;

import java.time.Instant;
import java.util.HashMap;

public class TruceStorage extends JsonStorage<HashMap<String, ActiveTruce>> {

    private static TruceStorage instance;

    protected TruceStorage() {
        super("TAN - Truce.json",
                new TypeToken<HashMap<String, HashMap<String, ActiveTruce>>>() {}.getType(),
                new GsonBuilder().setPrettyPrinting().create());
    }

    public static TruceStorage getInstance() {
        if (instance == null)
            instance = new TruceStorage();
        return instance;
    }

    public void add(ActiveTruce activeTruce) {
        String id1 = activeTruce.getTerritoryID1();
        String id2 = activeTruce.getTerritoryID2();

        dataMap.computeIfAbsent(id1, k -> new HashMap<>()).put(id2, activeTruce);
        dataMap.computeIfAbsent(id2, k -> new HashMap<>()).put(id1, activeTruce);
    }


    public long getRemainingTruce(Territory territoryData1, Territory territoryData2) {
        String id1 = territoryData1.getID();
        String id2 = territoryData2.getID();


        ActiveTruce truce = dataMap.getOrDefault(id1, new HashMap<>()).get(id2);
        if (truce == null) {
            return 0;
        }

        long remaining = truce.getEndOfTruce() - Instant.now().toEpochMilli();
        long remainingInHours = remaining / 1000 / 60 / 60;

        return Math.max(0, remainingInHours);
    }

}
