package org.leralix.tan.api.managers;

import org.leralix.tan.api.wrappers.FortDataWrapper;
import org.leralix.tan.storage.impl.FortDataStorage;
import org.leralix.tan.storage.stored.FortStorage;
import org.tan.api.getters.TanFortManager;
import org.tan.api.interfaces.TanFort;

import java.util.List;

public class FortManager implements TanFortManager {


    private final FortStorage FortStorage;

    private static FortManager instance;

    private FortManager () {
        FortStorage = FortDataStorage.getInstance();
    }

    public static FortManager getInstance() {
        if (instance == null) {
            instance = new FortManager();
        }
        return instance;
    }

    @Override
    public List<TanFort> getForts() {
        return FortStorage.getForts().stream()
                .map(FortDataWrapper::of)
                .map((TanFort t) -> t)
                .toList();
    }

    @Override
    public TanFort getFort(String fortID) {
        return FortDataWrapper.of(FortStorage.getFort(fortID));
    }
}
