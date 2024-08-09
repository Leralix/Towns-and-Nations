package org.tan.TownsAndNations.DataClass;

import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.SoundEnum;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.TerritoryUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

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
    public ArrayList<String> getTerritoriesIDWithRelation(TownRelation relation){
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

    public void cleanAll(String ownTownID){

        for(TownRelation relation : TownRelation.values()){

            Collection<String> territories = townRelations.get(relation);
            if(territories == null){
                continue;
            }

            for (String otherTerritory : territories) {

                ITerritoryData otherTerritoryData = TerritoryUtil.getTerritory(otherTerritory);
                if(otherTerritoryData == null)
                    continue;

                otherTerritoryData.getRelations().removeAllRelationWith(ownTownID);
                otherTerritoryData.broadCastMessageWithSound(
                        getTANString() + Lang.WARNING_OTHER_TOWN_HAS_BEEN_DELETED.get(TownDataStorage.get(otherTerritory).getName(),relation.getColoredName()),
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
