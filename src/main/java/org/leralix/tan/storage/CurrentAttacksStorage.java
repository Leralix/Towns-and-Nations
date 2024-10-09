package org.leralix.tan.storage;

import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.dataclass.wars.CurrentAttacks;

import java.util.HashMap;
import java.util.Map;

public class CurrentAttacksStorage {
    private static final Map<String, CurrentAttacks> attackStatusMap = new HashMap<>();

    public static void startAttack(PlannedAttack plannedAttack){
        String newID = getNextID();
        attackStatusMap.put(newID, new CurrentAttacks(newID, plannedAttack));
    }

    public static void remove(CurrentAttacks currentAttacks){
        attackStatusMap.remove(currentAttacks.getId());
    }

    private static String getNextID(){
        int ID = 0;
        while(attackStatusMap.containsKey("A"+ID)){
            ID++;
        }
        return "A"+ID;
    }

    public static CurrentAttacks get(String ID) {
        return attackStatusMap.get(ID);
    }
}
