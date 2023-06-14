package org.tan.towns_and_nations.GUI;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.utils.HeadUtils;
import org.tan.towns_and_nations.storage.PlayerChatListenerStorage;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;

import java.util.*;

import static org.tan.towns_and_nations.utils.HeadUtils.getCustomLoreItem;
import static org.tan.towns_and_nations.utils.HeadUtils.getTownIcon;
import static org.tan.towns_and_nations.storage.TownDataStorage.getTownList;


public class GuiManager2 {

    //done
    public static void OpenMainMenu(Player player){
        //Paramètres pincipaux

        String name = "Main menu";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        ItemStack KingdomHead = HeadUtils.makeSkull("Kingdom","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY5MTk2YjMzMGM2Yjg5NjJmMjNhZDU2MjdmYjZlY2NlNDcyZWFmNWM5ZDQ0Zjc5MWY2NzA5YzdkMGY0ZGVjZSJ9fX0=");
        ItemStack RegionHead = HeadUtils.makeSkull("Region","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljMTgzMmU0ZWY1YzRhZDljNTE5ZDE5NGIxOTg1MDMwZDI1NzkxNDMzNGFhZjI3NDVjOWRmZDYxMWQ2ZDYxZCJ9fX0=");
        ItemStack TownHead = HeadUtils.makeSkull("Town","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=");
        ItemStack PlayerHead = HeadUtils.getPlayerHead("Profil",player);
        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Quit", null);


        GuiItem Kingdom = ItemBuilder.from(KingdomHead).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage("En developpement");
        });
        GuiItem Region = ItemBuilder.from(RegionHead).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage("En developpement");
        });
        GuiItem Town = ItemBuilder.from(TownHead).asGuiItem(event -> {
            event.setCancelled(true);
            if(PlayerStatStorage.getStat(player.getUniqueId().toString()).haveTown()){
                OpenTownMenuHaveTown(player);
            }
            else{
                openTownMenuNoTown(player);
            }
        });
        GuiItem Player = ItemBuilder.from(PlayerHead).asGuiItem(event -> {
            event.setCancelled(true);
            openProfileMenu(player);
        });
        GuiItem Back = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            player.closeInventory();
        });

        gui.setItem(10,Kingdom);
        gui.setItem(12,Region);
        gui.setItem(14,Town);
        gui.setItem(16,Player);
        gui.setItem(18,Back);

        gui.open(player);
    }
    //Done
    public static void openProfileMenu(Player player){
        String name = "Profile";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        ItemStack PlayerHead = HeadUtils.getPlayerHead("Votre Profil",player);
        ItemStack GoldPurse = getCustomLoreItem(Material.GOLD_NUGGET, "Balance","You have " + PlayerStatStorage.getStat(player.getUniqueId().toString()).getBalance() + " gold");
        ItemStack killList = getCustomLoreItem(Material.IRON_SWORD, "Kills","You killed " + player.getStatistic(Statistic.MOB_KILLS) + " mobs");
        int time = player.getStatistic(Statistic.PLAY_ONE_MINUTE) /20 / 86400;
        ItemStack lastDeath = getCustomLoreItem(Material.SKELETON_SKULL, "Time Alive","You survived for " + time + " days");
        ItemStack totalRpKills = getCustomLoreItem(Material.SKELETON_SKULL, "Murder","You killed " + "//En developpement//" + " players");
        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Back", null);

        GuiItem Head = ItemBuilder.from(PlayerHead).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage("En developpement");
        });
        GuiItem Gold = ItemBuilder.from(GoldPurse).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage("En developpement");
        });
        GuiItem Kill = ItemBuilder.from(killList).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage("En developpement");
        });
        GuiItem LD = ItemBuilder.from(lastDeath).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage("En developpement");
        });
        GuiItem RPkill = ItemBuilder.from(totalRpKills).asGuiItem(event -> {
            event.setCancelled(true);
            player.closeInventory();
        });
        GuiItem Back = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenMainMenu(player);
        });

        gui.setItem(4, Head);
        gui.setItem(10, Gold);
        gui.setItem(12, Kill);
        gui.setItem(14, LD);
        gui.setItem(16, RPkill);
        gui.setItem(18, Back);

        gui.open(player);
    }
    //Done
    public static void openTownMenuNoTown(Player player){
        String name = "Town";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        ItemStack createNewland = getCustomLoreItem(Material.GRASS_BLOCK, "Create new Town","Cost: 100 gold");
        ItemStack joinLand = getCustomLoreItem(Material.ANVIL, "Join a Town","Look at every public town");
        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Back", null);

        GuiItem _create = ItemBuilder.from(createNewland).asGuiItem(event -> {
            event.setCancelled(true);
            PlayerDataClass playerStat = PlayerStatStorage.getStat(player.getUniqueId().toString());
            if (playerStat.getBalance() < 100) {
                player.sendMessage("You don't have enough money");
            } else {
                player.sendMessage("Write the name of the town in the chat");
                player.closeInventory();
                PlayerChatListenerStorage.addPlayer(player);
            }
        });

        GuiItem _join = ItemBuilder.from(joinLand).asGuiItem(event -> {
            event.setCancelled(true);
            OpenSearchTownMenu(player);
        });
        GuiItem _back = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenMainMenu(player);
        });

        gui.setItem(11, _create);
        gui.setItem(15, _join);
        gui.setItem(18, _back);

        gui.open(player);
    }
    //Done
    public static void OpenSearchTownMenu(Player player) {

        String name = "Town";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        HashMap<String, TownDataClass> townDataStorage = getTownList();

        int i = 0;
        for (Map.Entry<String, TownDataClass> entry : townDataStorage.entrySet()) {


            TownDataClass townDataClass = entry.getValue();
            String townId = townDataClass.getTownId();
            ItemStack townIcon = getTownIcon(townId);

            GuiItem _townIteration = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);
                player.sendMessage("en dev");
            });

            gui.setItem(i, _townIteration);
            i++;

        }
        gui.open(player);
    }
    //Done
    public static void OpenTownMenuHaveTown(Player player) {

        String name = "Town";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        ItemStack TownIcon = getTownIcon(PlayerStatStorage.getStat(player.getUniqueId().toString()).getTownId());
        ItemStack GoldIcon = HeadUtils.makeSkull("Treasury","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        ItemStack SkullIcon = HeadUtils.makeSkull("Members","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Q0ZDQ5NmIxZGEwNzUzNmM5NGMxMzEyNGE1ODMzZWJlMGM1MzgyYzhhMzM2YWFkODQ2YzY4MWEyOGQ5MzU2MyJ9fX0=");
        ItemStack ClaimIcon = HeadUtils.makeSkull("Claims","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=");
        ItemStack RelationIcon = HeadUtils.makeSkull("Relations","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUwN2Q2ZGU2MzE4MzhlN2E3NTcyMGU1YjM4ZWYxNGQyOTY2ZmRkODQ4NmU3NWQxZjY4MTJlZDk5YmJjYTQ5OSJ9fX0=");
        ItemStack LevelIcon = HeadUtils.makeSkull("Town Level","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmJlNTI5YWI2YjJlYTdjNTBkOTE5MmQ4OWY4OThmZDdkYThhOWU3NTBkMzc4Mjk1ZGY3MzIwNWU3YTdlZWFlMCJ9fX0=");
        ItemStack SettingIcon = HeadUtils.makeSkull("Settings","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVkMmNiMzg0NThkYTE3ZmI2Y2RhY2Y3ODcxNjE2MDJhMjQ5M2NiZjkzMjMzNjM2MjUzY2ZmMDdjZDg4YTljMCJ9fX0=");
        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Back", null);

        GuiItem _townIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _goldIcon = ItemBuilder.from(GoldIcon).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage("en dev");
        });
        GuiItem _membersIcon = ItemBuilder.from(SkullIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMemberList(player);
        });
        GuiItem _claimIcon = ItemBuilder.from(ClaimIcon).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage("en dev");
        });
        GuiItem _relationIcon = ItemBuilder.from(RelationIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelations(player);
        });
        GuiItem _levelIcon = ItemBuilder.from(LevelIcon).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage("en dev");
        });
        GuiItem _settingsIcon = ItemBuilder.from(SettingIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownSettings(player);
        });
        GuiItem _backIcon = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenMainMenu(player);
        });


        gui.setItem(4, _townIcon);
        gui.setItem(10, _goldIcon);
        gui.setItem(11, _membersIcon);
        gui.setItem(12, _claimIcon);
        gui.setItem(14, _relationIcon);
        gui.setItem(15, _levelIcon);
        gui.setItem(16, _settingsIcon);
        gui.setItem(18, _backIcon);

        gui.open(player);
    }
    //Done
    public static void OpenTownMemberList(Player player) {

        String name = "Town";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        TownDataClass town = TownDataStorage.getTown(PlayerStatStorage.getStat(player.getUniqueId().toString()).getTownId());
        ArrayList<String> players = town.getPlayerList();

        int i = 0;
        for (String playerUUID: players) {

            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));

            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate.getName(),playerIterate);

            GuiItem _playerIcon = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
            });

            gui.setItem(i, _playerIcon);
            i++;
        }
        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Back", null);
        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuHaveTown(player);
        });

        gui.setItem(3,1, _getBackArrow);

        gui.open(player);

    }
    //Done
    public static void OpenTownSettings(Player player) {

        String name = "Town";
        int nRow = 3;
        PlayerDataClass playerStat = PlayerStatStorage.getStat(player.getUniqueId().toString());

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        ItemStack TownIcon = getTownIcon(PlayerStatStorage.getStat(player.getUniqueId().toString()).getTownId());
        ItemStack leaveTown = getCustomLoreItem(Material.BARRIER, "Leave Town", "Quit the town \"" + TownDataStorage.getTown(PlayerStatStorage.getStat(player.getUniqueId().toString()).getTownId()).getTownName() + "\" ?");
        ItemStack deleteTown = getCustomLoreItem(Material.BARRIER, "Delete Town", "Delete the town \"" + TownDataStorage.getTown(PlayerStatStorage.getStat(player.getUniqueId().toString()).getTownId()).getTownName() + "\" ?");
        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Back", null);

        GuiItem _townIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _leaveTown = ItemBuilder.from(leaveTown).asGuiItem(event -> {
            event.setCancelled(true);
            if (TownDataStorage.getTown(playerStat.getTownId()).getUuidLeader().equals(playerStat.getUuid())) {
                player.sendMessage("You can't leave a town you are the leader, you need to disband it or give the leadership to someone else");
            } else {
                TownDataStorage.getTown(playerStat.getTownId()).removePlayer(player.getUniqueId().toString());
                playerStat.setTownId(null);
                player.sendMessage("You left the town");
                player.closeInventory();
            }
        });
        GuiItem _deleteTown = ItemBuilder.from(deleteTown).asGuiItem(event -> {
            event.setCancelled(true);
            if (!TownDataStorage.getTown(playerStat.getTownId()).getUuidLeader().equals(playerStat.getUuid())) {
                player.sendMessage("You can't delete a town if you are not the leader");
            } else {
                TownDataStorage.removeTown(playerStat.getTownId());
                playerStat.setTownId(null);
                player.closeInventory();
                player.sendMessage("Town deleted");
            }
        });
        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuHaveTown(player);
        });


        gui.setItem(4, _townIcon);
        gui.setItem(10, _leaveTown);
        gui.setItem(11, _deleteTown);
        gui.setItem(18, _getBackArrow);

        gui.open(player);
    }

    public static void OpenTownRelations(Player player) {

        String name = "Town";
        int nRow = 3;
        PlayerDataClass playerStat = PlayerStatStorage.getStat(player.getUniqueId().toString());

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        ItemStack warCategory = getCustomLoreItem(Material.IRON_SWORD,"War","Manage town you are at war with");
        ItemStack EmbargoCategory = getCustomLoreItem(Material.BARRIER,"Embargo","Manage town you are at war with");
        ItemStack NAPCategory = getCustomLoreItem(Material.WRITABLE_BOOK,"Non-aggression pact","Manage town you are at war with");
        ItemStack AllianceCategory = getCustomLoreItem(Material.CAMPFIRE,"Alliance","Manage town you are allied with");

        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Back", null);

        GuiItem _warCategory = ItemBuilder.from(warCategory).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelation(player,"war");
        });
        GuiItem _EmbargoCategory = ItemBuilder.from(EmbargoCategory).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelation(player,"embargo");

        });
        GuiItem _NAPCategory = ItemBuilder.from(NAPCategory).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelation(player,"nap");

        });
        GuiItem _AllianceCategory = ItemBuilder.from(AllianceCategory).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelation(player,"alliance");
        });
        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuHaveTown(player);
        });

        gui.setItem(10, _warCategory);
        gui.setItem(12, _EmbargoCategory);
        gui.setItem(14, _NAPCategory);
        gui.setItem(16, _AllianceCategory);

        gui.setItem(18, _getBackArrow);

        gui.open(player);
    }

    public static void OpenTownRelation(Player player, String relation) {

        String name = "Town - Relation";
        int nRow = 4;
        PlayerDataClass playerStat = PlayerStatStorage.getStat(player.getUniqueId().toString());

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        TownDataClass playerTown = TownDataStorage.getTown(playerStat.getTownId());

        ArrayList<String> TownListUUID = playerTown.getRelations().getOne(relation);
        player.sendMessage(relation);
        player.sendMessage(TownListUUID.toString());
        player.sendMessage(playerTown.getRelations().getAll().toString());
        int i = 0;
        for(String townUUID : TownListUUID){
            ItemStack townIcon = HeadUtils.getTownIcon(townUUID);

            GuiItem _town = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);
            });
            gui.setItem(i, _town);

            i = i+1;
        }


        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Back", null);

        ItemStack addTownButton = HeadUtils.makeSkull("add town","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
        ItemStack removeTownButton = HeadUtils.makeSkull("remove town","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=");

        ItemStack nextPageButton = HeadUtils.makeSkull("next page","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2MjYyYWYxZDVmNDE0YzU5NzA1NWMyMmUzOWNjZTE0OGU1ZWRiZWM0NTU1OWEyZDZiODhjOGQ2N2I5MmVhNiJ9fX0=");
        ItemStack previousPageButton = HeadUtils.makeSkull("previous page","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQyZmRlOGI4MmU4YzFiOGMyMmIyMjY3OTk4M2ZlMzVjYjc2YTc5Nzc4NDI5YmRhZGFiYzM5N2ZkMTUwNjEifX19");

        GuiItem _back = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelations(player);
        });
        GuiItem _add = ItemBuilder.from(addTownButton).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelationModification(player,"add",relation);
        });
        GuiItem _remove = ItemBuilder.from(removeTownButton).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelationModification(player,"remove",relation);
        });
        GuiItem _next = ItemBuilder.from(nextPageButton).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _previous = ItemBuilder.from(previousPageButton).asGuiItem(event -> {
            event.setCancelled(true);
        });

        gui.setItem(4,1, _back);
        gui.setItem(4,3,_add);
        gui.setItem(4,5,_remove);

        gui.setItem(4,7,_next);
        gui.setItem(4,8,_previous);

        gui.open(player);
    }

    public static void OpenTownRelationModification(Player player, String action, String relation) {

        String name = "Town - Relation";
        int nRow = 4;
        PlayerDataClass playerStat = PlayerStatStorage.getStat(player.getUniqueId().toString());

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        TownDataClass playerTown = TownDataStorage.getTown(PlayerStatStorage.getStat(player.getUniqueId().toString()).getTownId());

        LinkedHashMap<String, TownDataClass> allTown = getTownList();
        ArrayList<String> TownListUUID = playerTown.getRelations().getOne(relation);

        List<String> townNoRelation = new ArrayList<>(allTown.keySet());

        // Retirer tous les éléments de la seconde liste de la première
        townNoRelation.removeAll(TownListUUID);
        townNoRelation.remove(playerTown.getTownId());

        int i = 0;
        for(String townUUID : townNoRelation){
            ItemStack townIcon = HeadUtils.getTownIcon(townUUID);

            GuiItem _town = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);

                String message = "Guerre déclarée à : " + townIcon.getItemMeta().getDisplayName();
                player.sendMessage(message);

                if(action.equals("add")){
                    player.sendMessage("test");
                    player.sendMessage(TownDataStorage.getTown(playerTown.getTownId()).toString());
                    player.sendMessage("test");
                    TownDataStorage.getTown(playerTown.getTownId()).addTownRelations(relation,townUUID);
                }
                else
                    TownDataStorage.getTown(playerTown.getTownId()).removeTownRelations(relation,player.getUniqueId().toString());



                OpenTownRelation(player,relation);
            });

            gui.setItem(i, _town);


            i = i+1;
        }


        ItemStack getBackArrow = getCustomLoreItem(Material.ARROW, "Back", null);

        ItemStack nextPageButton = HeadUtils.makeSkull("next page","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2MjYyYWYxZDVmNDE0YzU5NzA1NWMyMmUzOWNjZTE0OGU1ZWRiZWM0NTU1OWEyZDZiODhjOGQ2N2I5MmVhNiJ9fX0=");
        ItemStack previousPageButton = HeadUtils.makeSkull("previous page","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQyZmRlOGI4MmU4YzFiOGMyMmIyMjY3OTk4M2ZlMzVjYjc2YTc5Nzc4NDI5YmRhZGFiYzM5N2ZkMTUwNjEifX19");

        GuiItem _back = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelation(player,relation);
        });
        GuiItem _next = ItemBuilder.from(nextPageButton).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _previous = ItemBuilder.from(previousPageButton).asGuiItem(event -> {
            event.setCancelled(true);
        });

        gui.setItem(4,1, _back);

        gui.setItem(4,7,_next);
        gui.setItem(4,8,_previous);

        gui.open(player);
    }



}
