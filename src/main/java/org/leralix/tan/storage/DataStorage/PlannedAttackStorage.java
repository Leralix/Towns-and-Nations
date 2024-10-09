package org.leralix.tan.storage.DataStorage;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.dataclass.wars.CreateAttackData;
import org.leralix.tan.dataclass.wars.wargoals.WarGoal;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.TypeAdapter.WargoalTypeAdapter;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlannedAttackStorage {
    private static Map<String, PlannedAttack> warDataMapWithWarKey = new HashMap<>();


    public static void newWar(CreateAttackData createAttackData){

        String newID = getNewID();
        long deltaDateTime = createAttackData.getDeltaDateTime();
        PlannedAttack plannedAttack = new PlannedAttack(newID, createAttackData, deltaDateTime);
        add(plannedAttack);
        save();
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

    public static void territoryDeleted(ITerritoryData territoryData) {
        for(PlannedAttack plannedAttack : getWars()){
            if(plannedAttack.isMainAttacker(territoryData) || plannedAttack.isMainDefender(territoryData))
                plannedAttack.remove();
        }
    }
}
