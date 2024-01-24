package org.tan.TownsAndNations.utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.PlayerDataStorage;

import java.util.Objects;

public class EconomyUtil {

    public static int getBalance(OfflinePlayer offlinePlayer){
        if(TownsAndNations.hasEconomy()){
            return (int)TownsAndNations.getEconomy().getBalance(offlinePlayer);
        }
        else
            return PlayerDataStorage.get(offlinePlayer.getUniqueId().toString()).getBalance();
    }
    public static int getBalance(Player player){
        if(TownsAndNations.hasEconomy()){
            return (int)TownsAndNations.getEconomy().getBalance(player);
        }
        else
            return PlayerDataStorage.get(player).getBalance();
    }

    public static void removeFromBalance(OfflinePlayer offlinePlayer, int amount){
        if(TownsAndNations.hasEconomy())
            TownsAndNations.getEconomy().withdrawPlayer(offlinePlayer,amount);
        else
            Objects.requireNonNull(PlayerDataStorage.get(offlinePlayer.getUniqueId().toString())).removeFromBalance(amount);
    }

    public static void removeFromBalance(Player player, int amount){
        if(TownsAndNations.hasEconomy())
            TownsAndNations.getEconomy().withdrawPlayer(player,amount);
        else
            Objects.requireNonNull(PlayerDataStorage.get(player)).removeFromBalance(amount);
    }

    public static void addFromBalance(Player player, int amount){
        if(TownsAndNations.hasEconomy())
            TownsAndNations.getEconomy().depositPlayer(player,amount);
        else
            Objects.requireNonNull(PlayerDataStorage.get(player)).addToBalance(amount);
    }

    public static void addFromBalance(OfflinePlayer offlinePlayer, int amount){
        if(TownsAndNations.hasEconomy())
            TownsAndNations.getEconomy().depositPlayer(offlinePlayer,amount);
        else
            Objects.requireNonNull(PlayerDataStorage.get(offlinePlayer.getUniqueId().toString())).addToBalance(amount);
    }

}
