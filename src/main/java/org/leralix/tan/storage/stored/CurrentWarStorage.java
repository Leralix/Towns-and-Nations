package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.typeadapter.WargoalTypeAdapter;
import org.leralix.tan.war.CurrentWar;
import org.leralix.tan.war.legacy.CreateAttackData;
import org.leralix.tan.war.legacy.wargoals.WarGoal;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CurrentWarStorage {
    private static Map<String, CurrentWar> warDataMapWithWarKey = new HashMap<>();


    public static CurrentWar newWar(CreateAttackData createAttackData){

        String newID = getNewID();
        long deltaDateTime = createAttackData.getDeltaDateTime();
        CurrentWar plannedAttack = new CurrentWar(newID, createAttackData, deltaDateTime);
        add(plannedAttack);
        save();
        return plannedAttack;
    }

    private static void add(CurrentWar plannedAttack) {
        warDataMapWithWarKey.put(plannedAttack.getID(), plannedAttack);
    }

    public static void remove(CurrentWar plannedAttack) {
        warDataMapWithWarKey.remove(plannedAttack.getID());
    }

    private static void setupAllAttacks(){
        for(CurrentWar plannedAttack : warDataMapWithWarKey.values()){
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

    public static Collection<CurrentWar> getWars() {
        return warDataMapWithWarKey.values();
    }

    public static CurrentWar get(String warID) {
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
            Type type = new TypeToken<HashMap<String, CurrentWar>>() {}.getType();
            warDataMapWithWarKey = gson.fromJson(reader, type);
            for(CurrentWar plannedAttack : warDataMapWithWarKey.values()){
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
        for(CurrentWar plannedAttack : getWars()){
            if(plannedAttack.isMainAttacker(territoryData) || plannedAttack.isMainDefender(territoryData))
                plannedAttack.endWar();
        }
    }
}
