package org.tan.TownsAndNations.DataClass;

import org.tan.TownsAndNations.enums.TownRelation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class TownRelations {

    private final LinkedHashMap<TownRelation, ArrayList<String>> townRelations = new LinkedHashMap<>();;


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
    public ArrayList<String> getOne(TownRelation relation){
        return this.townRelations.get(relation);
    }
    public org.tan.TownsAndNations.enums.TownRelation getRelationWith(TownData Town) {
        return getRelationWith(Town.getID());
    }
    public org.tan.TownsAndNations.enums.TownRelation getRelationWith(String TownID) {
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
}
