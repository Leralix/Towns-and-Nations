package org.tan.TownsAndNations.API;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.PlayerStatStorage;

public class TanAPI {


    public static String test(){
        return "test effectué avec succès";
    }

    public static String getVersion(){
        return TownsAndNations.getPlugin().getDescription().getVersion();
    }

    public static int getPlayerAmount(String playerUUID){
        return PlayerStatStorage.getStat(playerUUID).getBalance();
    }

    public static int getPlayerAmount(Player player){
        return PlayerStatStorage.getStat(player).getBalance();
    }








}

