package org.leralix.tan.data.territory;

import org.tan.api.interfaces.territory.TanRegion;

import java.util.Optional;

public interface Region extends Territory, TanRegion {
    void setCapital(String townID);

    Optional<Nation> getNation();

    String getNationID();
}
