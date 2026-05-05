package org.leralix.tan.utils.graphic;

import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.utils.constants.Constants;

/**
 * Utility class for handling prefix
 */
public class PrefixUtil {

    private PrefixUtil(){
        throw new AssertionError("Utility class");
    }

    /**
     * Add the town prefix to a player's name
     * @param player The player to add the prefix to
     */
    public static void updatePrefix(Player player){
        if(!Constants.enableTownTag()){
            return;
        }
        ITanPlayer tanPlayer = TownsAndNations.getPlugin().getPlayerDataStorage().get(player);

        Town playerTown = tanPlayer.getTown();
        if (playerTown != null){

            String prefix = playerTown.getFormatedTag();

            player.setPlayerListName(prefix + player.getName());
            player.setDisplayName(prefix + player.getName());
        }
        else {
            player.setPlayerListName(player.getName());
            player.setDisplayName(player.getName());
        }
    }

}
