package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.WarStartInternalEvent;
import org.leralix.tan.storage.typeadapter.AttackResultAdapter;
import org.leralix.tan.storage.typeadapter.WargoalTypeAdapter;
import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.War;
import org.leralix.tan.war.info.AttackResult;
import org.leralix.tan.war.legacy.wargoals.WarGoal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class WarStorage extends JsonStorage<War>{


    private static WarStorage instance;

    private WarStorage(){
        super("TAN - Wars.json",
                new TypeToken<HashMap<String, War>>() {}.getType(),
                new GsonBuilder()
                        .registerTypeAdapter(WarGoal.class, new WargoalTypeAdapter())
                        .registerTypeAdapter(AttackResult.class, new AttackResultAdapter())
                        .setPrettyPrinting().
                        create()
        );
    }

    /**
     * Since planned attacks have a start date, it is necessary to check if any should
     * have started while the server was offline.
     */
    void updateAttacks() {
        for (War war : dataMap.values()){
            for (PlannedAttack plannedAttack : war.getPlannedAttacks()) {
                plannedAttack.updateStatus();
            }
        }
    }

    public War newWar(TerritoryData attackingTerritory, TerritoryData defendingTerritory) {
        String newID = getNewID();
        War newWar = new War(
                newID,
                attackingTerritory,
                defendingTerritory,
                defendingTerritory.getRelations().getTerritoriesIDWithRelation(TownRelation.ALLIANCE)
        );
        add(newWar);
        EventManager.getInstance().callEvent(new WarStartInternalEvent(attackingTerritory, defendingTerritory));
        return newWar;
    }

    public static WarStorage getInstance() {
        if(instance == null) {
            instance = new WarStorage();
            instance.updateAttacks();
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

    public Collection<PlannedAttack> getAllAttacks() {
        List<PlannedAttack> res = new ArrayList<>();
        for(War war : getAll().values()){
            res.addAll(war.getPlannedAttacks());
        }
        return res;
    }
}
