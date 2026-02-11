package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.NationDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

public class TerritoryWithNameLeaderName extends PapiEntry {

    public TerritoryWithNameLeaderName(
            PlayerDataStorage playerDataStorage,
            TownDataStorage townDataStorage,
            RegionDataStorage regionDataStorage,
            NationDataStorage nationDataStorage
    ) {
        super("territory_with_id_{}_leader_name",
                playerDataStorage,
                townDataStorage,
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

        String[] values = extractValues(params);
        if (values.length == 0) return Lang.INVALID_VALUE.get(tanPlayer);
        String id = values[0];
        if (id == null) return Lang.INVALID_ID.get(tanPlayer);
        TerritoryData territoryData = TerritoryUtil.getTerritory(id);
        if (territoryData == null) return Lang.INVALID_TERRITORY.get(tanPlayer);

        return territoryData.getLeaderData().getOfflinePlayer().getName();
    }
}
