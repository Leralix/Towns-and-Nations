package org.leralix.tan.api;

import org.leralix.lib.data.PluginVersion;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.tan.api.TanAPI;
import org.tan.api.getters.TanPlayerManager;
import org.tan.api.getters.TanTerritoryManager;

public class InternalAPI extends TanAPI {



    @Override
    public TanPlayerManager getPlayerManager() {
        return PlayerDataStorage.getInstance();
    }

    @Override
    public TanTerritoryManager getTownManager() {
        return null;
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
