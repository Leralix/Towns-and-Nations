package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.NationStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionStorage;
import org.leralix.tan.storage.stored.TownStorage;

public class GetFirstTerritoryIdWithName extends PapiEntry {


    public GetFirstTerritoryIdWithName(
            PlayerDataStorage playerDataStorage,
            TownStorage townStorage,
            RegionStorage regionDataStorage,
            NationStorage nationDataStorage
    ) {
        super(
                "server_get_first_territory_id_with_name_{}",
                playerDataStorage,
                townStorage,
                regionDataStorage,
                nationDataStorage
        );
    }

    @Override
    public String getData(OfflinePlayer player, @NotNull String params) {
        ITanPlayer tanPlayer = playerDataStorage.get(player.getUniqueId());

        if (tanPlayer == null) {
            return PLAYER_NOT_FOUND;
        }

        String name = extractValues(params)[0];
        if(name == null) return Lang.INVALID_NAME.get(tanPlayer);
        Territory territoryData = getTerritoryByName(name);
        if(territoryData == null) return Lang.INVALID_TERRITORY.get(tanPlayer);
        return territoryData.getID();
    }
}
