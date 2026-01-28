package org.leralix.tan.api.external.papi.entries;

import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class GetFirstTerritoryIdWithName extends PapiEntry {

    public GetFirstTerritoryIdWithName() {
        super("server_get_first_territory_id_with_name_{}");
    }

    @Override
    public String getData(org.bukkit.OfflinePlayer player, @NotNull String params) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player.getUniqueId());

        if (tanPlayer == null) {
            return PLAYER_NOT_FOUND;
        }

        String name = extractValues(params)[0];
        if(name == null) return Lang.INVALID_NAME.get(tanPlayer);
        TerritoryData territoryData = getTerritoryByName(name);
        if(territoryData == null) return Lang.INVALID_TERRITORY.get(tanPlayer);
        return territoryData.getID();
    }
}
