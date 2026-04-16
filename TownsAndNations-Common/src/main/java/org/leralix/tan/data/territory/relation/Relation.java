package org.leralix.tan.data.territory.relation;

import org.leralix.tan.data.territory.Territory;

import java.util.List;

public interface Relation {

    void setRelation(TownRelation relation, String territoryID);

    List<String> getTerritoriesIDWithRelation(TownRelation relation);

    TownRelation getRelationWith(String territoryID);

    default TownRelation getRelationWith(Territory territory) {
        return getRelationWith(territory.getID());
    }

    /**
     * Get all territories with the specified relation
     * @param townRelation The relation to check
     * @return A list of territories not null with the specified relation
     */
    List<Territory> getTerritoriesWithRelation(TownRelation townRelation);

}
