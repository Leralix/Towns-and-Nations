package org.tan.TownsAndNations.storage.DataStorage;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.tan.TownsAndNations.DataClass.AttackData;
import org.tan.TownsAndNations.DataClass.CreateAttackData;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.TownsAndNations;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AttackDataStorage {
    private static Map<String, AttackData> warDataMapWithTerritoryKey = new HashMap<>();
    private static Map<String, AttackData> warDataMapWithWarKey = new HashMap<>();

    public static void newWar(String warName, ITerritoryData defendingTerritory, ITerritoryData attackingTerritory, CreateAttackData createAttackData){

        String newID = getNewID();
        long deltaDateTime = createAttackData.getDeltaDateTime();
        AttackData attackData = new AttackData(newID,warName, defendingTerritory, attackingTerritory, deltaDateTime);
        add(attackData);
        save();
    }

    private static void add(AttackData attackData) {
        warDataMapWithTerritoryKey.put(attackData.getMainDefender().getID(), attackData);
        warDataMapWithWarKey.put(attackData.getID(), attackData);
    }

    public static void remove(AttackData attackData) {
        attackData.remove();
        warDataMapWithTerritoryKey.remove(attackData.getMainDefender().getID());
        warDataMapWithWarKey.remove(attackData.getID());
    }

    private static void setupAllAttacks(){
        for(AttackData attackData : warDataMapWithTerritoryKey.values()){
            attackData.setUpStartOfAttack();
        }
    }

    private static String getNewID(){
        int ID = 0;
        while(warDataMapWithTerritoryKey.containsKey("W"+ID)){
            ID++;
        }
        return "W"+ID;
    }

    public static Collection<AttackData> getWars() {
        return warDataMapWithTerritoryKey.values();
    }

    public static AttackData getAttackFromID(String warID) {
        return warDataMapWithWarKey.get(warID);
    }
    public static AttackData getAttackFromTerritoryID(String defendingTerritoryID) {
        return warDataMapWithTerritoryKey.get(defendingTerritoryID);
    }

    public static void load(){

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Wars.json");
        if (file.exists()){
            Reader reader;
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Type type = new TypeToken<HashMap<String, AttackData>>() {}.getType();
            warDataMapWithTerritoryKey = gson.fromJson(reader, type);
            for(AttackData attackData : warDataMapWithTerritoryKey.values()){
                warDataMapWithWarKey.put(attackData.getID(), attackData);
            }
        }
        setupAllAttacks();
    }

    public static void save() {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Wars.json");
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
        gson.toJson(warDataMapWithTerritoryKey, writer);
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
}
