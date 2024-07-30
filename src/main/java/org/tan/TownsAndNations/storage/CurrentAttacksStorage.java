package org.tan.TownsAndNations.storage;

import org.tan.TownsAndNations.DataClass.wars.AttackInvolved;
import org.tan.TownsAndNations.DataClass.wars.CurrentAttacks;

import java.util.HashMap;
import java.util.Map;

public class CurrentAttacksStorage {
    private static final Map<String, CurrentAttacks> attackStatusMap = new HashMap<>();

    public static void startAttack(AttackInvolved attackInvolved){
        String newID = getNextID();
        attackStatusMap.put(newID, new CurrentAttacks(newID, attackInvolved));
    }

    public static void remove(CurrentAttacks currentAttacks){
        attackStatusMap.remove(currentAttacks.getID());
    }

    private static String getNextID(){
        int ID = 0;
        while(attackStatusMap.containsKey("A"+ID)){
            ID++;
        }
        return "A"+ID;
    }

    public static CurrentAttacks get(String ID) {
        System.out.println(attackStatusMap.get(ID));
        return attackStatusMap.get(ID);
    }
}
