package org.leralix.tan.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class EconomyUtil {

  private static AbstractTanEcon econ;

  public static void register(AbstractTanEcon newEcon) {
    econ = newEcon;
  }

  public static boolean isStandalone() {
    return econ instanceof TanEconomyStandalone;
  }

  public static double getBalance(OfflinePlayer offlinePlayer) {
    return econ.getBalance(PlayerDataStorage.getInstance().getSync(offlinePlayer));
  }

  public static double getBalance(ITanPlayer player) {
    return econ.getBalance(player);
  }

  public static double getBalance(Player player) {
    return econ.getBalance(PlayerDataStorage.getInstance().getSync(player));
  }

  public static void removeFromBalance(ITanPlayer tanPlayer, double amount) {
    econ.withdrawPlayer(tanPlayer, amount);
  }

  public static void removeFromBalance(OfflinePlayer offlinePlayer, double amount) {
    econ.withdrawPlayer(PlayerDataStorage.getInstance().getSync(offlinePlayer), amount);
  }

  public static void removeFromBalance(Player player, double amount) {
    econ.withdrawPlayer(PlayerDataStorage.getInstance().getSync(player), amount);
  }

  public static void addFromBalance(ITanPlayer player, double amount) {
    econ.depositPlayer(player, amount);
  }

  public static void addFromBalance(Player player, double amount) {
    econ.depositPlayer(PlayerDataStorage.getInstance().getSync(player), amount);
  }

  public static void addFromBalance(OfflinePlayer offlinePlayer, double amount) {
    econ.depositPlayer(PlayerDataStorage.getInstance().getSync(offlinePlayer), amount);
  }

  public static String getMoneyIcon() {
    return econ.getMoneyIcon();
  }

  public static void setBalance(ITanPlayer target, double amount) {
    removeFromBalance(target, getBalance(target));
    addFromBalance(target, amount);
  }
}
