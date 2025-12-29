package org.leralix.tan.war.cosmetic;

import org.leralix.lib.position.Vector2D;
import org.leralix.tan.war.info.BoundaryType;

public class ChunkLine {

    private final Vector2D point1;
    private final Vector2D point2;
    private final BoundaryType boundaryType;

    ChunkLine(Vector2D point1, Vector2D point2, BoundaryType boundaryType) {
        this.point1 = point1;
        this.point2 = point2;
        this.boundaryType = boundaryType;
    }

    public Vector2D getPoint1() {
        return point1;
    }

    public Vector2D getPoint2() {
        return point2;
    }

    public BoundaryType getBoundaryType() {
        return boundaryType;
    }
}
