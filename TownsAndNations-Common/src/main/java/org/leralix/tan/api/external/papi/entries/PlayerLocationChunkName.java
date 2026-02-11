
package org.leralix.tan.api.external.papi.entries;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.chunk.*;
import org.leralix.tan.storage.stored.*;

public class PlayerLocationChunkName extends PapiEntry{

    public PlayerLocationChunkName(
            PlayerDataStorage playerDataStorage,
            TownDataStorage townDataStorage,
            RegionDataStorage regionDataStorage,
            NationDataStorage nationDataStorage
    ) {
        super("player_location_chunk_name",
                playerDataStorage,
                townDataStorage,
                regionDataStorage,
                nationDataStorage
        );
    }

    @Override
    public String getData(OfflinePlayer player, @NotNull String params) {

        Player onlinePlayer = player.getPlayer();

        if (onlinePlayer == null) {
            return PLAYER_NOT_FOUND;
        }

        Location location = onlinePlayer.getLocation();

        ClaimedChunk claimedChunk = NewClaimedChunkStorage.getInstance().get(location.getChunk());
        return switch (claimedChunk){
            case TownClaimedChunk townClaimedChunk -> townClaimedChunk.getTown().getName();
            case RegionClaimedChunk regionClaimedChunk -> regionClaimedChunk.getRegion().getName();
            case NationClaimedChunk nationClaimedChunk -> nationClaimedChunk.getOwner().getName();
            case LandmarkClaimedChunk landmarkClaimedChunk -> landmarkClaimedChunk.getLandMark().getName();
            case WildernessChunk wildernessChunk -> wildernessChunk.getName();
            default -> throw new IllegalStateException("Unexpected value: " + claimedChunk);
        };
    }
}
