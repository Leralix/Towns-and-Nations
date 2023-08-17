package org.tan.towns_and_nations.DataClass;

import org.tan.towns_and_nations.enums.TownRelation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class TownRelationClass {

    private final LinkedHashMap<TownRelation, ArrayList<String>> townRelations = new LinkedHashMap<>();;


    public TownRelationClass(){
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
    public TownRelation getRelationWith(TownDataClass Town) {
        return getRelationWith(Town.getTownId());
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
}
