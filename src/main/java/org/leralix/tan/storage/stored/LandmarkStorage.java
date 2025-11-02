package org.leralix.tan.storage.stored;

import com.google.gson.GsonBuilder;
import org.bukkit.Location;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.territory.TerritoryData;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.List;

public class LandmarkStorage extends DatabaseStorage<Landmark> {

    private static final String TABLE_NAME = "tan_landmarks";
    private int newLandmarkID;

    private static LandmarkStorage instance;

    private LandmarkStorage() {
        super(TABLE_NAME,
                Landmark.class,
                new GsonBuilder()
                        .setPrettyPrinting()
                        .create());
        loadNextLandmarkID();
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

    private void loadNextLandmarkID() {
        int ID = 0;
        for (String ids: getAll().keySet()) {
            int newID =  Integer.parseInt(ids.substring(1));
            if(newID > ID)
                ID = newID;
        }
        newLandmarkID = ID+1;
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
        return landmark;
    }

    public List<Landmark> getLandmarkOf(TerritoryData territoryData){
        return getAll().values().stream()
                .filter(landmark -> landmark.isOwnedBy(territoryData))
                .toList();
    }

    public void generateAllResources(){
        for (Landmark landmark : getAll().values()) {
            landmark.generateResources();
        }
    }

    @Override
    public void reset() {
        instance = null;
    }
}
