package org.tan.TownsAndNations.DataClass;

import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.SoundEnum;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.TerritoryUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class TownRelations {

    private final LinkedHashMap<TownRelation, ArrayList<String>> townRelations = new LinkedHashMap<>();


    public TownRelations(){
        for(TownRelation relation : TownRelation.values()){
            this.townRelations.put(relation, new ArrayList<>());
        }
    }

    public void addRelation(TownRelation relation, String townID){
        this.townRelations.get(relation).add(townID);
    }
    public void removeRelation(TownRelation relation, String townID){
        townRelations.get(relation).remove(townID);
    }
    public ArrayList<String> getTerritoryIDWithRelation(TownRelation relation){
        return townRelations.get(relation);
    }
    public ArrayList<ITerritoryData> getTerritoryWithRelation(TownRelation relation){
        ArrayList<ITerritoryData> territoryData = new ArrayList<>();
        for(String townID : townRelations.get(relation))
            territoryData.add(TerritoryUtil.getTerritory(townID));
        return territoryData;
    }

    public TownRelation getRelationWith(String TownID) {
        for (Map.Entry<TownRelation, ArrayList<String>> entry : townRelations.entrySet()) {
            TownRelation relation = entry.getKey();
            ArrayList<String> list = entry.getValue();

            for (String townUUID : list) {
                if (TownID.equals(townUUID)) {
                    return relation;
                }
            }
        }
        return null;
    }
    public TownRelation getRelationWith(ITerritoryData relation) {
        return getRelationWith(relation.getID());
    }
    public void removeAllRelationWith(String townID){
        for(TownRelation relation : TownRelation.values()){
            if(relation == TownRelation.NEUTRAL)
                continue;
            townRelations.get(relation).remove(townID);
        }
    }
    public void cleanAll(String ownTownID){

        for(TownRelation relation : TownRelation.values()){
            for (String townID : townRelations.get(relation)) {
                TownDataStorage.get(townID).getRelations().removeAllRelationWith(ownTownID);
                TownDataStorage.get(townID).broadCastMessageWithSound(
                        Lang.WARNING_OTHER_TOWN_HAS_BEEN_DELETED.get(TownDataStorage.get(townID).getName(),relation.getColoredName()),
                        SoundEnum.MINOR_BAD
                );
            }
        }

    }
}
