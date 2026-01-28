package org.leralix.tan.api.external.papi.entries;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class OtherPlayerRegionName extends PapiEntry {

    public OtherPlayerRegionName() {
        super("player_{}_region_name");
    }

    @Override
    public String getData(OfflinePlayer player, @NotNull String params) {

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player.getUniqueId());

        if (tanPlayer == null) {
            return PLAYER_NOT_FOUND;
        }

        String[] values = extractValues(params);
        OfflinePlayer playerSelected = Bukkit.getOfflinePlayer(values[0]);

        ITanPlayer otherTanPlayer = PlayerDataStorage.getInstance().get(playerSelected);

        return otherTanPlayer.hasRegion() ?
                otherTanPlayer.getRegion().getName() :
                Lang.NO_REGION.get(tanPlayer);
    }
}
