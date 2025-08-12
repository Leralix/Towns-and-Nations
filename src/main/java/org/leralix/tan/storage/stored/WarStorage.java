package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.typeadapter.WargoalTypeAdapter;
import org.leralix.tan.war.War;
import org.leralix.tan.war.legacy.wargoals.WarGoal;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarStorage {

    private Map<String, War> warMap;

    private static WarStorage instance;

    private WarStorage(){
        warMap = new HashMap<>();
        load();
    }

    public War newWar(TerritoryData attackingTerritory, TerritoryData defendingTerritory) {
        String newID = getNewID();
        War newWar = new War(newID, attackingTerritory, defendingTerritory);
        add(newWar);
        return newWar;
    }

    public static WarStorage getInstance() {
        if(instance == null) {
            instance = new WarStorage();
        }
        return instance;
    }

    private void add(War plannedAttack) {
        warMap.put(plannedAttack.getID(), plannedAttack);
        save();
    }

    public void remove(War plannedAttack) {
        warMap.remove(plannedAttack.getID());
    }

    private String getNewID(){
        int ID = 0;
        while(warMap.containsKey("W"+ID)){
            ID++;
        }
        return "W"+ID;
    }

    public Collection<War> getWars() {
        return warMap.values();
    }

    public War get(String warID) {
        return warMap.get(warID);
    }

    public void load(){

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(WarGoal.class, new WargoalTypeAdapter())
                .setPrettyPrinting().
                create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/storage/json/Wars.json");
        if (file.exists()){
            Reader reader;
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Type type = new TypeToken<HashMap<String, War>>() {}.getType();
            warMap = gson.fromJson(reader, type);
            for(War plannedAttack : warMap.values()){
                warMap.put(plannedAttack.getID(), plannedAttack);
            }
        }
    }

    public void save() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(WarGoal.class, new WargoalTypeAdapter())
                .setPrettyPrinting()
                .create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/storage/json/Wars.json");
        try {
            Files.createDirectories(file.getParentFile().toPath());
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

    public void territoryDeleted(TerritoryData territoryData) {
        for(War plannedAttack : getWars()){
            if(plannedAttack.isMainAttacker(territoryData) || plannedAttack.isMainDefender(territoryData))
                plannedAttack.endWar();
        }
    }

    public List<War> getWarsOfTerritory(TerritoryData territoryData) {
        return warMap.values().stream()
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
}
