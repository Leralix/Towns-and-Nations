package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class PlayerRegionResidentQuantity extends PapiEntry {


    private final PlayerDataStorage playerDataStorage;

    public PlayerRegionResidentQuantity(PlayerDataStorage playerDataStorage){
        super("player_region_resident_quantity");
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public String getData(OfflinePlayer player, @NotNull String params) {

        ITanPlayer tanPlayer = playerDataStorage.get(player.getUniqueId());

        if (tanPlayer == null) {
            return PLAYER_NOT_FOUND;
        }

        return tanPlayer.hasRegion() ? Integer.toString(tanPlayer.getRegion().getTotalPlayerCount()) : Lang.NO_REGION.get(tanPlayer);
    }
}
