package org.tan.TownsAndNations.API;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;

public class TanAPI {


    public static String test(){
        return "test effectué avec succès";
    }

    public static String getVersion(){
        return TownsAndNations.getPlugin().getDescription().getVersion();
    }

    public static int getPlayerAmount(String playerUUID){
        return PlayerDataStorage.getStat(playerUUID).getBalance();
    }
    public static int getPlayerAmount(Player player){
        return PlayerDataStorage.getStat(player).getBalance();
    }

    public static void addPlayerAmount(Player player,int amount){
        PlayerDataStorage.getStat(player).addToBalance(amount);
    }
    public static void addPlayerAmount(String playerUUID,int amount){
        PlayerDataStorage.getStat(playerUUID).addToBalance(amount);
    }

    public static void setPlayerAmount(Player player,int amount){
        PlayerDataStorage.getStat(player).setBalance(amount);
    }
    public static void setPlayerAmount(String playerUUID,int amount){
        PlayerDataStorage.getStat(playerUUID).setBalance(amount);
    }

    public static TownData getPlayerTown(Player player){
        return TownDataStorage.getTown(player);
    }
    public static TownData getPlayerTown(String playerUUID){
        return TownDataStorage.getTown(playerUUID);
    }






}

