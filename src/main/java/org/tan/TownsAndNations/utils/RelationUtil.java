package org.tan.TownsAndNations.utils;

import org.tan.TownsAndNations.DataClass.TownDataClass;
import org.tan.TownsAndNations.enums.TownRelation;

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
