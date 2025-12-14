package org.tan.api.events;

import org.tan.api.interfaces.TanTerritory;
import org.tan.api.interfaces.war.wargoals.TanWargoal;

import java.util.List;

public interface WarEndEvent {

    TanTerritory getWinner();

    TanTerritory getDefeated();

    List<TanWargoal> getAppliedWargoals();

}
