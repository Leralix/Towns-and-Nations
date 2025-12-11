package org.leralix.tan.storage.stored;

import com.google.gson.GsonBuilder;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.HashMap;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ActiveTruce;
import org.leralix.tan.dataclass.territory.TerritoryData;

public class TruceStorage extends DatabaseStorage<HashMap<String, ActiveTruce>> {

  private static final String TABLE_NAME = "tan_truces";
  private static TruceStorage instance;

  protected TruceStorage() {
    super(
        TABLE_NAME,
        (Class<HashMap<String, ActiveTruce>>) (Class<?>) HashMap.class,
        new GsonBuilder().setPrettyPrinting().create());
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
      stmt.execute(createTableSQL);
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe("Error creating table " + TABLE_NAME + ": " + e.getMessage());
    }
  }

  public static TruceStorage getInstance() {
    if (instance == null) instance = new TruceStorage();
    return instance;
  }

  @Override
  public void reset() {
    instance = null;
  }

  public void add(ActiveTruce activeTruce) {
    String id1 = activeTruce.getTerritoryID1();
    String id2 = activeTruce.getTerritoryID2();

    HashMap<String, ActiveTruce> map1 = get(id1).join();
    if (map1 == null) {
      map1 = new HashMap<>();
    }
    map1.put(id2, activeTruce);
    putWithInvalidation(id1, map1).join(); // ✅ SYNC-FIX: Use putWithInvalidation

    HashMap<String, ActiveTruce> map2 = get(id2).join();
    if (map2 == null) {
      map2 = new HashMap<>();
    }
    map2.put(id1, activeTruce);
    putWithInvalidation(id2, map2).join(); // ✅ SYNC-FIX: Use putWithInvalidation
  }

  public long getRemainingTruce(TerritoryData territoryData1, TerritoryData territoryData2) {
    String id1 = territoryData1.getID();
    String id2 = territoryData2.getID();

    HashMap<String, ActiveTruce> truceMap = get(id1).join();
    if (truceMap == null) {
      return 0;
    }

    ActiveTruce truce = truceMap.get(id2);
    if (truce == null) {
      return 0;
    }

    long remaining = truce.getEndOfTruce() - Instant.now().toEpochMilli();
    long remainingInHours = remaining / 1000 / 60 / 60;

    return Math.max(0, remainingInHours);
  }
}
