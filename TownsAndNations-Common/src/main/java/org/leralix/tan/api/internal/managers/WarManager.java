package org.leralix.tan.api.internal.managers;

import org.leralix.tan.api.internal.wrappers.WarWrapper;
import org.leralix.tan.storage.stored.WarStorage;
import org.tan.api.getters.TanWarManager;
import org.tan.api.interfaces.war.TanWar;

import java.util.Collection;
import java.util.List;

public class WarManager implements TanWarManager {

    private final WarStorage warStorage;

    public WarManager(WarStorage warStorage){
        this.warStorage = warStorage;
    }

    @Override
    public TanWar getWar(String warID) {
        return new WarWrapper(warStorage.get(warID));
    }

    @Override
    public Collection<TanWar> getWars() {
        return List.copyOf(warStorage.getAllWars().stream().map(WarWrapper::new).toList());
    }
}
