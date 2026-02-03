package org.tan.api.events;

import org.tan.api.interfaces.territory.TanTerritory;
import org.tan.api.interfaces.war.wargoals.TanWargoal;

import java.util.List;

public interface WarEndEvent extends TanEvent {

    TanTerritory getWinner();

    TanTerritory getDefeated();

    List<TanWargoal> getAppliedWargoals();

}
