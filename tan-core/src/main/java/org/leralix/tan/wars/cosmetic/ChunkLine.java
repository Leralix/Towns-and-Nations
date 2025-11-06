package org.leralix.tan.wars.cosmetic;

import org.leralix.lib.position.Vector2D;

public class ChunkLine {

  private final Vector2D point1;
  private final Vector2D point2;

  ChunkLine(Vector2D point1, Vector2D point2) {
    this.point1 = point1;
    this.point2 = point2;
  }

  public Vector2D getPoint1() {
    return point1;
  }

  public Vector2D getPoint2() {
    return point2;
  }
}
