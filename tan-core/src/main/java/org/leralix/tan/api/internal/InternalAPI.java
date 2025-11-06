package org.leralix.tan.api.internal;

import org.leralix.lib.data.PluginVersion;
import org.leralix.tan.api.internal.managers.*;
import org.leralix.tan.events.EventManager;
import org.tan.api.TanAPI;
import org.tan.api.getters.*;

public class InternalAPI extends TanAPI {

  PlayerManager playerManager = new PlayerManager();
  TerritoryManager territoryManager = TerritoryManager.getInstance();
  ClaimManager claimManager = ClaimManager.getInstance();
  LandmarkManager landmarkManager = LandmarkManager.getInstance();
  EventManager eventManager = EventManager.getInstance();
  FortManager fortManager = FortManager.getInstance();

  PluginVersion pluginVersion;
  PluginVersion minimumSupportingMapPlugin;

  public InternalAPI(PluginVersion pluginVersion, PluginVersion minimumSupportingMapPlugin) {
    super();
    this.pluginVersion = pluginVersion;
    this.minimumSupportingMapPlugin = minimumSupportingMapPlugin;
  }

  @Override
  public TanPlayerManager getPlayerManager() {
    return playerManager;
  }

  @Override
  public TanTerritoryManager getTerritoryManager() {
    return territoryManager;
  }

  @Override
  public TanClaimManager getClaimManager() {
    return claimManager;
  }

  @Override
  public TanLandmarkManager getLandmarkManager() {
    return landmarkManager;
  }

  @Override
  public TanEventManager getEventManager() {
    return eventManager;
  }

  @Override
  public TanFortManager getFortManager() {
    return fortManager;
  }

  @Override
  public PluginVersion getPluginVersion() {
    return new PluginVersion("0.14.0");
  }

  @Override
  public PluginVersion getMinimumSupportingMapPlugin() {
    return new PluginVersion("0.11.0");
  }
}
