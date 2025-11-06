package org.leralix.tan.dataclass.territory.cosmetic;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.stored.PremiumStorage;

public class PlayerHeadIcon implements ICustomIcon {
  private final String playerUUID;

  public PlayerHeadIcon(ITanPlayer player) {
    if (player == null) {
      this.playerUUID = null;
    } else {
      this.playerUUID = player.getID();
    }
  }

  public PlayerHeadIcon(String playerID) {
    this.playerUUID = playerID;
  }

  public ItemStack getIcon() {
    if (playerUUID == null) return new ItemStack(Material.SKELETON_SKULL);

    ItemStack icon = new ItemStack(Material.PLAYER_HEAD);

    SkullMeta skullMeta = (SkullMeta) icon.getItemMeta();

    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));

    if (skullMeta == null || !PremiumStorage.getInstance().isPremium(offlinePlayer.getName())) {
      return icon;
    }

    skullMeta.setOwnerProfile(offlinePlayer.getPlayerProfile());
    icon.setItemMeta(skullMeta);
    return icon;
  }
}
