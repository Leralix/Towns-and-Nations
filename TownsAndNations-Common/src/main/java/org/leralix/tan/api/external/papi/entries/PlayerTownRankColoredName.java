package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class PlayerTownRankColoredName extends PapiEntry{

    private final PlayerDataStorage playerDataStorage;

    public PlayerTownRankColoredName(PlayerDataStorage playerDataStorage) {
        super("player_town_rank_colored_name");
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public String getData(OfflinePlayer player, @NotNull String params) {

        ITanPlayer tanPlayer = playerDataStorage.get(player.getUniqueId());

        if (tanPlayer == null) {
            return PLAYER_NOT_FOUND;
        }

        return tanPlayer.hasTown() ? tanPlayer.getTownRank().getColoredName() : Lang.NO_TOWN.get(tanPlayer);
    }
}
