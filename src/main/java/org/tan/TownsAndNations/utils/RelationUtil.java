package org.tan.TownsAndNations.utils;

import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.enums.TownRelation;

public class RelationUtil {


    public static boolean HaveRelation(TownData town, TownData targetTown){

        TownRelation currentRelation = town.getRelationWith(targetTown);
        return currentRelation != null;
    }


    public static void addTownRelation(TownData town, TownData targetTown, TownRelation newRelation) {
        town.addTownRelations(newRelation, targetTown.getID());
        targetTown.addTownRelations(newRelation, town.getID());
    }

    public static void removeRelation(TownData town, TownData targetTown, TownRelation oldRelation){
        town.removeTownRelations(oldRelation, targetTown.getID());
        targetTown.removeTownRelations(oldRelation, town.getID());
    }

    public static void removeRelation(TownData town, TownData targetTown){
        TownRelation oldRelation = town.getRelationWith(targetTown);
        removeRelation(town,targetTown,oldRelation);
    }


}
