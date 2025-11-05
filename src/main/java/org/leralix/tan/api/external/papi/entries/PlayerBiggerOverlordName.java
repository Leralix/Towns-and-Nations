package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class PlayerBiggerOverlordName extends PapiEntry{

    public PlayerBiggerOverlordName() {
        super("player_bigger_overlord_name");
    }

    @Override
    public String getData(OfflinePlayer player, @NotNull String params) {

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player.getUniqueId());

        if (tanPlayer == null) {
            return PLAYER_NOT_FOUND;
        }

        if(tanPlayer.hasRegion())
            return tanPlayer.getRegionSync().getName();
        if(tanPlayer.hasTown())
            return tanPlayer.getTownSync().getName();
        return Lang.NO_TOWN.get(tanPlayer);
    }
}
