package org.leralix.tan.storage.stored;

import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.War;
import org.leralix.tan.war.info.WarRole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface WarStorage {

    /**
     * Since planned attacks have a start date, it is necessary to check if any should
     * have started while the server was offline.
     */
    default void updateAttacks() {
        for (War war : getAllWars()) {
            for (PlannedAttack plannedAttack : war.getPlannedAttacks()) {
                plannedAttack.updateStatus();
            }
        }
    }

    /**
     * Get all wars a specific territory takes part in.
     * All wars will be shown, secondary or main role.
     *
     * @param territoryData The territory to get all related war
     * @return The list of war the territory takes part in.
     */
    default List<War> getWarsOfTerritory(Territory territoryData) {
        return getAllWars().stream().filter(war -> war.getTerritoryRole(territoryData) != WarRole.NEUTRAL).toList();
    }

    War newWar(Territory attackingTerritory, Territory defendingTerritory);

    void remove(War plannedAttack);

    /**
     * Check if two territory are part of the same war in enemy side
     *
     * @param mainTerritory The first territory to check
     * @param territoryData The second territory to check
     * @return True if both territory are against in at least a war. False otherwise
     */
    default boolean isTerritoryAtWarWith(Territory mainTerritory, Territory territoryData) {
        for (War war : getWarsOfTerritory(mainTerritory)) {
            if (war.getTerritoryRole(mainTerritory).isOpposite(war.getTerritoryRole(territoryData))) {
                return true;
            }
        }
        return false;
    }

    default Collection<PlannedAttack> getAllAttacks() {
        List<PlannedAttack> res = new ArrayList<>();
        for (War war : getAllWars()) {
            res.addAll(war.getPlannedAttacks());
        }
        return res;
    }

    Collection<War> getAllWars();

    Map<String, War> getAll();


    War get(String warID);

    void save();
}
