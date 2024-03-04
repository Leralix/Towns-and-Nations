package org.tan.TownsAndNations.GUI;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tan.TownsAndNations.DataClass.*;
import org.tan.TownsAndNations.Lang.DynamicLang;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.*;
import org.tan.TownsAndNations.storage.*;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.RegionDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.storage.Invitation.RegionInviteDataStorage;
import org.tan.TownsAndNations.storage.Invitation.TownRelationConfirmStorage;
import org.tan.TownsAndNations.storage.Legacy.UpgradeStorage;
import org.tan.TownsAndNations.utils.*;

import static org.tan.TownsAndNations.TownsAndNations.isDynmapAddonLoaded;
import static org.tan.TownsAndNations.TownsAndNations.isSqlEnable;
import static org.tan.TownsAndNations.enums.ChatCategory.*;
import static org.tan.TownsAndNations.enums.MessageKey.*;
import static org.tan.TownsAndNations.enums.SoundEnum.*;
import static org.tan.TownsAndNations.enums.TownRolePermission.*;
import static org.tan.TownsAndNations.storage.MobChunkSpawnStorage.getMobSpawnCost;
import static org.tan.TownsAndNations.storage.DataStorage.TownDataStorage.getTownMap;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;
import static org.tan.TownsAndNations.utils.HeadUtils.*;
import static org.tan.TownsAndNations.utils.RelationUtil.*;
import static org.tan.TownsAndNations.utils.StringUtil.getHexColor;
import static org.tan.TownsAndNations.utils.TeamUtils.updateAllScoreboardColor;
import static org.tan.TownsAndNations.utils.TownUtil.*;

import java.util.ArrayList;


import java.util.*;
import java.util.function.Consumer;

public class GuiManager2 implements IGUI {

    public static void OpenMainMenu(Player player){

        PlayerData playerStat = PlayerDataStorage.get(player);
        boolean playerHaveTown = playerStat.haveTown();
        boolean playerHaveRegion = playerStat.haveRegion();

        TownData town = TownDataStorage.get(playerStat);
        RegionData region = null;
        if(playerHaveRegion){
            region = town.getRegion();
        }


        Gui gui = IGUI.createChestGui("Main menu",3);

        ItemStack KingdomHead = HeadUtils.makeSkull(Lang.GUI_KINGDOM_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY5MTk2YjMzMGM2Yjg5NjJmMjNhZDU2MjdmYjZlY2NlNDcyZWFmNWM5ZDQ0Zjc5MWY2NzA5YzdkMGY0ZGVjZSJ9fX0=");
        ItemStack RegionHead = HeadUtils.makeSkull(Lang.GUI_REGION_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljMTgzMmU0ZWY1YzRhZDljNTE5ZDE5NGIxOTg1MDMwZDI1NzkxNDMzNGFhZjI3NDVjOWRmZDYxMWQ2ZDYxZCJ9fX0=");
        ItemStack TownHead = HeadUtils.makeSkull(Lang.GUI_TOWN_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=");
        ItemStack PlayerHead = HeadUtils.getPlayerHeadInformation(player);

        HeadUtils.setLore(KingdomHead, Lang.GUI_KINGDOM_ICON_DESC1.get());

        HeadUtils.setLore(RegionHead, playerHaveRegion?
                Lang.GUI_REGION_ICON_DESC1_REGION.get(region.getName()):Lang.GUI_REGION_ICON_DESC1_NO_REGION.get());

        HeadUtils.setLore(TownHead, playerHaveTown?
                Lang.GUI_TOWN_ICON_DESC1_HAVE_TOWN.get(town.getName()):Lang.GUI_TOWN_ICON_DESC1_NO_TOWN.get());


        GuiItem Kingdom = ItemBuilder.from(KingdomHead).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(getTANString() + Lang.GUI_WARNING_STILL_IN_DEV.get());
        });
        GuiItem Region = ItemBuilder.from(RegionHead).asGuiItem(event -> {
            event.setCancelled(true);
            if(playerStat.haveRegion()) {
                OpenRegionMenu(player);
            }
            else {
                OpenNoRegionMenu(player);
            }
        });
        GuiItem Town = ItemBuilder.from(TownHead).asGuiItem(event -> {
            event.setCancelled(true);
            if(PlayerDataStorage.get(player).haveTown()){
                OpenTownMenuHaveTown(player);
            }
            else{
                OpenTownMenuNoTown(player);
            }
        });
        GuiItem Player = ItemBuilder.from(PlayerHead).asGuiItem(event -> event.setCancelled(true));

        gui.setItem(10,Kingdom);
        gui.setItem(12,Region);
        gui.setItem(14,Town);
        gui.setItem(16,Player);
        gui.setItem(18,IGUI.CreateBackArrow(player, p -> player.closeInventory()));

        gui.open(player);
    }
    public static void OpenProfileMenu(Player player){

        Gui gui = IGUI.createChestGui("Profile",3);


        ItemStack PlayerHead = HeadUtils.getPlayerHead(Lang.GUI_YOUR_PROFILE.get(),player);
        ItemStack GoldPurse = HeadUtils.getCustomLoreItem(Material.GOLD_NUGGET, Lang.GUI_YOUR_BALANCE.get(),Lang.GUI_YOUR_BALANCE_DESC1.get(EconomyUtil.getBalance(player)));
        ItemStack killList = HeadUtils.getCustomLoreItem(Material.IRON_SWORD, Lang.GUI_YOUR_PVE_KILLS.get(),Lang.GUI_YOUR_PVE_KILLS_DESC1.get(player.getStatistic(Statistic.MOB_KILLS)));
        int time = player.getStatistic(Statistic.PLAY_ONE_MINUTE) /20 / 86400;
        ItemStack lastDeath = HeadUtils.getCustomLoreItem(Material.SKELETON_SKULL, Lang.GUI_YOUR_CURRENT_TIME_ALIVE.get(),Lang.GUI_YOUR_CURRENT_TIME_ALIVE_DESC1.get(time));
        ItemStack totalRpKills = HeadUtils.getCustomLoreItem(Material.SKELETON_SKULL, Lang.GUI_YOUR_CURRENT_MURDER.get(),Lang.GUI_YOUR_CURRENT_MURDER_DESC1.get("0"));

        GuiItem Head = ItemBuilder.from(PlayerHead).asGuiItem(event -> event.setCancelled(true));
        GuiItem Gold = ItemBuilder.from(GoldPurse).asGuiItem(event -> event.setCancelled(true));
        GuiItem Kill = ItemBuilder.from(killList).asGuiItem(event -> event.setCancelled(true));
        GuiItem LD = ItemBuilder.from(lastDeath).asGuiItem(event -> event.setCancelled(true));
        GuiItem RPkill = ItemBuilder.from(totalRpKills).asGuiItem(event -> event.setCancelled(true));

        gui.setItem(4, Head);
        gui.setItem(10, Gold);
        gui.setItem(12, Kill);
        gui.setItem(14, LD);
        gui.setItem(16, RPkill);
        gui.setItem(18, IGUI.CreateBackArrow(player,p -> OpenMainMenu(player)));

        gui.open(player);
    }
    public static void OpenTownMenuNoTown(Player player){

        Gui gui = IGUI.createChestGui("Town",3);

        int townPrice = ConfigUtil.getCustomConfig("config.yml").getInt("CostOfCreatingTown");

        ItemStack createTown = HeadUtils.getCustomLoreItem(Material.GRASS_BLOCK,
                Lang.GUI_NO_TOWN_CREATE_NEW_TOWN.get(),
                Lang.GUI_NO_TOWN_CREATE_NEW_TOWN_DESC1.get(townPrice));
        ItemStack joinLand = HeadUtils.getCustomLoreItem(Material.ANVIL,
                Lang.GUI_NO_TOWN_JOIN_A_TOWN.get(),
                Lang.GUI_NO_TOWN_JOIN_A_TOWN_DESC1.get(TownDataStorage.getNumberOfTown()));

        GuiItem _create = ItemBuilder.from(createTown).asGuiItem(event -> {
            event.setCancelled(true);
            TownUtil.registerNewTown(player,townPrice);
        });

        GuiItem _join = ItemBuilder.from(joinLand).asGuiItem(event -> {
            event.setCancelled(true);
            OpenSearchTownMenu(player);
        });

        gui.setItem(11, _create);
        gui.setItem(15, _join);
        gui.setItem(18, IGUI.CreateBackArrow(player,p -> OpenMainMenu(player)));

        gui.open(player);
    }
    public static void OpenSearchTownMenu(Player player) {

        Gui gui = IGUI.createChestGui("Town",3);

        HashMap<String, TownData> townDataStorage = getTownMap();

        int i = 0;
        for (TownData townData : townDataStorage.values()) {

            ItemStack townIcon = HeadUtils.getTownIcon(townData);

            HeadUtils.setLore(townIcon,
                    Lang.GUI_TOWN_INFO_DESC0.get(townData.getDescription()),
                    Lang.GUI_TOWN_INFO_DESC1.get(Bukkit.getServer().getOfflinePlayer(UUID.fromString(townData.getLeaderID())).getName()),
                    Lang.GUI_TOWN_INFO_DESC2.get(townData.getPlayerList().size()),
                    Lang.GUI_TOWN_INFO_DESC3.get(townData.getNumberOfClaimedChunk()),
                    "",
                    (townData.isRecruiting()) ? Lang.GUI_TOWN_INFO_IS_RECRUITING.get() : Lang.GUI_TOWN_INFO_IS_NOT_RECRUITING.get(),
                    (townData.isPlayerAlreadyRequested(player)) ? Lang.GUI_TOWN_INFO_RIGHT_CLICK_TO_CANCEL.get() : Lang.GUI_TOWN_INFO_LEFT_CLICK_TO_JOIN.get()
            );

            GuiItem _townIteration = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);

                if(event.isLeftClick()){
                    if(townData.isPlayerAlreadyRequested(player)){
                       return;
                    }
                    if(!townData.isRecruiting()){
                        player.sendMessage(getTANString() + Lang.PLAYER_TOWN_NOT_RECRUITING.get());
                        return;
                    }
                    townData.addPlayerJoinRequest(player);
                    player.sendMessage(getTANString() + Lang.PLAYER_ASK_TO_JOIN_TOWN_PLAYER_SIDE.get(townData.getName()));
                    OpenSearchTownMenu(player);
                }

                if(event.isRightClick()){
                    if(!townData.isPlayerAlreadyRequested(player)){
                        return;
                    }
                    townData.removePlayerJoinRequest(player);
                    player.sendMessage(getTANString() + Lang.PLAYER_REMOVE_ASK_TO_JOIN_TOWN_PLAYER_SIDE.get());
                    OpenSearchTownMenu(player);
                }

            });

            gui.setItem(i, _townIteration);
            i++;

        }

        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenTownMenuNoTown(player)));

        gui.open(player);
    }
    public static void OpenTownMenuHaveTown(Player player) {
        Gui gui = IGUI.createChestGui("Town",3);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(playerStat);

        ItemStack TownIcon = HeadUtils.getTownIcon(playerTown);
        HeadUtils.setLore(TownIcon,
                Lang.GUI_TOWN_INFO_DESC0.get(playerTown.getDescription()),
                "",
                Lang.GUI_TOWN_INFO_DESC1.get(Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerTown.getLeaderID())).getName()),
                Lang.GUI_TOWN_INFO_DESC2.get(playerTown.getPlayerList().size()),
                Lang.GUI_TOWN_INFO_DESC3.get(playerTown.getNumberOfClaimedChunk()),
                Lang.GUI_TOWN_INFO_DESC4.get(playerTown.getBalance()),
                playerTown.haveRegion()? Lang.GUI_TOWN_INFO_DESC5_REGION.get(playerTown.getRegion().getName()): Lang.GUI_TOWN_INFO_DESC5_NO_REGION.get(),
                Lang.GUI_TOWN_INFO_CHANGE_ICON.get()
        );

        ItemStack GoldIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_TREASURY_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        HeadUtils.setLore(GoldIcon, Lang.GUI_TOWN_TREASURY_ICON_DESC1.get());

        ItemStack SkullIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_MEMBERS_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Q0ZDQ5NmIxZGEwNzUzNmM5NGMxMzEyNGE1ODMzZWJlMGM1MzgyYzhhMzM2YWFkODQ2YzY4MWEyOGQ5MzU2MyJ9fX0=");
        HeadUtils.setLore(SkullIcon, Lang.GUI_TOWN_MEMBERS_ICON_DESC1.get());

        ItemStack ClaimIcon = HeadUtils.makeSkull(Lang.GUI_CLAIM_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=");
        HeadUtils.setLore(ClaimIcon, Lang.GUI_CLAIM_ICON_DESC1.get());

        ItemStack otherTownIcon = HeadUtils.makeSkull(Lang.GUI_OTHER_TOWN_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMzc0ZTIxYjgxYzBiMjFhYmViOGU5N2UxM2UwNzdkM2VkMWVkNDRmMmU5NTZjNjhmNjNhM2UxOWU4OTlmNiJ9fX0=");
        HeadUtils.setLore(otherTownIcon, Lang.GUI_OTHER_TOWN_ICON_DESC1.get());

        ItemStack RelationIcon = HeadUtils.makeSkull(Lang.GUI_RELATION_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUwN2Q2ZGU2MzE4MzhlN2E3NTcyMGU1YjM4ZWYxNGQyOTY2ZmRkODQ4NmU3NWQxZjY4MTJlZDk5YmJjYTQ5OSJ9fX0=");
        HeadUtils.setLore(RelationIcon, Lang.GUI_RELATION_ICON_DESC1.get());

        ItemStack LevelIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_LEVEL_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmJlNTI5YWI2YjJlYTdjNTBkOTE5MmQ4OWY4OThmZDdkYThhOWU3NTBkMzc4Mjk1ZGY3MzIwNWU3YTdlZWFlMCJ9fX0=");
        HeadUtils.setLore(LevelIcon, Lang.GUI_TOWN_LEVEL_ICON_DESC1.get());

        ItemStack SettingIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_SETTINGS_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVkMmNiMzg0NThkYTE3ZmI2Y2RhY2Y3ODcxNjE2MDJhMjQ5M2NiZjkzMjMzNjM2MjUzY2ZmMDdjZDg4YTljMCJ9fX0=");
        HeadUtils.setLore(SettingIcon, Lang.GUI_TOWN_SETTINGS_ICON_DESC1.get());

        GuiItem _townIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> {
            event.setCancelled(true);

            if(!playerStat.isTownLeader())
                return;
            if(event.getCursor() == null)
                return;

            Material itemMaterial = event.getCursor().getType();
            if(itemMaterial == Material.AIR || itemMaterial == Material.LEGACY_AIR){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_NO_ITEM_SHOWED.get());
            }

            else {
                playerTown.setTownIconMaterialCode(itemMaterial);
                OpenTownMenuHaveTown(player);
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_CHANGED_ICON_SUCCESS.get());
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
            OpenTownChunk(player);
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


        gui.setItem(4, _townIcon);
        gui.setItem(10, _goldIcon);
        gui.setItem(11, _membersIcon);
        gui.setItem(12, _claimIcon);
        gui.setItem(13, _otherTownIcon);
        gui.setItem(14, _relationIcon);
        gui.setItem(15, _levelIcon);
        gui.setItem(16, _settingsIcon);
        gui.setItem(18, IGUI.CreateBackArrow(player,p -> OpenMainMenu(player)));

        gui.open(player);
    }
    public static void OpenTownMenuOtherTown(Player player) {
        Gui gui = IGUI.createChestGui("Town",3);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(playerStat);

        HashMap<String, TownData> townDataStorage = getTownMap();

        int i = 0;
        for (TownData otherTown: townDataStorage.values()) {


            ItemStack townIcon = HeadUtils.getTownIcon(otherTown.getID());
            TownRelation relation = playerTown.getRelationWith(otherTown);

            String relationName;
            if(relation == null){
                relationName = Lang.GUI_TOWN_RELATION_NEUTRAL.get();
            }
            else {
                relationName = relation.getColor() + relation.getName();
            }

            HeadUtils.setLore(townIcon,
                    Lang.GUI_TOWN_INFO_DESC0.get(otherTown.getDescription()),
                    Lang.GUI_TOWN_INFO_DESC1.get(Bukkit.getServer().getOfflinePlayer(UUID.fromString(otherTown.getLeaderID())).getName()),
                    Lang.GUI_TOWN_INFO_DESC2.get(otherTown.getPlayerList().size()),
                    Lang.GUI_TOWN_INFO_DESC3.get(otherTown.getNumberOfClaimedChunk()),
                    Lang.GUI_TOWN_INFO_TOWN_RELATION.get(relationName)
            );

            GuiItem _townIteration = ItemBuilder.from(townIcon).asGuiItem(event -> event.setCancelled(true));

            gui.setItem(i, _townIteration);
            i++;

        }
        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenTownMenuHaveTown(player)));

        gui.open(player);
    }
    public static void OpenTownMemberList(Player player) {

        Gui gui = IGUI.createChestGui("Town",3);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData town = TownDataStorage.get(playerStat);

        int i = 0;
        for (String playerUUID: town.getPlayerList()) {

            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            PlayerData otherPlayerStat = PlayerDataStorage.get(playerUUID);
            assert otherPlayerStat != null;

            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate);
            HeadUtils.setLore(
                    playerHead,
                    Lang.GUI_TOWN_MEMBER_DESC1.get(town.getRank(otherPlayerStat.getTownRankID()).getColoredName()),
                    Lang.GUI_TOWN_MEMBER_DESC2.get(EconomyUtil.getBalance(playerIterate)),
                    playerStat.hasPermission(KICK_PLAYER) ? Lang.GUI_TOWN_MEMBER_DESC3.get() : ""
            );
            GuiItem _playerIcon = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                if(event.getClick() == ClickType.RIGHT){
                    TownUtil.kickPlayer(player,playerIterate);
                }
                OpenTownMemberList(player);
            });

            gui.setItem(i, _playerIcon);
            i++;
        }

        ItemStack manageRanks = HeadUtils.getCustomLoreItem(Material.LADDER, Lang.GUI_TOWN_MEMBERS_MANAGE_ROLES.get());
        ItemStack manageApplication = HeadUtils.getCustomLoreItem(Material.WRITABLE_BOOK,
                Lang.GUI_TOWN_MEMBERS_MANAGE_APPLICATION.get(),
                Lang.GUI_TOWN_MEMBERS_MANAGE_APPLICATION_DESC1.get(town.getPlayerJoinRequestSet().size())
        );

        GuiItem _manageRanks = ItemBuilder.from(manageRanks).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuRoles(player);
        });
        GuiItem _manageApplication = ItemBuilder.from(manageApplication).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownApplications(player);
        });

        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenTownMenuHaveTown(player)));
        gui.setItem(3,2, _manageRanks);
        gui.setItem(3,3, _manageApplication);


        gui.open(player);

    }
    public static void OpenTownApplications(Player player) {

        Gui gui = IGUI.createChestGui("Town",3);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData town = TownDataStorage.get(playerStat);

        HashSet<String> players = town.getPlayerJoinRequestSet();

        int i = 0;
        for (String playerUUID: players) {

            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            PlayerData playerIterateData = PlayerDataStorage.get(playerUUID);
            assert playerIterateData != null;

            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate);

            HeadUtils.setLore(
                    playerHead,
                    Lang.GUI_PLAYER_ASK_JOIN_PROFILE_DESC2.get(),
                    Lang.GUI_PLAYER_ASK_JOIN_PROFILE_DESC3.get()
            );

            GuiItem _playerIcon = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                if(event.isLeftClick()){

                    if(!playerStat.hasPermission(TownRolePermission.INVITE_PLAYER)){
                        player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                        return;
                    }
                    if(!town.canAddMorePlayer()){
                        player.sendMessage(getTANString() + Lang.INVITATION_TOWN_FULL.get());
                        return;
                    }

                    town.addPlayer(playerIterateData.getUuid());
                    town.getRank(town.getTownDefaultRank()).addPlayer(playerIterateData.getUuid());

                    playerIterateData.setTownId(town.getID());
                    playerIterateData.setRank(town.getTownDefaultRank());

                    Player playerIterateOnline = playerIterate.getPlayer();
                    if(playerIterateOnline != null){
                        playerIterateOnline.sendMessage(getTANString() + Lang.TOWN_INVITATION_ACCEPTED_MEMBER_SIDE.get(town.getName()));
                    }
                    town.broadCastMessageWithSound(Lang.TOWN_INVITATION_ACCEPTED_TOWN_SIDE.get(player.getName()),
                            MINOR_GOOD);

                    updateAllScoreboardColor();

                    town.removePlayerJoinRequest(playerIterateData.getUuid());

                    for (TownData allTown : TownDataStorage.getTownMap().values()){
                        allTown.removePlayerJoinRequest(playerIterateData.getUuid());
                    }

                    player.sendMessage(getTANString() + Lang.PLAYER_REMOVE_ASK_TO_JOIN_TOWN_PLAYER_SIDE.get());

                }
                if(event.isRightClick()){
                    if(!playerStat.hasPermission(TownRolePermission.INVITE_PLAYER)){
                        player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                        return;
                    }

                    town.removePlayerJoinRequest(playerIterateData.getUuid());
                    player.sendMessage(getTANString() + Lang.PLAYER_REMOVE_ASK_TO_JOIN_TOWN_PLAYER_SIDE.get());
                }
                OpenTownMemberList(player);
            });

            gui.setItem(i, _playerIcon);
            i++;
        }

        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenTownMemberList(player)));


        gui.open(player);

    }
    public static void OpenTownMenuRoles(Player player) {

        Gui gui = IGUI.createChestGui("Town",3);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData town = TownDataStorage.get(playerStat);

        int i = 0;
        for (TownRank townRank: town.getTownRanks()) {

            Material townMaterial = Material.getMaterial(townRank.getRankIconName());
            ItemStack townRankItemStack = HeadUtils.getCustomLoreItem(townMaterial, townRank.getColoredName());
            GuiItem _townRankItemStack = ItemBuilder.from(townRankItemStack).asGuiItem(event -> {
                event.setCancelled(true);
                if(!playerStat.hasPermission(TownRolePermission.MANAGE_RANKS)) {
                    player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                    return;
                }
                if(town.getRank(playerStat).getLevel() >= townRank.getLevel() && !town.isLeader(player)){
                    player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get());
                    return;
                }
                OpenTownMenuRoleManager(player,townRank.getName());
            });
            gui.setItem(i, _townRankItemStack);
            i = i+1;
        }

        ItemStack createNewRole = HeadUtils.getCustomLoreItem(Material.EGG, Lang.GUI_TOWN_MEMBERS_ADD_NEW_ROLES.get());
        GuiItem _createNewRole = ItemBuilder.from(createNewRole).asGuiItem(event -> {
            event.setCancelled(true);

            if(playerStat.hasPermission(TownRolePermission.CREATE_RANK)){
                if(town.getNumberOfRank() >= 8){
                    player.sendMessage(getTANString() + Lang.TOWN_RANK_CAP_REACHED.get());
                    return;
                }

                player.sendMessage(getTANString() + Lang.WRITE_IN_CHAT_NEW_ROLE_NAME.get());
                player.closeInventory();
                PlayerChatListenerStorage.addPlayer(RANK_CREATION,player);
            }
            else
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());


        });


        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenTownMemberList(player)));
        gui.setItem(3,3, _createNewRole);

        gui.open(player);

    }
    public static void OpenTownMenuRoleManager(Player player, String roleName) {

        Gui gui = IGUI.createChestGui("Town",4);


        TownData town = TownDataStorage.get(player);
        TownRank townRank = town.getRank(roleName);

        boolean isDefaultRank = town.getTownDefaultRank().equals(townRank.getName());

        Material roleMaterial = Material.getMaterial(townRank.getRankIconName());

        ItemStack roleIcon = HeadUtils.getCustomLoreItem(
                roleMaterial,
                Lang.GUI_TOWN_MEMBERS_ROLE_NAME.get(townRank.getColoredName()),
                Lang.GUI_TOWN_MEMBERS_ROLE_NAME_DESC1.get());

        ItemStack roleRankIcon = townRank.getRankEnum().getRankGuiIcon();
        HeadUtils.addLore(roleRankIcon, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_DESC1.get(),
                Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_DESC2.get());

        ItemStack membersRank = HeadUtils.makeSkull(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I0M2IyMzE4OWRjZjEzMjZkYTQyNTNkMWQ3NTgyZWY1YWQyOWY2YzI3YjE3MWZlYjE3ZTMxZDA4NGUzYTdkIn19fQ==");

        ArrayList<String> playerNames = new ArrayList<>();
        playerNames.add(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO_DESC1.get());
        for (String playerUUID : townRank.getPlayers(town.getID())) {
            PlayerData playerData = PlayerDataStorage.get(playerUUID);
            assert playerData != null;
            String playerName = playerData.getName();
            playerNames.add(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO_DESC.get(playerName));
        }

        HeadUtils.setLore(membersRank, playerNames);

        ItemStack managePermission = HeadUtils.getCustomLoreItem(Material.ANVIL,Lang.GUI_TOWN_MEMBERS_ROLE_MANAGE_PERMISSION.get());

        ItemStack renameRank = HeadUtils.getCustomLoreItem(Material.NAME_TAG,Lang.GUI_TOWN_MEMBERS_ROLE_CHANGE_NAME.get());

        ItemStack changeRoleTaxRelation = HeadUtils.getCustomLoreItem(
                Material.GOLD_NUGGET,
                townRank.isPayingTaxes() ? Lang.GUI_TOWN_MEMBERS_ROLE_PAY_TAXES.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NOT_PAY_TAXES.get(),
                Lang.GUI_TOWN_MEMBERS_ROLE_TAXES_DESC1.get()
        );


        ItemStack makeRankDefault = HeadUtils.getCustomLoreItem(Material.RED_BED,
                isDefaultRank ? Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_IS_DEFAULT.get() : Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_IS_NOT_DEFAULT.get(),
                Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT1.get(),
                isDefaultRank ? "" : Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT2.get());


        ItemStack removeRank = HeadUtils.getCustomLoreItem(Material.BARRIER, Lang.GUI_TOWN_MEMBERS_ROLE_DELETE.get());

        ItemStack salary = HeadUtils.getCustomLoreItem(Material.GOLD_INGOT,
                Lang.GUI_TOWN_MEMBERS_ROLE_SALARY.get(),
                Lang.GUI_TOWN_MEMBERS_ROLE_SALARY_DESC1.get(townRank.getSalary()));

        ItemStack lowerSalary = HeadUtils.makeSkull(Lang.GUI_TREASURY_LOWER_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=");
        ItemStack increaseSalary = HeadUtils.makeSkull(Lang.GUI_TREASURY_INCREASE_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
        HeadUtils.setLore(lowerSalary,
                Lang.GUI_DECREASE_1_DESC.get(),
                Lang.GUI_DECREASE_10_DESC.get()
        );
        HeadUtils.setLore(increaseSalary,
                Lang.GUI_INCREASE_1_DESC.get(),
                Lang.GUI_INCREASE_10_DESC.get()
        );


        GuiItem _roleIcon = ItemBuilder.from(roleIcon).asGuiItem(event -> {
            event.getCursor();
            Material itemMaterial = event.getCursor().getData().getItemType();
            if(itemMaterial == Material.AIR || itemMaterial == Material.LEGACY_AIR){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_NO_ITEM_SHOWED.get());
            }
            else {
                townRank.setRankIconName(town.getID(), itemMaterial.toString());
                OpenTownMenuRoleManager(player, roleName);
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_CHANGED_ICON_SUCCESS.get());
            }
            event.setCancelled(true);
        });

        GuiItem _roleRankIcon = ItemBuilder.from(roleRankIcon).asGuiItem(event -> {
            townRank.incrementLevel(town.getID());
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
            player.sendMessage(getTANString() + Lang.WRITE_IN_CHAT_NEW_ROLE_NAME.get());

            HashMap<MessageKey, String> newMap = new HashMap<>();
            newMap.put(RANK_NAME,roleName);
            PlayerChatListenerStorage.addPlayer(RANK_RENAME,player,newMap);
            event.setCancelled(true);
        });
        GuiItem _changeRoleTaxRelation = ItemBuilder.from(changeRoleTaxRelation).asGuiItem(event -> {
            townRank.swapPayingTaxes(town.getID());
            OpenTownMenuRoleManager(player,roleName);
            event.setCancelled(true);
        });
        GuiItem _makeRankDefault = ItemBuilder.from(makeRankDefault).asGuiItem(event -> {
            if(isDefaultRank){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_ALREADY_DEFAULT.get());
            }
            else{
                town.setTownDefaultRank(roleName);
            }
            event.setCancelled(true);
            OpenTownMenuRoleManager(player,roleName);
        });

        GuiItem _removeRank = ItemBuilder.from(removeRank).asGuiItem(event -> {
            if(townRank.getNumberOfPlayer(town.getID()) != 0){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_DELETE_ERROR_NOT_EMPTY.get());
                event.setCancelled(true);
            }
            else{
                town.removeRank(townRank.getName());
                OpenTownMenuRoles(player);
                event.setCancelled(true);
            }
        });

        GuiItem _lowerSalary = ItemBuilder.from(lowerSalary).asGuiItem(event -> {
            event.setCancelled(true);

            int currentSalary = townRank.getSalary();
            int amountToRemove = event.isShiftClick() && currentSalary >= 10 ? 10 : 1;

            if (currentSalary <= 0) {
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_SALARY_ERROR_LOWER.get());
                return;
            }

            townRank.removeFromSalary(town.getID(), amountToRemove);
            SoundUtil.playSound(player, REMOVE);
            OpenTownMenuRoleManager(player, roleName);
        });
        GuiItem _IncreaseSalary = ItemBuilder.from(increaseSalary).asGuiItem(event -> {

            event.setCancelled(true);

            int amountToAdd = event.isShiftClick() ? 10 : 1;

            townRank.addFromSalary(town.getID(), amountToAdd);
            SoundUtil.playSound(player, ADD);
            OpenTownMenuRoleManager(player, roleName);
        });

        GuiItem _salary = ItemBuilder.from(salary).asGuiItem(event -> {
            event.setCancelled(true);
        });

        gui.setItem(1,5, _roleIcon);

        gui.setItem(2,2, _roleRankIcon);
        gui.setItem(2,3, _membersRank);
        gui.setItem(2,4, _managePermission);
        gui.setItem(3,2, _renameRank);
        gui.setItem(3,3, _changeRoleTaxRelation);
        gui.setItem(3,4, _makeRankDefault);
        gui.setItem(3,6, _removeRank);

        gui.setItem(2,6, _lowerSalary);
        gui.setItem(2,7, _salary);
        gui.setItem(2,8, _IncreaseSalary);

        gui.setItem(4,1, IGUI.CreateBackArrow(player,p -> OpenTownMemberList(player)));

        gui.open(player);

    }
    public static void OpenTownMenuRoleManagerAddPlayer(Player player, String roleName) {

        Gui gui = IGUI.createChestGui("Town",3);


        TownData town = TownDataStorage.get(player);
        TownRank townRank = town.getRank(roleName);
        int i = 0;

        for (String otherPlayerUUID : town.getPlayerList()) {
            PlayerData otherPlayerData = PlayerDataStorage.get(otherPlayerUUID);
            boolean skip = false;

            for (String playerWithRoleUUID : townRank.getPlayers(town.getID())) {
                if (otherPlayerUUID.equals(playerWithRoleUUID)) {
                    skip = true;
                    break;
                }
            }
            if (skip) {
                continue;
            }

            ItemStack playerHead = HeadUtils.getPlayerHead(PlayerDataStorage.get(otherPlayerUUID).getName(),
                    Bukkit.getOfflinePlayer(UUID.fromString(otherPlayerUUID)));

            GuiItem _playerHead = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);

                if(town.getRank(player).getLevel() >= town.getRank(otherPlayerData).getLevel() && !town.isLeader(player)){
                    player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get());
                    return;
                }

                PlayerData playerStat = PlayerDataStorage.get(otherPlayerUUID);
                town.getRank(playerStat.getTownRankID()).removePlayer(otherPlayerUUID);
                playerStat.setRank(roleName);
                townRank.addPlayer(otherPlayerUUID);

                OpenTownMenuRoleManager(player, roleName);
            });

            gui.setItem(i, _playerHead);
            i = i + 1;
        }
        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenTownMenuRoleManager(player,roleName)));

        gui.open(player);
    }
    public static void OpenTownMenuRoleManagerPermissions(Player player, String roleName) {

        Gui gui = IGUI.createChestGui("Town",3);

        TownData town = TownDataStorage.get(player);
        String townID = town.getID();
        TownRank townRank = town.getRank(roleName);


        ItemStack manage_taxes = HeadUtils.getCustomLoreItem(Material.GOLD_INGOT, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_TAXES.get(),(townRank.hasPermission(townID,MANAGE_TAXES)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack promote_rank_player = HeadUtils.getCustomLoreItem(Material.EMERALD, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_PROMOTE_RANK_PLAYER.get(),(townRank.hasPermission(townID,PROMOTE_RANK_PLAYER)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack derank_player = HeadUtils.getCustomLoreItem(Material.REDSTONE, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_DERANK_RANK_PLAYER.get(),(townRank.hasPermission(townID,DERANK_RANK_PLAYER)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack claim_chunk = HeadUtils.getCustomLoreItem(Material.EMERALD_BLOCK, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_CLAIM_CHUNK.get(),(townRank.hasPermission(townID,CLAIM_CHUNK)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack unclaim_chunk = HeadUtils.getCustomLoreItem(Material.REDSTONE_BLOCK, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_UNCLAIM_CHUNK.get(),(townRank.hasPermission(townID,UNCLAIM_CHUNK)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack upgrade_town = HeadUtils.getCustomLoreItem(Material.SPECTRAL_ARROW, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_UPGRADE_TOWN.get(),(townRank.hasPermission(townID,UPGRADE_TOWN)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack invite_player = HeadUtils.getCustomLoreItem(Material.SKELETON_SKULL, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_INVITE_PLAYER.get(),(townRank.hasPermission(townID,INVITE_PLAYER)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack kick_player = HeadUtils.getCustomLoreItem(Material.CREEPER_HEAD, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_KICK_PLAYER.get(),(townRank.hasPermission(townID,KICK_PLAYER)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack create_rank = HeadUtils.getCustomLoreItem(Material.LADDER, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_CREATE_RANK.get(),(townRank.hasPermission(townID,CREATE_RANK)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack delete_rank = HeadUtils.getCustomLoreItem(Material.CHAIN, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_DELETE_RANK.get(),(townRank.hasPermission(townID,DELETE_RANK)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack modify_rank = HeadUtils.getCustomLoreItem(Material.STONE_PICKAXE, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MODIFY_RANK.get(),(townRank.hasPermission(townID,MANAGE_RANKS)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack manage_claim_settings = HeadUtils.getCustomLoreItem(Material.GRASS_BLOCK, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_CLAIM_SETTINGS.get(),(townRank.hasPermission(townID,MANAGE_CLAIM_SETTINGS)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack manage_town_relation = HeadUtils.getCustomLoreItem(Material.FLOWER_POT, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_TOWN_RELATION.get(),(townRank.hasPermission(townID,MANAGE_TOWN_RELATION)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack manage_mob_spawn = HeadUtils.getCustomLoreItem(Material.CREEPER_HEAD, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_MOB_SPAWN.get(),(townRank.hasPermission(townID,MANAGE_MOB_SPAWN)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());

        GuiItem _manage_taxes = ItemBuilder.from(manage_taxes).asGuiItem(event -> {
            townRank.switchPermission(town.getID(), MANAGE_TAXES);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _promote_rank_player = ItemBuilder.from(promote_rank_player).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),PROMOTE_RANK_PLAYER);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _derank_player = ItemBuilder.from(derank_player).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),DERANK_RANK_PLAYER);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _claim_chunk = ItemBuilder.from(claim_chunk).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),CLAIM_CHUNK);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _unclaim_chunk = ItemBuilder.from(unclaim_chunk).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),UNCLAIM_CHUNK);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _upgrade_town = ItemBuilder.from(upgrade_town).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),UPGRADE_TOWN);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _invite_player = ItemBuilder.from(invite_player).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),INVITE_PLAYER);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _kick_player = ItemBuilder.from(kick_player).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),KICK_PLAYER);
            OpenTownMenuRoleManagerPermissions(player, roleName);

            event.setCancelled(true);
        });
        GuiItem _create_rank = ItemBuilder.from(create_rank).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),CREATE_RANK);
            OpenTownMenuRoleManagerPermissions(player, roleName);

            event.setCancelled(true);
        });
        GuiItem _delete_rank = ItemBuilder.from(delete_rank).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),DELETE_RANK);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _modify_rank = ItemBuilder.from(modify_rank).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),MANAGE_RANKS);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _manage_claim_settings = ItemBuilder.from(manage_claim_settings).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),MANAGE_CLAIM_SETTINGS);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _manage_town_relation = ItemBuilder.from(manage_town_relation).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),MANAGE_TOWN_RELATION);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });
        GuiItem _manage_mob_spawn = ItemBuilder.from(manage_mob_spawn).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),MANAGE_MOB_SPAWN);
            OpenTownMenuRoleManagerPermissions(player, roleName);
            event.setCancelled(true);
        });


        gui.setItem(1, _manage_taxes);
        gui.setItem(2, _promote_rank_player);
        gui.setItem(3, _derank_player);
        gui.setItem(4, _claim_chunk);
        gui.setItem(5, _unclaim_chunk);
        gui.setItem(6, _upgrade_town);
        gui.setItem(7, _invite_player);
        gui.setItem(8, _kick_player);
        gui.setItem(9, _create_rank);
        gui.setItem(10, _delete_rank);
        gui.setItem(11, _modify_rank);
        gui.setItem(12, _manage_claim_settings);
        gui.setItem(13, _manage_town_relation);
        gui.setItem(14, _manage_mob_spawn);

        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenTownMenuRoleManager(player,roleName)));

        gui.open(player);

    }
    public static void OpenTownEconomics(Player player) {

        Gui gui = IGUI.createChestGui("Town",4);


        TownData town = TownDataStorage.get(player);
        PlayerData playerStat = PlayerDataStorage.get(player);


        ItemStack goldIcon = HeadUtils.makeSkull(Lang.GUI_TREASURY_STORAGE.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        ItemStack goldSpendingIcon = HeadUtils.makeSkull(Lang.GUI_TREASURY_SPENDING.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        ItemStack lowerTax = HeadUtils.makeSkull(Lang.GUI_TREASURY_LOWER_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=");
        ItemStack increaseTax = HeadUtils.makeSkull(Lang.GUI_TREASURY_INCREASE_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
        ItemStack taxInfo = HeadUtils.makeSkull(Lang.GUI_TREASURY_FLAT_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTk4ZGY0MmY0NzdmMjEzZmY1ZTlkN2ZhNWE0Y2M0YTY5ZjIwZDljZWYyYjkwYzRhZTRmMjliZDE3Mjg3YjUifX19");
        ItemStack taxHistory = HeadUtils.makeSkull(Lang.GUI_TREASURY_TAX_HISTORY.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmU1OWYyZDNiOWU3ZmI5NTBlOGVkNzkyYmU0OTIwZmI3YTdhOWI5MzQ1NjllNDQ1YjJiMzUwM2ZlM2FiOTAyIn19fQ==");
        ItemStack salarySpending = HeadUtils.makeSkull(Lang.GUI_TREASURY_SALARY_HISTORY.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjlhNjAwYWIwYTgzMDk3MDY1Yjk1YWUyODRmODA1OTk2MTc3NDYwOWFkYjNkYmQzYTRjYTI2OWQ0NDQwOTU1MSJ9fX0=");
        ItemStack chunkSpending = HeadUtils.makeSkull(Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        ItemStack miscSpending = HeadUtils.makeSkull(Lang.GUI_TREASURY_MISCELLANEOUS_SPENDING.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGMzNjA0NTIwOGY5YjVkZGNmOGM0NDMzZTQyNGIxY2ExN2I5NGY2Yjk2MjAyZmIxZTUyNzBlZThkNTM4ODFiMSJ9fX0=");
        ItemStack donation = HeadUtils.getCustomLoreItem(Material.DIAMOND,Lang.GUI_TREASURY_DONATION.get(),Lang.GUI_TOWN_TREASURY_DONATION_DESC1.get());
        ItemStack donationHistory = HeadUtils.getCustomLoreItem(Material.PAPER,Lang.GUI_TREASURY_DONATION_HISTORY.get());


        int nextTaxes = 0;

        for (String playerID : town.getPlayerList()){
            PlayerData otherPlayerData = PlayerDataStorage.get(playerID);
            OfflinePlayer otherPlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));
            if(!otherPlayerData.getTownRank().isPayingTaxes()){
                continue;
            }
            if(EconomyUtil.getBalance(otherPlayer) < town.getFlatTax()){
                continue;
            }
            nextTaxes = nextTaxes + town.getFlatTax();

        }

        // Chunk upkeep
        int numberClaimedChunk = town.getNumberOfClaimedChunk();
        float upkeepCost = ConfigUtil.getCustomConfig("config.yml").getInt("ChunkUpkeepCost");
        float totalUpkeep = numberClaimedChunk * upkeepCost/10;
        //total salary
        int totalSalary = 0;
        for (TownRank rank : town.getTownRanks()) {

            List<String> playerIdList = rank.getPlayers(town.getID());
            totalSalary += playerIdList.size() * rank.getSalary();
        }

        HeadUtils.setLore(goldIcon,
                Lang.GUI_TREASURY_STORAGE_DESC1.get(town.getBalance()),
                Lang.GUI_TREASURY_STORAGE_DESC2.get(nextTaxes));
        HeadUtils.setLore(goldSpendingIcon,
                Lang.GUI_TREASURY_SPENDING_DESC1.get(totalSalary + totalUpkeep),
                Lang.GUI_TREASURY_SPENDING_DESC2.get(totalSalary),
                Lang.GUI_TREASURY_SPENDING_DESC3.get(totalUpkeep));
        HeadUtils.setLore(lowerTax,
                Lang.GUI_DECREASE_1_DESC.get(),
                Lang.GUI_DECREASE_10_DESC.get());
        HeadUtils.setLore(taxInfo,
                Lang.GUI_TREASURY_FLAT_TAX_DESC1.get(town.getFlatTax()));
        HeadUtils.setLore(increaseTax,
                Lang.GUI_INCREASE_1_DESC.get(),
                Lang.GUI_INCREASE_10_DESC.get());
        HeadUtils.setLore(salarySpending,
                Lang.GUI_TREASURY_SALARY_HISTORY_DESC1.get(totalSalary));
        HeadUtils.setLore(chunkSpending,
                Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC1.get(totalUpkeep),
                Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC2.get(upkeepCost),
                Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC3.get(numberClaimedChunk));
        HeadUtils.setLore(miscSpending,
                Lang.GUI_TREASURY_MISCELLANEOUS_SPENDING_DESC1.get());

        if(!isSqlEnable()){
            HeadUtils.setLore(donationHistory, town.getDonationHistory().get(5));
            HeadUtils.setLore(miscSpending, town.getMiscellaneousHistory().get(5));
            HeadUtils.setLore(taxHistory, town.getTaxHistory().get(5));
        }
        HeadUtils.addLore(taxHistory,Lang.GUI_TREASURY_TAX_HISTORY_DESC1.get());


        GuiItem _goldInfo = ItemBuilder.from(goldIcon).asGuiItem(event -> event.setCancelled(true));
        GuiItem _goldSpendingIcon = ItemBuilder.from(goldSpendingIcon).asGuiItem(event -> event.setCancelled(true));
        GuiItem _taxHistory = ItemBuilder.from(taxHistory).asGuiItem(event -> {
            if(!isSqlEnable())
                OpenTownEconomicsHistory(player,HistoryEnum.TAX);
            event.setCancelled(true);
        });
        GuiItem _salarySpending = ItemBuilder.from(salarySpending).asGuiItem(event -> {
            if(!isSqlEnable())
                OpenTownEconomicsHistory(player,HistoryEnum.SALARY);
            event.setCancelled(true);
        });
        GuiItem _chunkSpending = ItemBuilder.from(chunkSpending).asGuiItem(event -> {
            if(!isSqlEnable())
                OpenTownEconomicsHistory(player,HistoryEnum.CHUNK);
            event.setCancelled(true);
        });
        GuiItem _miscSpending = ItemBuilder.from(miscSpending).asGuiItem(event -> {
            if(!isSqlEnable())
                OpenTownEconomicsHistory(player,HistoryEnum.MISCELLANEOUS);
            event.setCancelled(true);
        });
        GuiItem _donation = ItemBuilder.from(donation).asGuiItem(event -> {
            player.sendMessage(getTANString() + Lang.WRITE_IN_CHAT_AMOUNT_OF_MONEY_FOR_DONATION.get());
            PlayerChatListenerStorage.addPlayer(TOWN_DONATION,player);
            player.closeInventory();
            event.setCancelled(true);
        });
        GuiItem _donationHistory = ItemBuilder.from(donationHistory).asGuiItem(event -> {
            if(!isSqlEnable())
                OpenTownEconomicsHistory(player,HistoryEnum.DONATION);
            event.setCancelled(true);
        });

        GuiItem _lowerTax = ItemBuilder.from(lowerTax).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.hasPermission(MANAGE_TAXES)) {
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }

            int currentTax = town.getFlatTax();
            int amountToRemove = event.isShiftClick() && currentTax >= 10 ? 10 : 1;

            if(currentTax <= 1){
                player.sendMessage(getTANString() + Lang.GUI_TREASURY_CANT_TAX_LESS.get());
                return;
            }
            SoundUtil.playSound(player, REMOVE);

            town.addToFlatTax(-amountToRemove);
            OpenTownEconomics(player);
        });
        GuiItem _taxInfo = ItemBuilder.from(taxInfo).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownEconomics(player);
        });
        GuiItem _moreTax = ItemBuilder.from(increaseTax).asGuiItem(event -> {
            event.setCancelled(true);

            if(!playerStat.hasPermission(MANAGE_TAXES)){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }

            int amountToAdd = event.isShiftClick() ? 10 : 1;

            town.addToFlatTax(amountToAdd);
            SoundUtil.playSound(player, ADD);
            OpenTownEconomics(player);
        });

        GuiItem _decorativeGlass = ItemBuilder.from(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE)).asGuiItem(event -> event.setCancelled(true));



        gui.setItem(1,1, _decorativeGlass);
        gui.setItem(1,2, _decorativeGlass);
        gui.setItem(1,3, _decorativeGlass);
        gui.setItem(1,5, _decorativeGlass);
        gui.setItem(1,7, _decorativeGlass);
        gui.setItem(1,8, _decorativeGlass);
        gui.setItem(1,9, _decorativeGlass);

        gui.setItem(1,4, _goldInfo);
        gui.setItem(1,6, _goldSpendingIcon);

        gui.setItem(2,2, _lowerTax);
        gui.setItem(2,3, _taxInfo);
        gui.setItem(2,4, _moreTax);

        gui.setItem(2,6, _salarySpending);
        gui.setItem(2,7, _chunkSpending);
        gui.setItem(2,8, _miscSpending);

        gui.setItem(3,2, _donation);
        gui.setItem(3,3, _donationHistory);
        gui.setItem(3,4, _taxHistory);



        gui.setItem(4,1, IGUI.CreateBackArrow(player,p -> OpenTownMenuHaveTown(player)));

        gui.open(player);

    }
    public static void OpenTownEconomicsHistory(Player player, HistoryEnum historyType) {

        Gui gui = IGUI.createChestGui("Town",6);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData town = playerStat.getTown();


        switch (historyType){

            case DONATION -> {

                int i = 0;
                for(TransactionHistory donation : town.getDonationHistory().getReverse()){

                    ItemStack transactionIcon = HeadUtils.getCustomLoreItem(Material.PAPER,
                            ChatColor.DARK_AQUA + donation.getName(),
                            Lang.DONATION_SINGLE_LINE_1.get(donation.getAmount()),
                            Lang.DONATION_SINGLE_LINE_2.get(donation.getDate())
                    );

                    GuiItem _transactionIcon = ItemBuilder.from(transactionIcon).asGuiItem(event -> event.setCancelled(true));

                    gui.setItem(i,_transactionIcon);
                    i = i + 1;
                    if (i > 44){
                        break;
                    }
                }
            }
            case TAX -> {

                int i = 0;
                for(Map.Entry<String,ArrayList<TransactionHistory>> oneDay : town.getTaxHistory().get().entrySet()){

                    String date = oneDay.getKey();
                    ArrayList<TransactionHistory> taxes = oneDay.getValue();


                    List<String> lines = new ArrayList<>();

                    for (TransactionHistory singleTax : taxes){

                        if(singleTax.getAmount() == -1){
                            lines.add(Lang.TAX_SINGLE_LINE_NOT_ENOUGH.get(singleTax.getName()));
                        }
                        else{
                            lines.add(Lang.TAX_SINGLE_LINE.get(singleTax.getName(), singleTax.getAmount()));
                        }
                    }

                    ItemStack transactionHistoryItem = HeadUtils.getCustomLoreItem(Material.PAPER,date);

                    HeadUtils.setLore(transactionHistoryItem,lines);

                    GuiItem _transactionHistoryItem = ItemBuilder.from(transactionHistoryItem).asGuiItem(event -> event.setCancelled(true));

                    gui.setItem(i,_transactionHistoryItem);
                    i = i+1;
                    if (i > 44){
                        break;
                    }
                }

            }
            case CHUNK  -> {

                int i = 0;

                float upkeepCost = ConfigUtil.getCustomConfig("config.yml").getInt("ChunkUpkeepCost");

                for(TransactionHistory chunkTax : town.getChunkHistory().get().values()){


                    ItemStack transactionIcon = HeadUtils.getCustomLoreItem(Material.PAPER,
                            ChatColor.DARK_AQUA + chunkTax.getDate(),
                            Lang.CHUNK_HISTORY_DESC1.get(chunkTax.getAmount()),
                            Lang.CHUNK_HISTORY_DESC2.get(chunkTax.getName(), String.format("%.2f", upkeepCost/10),chunkTax.getAmount())

                    );

                    GuiItem _transactionIcon = ItemBuilder.from(transactionIcon).asGuiItem(event -> event.setCancelled(true));

                    gui.setItem(i,_transactionIcon);
                    i = i + 1;

                    if (i > 44){
                        break;
                    }
                }

            }
            case SALARY -> {

                int i = 0;
                for(Map.Entry<String,ArrayList<TransactionHistory>> oneDay : town.getSalaryHistory().get().entrySet()){

                    String date = oneDay.getKey();
                    ArrayList<TransactionHistory> salaries = oneDay.getValue();

                    List<String> lines = new ArrayList<>();

                    for (TransactionHistory singleSalary : salaries){
                        if(singleSalary.getAmount() < 0){
                            lines.add(Lang.HISTORY_NEGATIVE_SINGLE_LINE.get(singleSalary.getPlayerName(), singleSalary.getAmount()));
                        }
                    }

                    ItemStack transactionHistoryItem = HeadUtils.getCustomLoreItem(Material.PAPER,date);

                    HeadUtils.setLore(transactionHistoryItem,lines);

                    GuiItem _transactionHistoryItem = ItemBuilder.from(transactionHistoryItem).asGuiItem(event -> event.setCancelled(true));

                    gui.setItem(i,_transactionHistoryItem);
                    i = i+1;
                    if (i > 44){
                        break;
                    }
                }
            }
            case MISCELLANEOUS -> {
                int i = 0;

                for (TransactionHistory miscellaneous : town.getMiscellaneousHistory().get()){

                    ItemStack transactionIcon = HeadUtils.getCustomLoreItem(Material.PAPER,
                            ChatColor.DARK_AQUA + miscellaneous.getDate(),
                            Lang.MISCELLANEOUS_HISTORY_DESC1.get(miscellaneous.getName()),
                            Lang.MISCELLANEOUS_HISTORY_DESC2.get(miscellaneous.getAmount())
                    );

                    GuiItem _transactionIcon = ItemBuilder.from(transactionIcon).asGuiItem(event -> event.setCancelled(true));

                    gui.setItem(i,_transactionIcon);
                    i = i + 1;

                    if (i > 44){
                        break;
                    }
                }
            }

        }

        gui.setItem(6,1, IGUI.CreateBackArrow(player,p -> OpenTownEconomics(player)));
        gui.open(player);

    }
    public static void OpenTownLevel(Player player){
        Gui gui = IGUI.createChestGui("Town",6);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData townData = TownDataStorage.get(player);
        TownLevel townLevel = townData.getTownLevel();

        ItemStack whitePanel = HeadUtils.getCustomLoreItem(Material.WHITE_STAINED_GLASS_PANE,"");
        ItemStack iron_bars = HeadUtils.getCustomLoreItem(Material.IRON_BARS,"Level locked");

        GuiItem _TownIcon = GuiUtil.townUpgradeResume(townData);

        GuiItem _whitePanel = ItemBuilder.from(whitePanel).asGuiItem(event -> event.setCancelled(true));
        GuiItem _iron_bars = ItemBuilder.from(iron_bars).asGuiItem(event -> event.setCancelled(true));
        ItemStack green_level = HeadUtils.getCustomLoreItem(Material.GREEN_STAINED_GLASS_PANE,"");

        gui.setItem(1,1,_TownIcon);
        gui.setItem(2,1,_whitePanel);
        gui.setItem(3,1,_whitePanel);
        gui.setItem(4,1,_whitePanel);
        gui.setItem(5,1,_whitePanel);
        gui.setItem(6,2,_whitePanel);
        gui.setItem(6,3,_whitePanel);
        gui.setItem(6,4,_whitePanel);
        gui.setItem(6,5,_whitePanel);
        gui.setItem(6,6,_whitePanel);
        gui.setItem(6,9,_whitePanel);

        GuiItem _pannel;
        GuiItem _bottompannel;

        for(int i = 2; i < 10; i++){
            if(townLevel.getTownLevel() > (i-2)){
                ItemStack filler_green = HeadUtils.getCustomLoreItem(Material.LIME_STAINED_GLASS_PANE,"Level " + (i-1));

                _pannel = ItemBuilder.from(green_level).asGuiItem(event -> event.setCancelled(true));
                _bottompannel = ItemBuilder.from(filler_green).asGuiItem(event -> event.setCancelled(true));
            }
            else if(townLevel.getTownLevel() == i-2){
                _pannel = _iron_bars;
                ItemStack upgradeTownLevel = HeadUtils.getCustomLoreItem(Material.ORANGE_STAINED_GLASS_PANE, Lang.GUI_TOWN_LEVEL_UP.get());
                HeadUtils.setLore(upgradeTownLevel,
                        Lang.GUI_TOWN_LEVEL_UP_DESC1.get(townLevel.getTownLevel()),
                        Lang.GUI_TOWN_LEVEL_UP_DESC2.get(townLevel.getTownLevel()+1, townLevel.getMoneyRequiredTownLevel())
                );
                _bottompannel = ItemBuilder.from(upgradeTownLevel).asGuiItem(event -> {
                    event.setCancelled(true);
                    upgradeTown(player,townData);
                    OpenTownLevel(player);
                });
            }
            else{
                _pannel = _iron_bars;
                ItemStack red_level = HeadUtils.getCustomLoreItem(Material.RED_STAINED_GLASS_PANE,"Town level " + (i-2) + " locked");
                _bottompannel = ItemBuilder.from(red_level).asGuiItem(event -> event.setCancelled(true));
            }
            gui.setItem(1,i, _pannel);
            gui.setItem(2,i, _pannel);
            gui.setItem(3,i, _pannel);
            gui.setItem(4,i, _pannel);
            gui.setItem(5,i, _bottompannel);
        }

        for(TownUpgrade townUpgrade : UpgradeStorage.getUpgrades()){
            GuiItem _guiItem = GuiUtil.makeUpgradeGuiItem(player,townUpgrade,townData);
            gui.setItem(townUpgrade.getRow(),townUpgrade.getCol() + 1,_guiItem);
        }

        gui.setItem(6,1, IGUI.CreateBackArrow(player,p -> OpenTownMenuHaveTown(player)));

        gui.open(player);

    }
    public static void OpenTownSettings(Player player) {

        Gui gui = IGUI.createChestGui("Town",4);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(player);
        int changeTownNameCost = ConfigUtil.getCustomConfig("config.yml").getInt("ChangeTownNameCost");


        ItemStack TownIcon = HeadUtils.getTownIcon(playerStat.getTownId());
        ItemStack leaveTown = HeadUtils.getCustomLoreItem(Material.BARRIER,
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN.get(),
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC1.get(playerTown.getName()),
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC2.get());
        ItemStack deleteTown = HeadUtils.getCustomLoreItem(Material.BARRIER,
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN.get(),
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC1.get(playerTown.getName()),
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC2.get());
        ItemStack changeOwnershipTown = HeadUtils.getCustomLoreItem(Material.BEEHIVE,
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP.get(),
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC1.get(),
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC2.get());
        ItemStack changeMessage = HeadUtils.getCustomLoreItem(Material.WRITABLE_BOOK,
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_MESSAGE.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_MESSAGE_DESC1.get(playerTown.getDescription()));
        ItemStack toggleApplication = HeadUtils.getCustomLoreItem(Material.PAPER,
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION.get(),
                (playerTown.isRecruiting() ? Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_ACCEPT.get() : Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_NOT_ACCEPT.get()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_CLICK_TO_SWITCH.get());
        ItemStack changeTownName = HeadUtils.getCustomLoreItem(Material.NAME_TAG,
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME_DESC1.get(playerTown.getName()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME_DESC2.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME_DESC3.get(changeTownNameCost));
        ItemStack quitRegion = HeadUtils.getCustomLoreItem(Material.SPRUCE_DOOR,
                Lang.GUI_TOWN_SETTINGS_QUIT_REGION.get(),
                playerTown.haveRegion() ? Lang.GUI_TOWN_SETTINGS_QUIT_REGION_DESC1_REGION.get() : Lang.GUI_TOWN_SETTINGS_QUIT_REGION_DESC1_NO_REGION.get());
        ItemStack changeChunkColor = HeadUtils.getCustomLoreItem(Material.PURPLE_WOOL,
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC1.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC2.get(getHexColor(playerTown.getChunkColorInHex()) + playerTown.getChunkColorInHex()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC3.get());

        GuiItem _townIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> event.setCancelled(true));

        GuiItem _leaveTown = ItemBuilder.from(leaveTown).asGuiItem(event -> {
            event.setCancelled(true);
            if (playerStat.isTownLeader()) {
                SoundUtil.playSound(player, NOT_ALLOWED);
                player.sendMessage(getTANString() + Lang.CHAT_CANT_LEAVE_TOWN_IF_LEADER.get());
            } else {
                playerTown.removePlayer(player);
                playerTown.getRank(playerStat.getTownRankID()).removePlayer(playerStat.getUuid());

                player.sendMessage(getTANString() + Lang.CHAT_PLAYER_LEFT_THE_TOWN.get());
                playerStat.leaveTown();
                playerTown.broadCastMessageWithSound(Lang.TOWN_BROADCAST_PLAYER_LEAVE_THE_TOWN.get(playerStat.getName()),
                        BAD);
                player.closeInventory();
            }
        });
        GuiItem _deleteTown = ItemBuilder.from(deleteTown).asGuiItem(event -> {
            event.setCancelled(true);
            if (!playerStat.isTownLeader()) {
                player.sendMessage(getTANString() + Lang.CHAT_CANT_DISBAND_TOWN_IF_NOT_LEADER.get());
                return;
            }
            deleteTown(playerTown);

            player.closeInventory();
            SoundUtil.playSound(player,GOOD);
            player.sendMessage(getTANString() + Lang.CHAT_PLAYER_TOWN_SUCCESSFULLY_DELETED.get());
        });

        GuiItem _changeOwnershipTown = ItemBuilder.from(changeOwnershipTown).asGuiItem(event -> {

            event.setCancelled(true);

            if(playerStat.isTownLeader())
                OpenTownChangeOwnershipPlayerSelect(player, playerTown);
            else
                player.sendMessage(getTANString() + Lang.NOT_TOWN_LEADER_ERROR.get());

        });

        GuiItem _changeMessage = ItemBuilder.from(changeMessage).asGuiItem(event -> {
            player.closeInventory();
            player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            Map<MessageKey, String> data = new HashMap<>();
            data.put(MessageKey.TOWN_ID,playerTown.getID());
            PlayerChatListenerStorage.addPlayer(CHANGE_TOWN_DESCRIPTION,player,data);
            event.setCancelled(true);
        });

        GuiItem _toggleApplication = ItemBuilder.from(toggleApplication).asGuiItem(event -> {
            playerTown.swapRecruiting();
            OpenTownSettings(player);
            event.setCancelled(true);
        });

        GuiItem _changeTownName = ItemBuilder.from(changeTownName).asGuiItem(event -> {
            event.setCancelled(true);

            if(playerTown.getBalance() < changeTownNameCost){
                player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get());
                return;
            }

            if(playerStat.isTownLeader()){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
                Map<MessageKey, String> data = new HashMap<>();
                data.put(MessageKey.TOWN_ID,playerTown.getID());
                data.put(MessageKey.COST,Integer.toString(changeTownNameCost));
                PlayerChatListenerStorage.addPlayer(CHANGE_TOWN_NAME,player,data);
                player.closeInventory();
            }
            else
                player.sendMessage(getTANString() + Lang.NOT_TOWN_LEADER_ERROR.get());
        });

        GuiItem _quitRegion = ItemBuilder.from(quitRegion).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerTown.haveRegion())
                player.sendMessage(getTANString() + Lang.TOWN_NO_REGION.get());

            RegionData regionData = playerTown.getRegion();

            if(playerTown.isRegionalCapital())
                player.sendMessage(getTANString() + Lang.NOT_TOWN_LEADER_ERROR.get());

            regionData.removeTown(playerTown);
            playerTown.removeRegion();
            player.closeInventory();
        });

        GuiItem _changeChunkColor = ItemBuilder.from(changeChunkColor).asGuiItem(event -> {
            event.setCancelled(true);

            if(playerStat.isTownLeader()){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT.get());
                Map<MessageKey, String> data = new HashMap<>();
                data.put(MessageKey.TOWN_ID,playerTown.getID());
                PlayerChatListenerStorage.addPlayer(CHANGE_CHUNK_COLOR,player,data);
                player.closeInventory();
            }
            else
                player.sendMessage(getTANString() + Lang.NOT_TOWN_LEADER_ERROR.get());
        });




        gui.setItem(4, _townIcon);
        gui.setItem(2,2, _leaveTown);
        gui.setItem(2,3, _deleteTown);
        gui.setItem(2,4, _changeOwnershipTown);
        gui.setItem(2,6, _changeMessage);
        gui.setItem(2,7, _toggleApplication);
        gui.setItem(2,8, _changeTownName);

        gui.setItem(3,2, _quitRegion);
        if(isDynmapAddonLoaded())
            gui.setItem(3,8, _changeChunkColor);

        gui.setItem(4,1, IGUI.CreateBackArrow(player,p -> OpenTownMenuHaveTown(player)));

        gui.open(player);
    }
    public static void OpenTownChangeOwnershipPlayerSelect(Player player, TownData townData) {

        Gui gui = IGUI.createChestGui("Town",3);

        int i = 0;
        for (String playerUUID : townData.getPlayerList()){
            OfflinePlayer townPlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerUUID));

            ItemStack playerHead = HeadUtils.getPlayerHead(townPlayer.getName(),townPlayer);
            HeadUtils.setLore(playerHead,
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC1.get(player.getName()),
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC2.get()
            );

            GuiItem _playerHead = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);

                townData.setLeaderID(townPlayer.getUniqueId().toString());
                player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_SUCCESS.get(townPlayer.getName()));
                OpenTownMenuHaveTown(player);
            });

            gui.setItem(i, _playerHead);

            i = i+1;
        }
        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenTownSettings(player)));
        gui.open(player);
    }
    public static void OpenTownRelations(Player player) {

        Gui gui = IGUI.createChestGui("Town",3);


        ItemStack warCategory = HeadUtils.getCustomLoreItem(Material.IRON_SWORD,
                Lang.GUI_TOWN_RELATION_WAR.get(),
                Lang.GUI_TOWN_RELATION_WAR_DESC1.get());
        ItemStack EmbargoCategory = HeadUtils.getCustomLoreItem(Material.BARRIER,
                Lang.GUI_TOWN_RELATION_EMBARGO.get(),
                Lang.GUI_TOWN_RELATION_EMBARGO_DESC1.get());
        ItemStack NAPCategory = HeadUtils.getCustomLoreItem(Material.WRITABLE_BOOK,
                Lang.GUI_TOWN_RELATION_NAP.get(),
                Lang.GUI_TOWN_RELATION_NAP_DESC1.get());
        ItemStack AllianceCategory = HeadUtils.getCustomLoreItem(Material.CAMPFIRE,
                Lang.GUI_TOWN_RELATION_ALLIANCE.get(),
                Lang.GUI_TOWN_RELATION_ALLIANCE_DESC1.get());

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

        GuiItem _decorativeGlass = ItemBuilder.from(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).asGuiItem(event -> event.setCancelled(true));
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

        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenTownMenuHaveTown(player)));

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
        Gui gui = IGUI.createChestGui("Town - Relation",4);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(playerStat);

        ArrayList<String> TownListUUID = playerTown.getTownWithRelation(relation);
        int i = 0;
        for(String otherTownUUID : TownListUUID){
            ItemStack townIcon = getTownIconWithInformations(otherTownUUID);

            if(relation == TownRelation.WAR) {
                ItemMeta meta = townIcon.getItemMeta();
                assert meta != null;
                List<String> lore = meta.getLore();
                assert lore != null;
                lore.add(Lang.GUI_TOWN_ATTACK_TOWN_DESC1.get());
                lore.add(Lang.GUI_TOWN_ATTACK_TOWN_DESC2.get());
                meta.setLore(lore);
                townIcon.setItemMeta(meta);
            }

            GuiItem _town = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);

                if(relation == TownRelation.WAR){
                    player.sendMessage(getTANString() + Lang.GUI_TOWN_ATTACK_TOWN_EXECUTED.get(TownDataStorage.get(otherTownUUID).getName()));
                    WarTaggedPlayer.addPlayersToTown(otherTownUUID,playerTown.getPlayerList());
                    TownDataStorage.get(otherTownUUID).broadCastMessageWithSound(Lang.GUI_TOWN_ATTACK_TOWN_INFO.get(playerTown.getName()),
                            WAR);
                }
            });
            gui.setItem(i, _town);

            i = i+1;
        }



        ItemStack addTownButton = HeadUtils.makeSkull(
                Lang.GUI_TOWN_RELATION_ADD_TOWN.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19"
        );
        ItemStack removeTownButton = HeadUtils.makeSkull(
                Lang.GUI_TOWN_RELATION_REMOVE_TOWN.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0="
        );

        ItemStack nextPageButton = HeadUtils.makeSkull(
                Lang.GUI_NEXT_PAGE.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2MjYyYWYxZDVmNDE0YzU5NzA1NWMyMmUzOWNjZTE0OGU1ZWRiZWM0NTU1OWEyZDZiODhjOGQ2N2I5MmVhNiJ9fX0="
        );

        ItemStack previousPageButton = HeadUtils.makeSkull(
                Lang.GUI_PREVIOUS_PAGE.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQyZmRlOGI4MmU4YzFiOGMyMmIyMjY3OTk4M2ZlMzVjYjc2YTc5Nzc4NDI5YmRhZGFiYzM5N2ZkMTUwNjEifX19"
        );
        GuiItem _add = ItemBuilder.from(addTownButton).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.hasPermission(TownRolePermission.MANAGE_TOWN_RELATION)){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }
            OpenTownRelationModification(player,Action.ADD,relation);
        });
        GuiItem _remove = ItemBuilder.from(removeTownButton).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.hasPermission(TownRolePermission.MANAGE_TOWN_RELATION)){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }
            OpenTownRelationModification(player,Action.REMOVE,relation);
        });
        GuiItem _next = ItemBuilder.from(nextPageButton).asGuiItem(event -> event.setCancelled(true));
        GuiItem _previous = ItemBuilder.from(previousPageButton).asGuiItem(event -> event.setCancelled(true));
        GuiItem _decorativeGlass = ItemBuilder.from(new ItemStack(Material.WHITE_STAINED_GLASS_PANE)).asGuiItem(event -> {
            event.setCancelled(true);
        });

        gui.setItem(4,1, IGUI.CreateBackArrow(player,p -> OpenTownRelations(player)));
        gui.setItem(4,4,_add);
        gui.setItem(4,5,_remove);

        gui.setItem(4,7,_previous);
        gui.setItem(4,8,_next);


        gui.setItem(4,2, _decorativeGlass);
        gui.setItem(4,3, _decorativeGlass);
        gui.setItem(4,6, _decorativeGlass);
        gui.setItem(4,9, _decorativeGlass);

        gui.open(player);
    }
    public static void OpenTownRelationModification(Player player, Action action, TownRelation relation) {
        Gui gui = IGUI.createChestGui("Town - Relation",4);

        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        TownData playerTown = TownDataStorage.get(playerStat);

        LinkedHashMap<String, TownData> allTown = getTownMap();
        ArrayList<String> TownListUUID = playerTown.getTownWithRelation(relation);

        GuiItem _decorativeGlass = ItemBuilder.from(new ItemStack(Material.GREEN_STAINED_GLASS_PANE)).asGuiItem(event -> {
            event.setCancelled(true);
        });

        if(action == Action.ADD){
            List<String> townNoRelation = new ArrayList<>(allTown.keySet());
            townNoRelation.removeAll(TownListUUID);
            townNoRelation.remove(playerTown);
            int i = 0;
            for(String otherTownUUID : townNoRelation){
                TownData otherTown = TownDataStorage.get(otherTownUUID);
                ItemStack townIcon = getTownIconWithInformations(otherTownUUID, playerTown.getID());

                GuiItem _town = ItemBuilder.from(townIcon).asGuiItem(event -> {
                    event.setCancelled(true);

                    if(HaveRelation(playerTown, otherTown)){
                        player.sendMessage(getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_ALREADY_HAVE_RELATION.get());
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    if(relation.getNeedsConfirmationToStart()){
                        // Can only be good relations
                        OfflinePlayer otherTownLeader = Bukkit.getOfflinePlayer(UUID.fromString(otherTown.getLeaderID()));

                        if (!otherTownLeader.isOnline()) {
                            player.sendMessage(getTANString() + Lang.LEADER_NOT_ONLINE.get());
                            return;
                        }
                        Player otherTownLeaderOnline = otherTownLeader.getPlayer();

                        TownRelationConfirmStorage.addInvitation(otherTown.getLeaderID(), playerTown.getID(), relation);

                        otherTownLeaderOnline.sendMessage(getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_RECEIVED_1.get(playerTown.getName(),relation.getColor() + relation.getName()));
                        ChatUtils.sendClickableCommand(otherTownLeaderOnline,getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_RECEIVED_2.get(),"tan accept "  + playerTown.getID());

                        player.sendMessage(getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_SENT_SUCCESS.get(otherTownLeaderOnline.getName()));

                        player.closeInventory();
                    }
                    else{ //Can only be bad relations
                        playerTown.broadCastMessageWithSound(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.get(otherTown.getName(),relation.getColoredName()),
                                BAD);
                        otherTown.broadCastMessageWithSound(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.get(playerTown.getName(),relation.getColoredName()),
                                BAD);
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
                ItemStack townIcon = getTownIconWithInformations(otherTownUUID);
                GuiItem _town = ItemBuilder.from(townIcon).asGuiItem(event -> {
                    event.setCancelled(true);

                    if(relation.getNeedsConfirmationToEnd()){ //Can only be better relations
                        player.sendMessage(getTANString() + "Sent to the leader of the other town");

                        Player otherTownLeader = Bukkit.getPlayer(UUID.fromString(otherTown.getLeaderID()));

                        TownRelationConfirmStorage.addInvitation(otherTown.getLeaderID(), playerTown.getID(), null);

                        otherTownLeader.sendMessage(getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_RECEIVED_1.get(playerTown.getName(),"neutral"));
                        ChatUtils.sendClickableCommand(otherTownLeader,getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_RECEIVED_2.get(),"tan accept "  + playerTown.getID());
                        player.closeInventory();
                    }
                    else{ //Can only be worst relations
                        playerTown.broadCastMessageWithSound(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.get(otherTown.getName(),"neutral"),
                                BAD);
                        otherTown.broadCastMessageWithSound(getTANString() + Lang.GUI_TOWN_CHANGED_RELATION_RESUME.get(playerTown.getName(),"neutral"),
                                BAD);
                        removeRelation(playerTown,otherTown,relation);
                        OpenTownRelation(player,relation);
                    }
                    OpenTownRelation(player,relation);
                });
                gui.setItem(i, _town);
                i = i+1;
            }
            _decorativeGlass = ItemBuilder.from(new ItemStack(Material.RED_STAINED_GLASS_PANE)).asGuiItem(event -> event.setCancelled(true));
        }


        ItemStack nextPageButton = HeadUtils.makeSkull(
                Lang.GUI_NEXT_PAGE.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2MjYyYWYxZDVmNDE0YzU5NzA1NWMyMmUzOWNjZTE0OGU1ZWRiZWM0NTU1OWEyZDZiODhjOGQ2N2I5MmVhNiJ9fX0="
        );
        ItemStack previousPageButton = HeadUtils.makeSkull(
                Lang.GUI_PREVIOUS_PAGE.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQyZmRlOGI4MmU4YzFiOGMyMmIyMjY3OTk4M2ZlMzVjYjc2YTc5Nzc4NDI5YmRhZGFiYzM5N2ZkMTUwNjEifX19"
        );

        GuiItem _next = ItemBuilder.from(nextPageButton).asGuiItem(event -> event.setCancelled(true));
        GuiItem _previous = ItemBuilder.from(previousPageButton).asGuiItem(event -> event.setCancelled(true));

        gui.setItem(4,1, IGUI.CreateBackArrow(player,p -> OpenTownRelation(player,relation)));

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
    public static void OpenTownChunk(Player player) {
        Gui gui = IGUI.createChestGui("Town",3);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(player);

        ItemStack playerChunkIcon = HeadUtils.getCustomLoreItem(Material.PLAYER_HEAD,
                Lang.GUI_TOWN_CHUNK_PLAYER.get(),
                Lang.GUI_TOWN_CHUNK_PLAYER_DESC1.get()
                );

        ItemStack mobChunckIcon = HeadUtils.getCustomLoreItem(Material.CREEPER_HEAD,
                Lang.GUI_TOWN_CHUNK_MOB.get(),
                Lang.GUI_TOWN_CHUNK_MOB_DESC1.get()
        );

        GuiItem _playerChunkIcon = ItemBuilder.from(playerChunkIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownChunkPlayerSettings(player);
        });

        GuiItem _mobChunckIcon = ItemBuilder.from(mobChunckIcon).asGuiItem(event -> {
            event.setCancelled(true);

            if(playerTown.getTownLevel().getBenefitsLevel("UNLOCK_MOB_BAN") >= 1)
                OpenTownChunkMobSettings(player);
            else{
                player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_LEVEL.get(DynamicLang.get("UNLOCK_MOB_BAN")));
                SoundUtil.playSound(player, NOT_ALLOWED);
            }
        });

        gui.setItem(2,4, _playerChunkIcon);
        gui.setItem(2,6, _mobChunckIcon);


        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenTownMenuHaveTown(player)));

        gui.open(player);
    }
    public static void OpenTownChunkMobSettings(Player player){
        Gui gui = IGUI.createChestGui("Town",4);

        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        TownData townData = TownDataStorage.get(player);
        ClaimedChunkSettings chunkSettings = townData.getChunkSettings();


        int i = 0;
        for (MobChunkSpawnEnum mobEnum : MobChunkSpawnStorage.getMobSpawnStorage().values()){
            ItemStack mobIcon = HeadUtils.makeSkull(mobEnum.name(),mobEnum.getTexture());

            UpgradeStatus upgradeStatus = chunkSettings.getSpawnControl(mobEnum);

            List<String> status = new ArrayList<>();
            int cost = getMobSpawnCost(mobEnum);
            if(upgradeStatus.isUnlocked()){
                if(upgradeStatus.isActivated()){
                    status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_ACTIVATED.get());
                }
                else{
                    status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_DEACTIVATED.get());
                }
            }
            else{

                status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_LOCKED.get());
                status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_LOCKED2.get(cost));
            }

            HeadUtils.setLore(mobIcon,status);

            GuiItem mobItem = new GuiItem(mobIcon, event -> {
                event.setCancelled(true);
                if(!playerStat.hasPermission(TownRolePermission.MANAGE_MOB_SPAWN)){
                    player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                    return;
                }
                if(upgradeStatus.isUnlocked()){
                    if(upgradeStatus.isActivated())
                        upgradeStatus.setActivated(false);
                    else
                        upgradeStatus.setActivated(true);
                    SoundUtil.playSound(player, ADD);
                }
                else{
                    if(townData.getBalance() < cost){
                        player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get());
                        return;
                    }
                    townData.removeToBalance(cost);
                    SoundUtil.playSound(player,GOOD);
                    upgradeStatus.setUnlocked(true);
                }

                OpenTownChunkMobSettings(player);

            });
            gui.setItem(i, mobItem);
            i = i+1;
        }

        gui.setItem(27, IGUI.CreateBackArrow(player,p -> OpenTownChunk(player)));
        gui.open(player);
    }
    public static void OpenTownChunkPlayerSettings(Player player){
        Gui gui = IGUI.createChestGui("Town",4);

        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        TownData townData = TownDataStorage.get(player);



        Object[][] itemData = {
                {ChunkPermissionType.DOOR, Material.OAK_DOOR, Lang.GUI_TOWN_CLAIM_SETTINGS_DOOR},
                {ChunkPermissionType.CHEST, Material.CHEST, Lang.GUI_TOWN_CLAIM_SETTINGS_CHEST},
                {ChunkPermissionType.PLACE, Material.BRICKS, Lang.GUI_TOWN_CLAIM_SETTINGS_BUILD},
                {ChunkPermissionType.BREAK, Material.IRON_PICKAXE, Lang.GUI_TOWN_CLAIM_SETTINGS_BREAK},
                {ChunkPermissionType.ATTACK_PASSIVE_MOB, Material.BEEF, Lang.GUI_TOWN_CLAIM_SETTINGS_ATTACK_PASSIVE_MOBS},
                {ChunkPermissionType.USE_BUTTONS, Material.STONE_BUTTON, Lang.GUI_TOWN_CLAIM_SETTINGS_BUTTON},
                {ChunkPermissionType.USE_REDSTONE, Material.REDSTONE, Lang.GUI_TOWN_CLAIM_SETTINGS_REDSTONE},
                {ChunkPermissionType.USE_FURNACE, Material.FURNACE, Lang.GUI_TOWN_CLAIM_SETTINGS_FURNACE},
                {ChunkPermissionType.INTERACT_ITEM_FRAME, Material.ITEM_FRAME, Lang.GUI_TOWN_CLAIM_SETTINGS_INTERACT_ITEM_FRAME},
                {ChunkPermissionType.INTERACT_ARMOR_STAND, Material.ARMOR_STAND, Lang.GUI_TOWN_CLAIM_SETTINGS_INTERACT_ARMOR_STAND},
                {ChunkPermissionType.DECORATIVE_BLOCK, Material.CAULDRON, Lang.GUI_TOWN_CLAIM_SETTINGS_DECORATIVE_BLOCK},
                {ChunkPermissionType.MUSIC_BLOCK, Material.JUKEBOX, Lang.GUI_TOWN_CLAIM_SETTINGS_MUSIC_BLOCK},
                {ChunkPermissionType.LEAD, Material.LEAD, Lang.GUI_TOWN_CLAIM_SETTINGS_LEAD},
                {ChunkPermissionType.SHEARS, Material.SHEARS, Lang.GUI_TOWN_CLAIM_SETTINGS_SHEARS},
        };

        for (int i = 0; i < itemData.length; i++) {
            ChunkPermissionType type = (ChunkPermissionType) itemData[i][0];
            Material material = (Material) itemData[i][1];
            Lang label = (Lang) itemData[i][2];

            TownChunkPermission permission = townData.getPermission(type);
            ItemStack itemStack = HeadUtils.getCustomLoreItem(
                    material,
                    label.get(),
                    Lang.GUI_TOWN_CLAIM_SETTINGS_DESC1.get(permission.getColoredName()),
                    Lang.GUI_LEFT_CLICK_TO_INTERACT.get()
            );

            GuiItem guiItem = createGuiItem(itemStack, playerStat, player, v -> townData.nextPermission(type));
            gui.setItem(i, guiItem);
        }

        gui.setItem(27, IGUI.CreateBackArrow(player,p -> OpenTownChunk(player)));

        gui.open(player);
    }


    public static void OpenNoRegionMenu(Player player){

        Gui gui = IGUI.createChestGui("Region",3);


        int regionCost = ConfigUtil.getCustomConfig("config.yml").getInt("regionCost");

        ItemStack createRegion = HeadUtils.getCustomLoreItem(Material.STONE_BRICKS,
                Lang.GUI_REGION_CREATE.get(),
                Lang.GUI_REGION_CREATE_DESC1.get(regionCost),
                Lang.GUI_REGION_CREATE_DESC2.get()
        );

        ItemStack browseRegion = HeadUtils.getCustomLoreItem(Material.BOOK,
                Lang.GUI_REGION_BROWSE.get(),
                Lang.GUI_REGION_BROWSE_DESC1.get(RegionDataStorage.getNumberOfRegion()),
                Lang.GUI_REGION_BROWSE_DESC2.get()
        );

        GuiItem _createRegion = ItemBuilder.from(createRegion).asGuiItem(event -> {
            event.setCancelled(true);
            RegionUtil.registerNewRegion(player, regionCost);
        });

        GuiItem _browseRegion = ItemBuilder.from(browseRegion).asGuiItem(event -> {
            event.setCancelled(true);
            OpenRegionList(player, false);
        });

        gui.setItem(2,4, _createRegion);
        gui.setItem(2,6, _browseRegion);
        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenMainMenu(player)));

        gui.open(player);
    }
    private static void OpenRegionMenu(Player player) {

        Gui gui = IGUI.createChestGui("Region",3);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(playerStat);
        RegionData playerRegion = playerTown.getRegion();


        ItemStack regionIcon = getRegionIcon(playerRegion);

        ItemStack GoldIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_TREASURY_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        HeadUtils.setLore(GoldIcon, Lang.GUI_TOWN_TREASURY_ICON_DESC1.get());

        ItemStack townIcon = HeadUtils.makeSkull(Lang.GUI_REGION_TOWN_LIST.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=");
        HeadUtils.setLore(townIcon, Lang.GUI_REGION_TOWN_LIST_DESC1.get());

        ItemStack otherRegionIcon = HeadUtils.makeSkull(Lang.GUI_OTHER_REGION_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMzc0ZTIxYjgxYzBiMjFhYmViOGU5N2UxM2UwNzdkM2VkMWVkNDRmMmU5NTZjNjhmNjNhM2UxOWU4OTlmNiJ9fX0=");
        HeadUtils.setLore(otherRegionIcon, Lang.GUI_OTHER_REGION_ICON_DESC1.get());

        ItemStack RelationIcon = HeadUtils.makeSkull(Lang.GUI_RELATION_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUwN2Q2ZGU2MzE4MzhlN2E3NTcyMGU1YjM4ZWYxNGQyOTY2ZmRkODQ4NmU3NWQxZjY4MTJlZDk5YmJjYTQ5OSJ9fX0=");
        HeadUtils.setLore(RelationIcon, Lang.GUI_RELATION_ICON_DESC1.get());

        ItemStack LevelIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_LEVEL_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmJlNTI5YWI2YjJlYTdjNTBkOTE5MmQ4OWY4OThmZDdkYThhOWU3NTBkMzc4Mjk1ZGY3MzIwNWU3YTdlZWFlMCJ9fX0=");
        HeadUtils.setLore(LevelIcon, Lang.GUI_TOWN_LEVEL_ICON_DESC1.get());

        ItemStack SettingIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_SETTINGS_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVkMmNiMzg0NThkYTE3ZmI2Y2RhY2Y3ODcxNjE2MDJhMjQ5M2NiZjkzMjMzNjM2MjUzY2ZmMDdjZDg4YTljMCJ9fX0=");
        HeadUtils.setLore(SettingIcon, Lang.GUI_TOWN_SETTINGS_ICON_DESC1.get());

        GuiItem _regionIcon = ItemBuilder.from(regionIcon).asGuiItem(event -> {
            event.setCancelled(true);

            if(!playerStat.isTownLeader() && playerRegion.isCapital(playerTown))
                return;
            if(event.getCursor() == null)
                return;

            Material itemMaterial = event.getCursor().getType();
            if(itemMaterial == Material.AIR || itemMaterial == Material.LEGACY_AIR){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_NO_ITEM_SHOWED.get());
            }

            else {
                playerRegion.setRegionIconType(itemMaterial);
                OpenRegionMenu(player);
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_CHANGED_ICON_SUCCESS.get());
            }
        });
        GuiItem _goldIcon = ItemBuilder.from(GoldIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenRegionEconomy(player);
        });
        GuiItem _townIcon = ItemBuilder.from(townIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownInRegion(player);
        });

        GuiItem _otherRegionIcon = ItemBuilder.from(otherRegionIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenRegionList(player,true);
        });
        GuiItem _relationIcon = ItemBuilder.from(RelationIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });

        GuiItem _settingsIcon = ItemBuilder.from(SettingIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenRegionSettings(player);
        });


        gui.setItem(4, _regionIcon);
        gui.setItem(10, _goldIcon);
        gui.setItem(11, _townIcon);
        gui.setItem(13, _otherRegionIcon);
        gui.setItem(15, _relationIcon);
        gui.setItem(16, _settingsIcon);

        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenMainMenu(player)));

        gui.open(player);
    }
    public static void OpenRegionList(Player player, boolean isTownMenu) {

        Gui gui = IGUI.createChestGui("Region",4);

        int i = 0;
        for (RegionData regionData : RegionDataStorage.getAllRegions()){
            ItemStack regionIcon = getRegionIcon(regionData);

            GuiItem _region = ItemBuilder.from(regionIcon).asGuiItem(event -> {
                event.setCancelled(true);
            });
            gui.setItem(i, _region);
            i = i+1;
        }

        gui.setItem(27, IGUI.CreateBackArrow(player,p -> OpenNoRegionMenu(player)));
        gui.open(player);

        if(isTownMenu)
            gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenRegionMenu(player)));
        else
            gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenNoRegionMenu(player)));

    }
    private static void OpenTownInRegion(Player player){

        Gui gui = IGUI.createChestGui("Region",4);
        PlayerData playerData = PlayerDataStorage.get(player);
        RegionData regionData = RegionDataStorage.get(player);

        for (TownData townData : regionData.getTownsInRegion()){
            ItemStack townIcon = getTownIconWithInformations(townData);

            GuiItem _townIcon = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);
            });
            gui.addItem(_townIcon);
        }

        ItemStack addTown = HeadUtils.makeSkull(Lang.GUI_INVITE_TOWN_TO_REGION.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
        ItemStack removeTown = HeadUtils.makeSkull(Lang.GUI_KICK_TOWN_TO_REGION.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=");


        GuiItem _addTown = ItemBuilder.from(addTown).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerData.isTownLeader() || !regionData.isCapital(playerData.getTown())){
                player.sendMessage(getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get());
                return;
            }
            OpenRegionTownInteraction(player, Action.ADD);
        });
        GuiItem _removeTown = ItemBuilder.from(removeTown).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerData.isTownLeader() || !regionData.isCapital(playerData.getTown())){
                player.sendMessage(getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get());
                return;
            }
            OpenRegionTownInteraction(player, Action.REMOVE);
        });


        gui.setItem(4,1, IGUI.CreateBackArrow(player,p -> OpenRegionMenu(player)));
        gui.setItem(4,2, _addTown);
        gui.setItem(4,3, _removeTown);
        gui.open(player);
    }
    private static void OpenRegionTownInteraction(Player player, Action action) {

        Gui gui = IGUI.createChestGui("Region", 4);
        RegionData regionData = RegionDataStorage.get(player);

        if(action == Action.ADD) {
            for (TownData townData : TownDataStorage.getTownMap().values()) {

                if(townData.getID().equals(regionData.getID()))
                    continue;

                ItemStack townIcon = getTownIconWithInformations(townData);
                HeadUtils.addLore(townIcon, Lang.GUI_REGION_INVITE_TOWN_DESC1.get());

                GuiItem _townIcon = ItemBuilder.from(townIcon).asGuiItem(event -> {
                    event.setCancelled(true);
                    if (!townData.isLeaderOnline()) {
                        player.sendMessage(getTANString() + Lang.LEADER_NOT_ONLINE.get());
                        return;
                    }
                    Player townLeader = Bukkit.getPlayer(UUID.fromString(townData.getLeaderID()));

                    RegionInviteDataStorage.addInvitation(townData.getLeaderID(), regionData.getID());

                    townLeader.sendMessage(getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_RECEIVED_1.get(regionData.getName(), townData.getName()));
                    ChatUtils.sendClickableCommand(townLeader, getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_RECEIVED_2.get(), "tan acceptregion " + regionData.getID());

                    player.sendMessage(getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_SENT_SUCCESS.get(townLeader.getName(), regionData.getName()));
                    player.closeInventory();
                });
                gui.addItem(_townIcon);
            }
        }
        else if (action == Action.REMOVE){
            for (TownData townData : regionData.getTownsInRegion()){
                ItemStack townIcon = getTownIconWithInformations(townData);
                HeadUtils.addLore(townIcon, Lang.GUI_REGION_INVITE_TOWN_DESC1.get());

                GuiItem _townIcon = ItemBuilder.from(townIcon).asGuiItem(event -> {
                    event.setCancelled(true);
                    if (!townData.isLeaderOnline()) {
                        player.sendMessage(getTANString() + Lang.LEADER_NOT_ONLINE.get());
                        return;
                    }
                    townData.removeRegion();
                    regionData.removeTown(townData);

                    player.closeInventory();
                });
                gui.addItem(_townIcon);
            }
        }


        gui.setItem(4,1, IGUI.CreateBackArrow(player,p -> OpenTownInRegion(player)));
        gui.open(player);
    }
    private static void OpenRegionSettings(Player player) {

        Gui gui = IGUI.createChestGui("Region", 3);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(playerStat);
        RegionData playerRegion = playerTown.getRegion();


        ItemStack regionIcon = getRegionIcon(playerRegion);

        ItemStack deleteRegion = HeadUtils.getCustomLoreItem(Material.BARRIER,
                Lang.GUI_REGION_DELETE.get(),
                Lang.GUI_REGION_DELETE_DESC1.get(playerRegion.getName()),
                Lang.GUI_REGION_DELETE_DESC2.get(),
                Lang.GUI_REGION_DELETE_DESC3.get()
        );

        ItemStack changeCapital = HeadUtils.getCustomLoreItem(Material.GOLDEN_HELMET,
                Lang.GUI_REGION_CHANGE_CAPITAL.get(),
                Lang.GUI_REGION_CHANGE_CAPITAL_DESC1.get(playerRegion.getCapital().getName()),
                Lang.GUI_REGION_CHANGE_CAPITAL_DESC2.get()
        );

        ItemStack changeDescription = HeadUtils.getCustomLoreItem(Material.WRITABLE_BOOK,
                Lang.GUI_REGION_CHANGE_DESCRIPTION.get(),
                Lang.GUI_REGION_CHANGE_DESCRIPTION_DESC1.get(playerRegion.getDescription()),
                Lang.GUI_REGION_CHANGE_DESCRIPTION_DESC2.get()
        );

        GuiItem _regionIcon = ItemBuilder.from(regionIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });

        GuiItem _deleteRegion = ItemBuilder.from(deleteRegion).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.isTownLeader() && playerRegion.isCapital(playerTown)){
                player.sendMessage(getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get());
            }
            RegionDataStorage.deleteRegion(playerRegion.getID());
            SoundUtil.playSound(player, BAD);
            player.sendMessage(getTANString() + Lang.CHAT_PLAYER_REGION_SUCCESSFULLY_DELETED.get());
            OpenMainMenu(player);
        });

        GuiItem _changeCapital = ItemBuilder.from(changeCapital).asGuiItem(event -> {
            event.setCancelled(true);
            if(playerStat.isTownLeader() && playerRegion.isCapital(playerTown)){
                OpenRegionalCapitalSwitch(player);
                return;
            }
            player.sendMessage(getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get());
        });

        GuiItem _changeDescription = ItemBuilder.from(changeDescription).asGuiItem(event -> {
            event.setCancelled(true);
            if(playerStat.isTownLeader() && playerRegion.isCapital(playerTown)){
                player.closeInventory();
                player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
                Map<MessageKey, String> data = new HashMap<>();
                data.put(MessageKey.REGION_ID,playerRegion.getID());
                PlayerChatListenerStorage.addPlayer(CHANGE_REGION_DESCRIPTION,player,data);
                return;
            }
            player.sendMessage(getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get());
        });



        gui.setItem(1,5, _regionIcon);

        gui.setItem(2,4, _deleteRegion);
        gui.setItem(2,5, _changeCapital);
        gui.setItem(2,6, _changeDescription);



        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenRegionMenu(player)));

        gui.open(player);
    }
    private static void OpenRegionEconomy(Player player) {
        Gui gui = IGUI.createChestGui("Region", 4);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = playerStat.getTown();
        RegionData playerRegion = playerTown.getRegion();

        int tax = playerRegion.getTaxRate();
        int treasury = playerRegion.getBalance();
        int taxTomorrow = playerRegion.getIncomeTomorrow();


        ItemStack goldIcon = HeadUtils.makeSkull(Lang.GUI_TREASURY_STORAGE.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        ItemStack goldSpendingIcon = HeadUtils.makeSkull(Lang.GUI_TREASURY_SPENDING.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");

        ItemStack lowerTax = HeadUtils.makeSkull(Lang.GUI_TREASURY_LOWER_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=");
        ItemStack increaseTax = HeadUtils.makeSkull(Lang.GUI_TREASURY_INCREASE_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
        ItemStack taxInfo = HeadUtils.makeSkull(Lang.GUI_TREASURY_FLAT_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTk4ZGY0MmY0NzdmMjEzZmY1ZTlkN2ZhNWE0Y2M0YTY5ZjIwZDljZWYyYjkwYzRhZTRmMjliZDE3Mjg3YjUifX19");
        ItemStack taxHistory = HeadUtils.makeSkull(Lang.GUI_TREASURY_TAX_HISTORY.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmU1OWYyZDNiOWU3ZmI5NTBlOGVkNzkyYmU0OTIwZmI3YTdhOWI5MzQ1NjllNDQ1YjJiMzUwM2ZlM2FiOTAyIn19fQ==");

        ItemStack chunkSpending = HeadUtils.makeSkull(Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        ItemStack donation = HeadUtils.getCustomLoreItem(Material.DIAMOND,
                Lang.GUI_TREASURY_DONATION.get(),
                Lang.GUI_REGION_TREASURY_DONATION_DESC1.get());
        ItemStack donationHistory = HeadUtils.getCustomLoreItem(Material.PAPER,Lang.GUI_TREASURY_DONATION_HISTORY.get());

        HeadUtils.setLore(goldIcon,
                Lang.GUI_TREASURY_STORAGE_DESC1.get(treasury),
                Lang.GUI_TREASURY_STORAGE_DESC2.get(taxTomorrow));
        HeadUtils.setLore(goldSpendingIcon,
                Lang.GUI_WARNING_STILL_IN_DEV.get());

        HeadUtils.setLore(lowerTax,
                Lang.GUI_DECREASE_1_DESC.get(),
                Lang.GUI_DECREASE_10_DESC.get());
        HeadUtils.setLore(increaseTax,
                Lang.GUI_INCREASE_1_DESC.get(),
                Lang.GUI_INCREASE_10_DESC.get());
        HeadUtils.setLore(taxInfo,
                Lang.GUI_TREASURY_FLAT_TAX_DESC1.get(tax));

        HeadUtils.setLore(donationHistory, playerRegion.getDonationHistory().get(5));
        HeadUtils.addLore(donationHistory,Lang.GUI_TREASURY_TAX_HISTORY_DESC1.get());
        HeadUtils.setLore(taxHistory, playerRegion.getTaxHistory().get(5));
        HeadUtils.addLore(taxHistory,Lang.GUI_TREASURY_TAX_HISTORY_DESC1.get());

        GuiItem _goldIcon = ItemBuilder.from(goldIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });

        GuiItem _goldSpendingIcon = ItemBuilder.from(goldSpendingIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });

        GuiItem _lowerTax = ItemBuilder.from(lowerTax).asGuiItem(event -> {
            event.setCancelled(true);
            int currentTax = playerRegion.getTaxRate();
            int amountToRemove = event.isShiftClick() && currentTax >= 10 ? 10 : 1;

            if(currentTax <= 1){
                player.sendMessage(getTANString() + Lang.GUI_TREASURY_CANT_TAX_LESS.get());
                return;
            }
            SoundUtil.playSound(player, REMOVE);

            playerRegion.addToTax(-amountToRemove);
            OpenRegionEconomy(player);
        });

        GuiItem _increaseTax = ItemBuilder.from(increaseTax).asGuiItem(event -> {
            event.setCancelled(true);
            int currentTax = playerRegion.getTaxRate();
            int amountToRemove = event.isShiftClick() && currentTax >= 10 ? 10 : 1;

            SoundUtil.playSound(player, ADD);

            playerRegion.addToTax(amountToRemove);
            OpenRegionEconomy(player);
        });

        GuiItem _taxInfo = ItemBuilder.from(taxInfo).asGuiItem(event -> {
            event.setCancelled(true);
        });

        GuiItem _chunkSpending = ItemBuilder.from(chunkSpending).asGuiItem(event -> {
            event.setCancelled(true);
        });

        GuiItem _donation = ItemBuilder.from(donation).asGuiItem(event -> {
            player.sendMessage(getTANString() + Lang.WRITE_IN_CHAT_AMOUNT_OF_MONEY_FOR_DONATION.get());
            PlayerChatListenerStorage.addPlayer(REGION_DONATION,player);
            player.closeInventory();
            event.setCancelled(true);
        });

        GuiItem _donationHistory = ItemBuilder.from(donationHistory).asGuiItem(event -> {
            event.setCancelled(true);
            OpenRegionEconomyHistory(player, HistoryEnum.DONATION);
        });

        GuiItem _taxHistory = ItemBuilder.from(taxHistory).asGuiItem(event -> {
            event.setCancelled(true);
            OpenRegionEconomyHistory(player, HistoryEnum.TAX);
        });

        GuiItem _decorativeGlass = ItemBuilder.from(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE)).asGuiItem(event -> event.setCancelled(true));
        gui.setItem(1,1, _decorativeGlass);
        gui.setItem(1,2, _decorativeGlass);
        gui.setItem(1,3, _decorativeGlass);
        gui.setItem(1,5, _decorativeGlass);
        gui.setItem(1,7, _decorativeGlass);
        gui.setItem(1,8, _decorativeGlass);
        gui.setItem(1,9, _decorativeGlass);


        gui.setItem(1,4, _goldIcon);
        gui.setItem(1,6, _goldSpendingIcon);
        gui.setItem(2,2, _lowerTax);
        gui.setItem(2,3, _taxInfo);
        gui.setItem(2,4, _increaseTax);

        gui.setItem(3,2, _donation);
        gui.setItem(3,3, _donationHistory);
        gui.setItem(3,4, _taxHistory);
        gui.setItem(4,1, IGUI.CreateBackArrow(player,p -> OpenRegionMenu(player)));

        gui.open(player);
    }
    public static void OpenRegionEconomyHistory(Player player, HistoryEnum historyType) {

        Gui gui = IGUI.createChestGui("Town", 6);

        PlayerData playerStat = PlayerDataStorage.get(player);
        RegionData region = playerStat.getRegion();


        switch (historyType) {

            case DONATION -> {

                int i = 0;
                for (TransactionHistory donation : region.getDonationHistory().getReverse()) {

                    ItemStack transactionIcon = HeadUtils.getCustomLoreItem(Material.PAPER,
                            ChatColor.DARK_AQUA + donation.getName(),
                            Lang.DONATION_SINGLE_LINE_1.get(donation.getAmount()),
                            Lang.DONATION_SINGLE_LINE_2.get(donation.getDate())
                    );

                    GuiItem _transactionIcon = ItemBuilder.from(transactionIcon).asGuiItem(event -> event.setCancelled(true));

                    gui.setItem(i, _transactionIcon);
                    i = i + 1;
                    if (i > 44) {
                        break;
                    }
                }
            }
            case TAX -> {

                int i = 0;
                for (Map.Entry<String, ArrayList<TransactionHistory>> oneDay : region.getTaxHistory().get().entrySet()) {

                    String date = oneDay.getKey();
                    ArrayList<TransactionHistory> taxes = oneDay.getValue();


                    List<String> lines = new ArrayList<>();

                    for (TransactionHistory singleTax : taxes) {

                        if (singleTax.getAmount() == -1) {
                            lines.add(Lang.TAX_SINGLE_LINE_NOT_ENOUGH.get(singleTax.getName()));
                        } else {
                            lines.add(Lang.TAX_SINGLE_LINE.get(singleTax.getName(), singleTax.getAmount()));
                        }
                    }

                    ItemStack transactionHistoryItem = HeadUtils.getCustomLoreItem(Material.PAPER, date);
                    HeadUtils.setLore(transactionHistoryItem, lines);
                    GuiItem _transactionHistoryItem = ItemBuilder.from(transactionHistoryItem).asGuiItem(event -> event.setCancelled(true));

                    gui.setItem(i, _transactionHistoryItem);
                    i = i + 1;
                    if (i > 44) {
                        break;
                    }
                }
            }
        }
        gui.setItem(6,1, IGUI.CreateBackArrow(player,p -> OpenRegionEconomy(player)));
        gui.open(player);
    }
    public static void OpenRegionalCapitalSwitch(Player player){

            Gui gui = IGUI.createChestGui("Region", 3);
            PlayerData playerData = PlayerDataStorage.get(player);
            RegionData regionData = playerData.getRegion();

            for (TownData townData : regionData.getTownsInRegion() ){

                if(townData.getID() == regionData.getCapital().getID())
                    continue;
                ItemStack regionIcon = getRegionIcon(regionData);

                GuiItem _region = ItemBuilder.from(regionIcon).asGuiItem(event -> {
                    event.setCancelled(true);
                    regionData.setCapital(townData);
                    SoundUtil.playSound(player, GOOD);
                    player.sendMessage(getTANString() + Lang.GUI_REGION_SETTINGS_REGION_CHANGE_OWNERSHIP_SUCCESS.get());
                    OpenRegionMenu(player);
                });
                gui.addItem(_region);
            }


            gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenRegionSettings(player)));
            gui.open(player);
    }
    
    private static GuiItem createGuiItem(ItemStack itemStack, PlayerData playerStat, Player player, Consumer<Void> action) {
        return ItemBuilder.from(itemStack).asGuiItem(event -> {
            event.setCancelled(true);
            if (!playerStat.hasPermission(TownRolePermission.MANAGE_CLAIM_SETTINGS)) {
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }
            action.accept(null);
            OpenTownChunkPlayerSettings(player);
        });
    }
}
