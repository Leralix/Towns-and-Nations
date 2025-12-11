package org.leralix.tan.gui.cosmetic.type;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.leralix.tan.utils.PlayerProfileCache;

public class PlayerHeadIconType extends IconType {

  @Override
  protected ItemStack getItemStack(Player player) {
    PlayerProfile playerProfile = PlayerProfileCache.getInstance().getProfileSync(player);
    return createSkull(playerProfile);
  }

  private ItemStack createSkull(PlayerProfile playerProfile) {
    ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) skull.getItemMeta();
    meta.setOwnerProfile(playerProfile);
    skull.setItemMeta(meta);
    return skull;
  }
}
