package org.tan.TownsAndNations.GUI;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.tan.TownsAndNations.DataClass.*;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.DataClass.territoryData.RegionData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.DataClass.wars.AttackInvolved;
import org.tan.TownsAndNations.DataClass.wars.CreateAttackData;
import org.tan.TownsAndNations.DataClass.wars.wargoals.ConquerWarGoal;
import org.tan.TownsAndNations.DataClass.wars.wargoals.LiberateWarGoal;
import org.tan.TownsAndNations.DataClass.wars.wargoals.SubjugateWarGoal;
import org.tan.TownsAndNations.Lang.DynamicLang;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.*;
import org.tan.TownsAndNations.storage.*;
import org.tan.TownsAndNations.storage.DataStorage.*;
import org.tan.TownsAndNations.storage.Invitation.RegionInviteDataStorage;
import org.tan.TownsAndNations.storage.Invitation.TownRelationConfirmStorage;
import org.tan.TownsAndNations.storage.Legacy.UpgradeStorage;
import org.tan.TownsAndNations.utils.*;

import static org.tan.TownsAndNations.TownsAndNations.isDynmapAddonLoaded;
import static org.tan.TownsAndNations.TownsAndNations.isSQLEnabled;
import static org.tan.TownsAndNations.enums.ChatCategory.*;
import static org.tan.TownsAndNations.enums.MessageKey.*;
import static org.tan.TownsAndNations.enums.SoundEnum.*;
import static org.tan.TownsAndNations.enums.TownRolePermission.*;
import static org.tan.TownsAndNations.storage.MobChunkSpawnStorage.getMobSpawnCost;
import static org.tan.TownsAndNations.storage.DataStorage.TownDataStorage.getTownMap;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;
import static org.tan.TownsAndNations.utils.GuiUtil.createIterator;
import static org.tan.TownsAndNations.utils.HeadUtils.*;
import static org.tan.TownsAndNations.utils.TeamUtils.updateAllScoreboardColor;
import static org.tan.TownsAndNations.utils.TownUtil.*;

import java.util.ArrayList;


import java.util.*;
import java.util.function.Consumer;

public class GuiManager implements IGUI {

    public static void OpenMainMenu(Player player){

        PlayerData playerStat = PlayerDataStorage.get(player);
        boolean playerHaveTown = playerStat.haveTown();
        boolean playerHaveRegion = playerStat.haveRegion();

        TownData town = TownDataStorage.get(playerStat);
        RegionData region = null;
        if(playerHaveRegion){
            region = town.getOverlord();
        }


        Gui gui = IGUI.createChestGui("Main menu",3);

        ItemStack kingdomHead = HeadUtils.makeSkull(Lang.GUI_KINGDOM_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY5MTk2YjMzMGM2Yjg5NjJmMjNhZDU2MjdmYjZlY2NlNDcyZWFmNWM5ZDQ0Zjc5MWY2NzA5YzdkMGY0ZGVjZSJ9fX0=",
                Lang.GUI_KINGDOM_ICON_DESC1.get());
        ItemStack regionHead = HeadUtils.makeSkull(Lang.GUI_REGION_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljMTgzMmU0ZWY1YzRhZDljNTE5ZDE5NGIxOTg1MDMwZDI1NzkxNDMzNGFhZjI3NDVjOWRmZDYxMWQ2ZDYxZCJ9fX0=",
                playerHaveRegion? Lang.GUI_REGION_ICON_DESC1_REGION.get(region.getName()):Lang.GUI_REGION_ICON_DESC1_NO_REGION.get());
        ItemStack townHead = HeadUtils.makeSkull(Lang.GUI_TOWN_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=",
                playerHaveTown? Lang.GUI_TOWN_ICON_DESC1_HAVE_TOWN.get(town.getName()):Lang.GUI_TOWN_ICON_DESC1_NO_TOWN.get());
        ItemStack PlayerHead = HeadUtils.getPlayerHeadInformation(player);

        GuiItem Kingdom = ItemBuilder.from(kingdomHead).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(getTANString() + Lang.GUI_WARNING_STILL_IN_DEV.get());
        });
        GuiItem Region = ItemBuilder.from(regionHead).asGuiItem(event -> {
            event.setCancelled(true);
            dispatchPlayerRegion(player);
        });
        GuiItem Town = ItemBuilder.from(townHead).asGuiItem(event -> {
            event.setCancelled(true);
            dispatchPlayerTown(player);
        });
        GuiItem Player = ItemBuilder.from(PlayerHead).asGuiItem(event -> {
            event.setCancelled(true);
            OpenPlayerProfileMenu(player);
        });


        int slotKingdom = 2;
        int slotRegion = 4;
        int slotTown = 6;
        int slotPlayer = 8;

        if(ConfigUtil.getCustomConfig("config.yml").getBoolean("EnableKingdom",true) &&
                ConfigUtil.getCustomConfig("config.yml").getBoolean("EnableRegion",true)) {
            gui.setItem(2, slotKingdom, Kingdom);
        }

        if(ConfigUtil.getCustomConfig("config.yml").getBoolean("EnableRegion",true)){
            gui.setItem(2,slotRegion,Region);
        }
        else {
            slotTown = 4;
            slotPlayer = 6;
        }

        gui.setItem(2,slotTown,Town);
        gui.setItem(2,slotPlayer,Player);
        gui.setItem(2,slotPlayer,Player);
        gui.setItem(3,1,IGUI.CreateBackArrow(player, p -> player.closeInventory()));

        gui.open(player);
    }

    private static void dispatchPlayerRegion(Player player) {
        if(PlayerDataStorage.get(player).haveRegion()) {
            OpenRegionMenu(player);
        }
        else {
            OpenNoRegionMenu(player);
        }
    }

    public static void dispatchPlayerTown(Player player){
        if(PlayerDataStorage.get(player).haveTown()){
            OpenTownMenuHaveTown(player);
        }
        else{
            OpenTownMenuNoTown(player);
        }
    }

    public static void OpenPlayerProfileMenu(Player player){

        Gui gui = IGUI.createChestGui("Profile",3);


        ItemStack playerHead = HeadUtils.getPlayerHead(Lang.GUI_YOUR_PROFILE.get(),player);
        ItemStack goldPurse = HeadUtils.createCustomItemStack(Material.GOLD_NUGGET, Lang.GUI_YOUR_BALANCE.get(),Lang.GUI_YOUR_BALANCE_DESC1.get(EconomyUtil.getBalance(player)));
        ItemStack properties = HeadUtils.createCustomItemStack(Material.OAK_HANGING_SIGN, Lang.GUI_PLAYER_MANAGE_PROPERTIES.get(),Lang.GUI_PLAYER_MANAGE_PROPERTIES_DESC1.get());


        GuiItem _playerHead = ItemBuilder.from(playerHead).asGuiItem(event -> event.setCancelled(true));
        GuiItem _goldPurse = ItemBuilder.from(goldPurse).asGuiItem(event -> event.setCancelled(true));
        GuiItem _properties = ItemBuilder.from(properties).asGuiItem(event -> {
            event.setCancelled(true);
            OpenPlayerPropertiesMenu(player);
        });

        gui.setItem(1,5, _playerHead);
        gui.setItem(2,2, _goldPurse);
        gui.setItem(2,4, _properties);


        gui.setItem(18, IGUI.CreateBackArrow(player,p -> OpenMainMenu(player)));

        gui.open(player);
    }
    public static void OpenPlayerPropertiesMenu(Player player){
        int nRows = 6;
        Gui gui = IGUI.createChestGui("Properties of " + player.getName(),nRows);

        PlayerData playerData = PlayerDataStorage.get(player);

        int i = 0;
        for (PropertyData propertyData : playerData.getProperties()){

            ItemStack property = propertyData.getIcon();


            GuiItem _property = ItemBuilder.from(property).asGuiItem(event -> {
                OpenPropertyManagerMenu(player, propertyData);
                event.setCancelled(true);
            });
            gui.setItem(i,_property);
            i++;
        }

        ItemStack newProperty = HeadUtils.makeSkull(
                Lang.GUI_PLAYER_NEW_PROPERTY.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19"
        );

        GuiItem _newProperty = ItemBuilder.from(newProperty).asGuiItem(event -> {
            event.setCancelled(true);

            TownData playerTown = playerData.getTown();

            if(!playerData.hasPermission(CREATE_PROPERTY)){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                SoundUtil.playSound(player, NOT_ALLOWED);
                return;
            }

            if(playerTown.getPropertyDataMap().size() >= playerTown.getTownLevel().getPropertyCap()){
                player.sendMessage(getTANString() + Lang.PLAYER_PROPERTY_CAP_REACHED.get());
                return;
            }

            if(PlayerSelectPropertyPositionStorage.contains(playerData)){
                player.sendMessage(getTANString() + Lang.PLAYER_ALREADY_IN_SCOPE.get());
                return;
            }
            player.sendMessage(getTANString() + Lang.PLAYER_RIGHT_CLICK_2_POINTS_TO_CREATE_PROPERTY.get());
            PlayerSelectPropertyPositionStorage.addPlayer(playerData);
            player.closeInventory();
        });

        gui.setItem(nRows,3, _newProperty);
        gui.setItem(nRows,1, IGUI.CreateBackArrow(player,p -> OpenMainMenu(player)));

        gui.open(player);
    }
    public static void OpenPropertyManagerRentMenu(Player player, @NotNull PropertyData propertyData) {
        int nRows = 4;

        Gui gui = IGUI.createChestGui("Property " + propertyData.getName(), nRows);

        ItemStack propertyIcon = propertyData.getIcon();

        ItemStack stopRentingProperty = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.GUI_PROPERTY_STOP_RENTING_PROPERTY.get(),
                Lang.GUI_PROPERTY_STOP_RENTING_PROPERTY_DESC1.get());


        GuiItem _propertyIcon = ItemBuilder.from(propertyIcon).asGuiItem(event -> event.setCancelled(true));

        GuiItem _stopRentingProperty = ItemBuilder.from(stopRentingProperty).asGuiItem(event -> {
            event.setCancelled(true);
            propertyData.expelRenter(true);

            player.sendMessage(getTANString() + Lang.PROPERTY_RENTER_LEAVE_RENTER_SIDE.get(propertyData.getName()));
            SoundUtil.playSound(player,MINOR_GOOD);

            Player owner = propertyData.getOwnerPlayer();
            if(owner != null){
                owner.sendMessage(getTANString() + Lang.PROPERTY_RENTER_LEAVE_OWNER_SIDE.get(player.getName(), propertyData.getName()));
                SoundUtil.playSound(owner,MINOR_BAD);
            }

            player.closeInventory();
        });

        gui.setItem(1,5,_propertyIcon);

        gui.setItem(2,7,_stopRentingProperty);

        gui.setItem(nRows,1, IGUI.CreateBackArrow(player,p -> player.closeInventory()));


        gui.open(player);
    }
    public static void OpenPropertyManagerMenu(Player player, @NotNull PropertyData propertyData){
        int nRows = 4;

        Gui gui = IGUI.createChestGui("Property " + propertyData.getName(),nRows);


        ItemStack propertyIcon = propertyData.getIcon();

        ItemStack changeName = HeadUtils.createCustomItemStack(
                Material.NAME_TAG,
                Lang.GUI_PROPERTY_CHANGE_NAME.get(),
                Lang.GUI_PROPERTY_CHANGE_NAME_DESC1.get(propertyData.getName())
        );

        ItemStack changeDescription = HeadUtils.createCustomItemStack(
                Material.WRITABLE_BOOK,
                Lang.GUI_PROPERTY_CHANGE_DESCRIPTION.get(),
                Lang.GUI_PROPERTY_CHANGE_DESCRIPTION_DESC1.get(propertyData.getDescription())
        );

        ItemStack isForSale;
        if(propertyData.isForSale()){
            isForSale = HeadUtils.makeSkull(Lang.SELL_PROPERTY.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2UyYTUzMGY0MjcyNmZhN2EzMWVmYWI4ZTQzZGFkZWUxODg5MzdjZjgyNGFmODhlYThlNGM5M2E0OWM1NzI5NCJ9fX0=");
        }
        else{
            isForSale = HeadUtils.makeSkull(Lang.SELL_PROPERTY.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVmMDcwOGZjZTVmZmFhNjYwOGNiZWQzZTc4ZWQ5NTgwM2Q4YTg5Mzc3ZDFkOTM4Y2UwYmRjNjFiNmRjOWY0ZiJ9fX0=");
        }
        HeadUtils.setLore(isForSale,
                propertyData.isForSale() ? Lang.GUI_PROPERTY_FOR_SALE.get(): Lang.GUI_PROPERTY_NOT_FOR_SALE.get(),
                Lang.GUI_BUYING_PRICE.get(propertyData.getBuyingPrice()),
                Lang.GUI_LEFT_CLICK_TO_SWITCH_SALE.get(),
                Lang.GUI_RIGHT_CLICK_TO_CHANGE_PRICE.get()
        );



        ItemStack isForRent;
        if(propertyData.isForRent()){
            isForRent = HeadUtils.makeSkull(Lang.RENT_PROPERTY.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2UyYTUzMGY0MjcyNmZhN2EzMWVmYWI4ZTQzZGFkZWUxODg5MzdjZjgyNGFmODhlYThlNGM5M2E0OWM1NzI5NCJ9fX0=");
        }
        else{
            isForRent = HeadUtils.makeSkull(Lang.RENT_PROPERTY.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVmMDcwOGZjZTVmZmFhNjYwOGNiZWQzZTc4ZWQ5NTgwM2Q4YTg5Mzc3ZDFkOTM4Y2UwYmRjNjFiNmRjOWY0ZiJ9fX0=");
        }
        HeadUtils.setLore(isForRent,
                propertyData.isForRent() ? Lang.GUI_PROPERTY_FOR_RENT.get(): Lang.GUI_PROPERTY_NOT_FOR_RENT.get(),
                Lang.GUI_RENTING_PRICE.get(propertyData.getRentPrice()),
                Lang.GUI_LEFT_CLICK_TO_SWITCH_SALE.get(),
                Lang.GUI_RIGHT_CLICK_TO_CHANGE_PRICE.get()
        );

        ItemStack drawnBox = HeadUtils.makeSkull(Lang.GUI_PROPERTY_DRAWN_BOX.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzc3ZDRhMjA2ZDc3NTdmNDc5ZjMzMmVjMWEyYmJiZWU1N2NlZjk3NTY4ZGQ4OGRmODFmNDg2NGFlZTdkM2Q5OCJ9fX0=",
                Lang.GUI_PROPERTY_DRAWN_BOX_DESC1.get());

        ItemStack deleteProperty = HeadUtils.createCustomItemStack(Material.BARRIER,Lang.GUI_PROPERTY_DELETE_PROPERTY.get(),
                Lang.GUI_PROPERTY_DELETE_PROPERTY_DESC1.get());

        ItemStack playerList = HeadUtils.createCustomItemStack(Material.PLAYER_HEAD,Lang.GUI_PROPERTY_PLAYER_LIST.get(),
                Lang.GUI_PROPERTY_PLAYER_LIST_DESC1.get());

        GuiItem _propertyIcon = ItemBuilder.from(propertyIcon).asGuiItem(event -> event.setCancelled(true));

        GuiItem _changeName = ItemBuilder.from(changeName).asGuiItem(event -> {
            event.setCancelled(true);

            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
            player.closeInventory();

            Map<MessageKey, String> data = new HashMap<>();
            data.put(MessageKey.PROPERTY_ID,propertyData.getTotalID());
            PlayerChatListenerStorage.addPlayer(CHANGE_PROPERTY_NAME,player,data);
        });
        GuiItem _changeDescription = ItemBuilder.from(changeDescription).asGuiItem(event -> {
            event.setCancelled(true);

            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
            player.closeInventory();

            Map<MessageKey, String> data = new HashMap<>();
            data.put(MessageKey.PROPERTY_ID,propertyData.getTotalID());
            PlayerChatListenerStorage.addPlayer(CHANGE_PROPERTY_DESCRIPTION,player,data);
        });


        GuiItem _drawnBox = ItemBuilder.from(drawnBox).asGuiItem(event -> {
            event.setCancelled(true);
            player.closeInventory();
            propertyData.showBox(player);
        });

        GuiItem _isForSale = ItemBuilder.from(isForSale).asGuiItem(event -> {
            event.setCancelled(true);

            if(event.getClick() == ClickType.RIGHT){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
                player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
                player.closeInventory();

                Map<MessageKey, String> data = new HashMap<>();
                data.put(MessageKey.PROPERTY_ID,propertyData.getTotalID());
                PlayerChatListenerStorage.addPlayer(CHANGE_PROPERTY_SALE_PRICE,player,data);
            }
            else if (event.getClick() == ClickType.LEFT){
                if(propertyData.isRented()){
                    player.sendMessage(getTANString() + Lang.PROPERTY_ALREADY_RENTED.get());
                    return;
                }
                propertyData.swapIsForSale();
                OpenPropertyManagerMenu(player,propertyData);
            }
        });
        GuiItem _isForRent = ItemBuilder.from(isForRent).asGuiItem(event -> {
            event.setCancelled(true);

            if(event.getClick() == ClickType.RIGHT){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
                player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
                player.closeInventory();

                Map<MessageKey, String> data = new HashMap<>();
                data.put(MessageKey.PROPERTY_ID,propertyData.getTotalID());
                PlayerChatListenerStorage.addPlayer(CHANGE_PROPERTY_RENT_PRICE,player,data);
            }
            else if (event.getClick() == ClickType.LEFT){
                if(propertyData.isRented()){
                    player.sendMessage(getTANString() + Lang.PROPERTY_ALREADY_RENTED.get());
                    return;
                }
                propertyData.swapIsRent();
                OpenPropertyManagerMenu(player,propertyData);
            }
        });

        GuiItem _deleteProperty = ItemBuilder.from(deleteProperty).asGuiItem(event -> {
            event.setCancelled(true);
            propertyData.delete();
            OpenPlayerPropertiesMenu(player);
        });

        GuiItem _playerList = ItemBuilder.from(playerList).asGuiItem(event -> {
            event.setCancelled(true);
            OpenPlayerPropertyPlayerList(player, propertyData, 0);
        });

        if(propertyData.isRented()){
            ItemStack renterIcon = HeadUtils.getPlayerHead(
                    Lang.GUI_PROPERTY_RENTED_BY.get(propertyData.getRenter().getName()),
                    propertyData.getOfflineRenter(),
                    Lang.GUI_PROPERTY_RIGHT_CLICK_TO_EXPEL_RENTER.get());
            GuiItem _renter = ItemBuilder.from(renterIcon).asGuiItem(event -> {
                event.setCancelled(true);

                Player renter = propertyData.getRenterPlayer();
                propertyData.expelRenter(false);

                player.sendMessage(getTANString() + Lang.PROPERTY_RENTER_EXPELLED_OWNER_SIDE.get());
                SoundUtil.playSound(player,MINOR_GOOD);

                if(renter != null){
                    renter.sendMessage(getTANString() + Lang.PROPERTY_RENTER_EXPELLED_RENTER_SIDE.get(propertyData.getName()));
                    SoundUtil.playSound(renter,MINOR_BAD);
                }

                OpenPropertyManagerMenu(player,propertyData);
            });
            gui.setItem(3,7,_renter);
        }


        gui.setItem(1,5,_propertyIcon);
        gui.setItem(2,2,_changeName);
        gui.setItem(2,3,_changeDescription);

        gui.setItem(2,5,_drawnBox);

        gui.setItem(2,7,_isForSale);
        gui.setItem(2,8,_isForRent);

        gui.setItem(3, 2, _playerList);
        gui.setItem(3,8,_deleteProperty);



        gui.setItem(nRows,1, IGUI.CreateBackArrow(player,p -> OpenPlayerPropertiesMenu(player)));

        gui.open(player);
    }

    private static void OpenPlayerPropertyPlayerList(Player player, PropertyData propertyData, int page) {

        int nRows = 4;
        Gui gui = IGUI.createChestGui("Property " + propertyData.getName(),nRows);

        PlayerData playerData = PlayerDataStorage.get(player);
        boolean canKick = propertyData.canPlayerManageInvites(playerData.getID());
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for(String playerID : propertyData.getAllowedPlayersID()){
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));

            ItemStack playerHead = HeadUtils.getPlayerHead(offlinePlayer,
                    canKick ? Lang.GUI_TOWN_MEMBER_DESC3.get() : "");

            GuiItem headGui = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                if(!canKick || event.getClick() != ClickType.RIGHT ){
                    return;
                }
                propertyData.removeAuthorizedPlayer(playerID);
                OpenPlayerPropertyPlayerList(player, propertyData, page);

                SoundUtil.playSound(player,MINOR_GOOD);
                player.sendMessage(Lang.PLAYER_REMOVED_FROM_PROPERTY.get(offlinePlayer.getName()));
            });
            guiItems.add(headGui);
        }
        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> OpenPropertyManagerMenu(player, propertyData),
                p -> OpenPlayerPropertyPlayerList(player, propertyData, page + 1),
                p -> OpenPlayerPropertyPlayerList(player, propertyData, page - 1)
                );

        ItemStack addPlayer = HeadUtils.makeSkull(Lang.GUI_PROPERTY_AUTHORIZE_PLAYER.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
        GuiItem _addPlayer = ItemBuilder.from(addPlayer).asGuiItem(event -> {
            event.setCancelled(true);
            if(!propertyData.canPlayerManageInvites(playerData.getID())){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }
            OpenPlayerPropertyAddPlayerMenu(player, propertyData);
        });
        gui.setItem(nRows,4,_addPlayer);

        gui.open(player);

    }

    private static void OpenPlayerPropertyAddPlayerMenu(Player player, PropertyData propertyData) {
        Gui gui = IGUI.createChestGui("Property " + propertyData.getName(),3);

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for(Player playerIter : Bukkit.getOnlinePlayers()){
            if(playerIter.getUniqueId().equals(player.getUniqueId())){
                continue;
            }
            if(propertyData.isPlayerAuthorized(playerIter)){
                continue;
            }

            ItemStack playerHead = HeadUtils.getPlayerHead(playerIter);
            GuiItem headGui = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                propertyData.addAuthorizedPlayer(playerIter);
                OpenPlayerPropertyAddPlayerMenu(player, propertyData);
                SoundUtil.playSound(player,MINOR_GOOD);
                player.sendMessage(Lang.PLAYER_REMOVED_FROM_PROPERTY.get(playerIter.getName()));
            });
            guiItems.add(headGui);

        }

        GuiUtil.createIterator(gui, guiItems, 0, player,
                p -> OpenPlayerPropertyPlayerList(player, propertyData, 0),
                p -> OpenPlayerPropertyAddPlayerMenu(player, propertyData),
                p -> OpenPlayerPropertyAddPlayerMenu(player, propertyData)
        );

        gui.open(player);
    }

    public static void OpenPropertyBuyMenu(Player player, @NotNull PropertyData propertyData) {
        Gui gui = IGUI.createChestGui("Property " + propertyData.getName(),3);

        ItemStack propertyIcon = propertyData.getIcon();


        if(propertyData.isForRent()){
            ItemStack confirmRent = HeadUtils.makeSkull(Lang.CONFIRM_RENT.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=",
                    Lang.CONFIRM_RENT_DESC1.get(),
                    Lang.CONFIRM_RENT_DESC2.get(propertyData.getRentPrice()));
            ItemStack cancelRent = HeadUtils.makeSkull(Lang.CANCEL_RENT.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc1NDgzNjJhMjRjMGZhODQ1M2U0ZDkzZTY4YzU5NjlkZGJkZTU3YmY2NjY2YzAzMTljMWVkMWU4NGQ4OTA2NSJ9fX0=");


            GuiItem _confirmRent = ItemBuilder.from(confirmRent).asGuiItem(event -> {
                event.setCancelled(true);
                propertyData.allocateRenter(player);
                OpenPropertyManagerRentMenu(player, propertyData);
            });
            GuiItem _cancelRent = ItemBuilder.from(cancelRent).asGuiItem(event -> {
                event.setCancelled(true);
                player.closeInventory();
            });

            gui.setItem(2,3, _confirmRent);
            gui.setItem(2,7, _cancelRent);

        }
        else if (propertyData.isForSale()){
            ItemStack confirmRent = HeadUtils.makeSkull(Lang.CONFIRM_SALE.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=",
                    Lang.CONFIRM_SALE_DESC1.get(),
                    Lang.CONFIRM_SALE_DESC2.get(propertyData.getBuyingPrice()));
            ItemStack cancelRent = HeadUtils.makeSkull(Lang.CANCEL_SALE.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc1NDgzNjJhMjRjMGZhODQ1M2U0ZDkzZTY4YzU5NjlkZGJkZTU3YmY2NjY2YzAzMTljMWVkMWU4NGQ4OTA2NSJ9fX0=");

            GuiItem _confirmRent = ItemBuilder.from(confirmRent).asGuiItem(event -> {
                event.setCancelled(true);
                propertyData.buyProperty(player);
            }
            );
            GuiItem _cancelRent = ItemBuilder.from(cancelRent).asGuiItem(event -> {
                event.setCancelled(true);
                player.closeInventory();
            });


            gui.setItem(2,3, _confirmRent);
            gui.setItem(2,7, _cancelRent);
        }



        GuiItem _propertyIcon = ItemBuilder.from(propertyIcon).asGuiItem(event -> event.setCancelled(true));

        gui.setItem(1,5, _propertyIcon);

        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> player.closeInventory()));

        gui.open(player);

    }
    public static void OpenTownMenuNoTown(Player player){

        Gui gui = IGUI.createChestGui("Town",3);

        int townPrice = ConfigUtil.getCustomConfig("config.yml").getInt("townCost", 1000);

        ItemStack createTown = HeadUtils.createCustomItemStack(Material.GRASS_BLOCK,
                Lang.GUI_NO_TOWN_CREATE_NEW_TOWN.get(),
                Lang.GUI_NO_TOWN_CREATE_NEW_TOWN_DESC1.get(townPrice));
        ItemStack joinLand = HeadUtils.createCustomItemStack(Material.ANVIL,
                Lang.GUI_NO_TOWN_JOIN_A_TOWN.get(),
                Lang.GUI_NO_TOWN_JOIN_A_TOWN_DESC1.get(TownDataStorage.getNumberOfTown()));

        GuiItem _create = ItemBuilder.from(createTown).asGuiItem(event -> {
            event.setCancelled(true);
            TownUtil.registerNewTown(player,townPrice);
        });

        GuiItem _join = ItemBuilder.from(joinLand).asGuiItem(event -> {
            event.setCancelled(true);
            OpenSearchTownMenu(player,0);
        });

        gui.setItem(11, _create);
        gui.setItem(15, _join);
        gui.setItem(18, IGUI.CreateBackArrow(player,p -> OpenMainMenu(player)));

        gui.open(player);
    }
    public static void OpenSearchTownMenu(Player player, int page) {

        Gui gui = IGUI.createChestGui("Town list | page " + (page + 1),6);


        ArrayList<GuiItem> townItemStacks = new ArrayList<>();

        for(TownData townData : getTownMap().values()){
            ItemStack townIcon = townData.getIconWithInformations();
            HeadUtils.addLore(townIcon,
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
                    OpenSearchTownMenu(player,page);
                }
                if(event.isRightClick()){
                    if(!townData.isPlayerAlreadyRequested(player)){
                        return;
                    }
                    townData.removePlayerJoinRequest(player);
                    player.sendMessage(getTANString() + Lang.PLAYER_REMOVE_ASK_TO_JOIN_TOWN_PLAYER_SIDE.get());
                    OpenSearchTownMenu(player,page);
                }

            });
            townItemStacks.add(_townIteration);
        }

        createIterator(gui, townItemStacks, page, player, p -> OpenTownMenuNoTown(player),
                p -> OpenSearchTownMenu(player, page + 1),
                p -> OpenSearchTownMenu(player, page - 1));


        gui.open(player);
    }
    public static void OpenTownMenuHaveTown(Player player) {
        int nRows = 4;
        Gui gui = IGUI.createChestGui("Town",nRows);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(playerStat);

        ItemStack TownIcon = playerTown.getIconWithInformations();
        HeadUtils.addLore(TownIcon,
                Lang.GUI_TOWN_INFO_CHANGE_ICON.get()
        );

        ItemStack GoldIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_TREASURY_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=",
                Lang.GUI_TOWN_TREASURY_ICON_DESC1.get());

        ItemStack memberIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_MEMBERS_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Q0ZDQ5NmIxZGEwNzUzNmM5NGMxMzEyNGE1ODMzZWJlMGM1MzgyYzhhMzM2YWFkODQ2YzY4MWEyOGQ5MzU2MyJ9fX0=",
                Lang.GUI_TOWN_MEMBERS_ICON_DESC1.get());

        ItemStack ClaimIcon = HeadUtils.makeSkull(Lang.GUI_CLAIM_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=",
                Lang.GUI_CLAIM_ICON_DESC1.get());

        ItemStack otherTownIcon = HeadUtils.makeSkull(Lang.GUI_OTHER_TOWN_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMzc0ZTIxYjgxYzBiMjFhYmViOGU5N2UxM2UwNzdkM2VkMWVkNDRmMmU5NTZjNjhmNjNhM2UxOWU4OTlmNiJ9fX0=",
                Lang.GUI_OTHER_TOWN_ICON_DESC1.get());

        ItemStack RelationIcon = HeadUtils.makeSkull(Lang.GUI_RELATION_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUwN2Q2ZGU2MzE4MzhlN2E3NTcyMGU1YjM4ZWYxNGQyOTY2ZmRkODQ4NmU3NWQxZjY4MTJlZDk5YmJjYTQ5OSJ9fX0=",
                Lang.GUI_RELATION_ICON_DESC1.get());

        ItemStack LevelIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_LEVEL_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmJlNTI5YWI2YjJlYTdjNTBkOTE5MmQ4OWY4OThmZDdkYThhOWU3NTBkMzc4Mjk1ZGY3MzIwNWU3YTdlZWFlMCJ9fX0=",
                Lang.GUI_TOWN_LEVEL_ICON_DESC1.get());

        ItemStack SettingIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_SETTINGS_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVkMmNiMzg0NThkYTE3ZmI2Y2RhY2Y3ODcxNjE2MDJhMjQ5M2NiZjkzMjMzNjM2MjUzY2ZmMDdjZDg4YTljMCJ9fX0=",
                Lang.GUI_TOWN_SETTINGS_ICON_DESC1.get());

        ItemStack propertyIcon = HeadUtils.createCustomItemStack(Material.OAK_HANGING_SIGN, Lang.GUI_TOWN_PROPERTIES_ICON.get(),Lang.GUI_TOWN_PROPERTIES_ICON_DESC1.get());

        ItemStack landmark = HeadUtils.makeSkull(Lang.ADMIN_GUI_LANDMARK_ICON.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQ3NjFjYzE2NTYyYzg4ZDJmYmU0MGFkMzg1MDJiYzNiNGE4Nzg1OTg4N2RiYzM1ZjI3MmUzMGQ4MDcwZWVlYyJ9fX0=",
                Lang.ADMIN_GUI_LANDMARK_DESC1.get());

        ItemStack war = HeadUtils.makeSkull(Lang.GUI_ATTACK_ICON.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjVkZTRmZjhiZTcwZWVlNGQxMDNiMWVlZGY0NTRmMGFiYjlmMDU2OGY1ZjMyNmVjYmE3Y2FiNmE0N2Y5YWRlNCJ9fX0=",
                Lang.GUI_ATTACK_ICON_DESC1.get());

        GuiItem _townIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> {
            event.setCancelled(true);

            if(!playerStat.hasPermission(TOWN_ADMINISTRATOR))
                return;
            if(event.getCursor() == null)
                return;

            Material itemMaterial = event.getCursor().getType();
            if(itemMaterial == Material.AIR ){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_NO_ITEM_SHOWED.get());
            }

            else {
                playerTown.setIconMaterial(itemMaterial);
                OpenTownMenuHaveTown(player);
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_CHANGED_ICON_SUCCESS.get());
            }
        });
        GuiItem _goldIcon = ItemBuilder.from(GoldIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownEconomy(player);
        });
        GuiItem _membersIcon = ItemBuilder.from(memberIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMemberList(player);
        });
        GuiItem _claimIcon = ItemBuilder.from(ClaimIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownChunk(player);
        });
        GuiItem _otherTownIcon = ItemBuilder.from(otherTownIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTerritoryListWithRelation(player, playerTown,p -> OpenTownMenuHaveTown(player), 0);
        });
        GuiItem _relationIcon = ItemBuilder.from(RelationIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenRelations(player, playerTown, p -> dispatchPlayerTown(player));
        });
        GuiItem _levelIcon = ItemBuilder.from(LevelIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownLevel(player,0);
        });
        GuiItem _settingsIcon = ItemBuilder.from(SettingIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownSettings(player);
        });
        GuiItem _propertyIcon = ItemBuilder.from(propertyIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownPropertiesMenu(player,0);
        });
        GuiItem _landmark = ItemBuilder.from(landmark).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownOwnedLandmark(player, playerTown,0);
        });
        GuiItem _war = ItemBuilder.from(war).asGuiItem(event -> {
            event.setCancelled(true);
            OpenWarMenu(player, playerTown, p -> dispatchPlayerTown(player), 0);
        });

        gui.setItem(4, _townIcon);
        gui.setItem(2,2, _goldIcon);
        gui.setItem(2,3, _membersIcon);
        gui.setItem(2,4, _claimIcon);
        gui.setItem(2,5, _otherTownIcon);
        gui.setItem(2,6, _relationIcon);
        gui.setItem(2,7, _levelIcon);
        gui.setItem(2,8, _settingsIcon);
        gui.setItem(3,2,_propertyIcon);
        gui.setItem(3,3,_landmark);
        gui.setItem(3,4,_war);

        gui.setItem(nRows,1, IGUI.CreateBackArrow(player,p -> OpenMainMenu(player)));

        gui.open(player);
    }

    private static void OpenWarMenu(Player player, ITerritoryData territory, Consumer<Player> exit, int page) {
        Gui gui = IGUI.createChestGui("Wars | page " + (page + 1),6);
        ArrayList<GuiItem> guiItems = new ArrayList<>();

        for(String attackID : territory.getAttacksInvolvedID()){
            AttackInvolved attackInvolved = AttackInvolvedStorage.get(attackID);

            ItemStack attackIcon = attackInvolved.getIcon();
            HeadUtils.addLore(attackIcon, "", Lang.GUI_LEFT_CLICK_TO_INTERACT.get(), Lang.GUI_GENERIC_RIGHT_CLICK_TO_DELETE.get());

            GuiItem _attack = ItemBuilder.from(attackIcon).asGuiItem(event -> {
                event.setCancelled(true);
                if(event.isRightClick()){
                    if(!attackInvolved.isMainAttacker(territory)){
                        player.sendMessage(getTANString() + Lang.GUI_ATTACK_NOT_MAIN_ATTACKER.get());
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    attackInvolved.remove();
                    territory.broadCastMessageWithSound(Lang.ATTACK_SUCCESSFULLY_CANCELLED.get(attackInvolved.getMainDefender().getName()),MINOR_GOOD);
                    OpenWarMenu(player, territory, exit, page);
                }
            });
            guiItems.add(_attack);
        }

        createIterator(gui, guiItems, page, player, exit,
                p -> OpenWarMenu(player, territory, exit,page + 1),
                p -> OpenWarMenu(player, territory, exit,page - 1));

        gui.open(player);
    }

    public static void OpenStartWarSettings(Player player, Consumer<Player> exit, CreateAttackData createAttackData) {
        Gui gui = IGUI.createChestGui("War on " + createAttackData.getMainDefender().getName(),3);


        ITerritoryData mainAttacker = createAttackData.getMainAttacker();
        ITerritoryData mainDefender = createAttackData.getMainDefender();

        ItemStack addTime = HeadUtils.makeSkull(Lang.GUI_ATTACK_ADD_TIME.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjMyZmZmMTYzZTIzNTYzMmY0MDQ3ZjQ4NDE1OTJkNDZmODVjYmJmZGU4OWZjM2RmNjg3NzFiZmY2OWE2NjIifX19",
                Lang.GUI_LEFT_CLICK_FOR_1_MINUTE.get(),
                Lang.GUI_SHIFT_CLICK_FOR_1_HOUR.get());
        ItemStack removeTIme = HeadUtils.makeSkull(Lang.GUI_ATTACK_REMOVE_TIME.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE1NmRhYjUzZDRlYTFhNzlhOGU1ZWQ2MzIyYzJkNTZjYjcxNGRkMzVlZGY0Nzg3NjNhZDFhODRhODMxMCJ9fX0=",
                Lang.GUI_LEFT_CLICK_FOR_1_MINUTE.get(),
                Lang.GUI_SHIFT_CLICK_FOR_1_HOUR.get());
        ItemStack time = HeadUtils.makeSkull(Lang.GUI_ATTACK_SET_TO_START_IN.get(DateUtil.getStringDeltaDateTime(createAttackData.getDeltaDateTime())),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWU5OThmM2ExNjFhNmM5ODlhNWQwYTFkYzk2OTMxYTM5OTI0OWMwODBiNjYzNjQ1ODFhYjk0NzBkZWE1ZTcyMCJ9fX0=",
                Lang.GUI_LEFT_CLICK_FOR_1_MINUTE.get(),
                Lang.GUI_SHIFT_CLICK_FOR_1_HOUR.get());
        ItemStack confirm = HeadUtils.makeSkull(Lang.GUI_CONFIRM_ATTACK.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNhNDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIwZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=",
                Lang.GUI_CONFIRM_ATTACK_DESC1.get(mainDefender.getColoredName()));

        if(!createAttackData.getWargoal().isCompleted()){
            HeadUtils.addLore(confirm, Lang.GUI_WARGOAL_NOT_COMPLETED.get());
        }

        ItemStack wargoal = createAttackData.getWargoal().getIcon();


        GuiItem _addTime = ItemBuilder.from(addTime).asGuiItem(event -> {
            event.setCancelled(true);
            SoundUtil.playSound(player, ADD);
            if(event.isShiftClick()){
                createAttackData.addDeltaDateTime(60 * 1200);
            }
            else if(event.isLeftClick()){
                createAttackData.addDeltaDateTime(1200);
            }
            OpenStartWarSettings(player, exit, createAttackData);
        });

        GuiItem _removeTime = ItemBuilder.from(removeTIme).asGuiItem(event -> {
            event.setCancelled(true);
            SoundUtil.playSound(player, REMOVE);

            if(event.isShiftClick()){
                createAttackData.addDeltaDateTime(-60 * 1200);
            }
            else if(event.isLeftClick()){
                createAttackData.addDeltaDateTime(-1200);
            }
            OpenStartWarSettings(player, exit, createAttackData);
        });

        GuiItem _time = ItemBuilder.from(time).asGuiItem(event -> {
            event.setCancelled(true);
        });

        GuiItem _wargoal = ItemBuilder.from(wargoal).asGuiItem(event -> {
            OpenSelectWarGoalMenu(player, exit,  createAttackData);
            event.setCancelled(true);
        });

        GuiItem _confirm = ItemBuilder.from(confirm).asGuiItem(event -> {
            event.setCancelled(true);

            if(!createAttackData.getWargoal().isCompleted()){
                player.sendMessage(getTANString() + Lang.GUI_WARGOAL_NOT_COMPLETED.get());
                return;
            }

            AttackInvolvedStorage.newWar(createAttackData);
            OpenWarMenu(player, mainAttacker, exit, 0);

            player.sendMessage(getTANString() + Lang.GUI_TOWN_ATTACK_TOWN_EXECUTED.get(mainDefender.getName()));
            mainAttacker.broadCastMessageWithSound(Lang.GUI_TOWN_ATTACK_TOWN_INFO.get(mainAttacker.getName(), mainDefender.getName()), WAR);
            mainDefender.broadCastMessageWithSound(Lang.GUI_TOWN_ATTACK_TOWN_INFO.get(mainAttacker.getName(), mainDefender.getName()), WAR);
        });


        gui.setItem(2,2,_removeTime);
        gui.setItem(2,3,_time);
        gui.setItem(2,4,_addTime);

        gui.setItem(2,6,_wargoal);

        gui.setItem(2,8,_confirm);
        gui.setItem(3,1,IGUI.CreateBackArrow(player, e -> openSingleRelation(player, mainAttacker, TownRelation.WAR,0, exit)));

        createAttackData.getWargoal().addExtraOptions(gui, player, createAttackData,exit);

        gui.open(player);

    }

    public static void openSelecteTerritoryToLiberate(Player player, CreateAttackData createAttackData, LiberateWarGoal liberateWarGoal, Consumer<Player> exit) {

        Gui gui = IGUI.createChestGui("War on " + createAttackData.getMainDefender().getName(),6);

        ITerritoryData territoryToAttack = createAttackData.getMainDefender();
        for(ITerritoryData territoryData : territoryToAttack.getSubjects()){
            if(territoryData.isCapital()){
                continue;
            }
            ItemStack territoryIcon = territoryData.getIconWithInformations();
            HeadUtils.addLore(territoryIcon, "", Lang.LEFT_CLICK_TO_SELECT.get());

            GuiItem _territory = ItemBuilder.from(territoryIcon).asGuiItem(event -> {
                event.setCancelled(true);
                liberateWarGoal.setTerritoryToLiberate(territoryData);
                //createAttackData.setWargoal(liberateWarGoal); OOP test
                OpenStartWarSettings(player, exit, createAttackData);
            });

            gui.addItem(_territory);
        }

        gui.setItem(6,1,IGUI.CreateBackArrow(player, e -> OpenStartWarSettings(player, exit, createAttackData)));
        gui.open(player);

    }

    private static void OpenSelectWarGoalMenu(Player player, Consumer<Player> exit, CreateAttackData createAttackData) {
        Gui gui = IGUI.createChestGui("Select wargoals", 3);

        boolean canBeSubjugated = createAttackData.canBeSubjugated();
        boolean canBeLiberated = !(createAttackData.getMainDefender() instanceof TownData);

        ItemStack conquer = HeadUtils.createCustomItemStack(Material.IRON_SWORD, Lang.CONQUER_WAR_GOAL.get(),
                Lang.CONQUER_WAR_GOAL_DESC.get(),
                Lang.LEFT_CLICK_TO_SELECT.get());
        ItemStack subjugate = HeadUtils.createCustomItemStack(Material.CHAIN, Lang.SUBJUGATE_WAR_GOAL.get(),
                Lang.GUI_WARGOAL_SUBJUGATE_WAR_GOAL_RESULT.get(createAttackData.getMainDefender().getName(), createAttackData.getMainAttacker().getName()));

        if(!canBeSubjugated)
            HeadUtils.addLore(subjugate, Lang.GUI_WARGOAL_SUBJUGATE_CANNOT_BE_USED.get());
        else
            HeadUtils.addLore(subjugate, Lang.LEFT_CLICK_TO_SELECT.get());

        ItemStack liberate = HeadUtils.createCustomItemStack(Material.LANTERN, Lang.LIBERATE_SUBJECT_WAR_GOAL.get(),
                Lang.LIBERATE_SUBJECT_WAR_GOAL_DESC.get());

        if(!canBeLiberated)
            HeadUtils.addLore(liberate, Lang.GUI_WARGOAL_LIBERATE_CANNOT_BE_USED.get());
        else
            HeadUtils.addLore(liberate, Lang.LEFT_CLICK_TO_SELECT.get());


        GuiItem _conquer = ItemBuilder.from(conquer).asGuiItem(event -> {
            event.setCancelled(true);
            createAttackData.setWargoal(new ConquerWarGoal());
            OpenStartWarSettings(player, exit, createAttackData);
        });

        GuiItem _subjugate = ItemBuilder.from(subjugate).asGuiItem(event -> {
            event.setCancelled(true);
            if(!canBeSubjugated){
                player.sendMessage(getTANString() + Lang.GUI_WARGOAL_SUBJUGATE_CANNOT_BE_USED.get());
                return;
            }
            createAttackData.setWargoal(new SubjugateWarGoal());
            OpenStartWarSettings(player, exit, createAttackData);
        });

        GuiItem _liberate = ItemBuilder.from(liberate).asGuiItem(event -> {
            event.setCancelled(true);

            if(!canBeLiberated){
                player.sendMessage(getTANString() + Lang.GUI_WARGOAL_LIBERATE_CANNOT_BE_USED.get());
                return;
            }
            createAttackData.setWargoal(new LiberateWarGoal());
            OpenStartWarSettings(player, exit, createAttackData);
        });

        gui.setItem(2,3,_conquer);
        gui.setItem(2,5,_subjugate);
        gui.setItem(2,7,_liberate);

        gui.setItem(3,1,IGUI.CreateBackArrow(player, e -> OpenStartWarSettings(player, exit, createAttackData)));

        gui.open(player);
    }

    private static void OpenTownOwnedLandmark(Player player, TownData townData, int page) {
        Gui gui = IGUI.createChestGui("Town owned landmark | page " + (page + 1),6);

        ArrayList<GuiItem> landmarkGui = new ArrayList<>();

        for(String landmarkID : townData.getOwnedLandmarks()){
            Landmark landmarkData = LandmarkStorage.get(landmarkID);

            GuiItem _landmarkIcon = ItemBuilder.from(landmarkData.getIcon()).asGuiItem(event -> event.setCancelled(true));
            landmarkGui.add(_landmarkIcon);
        }
        GuiUtil.createIterator(gui, landmarkGui, page, player,
                p -> OpenTownMenuHaveTown(player),
                p -> OpenTownOwnedLandmark(player, townData, page + 1),
                p -> OpenTownOwnedLandmark(player, townData, page - 1)
        );

        gui.open(player);

    }

    public static void OpenTerritoryListWithRelation(Player player, ITerritoryData territoryData, Consumer<Player> exitMenu, int page) {
        Gui gui = IGUI.createChestGui("Town list | page " + (page + 1),6);

        ArrayList<GuiItem> townGuiItems = new ArrayList<>();


        List<ITerritoryData> territoryList = new ArrayList<>();
        territoryList.addAll(getTownMap().values());
        territoryList.addAll(RegionDataStorage.getAllRegions());

        //territoryList.removeIf(t -> t.getID().equals(territoryData.getID())); //Remove self

        for(ITerritoryData townData : territoryList){
            ItemStack territoryIcon = townData.getIconWithInformationAndRelation(territoryData);
            GuiItem territoryGUI = ItemBuilder.from(territoryIcon).asGuiItem(event -> event.setCancelled(true));

            townGuiItems.add(territoryGUI);
        }

        createIterator(gui, townGuiItems, page, player, exitMenu,
                p -> OpenTerritoryListWithRelation(player, territoryData, exitMenu, page + 1),
                p -> OpenTerritoryListWithRelation(player, territoryData, exitMenu, page - 1));

        gui.open(player);
    }
    public static void OpenTownMemberList(Player player) {

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(playerStat);

        int rowSize = Math.min(playerTown.getPlayerIDList().size() / 9 + 3,6);

        Gui gui = IGUI.createChestGui("Town",rowSize);



        int i = 0;
        for (String playerUUID: playerTown.getPlayerIDList()) {

            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            PlayerData playerIterateData = PlayerDataStorage.get(playerUUID);

            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate,
                    Lang.GUI_TOWN_MEMBER_DESC1.get(playerIterateData.getTownRank().getColoredName()),
                    Lang.GUI_TOWN_MEMBER_DESC2.get(EconomyUtil.getBalance(playerIterate)),
                    playerStat.hasPermission(KICK_PLAYER) ? Lang.GUI_TOWN_MEMBER_DESC3.get() : "");

            GuiItem _playerIcon = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                if(event.getClick() == ClickType.RIGHT){
                    event.setCancelled(true);

                    PlayerData playerData = PlayerDataStorage.get(player);
                    PlayerData kickedPlayerData = PlayerDataStorage.get(playerIterate);
                    TownData townData = TownDataStorage.get(playerData);


                    if(!playerData.hasPermission(KICK_PLAYER)){
                        player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                        return;
                    }
                    int playerLevel = townData.getRank(playerData).getLevel();
                    int kickedPlayerLevel = townData.getRank(kickedPlayerData).getLevel();
                    if(playerLevel >= kickedPlayerLevel && !playerData.isTownLeader()){
                        player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get());
                        return;
                    }
                    if(kickedPlayerData.isTownLeader()){
                        player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_LEADER.get());
                        return;
                    }
                    if(playerData.getID().equals(kickedPlayerData.getID())){
                        player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_YOURSELF.get());
                        return;
                    }

                    OpenConfirmMenu(player, Lang.CONFIRM_PLAYER_KICKED.get(playerIterate.getName()),
                            confirmAction -> playerTown.kickPlayer(playerIterate),
                            p -> OpenTownMemberList(player));
                }
                OpenTownMemberList(player);
            });

            gui.setItem(i, _playerIcon);
            i++;
        }

        ItemStack manageRanks = HeadUtils.createCustomItemStack(Material.LADDER, Lang.GUI_TOWN_MEMBERS_MANAGE_ROLES.get());
        ItemStack manageApplication = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK,
                Lang.GUI_TOWN_MEMBERS_MANAGE_APPLICATION.get(),
                Lang.GUI_TOWN_MEMBERS_MANAGE_APPLICATION_DESC1.get(playerTown.getPlayerJoinRequestSet().size())
        );

        GuiItem _manageRanks = ItemBuilder.from(manageRanks).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRanks(player);
        });
        GuiItem _manageApplication = ItemBuilder.from(manageApplication).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownApplications(player);
        });

        GuiItem _panel = ItemBuilder.from(Material.LIME_STAINED_GLASS_PANE).asGuiItem(event -> event.setCancelled(true));

        gui.setItem(rowSize,1, IGUI.CreateBackArrow(player,p -> dispatchPlayerTown(player)));
        gui.setItem(rowSize,2,_panel);
        gui.setItem(rowSize,3, _manageRanks);
        gui.setItem(rowSize,4, _manageApplication);
        gui.setItem(rowSize,5,_panel);
        gui.setItem(rowSize,6,_panel);
        gui.setItem(rowSize,7,_panel);
        gui.setItem(rowSize,8,_panel);
        gui.setItem(rowSize,9,_panel);



        gui.open(player);

    }
    public static void OpenTownApplications(Player player) {


        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData town = TownDataStorage.get(playerStat);

        int rowSize = Math.min(town.getPlayerJoinRequestSet().size() / 9 + 3,6);

        Gui gui = IGUI.createChestGui("Town",rowSize);

        HashSet<String> players = town.getPlayerJoinRequestSet();

        int i = 0;
        for (String playerUUID: players) {

            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            PlayerData playerIterateData = PlayerDataStorage.get(playerUUID);

            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate,
                    Lang.GUI_PLAYER_ASK_JOIN_PROFILE_DESC2.get(),
                    Lang.GUI_PLAYER_ASK_JOIN_PROFILE_DESC3.get());

            GuiItem _playerIcon = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                if(event.isLeftClick()){

                    if(!playerStat.hasPermission(TownRolePermission.INVITE_PLAYER)){
                        player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    if(town.isFull()){
                        player.sendMessage(getTANString() + Lang.INVITATION_TOWN_FULL.get());
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }

                    town.addPlayer(playerIterateData);

                    Player playerIterateOnline = playerIterate.getPlayer();
                    if(playerIterateOnline != null)
                        playerIterateOnline.sendMessage(getTANString() + Lang.TOWN_INVITATION_ACCEPTED_MEMBER_SIDE.get(town.getName()));

                    town.broadCastMessageWithSound(
                            Lang.TOWN_INVITATION_ACCEPTED_TOWN_SIDE.get(playerIterateData.getName()),
                            MINOR_GOOD);

                    updateAllScoreboardColor();

                    for (TownData allTown : TownDataStorage.getTownMap().values()){
                        allTown.removePlayerJoinRequest(playerIterateData.getID());
                    }

                    player.sendMessage(getTANString() + Lang.PLAYER_REMOVE_ASK_TO_JOIN_TOWN_PLAYER_SIDE.get());

                }
                if(event.isRightClick()){
                    if(!playerStat.hasPermission(TownRolePermission.INVITE_PLAYER)){
                        player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                        return;
                    }

                    town.removePlayerJoinRequest(playerIterateData.getID());
                    player.sendMessage(getTANString() + Lang.PLAYER_REMOVE_ASK_TO_JOIN_TOWN_PLAYER_SIDE.get());
                }
                OpenTownMemberList(player);
            });

            gui.setItem(i, _playerIcon);
            i++;
        }
        ItemStack itemStack = HeadUtils.createCustomItemStack(Material.LIME_STAINED_GLASS_PANE,"");
        GuiItem _panel = ItemBuilder.from(itemStack).asGuiItem(event -> event.setCancelled(true));


        gui.setItem(rowSize,1, IGUI.CreateBackArrow(player,p -> OpenTownMemberList(player)));
        gui.setItem(rowSize,2,_panel);
        gui.setItem(rowSize,3,_panel);
        gui.setItem(rowSize,4,_panel);
        gui.setItem(rowSize,5,_panel);
        gui.setItem(rowSize,6,_panel);
        gui.setItem(rowSize,7,_panel);
        gui.setItem(rowSize,8,_panel);
        gui.setItem(rowSize,9,_panel);



        gui.open(player);

    }
    public static void OpenTownRanks(Player player) {

        int row = 3;
        Gui gui = IGUI.createChestGui("Town",row);

        PlayerData playerData = PlayerDataStorage.get(player);
        TownData town = TownDataStorage.get(playerData);

        int i = 0;
        for (TownRank townRank: town.getRanks()) {

            Material townMaterial = Material.getMaterial(townRank.getRankIconName());
            ItemStack townRankItemStack = HeadUtils.createCustomItemStack(townMaterial, townRank.getColoredName());
            GuiItem _townRankItemStack = ItemBuilder.from(townRankItemStack).asGuiItem(event -> {
                event.setCancelled(true);
                if(!playerData.hasPermission(TownRolePermission.MANAGE_RANKS)) {
                    player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                    return;
                }
                if(town.getRank(playerData).getLevel() >= townRank.getLevel() && !town.isLeader(player)){
                    player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get());
                    return;
                }
                OpenTownRankManager(player,townRank.getID());
            });
            gui.setItem(i, _townRankItemStack);
            i = i+1;
        }

        ItemStack createNewRole = HeadUtils.makeSkull(Lang.GUI_TOWN_MEMBERS_ADD_NEW_ROLES.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTBjOTdlNGI2OGFhYWFlODQ3MmUzNDFiMWQ4NzJiOTNiMzZkNGViNmVhODllY2VjMjZhNjZlNmM0ZTE3OCJ9fX0=");
        GuiItem _createNewRole = ItemBuilder.from(createNewRole).asGuiItem(event -> {
            event.setCancelled(true);

            if(!playerData.hasPermission(TownRolePermission.CREATE_RANK)) {
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }
            if(town.getNumberOfRank() >= ConfigUtil.getCustomConfig("config.yml").getInt("townMaxRank",8)){
                player.sendMessage(getTANString() + Lang.TOWN_RANK_CAP_REACHED.get());
                return;
            }
            PlayerChatListenerStorage.addPlayer(RANK_CREATION,player);
            player.sendMessage(getTANString() + Lang.WRITE_IN_CHAT_NEW_ROLE_NAME.get());
            player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
            player.closeInventory();
        });


        gui.setItem(row,1, IGUI.CreateBackArrow(player,p -> OpenTownMemberList(player)));
        gui.setItem(row,3, _createNewRole);

        gui.open(player);

    }
    public static void OpenTownRankManager(Player player, int rankID) {

        TownData townData = TownDataStorage.get(player);
        TownRank townRank = townData.getRank(rankID);

        Gui gui = IGUI.createChestGui("Town - Rank " + townRank.getName(),4);


        boolean isDefaultRank = Objects.equals(townRank.getID(), townData.getTownDefaultRankID());

        ItemStack roleIcon = HeadUtils.createCustomItemStack(
                Material.getMaterial(townRank.getRankIconName()),
                Lang.GUI_BASIC_NAME.get(townRank.getColoredName()),
                Lang.GUI_TOWN_MEMBERS_ROLE_NAME_DESC1.get());

        ItemStack roleRankIcon = townRank.getRankEnum().getRankGuiIcon();

        ArrayList<String> playerNames = new ArrayList<>();
        playerNames.add(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO_DESC1.get());
        for (String playerUUID : townRank.getPlayers(townData.getID())) {
            String playerName = PlayerDataStorage.get(playerUUID).getName();
            playerNames.add("-" + Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO_DESC.get(playerName));
        }
        ItemStack membersRank = HeadUtils.makeSkull(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I0M2IyMzE4OWRjZjEzMjZkYTQyNTNkMWQ3NTgyZWY1YWQyOWY2YzI3YjE3MWZlYjE3ZTMxZDA4NGUzYTdkIn19fQ==",
                playerNames);

        ItemStack managePermission = HeadUtils.createCustomItemStack(Material.ANVIL,Lang.GUI_TOWN_MEMBERS_ROLE_MANAGE_PERMISSION.get());
        ItemStack renameRank = HeadUtils.createCustomItemStack(Material.NAME_TAG,Lang.GUI_TOWN_MEMBERS_ROLE_CHANGE_NAME.get());
        ItemStack changeRoleTaxRelation = HeadUtils.createCustomItemStack(
                Material.GOLD_NUGGET,
                townRank.isPayingTaxes() ? Lang.GUI_TOWN_MEMBERS_ROLE_PAY_TAXES.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NOT_PAY_TAXES.get(),
                Lang.GUI_TOWN_MEMBERS_ROLE_TAXES_DESC1.get()
        );

        ItemStack makeRankDefault = HeadUtils.createCustomItemStack(Material.RED_BED,
                isDefaultRank ? Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_IS_DEFAULT.get() : Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_IS_NOT_DEFAULT.get(),
                Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT1.get(),
                isDefaultRank ? "" : Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT2.get());

        ItemStack removeRank = HeadUtils.createCustomItemStack(Material.BARRIER, Lang.GUI_TOWN_MEMBERS_ROLE_DELETE.get());

        ItemStack salary = HeadUtils.createCustomItemStack(Material.GOLD_INGOT,
                Lang.GUI_TOWN_MEMBERS_ROLE_SALARY.get(),
                Lang.GUI_TOWN_MEMBERS_ROLE_SALARY_DESC1.get(townRank.getSalary()));

        ItemStack lowerSalary = HeadUtils.makeSkull(Lang.GUI_TREASURY_LOWER_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=",
                Lang.GUI_DECREASE_1_DESC.get(),
                Lang.GUI_DECREASE_10_DESC.get());
        ItemStack increaseSalary = HeadUtils.makeSkull(Lang.GUI_TREASURY_INCREASE_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                Lang.GUI_INCREASE_1_DESC.get(),
                Lang.GUI_INCREASE_10_DESC.get());

        GuiItem _roleIcon = ItemBuilder.from(roleIcon).asGuiItem(event -> {
            event.setCancelled(true);

            if(event.getCursor() == null)
                return;

            if(event.getCursor().getData() != null)
                return;
            Material itemMaterial = event.getCursor().getData().getItemType();
            if(itemMaterial == Material.AIR){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_NO_ITEM_SHOWED.get());
            }
            else {
                townRank.setRankIconName(townData.getID(), itemMaterial.toString());
                OpenTownRankManager(player, rankID);
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_CHANGED_ICON_SUCCESS.get());
            }
        });

        GuiItem _roleRankIcon = ItemBuilder.from(roleRankIcon).asGuiItem(event -> {
            townRank.incrementLevel(townData.getID());
            OpenTownRankManager(player, rankID);
            event.setCancelled(true);
        });
        GuiItem _managePermission = ItemBuilder.from(managePermission).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRankManagerPermissions(player,rankID);
        });
        GuiItem _membersRank = ItemBuilder.from(membersRank).asGuiItem(event -> {
            OpenTownRankManagerAddPlayer(player,rankID);
            event.setCancelled(true);
        });
        GuiItem _renameRank = ItemBuilder.from(renameRank).asGuiItem(event -> {

            player.sendMessage(getTANString() + Lang.WRITE_IN_CHAT_NEW_ROLE_NAME.get());
            player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
            player.closeInventory();

            HashMap<MessageKey, String> newMap = new HashMap<>();
            newMap.put(RANK_ID, String.valueOf(rankID));
            PlayerChatListenerStorage.addPlayer(RANK_RENAME,player,newMap);
            event.setCancelled(true);
        });
        GuiItem _changeRoleTaxRelation = ItemBuilder.from(changeRoleTaxRelation).asGuiItem(event -> {
            townRank.swapPayingTaxes(townData.getID());
            OpenTownRankManager(player,rankID);
            event.setCancelled(true);
        });
        GuiItem _makeRankDefault = ItemBuilder.from(makeRankDefault).asGuiItem(event -> {
            event.setCancelled(true);

            if(isDefaultRank){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_ALREADY_DEFAULT.get());
            }
            else{
                townData.setTownDefaultRankID(rankID);
                OpenTownRankManager(player,rankID);
            }
        });

        GuiItem _removeRank = ItemBuilder.from(removeRank).asGuiItem(event -> {
            event.setCancelled(true);

            if(townRank.getNumberOfPlayer(townData.getID()) != 0){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_DELETE_ERROR_NOT_EMPTY.get());
            }
            else if(townData.getTownDefaultRankID() == rankID){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_DELETE_ERROR_DEFAULT.get());
            }
            else{
                townData.removeRank(townRank.getID());
                OpenTownRanks(player);
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

            townRank.removeFromSalary(townData.getID(), amountToRemove);
            SoundUtil.playSound(player, REMOVE);
            OpenTownRankManager(player, rankID);
        });
        GuiItem _IncreaseSalary = ItemBuilder.from(increaseSalary).asGuiItem(event -> {

            event.setCancelled(true);

            int amountToAdd = event.isShiftClick() ? 10 : 1;

            townRank.addFromSalary(townData.getID(), amountToAdd);
            SoundUtil.playSound(player, ADD);
            OpenTownRankManager(player, rankID);
        });

        GuiItem _salary = ItemBuilder.from(salary).asGuiItem(event -> event.setCancelled(true));

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
    public static void OpenTownRankManagerAddPlayer(Player player, int rankID) {

        Gui gui = IGUI.createChestGui("Town",3);


        TownData town = TownDataStorage.get(player);
        TownRank townRank = town.getRank(rankID);
        int i = 0;

        for (String otherPlayerUUID : town.getPlayerIDList()) {
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

                town.setPlayerRank(playerStat, rankID);

                OpenTownRankManager(player, rankID);
            });

            gui.setItem(i, _playerHead);
            i = i + 1;
        }
        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenTownRankManager(player,rankID)));

        gui.open(player);
    }
    public static void OpenTownRankManagerPermissions(Player player, int rankID) {

        Gui gui = IGUI.createChestGui("Town",3);

        TownData town = TownDataStorage.get(player);
        String townID = town.getID();
        TownRank townRank = town.getRank(rankID);


        ItemStack manage_taxes = HeadUtils.createCustomItemStack(Material.GOLD_INGOT, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_TAXES.get(),(townRank.hasPermission(townID,MANAGE_TAXES)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack promote_rank_player = HeadUtils.createCustomItemStack(Material.EMERALD, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_PROMOTE_RANK_PLAYER.get(),(townRank.hasPermission(townID,PROMOTE_RANK_PLAYER)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack derank_player = HeadUtils.createCustomItemStack(Material.REDSTONE, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_DERANK_RANK_PLAYER.get(),(townRank.hasPermission(townID,DERANK_RANK_PLAYER)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack claim_chunk = HeadUtils.createCustomItemStack(Material.EMERALD_BLOCK, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_CLAIM_CHUNK.get(),(townRank.hasPermission(townID,CLAIM_CHUNK)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack unclaim_chunk = HeadUtils.createCustomItemStack(Material.REDSTONE_BLOCK, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_UNCLAIM_CHUNK.get(),(townRank.hasPermission(townID,UNCLAIM_CHUNK)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack upgrade_town = HeadUtils.createCustomItemStack(Material.SPECTRAL_ARROW, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_UPGRADE_TOWN.get(),(townRank.hasPermission(townID,UPGRADE_TOWN)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack invite_player = HeadUtils.createCustomItemStack(Material.SKELETON_SKULL, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_INVITE_PLAYER.get(),(townRank.hasPermission(townID,INVITE_PLAYER)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack kick_player = HeadUtils.createCustomItemStack(Material.CREEPER_HEAD, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_KICK_PLAYER.get(),(townRank.hasPermission(townID,KICK_PLAYER)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack create_rank = HeadUtils.createCustomItemStack(Material.LADDER, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_CREATE_RANK.get(),(townRank.hasPermission(townID,CREATE_RANK)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack delete_rank = HeadUtils.createCustomItemStack(Material.CHAIN, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_DELETE_RANK.get(),(townRank.hasPermission(townID,DELETE_RANK)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack modify_rank = HeadUtils.createCustomItemStack(Material.STONE_PICKAXE, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MODIFY_RANK.get(),(townRank.hasPermission(townID,MANAGE_RANKS)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack manage_claim_settings = HeadUtils.createCustomItemStack(Material.GRASS_BLOCK, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_CLAIM_SETTINGS.get(),(townRank.hasPermission(townID,MANAGE_CLAIM_SETTINGS)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack manage_town_relation = HeadUtils.createCustomItemStack(Material.FLOWER_POT, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_TOWN_RELATION.get(),(townRank.hasPermission(townID,MANAGE_TOWN_RELATION)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack manage_mob_spawn = HeadUtils.createCustomItemStack(Material.CREEPER_HEAD, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_MOB_SPAWN.get(),(townRank.hasPermission(townID,MANAGE_MOB_SPAWN)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack create_property = HeadUtils.createCustomItemStack(Material.OAK_HANGING_SIGN, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_CREATE_PROPERTY.get(),(townRank.hasPermission(townID,CREATE_PROPERTY)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack manage_property = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_PROPERTY.get(),(townRank.hasPermission(townID,MANAGE_PROPERTY)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack town_administrator = HeadUtils.createCustomItemStack(Material.DIAMOND, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_PROPERTY.get(),(townRank.hasPermission(townID,TOWN_ADMINISTRATOR)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());

        GuiItem _manage_taxes = ItemBuilder.from(manage_taxes).asGuiItem(event -> {
            townRank.switchPermission(town.getID(), MANAGE_TAXES);
            OpenTownRankManagerPermissions(player, rankID);
            event.setCancelled(true);
        });
        GuiItem _promote_rank_player = ItemBuilder.from(promote_rank_player).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),PROMOTE_RANK_PLAYER);
            OpenTownRankManagerPermissions(player, rankID);
            event.setCancelled(true);
        });
        GuiItem _derank_player = ItemBuilder.from(derank_player).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),DERANK_RANK_PLAYER);
            OpenTownRankManagerPermissions(player, rankID);
            event.setCancelled(true);
        });
        GuiItem _claim_chunk = ItemBuilder.from(claim_chunk).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),CLAIM_CHUNK);
            OpenTownRankManagerPermissions(player, rankID);
            event.setCancelled(true);
        });
        GuiItem _unclaim_chunk = ItemBuilder.from(unclaim_chunk).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),UNCLAIM_CHUNK);
            OpenTownRankManagerPermissions(player, rankID);
            event.setCancelled(true);
        });
        GuiItem _upgrade_town = ItemBuilder.from(upgrade_town).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),UPGRADE_TOWN);
            OpenTownRankManagerPermissions(player, rankID);
            event.setCancelled(true);
        });
        GuiItem _invite_player = ItemBuilder.from(invite_player).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),INVITE_PLAYER);
            OpenTownRankManagerPermissions(player, rankID);
            event.setCancelled(true);
        });
        GuiItem _kick_player = ItemBuilder.from(kick_player).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),KICK_PLAYER);
            OpenTownRankManagerPermissions(player, rankID);

            event.setCancelled(true);
        });
        GuiItem _create_rank = ItemBuilder.from(create_rank).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),CREATE_RANK);
            OpenTownRankManagerPermissions(player, rankID);

            event.setCancelled(true);
        });
        GuiItem _delete_rank = ItemBuilder.from(delete_rank).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),DELETE_RANK);
            OpenTownRankManagerPermissions(player, rankID);
            event.setCancelled(true);
        });
        GuiItem _modify_rank = ItemBuilder.from(modify_rank).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),MANAGE_RANKS);
            OpenTownRankManagerPermissions(player, rankID);
            event.setCancelled(true);
        });
        GuiItem _manage_claim_settings = ItemBuilder.from(manage_claim_settings).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),MANAGE_CLAIM_SETTINGS);
            OpenTownRankManagerPermissions(player, rankID);
            event.setCancelled(true);
        });
        GuiItem _manage_town_relation = ItemBuilder.from(manage_town_relation).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),MANAGE_TOWN_RELATION);
            OpenTownRankManagerPermissions(player, rankID);
            event.setCancelled(true);
        });
        GuiItem _manage_mob_spawn = ItemBuilder.from(manage_mob_spawn).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),MANAGE_MOB_SPAWN);
            OpenTownRankManagerPermissions(player, rankID);
            event.setCancelled(true);
        });
        GuiItem _create_property = ItemBuilder.from(create_property).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),CREATE_PROPERTY);
            OpenTownRankManagerPermissions(player, rankID);
            event.setCancelled(true);
        });
        GuiItem _manage_property = ItemBuilder.from(manage_property).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),MANAGE_PROPERTY);
            OpenTownRankManagerPermissions(player, rankID);
            event.setCancelled(true);
        });
        GuiItem _town_administrator = ItemBuilder.from(town_administrator).asGuiItem(event -> {
            townRank.switchPermission(town.getID(),TOWN_ADMINISTRATOR);
            OpenTownRankManagerPermissions(player, rankID);
            event.setCancelled(true);
        });


        gui.addItem(_manage_taxes);
        gui.addItem(_promote_rank_player);
        gui.addItem(_derank_player);
        gui.addItem(_claim_chunk);
        gui.addItem( _unclaim_chunk);
        gui.addItem(_upgrade_town);
        gui.addItem(_invite_player);
        gui.addItem(_kick_player);
        gui.addItem(_create_rank);
        gui.addItem(_delete_rank);
        gui.addItem(_modify_rank);
        gui.addItem(_manage_claim_settings);
        gui.addItem(_manage_town_relation);
        gui.addItem(_manage_mob_spawn);
        gui.addItem(_create_property);
        gui.addItem(_manage_property);
        gui.addItem(_town_administrator);


        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenTownRankManager(player,rankID)));

        gui.open(player);

    }
    public static void OpenTownEconomy(Player player) {

        Gui gui = IGUI.createChestGui("Town",4);


        TownData town = TownDataStorage.get(player);
        PlayerData playerStat = PlayerDataStorage.get(player);

        // Chunk upkeep
        int numberClaimedChunk = town.getNumberOfClaimedChunk();
        float upkeepCost = ConfigUtil.getCustomConfig("config.yml").getInt("TownChunkUpkeepCost");
        float totalUpkeep = numberClaimedChunk * upkeepCost/10;
        int totalSalary = town.getTotalSalaryCost();
        int regionalTax =  town.getRegionTaxRate();

        ItemStack goldIcon = HeadUtils.makeSkull(Lang.GUI_TREASURY_STORAGE.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=",
                Lang.GUI_TREASURY_STORAGE_DESC1.get(town.getBalance()),
                Lang.GUI_TREASURY_STORAGE_DESC2.get(town.computeNextRevenue()));

        ItemStack goldSpendingIcon = HeadUtils.makeSkull(Lang.GUI_TREASURY_SPENDING.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=",
                Lang.GUI_TREASURY_SPENDING_DESC1.get(totalSalary + totalUpkeep + regionalTax),
                Lang.GUI_TREASURY_SPENDING_DESC2.get(totalSalary),
                Lang.GUI_TREASURY_SPENDING_DESC3.get(totalUpkeep),
                Lang.GUI_TREASURY_SPENDING_DESC4.get(regionalTax));

        ItemStack lowerTax = HeadUtils.makeSkull(Lang.GUI_TREASURY_LOWER_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=",
                Lang.GUI_DECREASE_1_DESC.get(),
                Lang.GUI_DECREASE_10_DESC.get());
        ItemStack increaseTax = HeadUtils.makeSkull(Lang.GUI_TREASURY_INCREASE_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                Lang.GUI_INCREASE_1_DESC.get(),
                Lang.GUI_INCREASE_10_DESC.get());
        ItemStack taxInfo = HeadUtils.makeSkull(Lang.GUI_TREASURY_FLAT_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTk4ZGY0MmY0NzdmMjEzZmY1ZTlkN2ZhNWE0Y2M0YTY5ZjIwZDljZWYyYjkwYzRhZTRmMjliZDE3Mjg3YjUifX19",
                Lang.GUI_TREASURY_FLAT_TAX_DESC1.get(town.getFlatTax()));
        ItemStack taxHistory = HeadUtils.makeSkull(Lang.GUI_TREASURY_TAX_HISTORY.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmU1OWYyZDNiOWU3ZmI5NTBlOGVkNzkyYmU0OTIwZmI3YTdhOWI5MzQ1NjllNDQ1YjJiMzUwM2ZlM2FiOTAyIn19fQ==",
                town.getTaxHistory().get(5), Lang.GUI_GENERIC_CLICK_TO_OPEN.get());
        ItemStack salarySpending = HeadUtils.makeSkull(Lang.GUI_TREASURY_SALARY_HISTORY.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjlhNjAwYWIwYTgzMDk3MDY1Yjk1YWUyODRmODA1OTk2MTc3NDYwOWFkYjNkYmQzYTRjYTI2OWQ0NDQwOTU1MSJ9fX0=",
                Lang.GUI_TREASURY_SALARY_HISTORY_DESC1.get(totalSalary));
        ItemStack chunkSpending = HeadUtils.makeSkull(Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=",
                Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC1.get(totalUpkeep),
                Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC2.get(upkeepCost),
                Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC3.get(numberClaimedChunk));
        ItemStack miscSpending = HeadUtils.makeSkull(Lang.GUI_TREASURY_MISCELLANEOUS_SPENDING.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGMzNjA0NTIwOGY5YjVkZGNmOGM0NDMzZTQyNGIxY2ExN2I5NGY2Yjk2MjAyZmIxZTUyNzBlZThkNTM4ODFiMSJ9fX0=",
                Lang.GUI_TREASURY_MISCELLANEOUS_SPENDING_DESC1.get());
        HeadUtils.setLore(miscSpending, town.getMiscellaneousHistory().get(5), Lang.GUI_GENERIC_CLICK_TO_OPEN.get());
        ItemStack donation = HeadUtils.createCustomItemStack(Material.DIAMOND,Lang.GUI_TREASURY_DONATION.get(),Lang.GUI_TOWN_TREASURY_DONATION_DESC1.get());
        ItemStack donationHistory = HeadUtils.createCustomItemStack(Material.PAPER,Lang.GUI_TREASURY_DONATION_HISTORY.get());
        HeadUtils.setLore(donationHistory, town.getDonationHistory().get(5),Lang.GUI_GENERIC_CLICK_TO_OPEN.get());

        ItemStack retrieveMoney = HeadUtils.makeSkull(Lang.GUI_TREASURY_RETRIEVE_GOLD.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWE2NDUwMWIxYmE1M2QxZDRlOWY0MDI5MTdiNWJkNDc3MjdiMTY3MDJhY2Y2OTMwZDYxMjFjMDdkYzQyYWUxYSJ9fX0=",
                Lang.GUI_TREASURY_RETRIEVE_GOLD_DESC1.get());



        GuiItem _goldInfo = ItemBuilder.from(goldIcon).asGuiItem(event -> event.setCancelled(true));
        GuiItem _goldSpendingIcon = ItemBuilder.from(goldSpendingIcon).asGuiItem(event -> event.setCancelled(true));
        GuiItem _taxHistory = ItemBuilder.from(taxHistory).asGuiItem(event -> {
            if(!isSQLEnabled())
                OpenTownEconomicsHistory(player,HistoryEnum.TAX);
            event.setCancelled(true);
        });
        GuiItem _salarySpending = ItemBuilder.from(salarySpending).asGuiItem(event -> {
            if(!isSQLEnabled())
                OpenTownEconomicsHistory(player,HistoryEnum.SALARY);
            event.setCancelled(true);
        });
        GuiItem _chunkSpending = ItemBuilder.from(chunkSpending).asGuiItem(event -> {
            if(!isSQLEnabled())
                OpenTownEconomicsHistory(player,HistoryEnum.CHUNK);
            event.setCancelled(true);
        });
        GuiItem _miscSpending = ItemBuilder.from(miscSpending).asGuiItem(event -> {
            if(!isSQLEnabled())
                OpenTownEconomicsHistory(player,HistoryEnum.MISCELLANEOUS);
            event.setCancelled(true);
        });
        GuiItem _donation = ItemBuilder.from(donation).asGuiItem(event -> {
            player.sendMessage(getTANString() + Lang.WRITE_IN_CHAT_AMOUNT_OF_MONEY_FOR_DONATION.get());
            player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
            player.closeInventory();

            PlayerChatListenerStorage.addPlayer(TOWN_DONATION,player);
            event.setCancelled(true);
        });
        GuiItem _donationHistory = ItemBuilder.from(donationHistory).asGuiItem(event -> {
            if(!isSQLEnabled())
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
            int amountToRemove = event.isShiftClick() && currentTax > 10 ? 10 : 1;

            if(currentTax <= 0){
                player.sendMessage(getTANString() + Lang.GUI_TREASURY_CANT_TAX_LESS.get());
                return;
            }
            SoundUtil.playSound(player, REMOVE);

            town.addToFlatTax(-amountToRemove);
            OpenTownEconomy(player);
        });
        GuiItem _taxInfo = ItemBuilder.from(taxInfo).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownEconomy(player);
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
            OpenTownEconomy(player);
        });

        GuiItem _retrieveMoney = ItemBuilder.from(retrieveMoney).asGuiItem(event -> {
            event.setCancelled(true);

            if(!playerStat.hasPermission(MANAGE_TAXES)){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }

            Map<MessageKey,String> data = new HashMap<>();
            data.put(TOWN_ID, town.getID());
            PlayerChatListenerStorage.addPlayer(ChatCategory.RETRIEVE_TOWN_MONEY,player,data);

            player.sendMessage(getTANString() + Lang.PLAYER_WRITE_QUANTITY_IN_CHAT.get());
            player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
            player.closeInventory();

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

        gui.setItem(3,6, _retrieveMoney);

        gui.setItem(4,1, IGUI.CreateBackArrow(player,p -> dispatchPlayerTown(player)));

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

                    ItemStack transactionIcon = HeadUtils.createCustomItemStack(Material.PAPER,
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

                    ItemStack transactionHistoryItem = HeadUtils.createCustomItemStack(Material.PAPER,date,lines);

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

                float upkeepCost = ConfigUtil.getCustomConfig("config.yml").getInt("TownChunkUpkeepCost");

                for(TransactionHistory chunkTax : town.getChunkHistory().get().values()){


                    ItemStack transactionIcon = HeadUtils.createCustomItemStack(Material.PAPER,
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

                    ItemStack transactionHistoryItem = HeadUtils.createCustomItemStack(Material.PAPER,date,lines);

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

                    ItemStack transactionIcon = HeadUtils.createCustomItemStack(Material.PAPER,
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

        gui.setItem(6,1, IGUI.CreateBackArrow(player,p -> OpenTownEconomy(player)));
        gui.open(player);

    }
    public static void OpenTownLevel(Player player,int level){
        Gui gui = IGUI.createChestGui("Town Upgrades | " + (level + 1),6);

        TownData townData = TownDataStorage.get(player);
        TownLevel townLevel = townData.getTownLevel();

        ItemStack whitePanel = HeadUtils.createCustomItemStack(Material.WHITE_STAINED_GLASS_PANE,"");
        ItemStack iron_bars = HeadUtils.createCustomItemStack(Material.IRON_BARS,Lang.LEVEL_LOCKED.get());

        GuiItem _TownIcon = GuiUtil.townUpgradeResume(townData);

        GuiItem _whitePanel = ItemBuilder.from(whitePanel).asGuiItem(event -> event.setCancelled(true));
        GuiItem _iron_bars = ItemBuilder.from(iron_bars).asGuiItem(event -> event.setCancelled(true));
        ItemStack green_level = HeadUtils.createCustomItemStack(Material.GREEN_STAINED_GLASS_PANE,"");

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
            if(townLevel.getTownLevel() > (i-2 + level)){
                ItemStack filler_green = HeadUtils.createCustomItemStack(Material.LIME_STAINED_GLASS_PANE,"Level " + (i-1 + level));

                _pannel = ItemBuilder.from(green_level).asGuiItem(event -> event.setCancelled(true));
                _bottompannel = ItemBuilder.from(filler_green).asGuiItem(event -> event.setCancelled(true));
            }
            else if(townLevel.getTownLevel() == (i - 2 + level)){
                _pannel = _iron_bars;
                ItemStack upgradeTownLevel = HeadUtils.createCustomItemStack(Material.ORANGE_STAINED_GLASS_PANE,
                        Lang.GUI_TOWN_LEVEL_UP.get(),
                        Lang.GUI_TOWN_LEVEL_UP_DESC1.get(townLevel.getTownLevel()),
                        Lang.GUI_TOWN_LEVEL_UP_DESC2.get(townLevel.getTownLevel() + 1, townLevel.getMoneyRequiredTownLevel()));

                _bottompannel = ItemBuilder.from(upgradeTownLevel).asGuiItem(event -> {
                    event.setCancelled(true);
                    townData.upgradeTown(player);
                    OpenTownLevel(player,level);
                });
            }
            else{
                _pannel = _iron_bars;
                ItemStack red_level = HeadUtils.createCustomItemStack(Material.RED_STAINED_GLASS_PANE,"Town level " + (i + level - 1) + " locked");
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
            if(level + 1 <= townUpgrade.getCol() && townUpgrade.getCol() <= level + 7)
                gui.setItem(townUpgrade.getRow(),townUpgrade.getCol() + (1 - level),_guiItem);
        }

        ItemStack nextPageButton = HeadUtils.makeSkull(
                Lang.GUI_NEXT_PAGE.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2MjYyYWYxZDVmNDE0YzU5NzA1NWMyMmUzOWNjZTE0OGU1ZWRiZWM0NTU1OWEyZDZiODhjOGQ2N2I5MmVhNiJ9fX0="
        );

        ItemStack previousPageButton = HeadUtils.makeSkull(
                Lang.GUI_PREVIOUS_PAGE.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQyZmRlOGI4MmU4YzFiOGMyMmIyMjY3OTk4M2ZlMzVjYjc2YTc5Nzc4NDI5YmRhZGFiYzM5N2ZkMTUwNjEifX19"
        );

        GuiItem _previous = ItemBuilder.from(previousPageButton).asGuiItem(event -> {
            event.setCancelled(true);
            if(level > 0)
                OpenTownLevel(player,level - 1);
        });
        GuiItem _next = ItemBuilder.from(nextPageButton).asGuiItem(event -> {
            event.setCancelled(true);
            int townMaxLevel = ConfigUtil.getCustomConfig("config.yml").getInt("TownMaxLevel",10);
            if(level < (townMaxLevel - 7))
                OpenTownLevel(player,level + 1);
        });



        gui.setItem(6,1, IGUI.CreateBackArrow(player,p -> dispatchPlayerTown(player)));


        gui.setItem(6,7, _previous);
        gui.setItem(6,8, _next);

        gui.open(player);

    }
    public static void OpenTownSettings(Player player) {

        Gui gui = IGUI.createChestGui("Town",4);

        PlayerData playerData = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(player);
        int changeTownNameCost = ConfigUtil.getCustomConfig("config.yml").getInt("ChangeTownNameCost");


        ItemStack TownIcon = playerTown.getIconWithInformations();
        ItemStack leaveTown = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN.get(),
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC1.get(playerTown.getName()),
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC2.get());
        ItemStack deleteTown = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN.get(),
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC1.get(playerTown.getName()),
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC2.get());
        ItemStack changeOwnershipTown = HeadUtils.createCustomItemStack(Material.BEEHIVE,
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP.get(),
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC1.get(),
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC2.get());
        ItemStack changeMessage = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK,
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_MESSAGE.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_MESSAGE_DESC1.get(playerTown.getDescription()));
        ItemStack toggleApplication = HeadUtils.createCustomItemStack(Material.PAPER,
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION.get(),
                (playerTown.isRecruiting() ? Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_ACCEPT.get() : Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_NOT_ACCEPT.get()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_CLICK_TO_SWITCH.get());
        ItemStack changeTownName = HeadUtils.createCustomItemStack(Material.NAME_TAG,
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME_DESC1.get(playerTown.getName()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME_DESC2.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME_DESC3.get(changeTownNameCost));
        ItemStack quitRegion = HeadUtils.createCustomItemStack(Material.SPRUCE_DOOR,
                Lang.GUI_TOWN_SETTINGS_QUIT_REGION.get(),
                playerTown.haveOverlord() ? Lang.GUI_TOWN_SETTINGS_QUIT_REGION_DESC1_REGION.get(playerTown.getOverlord().getName()) : Lang.TOWN_NO_REGION.get());
        ItemStack changeChunkColor = HeadUtils.createCustomItemStack(Material.PURPLE_WOOL,
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC1.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC2.get(StringUtil.getHexColor(playerTown.getChunkColorInHex()) + playerTown.getChunkColorInHex()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC3.get());

        ItemStack changeTag = HeadUtils.createCustomItemStack(Material.FLOWER_BANNER_PATTERN,
                Lang.GUI_TOWN_SETTINGS_CHANGE_TAG.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TAG_DESC1.get(playerTown.getColoredTag()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TAG_DESC2.get());

        GuiItem _townIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> event.setCancelled(true));

        GuiItem _leaveTown = ItemBuilder.from(leaveTown).asGuiItem(event -> {
            event.setCancelled(true);
            if (playerData.isTownLeader()){
                SoundUtil.playSound(player, NOT_ALLOWED);
                player.sendMessage(getTANString() + Lang.CHAT_CANT_LEAVE_TOWN_IF_LEADER.get());
                return;
            }

            OpenConfirmMenu(player, Lang.GUI_CONFIRM_PLAYER_LEAVE_TOWN.get(playerData.getName()), confirm -> {

                player.closeInventory();
                playerTown.removePlayer(playerData);
                player.sendMessage(getTANString() + Lang.CHAT_PLAYER_LEFT_THE_TOWN.get());
                playerTown.broadCastMessageWithSound(Lang.TOWN_BROADCAST_PLAYER_LEAVE_THE_TOWN.get(playerData.getName()), BAD);
            }, remove -> OpenTownSettings(player));
        });
        GuiItem _deleteTown = ItemBuilder.from(deleteTown).asGuiItem(event -> {
            event.setCancelled(true);
            if (!playerData.isTownLeader()){
                player.sendMessage(getTANString() + Lang.CHAT_CANT_DISBAND_TOWN_IF_NOT_LEADER.get());
                return;
            }

            OpenConfirmMenu(player, Lang.GUI_CONFIRM_PLAYER_DELETE_TOWN.get(playerTown.getName()), confirm -> {
                deleteTown(player, playerTown);

                player.closeInventory();
                SoundUtil.playSound(player,GOOD);
                player.sendMessage(getTANString() + Lang.CHAT_PLAYER_TOWN_SUCCESSFULLY_DELETED.get());
            }, remove -> OpenTownSettings(player));


        });

        GuiItem _changeOwnershipTown = ItemBuilder.from(changeOwnershipTown).asGuiItem(event -> {

            event.setCancelled(true);

            if(playerData.isTownLeader())
                OpenTownChangeOwnershipPlayerSelect(player, playerTown);
            else
                player.sendMessage(getTANString() + Lang.NOT_TOWN_LEADER_ERROR.get());

        });

        GuiItem _changeMessage = ItemBuilder.from(changeMessage).asGuiItem(event -> {
            player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
            player.closeInventory();

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

            if(playerData.hasPermission(TOWN_ADMINISTRATOR)){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
                player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
                player.closeInventory();

                Map<MessageKey, String> data = new HashMap<>();
                data.put(MessageKey.TOWN_ID,playerTown.getID());
                data.put(MessageKey.COST,Integer.toString(changeTownNameCost));
                PlayerChatListenerStorage.addPlayer(CHANGE_TOWN_NAME,player,data);
            }
            else
                player.sendMessage(getTANString() + Lang.NOT_TOWN_LEADER_ERROR.get());
        });

        GuiItem _quitRegion = ItemBuilder.from(quitRegion).asGuiItem(event -> {
            event.setCancelled(true);
            if (!playerTown.haveOverlord()) {
                player.sendMessage(getTANString() + Lang.TOWN_NO_REGION.get());
                return;
            }


            RegionData regionData = playerTown.getOverlord();

            if (playerTown.isRegionalCapital()){
                player.sendMessage(getTANString() + Lang.NOT_TOWN_LEADER_ERROR.get());
                return;
            }

            OpenConfirmMenu(player, Lang.GUI_CONFIRM_TOWN_LEAVE_REGION.get(playerTown.getName()), confirm -> {

                regionData.removeSubject(playerTown);
                playerTown.removeOverlord();
                playerTown.broadCastMessageWithSound(Lang.TOWN_BROADCAST_TOWN_LEFT_REGION.get(playerTown.getName(), regionData.getName()), BAD);
                regionData.broadCastMessage(Lang.REGION_BROADCAST_TOWN_LEFT_REGION.get(playerTown.getName()));

                player.closeInventory();

            }, remove -> OpenTownSettings(player));
        });

        GuiItem _changeChunkColor = ItemBuilder.from(changeChunkColor).asGuiItem(event -> {
            event.setCancelled(true);

            if(playerData.hasPermission(TOWN_ADMINISTRATOR)){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT.get());
                player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
                player.closeInventory();

                Map<MessageKey, String> data = new HashMap<>();
                data.put(MessageKey.TOWN_ID,playerTown.getID());
                PlayerChatListenerStorage.addPlayer(CHANGE_CHUNK_COLOR,player,data);
            }
            else
                player.sendMessage(getTANString() + Lang.NOT_TOWN_LEADER_ERROR.get());
        });

        GuiItem _changeTag = ItemBuilder.from(changeTag).asGuiItem(event -> {
            event.setCancelled(true);

            if(playerData.hasPermission(TOWN_ADMINISTRATOR)){
                player.closeInventory();
                player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));

                Map<MessageKey, String> data = new HashMap<>();
                data.put(MessageKey.TOWN_ID,playerTown.getID());
                PlayerChatListenerStorage.addPlayer(CHANGE_TOWN_TAG,player,data);
            }

        });





        gui.setItem(4, _townIcon);
        gui.setItem(2,2, _leaveTown);
        gui.setItem(2,3, _deleteTown);
        gui.setItem(2,4, _changeOwnershipTown);
        gui.setItem(2,6, _changeMessage);
        gui.setItem(2,7, _toggleApplication);
        gui.setItem(2,8, _changeTownName);

        gui.setItem(3,2, _quitRegion);

        if(ConfigUtil.getCustomConfig("config.yml").getBoolean("EnablePlayerPrefix",false))
            gui.setItem(3,7, _changeTag);
        if(isDynmapAddonLoaded())
            gui.setItem(3,8, _changeChunkColor);

        gui.setItem(4,1, IGUI.CreateBackArrow(player,p -> dispatchPlayerTown(player)));
        gui.open(player);
    }

    public static void OpenTownChangeOwnershipPlayerSelect(Player player, TownData townData) {

        Gui gui = IGUI.createChestGui("Town",3);

        int i = 0;
        for (String playerUUID : townData.getPlayerIDList()){
            OfflinePlayer townPlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerUUID));

            ItemStack playerHead = HeadUtils.getPlayerHead(townPlayer.getName(),townPlayer,
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC1.get(player.getName()),
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC2.get());


            GuiItem _playerHead = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);

                OpenConfirmMenu(player, Lang.GUI_CONFIRM_CHANGE_TOWN_LEADER.get(townPlayer.getName()), confirm -> {

                    townData.setLeaderID(townPlayer.getUniqueId().toString());
                    player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_SUCCESS.get(townPlayer.getName()));
                    dispatchPlayerTown(player);

                    player.closeInventory();

                }, remove -> OpenTownSettings(player));

            });
            gui.setItem(i, _playerHead);
            i = i+1;
        }
        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenTownSettings(player)));
        gui.open(player);
    }
    public static void OpenRelations(Player player, ITerritoryData territory, Consumer<Player> exitMenu) {

        Gui gui = IGUI.createChestGui("Town",3);


        ItemStack warCategory = HeadUtils.createCustomItemStack(Material.IRON_SWORD,
                Lang.GUI_TOWN_RELATION_HOSTILE.get(),
                Lang.GUI_TOWN_RELATION_HOSTILE_DESC1.get());
        ItemStack EmbargoCategory = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.GUI_TOWN_RELATION_EMBARGO.get(),
                Lang.GUI_TOWN_RELATION_EMBARGO_DESC1.get());
        ItemStack NAPCategory = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK,
                Lang.GUI_TOWN_RELATION_NAP.get(),
                Lang.GUI_TOWN_RELATION_NAP_DESC1.get());
        ItemStack AllianceCategory = HeadUtils.createCustomItemStack(Material.CAMPFIRE,
                Lang.GUI_TOWN_RELATION_ALLIANCE.get(),
                Lang.GUI_TOWN_RELATION_ALLIANCE_DESC1.get());

        GuiItem _warCategory = ItemBuilder.from(warCategory).asGuiItem(event -> {
            event.setCancelled(true);
            openSingleRelation(player,territory, TownRelation.WAR,0, exitMenu);
        });
        GuiItem _EmbargoCategory = ItemBuilder.from(EmbargoCategory).asGuiItem(event -> {
            event.setCancelled(true);
            openSingleRelation(player,territory, TownRelation.EMBARGO,0, exitMenu);

        });
        GuiItem _NAPCategory = ItemBuilder.from(NAPCategory).asGuiItem(event -> {
            event.setCancelled(true);
            openSingleRelation(player,territory, TownRelation.NON_AGGRESSION,0, exitMenu);

        });
        GuiItem _AllianceCategory = ItemBuilder.from(AllianceCategory).asGuiItem(event -> {
            event.setCancelled(true);
            openSingleRelation(player,territory, TownRelation.ALLIANCE,0, exitMenu);
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

        gui.setItem(3,1, IGUI.CreateBackArrow(player,exitMenu));

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
    public static void openSingleRelation(Player player, ITerritoryData mainTerritory, TownRelation relation, int page, Consumer<Player> doubleExit) {
        Gui gui = IGUI.createChestGui("Relation | page " + (page + 1), 6);

        PlayerData playerStat = PlayerDataStorage.get(player);

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for(String territoryID : mainTerritory.getRelations().getTerritoriesIDWithRelation(relation)){

            ITerritoryData territoryData = TerritoryUtil.getTerritory(territoryID);
            ItemStack icon = territoryData.getIconWithInformationAndRelation(mainTerritory);

            if (relation == TownRelation.WAR) {
                ItemMeta meta = icon.getItemMeta();
                assert meta != null;
                List<String> lore = meta.getLore();
                assert lore != null;
                lore.add(Lang.GUI_TOWN_ATTACK_TOWN_DESC1.get());
                meta.setLore(lore);
                icon.setItemMeta(meta);
            }

            GuiItem _town = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);

                if (relation == TownRelation.WAR) {
                    if(mainTerritory.atWarWith(territoryID)){
                        player.sendMessage(getTANString() + Lang.GUI_TOWN_ATTACK_ALREADY_ATTACKING.get());
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    OpenStartWarSettings(player, doubleExit, new CreateAttackData(mainTerritory, territoryData));
                }
            });
            guiItems.add(_town);
        }

        ItemStack addTownButton = HeadUtils.makeSkull(
                Lang.GUI_TOWN_RELATION_ADD_TOWN.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19"
        );
        ItemStack removeTownButton = HeadUtils.makeSkull(
                Lang.GUI_TOWN_RELATION_REMOVE_TOWN.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0="
        );

        GuiItem _add = ItemBuilder.from(addTownButton).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.hasPermission(TownRolePermission.MANAGE_TOWN_RELATION)){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }
            OpenTownRelationModification(player,mainTerritory,Action.ADD,relation, 0, doubleExit);
        });
        GuiItem _remove = ItemBuilder.from(removeTownButton).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.hasPermission(TownRolePermission.MANAGE_TOWN_RELATION)){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }
            OpenTownRelationModification(player,mainTerritory, Action.REMOVE,relation, 0, doubleExit);
        });

        createIterator(gui, guiItems, page, player, p -> OpenRelations(player, mainTerritory, doubleExit),
                p -> openSingleRelation(player, mainTerritory, relation, page - 1, doubleExit),
                p -> openSingleRelation(player, mainTerritory, relation,page - 1, doubleExit));

        gui.setItem(6,4, _add);
        gui.setItem(6,5, _remove);


        gui.open(player);
    }
    public static void OpenTownRelationModification(Player player,ITerritoryData territory, Action action, TownRelation relation, int page, Consumer<Player> exit) {
        int nRows = 6;
        Gui gui = IGUI.createChestGui("Town - Relation",nRows);

        ArrayList<String> relationListID = territory.getRelations().getTerritoriesIDWithRelation(relation);
        ItemStack decorativeGlass = getDecorativeGlass(action);
        ArrayList<GuiItem> guiItems = new ArrayList<>();


        if(action == Action.ADD){
            List<String> territories = new ArrayList<>();
            territories.addAll(TownDataStorage.getTownMap().keySet());
            territories.addAll(RegionDataStorage.getRegionStorage().keySet());

            territories.removeAll(relationListID); //Territory already have this relation
            territories.remove(territory.getID()); //Remove itself

            for(String otherTownUUID : territories){


                ITerritoryData otherTerritory = TerritoryUtil.getTerritory(otherTownUUID);
                ItemStack icon = otherTerritory.getIconWithInformationAndRelation(territory);


                GuiItem _town = ItemBuilder.from(icon).asGuiItem(event -> {
                    event.setCancelled(true);

                    if(otherTerritory.haveNoLeader()){
                        player.sendMessage(getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_NO_LEADER.get());
                        return;
                    }

                    if(territory.getRelations().getRelationWith(otherTerritory) != null){
                        player.sendMessage(getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_ALREADY_HAVE_RELATION.get());
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    if(relation.getNeedsConfirmationToStart()){
                        // Can only be good relations
                        OfflinePlayer otherTownLeader = Bukkit.getOfflinePlayer(UUID.fromString(otherTerritory.getLeaderID()));

                        if (!otherTownLeader.isOnline()) {
                            player.sendMessage(getTANString() + Lang.LEADER_NOT_ONLINE.get());
                            return;
                        }
                        Player otherTownLeaderOnline = otherTownLeader.getPlayer();
                        if(otherTownLeaderOnline == null)
                            return;
                        TownRelationConfirmStorage.addInvitation(otherTerritory.getLeaderID(), territory.getID(), relation);

                        otherTownLeaderOnline.sendMessage(getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_RECEIVED_1.get(territory.getName(),relation.getColor() + relation.getName()));
                        ChatUtils.sendClickableCommand(otherTownLeaderOnline,getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_RECEIVED_2.get(),"tan accept "  + territory.getID());

                        player.sendMessage(getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_SENT_SUCCESS.get(otherTownLeaderOnline.getName()));

                        player.closeInventory();
                    }
                    else{ //Can only be bad relations
                        territory.broadCastMessageWithSound(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.get(otherTerritory.getName(),relation.getColoredName()), BAD);
                        otherTerritory.broadCastMessageWithSound(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.get(territory.getName(),relation.getColoredName()), BAD);
                        RelationUtil.addTownRelation(territory,otherTerritory,relation);
                        openSingleRelation(player,territory, relation,0,exit);
                    }
                });
                guiItems.add(_town);
            }
        }
        else if(action == Action.REMOVE){
            for(String otherTownUUID : relationListID){
                ITerritoryData otherTerritory = TerritoryUtil.getTerritory(otherTownUUID);
                ItemStack townIcon = otherTerritory.getIconWithInformationAndRelation(territory);

                GuiItem _town = ItemBuilder.from(townIcon).asGuiItem(event -> {
                    event.setCancelled(true);
                    if(relation.getNeedsConfirmationToEnd()){ //Can only be better relations

                        OfflinePlayer otherTownLeader = Bukkit.getOfflinePlayer(UUID.fromString(otherTerritory.getLeaderID()));

                        if (!otherTownLeader.isOnline()) {
                            player.sendMessage(getTANString() + Lang.LEADER_NOT_ONLINE.get());
                            return;
                        }
                        Player otherTownLeaderOnline = otherTownLeader.getPlayer();
                        if(otherTownLeaderOnline == null)
                            return;

                        player.sendMessage(getTANString() + "Sent to the leader of the other town");

                        TownRelationConfirmStorage.addInvitation(otherTerritory.getLeaderID(), territory.getID(), null);

                        otherTownLeaderOnline.sendMessage(getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_RECEIVED_1.get(territory.getName(),"neutral"));
                        ChatUtils.sendClickableCommand(otherTownLeaderOnline,getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_RECEIVED_2.get(),"tan accept "  + territory.getID());
                        player.closeInventory();
                    }
                    else{
                        territory.broadCastMessageWithSound(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.get(otherTerritory.getName(),"neutral"), BAD);
                        otherTerritory.broadCastMessageWithSound(getTANString() + Lang.GUI_TOWN_CHANGED_RELATION_RESUME.get(territory.getName(),"neutral"), BAD);
                        RelationUtil.removeTownRelation(territory,otherTerritory, relation);
                    }
                    openSingleRelation(player,territory,relation,0, exit);
                });
                guiItems.add(_town);
            }
        }


        createIterator(gui, guiItems, 0, player, p -> openSingleRelation(player, territory, relation,0, exit),
                p -> openSingleRelation(player, territory, relation,page - 1, exit),
                p -> openSingleRelation(player, territory, relation,page + 1, exit),
                decorativeGlass);


        gui.open(player);
    }

    private static ItemStack getDecorativeGlass(Action action) {
        if (action == Action.ADD)
            return new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        else
            return new ItemStack(Material.RED_STAINED_GLASS_PANE);
    }

    public static void OpenTownChunk(Player player) {
        Gui gui = IGUI.createChestGui("Town",3);

        TownData playerTown = TownDataStorage.get(player);

        ItemStack playerChunkIcon = HeadUtils.createCustomItemStack(Material.PLAYER_HEAD,
                Lang.GUI_TOWN_CHUNK_PLAYER.get(),
                Lang.GUI_TOWN_CHUNK_PLAYER_DESC1.get()
                );

        ItemStack mobChunckIcon = HeadUtils.createCustomItemStack(Material.CREEPER_HEAD,
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
                OpenTownChunkMobSettings(player,1);
            else{
                player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_LEVEL.get(DynamicLang.get("UNLOCK_MOB_BAN")));
                SoundUtil.playSound(player, NOT_ALLOWED);
            }
        });

        gui.setItem(2,4, _playerChunkIcon);
        gui.setItem(2,6, _mobChunckIcon);


        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> dispatchPlayerTown(player)));

        gui.open(player);
    }
    public static void OpenTownChunkMobSettings(Player player, int page){
        Gui gui = IGUI.createChestGui("Mob settings - Page " + page,6);

        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        TownData townData = TownDataStorage.get(player);
        ClaimedChunkSettings chunkSettings = townData.getChunkSettings();

        ArrayList<GuiItem> guiLists = new ArrayList<>();
        Collection<MobChunkSpawnEnum> mobCollection = MobChunkSpawnStorage.getMobSpawnStorage().values();

        for (MobChunkSpawnEnum mobEnum : mobCollection) {

            UpgradeStatus upgradeStatus = chunkSettings.getSpawnControl(mobEnum);

            List<String> status = new ArrayList<>();
            int cost = getMobSpawnCost(mobEnum);
            if(upgradeStatus.isUnlocked()){
                if(upgradeStatus.canSpawn()){
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
            ItemStack mobIcon = HeadUtils.makeSkull(mobEnum.name(),mobEnum.getTexture(),status);

            GuiItem mobItem = new GuiItem(mobIcon, event -> {
                event.setCancelled(true);
                if(!playerStat.hasPermission(TownRolePermission.MANAGE_MOB_SPAWN)){
                    player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                    return;
                }
                if(upgradeStatus.isUnlocked()){
                    upgradeStatus.setActivated(!upgradeStatus.canSpawn());
                    SoundUtil.playSound(player, ADD);
                }
                else{
                    if(townData.getBalance() < cost){
                        player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get());
                        return;
                    }
                    townData.removeFromBalance(cost);
                    SoundUtil.playSound(player,GOOD);
                    upgradeStatus.setUnlocked(true);
                }

                OpenTownChunkMobSettings(player,page);

            });
            guiLists.add(mobItem);
        }

        createIterator(gui, guiLists, page, player, p -> OpenTownChunk(player),
                p -> OpenTownChunkMobSettings(player, page + 1),
                p -> OpenTownChunkMobSettings(player, page - 1));


        gui.open(player);
    }
    public static void OpenTownPropertiesMenu(Player player, int page){
        int nRows = 6;

        Gui gui = IGUI.createChestGui("Properties",nRows);
        ArrayList<GuiItem> guiItems = new ArrayList<>();

        PlayerData playerData = PlayerDataStorage.get(player);
        TownData townData = TownDataStorage.get(playerData);

        for (PropertyData townProperty : townData.getPropertyDataList()){
            ItemStack property = townProperty.getIcon();

            GuiItem _property = ItemBuilder.from(property).asGuiItem(event -> {
                event.setCancelled(true);
                if(!playerData.haveTown()){
                    player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.get());
                    SoundUtil.playSound(player, NOT_ALLOWED);
                    return;
                }
                if(!playerData.hasPermission(TownRolePermission.MANAGE_PROPERTY)){
                    player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                    SoundUtil.playSound(player, NOT_ALLOWED);
                    return;
                }
                OpenPropertyManagerMenu(player,townProperty);
            });
            guiItems.add(_property);
        }

        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> dispatchPlayerTown(player),
                p -> OpenTownPropertiesMenu(player,page + 1),
                p -> OpenTownPropertiesMenu(player,page - 1));
        gui.open(player);
    }
    public static void OpenTownChunkPlayerSettings(Player player){
        Gui gui = IGUI.createChestGui("Town",4);

        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        TownData townData = TownDataStorage.get(player);



        Object[][] itemData = {
                {ChunkPermissionType.OPEN_DOOR, Material.OAK_DOOR, Lang.GUI_TOWN_CLAIM_SETTINGS_DOOR},
                {ChunkPermissionType.CHEST, Material.CHEST, Lang.GUI_TOWN_CLAIM_SETTINGS_CHEST},
                {ChunkPermissionType.PLACE_BLOCK, Material.BRICKS, Lang.GUI_TOWN_CLAIM_SETTINGS_BUILD},
                {ChunkPermissionType.BREAK_BLOCK, Material.IRON_PICKAXE, Lang.GUI_TOWN_CLAIM_SETTINGS_BREAK},
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
                {ChunkPermissionType.PLACE_BOAT, Material.OAK_BOAT, Lang.GUI_TOWN_CLAIM_SETTINGS_PLACE_BOAT},
                {ChunkPermissionType.PLACE_MINECART, Material.MINECART, Lang.GUI_TOWN_CLAIM_SETTINGS_PLACE_VEHICLE},
                {ChunkPermissionType.GATHER_BERRIES, Material.SWEET_BERRIES, Lang.GUI_TOWN_CLAIM_SETTINGS_GATHER_BERRIES},
                {ChunkPermissionType.USE_BONEMEAL, Material.BONE_MEAL, Lang.GUI_TOWN_CLAIM_SETTINGS_USE_BONE_MEAL},

        };

        for (int i = 0; i < itemData.length; i++) {
            ChunkPermissionType type = (ChunkPermissionType) itemData[i][0];
            Material material = (Material) itemData[i][1];
            Lang label = (Lang) itemData[i][2];

            TownChunkPermission permission = townData.getPermission(type);
            ItemStack itemStack = HeadUtils.createCustomItemStack(
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

        ItemStack createRegion = HeadUtils.createCustomItemStack(Material.STONE_BRICKS,
                Lang.GUI_REGION_CREATE.get(),
                Lang.GUI_REGION_CREATE_DESC1.get(regionCost),
                Lang.GUI_REGION_CREATE_DESC2.get()
        );

        ItemStack browseRegion = HeadUtils.createCustomItemStack(Material.BOOK,
                Lang.GUI_REGION_BROWSE.get(),
                Lang.GUI_REGION_BROWSE_DESC1.get(RegionDataStorage.getNumberOfRegion()),
                Lang.GUI_REGION_BROWSE_DESC2.get()
        );

        GuiItem _createRegion = ItemBuilder.from(createRegion).asGuiItem(event -> {
            PlayerData playerData = PlayerDataStorage.get(player);
            if(!playerData.haveTown()){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.get());
                return;
            }

            event.setCancelled(true);
            int townMoney = TownDataStorage.get(player).getBalance();
            if (townMoney < regionCost) {
                player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY_EXTENDED.get(regionCost - townMoney));
            }
            else {
                player.sendMessage(getTANString() + Lang.WRITE_IN_CHAT_NEW_REGION_NAME.get());
                player.closeInventory();

                PlayerChatListenerStorage.addPlayer(CREATE_REGION,player);
            }
        });

        GuiItem _browseRegion = ItemBuilder.from(browseRegion).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTerritoryListWithRelation(player, null,p -> OpenNoRegionMenu(player), 0);
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
        RegionData playerRegion = playerTown.getOverlord();


        ItemStack regionIcon = getRegionIcon(playerRegion);

        ItemStack GoldIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_TREASURY_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=",
                Lang.GUI_TOWN_TREASURY_ICON_DESC1.get());
        ItemStack townIcon = HeadUtils.makeSkull(Lang.GUI_REGION_TOWN_LIST.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=",
                Lang.GUI_REGION_TOWN_LIST_DESC1.get());
        ItemStack otherRegionIcon = HeadUtils.makeSkull(Lang.GUI_OTHER_REGION_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMzc0ZTIxYjgxYzBiMjFhYmViOGU5N2UxM2UwNzdkM2VkMWVkNDRmMmU5NTZjNjhmNjNhM2UxOWU4OTlmNiJ9fX0=",
                Lang.GUI_OTHER_REGION_ICON_DESC1.get());
        ItemStack RelationIcon = HeadUtils.makeSkull(Lang.GUI_RELATION_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUwN2Q2ZGU2MzE4MzhlN2E3NTcyMGU1YjM4ZWYxNGQyOTY2ZmRkODQ4NmU3NWQxZjY4MTJlZDk5YmJjYTQ5OSJ9fX0=",
                Lang.GUI_RELATION_ICON_DESC1.get());
        ItemStack SettingIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_SETTINGS_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVkMmNiMzg0NThkYTE3ZmI2Y2RhY2Y3ODcxNjE2MDJhMjQ5M2NiZjkzMjMzNjM2MjUzY2ZmMDdjZDg4YTljMCJ9fX0=",
                Lang.GUI_TOWN_SETTINGS_ICON_DESC1.get());

        GuiItem _regionIcon = ItemBuilder.from(regionIcon).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.isRegionLeader())
                return;
            if(event.getCursor() == null)
                return;

            Material itemMaterial = event.getCursor().getType();
            if(itemMaterial == Material.AIR){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_NO_ITEM_SHOWED.get());
            }
            else {
                playerRegion.setIconMaterial(itemMaterial);
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
            OpenTerritoryListWithRelation(player, playerTown,p -> OpenRegionMenu(player), 0);
        });
        GuiItem _relationIcon = ItemBuilder.from(RelationIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenRelations(player, playerRegion, p -> OpenRegionMenu(player));

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
    private static void OpenTownInRegion(Player player){

        Gui gui = IGUI.createChestGui("Region",4);
        PlayerData playerData = PlayerDataStorage.get(player);
        RegionData regionData = RegionDataStorage.get(player);

        for (ITerritoryData townData : regionData.getSubjects()){
            ItemStack townIcon = townData.getIconWithInformations();

            GuiItem _townIcon = ItemBuilder.from(townIcon).asGuiItem(event -> event.setCancelled(true));
            gui.addItem(_townIcon);
        }

        ItemStack addTown = HeadUtils.makeSkull(Lang.GUI_INVITE_TOWN_TO_REGION.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
        ItemStack removeTown = HeadUtils.makeSkull(Lang.GUI_KICK_TOWN_TO_REGION.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=");


        GuiItem _addTown = ItemBuilder.from(addTown).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerData.isRegionLeader()){
                player.sendMessage(getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get());
                return;
            }
            OpenRegionTownInteraction(player, Action.ADD);
        });
        GuiItem _removeTown = ItemBuilder.from(removeTown).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerData.isRegionLeader()){
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

                if(regionData.isTownInRegion(townData))
                    continue;

                ItemStack townIcon = townData.getIconWithInformationAndRelation(regionData);
                HeadUtils.addLore(townIcon, Lang.GUI_REGION_INVITE_TOWN_DESC1.get());

                GuiItem _townIcon = ItemBuilder.from(townIcon).asGuiItem(event -> {
                    event.setCancelled(true);
                    if (!townData.isLeaderOnline()) {
                        player.sendMessage(getTANString() + Lang.LEADER_NOT_ONLINE.get());
                        return;
                    }
                    Player townLeader = Bukkit.getPlayer(UUID.fromString(townData.getLeaderID()));

                    if(townLeader == null)
                        return;

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
            for (ITerritoryData townData : regionData.getSubjects()){
                ItemStack townIcon = townData.getIconWithInformationAndRelation(regionData);
                HeadUtils.addLore(townIcon, Lang.GUI_REGION_INVITE_TOWN_DESC1.get());

                GuiItem _townIcon = ItemBuilder.from(townIcon).asGuiItem(event -> {
                    event.setCancelled(true);

                    if(regionData.getCapitalID().equals(townData.getID())){
                        player.sendMessage(getTANString() + Lang.CANT_KICK_REGIONAL_CAPITAL.get(townData.getName()));
                        return;
                    }
                    regionData.broadcastMessageWithSound(Lang.GUI_REGION_KICK_TOWN_BROADCAST.get(townData.getName()), BAD);
                    townData.removeOverlord();
                    regionData.removeSubject(townData);
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
        RegionData playerRegion = playerTown.getOverlord();

        ItemStack regionIcon = getRegionIcon(playerRegion);

        ItemStack deleteRegion = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.GUI_REGION_DELETE.get(),
                Lang.GUI_REGION_DELETE_DESC1.get(playerRegion.getName()),
                Lang.GUI_REGION_DELETE_DESC2.get(),
                Lang.GUI_REGION_DELETE_DESC3.get()
        );

        ItemStack changeLeader = HeadUtils.createCustomItemStack(Material.GOLDEN_HELMET,
                Lang.GUI_REGION_CHANGE_CAPITAL.get(),
                Lang.GUI_REGION_CHANGE_CAPITAL_DESC1.get(playerRegion.getCapital().getName()),
                Lang.GUI_REGION_CHANGE_CAPITAL_DESC2.get()
        );

        ItemStack changeDescription = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK,
                Lang.GUI_REGION_CHANGE_DESCRIPTION.get(),
                Lang.GUI_REGION_CHANGE_DESCRIPTION_DESC1.get(playerRegion.getDescription()),
                Lang.GUI_REGION_CHANGE_DESCRIPTION_DESC2.get()
        );

        ItemStack changeName = HeadUtils.createCustomItemStack(
                Material.NAME_TAG,
                Lang.GUI_PROPERTY_CHANGE_NAME.get(),
                Lang.GUI_PROPERTY_CHANGE_NAME_DESC1.get(playerRegion.getName())
        );

        ItemStack changeColor = HeadUtils.createCustomItemStack(
                Material.PURPLE_WOOL,
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC1.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC2.get(StringUtil.getHexColor(playerTown.getChunkColorInHex()) + playerTown.getChunkColorInHex()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC3.get()
        );

        GuiItem _regionIcon = ItemBuilder.from(regionIcon).asGuiItem(event -> event.setCancelled(true));

        GuiItem _deleteRegion = ItemBuilder.from(deleteRegion).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.isRegionLeader()){
                player.sendMessage(getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get());
                return;
            }

            OpenConfirmMenu(player, Lang.GUI_CONFIRM_DELETE_REGION.get(playerRegion.getName()), confirm -> {
                RegionDataStorage.deleteRegion(player, playerRegion);
                SoundUtil.playSound(player, BAD);
                player.sendMessage(getTANString() + Lang.CHAT_PLAYER_REGION_SUCCESSFULLY_DELETED.get());
                OpenMainMenu(player);
            }, remove -> OpenTownSettings(player));
        });

        GuiItem _changeCapital = ItemBuilder.from(changeLeader).asGuiItem(event -> {
            event.setCancelled(true);
            if(playerStat.isRegionLeader()){
                player.sendMessage(getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get());
                return;
            }
            OpenRegionChangeOwnership(player,0);
        });

        GuiItem _changeDescription = ItemBuilder.from(changeDescription).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.isRegionLeader()){
                player.sendMessage(getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get());
                return;
            }

            player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
            player.closeInventory();

            Map<MessageKey, String> data = new HashMap<>();
            data.put(MessageKey.REGION_ID,playerRegion.getID());
            PlayerChatListenerStorage.addPlayer(CHANGE_REGION_DESCRIPTION,player,data);
        });

        GuiItem _changeName = ItemBuilder.from(changeName).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.isRegionLeader()){
                player.sendMessage(getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get());
                return;
            }

            player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
            player.closeInventory();

            Map<MessageKey, String> data = new HashMap<>();
            data.put(REGION_ID,playerRegion.getID());
            data.put(COST, String.valueOf(0));
            PlayerChatListenerStorage.addPlayer(CHANGE_REGION_NAME,player,data);
        });

        GuiItem _changeChunkColor = ItemBuilder.from(changeColor).asGuiItem(event -> {
            event.setCancelled(true);

            player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT.get());
            player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
            player.closeInventory();

            Map<MessageKey, String> data = new HashMap<>();
            data.put(REGION_ID,playerRegion.getID());
            PlayerChatListenerStorage.addPlayer(CHANGE_REGION_CHUNK_COLOR,player,data);
        });


        gui.setItem(1,5, _regionIcon);

        gui.setItem(2,2, _deleteRegion);
        gui.setItem(2,3, _changeCapital);

        gui.setItem(2,6, _changeDescription);
        gui.setItem(2,7, _changeName);
        if(isDynmapAddonLoaded())
            gui.setItem(2,8,_changeChunkColor);


        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenRegionMenu(player)));

        gui.open(player);
    }
    private static void OpenRegionEconomy(Player player) {
        Gui gui = IGUI.createChestGui("Region", 4);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = playerStat.getTown();
        RegionData playerRegion = playerTown.getOverlord();

        int tax = playerRegion.getTaxRate();
        int treasury = playerRegion.getBalance();
        int taxTomorrow = playerRegion.getIncomeTomorrow();


        ItemStack goldIcon = HeadUtils.makeSkull(Lang.GUI_TREASURY_STORAGE.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=",
                Lang.GUI_TREASURY_STORAGE_DESC1.get(treasury),
                Lang.GUI_TREASURY_STORAGE_DESC2.get(taxTomorrow));
        ItemStack goldSpendingIcon = HeadUtils.makeSkull(Lang.GUI_TREASURY_SPENDING.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=",
                Lang.GUI_WARNING_STILL_IN_DEV.get());

        ItemStack lowerTax = HeadUtils.makeSkull(Lang.GUI_TREASURY_LOWER_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=",
                Lang.GUI_DECREASE_1_DESC.get(),
                Lang.GUI_DECREASE_10_DESC.get());
        ItemStack increaseTax = HeadUtils.makeSkull(Lang.GUI_TREASURY_INCREASE_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                Lang.GUI_INCREASE_1_DESC.get(),
                Lang.GUI_INCREASE_10_DESC.get());
        ItemStack taxInfo = HeadUtils.makeSkull(Lang.GUI_TREASURY_FLAT_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTk4ZGY0MmY0NzdmMjEzZmY1ZTlkN2ZhNWE0Y2M0YTY5ZjIwZDljZWYyYjkwYzRhZTRmMjliZDE3Mjg3YjUifX19",
                Lang.GUI_TREASURY_FLAT_TAX_DESC1.get(tax));
        ItemStack taxHistory = HeadUtils.makeSkull(Lang.GUI_TREASURY_TAX_HISTORY.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmU1OWYyZDNiOWU3ZmI5NTBlOGVkNzkyYmU0OTIwZmI3YTdhOWI5MzQ1NjllNDQ1YjJiMzUwM2ZlM2FiOTAyIn19fQ==",
                playerRegion.getTaxHistory().get(5),
                Lang.GUI_GENERIC_CLICK_TO_OPEN.get());
        ItemStack chunkSpending = HeadUtils.makeSkull(Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        ItemStack donation = HeadUtils.createCustomItemStack(Material.DIAMOND,
                Lang.GUI_TREASURY_DONATION.get(),
                Lang.GUI_REGION_TREASURY_DONATION_DESC1.get());
        ItemStack donationHistory = HeadUtils.createCustomItemStack(Material.PAPER,
                Lang.GUI_TREASURY_DONATION_HISTORY.get(),
                playerRegion.getDonationHistory().get(5),
                Lang.GUI_GENERIC_CLICK_TO_OPEN.get());



        GuiItem _goldIcon = ItemBuilder.from(goldIcon).asGuiItem(event -> event.setCancelled(true));
        GuiItem _goldSpendingIcon = ItemBuilder.from(goldSpendingIcon).asGuiItem(event -> event.setCancelled(true));
        GuiItem _lowerTax = ItemBuilder.from(lowerTax).asGuiItem(event -> {
            event.setCancelled(true);
            int currentTax = playerRegion.getTaxRate();
            int amountToRemove = event.isShiftClick() && currentTax > 10 ? 10 : 1;

            if(currentTax < 1){
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

        GuiItem _taxInfo = ItemBuilder.from(taxInfo).asGuiItem(event -> event.setCancelled(true));

        GuiItem _chunkSpending = ItemBuilder.from(chunkSpending).asGuiItem(event -> event.setCancelled(true));

        GuiItem _donation = ItemBuilder.from(donation).asGuiItem(event -> {
            player.sendMessage(getTANString() + Lang.WRITE_IN_CHAT_AMOUNT_OF_MONEY_FOR_DONATION.get());
            player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
            player.closeInventory();

            PlayerChatListenerStorage.addPlayer(REGION_DONATION,player);
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

                    ItemStack transactionIcon = HeadUtils.createCustomItemStack(Material.PAPER,
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

                    ItemStack transactionHistoryItem = HeadUtils.createCustomItemStack(Material.PAPER, date, lines);
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
    public static void OpenRegionChangeOwnership(Player player, int page){

            Gui gui = IGUI.createChestGui("Region", 6);
            PlayerData playerData = PlayerDataStorage.get(player);
            RegionData regionData = playerData.getRegion();

            ArrayList<GuiItem> guiItems = new ArrayList<>();
            for(String playerID : regionData.getPlayerIDList()){

                PlayerData iteratePlayerData = PlayerDataStorage.get(playerID);
                ItemStack switchPlayerIcon = HeadUtils.getPlayerHead(Bukkit.getOfflinePlayer(UUID.fromString(playerID)));

                GuiItem _switchPlayer = ItemBuilder.from(switchPlayerIcon).asGuiItem(event -> {
                    event.setCancelled(true);

                    OpenConfirmMenu(player, Lang.GUI_CONFIRM_CHANGE_LEADER.get(iteratePlayerData.getName()), confirm -> {
                        FileUtil.addLineToHistory(Lang.HISTORY_REGION_CAPITAL_CHANGED.get(player.getName(), regionData.getCapital().getName(), playerData.getTown().getName() ));
                        regionData.setLeaderID(iteratePlayerData.getID());

                        regionData.broadcastMessageWithSound(Lang.GUI_REGION_SETTINGS_REGION_CHANGE_LEADER_BROADCAST.get(iteratePlayerData.getName()),GOOD);

                        if(!regionData.getCapital().getID().equals(iteratePlayerData.getTown().getID())){
                            regionData.broadcastMessage(getTANString() + Lang.GUI_REGION_SETTINGS_REGION_CHANGE_CAPITAL_BROADCAST.get(iteratePlayerData.getTown().getName()));
                            regionData.setCapital(iteratePlayerData.getTownId());
                        }
                        OpenRegionSettings(player);
                    }, remove -> OpenRegionChangeOwnership(player,page));
                });
                guiItems.add(_switchPlayer);

            }

            GuiUtil.createIterator(gui,guiItems,page, player,
                    p -> OpenRegionSettings(player),
                    p -> OpenRegionChangeOwnership(player,page + 1),
                    p -> OpenRegionChangeOwnership(player,page - 1));


            gui.open(player);
    }


    public static void dispatchLandmarkGui(Player player, Landmark landmark){

        TownData townData = TownDataStorage.get(player);

        if(!landmark.hasOwner()){
            OpenLandMarkNoOwner(player,landmark);
            return;
        }
        if(townData.ownLandmark(landmark)){
            OpenPlayerOwnLandmark(player,landmark);
            return;
        }
        TownData owner = TownDataStorage.get(landmark.getOwnerID());
        player.sendMessage(getTANString() + Lang.LANDMARK_ALREADY_CLAIMED.get(owner.getName()));
        SoundUtil.playSound(player, MINOR_BAD);

    }

    private static void OpenLandMarkNoOwner(Player player, Landmark landmark) {
        Gui gui = IGUI.createChestGui("Landmark - unclaimed", 3);

        GuiItem landmarkIcon = ItemBuilder.from(landmark.getIcon()).asGuiItem(event -> event.setCancelled(true));

        TownData playerTown = TownDataStorage.get(player);

        ItemStack claimLandmark = HeadUtils.makeSkull(
                Lang.GUI_TOWN_RELATION_ADD_TOWN.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                playerTown.canClaimMoreLandmarks() ? Lang.GUI_LANDMARK_LEFT_CLICK_TO_CLAIM.get() : Lang.GUI_LANDMARK_TOWN_FULL.get()
        );

        GuiItem _claimLandmark = ItemBuilder.from(claimLandmark).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerTown.canClaimMoreLandmarks()) {
                player.sendMessage(getTANString() + Lang.GUI_LANDMARK_TOWN_FULL.get());
                SoundUtil.playSound(player, MINOR_BAD);
                return;
            }

            playerTown.addLandmark(landmark);
            playerTown.broadCastMessageWithSound(Lang.GUI_LANDMARK_CLAIMED.get(),GOOD);
            dispatchLandmarkGui(player, landmark);
        });

        ItemStack panel = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        GuiItem _landmarkPanel = ItemBuilder.from(panel).asGuiItem(event -> event.setCancelled(true));

        gui.setItem(1,5,landmarkIcon);
        gui.setItem(2,5, _claimLandmark);

        gui.setItem(3,1, IGUI.CreateBackArrow(player,Player::closeInventory));
        gui.setItem(3,2,_landmarkPanel);
        gui.setItem(3,3,_landmarkPanel);
        gui.setItem(3,4,_landmarkPanel);
        gui.setItem(3,5,_landmarkPanel);
        gui.setItem(3,6,_landmarkPanel);
        gui.setItem(3,7,_landmarkPanel);
        gui.setItem(3,8,_landmarkPanel);
        gui.setItem(3,9,_landmarkPanel);

        gui.open(player);
    }

    private static void OpenPlayerOwnLandmark(Player player, Landmark landmark) {
        TownData townData = TownDataStorage.get(landmark.getOwnerID());
        Gui gui = IGUI.createChestGui("Landmark - " + townData.getName(), 3);

        int quantity = landmark.computeStoredReward(townData);

        ItemStack removeTownButton = HeadUtils.makeSkull(
                Lang.GUI_REMOVE_LANDMARK.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0="
        );

        String bagTexture   ;
        if(quantity == 0)
            bagTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjRjMTY0YmFjMjE4NGE3NmExZWU5NjkxMzI0MmUzMzVmMWQ0MTFjYWZmNTEyMDVlYTM5YjIwNWU2ZjhmMDU4YSJ9fX0=";
        else
            bagTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTliOTA2YjIxNTVmMTkzNzg3MDQyMzM4ZDA1Zjg0MDM5MWMwNWE2ZDNlODE2MjM5MDFiMjk2YmVlM2ZmZGQyIn19fQ==";

        ItemStack collectRessources = HeadUtils.makeSkull(
                Lang.GUI_COLLECT_LANDMARK.get(),
                bagTexture,
                Lang.GUI_COLLECT_LANDMARK_DESC1.get(),
                Lang.GUI_COLLECT_LANDMARK_DESC2.get(quantity)
        );




        GuiItem _removeTownButton = ItemBuilder.from(removeTownButton).asGuiItem(event -> {
            event.setCancelled(true);
            townData.removeLandmark(landmark);
            TownData playerTown = TownDataStorage.get(player);
            playerTown.broadCastMessageWithSound(Lang.GUI_LANDMARK_REMOVED.get(),BAD);
            dispatchLandmarkGui(player,landmark);
        });

        GuiItem _collectRessources = ItemBuilder.from(collectRessources).asGuiItem(event -> {
            event.setCancelled(true);
            landmark.giveToPlayer(player,quantity);
            player.sendMessage(getTANString() + Lang.GUI_LANDMARK_REWARD_COLLECTED.get(quantity));
            SoundUtil.playSound(player, GOOD);
            dispatchLandmarkGui(player,landmark);
        });


        ItemStack panel = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        GuiItem _landmarkPanel = ItemBuilder.from(panel).asGuiItem(event -> event.setCancelled(true));


        GuiItem landmarkIcon = ItemBuilder.from(landmark.getIcon()).asGuiItem(event -> event.setCancelled(true));
        gui.setItem(1,1,_landmarkPanel);
        gui.setItem(1,2,_landmarkPanel);
        gui.setItem(1,3,_landmarkPanel);
        gui.setItem(1,4,_landmarkPanel);
        gui.setItem(1,5,landmarkIcon);
        gui.setItem(1,6,_landmarkPanel);
        gui.setItem(1,7,_landmarkPanel);
        gui.setItem(1,8,_landmarkPanel);
        gui.setItem(1,9,_landmarkPanel);

        gui.setItem(2,1,_landmarkPanel);

        gui.setItem(2,6,_collectRessources);
        gui.setItem(2,8,_removeTownButton);

        gui.setItem(2,9,_landmarkPanel);

        gui.setItem(3,1, IGUI.CreateBackArrow(player,Player::closeInventory));
        gui.setItem(3,2,_landmarkPanel);
        gui.setItem(3,3,_landmarkPanel);
        gui.setItem(3,4,_landmarkPanel);
        gui.setItem(3,5,_landmarkPanel);
        gui.setItem(3,6,_landmarkPanel);
        gui.setItem(3,7,_landmarkPanel);
        gui.setItem(3,8, _landmarkPanel);
        gui.setItem(3,9,_landmarkPanel);



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

    private static void OpenConfirmMenu(Player player, String confirmLore, Consumer<Void> confirmAction, Consumer<Void> returnAction) {

        Gui gui = IGUI.createChestGui("Confirm action", 3);

        ItemStack confirm = HeadUtils.makeSkull(Lang.GENERIC_CONFIRM_ACTION.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNhNDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIwZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=",
                confirmLore);

        ItemStack cancel = HeadUtils.makeSkull(Lang.GENERIC_CANCEL_ACTION.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==",
                "return to previous menu");

        GuiItem _confirm = ItemBuilder.from(confirm).asGuiItem(event -> {
            event.setCancelled(true);
            confirmAction.accept(null);
        });

        GuiItem _cancel = ItemBuilder.from(cancel).asGuiItem(event -> {
            event.setCancelled(true);
            returnAction.accept(null);
        });

        gui.setItem(2,4,_confirm);
        gui.setItem(2,6,_cancel);

        gui.open(player);
    }


}
