package org.leralix.tan.storage.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.FortStorage;
import org.leralix.tan.war.fort.Fort;
import org.leralix.tan.war.fort.FortData;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FortDataStorage extends FortStorage {

    int newFortID;
    Map<String, FortData> forts;

    public FortDataStorage(){
        this.forts = new HashMap<>();
        this.newFortID = 0;
        load();
    }

    @Override
    public List<Fort> getOccupiedFort(TerritoryData territoryData) {
        List<Fort> res = new ArrayList<>();
        for(String fortID : territoryData.getOccupiedFortIds()) {
            FortData fort = forts.get(fortID);
            if (fort == null) {
                continue;
            }
            res.add(fort);
        }
        return res;
    }

    @Override
    public List<Fort> getOwnedFort(TerritoryData territoryData) {
        List<Fort> res = new ArrayList<>();
        for(String fortID : territoryData.getOwnedFortIDs()) {
            FortData fort = forts.get(fortID);
            if (fort == null) {
                continue;
            }
            res.add(fort);
        }
        return res;
    }

    @Override
    public List<Fort> getAllControlledFort(TerritoryData territoryData) {
        List<Fort> allForts = new ArrayList<>(getOccupiedFort(territoryData));

        for(Fort fort : getOwnedFort(territoryData)) {
            if(!fort.isOccupied()){
                allForts.add(fort);
            }
        }
        return allForts;
    }

    @Override
    public List<Fort> getForts() {
        return new ArrayList<>(forts.values());
    }

    @Override
    public Fort getFort(String fortID) {
        return forts.get(fortID);
    }

    @Override
    public Fort register(Vector3D position, TerritoryData owningTerritory) {
        FortData fort = new FortData("F" + newFortID, position, Lang.DEFAULT_FORT_NAME.get( newFortID), owningTerritory);
        forts.put(fort.getID(), fort);
        save();
        return fort;
    }

    @Override
    public void delete(String fortID) {
        Fort fort = getFort(fortID);
        if(fort == null) {
            return;
        }
        TerritoryData owner = fort.getOwner();
        owner.removeFort(fortID);
        forts.remove(fortID);
        save();
    }


    public void load(){

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/storage/json/Forts.json");
        if (file.exists()){
            Reader reader;
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Type type = new TypeToken<HashMap<String, FortData>>() {}.getType();
            forts = gson.fromJson(reader, type);

            int id = 0;
            for (String ids: forts.keySet()) {
                int newID =  Integer.parseInt(ids.substring(1));
                if(newID > id)
                    id = newID;
            }
            newFortID = id+1;
        }
    }

    @Override
    public void save() {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/storage/json/Forts.json");
        file.getParentFile().getParentFile().mkdir();
        file.getParentFile().mkdir();

        if (!file.exists()){
            try {
                if(!file.createNewFile()){
                    throw new FileNotFoundException("Could not create file: " + file.getAbsolutePath());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Writer writer;
        try {
            writer = new FileWriter(file, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gson.toJson(forts, writer);
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
