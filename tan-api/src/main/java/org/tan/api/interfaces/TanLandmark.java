package org.tan.api.interfaces;

import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public interface TanLandmark {
  String getID();

  String getName();

  void setName(String name);

  Location getLocation();

  void setQuantity(int quantity);

  int getQuantity();

  void setItem(ItemStack item);

  ItemStack getItem();

  boolean isOwned();

  TanTerritory getOwner();

  void removeOwnership();

  void setOwner(UUID uuid);

  void setOwner(TanTerritory territory);
}
