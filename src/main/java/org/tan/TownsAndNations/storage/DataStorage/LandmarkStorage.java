package org.tan.TownsAndNations.storage.DataStorage;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Location;
import org.tan.TownsAndNations.DataClass.Landmark;
import org.tan.TownsAndNations.DataClass.RegionData;
import org.tan.TownsAndNations.DataClass.Vector3D;
import org.tan.TownsAndNations.TownsAndNations;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LandmarkStorage {

    private static Map<String, Landmark> landMarkMap = new HashMap<>();
    private static int newLandmarkID = 1;


    public static Landmark get(String landmarkID){
        return landMarkMap.get(landmarkID);
    }

    public static void addLandmark(Location position){
        Vector3D vector3D = new Vector3D(position);
        String landmarkID = "L" + newLandmarkID;
        Landmark landmark = new Landmark(landmarkID,vector3D);
        landMarkMap.put(landmarkID, landmark);
        newLandmarkID++;
        NewClaimedChunkStorage.claimLandmarkChunk(position.getChunk(), landmarkID);
        save();
    }

    public static boolean vectorAlreadyFilled(Vector3D vector3D){
        for(Landmark landmark : getList()){
            if(landmark.getPosition().equals(vector3D))
                return true;
        }
        return false;
    }

    public static Collection<Landmark> getList(){
        return landMarkMap.values();
    }

    public static void generateAllRessources(){
        for (Landmark landmark : getList()){
            landmark.generateRessources();
        }
    }

    public static void load(){

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Landmarks.json");
        if (file.exists()){
            Reader reader;
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Type type = new TypeToken<HashMap<String, Landmark>>() {}.getType();
            landMarkMap = gson.fromJson(reader, type);

            int ID = 0;
            for (String ids: landMarkMap.keySet()) {
                int newID =  Integer.parseInt(ids.substring(1));
                if(newID > ID)
                    ID = newID;
            }
            newLandmarkID = ID+1;

        }

    }

    public static void save() {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Landmarks.json");
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
        gson.toJson(landMarkMap, writer);
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

    public static Map<String, Landmark> getLandMarkMap() {
        return landMarkMap;
    }

}
