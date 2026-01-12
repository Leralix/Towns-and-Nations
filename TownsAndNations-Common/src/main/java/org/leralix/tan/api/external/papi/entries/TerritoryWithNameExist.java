package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;

public class TerritoryWithNameExist extends PapiEntry {


    public TerritoryWithNameExist() {
        super("territory_with_name_{}_exist");
    }

    @Override
    public String getData(OfflinePlayer player, @NotNull String params) {

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player.getUniqueId());

        if (tanPlayer == null) {
            return PLAYER_NOT_FOUND;
        }

        String[] values = extractValues(params);
        if(values.length == 0) return Lang.INVALID_VALUE.get(tanPlayer);
        String name = values[0];
        if(name == null) return Lang.INVALID_VALUE.get(tanPlayer);

        return getTerritoryByName(name) != null ? Constants.getTruePlaceholderString() : Constants.getFalsePlaceholderString();
    }
}
