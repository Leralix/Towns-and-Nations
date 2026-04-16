package org.leralix.tan.api.external.papi.entries;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.storage.stored.NationStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionStorage;
import org.leralix.tan.storage.stored.TownStorage;
import org.leralix.tan.utils.constants.Constants;

public class PlayerColoredTownTagOrCustomText extends PapiEntry {

    public PlayerColoredTownTagOrCustomText(
            PlayerDataStorage playerDataStorage,
            TownStorage townStorage,
            RegionStorage regionDataStorage,
            NationStorage nationDataStorage
    ) {
        super("player_colored_town_tag_or_{}",
                playerDataStorage,
                townStorage,
                regionDataStorage,
                nationDataStorage
        );
    }

    @Override
    public String getData(OfflinePlayer player, @NotNull String params) {

        ITanPlayer tanPlayer =  playerDataStorage.get(player.getUniqueId());

        var values = extractValues(params);
        String replacement = values.length == 0 ? "" : values[0];

        if (tanPlayer == null) {
            return PLAYER_NOT_FOUND;
        }


        return tanPlayer.hasTown() ?
                tanPlayer.getTown().getFormatedTag() :
                ChatColor.translateAlternateColorCodes('&',
                        Constants.getTownTagFormat()
                                .replace("{townColor}", "")
                                .replace("{townTag}", replacement));

    }
}
