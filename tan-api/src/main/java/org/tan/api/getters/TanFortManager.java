package org.tan.api.getters;

import java.util.List;
import org.tan.api.interfaces.TanFort;

public interface TanFortManager {
  List<TanFort> getForts();

  TanFort getFort(String id);
}
