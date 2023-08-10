package org.tan.towns_and_nations.utils;

import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.enums.TownRelation;
import org.tan.towns_and_nations.storage.TownDataStorage;

public class RelationUtil {


    public static boolean HaveRelation(TownDataClass town, TownDataClass targetTown){

        TownRelation currentRelation = town.getRelationWith(targetTown);
        if(currentRelation == null){
            return false;
        }
        return true;
    }


    public static void addTownRelation(TownDataClass town, TownDataClass targetTown, TownRelation newRelation) {
        town.addTownRelations(newRelation, targetTown.getTownId());
        targetTown.addTownRelations(newRelation, town.getTownId());
    }

    public static void removeRelation(TownDataClass town, TownDataClass targetTown, TownRelation oldRelation){
        town.removeTownRelations(oldRelation, targetTown.getTownId());
        targetTown.removeTownRelations(oldRelation, town.getTownId());
    }

    public static void removeRelation(TownDataClass town, TownDataClass targetTown){
        TownRelation oldRelation = town.getRelationWith(targetTown);
        removeRelation(town,targetTown,oldRelation);
    }


}
