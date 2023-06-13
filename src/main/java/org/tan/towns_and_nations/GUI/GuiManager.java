package org.tan.towns_and_nations.GUI;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.TownsAndNations;
import org.tan.towns_and_nations.utils.HeadUtils;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;

import java.util.*;

import static org.tan.towns_and_nations.utils.HeadUtils.getCustomLoreItem;
import static org.tan.towns_and_nations.utils.HeadUtils.getTownIcon;

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

        ItemStack GoldPurse = getCustomLoreItem(Material.GOLD_NUGGET, "Balance","You have " + PlayerStatStorage.getStatUUID(p.getUniqueId().toString()).getBalance() + " gold");

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
        if(PlayerStatStorage.getStatUUID(p.getUniqueId().toString()).haveTown()){
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


        ItemStack TownIcon = getTownIcon(PlayerStatStorage.getStatUUID(p.getUniqueId().toString()).getTownId());

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
    //Gui menu TownRelation //////////
    public static void OpenTownRelation(Player p) {

        Inventory inventory = Bukkit.createInventory(p,27, ChatColor.BLACK + "Town Relation");

        ItemStack warCategory = getCustomLoreItem(Material.IRON_SWORD,"War","Manage town you are at war with");
        ItemStack EmbargoCategory = getCustomLoreItem(Material.BARRIER,"Embargo","Manage town you are at war with");
        ItemStack NAPCategory = getCustomLoreItem(Material.WRITABLE_BOOK,"Non-aggression pact","Manage town you are at war with");
        ItemStack AllianceCategory = getCustomLoreItem(Material.CAMPFIRE,"Alliance","Manage town you are allied with");


        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Back", null);



        inventory.setItem(10, warCategory);
        inventory.setItem(12, EmbargoCategory);
        inventory.setItem(14, NAPCategory);
        inventory.setItem(16, AllianceCategory);

        inventory.setItem(18, getBackArrow);

        p.openInventory(inventory);
    }
    //Gui menu TownRelation //////////
    public static void OpenTownRelations(Player p, String relation) {

        TownDataClass playerTown = TownDataStorage.getTown(PlayerStatStorage.getStatUUID(p.getUniqueId().toString()).getTownId());
        ArrayList<String> TownListUUID = playerTown.getRelations().getOne(relation);

        Inventory inventory = Bukkit.createInventory(p,27, ChatColor.BLACK + "Town Relation - " + relation);

        int i = 0;
        for(String townUUID : TownListUUID){
            ItemStack townIcon = HeadUtils.getTownIcon(townUUID);
            inventory.setItem(i, townIcon);


            i = i+1;
        }


        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Back", null);

        ItemStack addTownButton = HeadUtils.makeSkull("add town","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
        ItemStack removeTownButton = HeadUtils.makeSkull("remove town","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=");

        ItemStack nextPageButton = HeadUtils.makeSkull("next page","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2MjYyYWYxZDVmNDE0YzU5NzA1NWMyMmUzOWNjZTE0OGU1ZWRiZWM0NTU1OWEyZDZiODhjOGQ2N2I5MmVhNiJ9fX0=");
        ItemStack previousPageButton = HeadUtils.makeSkull("previous page","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQyZmRlOGI4MmU4YzFiOGMyMmIyMjY3OTk4M2ZlMzVjYjc2YTc5Nzc4NDI5YmRhZGFiYzM5N2ZkMTUwNjEifX19");


        inventory.setItem(18, getBackArrow);
        inventory.setItem(20,addTownButton);
        inventory.setItem(21,removeTownButton);

        inventory.setItem(24,previousPageButton);
        inventory.setItem(25,nextPageButton);


        p.openInventory(inventory);
    }
    public static void OpenTownRelationInteraction(Player p,String action,String relation) {
        Inventory inventory = Bukkit.createInventory(p,27, ChatColor.BLACK + "Town relation - selection");

        int i = 0;
        LinkedHashMap<String,TownDataClass> towns =  TownDataStorage.getTownList();
        for (Map.Entry<String, TownDataClass> entry : towns.entrySet()) {
            String cle = entry.getKey();
            TownDataClass town = entry.getValue();
            ItemStack townIcon = HeadUtils.getTownIcon(town.getTownId());
            ItemMeta townItemMeta= townIcon.getItemMeta();

            NamespacedKey key = new NamespacedKey(TownsAndNations.getPlugin(), "townId");
            townItemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, town.getTownId());
            key = new NamespacedKey(TownsAndNations.getPlugin(), "action");
            townItemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, action);
            key = new NamespacedKey(TownsAndNations.getPlugin(), "relation");
            townItemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, relation);




            townIcon.setItemMeta(townItemMeta);


            inventory.setItem(i, townIcon);
            i = i+1;

        }

        p.openInventory(inventory);
    }

    //Gui menu TownSettings //////////
    public static void OpenTownSettings(Player p) {

        Inventory inventory = Bukkit.createInventory(p,27, ChatColor.BLACK + "Town Settings");

        ItemStack TownIcon = getTownIcon(PlayerStatStorage.getStatUUID(p.getUniqueId().toString()).getTownId());
        ItemStack leaveTown = getCustomLoreItem(Material.BARRIER, "Leave Town", "Quit the town \"" + TownDataStorage.getTown(PlayerStatStorage.getStatUUID(p.getUniqueId().toString()).getTownId()).getTownName() + "\" ?");
        ItemStack deleteTown = getCustomLoreItem(Material.BARRIER, "Delete Town", "Delete the town \"" + TownDataStorage.getTown(PlayerStatStorage.getStatUUID(p.getUniqueId().toString()).getTownId()).getTownName() + "\" ?");

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


        TownDataClass town = TownDataStorage.getTown(PlayerStatStorage.getStatUUID(p.getUniqueId().toString()).getTownId());

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

    //Gui menu TownEconomy //////////
    public static void OpenTownEconomy(Player p){

    }




}
