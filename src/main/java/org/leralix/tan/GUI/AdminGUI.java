package org.leralix.tan.GUI;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.LandmarkClaimedChunk;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.Lang.Lang;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.enums.SoundEnum;
import org.leralix.tan.listeners.ChatListener.Events.ChangeDescription;
import org.leralix.tan.listeners.ChatListener.Events.ChangeLandmarkName;
import org.leralix.tan.listeners.ChatListener.Events.ChangeTerritoryName;
import org.leralix.tan.listeners.ChatListener.Events.CreateEmptyTown;
import org.leralix.tan.listeners.ChatListener.PlayerChatListenerStorage;
import org.leralix.tan.storage.DataStorage.*;
import org.leralix.tan.utils.*;

import java.util.ArrayList;
import java.util.UUID;

import static org.leralix.tan.enums.SoundEnum.GOOD;
import static org.leralix.tan.utils.ChatUtils.getTANString;

public class AdminGUI implements IGUI{
    public static void OpenMainMenu(Player player){

        Gui gui = IGUI.createChestGui("Main menu - Admin",4);

        ItemStack regionHead = HeadUtils.makeSkullB64(Lang.GUI_REGION_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljMTgzMmU0ZWY1YzRhZDljNTE5ZDE5NGIxOTg1MDMwZDI1NzkxNDMzNGFhZjI3NDVjOWRmZDYxMWQ2ZDYxZCJ9fX0=");
        ItemStack townHead = HeadUtils.makeSkullB64(Lang.GUI_TOWN_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=",
                Lang.ADMIN_GUI_TOWN_DESC.get());
        ItemStack playerHead = HeadUtils.createCustomItemStack(Material.PLAYER_HEAD,
                Lang.GUI_TOWN_CHUNK_PLAYER.get(),
                Lang.ADMIN_GUI_PLAYER_DESC.get());
        ItemStack landmark = HeadUtils.makeSkullB64(Lang.ADMIN_GUI_LANDMARK_ICON.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQ3NjFjYzE2NTYyYzg4ZDJmYmU0MGFkMzg1MDJiYzNiNGE4Nzg1OTg4N2RiYzM1ZjI3MmUzMGQ4MDcwZWVlYyJ9fX0=",
                Lang.ADMIN_GUI_LANDMARK_DESC1.get());
        ItemStack landMark = HeadUtils.makeSkullURL(Lang.ADMIN_GUI_WAR_ICON.get(), "http://textures.minecraft.net/texture/e2941b8b71abe79ce12775aee601fec9126dee730e2a57257a784231de6da848",
                Lang.ADMIN_GUI_WAR_DESC1.get());


        GuiItem _region = ItemBuilder.from(regionHead).asGuiItem(event -> {
            event.setCancelled(true);
            OpenRegionDebugMenu(player, 0);
        });

        GuiItem _town = ItemBuilder.from(townHead).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuDebug(player, 0);
        });

        GuiItem _player = ItemBuilder.from(playerHead).asGuiItem(event -> {
            event.setCancelled(true);
            OpenPlayerMenu(player, 0);
        });

        GuiItem _landmark = ItemBuilder.from(landmark).asGuiItem(event -> {
            event.setCancelled(true);
            OpenLandmarks(player, 0);
        });
        GuiItem _wars = ItemBuilder.from(landMark).asGuiItem(event -> {
            event.setCancelled(true);
            OpenAdminWarMenu(player,0);
        });


        gui.setItem(2,2,_region);
        gui.setItem(2,3,_town);
        gui.setItem(2, 6,_player);
        gui.setItem(2, 7,_landmark);
        gui.setItem(2, 8,_wars);
        gui.setItem(4,1, IGUI.CreateBackArrow(player,p -> player.closeInventory()));

        gui.open(player);
    }

    private static void OpenAdminWarMenu(Player player, int page) {
        Gui gui = IGUI.createChestGui("Wars - Admin", 6);
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for(PlannedAttack plannedAttack : PlannedAttackStorage.getWars()){
            ItemStack icon = plannedAttack.getAdminIcon();

            GuiItem item = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);
                if(!plannedAttack.isAdminApproved()){
                    if(event.isLeftClick()){
                        plannedAttack.setAdminApproved(true);
                    }
                    else if(event.isRightClick()){
                        plannedAttack.remove();
                    }
                }
                OpenAdminWarMenu(player, page);
            });
            guiItems.add(item);
        }
        GuiUtil.createIterator(gui, guiItems,page,player, p -> OpenMainMenu(player),
                p -> OpenAdminWarMenu(player, page + 1),
                p -> OpenAdminWarMenu(player, page - 1));
        gui.open(player);
    }

    private static void OpenLandmarks(Player player, int page) {
        Gui gui = IGUI.createChestGui("Landmarks - Admin", 6);

        ArrayList<GuiItem> guiItems = new ArrayList<>();

        for(Landmark landmark : LandmarkStorage.getList()){
            ItemStack icon = landmark.getIcon();
            HeadUtils.addLore(icon,
                    "",
                    Lang.CLICK_TO_OPEN_LANDMARK_MENU.get(),
                    Lang.GUI_GENERIC_SHIFT_CLICK_TO_TELEPORT.get());

            GuiItem item = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);
                if(!event.isShiftClick()){
                    OpenSpecificLandmarkMenu(player, landmark);
                }
                else{
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            player.closeInventory();
                            player.teleport(landmark.getLocation());
                        }
                    }.runTaskLater(TownsAndNations.getPlugin(), 1L);


                    SoundUtil.playSound(player, SoundEnum.GOOD);
                }
            });
            guiItems.add(item);

        }
        GuiUtil.createIterator(gui, guiItems, page, player, p -> OpenMainMenu(player),
                p -> OpenLandmarks(player, page + 1),
                p -> OpenLandmarks(player, page - 1));

        ItemStack createLandmark = HeadUtils.makeSkullB64(Lang.ADMIN_GUI_CREATE_LANDMARK.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");

        GuiItem _createLandmark = ItemBuilder.from(createLandmark).asGuiItem(event -> {
            event.setCancelled(true);


            ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.get(player.getLocation().getBlock().getChunk());

            if (claimedChunk instanceof LandmarkClaimedChunk) {
                player.sendMessage(getTANString() + Lang.ADMIN_CHUNK_ALREADY_LANDMARK.get());
                return;
            }
            LandmarkStorage.addLandmark(player.getLocation());
            OpenLandmarks(player,page);
        });
        gui.setItem(6, 4, _createLandmark);
        gui.open(player);
    }

    private static void OpenSpecificLandmarkMenu(Player player, Landmark landmark) {

        Gui gui = IGUI.createChestGui("Landmark - " + landmark.getName(),3);

        ItemStack changeLandmarkName = HeadUtils.createCustomItemStack(Material.NAME_TAG,
                Lang.ADMIN_GUI_CHANGE_LANDMARK_NAME.get(),
                Lang.ADMIN_GUI_CHANGE_LANDMARK_NAME_DESC1.get());

        ItemStack deleteLandmark = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.ADMIN_GUI_DELETE_LANDMARK.get(),
                Lang.ADMIN_GUI_DELETE_LANDMARK_DESC1.get());

        ItemStack setReward = HeadUtils.createCustomItemStack(landmark.getRessources(),
                Lang.SPECIFIC_LANDMARK_ICON_DESC1.get(),
                Lang.SPECIFIC_LANDMARK_ICON_SWITCH_REWARD.get());

        GuiItem _changeLandmarkName = ItemBuilder.from(changeLandmarkName).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            PlayerChatListenerStorage.register(player, new ChangeLandmarkName(landmark));
        });

        GuiItem _deleteLandmark = ItemBuilder.from(deleteLandmark).asGuiItem(event -> {
            event.setCancelled(true);
            landmark.deleteLandmark();
            SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
            OpenLandmarks(player, 0);
        });

        GuiItem _setReward = ItemBuilder.from(setReward).asGuiItem(event -> {
            event.setCancelled(true);
            ItemStack itemOnCursor = player.getItemOnCursor();
            if(itemOnCursor.getType() == Material.AIR){
                return;
            }
            player.sendMessage(getTANString() + Lang.ADMIN_GUI_LANDMARK_REWARD_SET.get(itemOnCursor.getAmount(), itemOnCursor.getType().name()));
            landmark.setReward(itemOnCursor);
            OpenSpecificLandmarkMenu(player, landmark);
            SoundUtil.playSound(player, SoundEnum.GOOD);
        });




        gui.setItem(2,2, _changeLandmarkName);
        gui.setItem(2,4, _deleteLandmark);
        gui.setItem(2,6, _setReward);


        gui.open(player);

    }

    private static void OpenRegionDebugMenu(Player player, int page) {

        Gui gui = IGUI.createChestGui("Region - Admin",6);

        ArrayList<GuiItem> guiItems = new ArrayList<>();

        for (RegionData regionData : RegionDataStorage.getAllRegions()){

            ItemStack regionIcon = HeadUtils.getRegionIcon(regionData);
            HeadUtils.addLore(regionIcon, Lang.ADMIN_GUI_REGION_DESC.get());
            GuiItem _region = ItemBuilder.from(regionIcon).asGuiItem(event -> {
                event.setCancelled(true);
                OpenSpecificRegionMenu(player, regionData);
            });
            guiItems.add(_region);
        }

        GuiUtil.createIterator(gui, guiItems, page, player, p -> OpenMainMenu(player),
                p -> OpenRegionDebugMenu(player, page + 1),
                p -> OpenRegionDebugMenu(player, page - 1));
        gui.open(player);
    }

    private static void OpenSpecificRegionMenu(Player player, RegionData regionData) {
        Gui gui = IGUI.createChestGui("Region - Admin",3);


        ItemStack changeRegionName = HeadUtils.createCustomItemStack(Material.NAME_TAG,
                Lang.ADMIN_GUI_CHANGE_TOWN_NAME.get(),
                Lang.ADMIN_GUI_CHANGE_TOWN_NAME_DESC1.get(regionData.getName()));
        ItemStack changeRegionDescription = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK,
                Lang.ADMIN_GUI_CHANGE_TOWN_DESCRIPTION.get(),
                Lang.ADMIN_GUI_CHANGE_TOWN_DESCRIPTION_DESC1.get(regionData.getDescription()));
        ItemStack changeTownLeader = HeadUtils.createCustomItemStack(Material.PLAYER_HEAD,
                Lang.ADMIN_GUI_CHANGE_REGION_LEADER.get(),
                Lang.ADMIN_GUI_CHANGE_REGION_LEADER_DESC1.get(regionData.getLeaderData().getName(),regionData.getCapital().getName()));
        ItemStack deleteRegion = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.ADMIN_GUI_DELETE_TOWN.get(),
                Lang.ADMIN_GUI_DELETE_TOWN_DESC1.get(regionData.getName()));

        GuiItem _changeRegionName = ItemBuilder.from(changeRegionName).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            PlayerChatListenerStorage.register(player, new ChangeTerritoryName(regionData, 0, p -> OpenSpecificRegionMenu(player, regionData)));
        });
        GuiItem _changeRegionDescription = ItemBuilder.from(changeRegionDescription).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());

            PlayerChatListenerStorage.register(player, new ChangeDescription(regionData, p -> OpenSpecificRegionMenu(player, regionData)));
            event.setCancelled(true);
        });

        GuiItem _changeTownLeader = ItemBuilder.from(changeTownLeader).asGuiItem(event -> {

            event.setCancelled(true);
            OpenRegionDebugChangeOwnershipPlayerSelect(player, regionData,0);

        });

        GuiItem _deleteRegion = ItemBuilder.from(deleteRegion).asGuiItem(event -> {
            event.setCancelled(true);

            FileUtil.addLineToHistory(Lang.HISTORY_REGION_DELETED.get(player.getName(),regionData.getName()));
            regionData.delete();

            player.closeInventory();
            player.sendMessage(ChatUtils.getTANString() + Lang.CHAT_PLAYER_TOWN_SUCCESSFULLY_DELETED.get());
        });

        gui.setItem(2,2, _changeRegionName);
        gui.setItem(2,4, _changeRegionDescription);
        gui.setItem(2,6, _changeTownLeader);
        gui.setItem(2,8, _deleteRegion);

        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenRegionDebugMenu(player, 0)));
        gui.open(player);
    }

    private static void OpenRegionDebugChangeOwnershipPlayerSelect(Player player, RegionData regionData, int page) {
        Gui gui = IGUI.createChestGui("Region", 6);
        PlayerData playerData = PlayerDataStorage.get(player);

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for(String playerID : regionData.getPlayerIDList()){

            PlayerData iteratePlayerData = PlayerDataStorage.get(playerID);
            ItemStack switchPlayerIcon = HeadUtils.getPlayerHead(Bukkit.getOfflinePlayer(UUID.fromString(playerID)));

            GuiItem _switchPlayer = ItemBuilder.from(switchPlayerIcon).asGuiItem(event -> {
                event.setCancelled(true);
                FileUtil.addLineToHistory(Lang.HISTORY_REGION_CAPITAL_CHANGED.get(player.getName(), regionData.getCapital().getName(), playerData.getTown().getName() ));
                regionData.setLeaderID(iteratePlayerData.getID());

                regionData.broadCastMessageWithSound(Lang.GUI_REGION_SETTINGS_REGION_CHANGE_LEADER_BROADCAST.get(iteratePlayerData.getName()),GOOD);

                if(!regionData.getCapital().getID().equals(iteratePlayerData.getTown().getID())){
                    regionData.broadCastMessage(Lang.GUI_REGION_SETTINGS_REGION_CHANGE_CAPITAL_BROADCAST.get(iteratePlayerData.getTown().getName()));
                    regionData.setCapital(iteratePlayerData.getTownId());
                }
                OpenSpecificRegionMenu(player, regionData);
            });
            guiItems.add(_switchPlayer);

        }

        GuiUtil.createIterator(gui,guiItems,page, player,
                p -> OpenSpecificRegionMenu(player, regionData),
                p -> OpenRegionDebugChangeOwnershipPlayerSelect(player, regionData,page + 1),
                p -> OpenRegionDebugChangeOwnershipPlayerSelect(player, regionData,page - 1));


        gui.open(player);

    }

    public static void OpenTownMenuDebug(Player player, int page){


        Gui gui = IGUI.createChestGui("Town - Admin",6);
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (TownData townData : TownDataStorage.getTownMap().values()) {
            ItemStack townIcon = townData.getIconWithInformations();
            HeadUtils.addLore(townIcon,
                    "",
                    Lang.ADMIN_GUI_LEFT_CLICK_TO_MANAGE_TOWN.get()
            );

            GuiItem _townIteration = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);
                OpenSpecificTownMenu(player, townData);
            });
            guiItems.add(_townIteration);
        }

        GuiUtil.createIterator(gui, guiItems, 0, player, p -> OpenMainMenu(player),
                p -> OpenTownMenuDebug(player, page + 1),
                p -> OpenTownMenuDebug(player, page - 1));


        ItemStack createTown = HeadUtils.makeSkullB64(Lang.ADMIN_GUI_CREATE_TOWN.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                Lang.ADMIN_GUI_CREATE_TOWN_DESC1.get());


        GuiItem _createTown = ItemBuilder.from(createTown).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            PlayerChatListenerStorage.register(player, new CreateEmptyTown());
        });


        gui.setItem(6,5 , _createTown);
        gui.open(player);



    }

    public static void OpenSpecificTownMenu(Player player, @NotNull TownData townData) {


        Gui gui = IGUI.createChestGui("Town - Admin",3);


        ItemStack changeTownName = HeadUtils.createCustomItemStack(Material.NAME_TAG,
                Lang.ADMIN_GUI_CHANGE_TOWN_NAME.get(),
                Lang.ADMIN_GUI_CHANGE_TOWN_NAME_DESC1.get(townData.getName()));
        ItemStack changeTownDescription = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK,
                Lang.ADMIN_GUI_CHANGE_TOWN_DESCRIPTION.get(),
                Lang.ADMIN_GUI_CHANGE_TOWN_DESCRIPTION_DESC1.get(townData.getDescription()));
        ItemStack changeTownLeader = HeadUtils.createCustomItemStack(Material.PLAYER_HEAD,
                Lang.ADMIN_GUI_CHANGE_TOWN_LEADER.get(),
                Lang.ADMIN_GUI_CHANGE_TOWN_LEADER_DESC1.get(townData.getLeaderName()));
        ItemStack deleteTown = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.ADMIN_GUI_DELETE_TOWN.get(),
                Lang.ADMIN_GUI_DELETE_TOWN_DESC1.get(townData.getName()));

        GuiItem _changeTownName = ItemBuilder.from(changeTownName).asGuiItem(event -> {

            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            player.closeInventory();

            PlayerChatListenerStorage.register(player, new ChangeTerritoryName(townData,0, p -> OpenSpecificTownMenu(player, townData)));

        });
        GuiItem _changeTownDescription = ItemBuilder.from(changeTownDescription).asGuiItem(event -> {
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            PlayerChatListenerStorage.register(player, new ChangeDescription(townData, p -> OpenSpecificTownMenu(player, townData)));
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
            FileUtil.addLineToHistory(Lang.HISTORY_TOWN_DELETED.get(player.getName(),townData.getName()));
            townData.delete();

            player.closeInventory();
            player.sendMessage(ChatUtils.getTANString() + Lang.CHAT_PLAYER_TOWN_SUCCESSFULLY_DELETED.get());
        });

        gui.setItem(2,2, _changeTownName);
        gui.setItem(2,4, _changeTownDescription);
        gui.setItem(2,6, _changeTownLeader);
        gui.setItem(2,8, _deleteTown);

        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenTownMenuDebug(player, 0)));

        gui.open(player);
    }

    private static void OpenTownDebugChangeOwnershipPlayerSelect(Player player, TownData townData) {

        Gui gui = IGUI.createChestGui("Town",3);

        int i = 0;
        for (String playerUUID : townData.getPlayerIDList()){
            OfflinePlayer townPlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerUUID));

            ItemStack playerHead = HeadUtils.getPlayerHead(townPlayer,
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC1.get(townPlayer.getName()),
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

    public static void OpenPlayerMenu(Player player, int page) {

        Gui gui = IGUI.createChestGui("Player - Admin",6);

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (PlayerData playerData : PlayerDataStorage.getLists()) {

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerData.getID()));
            ItemStack playerHead = HeadUtils.getPlayerHeadInformation(offlinePlayer);

            GuiItem _playerHead = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                OpenSpecificPlayerMenu(player, playerData);
            });
            guiItems.add(_playerHead);
        }
        GuiUtil.createIterator(gui, guiItems, page, player, p -> OpenMainMenu(player),
                p -> OpenPlayerMenu(player, page + 1),
                p -> OpenPlayerMenu(player, page - 1));
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

                if(playerData.isTownLeader()){
                    player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_LEADER.get());
                    return;
                }
                townData.removePlayer(playerData);

                player.sendMessage(getTANString() + Lang.ADMIN_GUI_TOWN_PLAYER_LEAVE_TOWN_SUCCESS.get(playerData.getName(),townData.getName()));
                OpenSpecificPlayerMenu(player, playerData);
            });
            gui.setItem(2,2, _removePlayerTown);
        }
        else{
            ItemStack addPlayerTown = HeadUtils.createCustomItemStack(Material.SPRUCE_DOOR, "Add player to town", "Add player to town");

            GuiItem _addPlayerTown = ItemBuilder.from(addPlayerTown).asGuiItem(event -> {
                event.setCancelled(true);
                SetPlayerTown(player, playerData, 0);
            });
            gui.setItem(2,2, _addPlayerTown);
        }


        GuiItem _playerHead = ItemBuilder.from(playerHead).asGuiItem(event -> {
            event.setCancelled(true);
        });

        gui.setItem(1,5, _playerHead);
        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenPlayerMenu(player,0)));

        gui.open(player);
    }

    private static void SetPlayerTown(Player player, PlayerData playerData, int page) {

        Gui gui = IGUI.createChestGui("Town - Admin", 6);

        ArrayList<GuiItem> guiItems = new ArrayList<>();


        for (TownData townData : TownDataStorage.getTownMap().values()) {
            ItemStack townIcon = townData.getIconWithInformations();
            HeadUtils.addLore(townIcon,
                    "",
                    Lang.ADMIN_GUI_LEFT_CLICK_TO_MANAGE_TOWN.get()
            );
            GuiItem _townIteration = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);
                townData.addPlayer(playerData);
                OpenSpecificPlayerMenu(player, playerData);
            });
            guiItems.add(_townIteration);
        }

        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> OpenSpecificPlayerMenu(player, playerData),
                p -> SetPlayerTown(player, playerData, page + 1),
                p -> SetPlayerTown(player, playerData, page - 1)
        );
        gui.open(player);

    }
}
