package org.leralix.tan.utils;

import com.google.gson.JsonParser;
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
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.RegionDataStorage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * The class used to manage every head related commands
 */
public class HeadUtils {
    /**
     * Return the player head with information on balance, town name and rank name
     * @param offlinePlayer The offline player to copy the head
     * @return              The head of the player as an {@link ItemStack}
     */
    public static @NotNull ItemStack getPlayerHeadInformation(final @NotNull OfflinePlayer offlinePlayer){
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();

        if(skullMeta == null){
            return head;
        }

        skullMeta.setOwningPlayer(offlinePlayer);
        skullMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + offlinePlayer.getName());

        head.setItemMeta(skullMeta);

        setLore(head, Lang.GUI_PLAYER_PROFILE_DESC1.get(StringUtil.formatMoney(EconomyUtil.getBalance(offlinePlayer))));
        return head;
    }
    /**
     * Create a player head {@link ItemStack}.
     * @param headName      The name of the new created {@link ItemStack}.
     * @param offlinePlayer The player to extract the head from.
     * @param lore          The lore of the new created {@link ItemStack}.
     * @return              The head of the player as an {@link ItemStack}.
     */
    public static @NotNull ItemStack getPlayerHead(String headName, OfflinePlayer offlinePlayer, List<String> lore){
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
        if(skullMeta == null){
            return playerHead;
        }
        skullMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + headName);
        skullMeta.setOwningPlayer(offlinePlayer);
        if(lore != null)
            skullMeta.setLore(lore);
        playerHead.setItemMeta(skullMeta);
        return playerHead;
    }
    /**
     * Create a player head {@link ItemStack}.
     * @param headName      The name of the new created {@link ItemStack}.
     * @param offlinePlayer The player to extract the head from.
     * @param loreLines     The lore of the new created {@link ItemStack}.
     * @return              The head of the player as an {@link ItemStack}.
     */
    public static @NotNull ItemStack getPlayerHead(String headName, OfflinePlayer offlinePlayer,String... loreLines){
        List<String> lore = Arrays.asList(loreLines);
        return getPlayerHead(headName,offlinePlayer,lore);
    }
    /**
     * Create a player head {@link ItemStack}.
     * @param offlinePlayer The player to extract the head from.
     * @param loreLines     The lore of the new created {@link ItemStack}.
     * @return              The head of the player as an {@link ItemStack}.
     */
    public static @NotNull ItemStack getPlayerHead(OfflinePlayer offlinePlayer,String... loreLines){
        List<String> lore = Arrays.asList(loreLines);
        return getPlayerHead(offlinePlayer.getName(),offlinePlayer,lore);
    }
    /**
     * Create a player head {@link ItemStack}.
     * @param headName      The name of the new created {@link ItemStack}.
     * @param offlinePlayer The player to extract the head from.
     * @return              The head of the player as an {@link ItemStack}.
     */
    public static @NotNull ItemStack getPlayerHead(String headName, OfflinePlayer offlinePlayer){
        return getPlayerHead(headName,offlinePlayer,(List<String>) null);
    }
    /**
     * Create a player head with the player name as name of the {@link ItemStack}
     * @param offlinePlayer      The name of the new created {@link ItemStack}.
     * @return              The head of the player as an {@link ItemStack}.
     */
    public static @NotNull ItemStack getPlayerHead(OfflinePlayer offlinePlayer){
        return getPlayerHead(offlinePlayer.getName(),offlinePlayer);
    }
    /**
     * Create a head from base64 encoded string
     * This method is called when loading custom heads from the internet
     * Check <a href="https://minecraft-heads.com/">minecraft-heads.com</a> for more heads.
     * This method calls an unsafe bukkit methods but no other methods have been found
     * @param name                  The name of the new created head.
     * @param base64EncodedString   The base64 encoded String of the new head.
     * @param lore                  The lore of the new created head.
     * @return                      The {@link ItemStack} with custom texture.
     */
    public static @NotNull ItemStack makeSkullB64(final @NotNull String name, final @NotNull String base64EncodedString, List<String> lore) {
        return makeSkullURL(name,getUrlFromBase64_2(base64EncodedString),lore);
    }

    public static @NotNull ItemStack makeSkullURL(final @NotNull String name, final @NotNull String url, String... lore) {
        return makeSkull(name,getProfile(createURL(url)),Arrays.asList(lore));
    }
    public static @NotNull ItemStack makeSkullURL(final @NotNull String name, final @NotNull String url, List<String> lore) {
        return makeSkull(name,getProfile(createURL(url)),lore);
    }
    public static @NotNull ItemStack makeSkullURL(final @NotNull String name, final @NotNull URL url, String... lore) {
        return makeSkull(name,getProfile(url),Arrays.asList(lore));
    }
    public static @NotNull ItemStack makeSkullURL(final @NotNull String name, final @NotNull URL url, List<String> lore) {
        return makeSkull(name,getProfile(url),lore);
    }

    public static @NotNull ItemStack makeSkull(final @NotNull String name, final @NotNull PlayerProfile profile, List<String> lore) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwnerProfile(profile);
        skull.setItemMeta(meta);

        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + name);
        if(lore != null)
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


    public static URL getUrlFromBase64_2(String base64){
        var decoded = new String(Base64.getDecoder().decode(base64));
        var json = JsonParser.parseString(decoded).getAsJsonObject();
        var url = json.getAsJsonObject("textures")
                .getAsJsonObject("SKIN")
                .get("url").getAsString();
        return createURL(url);
    }

    private static URL createURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }



        /**
         * Create a head from base64 encoded string
         * This method is called when loading custom heads from the internet
         * Check <a href="https://minecraft-heads.com/">minecraft-heads.com</a> for more heads.
         * This method calls an unsafe bukkit methods but no other methods have been found
         * @param name                  The name of the new created head.
         * @param base64EncodedString   The base64 encoded String of the new head.
         * @return                      The {@link ItemStack} with custom texture.
         */
    public static @NotNull ItemStack makeSkullB64(final @NotNull String name, final @NotNull String base64EncodedString) {
        return makeSkullB64(name,base64EncodedString, (List<String>) null);
    }
    /**
     * Create a head from base64 encoded string
     * This method is called when loading custom heads from the internet
     * Check <a href="https://minecraft-heads.com/">minecraft-heads.com</a> for more heads.
     * This method calls an unsafe bukkit methods but no other methods have been found
     * @param name                  The name of the new created head.
     * @param base64EncodedString   The base64 encoded String of the new head.
     * @param loreLines             The lore of the new created head.
     * @return                      The {@link ItemStack} with custom texture.
     */
    public static @NotNull ItemStack makeSkullB64(final @NotNull String name, final @NotNull String base64EncodedString, String... loreLines) {
        List<String> lore = Arrays.asList(loreLines);
        return makeSkullB64(name,base64EncodedString,lore);
    }
    /**
     * Create a head from base64 encoded string
     * This method is called when loading custom heads from the internet
     * Check <a href="https://minecraft-heads.com/">minecraft-heads.com</a> for more heads.
     * This method calls an unsafe bukkit methods but no other methods have been found
     * @param name                  The name of the new created head.
     * @param base64EncodedString   The base64 encoded String of the new head.
     * @param lore                  The lore of the new created head.
     * @param loreLines             Additional lore.
     * @return                      The {@link ItemStack} with custom texture.
     */
    public static @NotNull ItemStack makeSkullB64(final @NotNull String name, final @NotNull String base64EncodedString, List<String> lore, String... loreLines) {
        List<String> lore2 = Arrays.asList(loreLines);
        lore.addAll(lore2);
        return makeSkullB64(name,base64EncodedString,lore);
    }
    /**
     * Create a player head displaying a town with his information
     * @param regionID      The ID of the region to display
     * @return              The ItemStack displaying the town
     */
    public static ItemStack getRegionIcon(String regionID){
        return getRegionIcon(RegionDataStorage.get(regionID));
    }
    /**
     * Create a player head displaying a town with his information
     * @param regionData    The data of the region to display
     * @return              The ItemStack displaying the town
     */
    public static ItemStack getRegionIcon(RegionData regionData){
        ItemStack icon = regionData.getIcon();

        ItemMeta meta = icon.getItemMeta();
        if(meta != null){
            meta.setDisplayName(ChatColor.AQUA + regionData.getName());

            List<String> lore = new ArrayList<>();
            lore.add(Lang.GUI_REGION_INFO_DESC0.get(regionData.getDescription()));
            lore.add(Lang.GUI_REGION_INFO_DESC1.get(regionData.getCapital().getName()));
            lore.add(Lang.GUI_REGION_INFO_DESC2.get(regionData.getNumberOfTownsIn()));
            lore.add(Lang.GUI_REGION_INFO_DESC3.get(regionData.getTotalPlayerCount()));
            lore.add(Lang.GUI_REGION_INFO_DESC4.get(regionData.getBalance()));
            lore.add(Lang.GUI_REGION_INFO_DESC5.get(regionData.getNumberOfClaimedChunk()));
            meta.setLore(lore);
            icon.setItemMeta(meta);
        }
        return icon;
    }


    /**
     * Create an {@link ItemStack} with custom Lore
     * @param itemMaterial  The data of the region to display
     * @param itemName      The display name of the item
     * @return              The ItemStack displaying the town
     */
    public static ItemStack createCustomItemStack(Material itemMaterial, String itemName){
        return createCustomItemStack(itemMaterial,itemName,(List<String>)null);
    }
    /**
     * Create an {@link ItemStack} with custom Lore
     * @param itemMaterial  The data of the region to display
     * @param itemName      The display name of the item
     * @param loreLines     The lore of the item
     * @return              The ItemStack displaying the town
     */
    public static ItemStack createCustomItemStack(Material itemMaterial, String itemName, String... loreLines){
        List<String> lore = Arrays.asList(loreLines);
        return createCustomItemStack(itemMaterial,itemName, lore);
    }
    /**
     * Create an {@link ItemStack} with custom Lore.
     * @param itemMaterial  The data of the region to display.
     * @param itemName      The display name of the item.
     * @param lore          The lore of the item.
     * @param loreLines     Additional lore.
     * @return              The ItemStack displaying the town.
     */
    public static ItemStack createCustomItemStack(Material itemMaterial, String itemName, List<String> lore, String... loreLines){
        List<String> lore2 = Arrays.asList(loreLines);
        lore.addAll(lore2);
        return createCustomItemStack(itemMaterial,itemName, lore2);
    }
    /**
     * Create an {@link ItemStack} with custom Lore.
     * @param itemMaterial  The data of the region to display.
     * @param itemName      The display name of the item.
     * @param lore          The lore of the item.
     * @return              The ItemStack displaying the town.
     */
    public static ItemStack createCustomItemStack(Material itemMaterial, String itemName, List<String> lore){
        ItemStack item = new ItemStack(itemMaterial);
        return createCustomItemStack(item, itemName, lore);
    }

    public static ItemStack createCustomItemStack(ItemStack item, String itemName, List<String> lore){
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + itemName);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create an {@link ItemStack} with custom Lore.
     * @param item          The Itemstack to use.
     * @param itemName      The display name of the item.
     * @param lore          The lore of the item.
     * @return              The ItemStack displaying the town.
     */
    public static ItemStack createCustomItemStack(ItemStack item, String itemName, String... lore){
        return createCustomItemStack(item, itemName, Arrays.asList(lore));
    }
    /**
     * Set the lore of an {@link ItemStack}
     * @param itemStack The item stack to set the lore
     * @param lore      The lore to set
     */
    public static void setLore(ItemStack itemStack, List<String> lore){
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta !=null){
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }
    }
    /**
     * Set the lore of an {@link ItemStack}
     * @param itemStack The item stack to set the lore
     * @param loreLines The lore to set
     */
    public static void setLore(ItemStack itemStack, String... loreLines) {
        List<String> lore = Arrays.asList(loreLines);
        setLore(itemStack,lore);
    }
    /**
     * Set the lore of an {@link ItemStack}
     * @param itemStack The item stack to set the lore
     * @param lore      The lore to set
     * @param loreLines Additional lore
     */
    public static void setLore(ItemStack itemStack, List<String> lore, String... loreLines) {
        List<String> lore2 = Arrays.asList(loreLines);
        lore.addAll(lore2);
        setLore(itemStack,lore);
    }
    /**
     * Add lore to an {@link ItemStack}
     * @param itemStack The item stack to add the lore
     * @param loreLines The lore to add
     */
    public static void addLore(ItemStack itemStack, String... loreLines) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta != null){
            List<String> lore = itemMeta.getLore();
            if(lore == null){
                lore = new ArrayList<>();
            }
            lore.addAll(Arrays.asList(loreLines));
            itemMeta.setLore(lore);
        }
        itemStack.setItemMeta(itemMeta);
    }
}
