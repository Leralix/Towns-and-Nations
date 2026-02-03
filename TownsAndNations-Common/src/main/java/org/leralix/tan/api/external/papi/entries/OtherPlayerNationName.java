package org.leralix.tan.api.external.papi.entries;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class OtherPlayerNationName extends PapiEntry {

    private final PlayerDataStorage playerDataStorage;

    public OtherPlayerNationName(PlayerDataStorage playerDataStorage) {
        super("player_{}_nation_name");
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public String getData(OfflinePlayer player, @NotNull String params) {

        ITanPlayer tanPlayer = playerDataStorage.get(player.getUniqueId());

        if (tanPlayer == null) {
            return PLAYER_NOT_FOUND;
        }

        String[] values = extractValues(params);
        OfflinePlayer playerSelected = Bukkit.getOfflinePlayer(values[0]);

        ITanPlayer otherTanPlayer = playerDataStorage.get(playerSelected);

        return otherTanPlayer.hasNation() ? otherTanPlayer.getNation().getName() : Lang.NO_NATION.get(tanPlayer);
    }
}
