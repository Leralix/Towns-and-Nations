package org.leralix.tan.api;

import org.leralix.lib.data.PluginVersion;
import org.leralix.tan.api.managers.ClaimManager;
import org.leralix.tan.api.managers.LandmarkManager;
import org.leralix.tan.api.managers.PlayerManager;
import org.leralix.tan.api.managers.TerritoryManager;
import org.tan.api.TanAPI;
import org.tan.api.getters.TanClaimManager;
import org.tan.api.getters.TanLandmarkManager;
import org.tan.api.getters.TanPlayerManager;
import org.tan.api.getters.TanTerritoryManager;

public class InternalAPI extends TanAPI {

    PlayerManager playerManager = new PlayerManager();
    TerritoryManager territoryManager = TerritoryManager.getInstance();
    ClaimManager claimManager = ClaimManager.getInstance();
    LandmarkManager landmarkManager = LandmarkManager.getInstance();

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
    public PluginVersion getPluginVersion() {
        return new PluginVersion("0.14.0");
    }

    @Override
    public PluginVersion getMinimumSupportingMapPlugin() {
        return new PluginVersion("0.11.0");
    }
}
