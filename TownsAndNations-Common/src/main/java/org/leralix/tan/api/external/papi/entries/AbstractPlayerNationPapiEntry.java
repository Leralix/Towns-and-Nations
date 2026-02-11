package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.NationDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

public abstract class AbstractPlayerNationPapiEntry extends PapiEntry {

    protected AbstractPlayerNationPapiEntry(
            String identifier,
            PlayerDataStorage playerDataStorage,
            TownDataStorage townDataStorage,
            RegionDataStorage regionDataStorage,
            NationDataStorage nationDataStorage) {
        super(identifier, playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage);
    }

    @Override
    public final String getData(OfflinePlayer player, @NotNull String params) {

        ITanPlayer tanPlayer = playerDataStorage.get(player.getUniqueId());

        if (tanPlayer == null) {
            return PLAYER_NOT_FOUND;
        }

        if (!tanPlayer.hasNation()) {
            return Lang.NO_NATION.get(tanPlayer);
        }

        return getDataForNationPlayer(tanPlayer);
    }

    protected abstract String getDataForNationPlayer(ITanPlayer tanPlayer);
}
