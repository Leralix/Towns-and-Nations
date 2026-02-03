package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.storage.LocalChatStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class PlayerChatMode extends PapiEntry{

    private final PlayerDataStorage playerDataStorage;

    public PlayerChatMode (PlayerDataStorage playerDataStorage) {
        super("chat_mode");
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public String getData(OfflinePlayer player, @NotNull String params) {

        ITanPlayer tanPlayer = playerDataStorage.get(player.getUniqueId());

        if (tanPlayer == null) {
            return PLAYER_NOT_FOUND;
        }

        return LocalChatStorage.getPlayerChatScope(player.getUniqueId().toString()).getName(tanPlayer.getLang());
    }
}
