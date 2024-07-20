package org.tan.TownsAndNations.storage;

import org.tan.TownsAndNations.DataClass.AttackData;
import org.tan.TownsAndNations.DataClass.CreateAttackData;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AttackDataStorage {
    private static final Map<String, AttackData> warDataMap = new HashMap<>();

    public static void newWar(String warName, ITerritoryData defendingTerritory, ITerritoryData attackingTerritory, CreateAttackData createAttackData){

        AttackData attackData = new AttackData(getNewID(),warName, defendingTerritory, attackingTerritory, createAttackData.getDeltaDateTime());
        warDataMap.put(defendingTerritory.getID(), attackData);
    }


    private static String getNewID(){
        int ID = 0;
        while(warDataMap.containsKey("W"+ID)){
            ID++;
        }
        return "W"+ID;
    }

    private static Collection<AttackData> getWars() {
        return warDataMap.values();
    }
}
