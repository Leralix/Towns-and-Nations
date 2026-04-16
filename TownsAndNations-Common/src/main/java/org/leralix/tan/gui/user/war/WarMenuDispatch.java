package org.leralix.tan.gui.user.war;

import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.war.War;
import org.leralix.tan.war.info.WarRole;

public class WarMenuDispatch {


    public static void openMenu(Player player, War war, Territory territoryData){

        WarRole warRole = war.getTerritoryRole(territoryData);
        switch (warRole){
            case NEUTRAL -> new NeutralWarMenu(player, territoryData, war);
            case MAIN_ATTACKER, MAIN_DEFENDER -> new WarMenu(player, territoryData, war);
            case OTHER_ATTACKER, OTHER_DEFENDER -> new SecondaryWarMenu(player, territoryData, war);
        }

    }
}
