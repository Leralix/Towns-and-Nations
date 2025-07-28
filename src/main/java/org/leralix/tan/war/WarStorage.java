package org.leralix.tan.war;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.typeadapter.WargoalTypeAdapter;
import org.leralix.tan.war.legacy.wargoals.WarGoal;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarStorage {

    private static Map<String, War> warMap = new HashMap<>();


    public static War newWar(TerritoryData attackingTerritory, TerritoryData defendingTerritory) {

        String newID = getNewID();
        War newWar = new War(newID, attackingTerritory, defendingTerritory);
        add(newWar);
        return newWar;
    }

    private static void add(War plannedAttack) {
        warMap.put(plannedAttack.getID(), plannedAttack);
        save();
    }

    public static void remove(War plannedAttack) {
        warMap.remove(plannedAttack.getID());
    }

    private static String getNewID(){
        int ID = 0;
        while(warMap.containsKey("W"+ID)){
            ID++;
        }
        return "W"+ID;
    }

    public static Collection<War> getWars() {
        return warMap.values();
    }

    public static War get(String warID) {
        return warMap.get(warID);
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
            warMap = gson.fromJson(reader, type);
            for(War plannedAttack : warMap.values()){
                warMap.put(plannedAttack.getID(), plannedAttack);
            }
        }
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
        gson.toJson(warMap, writer);
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
        for(War plannedAttack : getWars()){
            if(plannedAttack.isMainAttacker(territoryData) || plannedAttack.isMainDefender(territoryData))
                plannedAttack.endWar();
        }
    }

    public static List<War> getWarsOfTerritory(TerritoryData territoryData) {
        return warMap.values().stream()
                .filter(war -> war.isMainAttacker(territoryData) || war.isMainDefender(territoryData))
                .toList();
    }

    public static boolean isTerritoryAtWarWith(TerritoryData mainTerritory, TerritoryData territoryData) {
        for(War war : getWarsOfTerritory(mainTerritory)){
            if(war.isMainAttacker(territoryData) || war.isMainDefender(territoryData)){
                return true;
            }
        }
        return false;
    }
}
