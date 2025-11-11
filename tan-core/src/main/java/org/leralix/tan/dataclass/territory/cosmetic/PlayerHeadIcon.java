package org.leralix.tan.dataclass.territory.cosmetic;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.stored.PremiumStorage;
import org.leralix.tan.utils.PlayerProfileCache;

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

    // Use PlayerProfileCache to prevent Mojang API rate limiting (429 errors)
    // This fetches from cache or creates a basic profile without network request
    PlayerProfile profile = PlayerProfileCache.getInstance().getProfileSync(offlinePlayer);
    skullMeta.setOwnerProfile(profile);
    icon.setItemMeta(skullMeta);
    return icon;
  }
}
