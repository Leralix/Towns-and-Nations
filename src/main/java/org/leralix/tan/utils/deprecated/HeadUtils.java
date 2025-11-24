package org.leralix.tan.utils.deprecated;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The class used to manage every head related commands
 */
public class HeadUtils {
    private HeadUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static @NotNull ItemStack makeSkullURL(final @NotNull String name, final @NotNull String url, String... lore) {
        return makeSkull(name, getProfile(createURL(url)), List.of(lore));
    }

    public static @NotNull ItemStack makeSkull(final @NotNull String name, final @NotNull PlayerProfile profile, List<String> lore) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwnerProfile(profile);

        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + name);
        if (lore != null)
            meta.setLore(lore);

        skull.setItemMeta(meta);
        return skull;
    }

    private static final UUID RANDOM_UUID = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4"); // We reuse the same "random" UUID all the time

    private static PlayerProfile getProfile(URL url) {
        PlayerProfile profile = Bukkit.createPlayerProfile(RANDOM_UUID);
        PlayerTextures textures = profile.getTextures();
        textures.setSkin(url);
        profile.setTextures(textures);
        return profile;
    }


    private static URL createURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            try {
                return new URL("http://textures.minecraft.net/texture/e7f9c6fef2ad96b3a5465642ba954671be1c4543e2e25e56aef0a47d5f1f");
            } catch (MalformedURLException e2) {
                throw new IllegalArgumentException("Invalid URL: " + url);
            }
        }
    }


    /**
     * Create an {@link ItemStack} with custom Lore
     *
     * @param itemMaterial The data of the region to display
     * @param itemName     The display name of the item
     * @param loreLines    The lore of the item
     * @return The ItemStack displaying the town
     */
    public static ItemStack createCustomItemStack(Material itemMaterial, String itemName, String... loreLines) {
        List<String> lore = List.of(loreLines);
        return createCustomItemStack(itemMaterial, itemName, lore);
    }

    /**
     * Create an {@link ItemStack} with custom Lore.
     *
     * @param itemMaterial The data of the region to display.
     * @param itemName     The display name of the item.
     * @param lore         The lore of the item.
     * @return The ItemStack displaying the town.
     */
    public static ItemStack createCustomItemStack(Material itemMaterial, String itemName, List<String> lore) {
        ItemStack item = new ItemStack(itemMaterial);
        return createCustomItemStack(item, itemName, lore);
    }

    public static ItemStack createCustomItemStack(ItemStack item, String itemName, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + itemName);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }


    /**
     * Add lore to an {@link ItemStack}
     *
     * @param itemStack The item stack to add the lore
     * @param loreLines The lore to add
     */
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
