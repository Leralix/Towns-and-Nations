package org.leralix.tan.storage.stored.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.WarStartInternalEvent;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.storage.typeadapter.AttackResultAdapter;
import org.leralix.tan.storage.typeadapter.WargoalTypeAdapter;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.war.War;
import org.leralix.tan.war.WarData;
import org.leralix.tan.war.info.AttackResult;
import org.leralix.tan.war.info.WarRole;
import org.leralix.tan.war.wargoals.WarGoal;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class WarDataStorage extends JsonStorage<War> implements WarStorage {

    public WarDataStorage(){
        super("TAN - Wars.json",
                new TypeToken<HashMap<String, WarData>>() {}.getType(),
                new GsonBuilder()
                        .registerTypeAdapter(WarGoal.class, new WargoalTypeAdapter())
                        .registerTypeAdapter(AttackResult.class, new AttackResultAdapter())
                        .setPrettyPrinting().
                        create()
        );
    }

    @Override
    public War newWar(Territory attackingTerritory, Territory defendingTerritory) {
        String newID = "W" + getNextID();
        War newWar = new WarData(
                newID,
                attackingTerritory,
                defendingTerritory
        );
        put(newID, newWar);

        // All defender allies set their relation to war with attacking territory
        List<Territory> alliedTerritories = defendingTerritory.getRelations().getTerritoriesWithRelation(TownRelation.ALLIANCE);
        for(var alliedTerritory : alliedTerritories){
            TerritoryUtil.setRelation(alliedTerritory, attackingTerritory, TownRelation.WAR);
        }

        // If simple war mode is enabled, start an attack as soon as the war is declared and don't stop it until surrender
        if(Constants.isSimpleWarMode()){
            newWar.createPlannedAttack(WarRole.MAIN_ATTACKER, 0, -1);
        }

        EventManager.getInstance().callEvent(new WarStartInternalEvent(attackingTerritory, defendingTerritory));
        return newWar;
    }

    @Override
    public void remove(War plannedAttack) {
        delete(plannedAttack.getID());
    }

    @Override
    public Collection<War> getAllWars() {
        return dataMap.values();
    }
}
