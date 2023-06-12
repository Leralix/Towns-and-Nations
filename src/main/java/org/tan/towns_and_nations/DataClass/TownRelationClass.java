package org.tan.towns_and_nations.DataClass;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class TownRelationClass {

    private final LinkedHashMap<String, ArrayList<String>> townRelations = new LinkedHashMap<String, ArrayList<String>>();


    public TownRelationClass(){
        this.townRelations.put("alliance",new ArrayList<>());
        this.townRelations.put("nap",new ArrayList<>());
        this.townRelations.put("embargo",new ArrayList<>());
        this.townRelations.put("war",new ArrayList<>());
    }

    public void addRelation(String relationName, String townID){
        this.townRelations.get(relationName).add(townID);
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
