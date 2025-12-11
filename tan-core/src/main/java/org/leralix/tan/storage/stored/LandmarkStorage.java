package org.leralix.tan.storage.stored;

import com.google.gson.GsonBuilder;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.bukkit.Location;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.territory.TerritoryData;

public class LandmarkStorage extends DatabaseStorage<Landmark> {

  private static final String TABLE_NAME = "tan_landmarks";
  private int newLandmarkID;

  private static LandmarkStorage instance;

  private LandmarkStorage() {
    super(TABLE_NAME, Landmark.class, new GsonBuilder().setPrettyPrinting().create());
    loadNextLandmarkID();
  }

  @Override
  protected void createTable() {
    String createTableSQL =
        """
            CREATE TABLE IF NOT EXISTS %s (
                id VARCHAR(255) PRIMARY KEY,
                data TEXT NOT NULL
            )
        """
            .formatted(TABLE_NAME);

    try (Connection conn = getDatabase().getDataSource().getConnection();
        Statement stmt = conn.createStatement()) {
      TownsAndNations.getPlugin().getLogger().info("[TaN-DB] Creating table: " + TABLE_NAME);
      stmt.execute(createTableSQL);
      TownsAndNations.getPlugin()
          .getLogger()
          .info("[TaN-DB] Table " + TABLE_NAME + " created/verified successfully");
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe("Error creating table " + TABLE_NAME + ": " + e.getMessage());
    }
  }

  private void loadNextLandmarkID() {
    int ID = 0;
    for (String ids : getAllAsync().join().keySet()) {
      int newID = Integer.parseInt(ids.substring(1));
      if (newID > ID) ID = newID;
    }
    newLandmarkID = ID + 1;
  }

  public static LandmarkStorage getInstance() {
    if (instance == null) {
      instance = new LandmarkStorage();
    }
    return instance;
  }

  public static void setInstance(LandmarkStorage mockLandmarkStorage) {
    instance = mockLandmarkStorage;
  }

  public Landmark getSync(String id) {
    try {
      return get(id).join();
    } catch (Exception e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("Error getting landmark data synchronously: " + e.getMessage());
      return null;
    }
  }

  public Landmark addLandmark(Location position) {
    Vector3D vector3D = new Vector3D(position);
    String landmarkID = "L" + newLandmarkID;
    Landmark landmark = new Landmark(landmarkID, vector3D);
    put(landmarkID, landmark);
    newLandmarkID++;
    NewClaimedChunkStorage.getInstance().claimLandmarkChunk(position.getChunk(), landmarkID);
    return landmark;
  }

  public List<Landmark> getLandmarkOf(TerritoryData territoryData) {
    return getAllAsync().join().values().stream()
        .filter(landmark -> landmark.isOwnedBy(territoryData))
        .toList();
  }

  public void generateAllResources() {
    for (Landmark landmark : getAllAsync().join().values()) {
      landmark.generateResources();
    }
  }

  @Override
  public void reset() {
    instance = null;
  }
}
