package org.leralix.tan.storage;

import org.leralix.tan.dataclass.wars.CurrentAttack;
import org.leralix.tan.dataclass.wars.PlannedAttack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CurrentAttacksStorage {
    private static final Map<String, CurrentAttack> attackStatusMap = new HashMap<>();

    public static void startAttack(PlannedAttack plannedAttack){
        attackStatusMap.put(plannedAttack.getID(), new CurrentAttack(plannedAttack));
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
