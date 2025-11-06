package org.leralix.tan.dataclass;

import java.util.*;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

public class RelationData {

  private final Map<TownRelation, List<String>> townRelations = new LinkedHashMap<>();

  public RelationData() {
    this.townRelations.put(TownRelation.WAR, new ArrayList<>());
    this.townRelations.put(TownRelation.EMBARGO, new ArrayList<>());
    this.townRelations.put(TownRelation.NON_AGGRESSION, new ArrayList<>());
    this.townRelations.put(TownRelation.ALLIANCE, new ArrayList<>());
  }

  public void setRelation(TownRelation relation, TerritoryData territoryID) {
    setRelation(relation, territoryID.getID());
  }

  public void setRelation(TownRelation relation, String territoryID) {
    removeAllRelationWith(territoryID);
    if (!townRelations.containsKey(relation)) return;
    addRelation(relation, territoryID);
  }

  public void addRelation(TownRelation relation, String townID) {
    townRelations.get(relation).add(townID);
  }

  public List<String> getTerritoriesIDWithRelation(TownRelation relation) {
    return townRelations.get(relation);
  }

  public Map<TownRelation, List<String>> getAll() {
    return townRelations;
  }

  public TownRelation getRelationWith(String territoryID) {
    for (Map.Entry<TownRelation, List<String>> entry : townRelations.entrySet()) {
      TownRelation relation = entry.getKey();
      List<String> list = entry.getValue();

      for (String townUUID : list) {
        if (territoryID.equals(townUUID)) {
          return relation;
        }
      }
    }
    return TownRelation.NEUTRAL;
  }

  public TownRelation getRelationWith(TerritoryData territory) {
    return getRelationWith(territory.getID());
  }

  public void cleanAll(TerritoryData territoryData) {

    for (TownRelation relation : TownRelation.values()) {

      Collection<String> territories = townRelations.get(relation);
      if (territories == null) {
        continue;
      }

      for (String otherTerritory : territories) {

        TerritoryData otherTerritoryData = TerritoryUtil.getTerritory(otherTerritory);
        if (otherTerritoryData == null) continue;

        otherTerritoryData.getRelations().removeAllRelationWith(territoryData.getID());
        otherTerritoryData.broadcastMessageWithSound(
            Lang.WARNING_OTHER_TOWN_HAS_BEEN_DELETED.get(
                territoryData.getBaseColoredName(), relation.getColoredName(Lang.getServerLang())),
            SoundEnum.MINOR_BAD);
      }
    }
  }

  public void removeAllRelationWith(String townID) {
    for (List<String> territories : townRelations.values()) {
      territories.remove(townID);
    }
  }
}
