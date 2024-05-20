package org.tan.TownsAndNations.utils;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;

/**
 * Utility class for handling prefix
 */
public class prefixUtil {
    /**
     * Add the town prefix to a player's name
     * @param player The player to add the prefix to
     */
    public static void addPrefix(Player player){
        if(!ConfigUtil.getCustomConfig("config.yml").getBoolean("EnableTownPrefix",true)){
            return;
        }
        PlayerData playerData = PlayerDataStorage.get(player);

        if (playerData.getTown() != null){
            String prefix = playerData.getTown().getColoredTag() + " ";

            player.setPlayerListName(prefix + player.getName());
            player.setDisplayName(prefix + player.getName());
        }
    }

}
