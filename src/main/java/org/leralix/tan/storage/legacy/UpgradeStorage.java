package org.leralix.tan.storage.legacy;

import org.leralix.tan.upgrade.Upgrade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UpgradeStorage {

    private static final HashMap<String, Upgrade> UpgradeMap = new HashMap<>();

    public static Upgrade getUpgrade(String name) {
        return null;
    }

    public static List<Upgrade> getUpgrades() {
        return new ArrayList<>();
    }

}
