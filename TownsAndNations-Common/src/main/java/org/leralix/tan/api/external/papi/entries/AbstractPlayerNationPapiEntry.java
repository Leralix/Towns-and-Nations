package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public abstract class AbstractPlayerNationPapiEntry extends PapiEntry {

    protected AbstractPlayerNationPapiEntry(String identifier) {
        super(identifier);
    }

    @Override
    public final String getData(OfflinePlayer player, @NotNull String params) {

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player.getUniqueId());

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
