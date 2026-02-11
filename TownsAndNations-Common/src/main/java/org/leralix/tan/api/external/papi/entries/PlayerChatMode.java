package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.storage.LocalChatStorage;
import org.leralix.tan.storage.stored.NationDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

public class PlayerChatMode extends PapiEntry{

    private final LocalChatStorage localChatStorage;

    public PlayerChatMode (
            PlayerDataStorage playerDataStorage,
            TownDataStorage townDataStorage,
            RegionDataStorage regionDataStorage,
            NationDataStorage nationDataStorage,
            LocalChatStorage localChatStorage
    ) {
        super("chat_mode",
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

        return localChatStorage.getPlayerChatScope(player.getUniqueId().toString()).getName(tanPlayer.getLang());
    }
}
