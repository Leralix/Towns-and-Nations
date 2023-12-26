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
import org.tan.TownsAndNations.enums.*;
import org.tan.TownsAndNations.storage.*;
import org.tan.TownsAndNations.utils.*;

import static org.tan.TownsAndNations.enums.MessageKey.RANK_NAME;
import static org.tan.TownsAndNations.enums.MessageKey.TOWN_COST;
import static org.tan.TownsAndNations.storage.PlayerChatListenerStorage.ChatCategory.RANK_CREATION;
import static org.tan.TownsAndNations.storage.TownDataStorage.getTownList;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;
import static org.tan.TownsAndNations.utils.RelationUtil.*;
import static org.tan.TownsAndNations.utils.TownUtil.deleteTown;

import java.util.ArrayList;


import java.util.*;
import java.util.function.Consumer;

public class GuiManager2 {

    //done
    public static void OpenMainMenu(Player player){

        PlayerData playerStat = PlayerDataStorage.get(player);
        boolean playerHaveTown = playerStat.getTownId() != null;

        Gui gui = createChestGui("Main menu",3);

        ItemStack KingdomHead = HeadUtils.makeSkull(Lang.GUI_KINGDOM_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY5MTk2YjMzMGM2Yjg5NjJmMjNhZDU2MjdmYjZlY2NlNDcyZWFmNWM5ZDQ0Zjc5MWY2NzA5YzdkMGY0ZGVjZSJ9fX0=");
        ItemStack RegionHead = HeadUtils.makeSkull(Lang.GUI_REGION_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljMTgzMmU0ZWY1YzRhZDljNTE5ZDE5NGIxOTg1MDMwZDI1NzkxNDMzNGFhZjI3NDVjOWRmZDYxMWQ2ZDYxZCJ9fX0=");
        ItemStack TownHead = HeadUtils.makeSkull(Lang.GUI_TOWN_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=");
        ItemStack PlayerHead = HeadUtils.getPlayerHeadInformation(player);

        HeadUtils.setLore(KingdomHead, Lang.GUI_KINGDOM_ICON_DESC1.getTranslation());
        HeadUtils.setLore(RegionHead, Lang.GUI_REGION_ICON_DESC1.getTranslation());
        HeadUtils.setLore(TownHead, playerHaveTown? Lang.GUI_KINGDOM_ICON_DESC1_HAVE_TOWN.getTranslation(TownDataStorage.get(playerStat).getName()):Lang.GUI_KINGDOM_ICON_DESC1_NO_TOWN.getTranslation() );


        GuiItem Kingdom = ItemBuilder.from(KingdomHead).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(getTANString() + Lang.GUI_WARNING_STILL_IN_DEV.getTranslation());
        });
        GuiItem Region = ItemBuilder.from(RegionHead).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(getTANString() + Lang.GUI_WARNING_STILL_IN_DEV.getTranslation());
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
        gui.setItem(18,CreateBackArrow(player, p -> player.closeInventory()));

        gui.open(player);
    }
    public static void OpenProfileMenu(Player player){

        Gui gui = createChestGui("Profile",3);


        ItemStack PlayerHead = HeadUtils.getPlayerHead(Lang.GUI_YOUR_PROFILE.getTranslation(),player);
        ItemStack GoldPurse = HeadUtils.getCustomLoreItem(Material.GOLD_NUGGET, Lang.GUI_YOUR_BALANCE.getTranslation(),Lang.GUI_YOUR_BALANCE_DESC1.getTranslation(EconomyUtil.getBalance(player)));
        ItemStack killList = HeadUtils.getCustomLoreItem(Material.IRON_SWORD, Lang.GUI_YOUR_PVE_KILLS.getTranslation(),Lang.GUI_YOUR_PVE_KILLS_DESC1.getTranslation(player.getStatistic(Statistic.MOB_KILLS)));
        int time = player.getStatistic(Statistic.PLAY_ONE_MINUTE) /20 / 86400;
        ItemStack lastDeath = HeadUtils.getCustomLoreItem(Material.SKELETON_SKULL, Lang.GUI_YOUR_CURRENT_TIME_ALIVE.getTranslation(),Lang.GUI_YOUR_CURRENT_TIME_ALIVE_DESC1.getTranslation(time));
        ItemStack totalRpKills = HeadUtils.getCustomLoreItem(Material.SKELETON_SKULL, Lang.GUI_YOUR_CURRENT_MURDER.getTranslation(),Lang.GUI_YOUR_CURRENT_MURDER_DESC1.getTranslation("0"));

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
        gui.setItem(18, CreateBackArrow(player,p -> OpenMainMenu(player)));

        gui.open(player);
    }
    public static void OpenTownMenuNoTown(Player player){

        Gui gui = createChestGui("Town",3);

        int townPrice = ConfigUtil.getCustomConfig("config.yml").getInt("CostOfCreatingTown");

        ItemStack createNewLand = HeadUtils.getCustomLoreItem(Material.GRASS_BLOCK,
                Lang.GUI_NO_TOWN_CREATE_NEW_TOWN.getTranslation(),
                Lang.GUI_NO_TOWN_CREATE_NEW_TOWN_DESC1.getTranslation(townPrice)
        );
        ItemStack joinLand = HeadUtils.getCustomLoreItem(Material.ANVIL, Lang.GUI_NO_TOWN_JOIN_A_TOWN.getTranslation(),Lang.GUI_NO_TOWN_JOIN_A_TOWN_DESC1.getTranslation(TownDataStorage.getNumberOfTown()));

        GuiItem _create = ItemBuilder.from(createNewLand).asGuiItem(event -> {
            event.setCancelled(true);

            int playerMoney = EconomyUtil.getBalance(player);
            if (playerMoney < townPrice) {
                player.sendMessage(getTANString() + Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.getTranslation(townPrice - playerMoney));
            }
            else {
                player.sendMessage(getTANString() + Lang.PLAYER_WRITE_TOWN_NAME_IN_CHAT.getTranslation());
                player.closeInventory();

                Map<MessageKey,String> data = new HashMap<>();
                data.put(TOWN_COST, String.valueOf(townPrice));

                PlayerChatListenerStorage.addPlayer(PlayerChatListenerStorage.ChatCategory.CREATE_CITY,player,data);
            }
        });

        GuiItem _join = ItemBuilder.from(joinLand).asGuiItem(event -> {
            event.setCancelled(true);
            OpenSearchTownMenu(player);
        });

        gui.setItem(11, _create);
        gui.setItem(15, _join);
        gui.setItem(18, CreateBackArrow(player,p -> OpenMainMenu(player)));

        gui.open(player);
    }
    public static void OpenSearchTownMenu(Player player) {

        Gui gui = createChestGui("Town",3);

        HashMap<String, TownData> townDataStorage = getTownList();

        int i = 0;
        for (TownData townData : townDataStorage.values()) {

            ItemStack townIcon = HeadUtils.getTownIcon(townData.getID());

            HeadUtils.setLore(townIcon,
                    Lang.GUI_TOWN_INFO_DESC0.getTranslation(townData.getDescription()),
                    Lang.GUI_TOWN_INFO_DESC1.getTranslation(Bukkit.getServer().getOfflinePlayer(UUID.fromString(townData.getUuidLeader())).getName()),
                    Lang.GUI_TOWN_INFO_DESC2.getTranslation(townData.getPlayerList().size()),
                    Lang.GUI_TOWN_INFO_DESC3.getTranslation(townData.getChunkSettings().getNumberOfClaimedChunk()),
                    "",
                    (townData.isRecruiting()) ? Lang.GUI_TOWN_INFO_IS_RECRUITING.getTranslation() : Lang.GUI_TOWN_INFO_IS_NOT_RECRUITING.getTranslation(),
                    (townData.isPlayerAlreadyJoined(player)) ? Lang.GUI_TOWN_INFO_RIGHT_CLICK_TO_CANCEL.getTranslation() : Lang.GUI_TOWN_INFO_LEFT_CLICK_TO_JOIN.getTranslation()
            );

            GuiItem _townIteration = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);

                if(event.isLeftClick()){
                    if(townData.isPlayerAlreadyJoined(player)){
                       return;
                    }
                    if(!townData.isRecruiting()){
                        player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_TOWN_NOT_RECRUITING.getTranslation());
                        return;
                    }
                    townData.addPlayerJoinRequest(player);
                    player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_ASK_TO_JOIN_TOWN_PLAYER_SIDE.getTranslation(townData.getName()));
                    OpenSearchTownMenu(player);
                }

                if(event.isRightClick()){
                    if(!townData.isPlayerAlreadyJoined(player)){
                        return;
                    }
                    townData.removePlayerJoinRequest(player);
                    player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_REMOVE_ASK_TO_JOIN_TOWN_PLAYER_SIDE.getTranslation());
                    OpenSearchTownMenu(player);
                }

            });

            gui.setItem(i, _townIteration);
            i++;

        }

        gui.setItem(3,1, CreateBackArrow(player,p -> OpenMainMenu(player)));

        gui.open(player);
    }
    public static void OpenTownMenuHaveTown(Player player) {
        Gui gui = createChestGui("Town",3);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(playerStat);

        ItemStack TownIcon = HeadUtils.getTownIcon(playerTown.getID());
        HeadUtils.setLore(TownIcon,
                Lang.GUI_TOWN_INFO_DESC0.getTranslation(playerTown.getDescription()),
                Lang.GUI_TOWN_INFO_DESC1.getTranslation(Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerTown.getUuidLeader())).getName()),
                Lang.GUI_TOWN_INFO_DESC2.getTranslation(playerTown.getPlayerList().size()),
                Lang.GUI_TOWN_INFO_DESC3.getTranslation(playerTown.getChunkSettings().getNumberOfClaimedChunk()),
                Lang.GUI_TOWN_INFO_DESC4.getTranslation(playerTown.getTreasury().getBalance()),
                Lang.GUI_TOWN_INFO_CHANGE_ICON.getTranslation()
        );

        ItemStack GoldIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_TREASURY_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        HeadUtils.setLore(GoldIcon, Lang.GUI_TOWN_TREASURY_ICON_DESC1.getTranslation());

        ItemStack SkullIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_MEMBERS_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Q0ZDQ5NmIxZGEwNzUzNmM5NGMxMzEyNGE1ODMzZWJlMGM1MzgyYzhhMzM2YWFkODQ2YzY4MWEyOGQ5MzU2MyJ9fX0=");
        HeadUtils.setLore(SkullIcon, Lang.GUI_TOWN_MEMBERS_ICON_DESC1.getTranslation());

        ItemStack ClaimIcon = HeadUtils.makeSkull(Lang.GUI_CLAIM_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=");
        HeadUtils.setLore(ClaimIcon, Lang.GUI_CLAIM_ICON_DESC1.getTranslation());

        ItemStack otherTownIcon = HeadUtils.makeSkull(Lang.GUI_OTHER_TOWN_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMzc0ZTIxYjgxYzBiMjFhYmViOGU5N2UxM2UwNzdkM2VkMWVkNDRmMmU5NTZjNjhmNjNhM2UxOWU4OTlmNiJ9fX0=");
        HeadUtils.setLore(otherTownIcon, Lang.GUI_OTHER_TOWN_ICON_DESC1.getTranslation());

        ItemStack RelationIcon = HeadUtils.makeSkull(Lang.GUI_RELATION_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUwN2Q2ZGU2MzE4MzhlN2E3NTcyMGU1YjM4ZWYxNGQyOTY2ZmRkODQ4NmU3NWQxZjY4MTJlZDk5YmJjYTQ5OSJ9fX0=");
        HeadUtils.setLore(RelationIcon, Lang.GUI_RELATION_ICON_DESC1.getTranslation());

        ItemStack LevelIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_LEVEL_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmJlNTI5YWI2YjJlYTdjNTBkOTE5MmQ4OWY4OThmZDdkYThhOWU3NTBkMzc4Mjk1ZGY3MzIwNWU3YTdlZWFlMCJ9fX0=");
        HeadUtils.setLore(LevelIcon, Lang.GUI_TOWN_LEVEL_ICON_DESC1.getTranslation());

        ItemStack SettingIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_SETTINGS_ICON.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVkMmNiMzg0NThkYTE3ZmI2Y2RhY2Y3ODcxNjE2MDJhMjQ5M2NiZjkzMjMzNjM2MjUzY2ZmMDdjZDg4YTljMCJ9fX0=");
        HeadUtils.setLore(SettingIcon, Lang.GUI_TOWN_SETTINGS_ICON_DESC1.getTranslation());

        GuiItem _townIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> {
            event.setCancelled(true);

            if(!playerStat.isTownLeader())
                return;
            if(event.getCursor() == null)
                return;

            Material itemMaterial = event.getCursor().getType();
            if(itemMaterial == Material.AIR || itemMaterial == Material.LEGACY_AIR){
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


        gui.setItem(4, _townIcon);
        gui.setItem(10, _goldIcon);
        gui.setItem(11, _membersIcon);
        gui.setItem(12, _claimIcon);
        gui.setItem(13, _otherTownIcon);
        gui.setItem(14, _relationIcon);
        gui.setItem(15, _levelIcon);
        gui.setItem(16, _settingsIcon);
        gui.setItem(18, CreateBackArrow(player,p -> OpenMainMenu(player)));

        gui.open(player);
    }
    public static void OpenTownMenuOtherTown(Player player) {
        Gui gui = createChestGui("Town",3);

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

            HeadUtils.setLore(townIcon,
                    Lang.GUI_TOWN_INFO_DESC0.getTranslation(otherTown.getDescription()),
                    Lang.GUI_TOWN_INFO_DESC1.getTranslation(Bukkit.getServer().getOfflinePlayer(UUID.fromString(otherTown.getUuidLeader())).getName()),
                    Lang.GUI_TOWN_INFO_DESC2.getTranslation(otherTown.getPlayerList().size()),
                    Lang.GUI_TOWN_INFO_DESC3.getTranslation(otherTown.getChunkSettings().getNumberOfClaimedChunk()),
                    Lang.GUI_TOWN_INFO_TOWN_RELATION.getTranslation(relationName)
            );

            GuiItem _townIteration = ItemBuilder.from(townIcon).asGuiItem(event -> event.setCancelled(true));

            gui.setItem(i, _townIteration);
            i++;

        }
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());
        GuiItem _back = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuHaveTown(player);
        });
        gui.setItem(3,1, CreateBackArrow(player,p -> OpenTownMenuHaveTown(player)));

        gui.open(player);
    }
    public static void OpenTownMemberList(Player player) {

        Gui gui = createChestGui("Town",3);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData town = TownDataStorage.get(playerStat);

        HashSet<String> players = town.getPlayerList();

        int i = 0;
        for (String playerUUID: players) {

            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            PlayerData otherPlayerStat = PlayerDataStorage.get(playerUUID);
            assert otherPlayerStat != null;

            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate.getName(),playerIterate);
            HeadUtils.setLore(
                    playerHead,
                    Lang.GUI_TOWN_MEMBER_DESC1.getTranslation(otherPlayerStat.getTownRankID()),
                    Lang.GUI_TOWN_MEMBER_DESC2.getTranslation(EconomyUtil.getBalance(playerIterate)),
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
                        Objects.requireNonNull(playerIterate.getPlayer()).sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS_PLAYER.getTranslation());
                }
                OpenTownMemberList(player);
            });

            gui.setItem(i, _playerIcon);
            i++;
        }

        ItemStack manageRanks = HeadUtils.getCustomLoreItem(Material.LADDER, Lang.GUI_TOWN_MEMBERS_MANAGE_ROLES.getTranslation());
        ItemStack manageApplication = HeadUtils.getCustomLoreItem(Material.WRITABLE_BOOK,
                Lang.GUI_TOWN_MEMBERS_MANAGE_APPLICATION.getTranslation(),
                Lang.GUI_TOWN_MEMBERS_MANAGE_APPLICATION_DESC1.getTranslation(town.getPlayerJoinRequestSet().size())
        );

        GuiItem _manageRanks = ItemBuilder.from(manageRanks).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuRoles(player);
        });
        GuiItem _manageApplication = ItemBuilder.from(manageApplication).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownApplications(player);
        });

        gui.setItem(3,1, CreateBackArrow(player,p -> OpenTownMenuHaveTown(player)));
        gui.setItem(3,2, _manageRanks);
        gui.setItem(3,3, _manageApplication);


        gui.open(player);

    }
    public static void OpenTownApplications(Player player) {

        Gui gui = createChestGui("Town",3);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData town = TownDataStorage.get(playerStat);

        HashSet<String> players = town.getPlayerJoinRequestSet();

        int i = 0;
        for (String playerUUID: players) {

            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            PlayerData playerIterateData = PlayerDataStorage.get(playerUUID);
            assert playerIterateData != null;

            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate.getName(),playerIterate);

            HeadUtils.setLore(
                    playerHead,
                    Lang.GUI_PLAYER_ASK_JOIN_PROFILE_DESC2.getTranslation(),
                    Lang.GUI_PLAYER_ASK_JOIN_PROFILE_DESC3.getTranslation()
            );

            GuiItem _playerIcon = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                if(event.isLeftClick()){

                    if(!playerStat.hasPermission(TownRolePermission.INVITE_PLAYER)){
                        player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
                        return;
                    }
                    if(!town.canAddMorePlayer()){
                        player.sendMessage(getTANString() + Lang.INVITATION_ERROR_PLAYER_TOWN_FULL.getTranslation());
                        return;
                    }

                    town.addPlayer(playerIterateData.getUuid());
                    town.getRank(town.getTownDefaultRank()).addPlayer(playerIterateData.getUuid());

                    playerIterateData.setTownId(town.getID());
                    playerIterateData.setRank(town.getTownDefaultRank());

                    Player playerIterateOnline = playerIterate.getPlayer();
                    if(playerIterateOnline != null){
                        playerIterateOnline.sendMessage(getTANString() + Lang.TOWN_INVITATION_ACCEPTED_MEMBER_SIDE.getTranslation(town.getName()));
                    }
                    town.broadCastMessage(getTANString() + Lang.TOWN_INVITATION_ACCEPTED_TOWN_SIDE.getTranslation(player.getName()));

                    TeamUtils.updateColor();

                    town.removePlayerJoinRequest(playerIterateData.getUuid());

                    for (TownData allTown : TownDataStorage.getTownList().values()){
                        allTown.removePlayerJoinRequest(playerIterateData.getUuid());
                    }

                    player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_REMOVE_ASK_TO_JOIN_TOWN_PLAYER_SIDE.getTranslation());

                }
                if(event.isRightClick()){
                    if(!playerStat.hasPermission(TownRolePermission.INVITE_PLAYER)){
                        player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
                        return;
                    }

                    town.removePlayerJoinRequest(playerIterateData.getUuid());
                    player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_REMOVE_ASK_TO_JOIN_TOWN_PLAYER_SIDE.getTranslation());
                }
                OpenTownMemberList(player);
            });

            gui.setItem(i, _playerIcon);
            i++;
        }

        gui.setItem(3,1, CreateBackArrow(player,p -> OpenTownMemberList(player)));


        gui.open(player);

    }
    public static void OpenTownMenuRoles(Player player) {

        Gui gui = createChestGui("Town",3);

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
        GuiItem _createNewRole = ItemBuilder.from(createNewRole).asGuiItem(event -> {
            event.setCancelled(true);

            if(playerStat.hasPermission(TownRolePermission.CREATE_RANK)){
                if(town.getNumberOfRank() >= 8){
                    player.sendMessage(getTANString() + Lang.TOWN_RANK_CAP_REACHED.getTranslation());
                    return;
                }

                player.sendMessage(getTANString() + Lang.WRITE_IN_CHAT_NEW_ROLE_NAME.getTranslation());
                player.closeInventory();
                PlayerChatListenerStorage.addPlayer(RANK_CREATION,player);
            }
            else
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());


        });


        gui.setItem(3,1, CreateBackArrow(player,p -> OpenTownMemberList(player)));
        gui.setItem(3,3, _createNewRole);

        gui.open(player);

    }
    public static void OpenTownMenuRoleManager(Player player, String roleName) {

        Gui gui = createChestGui("Town",3);


        TownData town = TownDataStorage.get(player);
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
            assert playerData != null;
            String playerName = playerData.getName();
            playerNames.add(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO_DESC.getTranslation(playerName));
        }

        HeadUtils.setLore(membersRank, playerNames);

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


        GuiItem _roleIcon = ItemBuilder.from(roleIcon).asGuiItem(event -> {
            if(event.getCursor() == null)
                return;
            Material itemMaterial = event.getCursor().getData().getItemType();
            if(itemMaterial == Material.AIR || itemMaterial == Material.LEGACY_AIR){
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

            HashMap<MessageKey, String> newMap = new HashMap<>();
            newMap.put(RANK_NAME,roleName);
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

        gui.setItem(1,5, _roleIcon);

        gui.setItem(2,2, _roleRankIcon);
        gui.setItem(2,3, _membersRank);
        gui.setItem(2,4, _managePermission);
        gui.setItem(2,5, _renameRank);
        gui.setItem(2,6, _changeRoleTaxRelation);
        gui.setItem(2,7, _makeRankDefault);
        gui.setItem(2,8, _removeRank);

        gui.setItem(3,1, CreateBackArrow(player,p -> OpenTownMemberList(player)));

        gui.open(player);

    }
    public static void OpenTownMenuRoleManagerAddPlayer(Player player, String roleName) {

        Gui gui = createChestGui("Town",3);


        TownData town = TownDataStorage.get(player);
        TownRank townRank = town.getRank(roleName);
        int i = 0;

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
                assert playerStat != null;
                town.getRank(playerStat.getTownRankID()).removePlayer(playerUUID);
                playerStat.setRank(roleName);
                townRank.addPlayer(playerUUID);

                OpenTownMenuRoleManager(player, roleName);
            });

            gui.setItem(i, _playerHead);
            i = i + 1;
        }
        gui.setItem(3,1, CreateBackArrow(player,p -> OpenTownMenuRoleManager(player,roleName)));

        gui.open(player);
    }
    public static void OpenTownMenuRoleManagerPermissions(Player player, String roleName) {

        Gui gui = createChestGui("Town",3);

        TownData town = TownDataStorage.get(player);
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

        gui.setItem(3,1, _getBackArrow);

        gui.open(player);

    }
    public static void OpenTownEconomics(Player player) {

        Gui gui = createChestGui("Town",4);


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

        HeadUtils.setLore(taxHistory,Lang.GUI_TREASURY_TAX_HISTORY_DESC1.getTranslation());

        int nextTaxes = 0;

        for (String playerID : town.getPlayerList()){
            PlayerData otherPlayerData = PlayerDataStorage.get(playerID);assert otherPlayerData != null;
            OfflinePlayer otherPlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));
            if(!otherPlayerData.getTownRank().isPayingTaxes()){
                continue;
            }
            if(EconomyUtil.getBalance(otherPlayer) < town.getTreasury().getFlatTax()){
                continue;
            }
            nextTaxes = nextTaxes + town.getTreasury().getFlatTax();

        }


        int numberClaimedChunk = town.getChunkSettings().getNumberOfClaimedChunk();
        float upkeepCost = ConfigUtil.getCustomConfig("config.yml").getInt("ChunkUpkeepCost");
        float totalUpkeep = numberClaimedChunk * upkeepCost/10;

        HeadUtils.setLore(goldIcon,
                Lang.GUI_TREASURY_STORAGE_DESC1.getTranslation(town.getBalance()),
                Lang.GUI_TREASURY_STORAGE_DESC2.getTranslation(nextTaxes)
        );
        HeadUtils.setLore(goldSpendingIcon, Lang.GUI_TREASURY_SPENDING_DESC1.getTranslation(0), Lang.GUI_TREASURY_SPENDING_DESC2.getTranslation(0),Lang.GUI_TREASURY_SPENDING_DESC3.getTranslation(0));



        HeadUtils.setLore(lowerTax, Lang.GUI_TREASURY_LOWER_TAX_DESC1.getTranslation());
        HeadUtils.setLore(taxInfo, Lang.GUI_TREASURY_FLAT_TAX_DESC1.getTranslation(town.getTreasury().getFlatTax()));
        HeadUtils.setLore(increaseTax, Lang.GUI_TREASURY_INCREASE_TAX_DESC1.getTranslation());

        HeadUtils.setLore(salarySpending, Lang.GUI_TREASURY_SALARY_HISTORY_DESC1.getTranslation("0"));
        HeadUtils.setLore(chunkSpending,
                Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC1.getTranslation(totalUpkeep),
                Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC2.getTranslation(upkeepCost),
                Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC3.getTranslation(numberClaimedChunk));
        HeadUtils.setLore(workbenchSpending, Lang.GUI_TREASURY_MISCELLANEOUS_SPENDING_DESC1.getTranslation());

        HeadUtils.setLore(donation, Lang.GUI_TREASURY_DONATION_DESC1.getTranslation());
        HeadUtils.setLore(donationHistory, town.getTreasury().getDonationLimitedHistory(5));

        GuiItem _goldInfo = ItemBuilder.from(goldIcon).asGuiItem(event -> event.setCancelled(true));
        GuiItem _goldSpendingIcon = ItemBuilder.from(goldSpendingIcon).asGuiItem(event -> event.setCancelled(true));
        GuiItem _taxHistory = ItemBuilder.from(taxHistory).asGuiItem(event -> {
            OpenTownEconomicsHistory(player,HistoryEnum.TAX);
            event.setCancelled(true);
        });
        GuiItem _salarySpending = ItemBuilder.from(salarySpending).asGuiItem(event -> {
            OpenTownEconomicsHistory(player,HistoryEnum.SALARY);
            event.setCancelled(true);
        });
        GuiItem _chunkSpending = ItemBuilder.from(chunkSpending).asGuiItem(event -> {
            OpenTownEconomicsHistory(player,HistoryEnum.CHUNK);
            event.setCancelled(true);
        });
        GuiItem _workbenchSpending = ItemBuilder.from(workbenchSpending).asGuiItem(event -> {
            OpenTownEconomicsHistory(player,HistoryEnum.MISCELLANEOUS);
            event.setCancelled(true);
        });
        GuiItem _donation = ItemBuilder.from(donation).asGuiItem(event -> {
            player.sendMessage(ChatUtils.getTANString() + Lang.WRITE_IN_CHAT_AMOUNT_OF_MONEY_FOR_TOWN_DONATION.getTranslation());
            PlayerChatListenerStorage.addPlayer(PlayerChatListenerStorage.ChatCategory.DONATION,player);
            player.closeInventory();
            event.setCancelled(true);
        });
        GuiItem _donationHistory = ItemBuilder.from(donationHistory).asGuiItem(event -> {
            OpenTownEconomicsHistory(player,HistoryEnum.DONATION);
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

        GuiItem _decorativeGlass = ItemBuilder.from(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE)).asGuiItem(event -> event.setCancelled(true));

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
    public static void OpenTownEconomicsHistory(Player player, HistoryEnum historyType) {

        Gui gui = createChestGui("Town",6);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData town = TownDataStorage.get(playerStat.getTownId());


        switch (historyType){

            case DONATION -> {

                int i = 0;
                for(TransactionHistory donation : town.getTreasury().getDonationHistory()){

                    ItemStack transactionIcon = HeadUtils.getCustomLoreItem(Material.PAPER,
                            ChatColor.DARK_AQUA + donation.getName(),
                            Lang.DONATION_SINGLE_LINE_1.getTranslation(donation.getAmount()),
                            Lang.DONATION_SINGLE_LINE_2.getTranslation(donation.getDate())
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

                LinkedHashMap<String, ArrayList<TransactionHistory>> taxHistory = town.getTreasury().getTaxHistory();

                int i = 0;

                for(Map.Entry<String,ArrayList<TransactionHistory>> oneDay : taxHistory.entrySet()){

                    String date = oneDay.getKey();
                    ArrayList<TransactionHistory> taxes = oneDay.getValue();


                    List<String> lines = new ArrayList<>();

                    for (TransactionHistory singleTax : taxes){

                        if(singleTax.getAmount() == -1){
                            lines.add(Lang.TAX_SINGLE_LINE_NOT_ENOUGH.getTranslation(singleTax.getName()));
                        }
                        else{
                            lines.add(Lang.TAX_SINGLE_LINE.getTranslation(singleTax.getName(), singleTax.getAmount()));
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

                for(TransactionHistory chunkTax : town.getTreasury().getChunkHistory().values()){


                    ItemStack transactionIcon = HeadUtils.getCustomLoreItem(Material.PAPER,
                            ChatColor.DARK_AQUA + chunkTax.getDate(),
                            Lang.CHUNK_HISTORY_DESC1.getTranslation(chunkTax.getAmount()),
                            Lang.CHUNK_HISTORY_DESC2.getTranslation(chunkTax.getName(), String.format("%.2f", upkeepCost/10),chunkTax.getAmount())

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

            }
            case MISCELLANEOUS -> {
                int i = 0;

                for (TransactionHistory miscellaneous : town.getTreasury().getMiscellaneousPurchaseHistory()){

                    ItemStack transactionIcon = HeadUtils.getCustomLoreItem(Material.PAPER,
                            ChatColor.DARK_AQUA + miscellaneous.getDate(),
                            Lang.MISCELLANEOUS_HISTORY_DESC1.getTranslation(miscellaneous.getName()),
                            Lang.MISCELLANEOUS_HISTORY_DESC2.getTranslation(miscellaneous.getAmount())
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



        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());

        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownEconomics(player);
        });

        gui.setItem(6,1, _getBackArrow);

        gui.open(player);

    }
    public static void OpenTownLevel(Player player){
        Gui gui = createChestGui("Town",3);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData townData = TownDataStorage.get(player);
        TownLevel townLevel = townData.getTownLevel();

        ItemStack TownIcon = HeadUtils.getTownIcon(PlayerDataStorage.get(player.getUniqueId().toString()).getTownId());
        ItemStack upgradeTownLevel = HeadUtils.getCustomLoreItem(Material.EMERALD, Lang.GUI_TOWN_LEVEL_UP.getTranslation());
        ItemStack upgradeChunkCap = HeadUtils.makeSkull(Lang.GUI_TOWN_LEVEL_UP_CHUNK_CAP.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=");
        ItemStack upgradePlayerCap = HeadUtils.makeSkull(Lang.GUI_TOWN_LEVEL_UP_PLAYER_CAP.getTranslation(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I0M2IyMzE4OWRjZjEzMjZkYTQyNTNkMWQ3NTgyZWY1YWQyOWY2YzI3YjE3MWZlYjE3ZTMxZDA4NGUzYTdkIn19fQ==");

        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());

        HeadUtils.setLore(upgradeTownLevel,
                Lang.GUI_TOWN_LEVEL_UP_DESC1.getTranslation(townLevel.getTownLevel()),
                Lang.GUI_TOWN_LEVEL_UP_DESC2.getTranslation(townLevel.getTownLevel()+1, townLevel.getMoneyRequiredTownLevel())
        );
        HeadUtils.setLore(upgradeChunkCap,
                Lang.GUI_TOWN_LEVEL_UP_CHUNK_CAP_DESC1.getTranslation(townLevel.getChunkCapLevel()),
                Lang.GUI_TOWN_LEVEL_UP_CHUNK_CAP_DESC2.getTranslation(townLevel.getChunkCapLevel()+1,townLevel.getMoneyRequiredChunkCap()),
                Lang.GUI_TOWN_LEVEL_UP_CHUNK_CAP_DESC3.getTranslation(townLevel.getMultiplierChunkCap()),
                Lang.GUI_TOWN_LEVEL_UP_CHUNK_CAP_DESC4.getTranslation(townLevel.getChunkCap())
        );
        HeadUtils.setLore(upgradePlayerCap,
                Lang.GUI_TOWN_LEVEL_UP_PLAYER_CAP_DESC1.getTranslation(townLevel.getPlayerCapLevel()),
                Lang.GUI_TOWN_LEVEL_UP_PLAYER_CAP_DESC2.getTranslation(townLevel.getPlayerCapLevel()+1,townLevel.getMoneyRequiredPlayerCap()),
                Lang.GUI_TOWN_LEVEL_UP_PLAYER_CAP_DESC3.getTranslation(townLevel.getMultiplierPlayerCap()),
                Lang.GUI_TOWN_LEVEL_UP_PLAYER_CAP_DESC4.getTranslation(townLevel.getPlayerCap())
        );


        GuiItem _TownIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> event.setCancelled(true));
        GuiItem _upgradeTownLevel = ItemBuilder.from(upgradeTownLevel).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.hasPermission(TownRolePermission.UPGRADE_TOWN)){
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
                return;
            }
            if(townData.getTreasury().getBalance() > townLevel.getMoneyRequiredTownLevel()){
                townData.getTreasury().removeToBalance(townLevel.getMoneyRequiredTownLevel());
                townLevel.TownLevelUp();
                player.sendMessage(getTANString() + Lang.BASIC_LEVEL_UP.getTranslation());
                OpenTownLevel(player);
            }
            else{
                player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.getTranslation());
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
                player.sendMessage(getTANString() + Lang.GUI_TOWN_LEVEL_UP.getTranslation());
                OpenTownLevel(player);
            }
            else{
                player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.getTranslation());
            }
        });
        GuiItem _upgradePlayerCap = ItemBuilder.from(upgradePlayerCap).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.hasPermission(TownRolePermission.UPGRADE_TOWN)){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
                return;
            }
            if (townData.getTreasury().getBalance() > townLevel.getMoneyRequiredPlayerCap()) {
                townData.getTreasury().removeToBalance(townLevel.getMoneyRequiredPlayerCap());
                townLevel.PlayerCapLevelUp();
                player.sendMessage(getTANString() + Lang.GUI_TOWN_LEVEL_UP.getTranslation());
                OpenTownLevel(player);
            } else {
                player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.getTranslation());
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

        Gui gui = createChestGui("Town",3);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(player);

        ItemStack TownIcon = HeadUtils.getTownIcon(playerStat.getTownId());
        ItemStack leaveTown = HeadUtils.getCustomLoreItem(Material.BARRIER,
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN.getTranslation(),
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC1.getTranslation(playerTown.getName()),
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC2.getTranslation()
        );

        ItemStack deleteTown = HeadUtils.getCustomLoreItem(Material.BARRIER,
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN.getTranslation(),
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC1.getTranslation(playerTown.getName()),
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC2.getTranslation()
        );

        ItemStack changeOwnershipTown = HeadUtils.getCustomLoreItem(Material.BEEHIVE,
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP.getTranslation(),
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC1.getTranslation(),
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC2.getTranslation()
        );

        ItemStack changeMessage = HeadUtils.getCustomLoreItem(Material.WRITABLE_BOOK,
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_MESSAGE.getTranslation(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_MESSAGE_DESC1.getTranslation(playerTown.getDescription())
        );

        ItemStack toggleApplication = HeadUtils.getCustomLoreItem(Material.PAPER,
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION.getTranslation(),
                (playerTown.isRecruiting() ? Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_ACCEPT.getTranslation() : Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_NOT_ACCEPT.getTranslation()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_CLICK_TO_SWITCH.getTranslation()
        );

        int changeTownNameCost = ConfigUtil.getCustomConfig("config.yml").getInt("ChangeTownNameCost");

        ItemStack changeTownName = HeadUtils.getCustomLoreItem(Material.NAME_TAG,
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME.getTranslation(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME_DESC1.getTranslation(playerTown.getName()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME_DESC2.getTranslation(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME_DESC3.getTranslation(changeTownNameCost)
        );


        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW,
                Lang.GUI_BACK_ARROW.getTranslation());

        GuiItem _townIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> event.setCancelled(true));

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
            deleteTown(playerTown);

            player.closeInventory();
            player.sendMessage(ChatUtils.getTANString() + Lang.CHAT_PLAYER_TOWN_SUCCESSFULLY_DELETED.getTranslation());
        });

        GuiItem _changeOwnershipTown = ItemBuilder.from(changeOwnershipTown).asGuiItem(event -> {

            event.setCancelled(true);

            if(playerStat.isTownLeader())
                OpenTownChangeOwnershipPlayerSelect(player, playerTown);
            else
                player.sendMessage(ChatUtils.getTANString() + Lang.NOT_TOWN_LEADER_ERROR.getTranslation());

        });

        GuiItem _changeMessage = ItemBuilder.from(changeMessage).asGuiItem(event -> {
            player.closeInventory();
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_MESSAGE_IN_CHAT.getTranslation());
            Map<MessageKey, String> data = new HashMap<>();
            data.put(MessageKey.TOWN_ID,playerTown.getID());
            PlayerChatListenerStorage.addPlayer(PlayerChatListenerStorage.ChatCategory.CHANGE_DESCRIPTION,player,data);
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
                player.sendMessage(ChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.getTranslation());
                return;
            }

            if(playerStat.isTownLeader()){
                player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_MESSAGE_IN_CHAT.getTranslation());
                Map<MessageKey, String> data = new HashMap<>();
                data.put(MessageKey.TOWN_ID,playerTown.getID());
                data.put(MessageKey.COST,Integer.toString(changeTownNameCost));
                PlayerChatListenerStorage.addPlayer(PlayerChatListenerStorage.ChatCategory.CHANGE_TOWN_NAME,player,data);
                player.closeInventory();
            }
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
        gui.setItem(13, _changeMessage);
        gui.setItem(14, _toggleApplication);
        gui.setItem(15, _changeTownName);

        gui.setItem(18, _getBackArrow);

        gui.open(player);
    }
    public static void OpenTownChangeOwnershipPlayerSelect(Player player, TownData townData) {

        Gui gui = createChestGui("Town",3);

        int i = 0;
        for (String playerUUID : townData.getPlayerList()){
            OfflinePlayer townPlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerUUID));

            ItemStack playerHead = HeadUtils.getPlayerHead(townPlayer.getName(),townPlayer);
            HeadUtils.setLore(playerHead,
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC1.getTranslation(player.getName()),
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC2.getTranslation()
            );

            GuiItem _playerHead = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);

                townData.setUuidLeader(townPlayer.getUniqueId().toString());
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

        Gui gui = createChestGui("Town",3);


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
        Gui gui = createChestGui("Town - Relation",4);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(playerStat);

        ArrayList<String> TownListUUID = playerTown.getRelations().getOne(relation);
        int i = 0;
        for(String otherTownUUID : TownListUUID){
            ItemStack townIcon = HeadUtils.getTownIconWithInformations(otherTownUUID);

            if(relation == TownRelation.WAR) {
                ItemMeta meta = townIcon.getItemMeta();
                assert meta != null;
                List<String> lore = meta.getLore();
                assert lore != null;
                lore.add(Lang.GUI_TOWN_ATTACK_TOWN_DESC1.getTranslation());
                lore.add(Lang.GUI_TOWN_ATTACK_TOWN_DESC2.getTranslation());
                meta.setLore(lore);
                townIcon.setItemMeta(meta);
            }

                GuiItem _town = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);

                if(relation == TownRelation.WAR){
                    player.sendMessage(getTANString() + Lang.GUI_TOWN_ATTACK_TOWN_EXECUTED.getTranslation(TownDataStorage.get(otherTownUUID).getName()));
                    WarTaggedPlayer.addPlayersToTown(otherTownUUID,playerTown.getPlayerList());
                    TownDataStorage.get(otherTownUUID).broadCastMessage(getTANString() + Lang.GUI_TOWN_ATTACK_TOWN_INFO.getTranslation(playerTown.getName()));
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
        GuiItem _next = ItemBuilder.from(nextPageButton).asGuiItem(event -> event.setCancelled(true));
        GuiItem _previous = ItemBuilder.from(previousPageButton).asGuiItem(event -> event.setCancelled(true));
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
        Gui gui = createChestGui("Town - Relation",4);

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
                        player.sendMessage(getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_ALREADY_HAVE_RELATION.getTranslation());
                        return;
                    }
                    if(relation.getNeedsConfirmationToStart()){

                        OfflinePlayer otherTownLeader = Bukkit.getOfflinePlayer(UUID.fromString(otherTown.getUuidLeader()));

                        if (!otherTownLeader.isOnline()) {
                            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NOT_ONLINE.getTranslation());
                            return;
                        }
                        Player otherTownLeaderOnline = otherTownLeader.getPlayer();assert otherTownLeaderOnline != null;

                        TownRelationConfirmStorage.addInvitation(otherTown.getUuidLeader(), playerTown.getID(), relation);

                        otherTownLeaderOnline.sendMessage(getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_RECEIVED_1.getTranslation(playerTown.getName(),relation.getColor() + relation.getName()));
                        ChatUtils.sendClickableCommand(otherTownLeaderOnline,getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_RECEIVED_2.getTranslation(),"tan accept "  + playerTown.getID());

                        player.sendMessage(ChatUtils.getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_SENT_SUCCESS.getTranslation(otherTownLeaderOnline.getName()));

                        player.closeInventory();
                    }
                    else{
                        playerTown.broadCastMessage(getTANString() + Lang.GUI_TOWN_CHANGED_RELATION_RESUME.getTranslation(otherTown.getName(),relation.getColor() + relation.getName()));
                        otherTown.broadCastMessage(getTANString() + Lang.GUI_TOWN_CHANGED_RELATION_RESUME.getTranslation(playerTown.getName(),relation.getColor() + relation.getName()));
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
                        playerTown.broadCastMessage(getTANString() + Lang.GUI_TOWN_CHANGED_RELATION_RESUME.getTranslation(otherTown.getName(),"neutral"));
                        otherTown.broadCastMessage(getTANString() + Lang.GUI_TOWN_CHANGED_RELATION_RESUME.getTranslation(playerTown.getName(),"neutral"));
                        removeRelation(playerTown,otherTown,relation);
                        OpenTownRelation(player,relation);
                    }



                    OpenTownRelation(player,relation);
                });
                gui.setItem(i, _town);
                i = i+1;
            }
            TownDataStorage.get(playerTown.getID()).removeTownRelations(relation,player.getUniqueId().toString());
            _decorativeGlass = ItemBuilder.from(new ItemStack(Material.RED_STAINED_GLASS_PANE)).asGuiItem(event -> event.setCancelled(true));
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
        GuiItem _next = ItemBuilder.from(nextPageButton).asGuiItem(event -> event.setCancelled(true));
        GuiItem _previous = ItemBuilder.from(previousPageButton).asGuiItem(event -> event.setCancelled(true));

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
        Gui gui = createChestGui("Town",4);

        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        TownData townClass = TownDataStorage.get(player);
        ClaimedChunkSettings settings = townClass.getChunkSettings();

        Material[] materials = {Material.OAK_DOOR, Material.CHEST,Material.BRICKS,Material.IRON_PICKAXE,Material.BEEF,
                Material.STONE_BUTTON,Material.REDSTONE,Material.FURNACE,Material.ITEM_FRAME,Material.ARMOR_STAND,
                Material.CAULDRON,Material.JUKEBOX,Material.LEAD,Material.SHEARS};


        Object[][] itemData = {
                {TownChunkPermissionType.DOOR, Material.OAK_DOOR, Lang.GUI_TOWN_CLAIM_SETTINGS_DOOR},
                {TownChunkPermissionType.CHEST, Material.CHEST, Lang.GUI_TOWN_CLAIM_SETTINGS_CHEST},
                {TownChunkPermissionType.PLACE, Material.BRICKS, Lang.GUI_TOWN_CLAIM_SETTINGS_BUILD},
                {TownChunkPermissionType.BREAK, Material.IRON_PICKAXE, Lang.GUI_TOWN_CLAIM_SETTINGS_BREAK},
                {TownChunkPermissionType.ATTACK_PASSIVE_MOB, Material.BEEF, Lang.GUI_TOWN_CLAIM_SETTINGS_ATTACK_PASSIVE_MOBS},
                {TownChunkPermissionType.USE_BUTTONS, Material.STONE_BUTTON, Lang.GUI_TOWN_CLAIM_SETTINGS_BUTTON},
                {TownChunkPermissionType.USE_REDSTONE, Material.REDSTONE, Lang.GUI_TOWN_CLAIM_SETTINGS_REDSTONE},
                {TownChunkPermissionType.USE_FURNACE, Material.FURNACE, Lang.GUI_TOWN_CLAIM_SETTINGS_FURNACE},
                {TownChunkPermissionType.INTERACT_ITEM_FRAME, Material.ITEM_FRAME, Lang.GUI_TOWN_CLAIM_SETTINGS_INTERACT_ITEM_FRAME},
                {TownChunkPermissionType.INTERACT_ARMOR_STAND, Material.ARMOR_STAND, Lang.GUI_TOWN_CLAIM_SETTINGS_INTERACT_ARMOR_STAND},
                {TownChunkPermissionType.DECORATIVE_BLOCK, Material.CAULDRON, Lang.GUI_TOWN_CLAIM_SETTINGS_DECORATIVE_BLOCK},
                {TownChunkPermissionType.MUSIC_BLOCK, Material.JUKEBOX, Lang.GUI_TOWN_CLAIM_SETTINGS_MUSIC_BLOCK},
                {TownChunkPermissionType.LEAD, Material.LEAD, Lang.GUI_TOWN_CLAIM_SETTINGS_LEAD},
                {TownChunkPermissionType.SHEARS, Material.SHEARS, Lang.GUI_TOWN_CLAIM_SETTINGS_SHEARS}
        };

        for (int i = 0; i < itemData.length; i++) {
            TownChunkPermissionType type = (TownChunkPermissionType) itemData[i][0];
            Material material = (Material) itemData[i][1];
            Lang label = (Lang) itemData[i][2];

            TownChunkPermission permission = settings.getPermission(type);
            ItemStack itemStack = HeadUtils.getCustomLoreItem(
                    material,
                    label.getTranslation(),
                    Lang.GUI_TOWN_CLAIM_SETTINGS_DESC1.getTranslation(permission.getColoredName()),
                    Lang.GUI_LEFT_CLICK_TO_INTERACT.getTranslation()
            );

            GuiItem guiItem = createGuiItem(itemStack, playerStat, player, v -> settings.nextPermission(type));
            gui.setItem(i, guiItem);
        }

        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());
        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuHaveTown(player);
        });
        gui.setItem(27, _getBackArrow);

        gui.open(player);
    }

    private static GuiItem createGuiItem(ItemStack itemStack, PlayerData playerStat, Player player, Consumer<Void> action) {
        return ItemBuilder.from(itemStack).asGuiItem(event -> {
            event.setCancelled(true);
            if (!playerStat.hasPermission(TownRolePermission.MANAGE_CLAIM_SETTINGS)) {
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
                return;
            }
            action.accept(null);
            OpenTownChunkMenu(player);
        });
    }
    private static Gui createChestGui(String name, int nRow) {
        return Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();
    }

    private static GuiItem CreateBackArrow(Player player, Consumer<Player> openMenuAction) {
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.getTranslation());
        return ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            openMenuAction.accept(player);
        });
    }
}
