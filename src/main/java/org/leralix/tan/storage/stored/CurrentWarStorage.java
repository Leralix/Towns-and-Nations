package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.typeadapter.WargoalTypeAdapter;
import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.War;
import org.leralix.tan.war.legacy.CreateAttackData;
import org.leralix.tan.war.legacy.wargoals.WarGoal;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CurrentWarStorage {
    private static Map<String, PlannedAttack> warDataMapWithWarKey = new HashMap<>();


    public static PlannedAttack newAttack(CreateAttackData createAttackData){
        String newID = getNewID();
        long deltaDateTime = createAttackData.getSelectedTime();
        PlannedAttack plannedAttack = new PlannedAttack(newID, createAttackData, deltaDateTime);
        add(plannedAttack);
        save();
        return plannedAttack;
    }

    private static void add(PlannedAttack plannedAttack) {
        warDataMapWithWarKey.put(plannedAttack.getID(), plannedAttack);
    }

    public static void remove(PlannedAttack plannedAttack) {
        warDataMapWithWarKey.remove(plannedAttack.getID());
    }

    private static void setupAllAttacks(){
        for(PlannedAttack plannedAttack : warDataMapWithWarKey.values()){
            plannedAttack.setUpStartOfAttack();
        }
    }

    private static String getNewID(){
        int ID = 0;
        while(warDataMapWithWarKey.containsKey("W"+ID)){
            ID++;
        }
        return "W"+ID;
    }

    public static Collection<PlannedAttack> getWars() {
        return warDataMapWithWarKey.values();
    }

    public static PlannedAttack get(String warID) {
        return warDataMapWithWarKey.get(warID);
    }

    public static void load(){

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(WarGoal.class, new WargoalTypeAdapter())
                .setPrettyPrinting().
                create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Planned_wars.json");
        if (file.exists()){
            Reader reader;
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Type type = new TypeToken<HashMap<String, PlannedAttack>>() {}.getType();
            warDataMapWithWarKey = gson.fromJson(reader, type);
            for(PlannedAttack plannedAttack : warDataMapWithWarKey.values()){
                warDataMapWithWarKey.put(plannedAttack.getID(), plannedAttack);
            }
        }
        setupAllAttacks();
    }

    public static void save() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(WarGoal.class, new WargoalTypeAdapter())
                .setPrettyPrinting()
                .create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Planned_wars.json");
        file.getParentFile().mkdir();

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Writer writer;
        try {
            writer = new FileWriter(file, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gson.toJson(warDataMapWithWarKey, writer);
        try {
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void territoryDeleted(TerritoryData territoryData) {
        for(PlannedAttack plannedAttack : getWars()){
            War war = plannedAttack.getWar();
            if(war.isMainAttacker(territoryData) || war.isMainDefender(territoryData))
                plannedAttack.end();
        }
    }
}
