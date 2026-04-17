
package org.leralix.tan.api.external.papi.entries;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.chunk.*;
import org.leralix.tan.storage.stored.NationStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionStorage;
import org.leralix.tan.storage.stored.TownStorage;

public class PlayerLocationChunkName extends PapiEntry{

    public PlayerLocationChunkName(
            PlayerDataStorage playerDataStorage,
            TownStorage townStorage,
            RegionStorage regionDataStorage,
            NationStorage nationDataStorage
    ) {
        super("player_location_chunk_name",
                playerDataStorage,
                townStorage,
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

        IClaimedChunk claimedChunk = TownsAndNations.getPlugin().getClaimStorage().get(location.getChunk());
        return switch (claimedChunk){
            case TownClaimedChunk townClaimedChunk -> townClaimedChunk.getTown().getName();
            case RegionClaimedChunk regionClaimedChunk -> regionClaimedChunk.getRegion().getName();
            case NationClaimedChunk nationClaimedChunk -> nationClaimedChunk.getOwner().getName();
            case LandmarkClaimedChunk landmarkClaimedChunk -> landmarkClaimedChunk.getLandMark().getName();
            case WildernessChunkData wildernessChunkData -> wildernessChunkData.getName();
            default -> throw new IllegalStateException("Unexpected value: " + claimedChunk);
        };
    }
}
