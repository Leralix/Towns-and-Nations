package org.tan.api.interfaces;

import java.util.Optional;
import org.leralix.lib.position.Vector3D;

public interface TanProperty {
  String getID();

  String getName();

  String getDescription();

  Vector3D getFirstCorner();

  Vector3D getSecondCorner();

  TanPlayer getOwner();

  boolean isForSale();

  boolean isForRent();

  boolean isRented();

  Optional<TanPlayer> getRenter();

  double getRentPrice();

  double getSalePrice();
}
