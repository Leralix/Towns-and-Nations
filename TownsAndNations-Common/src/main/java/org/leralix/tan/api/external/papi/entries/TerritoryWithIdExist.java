package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

public class TerritoryWithIdExist extends PapiEntry {

    private final PlayerDataStorage playerDataStorage;

    public TerritoryWithIdExist(PlayerDataStorage playerDataStorage) {
        super("territory_with_id_{}_exist");
        this.playerDataStorage = playerDataStorage;
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
        return TerritoryUtil.getTerritory(id) != null ? Constants.getTruePlaceholderString() : Constants.getFalsePlaceholderString();
    }
}
