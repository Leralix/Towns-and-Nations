package org.leralix.tan.storage.stored;

public class TributeVassalDailyStorage extends AbstractTributeDailyStorage {

    private static TributeVassalDailyStorage instance;

    public static synchronized TributeVassalDailyStorage getInstance() {
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
        instance = null;
    }
}
