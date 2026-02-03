package org.tan.api.interfaces.war.wargoals;

import org.tan.api.interfaces.territory.TanTerritory;

public interface TanLiberateWargoal extends TanWargoal {

    TanTerritory getTerritoryToLiberate();

}
