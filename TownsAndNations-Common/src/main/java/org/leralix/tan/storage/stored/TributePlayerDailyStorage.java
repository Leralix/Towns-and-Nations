package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;

import java.util.LinkedHashMap;

public class TributePlayerDailyStorage extends JsonStorage<Double> {

    private static TributePlayerDailyStorage instance;

    public static TributePlayerDailyStorage getInstance() {
        if (instance == null) {
            instance = new TributePlayerDailyStorage();
        }
        return instance;
    }

    private TributePlayerDailyStorage() {
        super(
                "TAN - Tribute Player Daily.json",
                new TypeToken<LinkedHashMap<String, Double>>() {}.getType(),
                new GsonBuilder().setPrettyPrinting().create()
        );
    }

    public synchronized double getAmount(String playerId) {
        Double val = dataMap.get(playerId);
        return val == null ? 0.0 : val;
    }

    public synchronized void addAmount(String playerId, double amount) {
        dataMap.put(playerId, getAmount(playerId) + amount);
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
