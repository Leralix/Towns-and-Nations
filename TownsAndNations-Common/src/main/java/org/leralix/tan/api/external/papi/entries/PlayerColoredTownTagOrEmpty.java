package org.leralix.tan.api.external.papi.entries;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.storage.stored.NationDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.constants.Constants;

public class PlayerColoredTownTagOrEmpty extends PapiEntry {

    public PlayerColoredTownTagOrEmpty(
            PlayerDataStorage playerDataStorage,
            TownDataStorage townDataStorage,
            RegionDataStorage regionDataStorage,
            NationDataStorage nationDataStorage
    ) {
        super("player_colored_town_tag_or_empty",
                playerDataStorage,
                townDataStorage,
                regionDataStorage,
                nationDataStorage
        );
    }

    @Override
    public String getData(OfflinePlayer player, @NotNull String params) {

        ITanPlayer tanPlayer =  playerDataStorage.get(player.getUniqueId());

        if (tanPlayer == null) {
            return PLAYER_NOT_FOUND;
        }


        return tanPlayer.hasTown() ?
                tanPlayer.getTown().getFormatedTag() :
                ChatColor.translateAlternateColorCodes('&',
                        Constants.getTownTagFormat()
                                .replace("{townColor}", "")
                                .replace("{townTag}", ""));
    }
}
