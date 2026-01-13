package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;

import java.util.LinkedHashMap;

abstract class AbstractTributeDailyStorage extends JsonStorage<Double> {

    protected AbstractTributeDailyStorage(String fileName) {
        super(
                fileName,
                new TypeToken<LinkedHashMap<String, Double>>() {}.getType(),
                new GsonBuilder().setPrettyPrinting().create()
        );
    }

    public synchronized double getAmount(String id) {
        Double val = dataMap.get(id);
        return val == null ? 0.0 : val;
    }

    public synchronized void addAmount(String id, double amount) {
        dataMap.put(id, getAmount(id) + amount);
        save();
    }

    public synchronized void resetDaily() {
        dataMap.clear();
        save();
    }
}
