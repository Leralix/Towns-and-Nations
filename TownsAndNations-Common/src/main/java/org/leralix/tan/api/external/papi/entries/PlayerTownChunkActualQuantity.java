package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.NationStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionStorage;
import org.leralix.tan.storage.stored.TownStorage;

public class PlayerTownChunkActualQuantity extends PapiEntry {


    public PlayerTownChunkActualQuantity(
            PlayerDataStorage playerDataStorage,
            TownStorage townStorage,
            RegionStorage regionDataStorage,
            NationStorage nationDataStorage
    ) {
        super("player_town_chunk_actual_quantity",
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

        return tanPlayer.hasTown() ? Integer.toString(tanPlayer.getTown().getNumberOfClaimedChunk()) : Lang.NO_TOWN.get(tanPlayer);
    }
}
