package org.leralix.tan.utils.deprecated;

import com.google.gson.JsonParser;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.StringUtil;

public class HeadUtils {
  private HeadUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static @NotNull ItemStack getPlayerHeadInformation(
      final @NotNull OfflinePlayer offlinePlayer) {
    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(offlinePlayer).join();
    SkullMeta skullMeta = (SkullMeta) head.getItemMeta();

    if (skullMeta == null) {
      return head;
    }

    skullMeta.setOwningPlayer(offlinePlayer);
    skullMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + offlinePlayer.getName());

    head.setItemMeta(skullMeta);

    setLore(
        head,
        Lang.GUI_YOUR_BALANCE_DESC1.get(
            tanPlayer, StringUtil.formatMoney(EconomyUtil.getBalance(offlinePlayer))));
    return head;
  }

  public static @NotNull ItemStack getPlayerHead(
      String headName, OfflinePlayer offlinePlayer, List<String> lore) {
    ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
    if (skullMeta == null) {
      return playerHead;
    }
    skullMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + headName);
    skullMeta.setOwningPlayer(offlinePlayer);
    if (lore != null) skullMeta.setLore(lore);
    playerHead.setItemMeta(skullMeta);
    return playerHead;
  }

  public static @NotNull ItemStack getPlayerHead(
      String headName, OfflinePlayer offlinePlayer, String... loreLines) {
    List<String> lore = List.of(loreLines);
    return getPlayerHead(headName, offlinePlayer, lore);
  }

  public static @NotNull ItemStack getPlayerHead(OfflinePlayer offlinePlayer, String... loreLines) {
    List<String> lore = List.of(loreLines);
    return getPlayerHead(offlinePlayer.getName(), offlinePlayer, lore);
  }

  public static @NotNull ItemStack getPlayerHead(String headName, OfflinePlayer offlinePlayer) {
    return getPlayerHead(headName, offlinePlayer, (List<String>) null);
  }

  public static @NotNull ItemStack getPlayerHead(OfflinePlayer offlinePlayer) {
    return getPlayerHead(offlinePlayer.getName(), offlinePlayer);
  }

  public static @NotNull ItemStack makeSkullB64(
      final @NotNull String name, final @NotNull String base64EncodedString, List<String> lore) {
    return makeSkullURL(name, getUrlFromBase64(base64EncodedString), lore);
  }

  public static @NotNull ItemStack makeSkullURL(
      final @NotNull String name, final @NotNull String url, String... lore) {
    return makeSkull(name, getProfile(createURL(url)), List.of(lore));
  }

  public static @NotNull ItemStack makeSkullURL(
      final @NotNull String name, final @NotNull String url, List<String> lore) {
    return makeSkull(name, getProfile(createURL(url)), lore);
  }

  public static @NotNull ItemStack makeSkullURL(
      final @NotNull String name, final @NotNull URL url, String... lore) {
    return makeSkull(name, getProfile(url), List.of(lore));
  }

  public static @NotNull ItemStack makeSkullURL(
      final @NotNull String name, final @NotNull URL url, List<String> lore) {
    return makeSkull(name, getProfile(url), lore);
  }

  public static @NotNull ItemStack makeSkull(
      final @NotNull String name, final @NotNull PlayerProfile profile, List<String> lore) {
    ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) skull.getItemMeta();
    meta.setOwnerProfile(profile);

    meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + name);
    if (lore != null) meta.setLore(lore);

    skull.setItemMeta(meta);
    return skull;
  }

  private static final UUID RANDOM_UUID = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4");

  private static PlayerProfile getProfile(URL url) {
    PlayerProfile profile = Bukkit.createPlayerProfile(RANDOM_UUID);
    PlayerTextures textures = profile.getTextures();
    textures.setSkin(url);
    profile.setTextures(textures);
    return profile;
  }

  @NotNull public static URL getUrlFromBase64(@NotNull String base64) {
    var decoded = new String(Base64.getDecoder().decode(base64));
    var json = JsonParser.parseString(decoded).getAsJsonObject();
    var url = json.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
    return createURL(url);
  }

  private static URL createURL(String url) {
    try {
      return java.net.URI.create(url).toURL();
    } catch (Exception e) {
      try {
        return java.net
            .URI
            .create(
                "http://textures.minecraft.net/texture/e7f9c6fef2ad96b3a5465642ba954671be1c4543e2e25e56aef0a47d5f1f")
            .toURL();
      } catch (Exception e2) {
        throw new IllegalArgumentException("Invalid URL: " + url);
      }
    }
  }

  public static @NotNull ItemStack makeSkullB64(
      final @NotNull String name, final @NotNull String base64EncodedString) {
    return makeSkullB64(name, base64EncodedString, (List<String>) null);
  }

  public static @NotNull ItemStack makeSkullB64(
      final @NotNull String name, final @NotNull String base64EncodedString, String... loreLines) {
    List<String> lore = List.of(loreLines);
    return makeSkullB64(name, base64EncodedString, lore);
  }

  public static @NotNull ItemStack makeSkullB64(
      final @NotNull String name,
      final @NotNull String base64EncodedString,
      List<String> lore,
      String... loreLines) {
    List<String> lore2 = List.of(loreLines);
    lore.addAll(lore2);
    return makeSkullB64(name, base64EncodedString, lore);
  }

  public static ItemStack getRegionIcon(RegionData regionData, LangType langType) {
    ItemStack icon = regionData.getIcon();

    ItemMeta meta = icon.getItemMeta();
    if (meta != null) {
      meta.setDisplayName(ChatColor.AQUA + regionData.getName());

      List<String> lore = new ArrayList<>();
      lore.add(Lang.GUI_REGION_INFO_DESC0.get(langType, regionData.getDescription()));
      lore.add(Lang.GUI_REGION_INFO_DESC1.get(langType, regionData.getCapital().getName()));
      lore.add(
          Lang.GUI_REGION_INFO_DESC2.get(
              langType, Integer.toString(regionData.getNumberOfTownsIn())));
      lore.add(
          Lang.GUI_REGION_INFO_DESC3.get(
              langType, Integer.toString(regionData.getTotalPlayerCount())));
      lore.add(Lang.GUI_REGION_INFO_DESC4.get(langType, Double.toString(regionData.getBalance())));
      lore.add(
          Lang.GUI_REGION_INFO_DESC5.get(
              langType, Integer.toString(regionData.getNumberOfClaimedChunk())));
      meta.setLore(lore);
      icon.setItemMeta(meta);
    }
    return icon;
  }

  public static ItemStack createCustomItemStack(
      Material itemMaterial, String itemName, String... loreLines) {
    List<String> lore = List.of(loreLines);
    return createCustomItemStack(itemMaterial, itemName, lore);
  }

  public static ItemStack createCustomItemStack(
      Material itemMaterial, String itemName, List<String> lore, String... loreLines) {
    List<String> lore2 = List.of(loreLines);
    lore.addAll(lore2);
    return createCustomItemStack(itemMaterial, itemName, lore2);
  }

  public static ItemStack createCustomItemStack(
      Material itemMaterial, String itemName, List<String> lore) {
    ItemStack item = new ItemStack(itemMaterial);
    return createCustomItemStack(item, itemName, lore);
  }

  public static ItemStack createCustomItemStack(
      ItemStack item, String itemName, List<String> lore) {
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + itemName);
      meta.setLore(lore);
      item.setItemMeta(meta);
    }
    return item;
  }

  public static ItemStack createCustomItemStack(ItemStack item, String itemName, String... lore) {
    return createCustomItemStack(item, itemName, List.of(lore));
  }

  public static void setLore(ItemStack itemStack, List<String> lore) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta != null) {
      itemMeta.setLore(lore);
      itemStack.setItemMeta(itemMeta);
    }
  }

  public static void setLore(ItemStack itemStack, String... loreLines) {
    List<String> lore = List.of(loreLines);
    setLore(itemStack, lore);
  }

  public static void setLore(ItemStack itemStack, List<String> lore, String... loreLines) {
    List<String> lore2 = List.of(loreLines);
    lore.addAll(lore2);
    setLore(itemStack, lore);
  }

  public static void addLore(ItemStack itemStack, String... loreLines) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta != null) {
      List<String> lore = itemMeta.getLore();
      if (lore == null) {
        lore = new ArrayList<>();
      }
      lore.addAll(List.of(loreLines));
      itemMeta.setLore(lore);
    }
    itemStack.setItemMeta(itemMeta);
  }
}
