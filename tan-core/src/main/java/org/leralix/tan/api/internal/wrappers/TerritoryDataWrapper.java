package org.leralix.tan.api.internal.wrappers;

import java.util.Collection;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.tan.api.enums.ETownPermission;
import org.tan.api.interfaces.TanClaimedChunk;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanTerritory;

public class TerritoryDataWrapper implements TanTerritory {

  private final TerritoryData territoryData;

  protected TerritoryDataWrapper(TerritoryData territoryData) {
    this.territoryData = territoryData;
  }

  public static TanTerritory of(TerritoryData territoryData) {
    if (territoryData == null) {
      return null;
    }
    return new TerritoryDataWrapper(territoryData);
  }

  @Override
  public String getID() {
    return territoryData.getID();
  }

  @Override
  public String getName() {
    return territoryData.getName();
  }

  public void setName(String newName) {
    territoryData.rename(newName);
  }

  public String getDescription() {
    return territoryData.getDescription();
  }

  public void setDescription(String s) {
    territoryData.setDescription(s);
  }

  public TanPlayer getOwner() {
    return TanPlayerWrapper.of(territoryData.getLeaderData());
  }

  public UUID getOwnerUUID() {
    return getOwner().getUUID();
  }

  public Long getCreationDate() {
    return territoryData.getCreationDate();
  }

  public ItemStack getIcon() {
    return territoryData.getIcon();
  }

  public Color getColor() {
    return Color.fromRGB(territoryData.getChunkColorCode());
  }

  public void setColor(Color color) {
    territoryData.setChunkColor(color.asRGB());
  }

  public int getNumberOfClaimedChunk() {
    return getClaimedChunks().size();
  }

  public Collection<TanPlayer> getMembers() {
    return territoryData.getITanPlayerList().stream().map(TanPlayerWrapper::of).toList();
  }

  public Collection<TanTerritory> getVassals() {
    return territoryData.getVassals().stream().map(TerritoryDataWrapper::of).toList();
  }

  public boolean haveOverlord() {
    return territoryData.haveOverlord();
  }

  public TanTerritory getOverlord() {
    return TerritoryDataWrapper.of(territoryData.getOverlord().get());
  }

  public boolean canPlayerDoAction(TanPlayer tanPlayer, ETownPermission permission) {

    Player player = Bukkit.getPlayer(tanPlayer.getUUID());
    if (player == null) {
      return false; // Player is not online
    }
    RolePermission playerPermission = RolePermission.valueOf(permission.name());

    return territoryData.doesPlayerHavePermission(player, playerPermission);
  }

  public Collection<TanClaimedChunk> getClaimedChunks() {
    return NewClaimedChunkStorage.getInstance().getAllChunkFrom(territoryData).stream()
        .map(ClaimedChunkWrapper::of)
        .toList();
  }
}
