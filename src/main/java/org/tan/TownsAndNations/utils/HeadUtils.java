package org.tan.TownsAndNations.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.TownDataStorage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class HeadUtils {



    public static ItemStack getPlayerHead(Player p){
        ItemStack PlayerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) PlayerHead.getItemMeta();
        skullMeta.setOwningPlayer(p);
        PlayerHead.setItemMeta(skullMeta);
        return PlayerHead;
    }
    public static ItemStack getPlayerHead(String headName, OfflinePlayer p){
        ItemStack PlayerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) PlayerHead.getItemMeta();
        skullMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + headName);
        skullMeta.setOwningPlayer(p);
        PlayerHead.setItemMeta(skullMeta);
        return PlayerHead;
    }

    public static ItemStack makeSkull(String name, String base64EncodedString) {
        final ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        assert meta != null;
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", base64EncodedString));
        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + name);
        skull.setItemMeta(meta);
        return skull;
    }

    public static ItemStack getTownIcon(String TownId){

        TownData town = TownDataStorage.get(TownId);
        ItemStack itemStack = town.getTownIconItemStack();
        if(itemStack == null){
            return HeadUtils.getPlayerHead(town.getName(), Bukkit.getOfflinePlayer(UUID.fromString(town.getUuidLeader())));
        }
        else {
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + town.getName());
            itemStack.setItemMeta(meta);
            return itemStack;
        }
    }

    public static ItemStack getTownIconWithInformations(String TownId){

        TownData town = TownDataStorage.get(TownId);
        ItemStack icon = HeadUtils.getTownIcon(town.getID());
        if (icon == null){
            icon =  HeadUtils.getPlayerHead(town.getName(), Bukkit.getOfflinePlayer(UUID.fromString(town.getUuidLeader())));
        }
        ItemMeta meta = icon.getItemMeta();
        List<String> lore = new ArrayList<>();

        lore.add(Lang.GUI_TOWN_INFO_DESC_1.getTranslation(Bukkit.getOfflinePlayer(UUID.fromString(town.getUuidLeader())).getName()));
        lore.add(Lang.GUI_TOWN_INFO_DESC_2.getTranslation(town.getPlayerList().size()));
        lore.add(Lang.GUI_TOWN_INFO_DESC_3.getTranslation(town.getChunkSettings().getNumberOfClaimedChunk()));

        meta.setLore(lore);
        icon.setItemMeta(meta);
        return icon;
    }

    public static ItemStack getTownIconWithInformations(String TownId,String ownTownID){

        TownData town = TownDataStorage.get(TownId);
        ItemStack icon = town.getTownIconItemStack();

        if (icon == null){
            icon =  HeadUtils.getPlayerHead(town.getName(), Bukkit.getOfflinePlayer(UUID.fromString(town.getUuidLeader())));
        }
        ItemMeta meta = icon.getItemMeta();
        List<String> lore = new ArrayList<>();

        TownRelation relation = town.getRelationWith(ownTownID);
        String relationName;
        if(relation == null){
            relationName = Lang.GUI_TOWN_RELATION_NEUTRAL.getTranslation();
        }
        else {
            relationName = relation.getColor() + relation.getName();
        }

        lore.add(Lang.GUI_TOWN_INFO_DESC_1.getTranslation(Bukkit.getOfflinePlayer(UUID.fromString(town.getUuidLeader())).getName()));
        lore.add(Lang.GUI_TOWN_INFO_DESC_2.getTranslation(town.getPlayerList().size()));
        lore.add(Lang.GUI_TOWN_INFO_DESC_3.getTranslation(town.getChunkSettings().getNumberOfClaimedChunk()));
        lore.add(Lang.GUI_TOWN_INFO_TOWN_RELATION.getTranslation(relationName));

        meta.setLore(lore);
        icon.setItemMeta(meta);
        return icon;
    }

    public static ItemStack getCustomLoreItem(Material itemMaterial, String itemName){
        return getCustomLoreItem(itemMaterial,itemName,(String)null);
    }
    public static ItemStack getCustomLoreItem(Material itemMaterial, String itemName, String... loreLines){
        ItemStack item = new ItemStack(itemMaterial);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + itemName);

        if(loreLines != null){
            List<String> lore = Arrays.asList(loreLines);
            meta.setLore(lore);
        }


        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack addLore(ItemStack itemStack, List<String> lore){
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static void addLore(ItemStack itemStack, String... loreLines) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = Arrays.asList(loreLines);
        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

    }

    public static ItemStack getRankLevelColor(int level){

        ItemStack skull;
        switch(level){

            case 1:
                skull = makeSkull(Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_1.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODIxNGEyYmUzM2YwMzdiZmU2ZmEzZTI0YjFjMmZlMDRmMWU1ZmZkNzQ4ODA5NGQ0ZmY3YWJiMGIzNzBlZjViZSJ9fX0=");
                break;

            case 2:
                skull = makeSkull(Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_2.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWEwZjQ2MDQ2YWUxM2QzMTkzZDQyNTcyZmRiY2I2MmVhMWQ2OWMzODA3ZjA2ZTQwYmQxMTc4MmY1MTQxNGM0NCJ9fX0=");
                break;

            case 3:
                skull = makeSkull(Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_3.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTlhMWMxOTFlMGViYWJlODlkZGYxOGE4YmFjOGY0MjgwZTNhYzZiYzY2MWMxM2NlMWRmZjY3NGRhZDI4ODVlMyJ9fX0=");
                break;

            case 4:
                skull = makeSkull(Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_4.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTFmZGM4YTk1YzEzM2NlYTRlZDNlNGQ0Njg0MWNkMjM1YmRmYmJlZjYwN2I0MDAzYjM5ZjQ0NzQ1NzQ5OTQyMSJ9fX0=");
                break;

            case 5:
                skull = makeSkull(Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_5.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmNmZTg2ODQ4MjdiMDUxM2UzMTBiNDVlODAyMzc2ZTEzM2YxYTI4MmZkYzEzNTBjZGQ0ZjdiZWExYmNjNzllZiJ9fX0=");
                break;

            default:
                TownsAndNations.getPluginLogger().info("Error in role color");
                skull = makeSkull(Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_5.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODIxNGEyYmUzM2YwMzdiZmU2ZmEzZTI0YjFjMmZlMDRmMWU1ZmZkNzQ4ODA5NGQ0ZmY3YWJiMGIzNzBlZjViZSJ9fX0=");
                break;
        }

        addLore(
                skull,
                Lang.GUI_TOWN_MEMBERS_CHANGE_ROLE_PRIORITY_DESC1.getTranslation()
        );
        return skull;


    }

}
