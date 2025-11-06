package org.leralix.tan.api.internal.managers;

import java.util.List;
import org.leralix.tan.api.internal.wrappers.FortDataWrapper;
import org.leralix.tan.storage.impl.FortDataStorage;
import org.leralix.tan.storage.stored.FortStorage;
import org.tan.api.getters.TanFortManager;
import org.tan.api.interfaces.TanFort;

public class FortManager implements TanFortManager {

  private final FortStorage fortStorage;

  private static FortManager instance;

  private FortManager() {
    fortStorage = FortDataStorage.getInstance();
  }

  public static FortManager getInstance() {
    if (instance == null) {
      instance = new FortManager();
    }
    return instance;
  }

  @Override
  public List<TanFort> getForts() {
    return fortStorage.getForts().stream().map(FortDataWrapper::of).map((TanFort t) -> t).toList();
  }

  @Override
  public TanFort getFort(String fortID) {
    return FortDataWrapper.of(fortStorage.getFort(fortID));
  }
}
