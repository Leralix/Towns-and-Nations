package org.leralix.tan.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.storage.stored.PlayerDataStorage;


/**
 * This class handles player money storage.
 * Depending on whether Vault is installed, money can be stored in the {@link PlayerData} class.
 */
public class EconomyUtil {

    private static boolean isExternalEconomy = false;
    private static AbstractTanEcon econ;

    public static void setEconomy(AbstractTanEcon newEcon, boolean isExternal) {
        econ = newEcon;
        isExternalEconomy = isExternal;
    }

    /**
     * Retrieves the current balance of the specified player.
     * @param offlinePlayer The player whose balance is to be retrieved.
     * @return              The player's current balance.
     */
    public static double getBalance(OfflinePlayer offlinePlayer){
        return econ.getBalance(PlayerDataStorage.get(offlinePlayer));
    }

    /**
     * Retrieves the current balance of the specified player.
     * @param player    The player whose balance is to be retrieved.
     * @return          The player's current balance.
     */
    public static double getBalance(Player player){
        return econ.getBalance(PlayerDataStorage.get(player));
    }

    /**
     * Remove the given amount of money to a player balance
     * @param offlinePlayer     The player whose balance is going to be affected
     * @param amount            The amount of money to be subtracted
     */
    public static void removeFromBalance(OfflinePlayer offlinePlayer, double amount){
        econ.withdrawPlayer(PlayerDataStorage.get(offlinePlayer), amount);
    }

    /**
     * Remove the given amount of money to a player balance
     * @param player     The player whose balance is going to be affected
     * @param amount     The amount of money to be subtracted
     */
    public static void removeFromBalance(Player player, double amount) {
        econ.withdrawPlayer(PlayerDataStorage.get(player), amount);
    }
    /**
     * Add the given amount of money to a player balance
     * @param player     The player whose balance is going to be affected
     * @param amount     The amount of money to be added
     */
    public static void addFromBalance(Player player, double amount){
        econ.depositPlayer(PlayerDataStorage.get(player), amount);
    }
    /**
     * Add the given amount of money to a player balance
     * @param offlinePlayer     The player whose balance is going to be affected
     * @param amount            The amount of money to be added
     */
    public static void addFromBalance(OfflinePlayer offlinePlayer, double amount) {
        econ.depositPlayer(PlayerDataStorage.get(offlinePlayer), amount);
    }

    public static boolean hasExternalEconomy() {
        return isExternalEconomy;
    }
}
