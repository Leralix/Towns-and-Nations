package org.tan.towns_and_nations.GUI;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.util.HeadUtils;
import org.tan.towns_and_nations.utils.PlayerStatStorage;
import org.tan.towns_and_nations.utils.TownDataStorage;

import java.lang.reflect.Field;
import java.util.*;

public class GuiManager {

    //Gui menu Main Menu //////////
    public static void OpenMainMenu(Player p) {

        Inventory inventory = Bukkit.createInventory(p,27, ChatColor.BLACK + "Towns and Nations");

        ItemStack KingdomHead = HeadUtils.makeSkull("Kingdom","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY5MTk2YjMzMGM2Yjg5NjJmMjNhZDU2MjdmYjZlY2NlNDcyZWFmNWM5ZDQ0Zjc5MWY2NzA5YzdkMGY0ZGVjZSJ9fX0=");
        ItemStack RegionHead = HeadUtils.makeSkull("Region","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljMTgzMmU0ZWY1YzRhZDljNTE5ZDE5NGIxOTg1MDMwZDI1NzkxNDMzNGFhZjI3NDVjOWRmZDYxMWQ2ZDYxZCJ9fX0=");
        ItemStack TownHead = HeadUtils.makeSkull("Town","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=");
        ItemStack PlayerHead = HeadUtils.getPlayerHead("Profil",p);
        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Quit", null);



        inventory.setItem(10, KingdomHead);
        inventory.setItem(12, RegionHead);
        inventory.setItem(14, TownHead);
        inventory.setItem(16, PlayerHead);
        inventory.setItem(18, getBackArrow);

        p.openInventory(inventory);
    }
    //Gui menu Profile //////////
    public static void OpenProfileMenu(Player p) {

        Inventory inventory = Bukkit.createInventory(p,27, ChatColor.BLACK + "Profil");

        ItemStack PlayerHead = HeadUtils.getPlayerHead("Votre Profil",p);

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
    //Gui menu SearchTown //////////
    public static void OpenSearchTownMenu(Player p) {
        Inventory inventory = Bukkit.createInventory(p,27, ChatColor.BLACK + "Search Town");

        HashMap<String,TownDataClass> townDataStorage = TownDataStorage.getTownList();

        int i = 0;
        for (Map.Entry<String, TownDataClass> entry : townDataStorage.entrySet()) {


            TownDataClass townDataClass = entry.getValue();
            String townId = townDataClass.getTownId();
            ItemStack townIcon = getTownIcon(townId);



            inventory.setItem(i, townIcon);
            i++;


            System.out.println(townDataClass.getTownName() + i);

        }





        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Back", null);


        inventory.setItem(18, getBackArrow);

        p.openInventory(inventory);
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

        Inventory inventory = Bukkit.createInventory(p,27, ChatColor.BLACK + "Town Menu");


        ItemStack TownIcon = getTownIcon(PlayerStatStorage.findStatUUID(p.getUniqueId().toString()).getTownId());

        ItemStack GoldIcon = HeadUtils.makeSkull("Treasury","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        ItemStack SkullIcon = HeadUtils.makeSkull("Members","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Q0ZDQ5NmIxZGEwNzUzNmM5NGMxMzEyNGE1ODMzZWJlMGM1MzgyYzhhMzM2YWFkODQ2YzY4MWEyOGQ5MzU2MyJ9fX0=");
        ItemStack ClaimIcon = HeadUtils.makeSkull("Claims","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=");

        ItemStack RelationIcon = HeadUtils.makeSkull("Relations","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUwN2Q2ZGU2MzE4MzhlN2E3NTcyMGU1YjM4ZWYxNGQyOTY2ZmRkODQ4NmU3NWQxZjY4MTJlZDk5YmJjYTQ5OSJ9fX0=");
        ItemStack LevelIcon = HeadUtils.makeSkull("Town Level","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmJlNTI5YWI2YjJlYTdjNTBkOTE5MmQ4OWY4OThmZDdkYThhOWU3NTBkMzc4Mjk1ZGY3MzIwNWU3YTdlZWFlMCJ9fX0=");
        ItemStack SettingIcon = HeadUtils.makeSkull("Settings","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVkMmNiMzg0NThkYTE3ZmI2Y2RhY2Y3ODcxNjE2MDJhMjQ5M2NiZjkzMjMzNjM2MjUzY2ZmMDdjZDg4YTljMCJ9fX0=");

        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Back", null);

        inventory.setItem(4, TownIcon);

        inventory.setItem(10, GoldIcon);
        inventory.setItem(11, SkullIcon);
        inventory.setItem(12, ClaimIcon);

        inventory.setItem(14, RelationIcon);
        inventory.setItem(15, LevelIcon);
        inventory.setItem(16, SettingIcon);

        inventory.setItem(18, getBackArrow);

        p.openInventory(inventory);
    }
    //Gui menu TownSettings //////////
    public static void OpenTownSettings(Player p) {

        Inventory inventory = Bukkit.createInventory(p,27, ChatColor.BLACK + "Town Settings");

        ItemStack TownIcon = getTownIcon(PlayerStatStorage.findStatUUID(p.getUniqueId().toString()).getTownId());
        ItemStack leaveTown = getCustomLoreItem(Material.BARRIER, "Leave Town", "Quit the town \"" + TownDataStorage.getTown(PlayerStatStorage.findStatUUID(p.getUniqueId().toString()).getTownId()).getTownName() + "\" ?");
        ItemStack deleteTown = getCustomLoreItem(Material.BARRIER, "Delete Town", "Delete the town \"" + TownDataStorage.getTown(PlayerStatStorage.findStatUUID(p.getUniqueId().toString()).getTownId()).getTownName() + "\" ?");

        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Back", null);

        inventory.setItem(4, TownIcon);

        inventory.setItem(10, leaveTown);
        inventory.setItem(11, deleteTown);

        inventory.setItem(18, getBackArrow);

        p.openInventory(inventory);
    }

    //Gui menu TownMembers //////////
        public static void OpenTownMemberList(Player p) {

        Inventory inventory = Bukkit.createInventory(p,27, ChatColor.BLACK + "Town Members");


        TownDataClass town = TownDataStorage.getTown(PlayerStatStorage.findStatUUID(p.getUniqueId().toString()).getTownId());

        ArrayList<String> players = town.getPlayerList();

        int i = 0;
        for (String playerUUID: players) {

            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));

            ItemStack playerHead = HeadUtils.getPlayerHead(player.getName(),player);

            inventory.setItem(i, playerHead);
            i++;


        }



        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Back", null);
        inventory.setItem(18, getBackArrow);

        p.openInventory(inventory);
    }

    public static void OpenTownEconomy(Player p){

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
            return HeadUtils.getPlayerHead(town.getTownName(), Bukkit.getOfflinePlayer(UUID.fromString(town.getUuidLeader())));
        }
        else {
            return itemStack;
        }

    }





}
