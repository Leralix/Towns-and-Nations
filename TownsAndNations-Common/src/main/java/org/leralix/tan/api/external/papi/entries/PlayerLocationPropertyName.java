package org.leralix.tan.api.external.papi.entries;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.chunk.ClaimedChunk;
import org.leralix.tan.dataclass.chunk.TownClaimedChunk;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;

public class PlayerLocationPropertyName extends PapiEntry{

    public PlayerLocationPropertyName() {
        super("player_location_property_name");
    }

    @Override
    public String getData(OfflinePlayer player, @NotNull String params) {

        Player onlinePlayer = player.getPlayer();

        if (onlinePlayer == null) {
            return PLAYER_NOT_FOUND;
        }

        Location location = onlinePlayer.getLocation();

        ClaimedChunk claimedChunk = NewClaimedChunkStorage.getInstance().get(location.getChunk());

        if(claimedChunk instanceof TownClaimedChunk townClaimedChunk){

            TownData townData = townClaimedChunk.getTown();
            PropertyData propertyData = townData.getProperty(location);

            if(propertyData != null){
                return propertyData.getName();
            }
        }

        return PROPERTY_NOT_FOUND;
    }
}
