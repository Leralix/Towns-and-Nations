package org.leralix.tan.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;


/**
 * This class handles player money storage.
 * Depending on whether Vault is installed, money can be stored in the {@link ITanPlayer} class.
 */
public class EconomyUtil {

    private static AbstractTanEcon econ;

    private static PlayerDataStorage playerDataStorage;

    private EconomyUtil(){
        throw new AssertionError("Static class");
    }

    public static void init(PlayerDataStorage instance) {
        playerDataStorage = instance;
    }


    public static void register(AbstractTanEcon newEcon) {
        econ = newEcon;
    }

    public static boolean isStandalone(){
        return econ instanceof TanEconomyStandalone;
    }

    /**
     * Retrieves the current balance of the specified player.
     * @param offlinePlayer The player whose balance is to be retrieved.
     * @return              The player's current balance.
     */
    public static double getBalance(OfflinePlayer offlinePlayer){
        return getBalance(playerDataStorage.get(offlinePlayer));
    }
    /**
     * Retrieves the current balance of the specified player.
     * @param player    The player whose balance is to be retrieved.
     * @return          The player's current balance.
     */
    public static double getBalance(ITanPlayer player){
        return econ.getBalance(player);
    }
    /**
     * Retrieves the current balance of the specified player.
     * @param player    The player whose balance is to be retrieved.
     * @return          The player's current balance.
     */
    public static double getBalance(Player player){
        return getBalance(playerDataStorage.get(player));
    }

    /**
     * Remove the given amount of money to a player balance
     * @param tanPlayer        The player whose balance is going to be affected
     * @param amount            The amount of money to be subtracted
     */
    public static void removeFromBalance(ITanPlayer tanPlayer, double amount){
        econ.withdrawPlayer(tanPlayer, amount);
    }
    /**
     * Remove the given amount of money to a player balance
     * @param offlinePlayer     The player whose balance is going to be affected
     * @param amount            The amount of money to be subtracted
     */
    public static void removeFromBalance(OfflinePlayer offlinePlayer, double amount){
        removeFromBalance(playerDataStorage.get(offlinePlayer), amount);
    }

    /**
     * Remove the given amount of money to a player balance
     * @param player     The player whose balance is going to be affected
     * @param amount     The amount of money to be subtracted
     */
    public static void removeFromBalance(Player player, double amount) {
        removeFromBalance(playerDataStorage.get(player), amount);
    }
    /**
     * Add the given amount of money to a player balance
     * @param player     The player whose balance is going to be affected
     * @param amount     The amount of money to be added
     */
    public static void addFromBalance(ITanPlayer player, double amount){
        econ.depositPlayer(player, amount);
    }
    /**
     * Add the given amount of money to a player balance
     * @param player     The player whose balance is going to be affected
     * @param amount     The amount of money to be added
     */
    public static void addFromBalance(Player player, double amount){
        addFromBalance(playerDataStorage.get(player), amount);
    }

    public static String getMoneyIcon() {
        return econ.getMoneyIcon();
    }


    public static void setBalance(ITanPlayer target, double amount) {
        removeFromBalance(target, getBalance(target));
        addFromBalance(target, amount);
    }
}
