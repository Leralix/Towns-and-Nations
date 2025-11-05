package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

public class TerritoryWithIdExist extends PapiEntry {

    public TerritoryWithIdExist(){
        super("territory_with_id_{}_exist");
    }

    @Override
    public String getData(OfflinePlayer player, @NotNull String params) {

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player.getUniqueId());

        if (tanPlayer == null) {
            return PLAYER_NOT_FOUND;
        }

        String[] values = extractValues(params);
        if(values.length == 0) return Lang.INVALID_VALUE.get(tanPlayer);
        String id = values[0];
        if(id == null) return Lang.INVALID_ID.get(tanPlayer);
        return TownDataStorage.getInstance().getSync(id) != null || RegionDataStorage.getInstance().getSync(id) != null ? TRUE : FALSE;
    }
}
