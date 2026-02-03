package org.leralix.tan.api.internal;

import org.leralix.lib.data.PluginVersion;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.api.internal.managers.*;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.storage.stored.WarStorage;
import org.tan.api.TanAPI;
import org.tan.api.getters.*;

public class InternalAPI extends TanAPI {

    private final PlayerManager playerManager;
    private final TerritoryManager territoryManager;
    private final ClaimManager claimManager;
    private final LandmarkManager landmarkManager;
    private final EventManager eventManager;
    private final FortManager fortManager;
    private final WarManager warManager;

    private final PluginVersion pluginVersion;

    public InternalAPI(PluginVersion pluginVersion, TownsAndNations plugin) {
        super();
        this.pluginVersion = pluginVersion;

        playerManager = new PlayerManager(plugin.getPlayerDataStorage());
        territoryManager = TerritoryManager.getInstance();
        claimManager = ClaimManager.getInstance();
        landmarkManager = LandmarkManager.getInstance();
        eventManager = EventManager.getInstance();
        fortManager = FortManager.getInstance();
        warManager = new WarManager(WarStorage.getInstance());
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
    public TanWarManager getWarManager() {
        return warManager;
    }

    @Override
    public PluginVersion getPluginVersion() {
        return pluginVersion;
    }

    @Override
    public PluginVersion getMinimumSupportingMapPlugin() {
        return new PluginVersion("0.11.0");
    }
}
