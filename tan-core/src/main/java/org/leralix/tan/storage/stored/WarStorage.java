package org.leralix.tan.storage.stored;

import com.google.gson.GsonBuilder;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.typeadapter.WargoalTypeAdapter;
import org.leralix.tan.wars.War;
import org.leralix.tan.wars.legacy.wargoals.WarGoal;

public class WarStorage extends DatabaseStorage<War> {

  private static final String TABLE_NAME = "tan_wars";
  private static WarStorage instance;

  private WarStorage() {
    super(
        TABLE_NAME,
        War.class,
        new GsonBuilder()
            .registerTypeAdapter(WarGoal.class, new WargoalTypeAdapter())
            .setPrettyPrinting()
            .create());
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

  public War newWar(TerritoryData attackingTerritory, TerritoryData defendingTerritory) {
    String newID = getNewID();
    War newWar = new War(newID, attackingTerritory, defendingTerritory);
    add(newWar);
    return newWar;
  }

  public static WarStorage getInstance() {
    if (instance == null) {
      instance = new WarStorage();
    }
    return instance;
  }

  private void add(War plannedAttack) {
    put(plannedAttack.getID(), plannedAttack);
  }

  public void remove(War plannedAttack) {
    delete(plannedAttack.getID());
  }

  private String getNewID() {
    int ID = 0;
    while (exists("W" + ID)) {
      ID++;
    }
    return "W" + ID;
  }

  public void territoryDeleted(TerritoryData territoryData) {
    for (War plannedAttack : getAll().values()) {
      if (plannedAttack.isMainAttacker(territoryData)
          || plannedAttack.isMainDefender(territoryData)) plannedAttack.endWar();
    }
  }

  public List<War> getWarsOfTerritory(TerritoryData territoryData) {
    return getAll().values().stream()
        .filter(war -> war.isMainAttacker(territoryData) || war.isMainDefender(territoryData))
        .toList();
  }

  public boolean isTerritoryAtWarWith(TerritoryData mainTerritory, TerritoryData territoryData) {
    for (War war : getWarsOfTerritory(mainTerritory)) {
      if (war.isMainAttacker(territoryData) || war.isMainDefender(territoryData)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void reset() {
    instance = null;
  }
}
