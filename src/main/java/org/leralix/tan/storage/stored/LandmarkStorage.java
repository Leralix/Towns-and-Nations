package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.bukkit.Location;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.dataclass.Landmark;

import java.util.HashMap;

public class LandmarkStorage extends JsonStorage<Landmark> {

    private int newLandmarkID;

    private static LandmarkStorage instance;

    private LandmarkStorage() {
        super("TAN - Landmarks.json",
                new TypeToken<HashMap<String, Landmark>>() {}.getType(),
                new GsonBuilder()
                        .setPrettyPrinting()
                        .create());
    }

    public static LandmarkStorage getInstance(){
        if(instance == null) {
            instance = new LandmarkStorage();
        }
        return instance;
    }

    public static void setInstance(LandmarkStorage mockLandmarkStorage) {
        instance = mockLandmarkStorage;
    }


    public Landmark addLandmark(Location position){
        Vector3D vector3D = new Vector3D(position);
        String landmarkID = "L" + newLandmarkID;
        Landmark landmark = new Landmark(landmarkID,vector3D);
        put(landmarkID, landmark);
        newLandmarkID++;
        NewClaimedChunkStorage.getInstance().claimLandmarkChunk(position.getChunk(), landmarkID);
        save();
        return landmark;
    }

    public void generateAllResources(){
        for (Landmark landmark : getAll().values()) {
            landmark.generateResources();
        }
    }

    @Override
    public void load(){
        super.load();

        int ID = 0;
        for (String ids: getAll().keySet()) {
            int newID =  Integer.parseInt(ids.substring(1));
            if(newID > ID)
                ID = newID;
        }
        newLandmarkID = ID+1;
    }
}
