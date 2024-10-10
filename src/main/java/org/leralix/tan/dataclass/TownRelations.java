package org.leralix.tan.dataclass;

import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.Lang.Lang;
import org.leralix.tan.enums.SoundEnum;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.utils.TerritoryUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.leralix.tan.utils.ChatUtils.getTANString;

public class TownRelations {

    private final LinkedHashMap<TownRelation, ArrayList<String>> townRelations = new LinkedHashMap<>();


    public TownRelations(){
        for(TownRelation relation : TownRelation.values()){
            this.townRelations.put(relation, new ArrayList<>());
        }
    }

    public void addRelation(TownRelation relation, String townID){
        townRelations.get(relation).add(townID);
    }
    public void removeRelation(TownRelation relation, String townID){
        townRelations.get(relation).remove(townID);
    }
    public List<String> getTerritoriesIDWithRelation(TownRelation relation){
        return townRelations.get(relation);
    }

    public TownRelation getRelationWith(String territoryID) {
        for (Map.Entry<TownRelation, ArrayList<String>> entry : townRelations.entrySet()) {
            TownRelation relation = entry.getKey();
            ArrayList<String> list = entry.getValue();

            for (String townUUID : list) {
                if (territoryID.equals(townUUID)) {
                    return relation;
                }
            }
        }
        return null;
    }
    public TownRelation getRelationWith(ITerritoryData territory) {
        return getRelationWith(territory.getID());
    }

    public void cleanAll(ITerritoryData territoryData){

        for(TownRelation relation : TownRelation.values()){

            Collection<String> territories = townRelations.get(relation);
            if(territories == null){
                continue;
            }

            for (String otherTerritory : territories) {

                ITerritoryData otherTerritoryData = TerritoryUtil.getTerritory(otherTerritory);
                if(otherTerritoryData == null)
                    continue;

                otherTerritoryData.getRelations().removeAllRelationWith(territoryData.getID());
                otherTerritoryData.broadCastMessageWithSound(
                        getTANString() + Lang.WARNING_OTHER_TOWN_HAS_BEEN_DELETED.get(territoryData.getColoredName(),relation.getColoredName()),
                        SoundEnum.MINOR_BAD
                );
            }
        }
    }

    public void removeAllRelationWith(String townID){
        for(TownRelation relation : TownRelation.values()){

            if(relation == TownRelation.NEUTRAL)
                continue;

            ArrayList<String> territoryList = townRelations.get(relation);
            if(territoryList == null)
                continue;
            territoryList.remove(townID);
        }
    }
}
