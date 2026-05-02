package org.leralix.tan.api.internal.managers;

import org.leralix.tan.storage.stored.FortStorage;
import org.tan.api.getters.TanFortManager;
import org.tan.api.interfaces.buildings.TanFort;

import java.util.List;

public class FortManager implements TanFortManager {


    private final FortStorage fortStorage;

    public FortManager (FortStorage fortStorage) {
        this.fortStorage = fortStorage;
    }

    @Override
    public List<TanFort> getForts() {
        return fortStorage.getForts().stream()
                .map((TanFort t) -> t)
                .toList();
    }

    @Override
    public TanFort getFort(String fortID) {
        return fortStorage.getFort(fortID);
    }
}
