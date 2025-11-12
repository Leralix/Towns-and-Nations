package org.leralix.tan.api.external.papi.entries.player;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.api.external.papi.entries.PapiEntry;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.utils.text.StringUtil;

public class PlayerBalance extends PapiEntry {

  public PlayerBalance() {
    super("player_balance");
  }

  public String getData(OfflinePlayer player, @NotNull String params) {
    String moneyChar = EconomyUtil.getMoneyIcon();
    return StringUtil.formatMoney(EconomyUtil.getBalance(player)) + moneyChar;
  }
}
