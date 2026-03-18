package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.json.NationDataStorage;
import org.leralix.tan.storage.stored.json.RegionDataStorage;
import org.leralix.tan.storage.stored.json.TownDataStorage;
import org.leralix.tan.utils.text.StringUtil;

public class PlayerBalance extends PapiEntry {

    public PlayerBalance(
            PlayerDataStorage playerDataStorage,
            TownDataStorage townDataStorage,
            RegionDataStorage regionDataStorage,
            NationDataStorage nationDataStorage)
    {
        super("player_balance", playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage);
    }

    public String getData(OfflinePlayer player, @NotNull String params) {
        String moneyChar = EconomyUtil.getMoneyIcon();
        return StringUtil.formatMoney(EconomyUtil.getBalance(player)) + moneyChar;
    }
}
