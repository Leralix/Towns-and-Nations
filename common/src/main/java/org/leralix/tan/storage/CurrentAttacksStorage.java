package org.leralix.tan.storage;

import org.leralix.tan.dataclass.wars.CurrentAttack;
import org.leralix.tan.dataclass.wars.PlannedAttack;

import java.util.HashMap;
import java.util.Map;

public class CurrentAttacksStorage {
    private static final Map<String, CurrentAttack> attackStatusMap = new HashMap<>();

    public static void startAttack(PlannedAttack plannedAttack){
        String newID = getNextID();
        attackStatusMap.put(newID, new CurrentAttack(newID, plannedAttack));
    }

    public static void remove(CurrentAttack currentAttacks){
        attackStatusMap.remove(currentAttacks.getId());
    }

    private static String getNextID(){
        int ID = 0;
        while(attackStatusMap.containsKey("A"+ID)){
            ID++;
        }
        return "A"+ID;
    }

    public static CurrentAttack get(String id) {
        return attackStatusMap.get(id);
    }
}
