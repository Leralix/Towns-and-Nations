package org.leralix.tan.storage.stored;

public class TributePlayerDailyStorage extends AbstractTributeDailyStorage {

    private static TributePlayerDailyStorage instance;

    public static synchronized TributePlayerDailyStorage getInstance() {
        if (instance == null) {
            instance = new TributePlayerDailyStorage();
        }
        return instance;
    }

    private TributePlayerDailyStorage() {
        super("TAN - Tribute Player Daily.json");
    }

    @Override
    public void reset() {
        instance = null;
    }
}
