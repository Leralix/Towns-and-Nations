package org.leralix.tan.api.external.papi.entries;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.LocalChatStorage;
import org.leralix.tan.storage.stored.NationDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

public class OtherPlayerChatMode extends PapiEntry {

    private final LocalChatStorage localChatStorage;

    public OtherPlayerChatMode(
            PlayerDataStorage playerDataStorage,
            TownDataStorage townDataStorage,
            RegionDataStorage regionDataStorage,
            NationDataStorage nationDataStorage,
            LocalChatStorage localChatStorage
    ) {
        super("chat_mode_{}",
                playerDataStorage,
                townDataStorage,
                regionDataStorage,
                nationDataStorage
        );
        this.localChatStorage = localChatStorage;
    }

    @Override
    public String getData(OfflinePlayer player, @NotNull String params) {

        ITanPlayer tanPlayer = playerDataStorage.get(player.getUniqueId());

        if (tanPlayer == null) {
            return PLAYER_NOT_FOUND;
        }
        LangType langType = tanPlayer.getLang();
        String[] values = extractValues(params);
        OfflinePlayer playerSelected = Bukkit.getOfflinePlayer(values[0]);
        if (!playerSelected.isOnline()) {
            return Lang.INVALID_PLAYER_NAME.get(langType);
        }
        return localChatStorage.getPlayerChatScope(playerSelected.getUniqueId().toString()).getName(langType);
    }
}
