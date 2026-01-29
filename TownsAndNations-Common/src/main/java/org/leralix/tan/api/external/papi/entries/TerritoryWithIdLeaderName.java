package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class TerritoryWithIdLeaderName extends PapiEntry {


    private final PlayerDataStorage playerDataStorage;

    public TerritoryWithIdLeaderName(PlayerDataStorage playerDataStorage) {
        super("territory_with_id_{}_leader_name");
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public String getData(OfflinePlayer player, @NotNull String params) {

        ITanPlayer tanPlayer = playerDataStorage.get(player.getUniqueId());

        if (tanPlayer == null) {
            return PLAYER_NOT_FOUND;
        }

        String[] values = extractValues(params);
        if(values.length == 0) return Lang.INVALID_VALUE.get(tanPlayer);
        String name = values[0];
        if(name == null) return Lang.INVALID_ID.get(tanPlayer);
        TerritoryData territoryData = getTerritoryByName(name);
        if (territoryData == null) return Lang.INVALID_TERRITORY.get(tanPlayer);

        return territoryData.getLeaderData().getOfflinePlayer().getName();
    }
}
