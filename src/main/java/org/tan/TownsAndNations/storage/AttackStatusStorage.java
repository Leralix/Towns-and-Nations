package org.tan.TownsAndNations.storage;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.tan.TownsAndNations.DataClass.AttackData;
import org.tan.TownsAndNations.DataClass.CreateAttackData;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.DataClass.wars.AttackStatus;
import org.tan.TownsAndNations.TownsAndNations;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AttackStatusStorage {
    private static final Map<String, AttackStatus> attackStatusMap = new HashMap<>();
    public static void startAttack(AttackData attackData){
        String newID = getNextID();
        attackStatusMap.put(newID, new AttackStatus(newID, attackData));
    }

    public static void add(AttackStatus attackStatus){
        attackStatusMap.put(attackStatus.getID(), attackStatus);
    }

    private static String getNextID(){
        int ID = 0;
        while(attackStatusMap.containsKey("A"+ID)){
            ID++;
        }
        return "A"+ID;
    }

    public static AttackStatus get(String warID) {
        return attackStatusMap.get(warID);
    }
}
