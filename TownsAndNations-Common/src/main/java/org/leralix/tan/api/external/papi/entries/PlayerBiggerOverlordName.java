package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.NationDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

public class PlayerBiggerOverlordName extends PapiEntry{

    public PlayerBiggerOverlordName(
            PlayerDataStorage playerDataStorage,
            TownDataStorage townDataStorage,
            RegionDataStorage regionDataStorage,
            NationDataStorage nationDataStorage
    ) {
        super("player_bigger_overlord_name",
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

        if(tanPlayer.hasNation())
            return tanPlayer.getNation().getName();
        if(tanPlayer.hasRegion())
            return tanPlayer.getRegion().getName();
        if(tanPlayer.hasTown())
            return tanPlayer.getTown().getName();
        return Lang.NO_NATION.get(tanPlayer);
    }
}
