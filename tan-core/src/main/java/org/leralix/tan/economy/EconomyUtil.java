package org.leralix.tan.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;

/**
 * This class handles player money storage. Depending on whether Vault is installed, money can be
 * stored in the {@link ITanPlayer} class.
 */
public class EconomyUtil {

  private static AbstractTanEcon econ;

  public static void register(AbstractTanEcon newEcon) {
    econ = newEcon;
  }

  public static boolean isStandalone() {
    return econ instanceof TanEconomyStandalone;
  }

  /**
   * Retrieves the current balance of the specified player.
   *
   * @param offlinePlayer The player whose balance is to be retrieved.
   * @return The player's current balance.
   */
  public static double getBalance(OfflinePlayer offlinePlayer) {
    return econ.getBalance(PlayerDataStorage.getInstance().getSync(offlinePlayer));
  }

  /**
   * Retrieves the current balance of the specified player.
   *
   * @param player The player whose balance is to be retrieved.
   * @return The player's current balance.
   */
  public static double getBalance(ITanPlayer player) {
    return econ.getBalance(player);
  }

  /**
   * Retrieves the current balance of the specified player.
   *
   * @param player The player whose balance is to be retrieved.
   * @return The player's current balance.
   */
  public static double getBalance(Player player) {
    return econ.getBalance(PlayerDataStorage.getInstance().getSync(player));
  }

  /**
   * Remove the given amount of money to a player balance
   *
   * @param tanPlayer The player whose balance is going to be affected
   * @param amount The amount of money to be subtracted
   */
  public static void removeFromBalance(ITanPlayer tanPlayer, double amount) {
    econ.withdrawPlayer(tanPlayer, amount);
  }

  /**
   * Remove the given amount of money to a player balance
   *
   * @param offlinePlayer The player whose balance is going to be affected
   * @param amount The amount of money to be subtracted
   */
  public static void removeFromBalance(OfflinePlayer offlinePlayer, double amount) {
    econ.withdrawPlayer(PlayerDataStorage.getInstance().getSync(offlinePlayer), amount);
  }

  /**
   * Remove the given amount of money to a player balance
   *
   * @param player The player whose balance is going to be affected
   * @param amount The amount of money to be subtracted
   */
  public static void removeFromBalance(Player player, double amount) {
    econ.withdrawPlayer(PlayerDataStorage.getInstance().getSync(player), amount);
  }

  /**
   * Add the given amount of money to a player balance
   *
   * @param player The player whose balance is going to be affected
   * @param amount The amount of money to be added
   */
  public static void addFromBalance(ITanPlayer player, double amount) {
    econ.depositPlayer(player, amount);
  }

  /**
   * Add the given amount of money to a player balance
   *
   * @param player The player whose balance is going to be affected
   * @param amount The amount of money to be added
   */
  public static void addFromBalance(Player player, double amount) {
    econ.depositPlayer(PlayerDataStorage.getInstance().getSync(player), amount);
  }

  /**
   * Add the given amount of money to a player balance
   *
   * @param offlinePlayer The player whose balance is going to be affected
   * @param amount The amount of money to be added
   */
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
