package org.tan.api.interfaces;

import java.util.Collection;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.tan.api.enums.ETownPermission;

public interface TanTerritory {
  String getID();

  String getName();

  void setName(String name);

  String getDescription();

  void setDescription(String description);

  TanPlayer getOwner();

  UUID getOwnerUUID();

  Long getCreationDate();

  ItemStack getIcon();

  Color getColor();

  void setColor(Color color);

  int getNumberOfClaimedChunk();

  Collection<TanClaimedChunk> getClaimedChunks();

  Collection<TanPlayer> getMembers();

  Collection<TanTerritory> getVassals();

  boolean haveOverlord();

  TanTerritory getOverlord();

  boolean canPlayerDoAction(TanPlayer player, ETownPermission permission);
}
