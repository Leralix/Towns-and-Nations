package org.leralix.tan.storage.stored;

public class TributePlayerDailyStorage extends AbstractTributeDailyStorage {

    private static TributePlayerDailyStorage instance;

    public static TributePlayerDailyStorage getInstance() {
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
        // Singleton reset: clear the static instance reference so a fresh storage can be recreated when needed.
        resetInstance();
    }

    private static synchronized void resetInstance() {
        instance = null;
    }
}
