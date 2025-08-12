package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.typeadapter.WargoalTypeAdapter;
import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.War;
import org.leralix.tan.war.legacy.CreateAttackData;
import org.leralix.tan.war.legacy.wargoals.WarGoal;

import java.util.HashMap;

public class PlannedAttackStorage extends JsonStorage<PlannedAttack> {

    private static PlannedAttackStorage instance;

    protected PlannedAttackStorage() {
        super("TAN - Planned_wars.json",
                new TypeToken<HashMap<String, PlannedAttack>>(){}.getType(),
                new GsonBuilder()
                        .registerTypeAdapter(WarGoal.class, new WargoalTypeAdapter())
                        .setPrettyPrinting()
                        .create());
    }

    public static PlannedAttackStorage getInstance(){
        if(instance == null){
            instance = new PlannedAttackStorage();
        }
        return instance;
    }

    public PlannedAttack newAttack(CreateAttackData createAttackData) {
        String newID = getNewID();
        long deltaDateTime = createAttackData.getSelectedTime();
        PlannedAttack plannedAttack = new PlannedAttack(newID, createAttackData, deltaDateTime);
        put(newID, plannedAttack);
        return plannedAttack;
    }

    private void setupAllAttacks() {
        for (PlannedAttack plannedAttack : getAll().values()) {
            plannedAttack.setUpStartOfAttack();
        }
    }

    private String getNewID() {
        int ID = 0;
        while (dataMap.containsKey("W" + ID)) {
            ID++;
        }
        return "W" + ID;
    }

    public void territoryDeleted(TerritoryData territoryData) {
        for (PlannedAttack plannedAttack : getAll().values()) {
            War war = plannedAttack.getWar();
            if (war.isMainAttacker(territoryData) || war.isMainDefender(territoryData))
                plannedAttack.end();
        }
    }

    public void delete(PlannedAttack plannedAttack){
        delete(plannedAttack.getID());
    }
}
