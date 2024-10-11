package org.leralix.tan.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.stored.PlayerDataStorage;


/**
 * This class handles player money storage.
 * Depending on whether Vault is installed, money can be stored in the {@link PlayerData} class.
 */
public class EconomyUtil {
    /**
     * Storing if vault is running on the server. Initialised at False
     */
    private static boolean hasEconomy = false;

    /**
     * Set the hasEconomy variable.
     * @param enable    True if vault is installed, false otherwise.
     */
    public static void setEconomy(boolean enable){
        hasEconomy = enable;
    }

    /**
     * Retrieves the current balance of the specified player.
     * @param offlinePlayer The player whose balance is to be retrieved.
     * @return              The player's current balance.
     */
    public static int getBalance(OfflinePlayer offlinePlayer){
        if(hasEconomy)
            return (int)TownsAndNations.getEconomy().getBalance(offlinePlayer);
        return PlayerDataStorage.get(offlinePlayer).getBalance();
    }

    /**
     * Retrieves the current balance of the specified player.
     * @param player    The player whose balance is to be retrieved.
     * @return          The player's current balance.
     */
    public static int getBalance(Player player){
        if(hasEconomy)
            return (int)TownsAndNations.getEconomy().getBalance(player);
        return PlayerDataStorage.get(player).getBalance();
    }

    /**
     * Remove the given amount of money to a player balance
     * @param offlinePlayer     The player whose balance is going to be affected
     * @param amount            The amount of money to be subtracted
     */
    public static void removeFromBalance(OfflinePlayer offlinePlayer, int amount){
        if(hasEconomy) {
            TownsAndNations.getEconomy().withdrawPlayer(offlinePlayer, amount);
            return;
        }
        PlayerDataStorage.get(offlinePlayer).removeFromBalance(amount);
    }

    /**
     * Remove the given amount of money to a player balance
     * @param player     The player whose balance is going to be affected
     * @param amount     The amount of money to be subtracted
     */
    public static void removeFromBalance(Player player, int amount) {
        if (hasEconomy){
            TownsAndNations.getEconomy().withdrawPlayer(player, amount);
            return;
        }
        PlayerDataStorage.get(player).removeFromBalance(amount);
    }
    /**
     * Add the given amount of money to a player balance
     * @param player     The player whose balance is going to be affected
     * @param amount     The amount of money to be added
     */
    public static void addFromBalance(Player player, int amount){
        if(hasEconomy) {
            TownsAndNations.getEconomy().depositPlayer(player, amount);
            return;
        }
        PlayerDataStorage.get(player).addToBalance(amount);
    }
    /**
     * Add the given amount of money to a player balance
     * @param offlinePlayer     The player whose balance is going to be affected
     * @param amount            The amount of money to be added
     */
    public static void addFromBalance(OfflinePlayer offlinePlayer, int amount) {
        if (hasEconomy){
            TownsAndNations.getEconomy().depositPlayer(offlinePlayer, amount);
            return;
        }
        PlayerDataStorage.get(offlinePlayer.getUniqueId().toString()).addToBalance(amount);
    }

}
