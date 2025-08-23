package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.typeadapter.WargoalTypeAdapter;
import org.leralix.tan.war.War;
import org.leralix.tan.war.legacy.wargoals.WarGoal;

import java.util.HashMap;
import java.util.List;

public class WarStorage extends JsonStorage<War>{


    private static WarStorage instance;

    private WarStorage(){
        super("Wars.json",
                new TypeToken<HashMap<String, War>>() {}.getType(),
                new GsonBuilder()
                        .registerTypeAdapter(WarGoal.class, new WargoalTypeAdapter())
                        .setPrettyPrinting().
                        create()
        );
    }

    public War newWar(TerritoryData attackingTerritory, TerritoryData defendingTerritory) {
        String newID = getNewID();
        War newWar = new War(newID, attackingTerritory, defendingTerritory);
        add(newWar);
        return newWar;
    }

    public static WarStorage getInstance() {
        if(instance == null) {
            instance = new WarStorage();
        }
        return instance;
    }

    private void add(War plannedAttack) {
        put(plannedAttack.getID(), plannedAttack);
    }

    public void remove(War plannedAttack) {
        delete(plannedAttack.getID());
    }

    private String getNewID(){
        int ID = 0;
        while(dataMap.containsKey("W"+ID)){
            ID++;
        }
        return "W"+ID;
    }


    public void territoryDeleted(TerritoryData territoryData) {
        for(War plannedAttack : dataMap.values()){
            if(plannedAttack.isMainAttacker(territoryData) || plannedAttack.isMainDefender(territoryData))
                plannedAttack.endWar();
        }
    }

    public List<War> getWarsOfTerritory(TerritoryData territoryData) {
        return dataMap.values().stream()
                .filter(war -> war.isMainAttacker(territoryData) || war.isMainDefender(territoryData))
                .toList();
    }

    public boolean isTerritoryAtWarWith(TerritoryData mainTerritory, TerritoryData territoryData) {
        for(War war : getWarsOfTerritory(mainTerritory)){
            if(war.isMainAttacker(territoryData) || war.isMainDefender(territoryData)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void reset() {
        instance = null;
    }
}
