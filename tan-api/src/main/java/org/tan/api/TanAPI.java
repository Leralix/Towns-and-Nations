package org.tan.api;

import org.leralix.lib.data.PluginVersion;
import org.tan.api.getters.TanClaimManager;
import org.tan.api.getters.TanEventManager;
import org.tan.api.getters.TanFortManager;
import org.tan.api.getters.TanLandmarkManager;
import org.tan.api.getters.TanPlayerManager;
import org.tan.api.getters.TanTerritoryManager;

public abstract class TanAPI {
  private static TanAPI instance;

  protected TanAPI() {}

  public static void register(TanAPI api) {
    instance = api;
  }

  public static TanAPI getInstance() {
    return instance;
  }

  public abstract TanPlayerManager getPlayerManager();

  public abstract TanTerritoryManager getTerritoryManager();

  public abstract TanClaimManager getClaimManager();

  public abstract TanLandmarkManager getLandmarkManager();

  public abstract TanEventManager getEventManager();

  public abstract TanFortManager getFortManager();

  public abstract PluginVersion getPluginVersion();

  public abstract PluginVersion getMinimumSupportingMapPlugin();
}
