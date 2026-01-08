package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class PlayerKingdomRankColoredName extends PapiEntry {

    public PlayerKingdomRankColoredName() {
        super("player_kingdom_rank_colored_name");
    }

    @Override
    public String getData(OfflinePlayer player, @NotNull String params) {

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player.getUniqueId());

        if (tanPlayer == null) {
            return PLAYER_NOT_FOUND;
        }

        return tanPlayer.hasKingdom() ? tanPlayer.getKingdomRank().getColoredName() : Lang.NO_KINGDOM.get(tanPlayer);
    }
}
