package org.leralix.tan.data.territory;

import org.tan.api.interfaces.territory.TanNation;

public interface Nation extends Territory, TanNation {

    void setCapital(String regionID);
}
