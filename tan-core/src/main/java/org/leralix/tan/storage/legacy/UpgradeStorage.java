package org.leralix.tan.storage.legacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.leralix.tan.upgrade.Upgrade;

public class UpgradeStorage {

  private static final HashMap<String, Upgrade> UpgradeMap = new HashMap<>();

  public static Upgrade getUpgrade(String name) {
    return null;
  }

  public static List<Upgrade> getUpgrades() {
    return new ArrayList<>();
  }
}
