package org.tan.TownsAndNations.utils;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;

public class TagUtil {

    public static void addPrefix(Player player){
        if(!ConfigUtil.getCustomConfig("config.yml").getBoolean("EnableTownPrefix",true)){
            return;
        }
        PlayerData playerData = PlayerDataStorage.get(player);

        if (playerData.getTown() != null){
            String prefix = playerData.getTown().getTag() + " ";

            player.setPlayerListName(prefix + player.getName());
            player.setDisplayName(prefix + player.getName());
        }
    }

}
