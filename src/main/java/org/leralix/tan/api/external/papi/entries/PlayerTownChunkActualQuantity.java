package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class PlayerTownChunkActualQuantity extends PapiEntry {


    public PlayerTownChunkActualQuantity(){
        super("player_town_chunk_actual_quantity");
    }

    @Override
    public String getData(OfflinePlayer player, @NotNull String params) {

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player.getUniqueId());

        if (tanPlayer == null) {
            return PLAYER_NOT_FOUND;
        }

        return tanPlayer.hasTown() ? Integer.toString(tanPlayer.getTownSync().getNumberOfClaimedChunk()) : Lang.NO_TOWN.get(tanPlayer);
    }
}
