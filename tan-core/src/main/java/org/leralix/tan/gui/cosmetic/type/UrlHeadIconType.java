package org.leralix.tan.gui.cosmetic.type;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

public class UrlHeadIconType extends IconType {

  private final String headUrl;

  private static final Map<String, PlayerProfile> profileCache = new HashMap<>();

  public UrlHeadIconType(String headUrl) {
    this.headUrl = headUrl;
  }

  @Override
  protected ItemStack getItemStack(Player player) {
    PlayerProfile playerProfile = getProfile(getUrl());
    return createSkull(playerProfile);
  }

  private URL getUrl() {
    try {
      // P4.1: Use URI.toURL() instead of deprecated URL constructor
      return java.net.URI.create(headUrl).toURL();
    } catch (Exception e) {
      try {
        // P4.1: Use URI.toURL() instead of deprecated URL constructor
        return java.net
            .URI
            .create(
                "http://textures.minecraft.net/texture/e7f9c6fef2ad96b3a5465642ba954671be1c4543e2e25e56aef0a47d5f1f")
            .toURL();
      } catch (Exception e2) {
        throw new IllegalArgumentException("Invalid URL: " + headUrl);
      }
    }
  }

  private static PlayerProfile getProfile(URL url) {
    String key = url.toString();

    if (profileCache.containsKey(key)) {
      return profileCache.get(key);
    }

    PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
    PlayerTextures textures = profile.getTextures();
    textures.setSkin(url);
    profile.setTextures(textures);

    profileCache.put(key, profile);
    return profile;
  }

  private ItemStack createSkull(PlayerProfile playerProfile) {
    ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) skull.getItemMeta();
    meta.setOwnerProfile(playerProfile);
    skull.setItemMeta(meta);
    return skull;
  }

  public static void clearCache() {
    profileCache.clear();
  }
}
