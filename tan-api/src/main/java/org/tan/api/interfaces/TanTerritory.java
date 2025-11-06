package org.tan.api.interfaces;

/** Represents a territory, which can be a town or a nation. */
public interface TanTerritory {

  /**
   * Get the name of the territory.
   *
   * @return The name of the territory.
   */
  String getName();

  /**
   * Get the ID of the territory.
   *
   * @return The ID of the territory.
   */
  String getID();
}
