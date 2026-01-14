package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;

import java.util.LinkedHashMap;

public class TributeVassalDailyStorage extends JsonStorage<Double> {

    private static TributeVassalDailyStorage instance;

    public static TributeVassalDailyStorage getInstance() {
        if (instance == null) {
            instance = new TributeVassalDailyStorage();
        }
        return instance;
    }

    private TributeVassalDailyStorage() {
        super(
                "TAN - Tribute Vassal Daily.json",
                new TypeToken<LinkedHashMap<String, Double>>() {}.getType(),
                new GsonBuilder().setPrettyPrinting().create()
        );
    }

    public synchronized double getAmount(String vassalTerritoryId) {
        Double val = dataMap.get(vassalTerritoryId);
        return val == null ? 0.0 : val;
    }

    public synchronized void addAmount(String vassalTerritoryId, double amount) {
        dataMap.put(vassalTerritoryId, getAmount(vassalTerritoryId) + amount);
        save();
    }

    public synchronized void resetDaily() {
        dataMap.clear();
        save();
    }

    @Override
    public void reset() {
        instance = null;
    }
}
