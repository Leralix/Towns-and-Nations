package org.tan.TownsAndNations.GUI;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tan.TownsAndNations.DataClass.*;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.Action;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.enums.TownRolePermission;
import org.tan.TownsAndNations.storage.*;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.ConfigUtil;
import org.tan.TownsAndNations.utils.HeadUtils;

import static org.tan.TownsAndNations.storage.PlayerChatListenerStorage.ChatCategory.RANK_CREATION;
import static org.tan.TownsAndNations.storage.TownDataStorage.getTownList;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;
import static org.tan.TownsAndNations.utils.RelationUtil.*;

import java.util.ArrayList;


import java.util.*;

public class GuiManager2 {

    //done
    public static void OpenMainMenu(Player player){

        if(PlayerDataStorage.get(player) == null){
            PlayerDataStorage.createPlayerDataClass(player);
        }

        PlayerData playerStat = PlayerDataStorage.get(player);
        boolean playerHaveTown = playerStat.getTownId() != null;
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
        ItemStack PlayerHead = HeadUtils.getPlayerHeadInformation(player);
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, "Quit");

        HeadUtils.addLore(KingdomHead, Lang.GUI_KINGDOM_ICON_DESC1.getTranslation());
        HeadUtils.addLore(RegionHead, Lang.GUI_REGION_ICON_DESC1.getTranslation());
        HeadUtils.addLore(TownHead, playerHaveTown? Lang.GUI_KINGDOM_ICON_DESC1_HAVE_TOWN.getTranslation(TownDataStorage.get(playerStat).getName()):Lang.GUI_KINGDOM_ICON_DESC1_NO_TOWN.getTranslation() );


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
            if(PlayerDataStorage.get(player).haveTown()){
                OpenTownMenuHaveTown(player);
            }
            else{
                openTownMenuNoTown(player);
            }
        });
        GuiItem Player = ItemBuilder.from(PlayerHead).asGuiItem(event -> {
            event.setCancelled(true);
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
    public static void openProfileMenu(Player player){
        String name = "Profile";
        PlayerData playerStat = PlayerDataStorage.get(player);

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
    public static void openTownMenuNoTown(Player player){


        PlayerData playerStat = PlayerDataStorage.get(player);

        String name = "Town";
        int nRow = 3;
        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        FileConfiguration config =  ConfigUtil.getCustomConfig("config.yml");
        int townPrice = config.getInt("CostOfCreatingTown");

        ItemStack createNewLand = HeadUtils.getCustomLoreItem(Material.GRASS_BLOCK,
                Lang.GUI_NO_TOWN_CREATE_NEW_TOWN.getTranslation(),
                Lang.GUI_NO_TOWN_CREATE_NEW_TOWN_DESC1.getTranslation(townPrice)
        );
        ItemStack joinLand = HeadUtils.getCustomLoreItem(Material.ANVIL, Lang.GUI_NO_TOWN_JOIN_A_TOWN.getTranslation(),Lang.GUI_NO_TOWN_JOIN_A_TOWN_DESC1.getTranslation(TownDataStorage.getNumberOfTown()));
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());

        GuiItem _create = ItemBuilder.from(createNewLand).asGuiItem(event -> {
            event.setCancelled(true);
            assert playerStat != null;
                if (playerStat.getBalance() < townPrice) {
                    player.sendMessage(Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.getTranslation(townPrice - playerStat.getBalance()));
                } else {
                    player.sendMessage(Lang.PLAYER_WRITE_TOWN_NAME_IN_CHAT.getTranslation());
                    player.closeInventory();

                    Map<String,String> data = new HashMap<>();
                    data.put("town cost", String.valueOf(townPrice));

                    PlayerChatListenerStorage.addPlayer(PlayerChatListenerStorage.ChatCategory.CREATE_CITY,player,data);
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
    public static void OpenSearchTownMenu(Player player) {

        String name = "Town";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        HashMap<String, TownData> townDataStorage = getTownList();

        int i = 0;
        for (Map.Entry<String, TownData> entry : townDataStorage.entrySet()) {


            TownData townData = entry.getValue();
            TownData playerTown = TownDataStorage.get(townData.getID());

            ItemStack townIcon = HeadUtils.getTownIcon(playerTown.getID());

            HeadUtils.addLore(townIcon,
                    Lang.GUI_TOWN_INFO_DESC1.getTranslation(Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerTown.getUuidLeader())).getName()),
                    Lang.GUI_TOWN_INFO_DESC2.getTranslation(playerTown.getChunkSettings().getNumberOfClaimedChunk()),
                    Lang.GUI_TOWN_INFO_DESC3.getTranslation(playerTown.getPlayerList().size())
            );

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
    public static void OpenTownMenuHaveTown(Player player) {

        String name = "Town";
        int nRow = 3;
        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(playerStat);
        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        ItemStack TownIcon = HeadUtils.getTownIcon(playerTown.getID());
        HeadUtils.addLore(TownIcon,
                Lang.GUI_TOWN_INFO_DESC1.getTranslation(Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerTown.getUuidLeader())).getName()),
                Lang.GUI_TOWN_INFO_DESC2.getTranslation(playerTown.getChunkSettings().getNumberOfClaimedChunk()),
                Lang.GUI_TOWN_INFO_DESC3.getTranslation(playerTown.getPlayerList().size()),
                Lang.GUI_TOWN_INFO_DESC4.getTranslation(playerTown.getTreasury().getBalance())
        );





        ItemStack GoldIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_TREASURY_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        HeadUtils.addLore(GoldIcon, Lang.GUI_TOWN_TREASURY_ICON_DESC1.getTranslation());

        ItemStack SkullIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_MEMBERS_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Q0ZDQ5NmIxZGEwNzUzNmM5NGMxMzEyNGE1ODMzZWJlMGM1MzgyYzhhMzM2YWFkODQ2YzY4MWEyOGQ5MzU2MyJ9fX0=");
        HeadUtils.addLore(SkullIcon, Lang.GUI_TOWN_MEMBERS_ICON_DESC1.getTranslation());

        ItemStack ClaimIcon = HeadUtils.makeSkull(Lang.GUI_CLAIM_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=");
        HeadUtils.addLore(ClaimIcon, Lang.GUI_CLAIM_ICON_DESC1.getTranslation());

        ItemStack otherTownIcon = HeadUtils.makeSkull(Lang.GUI_OTHER_TOWN_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMzc0ZTIxYjgxYzBiMjFhYmViOGU5N2UxM2UwNzdkM2VkMWVkNDRmMmU5NTZjNjhmNjNhM2UxOWU4OTlmNiJ9fX0=");
        HeadUtils.addLore(otherTownIcon, Lang.GUI_OTHER_TOWN_ICON_DESC1.getTranslation());

        ItemStack RelationIcon = HeadUtils.makeSkull(Lang.GUI_RELATION_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUwN2Q2ZGU2MzE4MzhlN2E3NTcyMGU1YjM4ZWYxNGQyOTY2ZmRkODQ4NmU3NWQxZjY4MTJlZDk5YmJjYTQ5OSJ9fX0=");
        HeadUtils.addLore(RelationIcon, Lang.GUI_RELATION_ICON_DESC1.getTranslation());

        ItemStack LevelIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_LEVEL_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmJlNTI5YWI2YjJlYTdjNTBkOTE5MmQ4OWY4OThmZDdkYThhOWU3NTBkMzc4Mjk1ZGY3MzIwNWU3YTdlZWFlMCJ9fX0=");
        HeadUtils.addLore(LevelIcon, Lang.GUI_TOWN_LEVEL_ICON_DESC1.getTranslation());

        ItemStack SettingIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_SETTINGS_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVkMmNiMzg0NThkYTE3ZmI2Y2RhY2Y3ODcxNjE2MDJhMjQ5M2NiZjkzMjMzNjM2MjUzY2ZmMDdjZDg4YTljMCJ9fX0=");
        HeadUtils.addLore(SettingIcon, Lang.GUI_TOWN_SETTINGS_ICON_DESC1.getTranslation());

        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());

        GuiItem _townIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> {
            event.setCancelled(true);

            if(!playerStat.isTownLeader())
                return;
            if(event.getCursor() == null)
                return;

            Material itemMaterial = event.getCursor().getData().getItemType();
            if(itemMaterial == Material.AIR){
                player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_NO_ITEM_SHOWED.getTranslation());
            }

            else {
                playerTown.setTownIconMaterialCode(itemMaterial);
                OpenTownMenuHaveTown(player);
                player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_CHANGED_ICON_SUCCESS.getTranslation());
            }
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
        GuiItem _otherTownIcon = ItemBuilder.from(otherTownIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuOtherTown(player);
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
        gui.setItem(13, _otherTownIcon);
        gui.setItem(14, _relationIcon);
        gui.setItem(15, _levelIcon);
        gui.setItem(16, _settingsIcon);
        gui.setItem(18, _backIcon);

        gui.open(player);
    }
    public static void OpenTownMenuOtherTown(Player player) {

        String name = "Town";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(playerStat);

        HashMap<String, TownData> townDataStorage = getTownList();

        int i = 0;
        for (Map.Entry<String, TownData> entry : townDataStorage.entrySet()) {


            TownData otherTown = entry.getValue();

            ItemStack townIcon = HeadUtils.getTownIcon(otherTown.getID());

            TownRelation relation = otherTown.getRelationWith(playerTown);

            String relationName;
            if(relation == null){
                relationName = Lang.GUI_TOWN_RELATION_NEUTRAL.getTranslation();
            }
            else {
                relationName = relation.getColor() + relation.getName();
            }

            HeadUtils.addLore(townIcon,
                    Lang.GUI_TOWN_INFO_DESC1.getTranslation(Bukkit.getServer().getOfflinePlayer(UUID.fromString(otherTown.getUuidLeader())).getName()),
                    Lang.GUI_TOWN_INFO_DESC2.getTranslation(otherTown.getChunkSettings().getNumberOfClaimedChunk()),
                    Lang.GUI_TOWN_INFO_DESC3.getTranslation(otherTown.getPlayerList().size()),
                    Lang.GUI_TOWN_INFO_TOWN_RELATION.getTranslation(relationName)
            );

            GuiItem _townIteration = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);
            });

            gui.setItem(i, _townIteration);
            i++;

        }
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());
        GuiItem _back = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuHaveTown(player);
        });
        gui.setItem(3,1, _back);

        gui.open(player);
    }
    public static void OpenTownMemberList(Player player) {

        String name = "Town";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData town = TownDataStorage.get(playerStat);

        HashSet<String> players = town.getPlayerList();

        int i = 0;
        for (String playerUUID: players) {

            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            PlayerData otherPlayerStat = PlayerDataStorage.get(playerUUID);

            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate.getName(),playerIterate);
            HeadUtils.addLore(
                    playerHead,
                    Lang.GUI_TOWN_MEMBER_DESC1.getTranslation(otherPlayerStat.getTownRankID()),
                    Lang.GUI_TOWN_MEMBER_DESC2.getTranslation(otherPlayerStat.getBalance()),
                    Lang.GUI_TOWN_MEMBER_DESC3.getTranslation()
            );
            GuiItem _playerIcon = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                if(event.getClick() == ClickType.RIGHT){

                    if(otherPlayerStat.isTownLeader()){
                        player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_LEADER.getTranslation());
                        return;
                    }
                    if(otherPlayerStat.getUuid().equals(playerStat.getUuid())){
                        player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_YOURSELF.getTranslation());
                        return;
                    }

                    town.getRank(otherPlayerStat.getTownRankID()).removePlayer(playerUUID);
                    town.removePlayer(playerUUID);
                    otherPlayerStat.leaveTown();
                    town.broadCastMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS.getTranslation(playerIterate.getName()));
                    if(playerIterate.isOnline())
                        playerIterate.getPlayer().sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS_PLAYER.getTranslation());
                }
                OpenTownMemberList(player);
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

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData town = TownDataStorage.get(playerStat);

        Map<String,TownRank> ranks = town.getTownRanks();

        int i = 0;
        for (TownRank townRank: ranks.values()) {

            Material townMaterial = Material.getMaterial(townRank.getRankIconName());
            ItemStack townRankItemStack = HeadUtils.getCustomLoreItem(townMaterial, townRank.getName());
            GuiItem _townRankItemStack = ItemBuilder.from(townRankItemStack).asGuiItem(event -> {
                event.setCancelled(true);
                if(playerStat.hasPermission(TownRolePermission.MANAGE_RANKS))
                    OpenTownMenuRoleManager(player,townRank.getName());
                else
                    player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
            });
            gui.setItem(i, _townRankItemStack);
            i = i+1;
        }

        ItemStack createNewRole = HeadUtils.getCustomLoreItem(Material.EGG, Lang.GUI_TOWN_MEMBERS_ADD_NEW_ROLES.getTranslation());
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());
        GuiItem _createNewRole = ItemBuilder.from(createNewRole).asGuiItem(event -> {
            event.setCancelled(true);

            if(playerStat.hasPermission(TownRolePermission.CREATE_RANK)){
                if(town.getNumberOfRank() >= 8){
                    player.sendMessage(Lang.TOWN_RANK_CAP_REACHED.getTranslation());
                    return;
                }

                player.sendMessage(Lang.WRITE_IN_CHAT_NEW_ROLE_NAME.getTranslation());
                player.closeInventory();
                PlayerChatListenerStorage.addPlayer(RANK_CREATION,player);
            }
            else
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());


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


        TownData town = TownDataStorage.get(PlayerDataStorage.get(player.getUniqueId().toString()).getTownId());
        TownRank townRank = town.getRank(roleName);

        boolean isDefaultRank;
        isDefaultRank = town.getTownDefaultRank().equals(townRank.getName());

        Material roleMaterial = Material.getMaterial(townRank.getRankIconName());
        int rankLevel = townRank.getLevel();

        ItemStack roleIcon = HeadUtils.getCustomLoreItem(
                roleMaterial,
                Lang.GUI_TOWN_MEMBERS_ROLE_NAME.getTranslation(townRank.getName()),
                Lang.GUI_TOWN_MEMBERS_ROLE_NAME_DESC1.getTranslation()
        );
        ItemStack roleRankIcon = HeadUtils.getRankLevelColor(rankLevel);
        ItemStack membersRank = HeadUtils.makeSkull(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I0M2IyMzE4OWRjZjEzMjZkYTQyNTNkMWQ3NTgyZWY1YWQyOWY2YzI3YjE3MWZlYjE3ZTMxZDA4NGUzYTdkIn19fQ==");

        ArrayList<String> playerNames = new ArrayList<>();
        playerNames.add(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO_DESC1.getTranslation());
        for (String playerUUID : townRank.getPlayers()) {
            PlayerData playerData = PlayerDataStorage.get(playerUUID);
            String playerName = playerData.getName();
            playerNames.add(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO_DESC.getTranslation(playerName));
        }

        HeadUtils.addLore(membersRank, playerNames);

        ItemStack managePermission = HeadUtils.getCustomLoreItem(Material.ANVIL,Lang.GUI_TOWN_MEMBERS_ROLE_MANAGE_PERMISSION.getTranslation());

        ItemStack renameRank = HeadUtils.getCustomLoreItem(Material.NAME_TAG,Lang.GUI_TOWN_MEMBERS_ROLE_CHANGE_NAME.getTranslation());
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

        ItemStack makeRankDefault;
        if(isDefaultRank){
            makeRankDefault = HeadUtils.getCustomLoreItem(Material.RED_BED,
                    Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_IS_DEFAULT.getTranslation(),
                    Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT1.getTranslation());
        }
        else{
            makeRankDefault = HeadUtils.getCustomLoreItem(Material.RED_BED,
                    Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_IS_NOT_DEFAULT.getTranslation(),
                    Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT1.getTranslation(),
                    Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT2.getTranslation());
        }

        ItemStack removeRank = HeadUtils.getCustomLoreItem(Material.BARRIER, Lang.GUI_TOWN_MEMBERS_ROLE_DELETE.getTranslation());
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());


        GuiItem _roleIcon = ItemBuilder.from(roleIcon).asGuiItem(event -> {
            if(event.getCursor() == null)
                return;
            Material itemMaterial = event.getCursor().getData().getItemType();
            if(itemMaterial == Material.AIR){
                player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_NO_ITEM_SHOWED.getTranslation());
            }
            else {

                townRank.setRankIconName(itemMaterial.toString());
                OpenTownMenuRoleManager(player, roleName);
                player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_CHANGED_ICON_SUCCESS.getTranslation());

            }
            event.setCancelled(true);
        });

        GuiItem _roleRankIcon = ItemBuilder.from(roleRankIcon).asGuiItem(event -> {
            townRank.incrementLevel();
            OpenTownMenuRoleManager(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _managePermission = ItemBuilder.from(managePermission).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuRoleManagerPermissions(player,roleName);
        });
        GuiItem _membersRank = ItemBuilder.from(membersRank).asGuiItem(event -> {
            OpenTownMenuRoleManagerAddPlayer(player,roleName);
            event.setCancelled(true);
        });
        GuiItem _renameRank = ItemBuilder.from(renameRank).asGuiItem(event -> {

            player.closeInventory();
            player.sendMessage(ChatUtils.getTANString() + Lang.WRITE_IN_CHAT_NEW_ROLE_NAME.getTranslation());

            HashMap<String, String> newMap = new HashMap<>();
            newMap.put("rankName",roleName);
            PlayerChatListenerStorage.addPlayer(PlayerChatListenerStorage.ChatCategory.RANK_RENAME,player,newMap);
            event.setCancelled(true);
        });
        GuiItem _changeRoleTaxRelation = ItemBuilder.from(changeRoleTaxRelation).asGuiItem(event -> {
            townRank.swapPayingTaxes();
            OpenTownMenuRoleManager(player,roleName);
            event.setCancelled(true);
        });
        GuiItem _makeRankDefault = ItemBuilder.from(makeRankDefault).asGuiItem(event -> {
            if(isDefaultRank){
                player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_ALREADY_DEFAULT.getTranslation());
            }
            else{
                town.setTownDefaultRank(roleName);
            }
            event.setCancelled(true);
            OpenTownMenuRoleManager(player,roleName);
        });

        GuiItem _removeRank = ItemBuilder.from(removeRank).asGuiItem(event -> {
            if(townRank.getNumberOfPlayer() != 0){
                player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_DELETE_ERROR_NOT_EMPTY.getTranslation());
                event.setCancelled(true);
            }
            else{
                town.removeRank(townRank.getName());
                OpenTownMenuRoles(player);
                event.setCancelled(true);
            }
        });

        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMemberList(player);
        });

        gui.setItem(1,5, _roleIcon);

        gui.setItem(2,2, _roleRankIcon);
        gui.setItem(2,3, _membersRank);
        gui.setItem(2,4, _managePermission);
        gui.setItem(2,5, _renameRank);
        gui.setItem(2,6, _changeRoleTaxRelation);
        gui.setItem(2,7, _makeRankDefault);
        gui.setItem(2,8, _removeRank);

        gui.setItem(3,1, _getBackArrow);

        gui.open(player);

    }
    public static void OpenTownMenuRoleManagerAddPlayer(Player player, String roleName) {

        String name = "Town";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        TownData town = TownDataStorage.get(PlayerDataStorage.get(player.getUniqueId().toString()).getTownId());
        TownRank townRank = town.getRank(roleName);
        int i = 0;

        // this is the label for the outer loop
        for (String playerUUID : town.getPlayerList()) {
            boolean skip = false;

            for (String playerWithRoleUUID : townRank.getPlayers()) {
                if (playerUUID.equals(playerWithRoleUUID)) {
                    skip = true;
                    break;
                }
            }
            if (skip) {
                continue;
            }

            ItemStack playerHead = HeadUtils.getPlayerHead(PlayerDataStorage.get(playerUUID).getName(), Bukkit.getOfflinePlayer(UUID.fromString(playerUUID)));

            GuiItem _playerHead = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);

                PlayerData playerStat = PlayerDataStorage.get(playerUUID);
                town.getRank(playerStat.getTownRankID()).removePlayer(playerUUID);
                playerStat.setRank(roleName);
                townRank.addPlayer(playerUUID);

                OpenTownMenuRoleManager(player, roleName);
            });

            gui.setItem(i, _playerHead);
            i = i + 1;
        }



        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());


        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuRoleManager(player,roleName);
        });


        gui.setItem(3,1, _getBackArrow);

        gui.open(player);

    }
    public static void OpenTownMenuRoleManagerPermissions(Player player, String roleName) {

        String name = "Town";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        TownData town = TownDataStorage.get(PlayerDataStorage.get(player.getUniqueId().toString()).getTownId());
        TownRank townRank = town.getRank(roleName);


        ItemStack manage_taxes = HeadUtils.getCustomLoreItem(Material.GOLD_INGOT, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_TAXES.getTranslation(),(townRank.hasPermission(TownRolePermission.MANAGE_TAXES)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.getTranslation() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.getTranslation());
        ItemStack promote_rank_player = HeadUtils.getCustomLoreItem(Material.EMERALD, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_PROMOTE_RANK_PLAYER.getTranslation(),(townRank.hasPermission(TownRolePermission.PROMOTE_RANK_PLAYER)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.getTranslation() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.getTranslation());
        ItemStack derank_player = HeadUtils.getCustomLoreItem(Material.REDSTONE, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_DERANK_RANK_PLAYER.getTranslation(),(townRank.hasPermission(TownRolePermission.DERANK_RANK_PLAYER)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.getTranslation() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.getTranslation());
        ItemStack claim_chunk = HeadUtils.getCustomLoreItem(Material.EMERALD_BLOCK, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_CLAIM_CHUNK.getTranslation(),(townRank.hasPermission(TownRolePermission.CLAIM_CHUNK)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.getTranslation() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.getTranslation());
        ItemStack unclaim_chunk = HeadUtils.getCustomLoreItem(Material.REDSTONE_BLOCK, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_UNCLAIM_CHUNK.getTranslation(),(townRank.hasPermission(TownRolePermission.UNCLAIM_CHUNK)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.getTranslation() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.getTranslation());
        ItemStack upgrade_town = HeadUtils.getCustomLoreItem(Material.SPECTRAL_ARROW, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_UPGRADE_TOWN.getTranslation(),(townRank.hasPermission(TownRolePermission.UPGRADE_TOWN)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.getTranslation() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.getTranslation());
        ItemStack invite_player = HeadUtils.getCustomLoreItem(Material.SKELETON_SKULL, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_INVITE_PLAYER.getTranslation(),(townRank.hasPermission(TownRolePermission.INVITE_PLAYER)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.getTranslation() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.getTranslation());
        ItemStack kick_player = HeadUtils.getCustomLoreItem(Material.CREEPER_HEAD, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_KICK_PLAYER.getTranslation(),(townRank.hasPermission(TownRolePermission.KICK_PLAYER)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.getTranslation() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.getTranslation());
        ItemStack create_rank = HeadUtils.getCustomLoreItem(Material.LADDER, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_CREATE_RANK.getTranslation(),(townRank.hasPermission(TownRolePermission.CREATE_RANK)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.getTranslation() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.getTranslation());
        ItemStack delete_rank = HeadUtils.getCustomLoreItem(Material.CHAIN, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_DELETE_RANK.getTranslation(),(townRank.hasPermission(TownRolePermission.DELETE_RANK)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.getTranslation() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.getTranslation());
        ItemStack modify_rank = HeadUtils.getCustomLoreItem(Material.STONE_PICKAXE, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MODIFY_RANK.getTranslation(),(townRank.hasPermission(TownRolePermission.MANAGE_RANKS)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.getTranslation() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.getTranslation());
        ItemStack manage_claim_settings = HeadUtils.getCustomLoreItem(Material.GRASS_BLOCK, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_CLAIM_SETTINGS.getTranslation(),(townRank.hasPermission(TownRolePermission.MANAGE_CLAIM_SETTINGS)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.getTranslation() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.getTranslation());
        ItemStack manage_town_relation = HeadUtils.getCustomLoreItem(Material.FLOWER_POT, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_TOWN_RELATION.getTranslation(),(townRank.hasPermission(TownRolePermission.MANAGE_TOWN_RELATION)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.getTranslation() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.getTranslation());

        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());

        GuiItem _manage_taxes = ItemBuilder.from(manage_taxes).asGuiItem(event -> {
            townRank.switchPermission(TownRolePermission.MANAGE_TAXES);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _promote_rank_player = ItemBuilder.from(promote_rank_player).asGuiItem(event -> {
            townRank.switchPermission(TownRolePermission.PROMOTE_RANK_PLAYER);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _derank_player = ItemBuilder.from(derank_player).asGuiItem(event -> {
            townRank.switchPermission(TownRolePermission.DERANK_RANK_PLAYER);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _claim_chunk = ItemBuilder.from(claim_chunk).asGuiItem(event -> {
            townRank.switchPermission(TownRolePermission.CLAIM_CHUNK);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _unclaim_chunk = ItemBuilder.from(unclaim_chunk).asGuiItem(event -> {
            townRank.switchPermission(TownRolePermission.UNCLAIM_CHUNK);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _upgrade_town = ItemBuilder.from(upgrade_town).asGuiItem(event -> {
            townRank.switchPermission(TownRolePermission.UPGRADE_TOWN);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _invite_player = ItemBuilder.from(invite_player).asGuiItem(event -> {
            townRank.switchPermission(TownRolePermission.INVITE_PLAYER);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _kick_player = ItemBuilder.from(kick_player).asGuiItem(event -> {
            townRank.switchPermission(TownRolePermission.KICK_PLAYER);
            OpenTownMenuRoleManagerPermissions(player, roleName);

            event.setCancelled(true);
        });
        GuiItem _create_rank = ItemBuilder.from(create_rank).asGuiItem(event -> {
            townRank.switchPermission(TownRolePermission.CREATE_RANK);
            OpenTownMenuRoleManagerPermissions(player, roleName);

            event.setCancelled(true);
        });
        GuiItem _delete_rank = ItemBuilder.from(delete_rank).asGuiItem(event -> {
            townRank.switchPermission(TownRolePermission.DELETE_RANK);
            OpenTownMenuRoleManagerPermissions(player, roleName);

            event.setCancelled(true);
        });
        GuiItem _modify_rank = ItemBuilder.from(modify_rank).asGuiItem(event -> {
            townRank.switchPermission(TownRolePermission.MANAGE_RANKS);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _manage_claim_settings = ItemBuilder.from(manage_claim_settings).asGuiItem(event -> {
            townRank.switchPermission(TownRolePermission.MANAGE_CLAIM_SETTINGS);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _manage_town_relation = ItemBuilder.from(manage_town_relation).asGuiItem(event -> {
            townRank.switchPermission(TownRolePermission.MANAGE_TOWN_RELATION);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuRoleManager(player,roleName);
        });

        gui.setItem(1,1, _manage_taxes);
        gui.setItem(1,2, _promote_rank_player);
        gui.setItem(1,3, _derank_player);
        gui.setItem(1,4, _claim_chunk);
        gui.setItem(1,5, _unclaim_chunk);
        gui.setItem(1,6, _upgrade_town);
        gui.setItem(1,7, _invite_player);
        gui.setItem(2,1, _kick_player);
        gui.setItem(2,2, _create_rank);
        gui.setItem(2,3, _delete_rank);
        gui.setItem(2,4, _modify_rank);
        gui.setItem(2,5, _manage_claim_settings);
        gui.setItem(2,6, _manage_town_relation);

        gui.setItem(3,1, _getBackArrow);

        gui.open(player);

    }
    public static void OpenTownEconomics(Player player) {

        String name = "Town";
        int nRow = 4;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        TownData town = TownDataStorage.get(player);
        PlayerData playerStat = PlayerDataStorage.get(player);


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



        int nextTaxes = 0;

        for (String playerID : town.getPlayerList()){
            PlayerData otherPlayer = PlayerDataStorage.get(playerID);

            if(!otherPlayer.getTownRank().isPayingTaxes()){
                continue;
            }
            if(otherPlayer.getBalance() < town.getTreasury().getFlatTax()){
                continue;
            }
            nextTaxes = nextTaxes + town.getTreasury().getFlatTax();

        }

        HeadUtils.addLore(goldIcon,
                Lang.GUI_TREASURY_STORAGE_DESC1.getTranslation(town.getBalance()),
                Lang.GUI_TREASURY_STORAGE_DESC2.getTranslation(nextTaxes)
        );
        HeadUtils.addLore(goldSpendingIcon, Lang.GUI_TREASURY_SPENDING_DESC1.getTranslation(0), Lang.GUI_TREASURY_SPENDING_DESC2.getTranslation(0),Lang.GUI_TREASURY_SPENDING_DESC3.getTranslation(0));



        HeadUtils.addLore(lowerTax, Lang.GUI_TREASURY_LOWER_TAX_DESC1.getTranslation());
        HeadUtils.addLore(taxInfo, Lang.GUI_TREASURY_FLAT_TAX_DESC1.getTranslation(town.getTreasury().getFlatTax()));
        HeadUtils.addLore(increaseTax, Lang.GUI_TREASURY_INCREASE_TAX_DESC1.getTranslation());

        HeadUtils.addLore(salarySpending, Lang.GUI_TREASURY_SALARY_HISTORY_DESC1.getTranslation("0"));
        HeadUtils.addLore(chunkSpending, Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC1.getTranslation(0), Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC2.getTranslation(0),Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC3.getTranslation(town.getChunkSettings().getNumberOfClaimedChunk()));
        HeadUtils.addLore(workbenchSpending, Lang.GUI_TREASURY_MISCELLANEOUS_SPENDING_DESC1.getTranslation());

        HeadUtils.addLore(donation, Lang.GUI_TREASURY_DONATION_DESC1.getTranslation());
        HeadUtils.addLore(donationHistory, town.getTreasury().getDonationLimitedHistory(5));

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
            PlayerChatListenerStorage.addPlayer(PlayerChatListenerStorage.ChatCategory.DONATION,player);
            player.closeInventory();
            event.setCancelled(true);

        });
        GuiItem _donationHistory = ItemBuilder.from(donationHistory).asGuiItem(event -> {
            event.setCancelled(true);
        });

        GuiItem _lessTax = ItemBuilder.from(lowerTax).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.hasPermission(TownRolePermission.MANAGE_TAXES)) {
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
                return;
            }
            if(town.getTreasury().getFlatTax() <= 1){
                player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TREASURY_CANT_TAX_LESS.getTranslation());
                return;
            }

            town.getTreasury().remove1FlatTax();
            OpenTownEconomics(player);
        });
        GuiItem _taxInfo = ItemBuilder.from(taxInfo).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownEconomics(player);
        });
        GuiItem _moreTax = ItemBuilder.from(increaseTax).asGuiItem(event -> {
            event.setCancelled(true);

            if(!playerStat.hasPermission(TownRolePermission.MANAGE_TAXES)){
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
                return;
            }

            town.getTreasury().add1FlatTax();
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
        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData townData = TownDataStorage.get(player);
        TownLevel townLevel = townData.getTownLevel();

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        ItemStack TownIcon = HeadUtils.getTownIcon(PlayerDataStorage.get(player.getUniqueId().toString()).getTownId());
        ItemStack upgradeTownLevel = HeadUtils.getCustomLoreItem(Material.EMERALD, Lang.GUI_TOWN_LEVEL_UP.getTranslation());
        ItemStack upgradeChunkCap = HeadUtils.makeSkull(Lang.GUI_TOWN_LEVEL_UP_CHUNK_CAP.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=");
        ItemStack upgradePlayerCap = HeadUtils.makeSkull(Lang.GUI_TOWN_LEVEL_UP_PLAYER_CAP.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I0M2IyMzE4OWRjZjEzMjZkYTQyNTNkMWQ3NTgyZWY1YWQyOWY2YzI3YjE3MWZlYjE3ZTMxZDA4NGUzYTdkIn19fQ==");

        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());

        HeadUtils.addLore(upgradeTownLevel,
                Lang.GUI_TOWN_LEVEL_UP_DESC1.getTranslation(townLevel.getTownLevel()),
                Lang.GUI_TOWN_LEVEL_UP_DESC2.getTranslation(townLevel.getTownLevel()+1, townLevel.getMoneyRequiredTownLevel())
        );
        HeadUtils.addLore(upgradeChunkCap,
                Lang.GUI_TOWN_LEVEL_UP_CHUNK_CAP_DESC1.getTranslation(townLevel.getChunkCapLevel()),
                Lang.GUI_TOWN_LEVEL_UP_CHUNK_CAP_DESC2.getTranslation(townLevel.getChunkCapLevel()+1,townLevel.getMoneyRequiredChunkCap()),
                Lang.GUI_TOWN_LEVEL_UP_CHUNK_CAP_DESC3.getTranslation(townLevel.getMultiplierChunkCap()),
                Lang.GUI_TOWN_LEVEL_UP_CHUNK_CAP_DESC4.getTranslation(townLevel.getChunkCap())
        );
        HeadUtils.addLore(upgradePlayerCap,
                Lang.GUI_TOWN_LEVEL_UP_PLAYER_CAP_DESC1.getTranslation(townLevel.getPlayerCapLevel()),
                Lang.GUI_TOWN_LEVEL_UP_PLAYER_CAP_DESC2.getTranslation(townLevel.getPlayerCapLevel()+1,townLevel.getMoneyRequiredPlayerCap()),
                Lang.GUI_TOWN_LEVEL_UP_PLAYER_CAP_DESC3.getTranslation(townLevel.getMultiplierPlayerCap()),
                Lang.GUI_TOWN_LEVEL_UP_PLAYER_CAP_DESC4.getTranslation(townLevel.getPlayerCap())
        );


        GuiItem _TownIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _upgradeTownLevel = ItemBuilder.from(upgradeTownLevel).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.hasPermission(TownRolePermission.UPGRADE_TOWN)){
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
                return;
            }
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
            if(!playerStat.hasPermission(TownRolePermission.UPGRADE_TOWN)){
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
                return;
            }
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
            if(!playerStat.hasPermission(TownRolePermission.UPGRADE_TOWN)){
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
                return;
            }
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
    public static void OpenTownSettings(Player player) {

        String name = "Town";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(player);

        ItemStack TownIcon = HeadUtils.getTownIcon(playerStat.getTownId());
        ItemStack leaveTown = HeadUtils.getCustomLoreItem(Material.BARRIER,
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN.getTranslation(),
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC1.getTranslation(TownDataStorage.get(playerStat).getName()),
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC2.getTranslation());

        ItemStack deleteTown = HeadUtils.getCustomLoreItem(Material.BARRIER,
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN.getTranslation(),
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC1.getTranslation(TownDataStorage.get(playerStat).getName()),
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC2.getTranslation());

        ItemStack changeOwnershipTown = HeadUtils.getCustomLoreItem(Material.BEEHIVE,
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP.getTranslation(),
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC1.getTranslation(TownDataStorage.get(playerStat).getName()),
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC2.getTranslation());

        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW,
                Lang.GUI_BACK_ARROW.getTranslation());

        GuiItem _townIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });

        GuiItem _leaveTown = ItemBuilder.from(leaveTown).asGuiItem(event -> {
            event.setCancelled(true);
            if (playerStat.isTownLeader()) {
                player.sendMessage(ChatUtils.getTANString() + Lang.CHAT_CANT_LEAVE_TOWN_IF_LEADER.getTranslation());
            } else {
                playerTown.removePlayer(player);
                playerTown.getRank(playerStat.getTownRankID()).removePlayer(playerStat.getUuid());
                playerStat.leaveTown();
                player.sendMessage(ChatUtils.getTANString() + Lang.CHAT_PLAYER_LEFT_THE_TOWN.getTranslation());
                playerTown.broadCastMessage(ChatUtils.getTANString() + Lang.TOWN_BROADCAST_PLAYER_LEAVE_THE_TOWN.getTranslation(Bukkit.getOfflinePlayer(UUID.fromString(playerStat.getUuid())).getName()));
                player.closeInventory();
            }
        });
        GuiItem _deleteTown = ItemBuilder.from(deleteTown).asGuiItem(event -> {
            event.setCancelled(true);
            if (!playerStat.isTownLeader()) {
                player.sendMessage(ChatUtils.getTANString() + Lang.CHAT_CANT_DISBAND_TOWN_IF_NOT_LEADER.getTranslation());
                return;
            }

            ClaimedChunkStorage.unclaimAllChunkFrom(playerStat.getTownId());

            TownDataStorage.removeTown(playerStat.getTownId());

            for(String memberUUID : playerTown.getPlayerList()){
                PlayerData memberStat = PlayerDataStorage.get(memberUUID);
                memberStat.leaveTown();
            }


            playerStat.setTownId(null);
            playerStat.setRank(null);
            player.closeInventory();
            player.sendMessage(ChatUtils.getTANString() + Lang.CHAT_PLAYER_TOWN_SUCCESSFULLY_DELETED.getTranslation());

        });

        GuiItem _changeOwnershipTown = ItemBuilder.from(changeOwnershipTown).asGuiItem(event -> {

            event.setCancelled(true);

            if(playerStat.isTownLeader())
                OpenTownChangeOwnershipPlayerSelect(player);
            else
                player.sendMessage(ChatUtils.getTANString() + Lang.NOT_TOWN_LEADER_ERROR.getTranslation());

        });

        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuHaveTown(player);
        });


        gui.setItem(4, _townIcon);
        gui.setItem(10, _leaveTown);
        gui.setItem(11, _deleteTown);
        gui.setItem(12, _changeOwnershipTown);
        gui.setItem(18, _getBackArrow);

        gui.open(player);
    }
    public static void OpenTownChangeOwnershipPlayerSelect(Player player) {

        String name = "Town";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        PlayerData senderStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(player);

        int i = 0;
        for (String playerUUID : playerTown.getPlayerList()){
            OfflinePlayer townPlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerUUID));

            ItemStack playerHead = HeadUtils.getPlayerHead(townPlayer.getName(),townPlayer);
            HeadUtils.addLore(playerHead,
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC1.getTranslation(player.getName()),
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC2.getTranslation()
            );

            GuiItem _playerHead = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);

                playerTown.setUuidLeader(townPlayer.getUniqueId().toString());
                player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_SUCCESS.getTranslation(townPlayer.getName()));
                OpenTownMenuHaveTown(player);
            });

            gui.setItem(i, _playerHead);

            i = i+1;
        }

        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW,
                Lang.GUI_BACK_ARROW.getTranslation());



        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownSettings(player);
        });


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
            OpenTownRelation(player,TownRelation.WAR);
        });
        GuiItem _EmbargoCategory = ItemBuilder.from(EmbargoCategory).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelation(player,TownRelation.EMBARGO);

        });
        GuiItem _NAPCategory = ItemBuilder.from(NAPCategory).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelation(player,TownRelation.NON_AGGRESSION);

        });
        GuiItem _AllianceCategory = ItemBuilder.from(AllianceCategory).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelation(player,TownRelation.ALLIANCE);
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
    public static void OpenTownRelation(Player player, TownRelation relation) {

        String name = "Town - Relation";
        int nRow = 4;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(playerStat);

        ArrayList<String> TownListUUID = playerTown.getRelations().getOne(relation);
        int i = 0;
        for(String otherTownUUID : TownListUUID){
            ItemStack townIcon = HeadUtils.getTownIconWithInformations(otherTownUUID);

            if(relation == TownRelation.WAR) {
                ItemMeta meta = townIcon.getItemMeta();
                List<String> lore = meta.getLore();
                lore.add(Lang.GUI_TOWN_ATTACK_TOWN_DESC1.getTranslation());
                lore.add(Lang.GUI_TOWN_ATTACK_TOWN_DESC2.getTranslation());
                meta.setLore(lore);
            }

                GuiItem _town = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);

                if(relation == TownRelation.WAR){
                    player.sendMessage(getTANString() + Lang.GUI_TOWN_ATTACK_TOWN_EXECUTED.getTranslation());
                    WarTaggedPlayer.addPlayersToTown(otherTownUUID,playerTown.getPlayerList());
                    TownDataStorage.get(otherTownUUID).broadCastMessage(getTANString() + Lang.GUI_TOWN_ATTACK_TOWN_INFO.getTranslation());
                }


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
            if(!playerStat.hasPermission(TownRolePermission.MANAGE_TOWN_RELATION)){
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
                return;
            }
            OpenTownRelationModification(player,Action.ADD,relation);
        });
        GuiItem _remove = ItemBuilder.from(removeTownButton).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.hasPermission(TownRolePermission.MANAGE_TOWN_RELATION)){
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
                return;
            }
            OpenTownRelationModification(player,Action.REMOVE,relation);
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

        gui.setItem(4,1,_back);
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
    public static void OpenTownRelationModification(Player player, Action action, TownRelation relation) {

        String name = "Town - Relation";
        int nRow = 4;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        TownData playerTown = TownDataStorage.get(playerStat);

        LinkedHashMap<String, TownData> allTown = getTownList();
        ArrayList<String> TownListUUID = playerTown.getRelations().getOne(relation);

        GuiItem _decorativeGlass = ItemBuilder.from(new ItemStack(Material.GREEN_STAINED_GLASS_PANE)).asGuiItem(event -> {
            event.setCancelled(true);
        });

        if(action == Action.ADD){
            List<String> townNoRelation = new ArrayList<>(allTown.keySet());
            townNoRelation.removeAll(TownListUUID);
            townNoRelation.remove(playerTown.getID());
            int i = 0;
            for(String otherTownUUID : townNoRelation){
                TownData otherTown = TownDataStorage.get(otherTownUUID);
                ItemStack townIcon = HeadUtils.getTownIconWithInformations(otherTownUUID, playerTown.getID());

                GuiItem _town = ItemBuilder.from(townIcon).asGuiItem(event -> {
                    event.setCancelled(true);


                    if(HaveRelation(playerTown, otherTown)){
                        player.sendMessage("Already a relation with this town, end it before changing");
                        return;
                    }
                    if(relation.getNeedsConfirmationToStart()){
                        player.sendMessage(ChatUtils.getTANString() + "Sent to the leader of the other town");

                        Player otherTownLeader = Bukkit.getPlayer(UUID.fromString(otherTown.getUuidLeader()));

                        TownRelationConfirmStorage.addInvitation(otherTown.getUuidLeader(), playerTown.getID(), relation);

                        otherTownLeader.sendMessage(getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_RECEIVED_1.getTranslation(playerTown.getName(),relation.getColor() + relation.getName()));
                        ChatUtils.sendClickableCommand(otherTownLeader,getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_RECEIVED_2.getTranslation(),"tan accept "  + playerTown.getID());

                        player.closeInventory();
                    }
                    else{
                        playerTown.broadCastMessage(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.getTranslation(otherTown.getName(),relation.getColor() + relation.getName()));
                        otherTown.broadCastMessage(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.getTranslation(playerTown.getName(),relation.getColor() + relation.getName()));
                        addTownRelation(playerTown,otherTown,relation);
                        OpenTownRelation(player,relation);
                    }
                });
                gui.setItem(i, _town);
                i = i+1;
                _decorativeGlass = ItemBuilder.from(new ItemStack(Material.GREEN_STAINED_GLASS_PANE)).asGuiItem(event -> {
                    event.setCancelled(true);
                });
            }


        }
        else if(action == Action.REMOVE){
            int i = 0;
            for(String otherTownUUID : TownListUUID){
                TownData otherTown = TownDataStorage.get(otherTownUUID);
                ItemStack townIcon = HeadUtils.getTownIconWithInformations(otherTownUUID);
                GuiItem _town = ItemBuilder.from(townIcon).asGuiItem(event -> {
                    event.setCancelled(true);

                    if(relation.getNeedsConfirmationToEnd()){
                        player.sendMessage(ChatUtils.getTANString() + "Sent to the leader of the other town");

                        Player otherTownLeader = Bukkit.getPlayer(UUID.fromString(otherTown.getUuidLeader()));

                        TownRelationConfirmStorage.addInvitation(otherTown.getUuidLeader(), playerTown.getID(), null);

                        otherTownLeader.sendMessage(getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_RECEIVED_1.getTranslation(playerTown.getName(),"neutral"));
                        ChatUtils.sendClickableCommand(otherTownLeader,getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_RECEIVED_2.getTranslation(),"tan accept "  + playerTown.getID());
                        player.closeInventory();
                    }
                    else{
                        playerTown.broadCastMessage(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.getTranslation(otherTown.getName(),"neutral"));
                        otherTown.broadCastMessage(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.getTranslation(playerTown.getName(),"neutral"));
                        removeRelation(playerTown,otherTown,relation);
                        OpenTownRelation(player,relation);
                    }



                    OpenTownRelation(player,relation);
                });
                gui.setItem(i, _town);
                i = i+1;
            }
            TownDataStorage.get(playerTown.getID()).removeTownRelations(relation,player.getUniqueId().toString());
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

        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        TownData townClass = TownDataStorage.get(player);
        ClaimedChunkSettings townChunkSettings = townClass.getChunkSettings();

        ItemStack doorAccess = HeadUtils.getCustomLoreItem(Material.OAK_DOOR,
                Lang.GUI_TOWN_CLAIM_SETTINGS_DOOR.getTranslation(),
                Lang.GUI_TOWN_CLAIM_SETTINGS_DOOR_DESC1.getTranslation(townChunkSettings.getDoorAuth().getColor() + townChunkSettings.getDoorAuth().getName()),
                Lang.GUI_LEFT_CLICK_TO_INTERACT.getTranslation());
        ItemStack chestAccess = HeadUtils.getCustomLoreItem(Material.CHEST,
                Lang.GUI_TOWN_CLAIM_SETTINGS_CHEST.getTranslation(),
                Lang.GUI_TOWN_CLAIM_SETTINGS_CHEST_DESC1.getTranslation(townChunkSettings.getChestAuth().getColor() + townChunkSettings.getChestAuth().getName()),
                Lang.GUI_LEFT_CLICK_TO_INTERACT.getTranslation());
        ItemStack placeBlockAccess = HeadUtils.getCustomLoreItem(Material.BRICKS,
                Lang.GUI_TOWN_CLAIM_SETTINGS_BUILD.getTranslation(),
                Lang.GUI_TOWN_CLAIM_SETTINGS_BUILD_DESC1.getTranslation(townChunkSettings.getPlaceAuth().getColor() + townChunkSettings.getPlaceAuth().getName()),
                Lang.GUI_LEFT_CLICK_TO_INTERACT.getTranslation());
        ItemStack breakBlockAccess = HeadUtils.getCustomLoreItem(Material.IRON_PICKAXE,
                Lang.GUI_TOWN_CLAIM_SETTINGS_BREAK.getTranslation(),
                Lang.GUI_TOWN_CLAIM_SETTINGS_BREAK_DESC1.getTranslation(townChunkSettings.getBreakAuth().getColor() + townChunkSettings.getBreakAuth().getName()),
                Lang.GUI_LEFT_CLICK_TO_INTERACT.getTranslation());
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());



        GuiItem _doorAccessManager = ItemBuilder.from(doorAccess).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.hasPermission(TownRolePermission.MANAGE_CLAIM_SETTINGS)){
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
                return;
            }
            townClass.getChunkSettings().nextDoorAuth();
            OpenTownChunkMenu(player);
        });
        GuiItem _chestAccessManager = ItemBuilder.from(chestAccess).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.hasPermission(TownRolePermission.MANAGE_CLAIM_SETTINGS)){
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
                return;
            }
            townClass.getChunkSettings().nextChestAuth();
            OpenTownChunkMenu(player);
        });
        GuiItem _placeBlockAccessManager = ItemBuilder.from(placeBlockAccess).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.hasPermission(TownRolePermission.MANAGE_CLAIM_SETTINGS)){
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
                return;
            }
            townClass.getChunkSettings().nextPlaceAuth();
            OpenTownChunkMenu(player);
        });
        GuiItem _breakBlockAccessManager = ItemBuilder.from(breakBlockAccess).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.hasPermission(TownRolePermission.MANAGE_CLAIM_SETTINGS)){
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
                return;
            }
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
