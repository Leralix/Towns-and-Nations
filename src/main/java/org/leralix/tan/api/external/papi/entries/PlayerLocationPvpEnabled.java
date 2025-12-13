
package org.leralix.tan.api.external.papi.entries;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;

public class PlayerLocationPvpEnabled extends PapiEntry {

    public PlayerLocationPvpEnabled() {
        super("player_location_pvp_enabled");
    }

    @Override
    public String getData(OfflinePlayer player, @NotNull String params) {

        Player onlinePlayer = player.getPlayer();

        if (onlinePlayer == null) {
            return PLAYER_NOT_FOUND;
        }

        Location location = onlinePlayer.getLocation();

        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(location.getChunk());

        return claimedChunk.canPVPHappen() ?
                TRUE :
                FALSE;

    }
}
