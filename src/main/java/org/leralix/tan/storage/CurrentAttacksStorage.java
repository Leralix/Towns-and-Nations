package org.leralix.tan.storage;

import org.leralix.tan.dataclass.wars.CurrentAttack;
import org.leralix.tan.dataclass.wars.CurrentWar;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CurrentAttacksStorage {
    private static final Map<String, CurrentAttack> attackStatusMap = new HashMap<>();

    public static void startAttack(CurrentWar plannedAttack, long startTime, long endTime) {
        attackStatusMap.put(plannedAttack.getID(), new CurrentAttack(plannedAttack, startTime, endTime));
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
}
