package org.leralix.tan.data.territory.relation;

import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

import java.util.*;

public class RelationData {

    private final Map<TownRelation, List<String>> townRelations = new LinkedHashMap<>();

    public RelationData() {
        this.townRelations.put(TownRelation.WAR, new ArrayList<>());
        this.townRelations.put(TownRelation.EMBARGO, new ArrayList<>());
        this.townRelations.put(TownRelation.NON_AGGRESSION, new ArrayList<>());
        this.townRelations.put(TownRelation.ALLIANCE, new ArrayList<>());
    }

    public void setRelation(TownRelation relation, String territoryID) {
        removeAllRelationWith(territoryID);
        if (!townRelations.containsKey(relation))
            return;
        addRelation(relation, territoryID);
    }

    private void addRelation(TownRelation relation, String townID) {
        townRelations.get(relation).add(townID);
    }

    public List<String> getTerritoriesIDWithRelation(TownRelation relation) {
        return townRelations.getOrDefault(relation, Collections.emptyList());
    }

    public TownRelation getRelationWith(Territory playerTerritory) {
        return getRelationWith(playerTerritory.getID());
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

    public void cleanAll(TerritoryData territoryData) {
        for (TownRelation relation : TownRelation.values()) {
            for (Territory otherTerritoryData : getTerritoriesWithRelation(relation)) {
                otherTerritoryData.getRelations().removeAllRelationWith(territoryData.getID());
                otherTerritoryData.broadcastMessageWithSound(
                        Lang.WARNING_OTHER_TOWN_HAS_BEEN_DELETED.get(territoryData.getColoredName(), relation.getColoredName(Lang.getServerLang())),
                        SoundEnum.MINOR_BAD
                );
            }
        }
    }

    public void removeAllRelationWith(String townID) {
        for (List<String> territories : townRelations.values()) {
            territories.remove(townID);
        }
    }

    public List<Territory> getTerritoriesWithRelation(TownRelation townRelation) {
        List<Territory> res = new ArrayList<>();
        for(String territoryID : getTerritoriesIDWithRelation(townRelation)){
            res.add(TerritoryUtil.getTerritory(territoryID));
        }
        res.removeAll(Collections.singleton(null));
        return res;
    }
}
