package org.tan.TownsAndNations.GUI;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.RegionData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.ChatCategory;
import org.tan.TownsAndNations.enums.MessageKey;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.RegionDataStorage;
import org.tan.TownsAndNations.storage.PlayerChatListenerStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.FileUtil;
import org.tan.TownsAndNations.utils.HeadUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.tan.TownsAndNations.enums.ChatCategory.*;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;
import static org.tan.TownsAndNations.utils.TownUtil.deleteTown;

public class AdminGUI implements IGUI{
    public static void OpenMainMenu(Player player){

        Gui gui = IGUI.createChestGui("Main menu - Admin",3);

        ItemStack regionHead = HeadUtils.makeSkull(Lang.GUI_REGION_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljMTgzMmU0ZWY1YzRhZDljNTE5ZDE5NGIxOTg1MDMwZDI1NzkxNDMzNGFhZjI3NDVjOWRmZDYxMWQ2ZDYxZCJ9fX0=");
        ItemStack townHead = HeadUtils.makeSkull(Lang.GUI_TOWN_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=",
                Lang.ADMIN_GUI_TOWN_DESC.get());
        ItemStack playerHead = HeadUtils.createCustomItemStack(Material.PLAYER_HEAD,
                Lang.GUI_TOWN_CHUNK_PLAYER.get(),
                Lang.ADMIN_GUI_PLAYER_DESC.get());

        GuiItem _region = ItemBuilder.from(regionHead).asGuiItem(event -> {
            event.setCancelled(true);
            OpenRegionDebugMenu(player);
        });

        GuiItem _town = ItemBuilder.from(townHead).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuDebug(player);
        });

        GuiItem _player = ItemBuilder.from(playerHead).asGuiItem(event -> {
            event.setCancelled(true);
            OpenPlayerMenu(player);
        });
        gui.setItem(12,_region);
        gui.setItem(14,_town);
        gui.setItem(16,_player);
        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> player.closeInventory()));

        gui.open(player);
    }

    private static void OpenRegionDebugMenu(Player player) {

        Gui gui = IGUI.createChestGui("Region - Admin",6);

        int i = 0;
        for (RegionData regionData : RegionDataStorage.getAllRegions()){

            ItemStack regionIcon = HeadUtils.getRegionIcon(regionData);

            HeadUtils.addLore(regionIcon, Lang.ADMIN_GUI_REGION_DESC.get());

            GuiItem _region = ItemBuilder.from(regionIcon).asGuiItem(event -> {
                event.setCancelled(true);
                OpenSpecificRegionMenu(player, regionData);
            });

            gui.addItem(_region);
            i++;

            if(i > 47){
                break;
            }
        }

        gui.setItem(6,1, IGUI.CreateBackArrow(player,p -> OpenMainMenu(player)));

        gui.open(player);
    }

    private static void OpenSpecificRegionMenu(Player player, RegionData regionData) {
        Gui gui = IGUI.createChestGui("Town - Admin",3);


        ItemStack changeRegionName = HeadUtils.createCustomItemStack(Material.NAME_TAG,
                Lang.ADMIN_GUI_CHANGE_TOWN_NAME.get(),
                Lang.ADMIN_GUI_CHANGE_TOWN_NAME_DESC1.get(regionData.getName()));
        ItemStack changeRegionDescription = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK,
                Lang.ADMIN_GUI_CHANGE_TOWN_DESCRIPTION.get(),
                Lang.ADMIN_GUI_CHANGE_TOWN_DESCRIPTION_DESC1.get(regionData.getDescription()));
        ItemStack deleteRegion = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.ADMIN_GUI_DELETE_TOWN.get(),
                Lang.ADMIN_GUI_DELETE_TOWN_DESC1.get(regionData.getName()));

        GuiItem _changeRegionName = ItemBuilder.from(changeRegionName).asGuiItem(event -> {

            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            Map<MessageKey, String> data = new HashMap<>();

            data.put(MessageKey.REGION_ID,regionData.getID());
            data.put(MessageKey.COST,Integer.toString(0));

            PlayerChatListenerStorage.addPlayer(CHANGE_REGION_NAME,player,data);
            player.closeInventory();

        });
        GuiItem _changeRegionDescription = ItemBuilder.from(changeRegionDescription).asGuiItem(event -> {
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
            player.closeInventory();


            Map<MessageKey, String> data = new HashMap<>();
            data.put(MessageKey.REGION_ID,regionData.getID());
            PlayerChatListenerStorage.addPlayer(CHANGE_REGION_DESCRIPTION,player,data);
            event.setCancelled(true);
        });

        GuiItem _deleteRegion = ItemBuilder.from(deleteRegion).asGuiItem(event -> {
            event.setCancelled(true);
            RegionDataStorage.deleteRegion(player, regionData);

            player.closeInventory();
            player.sendMessage(ChatUtils.getTANString() + Lang.CHAT_PLAYER_TOWN_SUCCESSFULLY_DELETED.get());
        });

        gui.setItem(2,2, _changeRegionName);
        gui.setItem(2,4, _changeRegionDescription);
        gui.setItem(2,8, _deleteRegion);

        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenRegionDebugMenu(player)));
        gui.open(player);
    }

    public static void OpenTownMenuDebug(Player player){


        Gui gui = IGUI.createChestGui("Town - Admin",6);

        int i = 0;
        for (TownData townData : TownDataStorage.getTownMap().values()) {
            ItemStack townIcon = HeadUtils.getTownIconWithInformations(townData);
            HeadUtils.addLore(townIcon,
                    "",
                    Lang.ADMIN_GUI_LEFT_CLICK_TO_MANAGE_TOWN.get()
            );

            GuiItem _townIteration = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);
                OpenSpecificTownMenu(player, townData);
            });

            gui.setItem(i, _townIteration);
            i++;
        }

        ItemStack createTown = HeadUtils.makeSkull(Lang.ADMIN_GUI_CREATE_TOWN.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                Lang.ADMIN_GUI_CREATE_TOWN_DESC1.get());

        GuiItem _createTown = ItemBuilder.from(createTown).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
            player.closeInventory();

            PlayerChatListenerStorage.addPlayer(ChatCategory.CREATE_ADMIN_TOWN,player);
        });


        gui.setItem(6,9, _createTown);


        gui.setItem(6,1, IGUI.CreateBackArrow(player,p -> OpenMainMenu(player)));

        gui.open(player);



    }

    public static void OpenSpecificTownMenu(Player player, TownData townData) {


        Gui gui = IGUI.createChestGui("Town - Admin",3);


        ItemStack changeTownName = HeadUtils.createCustomItemStack(Material.NAME_TAG,
                Lang.ADMIN_GUI_CHANGE_TOWN_NAME.get(),
                Lang.ADMIN_GUI_CHANGE_TOWN_NAME_DESC1.get(townData.getName()));
        ItemStack changeTownDescription = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK,
                Lang.ADMIN_GUI_CHANGE_TOWN_DESCRIPTION.get(),
                Lang.ADMIN_GUI_CHANGE_TOWN_DESCRIPTION_DESC1.get(townData.getDescription()));
        ItemStack changeTownLeader = HeadUtils.createCustomItemStack(Material.PLAYER_HEAD,
                Lang.ADMIN_GUI_CHANGE_TOWN_LEADER.get(),
                Lang.ADMIN_GUI_CHANGE_TOWN_LEADER_DESC1.get(Bukkit.getServer().getOfflinePlayer(UUID.fromString(townData.getLeaderID())).getName()));
        ItemStack deleteTown = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.ADMIN_GUI_DELETE_TOWN.get(),
                Lang.ADMIN_GUI_DELETE_TOWN_DESC1.get(townData.getName()));

        GuiItem _changeTownName = ItemBuilder.from(changeTownName).asGuiItem(event -> {

            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
            player.closeInventory();

            Map<MessageKey, String> data = new HashMap<>();

            data.put(MessageKey.TOWN_ID,townData.getID());
            data.put(MessageKey.COST,Integer.toString(0));
            PlayerChatListenerStorage.addPlayer(CHANGE_TOWN_NAME,player,data);



        });
        GuiItem _changeTownDescription = ItemBuilder.from(changeTownDescription).asGuiItem(event -> {
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
            player.closeInventory();

            Map<MessageKey, String> data = new HashMap<>();
            data.put(MessageKey.TOWN_ID,townData.getID());
            PlayerChatListenerStorage.addPlayer(CHANGE_TOWN_DESCRIPTION,player,data);

            event.setCancelled(true);
        });

        GuiItem _changeTownLeader = ItemBuilder.from(changeTownLeader).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownDebugChangeOwnershipPlayerSelect(player, townData);
        });
        GuiItem _deleteTown = ItemBuilder.from(deleteTown).asGuiItem(event -> {
            event.setCancelled(true);
            if(townData.isRegionalCapital()){
                player.sendMessage(getTANString() + Lang.ADMIN_GUI_CANT_DELETE_REGIONAL_CAPITAL.get());
                return;
            }
            deleteTown(player, townData);

            player.closeInventory();
            player.sendMessage(ChatUtils.getTANString() + Lang.CHAT_PLAYER_TOWN_SUCCESSFULLY_DELETED.get());
        });

        gui.setItem(2,2, _changeTownName);
        gui.setItem(2,4, _changeTownDescription);
        gui.setItem(2,6, _changeTownLeader);
        gui.setItem(2,8, _deleteTown);

        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenTownMenuDebug(player)));

        gui.open(player);
    }

    private static void OpenTownDebugChangeOwnershipPlayerSelect(Player player, TownData townData) {

        Gui gui = IGUI.createChestGui("Town",3);

        int i = 0;
        for (String playerUUID : townData.getPlayerList()){
            OfflinePlayer townPlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerUUID));

            ItemStack playerHead = HeadUtils.getPlayerHead(townPlayer,
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC1.get(player.getName()),
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC2.get());

            GuiItem _playerHead = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                FileUtil.addLineToHistory(Lang.HISTORY_TOWN_LEADER_CHANGED.get(player.getName(),townData.getLeaderData(),townPlayer.getName()));
                townData.setLeaderID(townPlayer.getUniqueId().toString());
                player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_SUCCESS.get(townPlayer.getName()));
                OpenSpecificTownMenu(player, townData);
            });

            gui.setItem(i, _playerHead);

            i = i+1;
        }

        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenSpecificTownMenu(player, townData)));
        gui.open(player);
    }

    public static void OpenPlayerMenu(Player player) {

        Gui gui = IGUI.createChestGui("Player - Admin",6);


        int i = 0;
        for (PlayerData playerData : PlayerDataStorage.getLists()) {

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerData.getID()));
            ItemStack playerHead = HeadUtils.getPlayerHeadInformation(offlinePlayer);

            GuiItem _playerHead = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                OpenSpecificPlayerMenu(player, playerData);
            });

            gui.setItem(i, _playerHead);
            i++;
            if(i > 53){
                break;
            }
        }

        gui.setItem(6,1, IGUI.CreateBackArrow(player,p -> OpenMainMenu(player)));
        gui.open(player);
    }

    private static void OpenSpecificPlayerMenu(Player player, PlayerData playerData) {

        Gui gui = IGUI.createChestGui("Player - Admin",3);

        ItemStack playerHead = HeadUtils.getPlayerHeadInformation(Bukkit.getOfflinePlayer(UUID.fromString(playerData.getID())));

        if(playerData.haveTown()){
            ItemStack removePlayerTown = HeadUtils.createCustomItemStack(Material.SPRUCE_DOOR,
                    Lang.ADMIN_GUI_TOWN_PLAYER_TOWN.get(playerData.getTown().getName()),
                    Lang.ADMIN_GUI_TOWN_PLAYER_TOWN_DESC1.get(),
                    Lang.ADMIN_GUI_TOWN_PLAYER_TOWN_DESC2.get());


            GuiItem _removePlayerTown = ItemBuilder.from(removePlayerTown).asGuiItem(event -> {
                event.setCancelled(true);
                TownData townData = playerData.getTown();

                if(townData.getLeaderID().equals(playerData.getID())){
                    player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_LEADER.get());
                    return;
                }
                townData.removePlayer(playerData);

                player.sendMessage(getTANString() + Lang.ADMIN_GUI_TOWN_PLAYER_LEAVE_TOWN_SUCCESS.get(playerData.getName(),townData.getName()));
                OpenPlayerMenu(player);
            });
            gui.setItem(2,2, _removePlayerTown);
        }
        else{
            ItemStack addPlayerTown = HeadUtils.createCustomItemStack(Material.SPRUCE_DOOR, "Add player to town", "Add player to town");

            GuiItem _addPlayerTown = ItemBuilder.from(addPlayerTown).asGuiItem(event -> {
                event.setCancelled(true);
                SetPlayerTown(player);
            });
            gui.setItem(2,2, _addPlayerTown);
        }


        GuiItem _playerHead = ItemBuilder.from(playerHead).asGuiItem(event -> {
            event.setCancelled(true);
        });

        gui.setItem(1,5, _playerHead);


        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenPlayerMenu(player)));

        gui.open(player);
    }

    private static void SetPlayerTown(Player player) {

        Gui gui = IGUI.createChestGui("Town - Admin",6);

        PlayerData playerData = PlayerDataStorage.get(player);
        int i = 0;
        for (TownData townData : TownDataStorage.getTownMap().values()) {
            ItemStack townIcon = HeadUtils.getTownIconWithInformations(townData);
            HeadUtils.addLore(townIcon,
                    "",
                    Lang.ADMIN_GUI_LEFT_CLICK_TO_MANAGE_TOWN.get()
            );

            GuiItem _townIteration = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);
                townData.addPlayer(playerData);
                OpenSpecificPlayerMenu(player, playerData);
            });

            gui.setItem(i, _townIteration);
            i++;

        }


        gui.setItem(6,1, IGUI.CreateBackArrow(player,p -> OpenMainMenu(player)));
        gui.open(player);

    }

}
