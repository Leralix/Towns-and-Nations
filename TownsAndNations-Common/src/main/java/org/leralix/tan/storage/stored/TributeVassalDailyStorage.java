package org.leralix.tan.storage.stored;

public class TributeVassalDailyStorage extends AbstractTributeDailyStorage {

    private static TributeVassalDailyStorage instance;

    public static TributeVassalDailyStorage getInstance() {
        if (instance == null) {
            instance = new TributeVassalDailyStorage();
        }
        return instance;
    }

    private TributeVassalDailyStorage() {
        super("TAN - Tribute Vassal Daily.json");
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
