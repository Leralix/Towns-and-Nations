package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.upgrade.rewards.numeric.ChunkCap;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.NationDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

public class PlayerTownRemainingQuantity extends PapiEntry {

    public PlayerTownRemainingQuantity(
            PlayerDataStorage playerDataStorage,
            TownDataStorage townDataStorage,
            RegionDataStorage regionDataStorage,
            NationDataStorage nationDataStorage
    ) {
        super("player_town_chunk_remaining_quantity",
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

        if (tanPlayer.hasTown()) {
            var town = tanPlayer.getTown();
            var level = town.getNewLevel().getStat(ChunkCap.class);

            if (level.isUnlimited()) {
                return "∞";
            } else {
                return Integer.toString(level.getMaxAmount() - town.getNumberOfClaimedChunk());
            }
        }
        return Lang.NO_TOWN.get(tanPlayer);
    }
}
