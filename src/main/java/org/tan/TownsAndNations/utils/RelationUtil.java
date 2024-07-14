package org.tan.TownsAndNations.utils;

import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.enums.TownRelation;

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
    public static void addTownRelation(ITerritoryData town, ITerritoryData targetTown, TownRelation newRelation) {
        town.addRelation(newRelation, targetTown);
        TeamUtils.updateAllScoreboardColor();
    }

    /**
     * Remove a relation between two towns
     * @param territoryRelation          The town to remove the relation from
     * @param targetTown    The town to remove the relation with
     * @param oldRelation   The relation to remove
     */
    public static void removeTownRelation(ITerritoryData territoryRelation, ITerritoryData targetTown, TownRelation oldRelation){
        territoryRelation.removeRelation(oldRelation, targetTown);
        targetTown.removeRelation(oldRelation, territoryRelation);

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
