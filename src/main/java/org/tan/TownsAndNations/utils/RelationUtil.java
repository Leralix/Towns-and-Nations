package org.tan.TownsAndNations.utils;

import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.enums.TownRelation;

import static org.tan.TownsAndNations.TownsAndNations.isSQLEnabled;

public class RelationUtil {


    public static void addTownRelation(TownData town, TownData targetTown, TownRelation newRelation) {
        town.addTownRelations(newRelation, targetTown);
        if(!isSQLEnabled()) //No need for double relation in sql
            targetTown.addTownRelations(newRelation, town);

        TeamUtils.updateAllScoreboardColor();
    }

    public static void removeTownRelation(TownData town, TownData targetTown, TownRelation oldRelation){
        town.removeTownRelations(oldRelation, targetTown);
        targetTown.removeTownRelations(oldRelation, town);

        TeamUtils.updateAllScoreboardColor();
    }

    public static void removeTownRelation(TownData town, TownData targetTown){
        TownRelation oldRelation = town.getRelationWith(targetTown);
        removeTownRelation(town,targetTown,oldRelation);

        TeamUtils.updateAllScoreboardColor();
    }


}
