package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.storage.stored.NationStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionStorage;
import org.leralix.tan.storage.stored.TownStorage;
import org.leralix.tan.utils.text.StringUtil;

public class PlayerBalance extends PapiEntry {

    public PlayerBalance(
            PlayerDataStorage playerDataStorage,
            TownStorage townStorage,
            RegionStorage regionDataStorage,
            NationStorage nationDataStorage)
    {
        super("player_balance", playerDataStorage, townStorage, regionDataStorage, nationDataStorage);
    }

    public String getData(OfflinePlayer player, @NotNull String params) {
        String moneyChar = EconomyUtil.getMoneyIcon();
        return StringUtil.formatMoney(EconomyUtil.getBalance(player)) + moneyChar;
    }
}
