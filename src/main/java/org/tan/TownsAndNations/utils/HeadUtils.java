package org.tan.TownsAndNations.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.territoryData.RegionData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

        PlayerData playerData = PlayerDataStorage.get(offlinePlayer);
        TownData playerTown = TownDataStorage.get(playerData);


        if(playerTown != null){
            setLore(head,
                    Lang.GUI_PLAYER_PROFILE_DESC1.get(EconomyUtil.getBalance(offlinePlayer)),
                    Lang.GUI_PLAYER_PROFILE_DESC2.get(playerTown.getName()),
                    Lang.GUI_PLAYER_PROFILE_DESC3.get(playerData.getTownRank().getColoredName())
            );

        }
        else {
            setLore(head,
                    Lang.GUI_PLAYER_PROFILE_DESC1.get(EconomyUtil.getBalance(offlinePlayer)),
                    Lang.GUI_PLAYER_PROFILE_NO_TOWN.get()
            );
        }

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
    public static @NotNull ItemStack makeSkull(final @NotNull String name, final @NotNull String base64EncodedString, List<String> lore) {
        UUID id = UUID.nameUUIDFromBytes(base64EncodedString.getBytes());
        int less = (int) id.getLeastSignificantBits();
        int most = (int) id.getMostSignificantBits();
        ItemStack skull = Bukkit.getUnsafe().modifyItemStack(new ItemStack(Material.PLAYER_HEAD),
                "{SkullOwner:{Id:[I;" + less * most + "," + (less >> 23) + "," + most / less + "," +
                        most * 8731 + "],Properties:{textures:[{Value:\"" + base64EncodedString + "\"}]}}}");

        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null){
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + name);
            if(lore != null)
                meta.setLore(lore);

            skull.setItemMeta(meta);
        }
        return skull;
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
    public static @NotNull ItemStack makeSkull(final @NotNull String name, final @NotNull String base64EncodedString) {
        return makeSkull(name,base64EncodedString, (List<String>) null);
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
    public static @NotNull ItemStack makeSkull(final @NotNull String name, final @NotNull String base64EncodedString, String... loreLines) {
        List<String> lore = Arrays.asList(loreLines);
        return makeSkull(name,base64EncodedString,lore);
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
    public static @NotNull ItemStack makeSkull(final @NotNull String name, final @NotNull String base64EncodedString, List<String> lore,  String... loreLines) {
        List<String> lore2 = Arrays.asList(loreLines);
        lore.addAll(lore2);
        return makeSkull(name,base64EncodedString,lore);
    }
    /**
     * Return the head displaying the town icon
     * @param townData  The data of the town whose icon is to be retrieved
     * @return          An ItemStack representing the town's icon, either predefined or a player head item stack
     *
     */
    public static @NotNull ItemStack getTownIcon(final @NotNull TownData townData){
        ItemStack itemStack = townData.getIconItem();

        ItemMeta meta = itemStack.getItemMeta();
        if(meta != null){
            meta.setDisplayName(ChatColor.GREEN + townData.getName());
            itemStack.setItemMeta(meta);
        }
        return itemStack;

    }
    /**
     * Return the head displaying the town icon
     * @param townID    The id of the town whose icon is to be retrieved
     * @return          An ItemStack representing the town's icon, either predefined or a player head item stack
     */
    public static @NotNull ItemStack getTownIcon(String townID){
        return getTownIcon(TownDataStorage.get(townID));
    }
    /**
     * Create a player head displaying a town with his information
     * @param TownId        The id of the town to display
     * @param ownTownID     The ID of the town to compare the relation with (optional)
     * @return              The ItemStack displaying the town
     */
    public static @NotNull ItemStack getTownIconWithInformations(final @NotNull String TownId, final @Nullable String ownTownID){

        TownData town = TownDataStorage.get(TownId);
        ItemStack icon = getTownIcon(TownId);

        ItemMeta meta = icon.getItemMeta();
        if(meta != null){
            meta.setDisplayName(ChatColor.GREEN + town.getName());

            List<String> lore = new ArrayList<>();
            lore.add(Lang.GUI_TOWN_INFO_DESC0.get(town.getDescription()));
            lore.add("");
            lore.add(Lang.GUI_TOWN_INFO_DESC1.get(town.getLeaderName()));
            lore.add(Lang.GUI_TOWN_INFO_DESC2.get(town.getPlayerList().size()));
            lore.add(Lang.GUI_TOWN_INFO_DESC3.get(town.getNumberOfClaimedChunk()));
            lore.add(town.haveRegion()? Lang.GUI_TOWN_INFO_DESC5_REGION.get(town.getRegion().getName()): Lang.GUI_TOWN_INFO_DESC5_NO_REGION.get());

            if(ownTownID != null){
                TownRelation relation = town.getRelationWith(ownTownID);
                String relationName;
                if(relation == null){
                    relationName = Lang.GUI_TOWN_RELATION_NEUTRAL.get();
                }
                else {
                    relationName = relation.getColor() + relation.getName();
                }
                lore.add(Lang.GUI_TOWN_INFO_TOWN_RELATION.get(relationName));
            }

            meta.setLore(lore);
            icon.setItemMeta(meta);
        }
        return icon;
    }
    /**
     * Create a player head displaying a town with his information
     * @param townData      The data of the town to display
     * @return              The ItemStack displaying the town
     */
    public static @NotNull ItemStack getTownIconWithInformations(final @NotNull TownData townData){
        return getTownIconWithInformations(townData.getID(), null);
    }
    /**
     * Create a player head displaying a town with his information
     * @param townID        The ID of the town to display
     * @return              The ItemStack displaying the town
     */
    public static @NotNull ItemStack getTownIconWithInformations(final @NotNull String townID){
        return getTownIconWithInformations(townID, null);
    }
    /**
     * Create a player head displaying a town with his information
     * @param regionData    The data of the region to display
     * @return              The ItemStack displaying the town
     */
    public static ItemStack getRegionIcon(RegionData regionData){
        ItemStack icon = regionData.getIconItem();

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
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + itemName);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
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
