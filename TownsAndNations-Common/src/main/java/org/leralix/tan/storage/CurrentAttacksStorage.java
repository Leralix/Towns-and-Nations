package org.leralix.tan.storage;

import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.legacy.CurrentAttack;
import org.tan.api.interfaces.TanPlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CurrentAttacksStorage {
    private static final Map<String, CurrentAttack> attackStatusMap = new HashMap<>();

    public static void startAttack(PlannedAttack plannedAttack, long endTime) {
        attackStatusMap.put(plannedAttack.getID(), new CurrentAttack(plannedAttack, System.currentTimeMillis(), endTime));
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

    public static void notifyPlayerDeath(TanPlayer tanPlayer) {
        for (CurrentAttack currentAttack : attackStatusMap.values()) {
            if (currentAttack.isPlayerInvolved(tanPlayer)) {
                currentAttack.handlePlayerDeath(tanPlayer);
            }
        }
    }
}
