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
import org.tan.towns_and_nations.DataClass.TownLevel;
import org.tan.towns_and_nations.DataClass.TownRank;
import org.tan.towns_and_nations.Lang.Lang;
import org.tan.towns_and_nations.utils.ChatUtils;
import org.tan.towns_and_nations.utils.HeadUtils;
import org.tan.towns_and_nations.storage.PlayerChatListenerStorage;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;
import static org.tan.towns_and_nations.storage.TownDataStorage.getTownList;
import static org.tan.towns_and_nations.storage.TownDataStorage.townDataMap;
import java.util.ArrayList;
import java.util.stream.Collectors;


import java.util.*;



public class GuiManager2 {

    //done
    public static void OpenMainMenu(Player player){

        if(PlayerStatStorage.getStat(player) == null){
            PlayerStatStorage.createPlayerDataClass(player);
        }

        String name = "Main menu";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        ItemStack KingdomHead = HeadUtils.makeSkull(Lang.GUI_KINGDOM_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY5MTk2YjMzMGM2Yjg5NjJmMjNhZDU2MjdmYjZlY2NlNDcyZWFmNWM5ZDQ0Zjc5MWY2NzA5YzdkMGY0ZGVjZSJ9fX0=");
        ItemStack RegionHead = HeadUtils.makeSkull(Lang.GUI_REGION_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljMTgzMmU0ZWY1YzRhZDljNTE5ZDE5NGIxOTg1MDMwZDI1NzkxNDMzNGFhZjI3NDVjOWRmZDYxMWQ2ZDYxZCJ9fX0=");
        ItemStack TownHead = HeadUtils.makeSkull(Lang.GUI_TOWN_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=");
        ItemStack PlayerHead = HeadUtils.getPlayerHead(Lang.GUI_PROFILE_ICON.getTranslation(),player);
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, "Quit");


        GuiItem Kingdom = ItemBuilder.from(KingdomHead).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(Lang.GUI_WARNING_STILL_IN_DEV.getTranslation());
        });
        GuiItem Region = ItemBuilder.from(RegionHead).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(Lang.GUI_WARNING_STILL_IN_DEV.getTranslation());
        });
        GuiItem Town = ItemBuilder.from(TownHead).asGuiItem(event -> {
            event.setCancelled(true);
            if(Objects.requireNonNull(PlayerStatStorage.getStat(player)).haveTown()){
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
        PlayerDataClass playerStat = PlayerStatStorage.getStat(player);

        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        ItemStack PlayerHead = HeadUtils.getPlayerHead(Lang.GUI_YOUR_PROFILE.getTranslation(),player);
        ItemStack GoldPurse = HeadUtils.getCustomLoreItem(Material.GOLD_NUGGET, Lang.GUI_YOUR_BALANCE.getTranslation(),Lang.GUI_YOUR_BALANCE_DESC1.getTranslation(playerStat.getBalance()));
        ItemStack killList = HeadUtils.getCustomLoreItem(Material.IRON_SWORD, Lang.GUI_YOUR_PVE_KILLS.getTranslation(),Lang.GUI_YOUR_PVE_KILLS_DESC1.getTranslation(player.getStatistic(Statistic.MOB_KILLS)));
        int time = player.getStatistic(Statistic.PLAY_ONE_MINUTE) /20 / 86400;
        ItemStack lastDeath = HeadUtils.getCustomLoreItem(Material.SKELETON_SKULL, Lang.GUI_YOUR_CURRENT_TIME_ALIVE.getTranslation(),Lang.GUI_YOUR_CURRENT_TIME_ALIVE_DESC1.getTranslation(time));
        ItemStack totalRpKills = HeadUtils.getCustomLoreItem(Material.SKELETON_SKULL, Lang.GUI_YOUR_CURRENT_MURDER.getTranslation(),Lang.GUI_YOUR_CURRENT_MURDER_DESC1.getTranslation("0"));
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());

        GuiItem Head = ItemBuilder.from(PlayerHead).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem Gold = ItemBuilder.from(GoldPurse).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem Kill = ItemBuilder.from(killList).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem LD = ItemBuilder.from(lastDeath).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem RPkill = ItemBuilder.from(totalRpKills).asGuiItem(event -> {
            event.setCancelled(true);
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


        PlayerDataClass playerStat = PlayerStatStorage.getStat(player);

        String name = "Town";
        int nRow = 3;
        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        ItemStack createNewLand = HeadUtils.getCustomLoreItem(Material.GRASS_BLOCK, Lang.GUI_NO_TOWN_CREATE_NEW_TOWN.getTranslation(),Lang.GUI_NO_TOWN_CREATE_NEW_TOWN_DESC1.getTranslation("100"));
        ItemStack joinLand = HeadUtils.getCustomLoreItem(Material.ANVIL, Lang.GUI_NO_TOWN_JOIN_A_TOWN.getTranslation(),Lang.GUI_NO_TOWN_JOIN_A_TOWN_DESC1.getTranslation(TownDataStorage.getNumberOfTown()));
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());

        GuiItem _create = ItemBuilder.from(createNewLand).asGuiItem(event -> {
            event.setCancelled(true);
            assert playerStat != null;
                if (playerStat.getBalance() < 100) {
                    player.sendMessage(Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.getTranslation(100 - playerStat.getBalance()));
                } else {
                    player.sendMessage(Lang.PLAYER_WRITE_TOWN_NAME_IN_CHAT.getTranslation());
                    player.closeInventory();
                    PlayerChatListenerStorage.addPlayer("creationVille",player);
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
            ItemStack townIcon = HeadUtils.getTownIconWithInformations(townId);

            GuiItem _townIteration = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);
                player.sendMessage(Lang.GUI_WARNING_STILL_IN_DEV.getTranslation());
            });

            gui.setItem(i, _townIteration);
            i++;

        }
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());
        GuiItem _back = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenMainMenu(player);
        });
        gui.setItem(3,1, _back);

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


        ItemStack TownIcon = HeadUtils.getTownIcon(PlayerStatStorage.getStat(player).getTownId());
        ItemStack GoldIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_TREASURY_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        ItemStack SkullIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_MEMBERS_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Q0ZDQ5NmIxZGEwNzUzNmM5NGMxMzEyNGE1ODMzZWJlMGM1MzgyYzhhMzM2YWFkODQ2YzY4MWEyOGQ5MzU2MyJ9fX0=");
        ItemStack ClaimIcon = HeadUtils.makeSkull(Lang.GUI_CLAIM_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=");
        ItemStack RelationIcon = HeadUtils.makeSkull(Lang.GUI_RELATION_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUwN2Q2ZGU2MzE4MzhlN2E3NTcyMGU1YjM4ZWYxNGQyOTY2ZmRkODQ4NmU3NWQxZjY4MTJlZDk5YmJjYTQ5OSJ9fX0=");
        ItemStack LevelIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_LEVEL_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmJlNTI5YWI2YjJlYTdjNTBkOTE5MmQ4OWY4OThmZDdkYThhOWU3NTBkMzc4Mjk1ZGY3MzIwNWU3YTdlZWFlMCJ9fX0=");
        ItemStack SettingIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_SETTINGS_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVkMmNiMzg0NThkYTE3ZmI2Y2RhY2Y3ODcxNjE2MDJhMjQ5M2NiZjkzMjMzNjM2MjUzY2ZmMDdjZDg4YTljMCJ9fX0=");
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());

        GuiItem _townIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _goldIcon = ItemBuilder.from(GoldIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownEconomics(player);
        });
        GuiItem _membersIcon = ItemBuilder.from(SkullIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMemberList(player);
        });
        GuiItem _claimIcon = ItemBuilder.from(ClaimIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownChunkMenu(player);
        });
        GuiItem _relationIcon = ItemBuilder.from(RelationIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelations(player);
        });
        GuiItem _levelIcon = ItemBuilder.from(LevelIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownLevel(player);
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
            PlayerDataClass playerStat = PlayerStatStorage.getStat(playerUUID);

            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate.getName(),playerIterate);
            HeadUtils.addLore(
                    playerHead,
                    Lang.GUI_TOWN_MEMBER_DESC1.getTranslation(playerStat.getTownRank()),
                    Lang.GUI_TOWN_MEMBER_DESC2.getTranslation(playerStat.getBalance())
            );
            GuiItem _playerIcon = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
            });

            gui.setItem(i, _playerIcon);
            i++;
        }

        ItemStack manageRanks = HeadUtils.getCustomLoreItem(Material.LADDER, Lang.GUI_TOWN_MEMBERS_MANAGE_ROLES.getTranslation());
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());


        GuiItem _manageRanks = ItemBuilder.from(manageRanks).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuRoles(player);
        });
        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuHaveTown(player);
        });

        gui.setItem(3,1, _getBackArrow);
        gui.setItem(3,2, _manageRanks);

        gui.open(player);

    }

    public static void OpenTownMenuRoles(Player player) {

        String name = "Town";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        TownDataClass town = TownDataStorage.getTown(PlayerStatStorage.getStat(player.getUniqueId().toString()).getTownId());
        HashMap<String,TownRank> ranks = town.getTownRanks();

        int i = 0;
        for (TownRank townRank: ranks.values()) {

            Material townMaterial = Material.getMaterial(townRank.getRankIconName());
            ItemStack townRankItemStack = HeadUtils.getCustomLoreItem(townMaterial, townRank.getName());
            GuiItem _townRankItemStack = ItemBuilder.from(townRankItemStack).asGuiItem(event -> {
                event.setCancelled(true);
                OpenTownMenuRoleManager(player,townRank.getName());
            });
            gui.setItem(i, _townRankItemStack);
            i = i+1;
        }

        ItemStack createNewRole = HeadUtils.getCustomLoreItem(Material.EGG, Lang.GUI_TOWN_MEMBERS_ADD_NEW_ROLES.getTranslation());
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());

        GuiItem _createNewRole = ItemBuilder.from(createNewRole).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(""+Lang.WRITE_IN_CHAT_NEW_ROLE_NAME.getTranslation());
            player.closeInventory();
            PlayerChatListenerStorage.addPlayer("rank creation",player);

        });
        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMemberList(player);
        });


        gui.setItem(3,1, _getBackArrow);
        gui.setItem(3,3, _createNewRole);

        gui.open(player);

    }
    public static void OpenTownMenuRoleManager(Player player, String roleName) {

        String name = "Town";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        TownDataClass town = TownDataStorage.getTown(PlayerStatStorage.getStat(player.getUniqueId().toString()).getTownId());
        TownRank townRank = town.getRank(roleName);

        Material roleMaterial = Material.getMaterial(townRank.getRankIconName());
        int rankLevel = townRank.getLevel();

        ItemStack roleIcon = HeadUtils.getCustomLoreItem(roleMaterial, townRank.getName());
        ItemStack roleRankIcon = HeadUtils.getRankLevelColor(rankLevel);
        ItemStack membersRank = HeadUtils.makeSkull(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I0M2IyMzE4OWRjZjEzMjZkYTQyNTNkMWQ3NTgyZWY1YWQyOWY2YzI3YjE3MWZlYjE3ZTMxZDA4NGUzYTdkIn19fQ==");

        ArrayList<String> playerNames = townRank.getPlayers().stream()
                .map(PlayerStatStorage::getStat) // transforme chaque ID en un objet Player
                .map(PlayerDataClass::getPlayerName) // appelle getName() sur chaque PlayerDataClass
                .collect(Collectors.toCollection(ArrayList::new)); // recueille le résultat dans une nouvelle ArrayList

        HeadUtils.addLore(membersRank, playerNames);

        ItemStack renameRole = HeadUtils.getCustomLoreItem(Material.NAME_TAG,Lang.GUI_TOWN_MEMBERS_ROLE_CHANGE_NAME.getTranslation());
        String title;
        if(townRank.isPayingTaxes()){
            title = Lang.GUI_TOWN_MEMBERS_ROLE_PAY_TAXES.getTranslation();
        }
        else{
            title = Lang.GUI_TOWN_MEMBERS_ROLE_NOT_PAY_TAXES.getTranslation();
        }
        ItemStack changeRoleTaxRelation = HeadUtils.getCustomLoreItem(
                Material.GOLD_NUGGET,
                title,
                Lang.GUI_TOWN_MEMBERS_ROLE_TAXES_DESC1.getTranslation()
        );



        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());


        GuiItem _roleIcon = ItemBuilder.from(roleIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });

        GuiItem _roleRankIcon = ItemBuilder.from(roleRankIcon).asGuiItem(event -> {
            townRank.incrementLevel();
            OpenTownMenuRoleManager(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _membersRank = ItemBuilder.from(membersRank).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _renameRole = ItemBuilder.from(renameRole).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _changeRoleTaxRelation = ItemBuilder.from(changeRoleTaxRelation).asGuiItem(event -> {
            townRank.swapPayingTaxes();
            OpenTownMenuRoleManager(player,roleName);
            event.setCancelled(true);
        });

        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMemberList(player);
        });

        gui.setItem(1,5, _roleIcon);

        gui.setItem(2,2, _roleRankIcon);
        gui.setItem(2,4, _membersRank);
        gui.setItem(2,6, _renameRole);
        gui.setItem(2,8, _changeRoleTaxRelation);

        gui.setItem(3,1, _getBackArrow);

        gui.open(player);

    }

    //Done
    public static void OpenTownEconomics(Player player) {

        String name = "Town";
        int nRow = 4;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        TownDataClass town = TownDataStorage.getTown(player);


        ItemStack goldIcon = HeadUtils.makeSkull(Lang.GUI_TREASURY_STORAGE.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        ItemStack goldSpendingIcon = HeadUtils.makeSkull(Lang.GUI_TREASURY_SPENDING.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        ItemStack lowerTax = HeadUtils.makeSkull(Lang.GUI_TREASURY_LOWER_TAX.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=");
        ItemStack increaseTax = HeadUtils.makeSkull(Lang.GUI_TREASURY_INCREASE_TAX.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
        ItemStack taxInfo = HeadUtils.makeSkull(Lang.GUI_TREASURY_FLAT_TAX.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTk4ZGY0MmY0NzdmMjEzZmY1ZTlkN2ZhNWE0Y2M0YTY5ZjIwZDljZWYyYjkwYzRhZTRmMjliZDE3Mjg3YjUifX19");
        ItemStack taxHistory = HeadUtils.makeSkull(Lang.GUI_TREASURY_TAX_HISTORY.getTranslation(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmU1OWYyZDNiOWU3ZmI5NTBlOGVkNzkyYmU0OTIwZmI3YTdhOWI5MzQ1NjllNDQ1YjJiMzUwM2ZlM2FiOTAyIn19fQ==");
        ItemStack salarySpending = HeadUtils.makeSkull(Lang.GUI_TREASURY_SALARY_HISTORY.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjlhNjAwYWIwYTgzMDk3MDY1Yjk1YWUyODRmODA1OTk2MTc3NDYwOWFkYjNkYmQzYTRjYTI2OWQ0NDQwOTU1MSJ9fX0=");
        ItemStack chunkSpending = HeadUtils.makeSkull(Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        ItemStack workbenchSpending = HeadUtils.makeSkull(Lang.GUI_TREASURY_MISCELLANEOUS_SPENDING.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGMzNjA0NTIwOGY5YjVkZGNmOGM0NDMzZTQyNGIxY2ExN2I5NGY2Yjk2MjAyZmIxZTUyNzBlZThkNTM4ODFiMSJ9fX0=");
        ItemStack donation = HeadUtils.getCustomLoreItem(Material.DIAMOND,Lang.GUI_TREASURY_DONATION.getTranslation(),Lang.GUI_TREASURY_DONATION_DESC1.getTranslation());
        ItemStack donationHistory = HeadUtils.getCustomLoreItem(Material.PAPER,Lang.GUI_TREASURY_DONATION_HISTORY.getTranslation());
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());

        goldIcon = HeadUtils.addLore(goldIcon, Lang.GUI_TREASURY_STORAGE_DESC1.getTranslation(town.getBalance()),Lang.GUI_TREASURY_STORAGE_DESC2.getTranslation(0));
        goldSpendingIcon = HeadUtils.addLore(goldSpendingIcon, Lang.GUI_TREASURY_SPENDING_DESC1.getTranslation(0), Lang.GUI_TREASURY_SPENDING_DESC2.getTranslation(0),Lang.GUI_TREASURY_SPENDING_DESC3.getTranslation(0));



        lowerTax = HeadUtils.addLore(lowerTax, Lang.GUI_TREASURY_LOWER_TAX_DESC1.getTranslation());
        taxInfo = HeadUtils.addLore(taxInfo, Lang.GUI_TREASURY_FLAT_TAX_DESC1.getTranslation(town.getTreasury().getFlatTax()));
        increaseTax = HeadUtils.addLore(increaseTax, Lang.GUI_TREASURY_INCREASE_TAX_DESC1.getTranslation());

        salarySpending = HeadUtils.addLore(salarySpending, Lang.GUI_TREASURY_SALARY_HISTORY_DESC1.getTranslation("0"));
        chunkSpending = HeadUtils.addLore(chunkSpending, Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC1.getTranslation(0), Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC2.getTranslation(0),Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC3.getTranslation(town.getChunkSettings().getNumberOfClaimedChunk()));
        workbenchSpending = HeadUtils.addLore(workbenchSpending, Lang.GUI_TREASURY_MISCELLANEOUS_SPENDING_DESC1.getTranslation());

        donation = HeadUtils.addLore(donation, Lang.GUI_TREASURY_DONATION_DESC1.getTranslation());
        donationHistory = HeadUtils.addLore(donationHistory, town.getTreasury().getDonationLimitedHistory(5));

        GuiItem _goldInfo = ItemBuilder.from(goldIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _goldSpendingIcon = ItemBuilder.from(goldSpendingIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _taxHistory = ItemBuilder.from(taxHistory).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _salarySpending = ItemBuilder.from(salarySpending).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _chunkSpending = ItemBuilder.from(chunkSpending).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _workbenchSpending = ItemBuilder.from(workbenchSpending).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _donation = ItemBuilder.from(donation).asGuiItem(event -> {
            player.sendMessage(ChatUtils.getTANString() + Lang.WRITE_IN_CHAT_AMOUNT_OF_MONEY_FOR_TOWN_DONATION.getTranslation());
            PlayerChatListenerStorage.addPlayer("donation",player);
            player.closeInventory();
            event.setCancelled(true);

        });
        GuiItem _donationHistory = ItemBuilder.from(donationHistory).asGuiItem(event -> {
            event.setCancelled(true);
        });

        GuiItem _lessTax = ItemBuilder.from(lowerTax).asGuiItem(event -> {
            town.getTreasury().remove1FlatTax();
            OpenTownEconomics(player);
            event.setCancelled(true);

        });
        GuiItem _taxInfo = ItemBuilder.from(taxInfo).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownEconomics(player);
        });
        GuiItem _moreTax = ItemBuilder.from(increaseTax).asGuiItem(event -> {
            town.getTreasury().add1FlatTax();
            event.setCancelled(true);
            OpenTownEconomics(player);
        });

        GuiItem _decorativeGlass = ItemBuilder.from(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE)).asGuiItem(event -> {
            event.setCancelled(true);
        });

        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuHaveTown(player);
        });


        gui.setItem(1,1, _decorativeGlass);
        gui.setItem(1,2, _decorativeGlass);
        gui.setItem(1,3, _decorativeGlass);
        gui.setItem(1,5, _decorativeGlass);
        gui.setItem(1,7, _decorativeGlass);
        gui.setItem(1,8, _decorativeGlass);
        gui.setItem(1,9, _decorativeGlass);

        gui.setItem(1,4, _goldInfo);
        gui.setItem(1,6, _goldSpendingIcon);

        gui.setItem(2,1, _lessTax);
        gui.setItem(2,2, _taxInfo);
        gui.setItem(2,3, _moreTax);
        gui.setItem(2,4, _taxHistory);

        gui.setItem(2,6, _salarySpending);
        gui.setItem(2,7, _chunkSpending);
        gui.setItem(2,8, _workbenchSpending);

        gui.setItem(3,2, _donation);
        gui.setItem(3,3, _donationHistory);



        gui.setItem(4,1, _getBackArrow);

        gui.open(player);

    }

    public static void OpenTownLevel(Player player){
        String name = "Town";
        int nRow = 3;
        PlayerDataClass playerStat = PlayerStatStorage.getStat(player);
        TownDataClass townData = TownDataStorage.getTown(player);
        TownLevel townLevel = townData.getTownLevel();

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        ItemStack TownIcon = HeadUtils.getTownIcon(PlayerStatStorage.getStat(player.getUniqueId().toString()).getTownId());
        ItemStack upgradeTownLevel = HeadUtils.getCustomLoreItem(Material.EMERALD, Lang.GUI_TOWN_LEVEL_UP.getTranslation());
        ItemStack upgradeChunkCap = HeadUtils.makeSkull(Lang.GUI_TOWN_LEVEL_UP_CHUNK_CAP.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=");
        ItemStack upgradePlayerCap = HeadUtils.makeSkull(Lang.GUI_TOWN_LEVEL_UP_PLAYER_CAP.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I0M2IyMzE4OWRjZjEzMjZkYTQyNTNkMWQ3NTgyZWY1YWQyOWY2YzI3YjE3MWZlYjE3ZTMxZDA4NGUzYTdkIn19fQ==");

        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());

        upgradeTownLevel = HeadUtils.addLore(upgradeTownLevel,Lang.GUI_TOWN_LEVEL_UP_DESC1.getTranslation(townLevel.getTownLevel()),Lang.GUI_TOWN_LEVEL_UP_DESC2.getTranslation(townLevel.getTownLevel()+1, townLevel.getMoneyRequiredTownLevel()));
        upgradeChunkCap = HeadUtils.addLore(upgradeChunkCap,Lang.GUI_TOWN_LEVEL_UP_CHUNK_CAP_DESC1.getTranslation(townLevel.getChunkCapLevel()) ,Lang.GUI_TOWN_LEVEL_UP_CHUNK_CAP_DESC2.getTranslation(townLevel.getChunkCapLevel()+1,townLevel.getMoneyRequiredChunkCap()));
        upgradePlayerCap = HeadUtils.addLore(upgradePlayerCap,Lang.GUI_TOWN_LEVEL_UP_PLAYER_CAP_DESC1.getTranslation(townLevel.getPlayerCapLevel()) ,Lang.GUI_TOWN_LEVEL_UP_PLAYER_CAP_DESC2.getTranslation(townLevel.getPlayerCapLevel()+1,townLevel.getMoneyRequiredPlayerCap()));


        GuiItem _TownIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _upgradeTownLevel = ItemBuilder.from(upgradeTownLevel).asGuiItem(event -> {
            event.setCancelled(true);

            if(townData.getTreasury().getBalance() > townLevel.getMoneyRequiredTownLevel()){
                townData.getTreasury().removeToBalance(townLevel.getMoneyRequiredTownLevel());
                townLevel.TownLevelUp();
                player.sendMessage(Lang.BASIC_LEVEL_UP.getTranslation());
                OpenTownLevel(player);
            }
            else{
                player.sendMessage(Lang.TOWN_NOT_ENOUGH_MONEY.getTranslation());
            }

        });
        GuiItem _upgradeChunkCap = ItemBuilder.from(upgradeChunkCap).asGuiItem(event -> {
            event.setCancelled(true);

            if(townData.getTreasury().getBalance() > townLevel.getMoneyRequiredChunkCap()){
                townData.getTreasury().removeToBalance(townLevel.getMoneyRequiredChunkCap());
                townLevel.chunkCapLevelUp();
                player.sendMessage(Lang.GUI_TOWN_LEVEL_UP.getTranslation());
                OpenTownLevel(player);
            }
            else{
                player.sendMessage(Lang.TOWN_NOT_ENOUGH_MONEY.getTranslation());
            }
        });
        GuiItem _upgradePlayerCap = ItemBuilder.from(upgradePlayerCap).asGuiItem(event -> {
            event.setCancelled(true);

            if (townData.getTreasury().getBalance() > townLevel.getMoneyRequiredPlayerCap()) {
                townData.getTreasury().removeToBalance(townLevel.getMoneyRequiredPlayerCap());
                townLevel.PlayerCapLevelUp();
                player.sendMessage(Lang.GUI_TOWN_LEVEL_UP.getTranslation());
                OpenTownLevel(player);
            } else {
                player.sendMessage(Lang.TOWN_NOT_ENOUGH_MONEY.getTranslation());
            }
        });

        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuHaveTown(player);
        });



        gui.setItem(1,5, _TownIcon);
        gui.setItem(2,3, _upgradeTownLevel);
        gui.setItem(2,5, _upgradeChunkCap);
        gui.setItem(2,7, _upgradePlayerCap);
        gui.setItem(3,1, _getBackArrow);

        gui.open(player);

    }

    //Done
    public static void OpenTownSettings(Player player) {

        String name = "Town";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        PlayerDataClass playerStat = PlayerStatStorage.getStat(player);
        TownDataClass playerTown = TownDataStorage.getTown(player);

        ItemStack TownIcon = HeadUtils.getTownIcon(playerStat.getTownId());
        ItemStack leaveTown = HeadUtils.getCustomLoreItem(Material.BARRIER,
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN.getTranslation(),
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC1.getTranslation(TownDataStorage.getTown(playerStat).getTownName()),
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC2.getTranslation());

        ItemStack deleteTown = HeadUtils.getCustomLoreItem(Material.BARRIER,
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN.getTranslation(),
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC1.getTranslation(TownDataStorage.getTown(playerStat).getTownName()),
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC2.getTranslation());

        ItemStack changeOwnershipTown = HeadUtils.getCustomLoreItem(Material.BEEHIVE,
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP.getTranslation(),
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC1.getTranslation(TownDataStorage.getTown(playerStat).getTownName()),
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC2.getTranslation());

        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW,
                Lang.GUI_BACK_ARROW.getTranslation());

        GuiItem _townIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });

        GuiItem _leaveTown = ItemBuilder.from(leaveTown).asGuiItem(event -> {
            event.setCancelled(true);
            if (playerTown.getUuidLeader().equals(playerStat.getUuid())) {
                player.sendMessage(Lang.CHAT_CANT_LEAVE_TOWN_IF_LEADER.getTranslation());
            } else {
                playerTown.removePlayer(player.getUniqueId().toString());
                playerStat.setTownId(null);
                player.sendMessage(Lang.CHAT_PLAYER_LEFT_THE_TOWN.getTranslation());
                player.closeInventory();
            }
        });
        GuiItem _deleteTown = ItemBuilder.from(deleteTown).asGuiItem(event -> {
            event.setCancelled(true);
            if (!playerTown.getUuidLeader().equals(playerStat.getUuid())) {
                player.sendMessage(Lang.CHAT_CANT_DISBAND_TOWN_IF_NOT_LEADER.getTranslation());
            } else {
                TownDataStorage.removeTown(playerStat.getTownId());
                playerStat.setTownId(null);
                player.closeInventory();
                player.sendMessage(Lang.CHAT_PLAYER_TOWN_SUCCESSFULLY_DELETED.getTranslation());
            }
        });

        GuiItem _changeOwnershipTown = ItemBuilder.from(changeOwnershipTown).asGuiItem(event -> {
            event.setCancelled(true);
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

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        ItemStack warCategory = HeadUtils.getCustomLoreItem(Material.IRON_SWORD,
                Lang.GUI_TOWN_RELATION_WAR.getTranslation(),
                Lang.GUI_TOWN_RELATION_WAR_DESC1.getTranslation());
        ItemStack EmbargoCategory = HeadUtils.getCustomLoreItem(Material.BARRIER,
                Lang.GUI_TOWN_RELATION_EMBARGO.getTranslation(),
                Lang.GUI_TOWN_RELATION_EMBARGO_DESC1.getTranslation());
        ItemStack NAPCategory = HeadUtils.getCustomLoreItem(Material.WRITABLE_BOOK,
                Lang.GUI_TOWN_RELATION_NAP.getTranslation(),
                Lang.GUI_TOWN_RELATION_NAP_DESC1.getTranslation());
        ItemStack AllianceCategory = HeadUtils.getCustomLoreItem(Material.CAMPFIRE,
                Lang.GUI_TOWN_RELATION_ALLIANCE.getTranslation(),
                Lang.GUI_TOWN_RELATION_ALLIANCE_DESC1.getTranslation());

        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW,
                Lang.GUI_BACK_ARROW.getTranslation());

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
        GuiItem _decorativeGlass = ItemBuilder.from(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).asGuiItem(event -> {
            event.setCancelled(true);
        });
        gui.setItem(0, _decorativeGlass);
        gui.setItem(1, _decorativeGlass);
        gui.setItem(2, _decorativeGlass);
        gui.setItem(3, _decorativeGlass);
        gui.setItem(4, _decorativeGlass);
        gui.setItem(5, _decorativeGlass);
        gui.setItem(6, _decorativeGlass);
        gui.setItem(7, _decorativeGlass);
        gui.setItem(8, _decorativeGlass);


        gui.setItem(10, _warCategory);
        gui.setItem(12, _EmbargoCategory);
        gui.setItem(14, _NAPCategory);
        gui.setItem(16, _AllianceCategory);

        gui.setItem(18, _getBackArrow);

        gui.setItem(19, _decorativeGlass);
        gui.setItem(20, _decorativeGlass);
        gui.setItem(21, _decorativeGlass);
        gui.setItem(22, _decorativeGlass);
        gui.setItem(23, _decorativeGlass);
        gui.setItem(24, _decorativeGlass);
        gui.setItem(25, _decorativeGlass);
        gui.setItem(26, _decorativeGlass);

        gui.open(player);
    }

    public static void OpenTownRelation(Player player, String relation) {

        String name = "Town - Relation";
        int nRow = 4;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        PlayerDataClass playerStat = PlayerStatStorage.getStat(player);
        TownDataClass playerTown = TownDataStorage.getTown(playerStat);

        ArrayList<String> TownListUUID = playerTown.getRelations().getOne(relation);
        int i = 0;
        for(String townUUID : TownListUUID){
            ItemStack townIcon = HeadUtils.getTownIconWithInformations(townUUID);

            GuiItem _town = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);
            });
            gui.setItem(i, _town);

            i = i+1;
        }


        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());

        ItemStack addTownButton = HeadUtils.makeSkull(
                Lang.GUI_TOWN_RELATION_ADD_TOWN.getTranslation(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19"
        );
        ItemStack removeTownButton = HeadUtils.makeSkull(
                Lang.GUI_TOWN_RELATION_REMOVE_TOWN.getTranslation(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0="
        );

        ItemStack nextPageButton = HeadUtils.makeSkull(
                Lang.GUI_NEXT_PAGE.getTranslation(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2MjYyYWYxZDVmNDE0YzU5NzA1NWMyMmUzOWNjZTE0OGU1ZWRiZWM0NTU1OWEyZDZiODhjOGQ2N2I5MmVhNiJ9fX0="
        );

        ItemStack previousPageButton = HeadUtils.makeSkull(
                Lang.GUI_PREVIOUS_PAGE.getTranslation(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQyZmRlOGI4MmU4YzFiOGMyMmIyMjY3OTk4M2ZlMzVjYjc2YTc5Nzc4NDI5YmRhZGFiYzM5N2ZkMTUwNjEifX19"
        );

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
        GuiItem _decorativeGlass = ItemBuilder.from(new ItemStack(Material.WHITE_STAINED_GLASS_PANE)).asGuiItem(event -> {
            event.setCancelled(true);
        });

        gui.setItem(4,1, _back);
        gui.setItem(4,4,_add);
        gui.setItem(4,5,_remove);

        gui.setItem(4,7,_next);
        gui.setItem(4,8,_previous);


        gui.setItem(4,2, _decorativeGlass);
        gui.setItem(4,3, _decorativeGlass);
        gui.setItem(4,6, _decorativeGlass);
        gui.setItem(4,9, _decorativeGlass);

        gui.open(player);
    }

    public static void OpenTownRelationModification(Player player, String action, String relation) {

        String name = "Town - Relation";
        int nRow = 4;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        PlayerDataClass playerStat = PlayerStatStorage.getStat(player.getUniqueId().toString());
        TownDataClass playerTown = TownDataStorage.getTown(playerStat);

        LinkedHashMap<String, TownDataClass> allTown = getTownList();
        ArrayList<String> TownListUUID = playerTown.getRelations().getOne(relation);
        GuiItem _decorativeGlass = ItemBuilder.from(new ItemStack(Material.WHITE_STAINED_GLASS)).asGuiItem(event -> {
            event.setCancelled(true);
        });
        if(action.equals("add")){
            List<String> townNoRelation = new ArrayList<>(allTown.keySet());
            townNoRelation.removeAll(TownListUUID);
            townNoRelation.remove(playerTown.getTownId());
            int i = 0;
            for(String townUUID : townNoRelation){
                ItemStack townIcon = HeadUtils.getTownIconWithInformations(townUUID);

                GuiItem _town = ItemBuilder.from(townIcon).asGuiItem(event -> {
                    event.setCancelled(true);

                    player.sendMessage(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.getTranslation(townIcon.getItemMeta().getDisplayName(),relation));
                    TownDataStorage.getTown(playerTown.getTownId()).addTownRelations(relation,townUUID);
                    OpenTownRelation(player,relation);
                });
                gui.setItem(i, _town);
                i = i+1;
                _decorativeGlass = ItemBuilder.from(new ItemStack(Material.GREEN_STAINED_GLASS_PANE)).asGuiItem(event -> {
                    event.setCancelled(true);
                });
            }


        }
        else if(action.equals("remove")){
            int i = 0;
            for(String townUUID : TownListUUID){
                ItemStack townIcon = HeadUtils.getTownIconWithInformations(townUUID);
                GuiItem _town = ItemBuilder.from(townIcon).asGuiItem(event -> {
                    event.setCancelled(true);
                    player.sendMessage(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.getTranslation(townIcon.getItemMeta().getDisplayName(),relation));
                    TownDataStorage.getTown(playerTown.getTownId()).removeTownRelations(relation,townUUID);
                    OpenTownRelation(player,relation);
                });
                gui.setItem(i, _town);
                i = i+1;
            }
            TownDataStorage.getTown(playerTown.getTownId()).removeTownRelations(relation,player.getUniqueId().toString());
            _decorativeGlass = ItemBuilder.from(new ItemStack(Material.RED_STAINED_GLASS_PANE)).asGuiItem(event -> {
                event.setCancelled(true);
            });
        }

        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());

        ItemStack nextPageButton = HeadUtils.makeSkull(
                Lang.GUI_NEXT_PAGE.getTranslation(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2MjYyYWYxZDVmNDE0YzU5NzA1NWMyMmUzOWNjZTE0OGU1ZWRiZWM0NTU1OWEyZDZiODhjOGQ2N2I5MmVhNiJ9fX0="
        );
        ItemStack previousPageButton = HeadUtils.makeSkull(
                Lang.GUI_PREVIOUS_PAGE.getTranslation(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQyZmRlOGI4MmU4YzFiOGMyMmIyMjY3OTk4M2ZlMzVjYjc2YTc5Nzc4NDI5YmRhZGFiYzM5N2ZkMTUwNjEifX19"
        );

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

        gui.setItem(4,2, _decorativeGlass);
        gui.setItem(4,3, _decorativeGlass);
        gui.setItem(4,4, _decorativeGlass);
        gui.setItem(4,5, _decorativeGlass);
        gui.setItem(4,6, _decorativeGlass);
        gui.setItem(4,9, _decorativeGlass);


        gui.open(player);
    }

    public static void OpenTownChunkMenu(Player player){
        String name = "Town";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        PlayerDataClass playerStat = PlayerStatStorage.getStat(player.getUniqueId().toString());
        TownDataClass townClass = TownDataStorage.getTown(player);

        ItemStack doorAccess = HeadUtils.getCustomLoreItem(Material.OAK_DOOR,
                Lang.GUI_TOWN_CLAIM_SETTINGS_DOOR.getTranslation(),
                Lang.GUI_TOWN_CLAIM_SETTINGS_DOOR_DESC1.getTranslation(townClass.getChunkSettings().getDoorAuth()));
        ItemStack chestAccess = HeadUtils.getCustomLoreItem(Material.CHEST,
                Lang.GUI_TOWN_CLAIM_SETTINGS_CHEST.getTranslation(),
                Lang.GUI_TOWN_CLAIM_SETTINGS_CHEST_DESC1.getTranslation(townClass.getChunkSettings().getChestAuth()));
        ItemStack placeBlockAccess = HeadUtils.getCustomLoreItem(Material.BRICKS,
                Lang.GUI_TOWN_CLAIM_SETTINGS_BUILD.getTranslation(),
                Lang.GUI_TOWN_CLAIM_SETTINGS_BUILD_DESC1.getTranslation(townClass.getChunkSettings().getPlaceAuth()));
        ItemStack breakBlockAccess = HeadUtils.getCustomLoreItem(Material.IRON_PICKAXE,
                Lang.GUI_TOWN_CLAIM_SETTINGS_BREAK.getTranslation(),
                Lang.GUI_TOWN_CLAIM_SETTINGS_BREAK_DESC1.getTranslation(townClass.getChunkSettings().getBreakAuth()));
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());



        GuiItem _doorAccessManager = ItemBuilder.from(doorAccess).asGuiItem(event -> {
            townClass.getChunkSettings().nextDoorAuth();
            OpenTownChunkMenu(player);
        });
        GuiItem _chestAccessManager = ItemBuilder.from(chestAccess).asGuiItem(event -> {
            townClass.getChunkSettings().nextChestAuth();
            OpenTownChunkMenu(player);
        });
        GuiItem _placeBlockAccessManager = ItemBuilder.from(placeBlockAccess).asGuiItem(event -> {
            townClass.getChunkSettings().nextPlaceAuth();
            OpenTownChunkMenu(player);
        });
        GuiItem _breakBlockAccessManager = ItemBuilder.from(breakBlockAccess).asGuiItem(event -> {
            townClass.getChunkSettings().nextBreakAuth();
            OpenTownChunkMenu(player);
        });

        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuHaveTown(player);
        });

        gui.setItem(10, _doorAccessManager);
        gui.setItem(12, _chestAccessManager);
        gui.setItem(14, _placeBlockAccessManager);
        gui.setItem(16, _breakBlockAccessManager);

        gui.setItem(18, _getBackArrow);

        gui.open(player);
    }

}
