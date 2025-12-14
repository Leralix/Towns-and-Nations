package org.tan.api.getters;

import org.tan.api.interfaces.TanFort;

import java.util.List;

public interface TanFortManager {

    List<TanFort> getForts();

    TanFort getFort(String id);

}
