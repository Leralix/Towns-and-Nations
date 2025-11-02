package org.leralix.tan.storage.stored;

import com.google.gson.GsonBuilder;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.typeadapter.WargoalTypeAdapter;
import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.War;
import org.leralix.tan.war.legacy.CreateAttackData;
import org.leralix.tan.war.legacy.wargoals.WarGoal;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class PlannedAttackStorage extends DatabaseStorage<PlannedAttack> {

    private static final String TABLE_NAME = "tan_planned_attacks";
    private static PlannedAttackStorage instance;

    protected PlannedAttackStorage() {
        super(TABLE_NAME,
                PlannedAttack.class,
                new GsonBuilder()
                        .registerTypeAdapter(WarGoal.class, new WargoalTypeAdapter())
                        .setPrettyPrinting()
                        .create());
    }

    @Override
    protected void createTable() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS %s (
                id VARCHAR(255) PRIMARY KEY,
                data TEXT NOT NULL
            )
        """.formatted(TABLE_NAME);

        try (Connection conn = getDatabase().getDataSource().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            TownsAndNations.getPlugin().getLogger().severe(
                "Error creating table " + TABLE_NAME + ": " + e.getMessage()
            );
        }
    }

    public static PlannedAttackStorage getInstance(){
        if(instance == null){
            instance = new PlannedAttackStorage();
        }
        return instance;
    }

    public PlannedAttack newAttack(CreateAttackData createAttackData) {
        String newID = getNewID();
        PlannedAttack plannedAttack = new PlannedAttack(newID, createAttackData);
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
        while (exists("W" + ID)) {
            ID++;
        }
        return "W" + ID;
    }

    public synchronized void territoryDeleted(TerritoryData territoryData) {
        for (PlannedAttack plannedAttack : getAll().values()) {
            War war = plannedAttack.getWar();
            if (war == null) {
                continue;
            }
            if (war.isMainAttacker(territoryData) || war.isMainDefender(territoryData)) {
                plannedAttack.end();
                //iterator.remove();
            }
        }
    }


    public void delete(PlannedAttack plannedAttack){
        delete(plannedAttack.getID());
    }

    @Override
    public void reset() {
        instance = null;
    }
}
