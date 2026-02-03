package org.tan.api.getters;

import org.tan.api.interfaces.war.TanWar;

import java.util.Collection;

public interface TanWarManager {

    TanWar getWar(String warID);

    Collection<TanWar> getWars();

}
