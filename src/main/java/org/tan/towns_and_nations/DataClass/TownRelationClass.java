package org.tan.towns_and_nations.DataClass;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class TownRelationClass {

    private final LinkedHashMap<String, ArrayList<String>> townRelations = new LinkedHashMap<String, ArrayList<String>>();


    public TownRelationClass(){
        ArrayList<String> emptyList = new ArrayList<>();
        this.townRelations.put("alliance",emptyList);
        this.townRelations.put("nap",emptyList);
        this.townRelations.put("embargo",emptyList);
        this.townRelations.put("war",emptyList);
    }

    public void addRelation(String relationName, String townID){
        townRelations.get(relationName).add(townID);

    }

    public void removeRelation(String relationName, String townID){
        townRelations.get(relationName).remove(townID);
    }

    public HashMap<String, ArrayList<String>> getAll(){
        return this.townRelations;
    }

    public ArrayList<String> getOne(String relation){
        return this.townRelations.get(relation);
    }






}
