package org.tan.TownsAndNations.utils;

import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.enums.TownRelation;

import static org.tan.TownsAndNations.TownsAndNations.isSQLEnabled;

/**
 * Utility class for handling town relations
 */
public class RelationUtil {

    /**
     * Add a relation between two towns
     * @param town          The town to add the relation to
     * @param targetTown    The town to add the relation with
     * @param newRelation   The relation to add
     */
    public static void addTownRelation(TownData town, TownData targetTown, TownRelation newRelation) {
        town.addTownRelations(newRelation, targetTown);
        if(!isSQLEnabled())
            targetTown.addTownRelations(newRelation, town);

        TeamUtils.updateAllScoreboardColor();
    }

    /**
     * Remove a relation between two towns
     * @param town          The town to remove the relation from
     * @param targetTown    The town to remove the relation with
     * @param oldRelation   The relation to remove
     */
    public static void removeTownRelation(TownData town, TownData targetTown, TownRelation oldRelation){
        town.removeTownRelations(oldRelation, targetTown);
        targetTown.removeTownRelations(oldRelation, town);

        TeamUtils.updateAllScoreboardColor();
    }
    /**
     * Remove a relation between two towns
     * @param town          The town to remove the relation from
     * @param targetTown    The town to remove the relation with
     */
    public static void removeTownRelation(TownData town, TownData targetTown){
        TownRelation oldRelation = town.getRelationWith(targetTown);
        removeTownRelation(town,targetTown,oldRelation);

        TeamUtils.updateAllScoreboardColor();
    }

}
