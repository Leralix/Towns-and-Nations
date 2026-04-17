package org.leralix.tan.api.external.papi.entries;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.chunk.IClaimedChunk;
import org.leralix.tan.data.chunk.TownClaimedChunk;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.storage.stored.NationStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionStorage;
import org.leralix.tan.storage.stored.TownStorage;
import org.leralix.tan.utils.constants.Constants;

public class PlayerLocationPropertyExist extends PapiEntry{

    public PlayerLocationPropertyExist(
            PlayerDataStorage playerDataStorage,
            TownStorage townStorage,
            RegionStorage regionDataStorage,
            NationStorage nationDataStorage
    ) {
        super("player_location_property_exist",
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

        if(claimedChunk instanceof TownClaimedChunk townClaimedChunk){

            Town townData = townClaimedChunk.getTown();

            return townData.getProperty(location) != null ?
                    Constants.getTruePlaceholderString() :
                    Constants.getFalsePlaceholderString();
        }

        return Constants.getFalsePlaceholderString();
    }
}
