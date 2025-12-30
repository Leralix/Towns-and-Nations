package org.leralix.tan.storage;

import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.legacy.CurrentAttack;
import org.leralix.tan.war.legacy.InfiniteCurrentAttack;
import org.leralix.tan.war.legacy.TemporalCurrentAttack;
import org.leralix.tan.war.legacy.WarRole;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CurrentAttacksStorage {
    private static final Map<String, CurrentAttack> attackStatusMap = new HashMap<>();

    public static void startAttack(PlannedAttack plannedAttack, long endTime) {

        if(endTime < 0 ){
            attackStatusMap.put(plannedAttack.getID(), new InfiniteCurrentAttack(plannedAttack));
        }
        else {
            attackStatusMap.put(plannedAttack.getID(), new TemporalCurrentAttack(plannedAttack, endTime));
        }
    }

    public static void remove(CurrentAttack currentAttacks){
        attackStatusMap.remove(currentAttacks.getAttackData().getID());
    }

    public static CurrentAttack get(String id) {
        return attackStatusMap.get(id);
    }

    public static Collection<CurrentAttack> getAll() {
        return attackStatusMap.values();
    }

    public static void notifyPlayerDeath(ITanPlayer tanPlayer) {
        for (CurrentAttack currentAttack : attackStatusMap.values()) {
            if (currentAttack.containsPlayer(tanPlayer)) {
                var role = currentAttack.getAttackData().getRole(tanPlayer);
                if(role == WarRole.MAIN_ATTACKER || role == WarRole.OTHER_ATTACKER) {
                    currentAttack.attackerKilled();
                }
                else if (role == WarRole.MAIN_DEFENDER || role == WarRole.OTHER_DEFENDER) {
                    currentAttack.defenderKilled();
                }
            }
        }
    }
}
