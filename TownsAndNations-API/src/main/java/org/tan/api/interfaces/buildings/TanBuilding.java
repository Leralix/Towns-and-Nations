package org.tan.api.interfaces.buildings;

import org.leralix.lib.position.Vector3D;

public interface TanBuilding {

    /**
     * @return the ID of the building
     */
    String getID();

    /**
     * @return the name of the building
     */
    String getName();

    /**
     * Delete the building.
     */
    void delete();

    /**
     * @return the position of the building
     */
    Vector3D getPosition();
}
