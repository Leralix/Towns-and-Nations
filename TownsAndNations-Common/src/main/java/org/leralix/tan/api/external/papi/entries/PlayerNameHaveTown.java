package org.leralix.tan.api.external.papi.entries;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;

public class PlayerNameHaveTown extends PapiEntry{

    private final PlayerDataStorage playerDataStorage;

    public PlayerNameHaveTown(PlayerDataStorage playerDataStorage) {
        super("player_{}_have_town");
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public String getData(OfflinePlayer player, @NotNull String params) {

        ITanPlayer tanPlayer = playerDataStorage.get(player.getUniqueId());

        if (tanPlayer == null) {
            return PLAYER_NOT_FOUND;
        }

        String[] values = extractValues(params);
        if(values.length == 0) return Lang.INVALID_VALUE.get(tanPlayer);
        String playerName = values[0];
        if(playerName == null) return Lang.INVALID_PLAYER_NAME.get(tanPlayer);
        OfflinePlayer playerSelected = Bukkit.getOfflinePlayer(playerName);
        ITanPlayer tanPlayer1 = playerDataStorage.get(playerSelected);
        if(tanPlayer1 == null) return Lang.INVALID_PLAYER_NAME.get(tanPlayer);
        return tanPlayer1.hasTown() ? Constants.getTruePlaceholderString(): Constants.getFalsePlaceholderString();
    }
}
