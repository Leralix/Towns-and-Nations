package org.leralix.tan.gui;

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
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.LandmarkClaimedChunk;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.ChangeDescription;
import org.leralix.tan.listeners.chat.events.ChangeLandmarkName;
import org.leralix.tan.listeners.chat.events.ChangeTerritoryName;
import org.leralix.tan.listeners.chat.events.CreateEmptyTown;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.utils.*;
import org.leralix.tan.lang.Lang;

import java.util.*;

import static org.leralix.lib.data.SoundEnum.GOOD;


public class AdminGUI implements IGUI{

    private AdminGUI() {
        throw new IllegalStateException("Utility class");
    }

    public static void openMainMenu(Player player){

        Gui gui = IGUI.createChestGui(Lang.HEADER_ADMIN_MAIN_MENU.get(),4);

        ItemStack regionHead = HeadUtils.makeSkullB64(Lang.GUI_REGION_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljMTgzMmU0ZWY1YzRhZDljNTE5ZDE5NGIxOTg1MDMwZDI1NzkxNDMzNGFhZjI3NDVjOWRmZDYxMWQ2ZDYxZCJ9fX0=");
        ItemStack townHead = HeadUtils.makeSkullB64(Lang.GUI_TOWN_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=",
                Lang.ADMIN_GUI_TOWN_DESC.get());
        ItemStack playerHead = HeadUtils.createCustomItemStack(Material.PLAYER_HEAD,
                Lang.GUI_TOWN_CHUNK_PLAYER.get(),
                Lang.ADMIN_GUI_PLAYER_DESC.get());
        ItemStack landmark = HeadUtils.makeSkullB64(Lang.ADMIN_GUI_LANDMARK_ICON.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQ3NjFjYzE2NTYyYzg4ZDJmYmU0MGFkMzg1MDJiYzNiNGE4Nzg1OTg4N2RiYzM1ZjI3MmUzMGQ4MDcwZWVlYyJ9fX0=",
                Lang.ADMIN_GUI_LANDMARK_DESC1.get());
        ItemStack landMark = HeadUtils.makeSkullURL(Lang.ADMIN_GUI_WAR_ICON.get(), "https://textures.minecraft.net/texture/e2941b8b71abe79ce12775aee601fec9126dee730e2a57257a784231de6da848",
                Lang.ADMIN_GUI_WAR_DESC1.get());


        GuiItem regionGui = ItemBuilder.from(regionHead).asGuiItem(event -> {
            event.setCancelled(true);
            openAdminBrowseRegion(player, 0);
        });

        GuiItem townGui = ItemBuilder.from(townHead).asGuiItem(event -> {
            event.setCancelled(true);
            openAdminBrowseTown(player, 0);
        });

        GuiItem playerGui = ItemBuilder.from(playerHead).asGuiItem(event -> {
            event.setCancelled(true);
            openPlayerMenu(player, 0);
        });

        GuiItem landmarkGui = ItemBuilder.from(landmark).asGuiItem(event -> {
            event.setCancelled(true);
            openLandmarks(player, 0);
        });
        GuiItem warsGui = ItemBuilder.from(landMark).asGuiItem(event -> {
            event.setCancelled(true);
            openAdminWarMenu(player,0);
        });


        gui.setItem(2,2,regionGui);
        gui.setItem(2,3,townGui);
        gui.setItem(2, 6,playerGui);
        gui.setItem(2, 7,landmarkGui);
        gui.setItem(2, 8,warsGui);
        gui.setItem(4,1, IGUI.createBackArrow(player, p -> player.closeInventory()));

        gui.open(player);
    }

    private static void openAdminWarMenu(Player player, int page) {
        Gui gui = IGUI.createChestGui(Lang.HEADER_ADMIN_WAR_MENU.get(), 6);
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
                openAdminWarMenu(player, page);
            });
            guiItems.add(item);
        }
        GuiUtil.createIterator(gui, guiItems,page,player, p -> openMainMenu(player),
                p -> openAdminWarMenu(player, page + 1),
                p -> openAdminWarMenu(player, page - 1));
        gui.open(player);
    }

    private static void openLandmarks(Player player, int page) {
        Gui gui = IGUI.createChestGui(Lang.HEADER_ADMIN_LANDMARK_MENU.get(), 6);

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
                    openSpecificLandmarkMenu(player, landmark);
                }
                else{
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            player.closeInventory();
                            player.teleport(landmark.getLocation());
                        }
                    }.runTaskLater(TownsAndNations.getPlugin(), 1L);


                    SoundUtil.playSound(player, GOOD);
                }
            });
            guiItems.add(item);

        }
        GuiUtil.createIterator(gui, guiItems, page, player, p -> openMainMenu(player),
                p -> openLandmarks(player, page + 1),
                p -> openLandmarks(player, page - 1));

        ItemStack createLandmark = HeadUtils.makeSkullB64(Lang.ADMIN_GUI_CREATE_LANDMARK.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");

        GuiItem createLandmarkGui = ItemBuilder.from(createLandmark).asGuiItem(event -> {
            event.setCancelled(true);


            ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.get(player.getLocation().getBlock().getChunk());

            if (claimedChunk instanceof LandmarkClaimedChunk) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.ADMIN_CHUNK_ALREADY_LANDMARK.get());
                return;
            }
            LandmarkStorage.addLandmark(player.getLocation());
            openLandmarks(player,page);
        });
        gui.setItem(6, 4, createLandmarkGui);
        gui.open(player);
    }

    private static void openSpecificLandmarkMenu(Player player, Landmark landmark) {

        Gui gui = IGUI.createChestGui(Lang.HEADER_ADMIN_SPECIFIC_LANDMARK_MENU.get( landmark.getName()),3);

        ItemStack changeLandmarkName = HeadUtils.createCustomItemStack(Material.NAME_TAG,
                Lang.ADMIN_GUI_CHANGE_LANDMARK_NAME.get(),
                Lang.ADMIN_GUI_CHANGE_LANDMARK_NAME_DESC1.get());

        ItemStack deleteLandmark = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.ADMIN_GUI_DELETE_LANDMARK.get(),
                Lang.ADMIN_GUI_DELETE_LANDMARK_DESC1.get());

        ItemStack setReward = HeadUtils.createCustomItemStack(landmark.getRessources(),
                Lang.SPECIFIC_LANDMARK_ICON_DESC1.get(),
                Lang.SPECIFIC_LANDMARK_ICON_SWITCH_REWARD.get());

        GuiItem changeLandmarkNameGui = ItemBuilder.from(changeLandmarkName).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            PlayerChatListenerStorage.register(player, new ChangeLandmarkName(landmark));
        });

        GuiItem deleteLandmarkGui = ItemBuilder.from(deleteLandmark).asGuiItem(event -> {
            event.setCancelled(true);
            landmark.deleteLandmark();
            SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
            openLandmarks(player, 0);
        });

        GuiItem setRewardGui = ItemBuilder.from(setReward).asGuiItem(event -> {
            event.setCancelled(true);
            ItemStack itemOnCursor = player.getItemOnCursor();
            if(itemOnCursor.getType() == Material.AIR){
                return;
            }
            player.sendMessage(TanChatUtils.getTANString() + Lang.ADMIN_GUI_LANDMARK_REWARD_SET.get(itemOnCursor.getAmount(), itemOnCursor.getType().name()));
            landmark.setReward(itemOnCursor);
            openSpecificLandmarkMenu(player, landmark);
            SoundUtil.playSound(player, GOOD);
        });




        gui.setItem(2,2, changeLandmarkNameGui);
        gui.setItem(2,4, deleteLandmarkGui);
        gui.setItem(2,6, setRewardGui);


        gui.open(player);

    }

    private static void openAdminBrowseRegion(Player player, int page) {
        Gui gui = IGUI.createChestGui(Lang.HEADER_ADMIN_REGION_MENU.get(),6);

        ArrayList<GuiItem> guiItems = new ArrayList<>();

        for (RegionData regionData : RegionDataStorage.getAll()){

            ItemStack regionIcon = HeadUtils.getRegionIcon(regionData);
            HeadUtils.addLore(regionIcon, Lang.ADMIN_GUI_REGION_DESC.get());
            GuiItem regionGui = ItemBuilder.from(regionIcon).asGuiItem(event -> {
                event.setCancelled(true);
                openSpecificRegionMenu(player, regionData);
            });
            guiItems.add(regionGui);
        }

        GuiUtil.createIterator(gui, guiItems, page, player, p -> openMainMenu(player),
                p -> openAdminBrowseRegion(player, page + 1),
                p -> openAdminBrowseRegion(player, page - 1));
        gui.open(player);
    }

    private static void openSpecificTerritoryMenu(Player player, TerritoryData territoryData) {
        if(territoryData instanceof TownData townData){
            openSpecificTownMenu(player, townData);
        }
        else if(territoryData instanceof RegionData regionData){
            openSpecificRegionMenu(player, regionData);
        }
    }


    private static void openSpecificRegionMenu(Player player, RegionData regionData) {
        Gui gui = IGUI.createChestGui(Lang.HEADER_ADMIN_SPECIFIC_REGION_MENU.get(),3);

        addCommonTerritoryDebugOption(gui, player, regionData);


        ItemStack changeTownLeader = HeadUtils.createCustomItemStack(Material.PLAYER_HEAD,
                Lang.ADMIN_GUI_CHANGE_REGION_LEADER.get(),
                Lang.ADMIN_GUI_CHANGE_REGION_LEADER_DESC1.get(regionData.getLeaderData().getName(),regionData.getCapital().getName()));
        GuiItem changeTownLeaderGui = ItemBuilder.from(changeTownLeader).asGuiItem(event -> {
            event.setCancelled(true);
            openRegionDebugChangeOwnershipPlayerSelect(player, regionData,0);
        });
        gui.setItem(2,6, changeTownLeaderGui);

        gui.setItem(3,1, IGUI.createBackArrow(player, p -> openAdminBrowseRegion(player, 0)));

        gui.open(player);
    }

    private static void addCommonTerritoryDebugOption(Gui gui, Player player, TerritoryData territoryData){

        ItemStack changeRegionName = HeadUtils.createCustomItemStack(Material.NAME_TAG,
                Lang.ADMIN_GUI_CHANGE_TOWN_NAME.get(),
                Lang.ADMIN_GUI_CHANGE_TOWN_NAME_DESC1.get(territoryData.getName()));
        ItemStack changeRegionDescription = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK,
                Lang.ADMIN_GUI_CHANGE_TOWN_DESCRIPTION.get(),
                Lang.ADMIN_GUI_CHANGE_TOWN_DESCRIPTION_DESC1.get(territoryData.getDescription()));
        ItemStack deleteRegion = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.ADMIN_GUI_DELETE_TOWN.get(),
                Lang.ADMIN_GUI_DELETE_TOWN_DESC1.get(territoryData.getName()));

        GuiItem changeRegionNameGui = ItemBuilder.from(changeRegionName).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            PlayerChatListenerStorage.register(player, new ChangeTerritoryName(territoryData, 0, p -> openSpecificTerritoryMenu(player, territoryData)));
        });
        GuiItem changeRegionDescriptionGui = ItemBuilder.from(changeRegionDescription).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            PlayerChatListenerStorage.register(player, new ChangeDescription(territoryData, p -> openSpecificTerritoryMenu(player, territoryData)));
        });
        GuiItem deleteRegionGui = ItemBuilder.from(deleteRegion).asGuiItem(event -> {
            event.setCancelled(true);

            FileUtil.addLineToHistory(Lang.HISTORY_REGION_DELETED.get(player.getName(),territoryData.getName()));

            if(territoryData.isCapital()){
                player.sendMessage(Lang.CANNOT_DELETE_TERRITORY_IF_CAPITAL.get(territoryData.getOverlord().getColoredName()));
                return;
            }
            territoryData.delete();

            player.sendMessage(TanChatUtils.getTANString() + Lang.CHAT_PLAYER_TOWN_SUCCESSFULLY_DELETED.get());
            player.closeInventory();
        });

        gui.setItem(2,3, changeRegionNameGui);
        gui.setItem(2,4, changeRegionDescriptionGui);
        gui.setItem(2,8, deleteRegionGui);
    }

    private static void openRegionDebugChangeOwnershipPlayerSelect(Player player, RegionData regionData, int page) {
        Gui gui = IGUI.createChestGui(Lang.HEADER_ADMIN_CHANGE_REGION_LEADER.get(regionData.getName()), 6);
        PlayerData playerData = PlayerDataStorage.get(player);

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for(String playerID : regionData.getPlayerIDList()){

            PlayerData iteratePlayerData = PlayerDataStorage.get(playerID);
            ItemStack switchPlayerIcon = HeadUtils.getPlayerHead(Bukkit.getOfflinePlayer(UUID.fromString(playerID)));

            GuiItem switchPlayerGui = ItemBuilder.from(switchPlayerIcon).asGuiItem(event -> {
                event.setCancelled(true);
                FileUtil.addLineToHistory(Lang.HISTORY_REGION_CAPITAL_CHANGED.get(player.getName(), regionData.getCapital().getName(), playerData.getTown().getName() ));
                regionData.setLeaderID(iteratePlayerData.getID());

                regionData.broadCastMessageWithSound(Lang.GUI_REGION_SETTINGS_REGION_CHANGE_LEADER_BROADCAST.get(iteratePlayerData.getName()),GOOD);

                if(!regionData.getCapital().getID().equals(iteratePlayerData.getTown().getID())){
                    regionData.broadCastMessage(Lang.GUI_REGION_SETTINGS_REGION_CHANGE_CAPITAL_BROADCAST.get(iteratePlayerData.getTown().getName()));
                    regionData.setCapital(iteratePlayerData.getTownId());
                }
                openSpecificRegionMenu(player, regionData);
            });
            guiItems.add(switchPlayerGui);

        }

        GuiUtil.createIterator(gui,guiItems,page, player,
                p -> openSpecificRegionMenu(player, regionData),
                p -> openRegionDebugChangeOwnershipPlayerSelect(player, regionData,page + 1),
                p -> openRegionDebugChangeOwnershipPlayerSelect(player, regionData,page - 1));


        gui.open(player);

    }

    public static void openAdminBrowseTown(Player player, int page){
        Gui gui = IGUI.createChestGui(Lang.HEADER_ADMIN_TOWN_MENU.get(),6);
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (TownData townData : TownDataStorage.getTownMap().values()) {
            ItemStack townIcon = townData.getIconWithInformations();
            HeadUtils.addLore(townIcon,
                    "",
                    Lang.ADMIN_GUI_LEFT_CLICK_TO_MANAGE_TOWN.get()
            );

            GuiItem townIterationGui = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);
                openSpecificTownMenu(player, townData);
            });
            guiItems.add(townIterationGui);
        }

        GuiUtil.createIterator(gui, guiItems, page, player, p -> openMainMenu(player),
                p -> openAdminBrowseTown(player, page + 1),
                p -> openAdminBrowseTown(player, page - 1));


        ItemStack createTown = HeadUtils.makeSkullB64(Lang.ADMIN_GUI_CREATE_TOWN.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                Lang.ADMIN_GUI_CREATE_TOWN_DESC1.get());


        GuiItem createTownGui = ItemBuilder.from(createTown).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            PlayerChatListenerStorage.register(player, new CreateEmptyTown());
        });


        gui.setItem(6,5 , createTownGui);
        gui.open(player);



    }

    public static void openSpecificTownMenu(Player player, @NotNull TownData townData) {

        Gui gui = IGUI.createChestGui( Lang.HEADER_ADMIN_SPECIFIC_TOWN_MENU.get(townData.getName()),3);

        addCommonTerritoryDebugOption(gui, player, townData);

        ItemStack changeTownLeader = HeadUtils.createCustomItemStack(Material.PLAYER_HEAD,
                Lang.ADMIN_GUI_CHANGE_TOWN_LEADER.get(),
                Lang.ADMIN_GUI_CHANGE_TOWN_LEADER_DESC1.get(townData.getLeaderName()));

        ItemStack setRegionIcon = HeadUtils.makeSkullB64(townData.haveOverlord() ? townData.getOverlord().getName() : Lang.NO_REGION.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljMTgzMmU0ZWY1YzRhZDljNTE5ZDE5NGIxOTg1MDMwZDI1NzkxNDMzNGFhZjI3NDVjOWRmZDYxMWQ2ZDYxZCJ9fX0=");
        if(townData.haveOverlord()) {
            if(townData.isCapital())
                HeadUtils.addLore(setRegionIcon, Lang.GUI_CANNOT_QUIT_IF_LEADER.get());
            else
                HeadUtils.addLore(setRegionIcon, Lang.GUI_RIGHT_CLICK_TO_QUIT.get());
        }
        else {
            HeadUtils.addLore(setRegionIcon, Lang.GUI_LEFT_CLICK_TO_SET_REGION.get());
        }

        GuiItem changeTownLeaderGui = ItemBuilder.from(changeTownLeader).asGuiItem(event -> {
            event.setCancelled(true);
            openTownDebugChangeOwnershipPlayerSelect(player, townData, 0);
        });

        GuiItem setRegionButton = ItemBuilder.from(setRegionIcon).asGuiItem(event -> {
            event.setCancelled(true);
            if(townData.haveOverlord()) {
                if(townData.isCapital())
                    player.sendMessage(Lang.GUI_CANNOT_QUIT_IF_LEADER.get());
                else{
                    townData.removeOverlord();
                    openSpecificTownMenu(player, townData);
                }
            }
            else {
                openChooseNewOverlord(player, townData, 0);
            }
        });

        gui.setItem(2,2, setRegionButton);
        gui.setItem(2,6, changeTownLeaderGui);

        gui.setItem(3,1, IGUI.createBackArrow(player, p -> openAdminBrowseTown(player, 0)));

        gui.open(player);
    }

    private static void openChooseNewOverlord(Player player, TerritoryData territoryData, int page) {
        Gui gui = IGUI.createChestGui(Lang.HEADER_ADMIN_CHANGE_OVERLORD.get(territoryData.getName()),6);

        Collection<RegionData> territoryDataList = RegionDataStorage.getAll();

        List<GuiItem> guiItems = new ArrayList<>();

        for(TerritoryData potentialOverlord : territoryDataList){
            ItemStack potentialOverlordIcon =  potentialOverlord.getIconWithInformations();
            HeadUtils.addLore(potentialOverlordIcon, Lang.LEFT_CLICK_TO_SELECT.get());

            guiItems.add(ItemBuilder.from(potentialOverlordIcon).asGuiItem(event -> {
                event.setCancelled(true);
                territoryData.setOverlord(potentialOverlord);
                potentialOverlord.addVassal(territoryData);
                openSpecificTerritoryMenu(player, territoryData);
            }));
        }
        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> openSpecificTerritoryMenu(player, territoryData),
                p -> openChooseNewOverlord(player, territoryData, page + 1),
                p -> openChooseNewOverlord(player, territoryData, page - 1));

        gui.open(player);

    }

    private static void openTownDebugChangeOwnershipPlayerSelect(Player player, TownData townData, int page) {
        Gui gui = IGUI.createChestGui(Lang.HEADER_ADMIN_CHANGE_TOWN_LEADER.get(townData.getName()),3);

        List<GuiItem> guiItems = new ArrayList<>();

        for (String playerUUID : townData.getPlayerIDList()){
            OfflinePlayer townPlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerUUID));

            ItemStack playerHead = HeadUtils.getPlayerHead(townPlayer,
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC1.get(townPlayer.getName()),
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC2.get());

            GuiItem playerHeadGui = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                FileUtil.addLineToHistory(Lang.HISTORY_TOWN_LEADER_CHANGED.get(player.getName(),townData.getLeaderData(),townPlayer.getName()));
                townData.setLeaderID(townPlayer.getUniqueId().toString());
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_SUCCESS.get(townPlayer.getName()));
                openSpecificTownMenu(player, townData);
            });
            guiItems.add(playerHeadGui);
        }

        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> openSpecificTownMenu(player, townData),
                p -> openTownDebugChangeOwnershipPlayerSelect(player, townData, page + 1),
                p -> openTownDebugChangeOwnershipPlayerSelect(player, townData, page - 1));

        gui.open(player);
    }

    public static void openPlayerMenu(Player player, int page) {
        Gui gui = IGUI.createChestGui(Lang.HEADER_ADMIN_PLAYER_MENU.get(),6);

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (PlayerData playerData : PlayerDataStorage.getLists()) {

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerData.getID()));
            ItemStack playerHead = HeadUtils.getPlayerHeadInformation(offlinePlayer);

            GuiItem playerHeadGui = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                openSpecificPlayerMenu(player, playerData);
            });
            guiItems.add(playerHeadGui);
        }
        GuiUtil.createIterator(gui, guiItems, page, player, p -> openMainMenu(player),
                p -> openPlayerMenu(player, page + 1),
                p -> openPlayerMenu(player, page - 1));
        gui.open(player);
    }

    private static void openSpecificPlayerMenu(Player player, PlayerData playerData) {
        Gui gui = IGUI.createChestGui(Lang.HEADER_ADMIN_PLAYER_MENU.get(),3);

        ItemStack playerHead = HeadUtils.getPlayerHeadInformation(Bukkit.getOfflinePlayer(UUID.fromString(playerData.getID())));

        if(playerData.haveTown()){
            ItemStack removePlayerTown = HeadUtils.createCustomItemStack(Material.SPRUCE_DOOR,
                    Lang.ADMIN_GUI_TOWN_PLAYER_TOWN.get(playerData.getTown().getName()),
                    Lang.ADMIN_GUI_TOWN_PLAYER_TOWN_DESC1.get(),
                    Lang.ADMIN_GUI_TOWN_PLAYER_TOWN_DESC2.get());


            GuiItem removePlayerTownGui = ItemBuilder.from(removePlayerTown).asGuiItem(event -> {
                event.setCancelled(true);
                TownData townData = playerData.getTown();

                if(townData.isLeader(playerData)){
                    player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_LEADER.get());
                    return;
                }
                townData.removePlayer(playerData);

                player.sendMessage(TanChatUtils.getTANString() + Lang.ADMIN_GUI_TOWN_PLAYER_LEAVE_TOWN_SUCCESS.get(playerData.getName(),townData.getName()));
                openSpecificPlayerMenu(player, playerData);
            });
            gui.setItem(2,2, removePlayerTownGui);
        }
        else{
            ItemStack addPlayerTown = HeadUtils.createCustomItemStack(Material.SPRUCE_DOOR, "Add player to town", "Add player to town");

            GuiItem addPlayerTownGui = ItemBuilder.from(addPlayerTown).asGuiItem(event -> {
                event.setCancelled(true);
                setPlayerTown(player, playerData, 0);
            });
            gui.setItem(2,2, addPlayerTownGui);
        }


        GuiItem playerHeadGui = ItemBuilder.from(playerHead).asGuiItem(event -> event.setCancelled(true));

        gui.setItem(1,5, playerHeadGui);
        gui.setItem(3,1, IGUI.createBackArrow(player, p -> openPlayerMenu(player,0)));

        gui.open(player);
    }

    private static void setPlayerTown(Player player, PlayerData playerData, int page) {

        Gui gui = IGUI.createChestGui(Lang.HEADER_ADMIN_SET_PLAYER_TOWN.get(), 6);

        ArrayList<GuiItem> guiItems = new ArrayList<>();


        for (TownData townData : TownDataStorage.getTownMap().values()) {
            ItemStack townIcon = townData.getIconWithInformations();
            HeadUtils.addLore(townIcon,
                    "",
                    Lang.ADMIN_GUI_LEFT_CLICK_TO_MANAGE_TOWN.get()
            );
            GuiItem townIterationGui = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);
                townData.addPlayer(playerData);
                openSpecificPlayerMenu(player, playerData);
            });
            guiItems.add(townIterationGui);
        }

        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> openSpecificPlayerMenu(player, playerData),
                p -> setPlayerTown(player, playerData, page + 1),
                p -> setPlayerTown(player, playerData, page - 1)
        );
        gui.open(player);

    }
}
