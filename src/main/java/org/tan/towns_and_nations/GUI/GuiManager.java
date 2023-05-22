package org.tan.towns_and_nations.GUI;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.utils.PlayerStatStorage;
import org.tan.towns_and_nations.utils.TownDataStorage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuiManager {

    //Gui menu Main Menu //////////
    public static void OpenMainMenu(Player p) {

        Inventory inventory = Bukkit.createInventory(p,27, ChatColor.BLACK + "Towns and Nations");

        ItemStack KingdomHead = makeSkull("Kingdom","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY5MTk2YjMzMGM2Yjg5NjJmMjNhZDU2MjdmYjZlY2NlNDcyZWFmNWM5ZDQ0Zjc5MWY2NzA5YzdkMGY0ZGVjZSJ9fX0=");
        ItemStack RegionHead = makeSkull("Region","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljMTgzMmU0ZWY1YzRhZDljNTE5ZDE5NGIxOTg1MDMwZDI1NzkxNDMzNGFhZjI3NDVjOWRmZDYxMWQ2ZDYxZCJ9fX0=");
        ItemStack TownHead = makeSkull("Town","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=");
        ItemStack PlayerHead = getPlayerHead("Profil",p);
        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Quit", null);



        inventory.setItem(10, KingdomHead);
        inventory.setItem(12, RegionHead);
        inventory.setItem(14, TownHead);
        inventory.setItem(16, PlayerHead);
        inventory.setItem(18, getBackArrow);

        p.openInventory(inventory);
    }
    //Gui menu Profil //////////
    public static void OpenProfileMenu(Player p) {

        Inventory inventory = Bukkit.createInventory(p,27, ChatColor.BLACK + "Profil");

        ItemStack PlayerHead = getPlayerHead("Votre Profil",p);

        ItemStack GoldPurse = getCustomLoreItem(Material.GOLD_NUGGET, "Balance","You have " + PlayerStatStorage.findStatUUID(p.getUniqueId().toString()).getBalance() + " gold");

        ItemStack killList = getCustomLoreItem(Material.IRON_SWORD, "Kills","You killed " + p.getStatistic(Statistic.MOB_KILLS) + " mobs");

        int time = p.getStatistic(Statistic.PLAY_ONE_MINUTE) /20 / 86400;
        ItemStack lastDeath = getCustomLoreItem(Material.SKELETON_SKULL, "Time Alive","You survived for " + time + " days");

        ItemStack totalRpKills = getCustomLoreItem(Material.SKELETON_SKULL, "Murder","You killed " + "//En developpement//" + " players");

        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Back", null);

        inventory.setItem(4, PlayerHead);
        inventory.setItem(10, GoldPurse);
        inventory.setItem(12, killList);
        inventory.setItem(14, lastDeath);
        inventory.setItem(16, totalRpKills);
        inventory.setItem(18, getBackArrow);


        p.openInventory(inventory);
    }
    //Gui menu Town //////////
    public static void OpenTownMenu(Player p) {
        System.out.println("tesrt");
        if(PlayerStatStorage.findStatUUID(p.getUniqueId().toString()).haveTown()){
            OpenTownMenuHaveTown(p);
        }
        else{
            OpenTownMenuNoTown(p);
        }
    }
    //Gui menu NoTown //////////
    public static void OpenTownMenuNoTown(Player p) {

        Inventory inventory = Bukkit.createInventory(p,27, ChatColor.BLACK + "Town");

        ItemStack createNewland = getCustomLoreItem(Material.GRASS_BLOCK, "Create new Town","Cost: 100 gold");
        ItemStack joinLand = getCustomLoreItem(Material.ANVIL, "Join a Town","Look at every public town");
        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Back", null);


        inventory.setItem(11, createNewland);
        inventory.setItem(15, joinLand);
        inventory.setItem(18, getBackArrow);

        p.openInventory(inventory);
    }
    //Gui menu HaveTown //////////
    public static void OpenTownMenuHaveTown(Player p) {

        Inventory inventory = Bukkit.createInventory(p,27, ChatColor.BLACK + "Town");


        ItemStack TownIcon = getTownIcon(PlayerStatStorage.findStatUUID(p.getUniqueId().toString()).getTownId());


        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Back", null);

        inventory.setItem(4, TownIcon);
        inventory.setItem(18, getBackArrow);

        p.openInventory(inventory);
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
        meta.setDisplayName(name);
        skull.setItemMeta(meta);
        return skull;
    }

    public static ItemStack getPlayerHead(String headName, Player p){
        ItemStack PlayerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) PlayerHead.getItemMeta();
        skullMeta.setDisplayName(headName);
        skullMeta.setOwningPlayer(p);
        PlayerHead.setItemMeta(skullMeta);
        return PlayerHead;
    }

    public static ItemStack getCustomLoreItem(Material itemMaterial, String itemName, String itemLoreOneLine){
        ItemStack item = new ItemStack(itemMaterial);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemName);
        if(itemLoreOneLine != null){
            List<String> itemLore = new ArrayList<String>();
            itemLore.add(itemLoreOneLine);
            meta.setLore(itemLore);
        }

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getTownIcon(String TownId){

        if(TownId == null){
            System.out.println("Erreur critique: Fonction accesible seulement a un joueur qui a une ville apellée par un joueur qui n'en possède pas");
            return null;
        }

        TownDataClass town = TownDataStorage.getTown(TownId);
        ItemStack itemStack = town.getTownIconItemStack();
        if (itemStack == null){
            return getPlayerHead(town.getTownName(), Bukkit.getPlayer(UUID.fromString(town.getUuidLeader())));
        }
        else {
            return itemStack;
        }

    }


}
