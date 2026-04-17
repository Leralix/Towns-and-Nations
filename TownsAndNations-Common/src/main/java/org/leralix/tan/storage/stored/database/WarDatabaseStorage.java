package org.leralix.tan.storage.stored.database;

import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.WarStartInternalEvent;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.constants.database.RedisConfig;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.war.War;
import org.leralix.tan.war.WarData;
import org.leralix.tan.war.WarDatabase;
import org.leralix.tan.war.info.WarRole;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class WarDatabaseStorage extends DatabaseStorage<WarDatabase, War> implements WarStorage {


    public WarDatabaseStorage(RedisConfig redisConfig) {
        super(new WarDbManager(redisConfig));
    }

    private int getNextID() {
        int nextTownID = 0;
        for (War landmark : getAll().values()) {
            String idString = landmark.getID();
            if (idString != null && idString.length() >= 2) {
                String suffix = idString.substring(1);
                boolean isNumeric = suffix.chars().allMatch(Character::isDigit);
                if (isNumeric) {
                    int newID = Integer.parseInt(suffix);
                    if (newID >= nextTownID) {
                        nextTownID = newID + 1;
                    }
                }
            }
        }
        return nextTownID;
    }

    @Override
    public War newWar(Territory attackingTerritory, Territory defendingTerritory) {
        String newID = "W" + getNextID();
        War newWar = new WarData(
                newID,
                attackingTerritory,
                defendingTerritory
        );
        databaseManager.save(newWar);

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

    }

    @Override
    public Collection<War> getAllWars() {
        return List.of();
    }

    @Override
    public Map<String, War> getAll() {
        return databaseManager.getMap();
    }

    @Override
    public War get(String warID) {
        return getOrLoad(warID, this::load);
    }

    private WarDatabase load(String id) {
        War data = databaseManager.load(id);
        if(data == null){
            return null;
        }
        return new WarDatabase(data, databaseManager );
    }

    @Override
    public void save() {
        // No need to implement since all operations are directly done on the database
    }
}
