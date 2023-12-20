package org.tan.TownsAndNations.utils;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.PlayerDataStorage;

import java.util.Objects;

public class EconomyUtil {




    public static int getBalance(Player player){
        if(TownsAndNations.hasEconomy()){
            return (int)TownsAndNations.getEconomy().getBalance(player);
        }

        else
            return Objects.requireNonNull(PlayerDataStorage.get(player)).getBalance();
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

}
