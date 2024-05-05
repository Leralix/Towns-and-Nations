package org.tan.TownsAndNations.GUI;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
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
    public static void OpenPlayerProfileMenu(Player player){

        Gui gui = IGUI.createChestGui("Profile",3);


        ItemStack playerHead = HeadUtils.getPlayerHead(Lang.GUI_YOUR_PROFILE.get(),player);
        ItemStack goldPurse = HeadUtils.getCustomLoreItem(Material.GOLD_NUGGET, Lang.GUI_YOUR_BALANCE.get(),Lang.GUI_YOUR_BALANCE_DESC1.get(EconomyUtil.getBalance(player)));
        ItemStack properties = HeadUtils.getCustomLoreItem(Material.OAK_HANGING_SIGN, Lang.GUI_PLAYER_MANAGE_PROPERTIES.get(),Lang.GUI_PLAYER_MANAGE_PROPERTIES_DESC1.get());


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
    public static void OpenPropertyManagerRentMenu(Player player, PropertyData propertyData) {
        int nRows = 4;

        Gui gui = IGUI.createChestGui("Property " + propertyData.getName(), nRows);

        ItemStack propertyIcon = propertyData.getIcon();

        ItemStack stopRentingProperty = HeadUtils.getCustomLoreItem(Material.BARRIER, Lang.GUI_PROPERTY_STOP_RENTING_PROPERTY.get());
        HeadUtils.setLore(stopRentingProperty, Lang.GUI_PROPERTY_STOP_RENTING_PROPERTY_DESC1.get());


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
    public static void OpenPropertyManagerMenu(Player player, PropertyData propertyData){
        int nRows = 4;

        Gui gui = IGUI.createChestGui("Property " + propertyData.getName(),nRows);


        ItemStack propertyIcon = propertyData.getIcon();

        ItemStack changeName = HeadUtils.getCustomLoreItem(
                Material.NAME_TAG,
                Lang.GUI_PROPERTY_CHANGE_NAME.get(),
                Lang.GUI_PROPERTY_CHANGE_NAME_DESC1.get(propertyData.getName())
        );

        ItemStack changeDescription = HeadUtils.getCustomLoreItem(
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

        ItemStack drawnBox = HeadUtils.makeSkull(Lang.GUI_PROPERTY_DRAWN_BOX.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzc3ZDRhMjA2ZDc3NTdmNDc5ZjMzMmVjMWEyYmJiZWU1N2NlZjk3NTY4ZGQ4OGRmODFmNDg2NGFlZTdkM2Q5OCJ9fX0=");

        HeadUtils.setLore(drawnBox,
                Lang.GUI_PROPERTY_DRAWN_BOX_DESC1.get()
        );

        ItemStack deleteProperty = HeadUtils.getCustomLoreItem(Material.BARRIER,Lang.GUI_PROPERTY_DELETE_PROPERTY.get());
        HeadUtils.setLore(deleteProperty, Lang.GUI_PROPERTY_DELETE_PROPERTY_DESC1.get());


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

        if(propertyData.isRented()){
            ItemStack renterIcon = HeadUtils.getPlayerHead(Lang.GUI_PROPERTY_RENTED_BY.get(propertyData.getRenter().getName()),propertyData.getOfflineRenter());
            HeadUtils.setLore(renterIcon,Lang.GUI_PROPERTY_RIGHT_CLICK_TO_EXPEL_RENTER.get());
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
        gui.setItem(3,8,_deleteProperty);



        gui.setItem(nRows,1, IGUI.CreateBackArrow(player,p -> OpenPlayerPropertiesMenu(player)));

        gui.open(player);
    }
    public static void OpenPropertyBuyMenu(Player player, PropertyData propertyData) {
        Gui gui = IGUI.createChestGui("Property " + propertyData.getName(),3);

        ItemStack propertyIcon = propertyData.getIcon();


        if(propertyData.isForRent()){
            ItemStack confirmRent = HeadUtils.makeSkull(Lang.CONFIRM_RENT.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=");
            ItemStack cancelRent = HeadUtils.makeSkull(Lang.CANCEL_RENT.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc1NDgzNjJhMjRjMGZhODQ1M2U0ZDkzZTY4YzU5NjlkZGJkZTU3YmY2NjY2YzAzMTljMWVkMWU4NGQ4OTA2NSJ9fX0=");

            HeadUtils.setLore(confirmRent,
                    Lang.CONFIRM_RENT_DESC1.get(),
                    Lang.CONFIRM_RENT_DESC2.get(propertyData.getRentPrice())
            );

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
            ItemStack confirmRent = HeadUtils.makeSkull(Lang.CONFIRM_SALE.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=");
            ItemStack cancelRent = HeadUtils.makeSkull(Lang.CANCEL_SALE.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc1NDgzNjJhMjRjMGZhODQ1M2U0ZDkzZTY4YzU5NjlkZGJkZTU3YmY2NjY2YzAzMTljMWVkMWU4NGQ4OTA2NSJ9fX0=");

            HeadUtils.setLore(confirmRent,
                    Lang.CONFIRM_SALE_DESC1.get(),
                    Lang.CONFIRM_SALE_DESC2.get(propertyData.getBuyingPrice())
            );

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



        GuiItem _propertyIcon = ItemBuilder.from(propertyIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });

        gui.setItem(1,5, _propertyIcon);

        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> player.closeInventory()));

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
            OpenSearchTownMenu(player,0);
        });

        gui.setItem(11, _create);
        gui.setItem(15, _join);
        gui.setItem(18, IGUI.CreateBackArrow(player,p -> OpenMainMenu(player)));

        gui.open(player);
    }
    public static void OpenSearchTownMenu(Player player, int page) {

        Gui gui = IGUI.createChestGui("Town list | page " + (page + 1),6);

        int pageSize = 45;
        int startIndex = page * pageSize;
        boolean lastPage;
        int numberOfTowns = TownDataStorage.getTownMap().size();

        int endIndex;
        if(startIndex + pageSize > numberOfTowns){
            endIndex = numberOfTowns;
            lastPage = true;
        }
        else {
            lastPage = false;
            endIndex = startIndex + pageSize;
        }



        Collection<TownData> townDataStorage = getTownMap().values();

        int slot = 0;

        for (int i = startIndex; i < endIndex; i++) {
            TownData townData = (TownData) townDataStorage.toArray()[i];

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

            gui.setItem(slot, _townIteration);
            slot++;

        }
        GuiItem panel = ItemBuilder.from(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).asGuiItem(event -> event.setCancelled(true));

        ItemStack nextPageButton = HeadUtils.makeSkull(
                Lang.GUI_NEXT_PAGE.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2MjYyYWYxZDVmNDE0YzU5NzA1NWMyMmUzOWNjZTE0OGU1ZWRiZWM0NTU1OWEyZDZiODhjOGQ2N2I5MmVhNiJ9fX0="
        );

        ItemStack previousPageButton = HeadUtils.makeSkull(
                Lang.GUI_PREVIOUS_PAGE.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQyZmRlOGI4MmU4YzFiOGMyMmIyMjY3OTk4M2ZlMzVjYjc2YTc5Nzc4NDI5YmRhZGFiYzM5N2ZkMTUwNjEifX19"
        );

        GuiItem _previous = ItemBuilder.from(previousPageButton).asGuiItem(event -> {
            if(page == 0){
                event.setCancelled(true);
                return;
            }
            OpenTownChunkMobSettings(player,page-1);
        });

        GuiItem _next = ItemBuilder.from(nextPageButton).asGuiItem(event -> {
            if(lastPage) {
                event.setCancelled(true);
                return;
            }
            OpenTownChunkMobSettings(player,page+1);
        });


        gui.setItem(6,1, IGUI.CreateBackArrow(player,p -> OpenTownMenuNoTown(player)));
        gui.setItem(6,2, panel);
        gui.setItem(6,3, panel);
        gui.setItem(6,5, panel);
        gui.setItem(6,6, panel);
        gui.setItem(6,7, _previous);
        gui.setItem(6,8, _next);
        gui.setItem(6,9, panel);


        gui.open(player);
    }
    public static void OpenTownMenuHaveTown(Player player) {
        int nRows = 4;
        Gui gui = IGUI.createChestGui("Town",nRows);

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

        ItemStack memberIcon = HeadUtils.makeSkull(Lang.GUI_TOWN_MEMBERS_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Q0ZDQ5NmIxZGEwNzUzNmM5NGMxMzEyNGE1ODMzZWJlMGM1MzgyYzhhMzM2YWFkODQ2YzY4MWEyOGQ5MzU2MyJ9fX0=");
        HeadUtils.setLore(memberIcon, Lang.GUI_TOWN_MEMBERS_ICON_DESC1.get());

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

        ItemStack propertyIcon = HeadUtils.getCustomLoreItem(Material.OAK_HANGING_SIGN, Lang.GUI_TOWN_PROPERTIES_ICON.get());
        HeadUtils.setLore(propertyIcon, Lang.GUI_TOWN_PROPERTIES_ICON_DESC1.get());

        GuiItem _townIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> {
            event.setCancelled(true);

            if(!playerStat.isTownLeader())
                return;
            if(event.getCursor() == null)
                return;

            Material itemMaterial = event.getCursor().getType();
            if(itemMaterial == Material.AIR ){
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
            OpenTownMenuOtherTown(player, 0);
        });
        GuiItem _relationIcon = ItemBuilder.from(RelationIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelations(player);
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
            OpenTownPropertiesMenu(player);
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
        gui.setItem(nRows,1, IGUI.CreateBackArrow(player,p -> OpenMainMenu(player)));

        gui.open(player);
    }
    public static void OpenTownMenuOtherTown(Player player, int page) {
        Gui gui = IGUI.createChestGui("Town list | page " + (page + 1),6);

        int pageSize = 45;
        int startIndex = page * pageSize;
        boolean lastPage;

        int endIndex;
        if(startIndex + pageSize > TownDataStorage.getNumberOfTown()){
            endIndex = TownDataStorage.getNumberOfTown();
            lastPage = true;
        }
        else {
            lastPage = false;
            endIndex = startIndex + pageSize;
        }

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(playerStat);

        Collection<TownData> townDataStorage = getTownMap().values();

        int slot = 0;

        for (int i = startIndex; i < endIndex; i++) {
            TownData otherTown = (TownData) townDataStorage.toArray()[i];


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

            gui.setItem(slot, _townIteration);
            slot++;
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
            if(page == 0){
                event.setCancelled(true);
                return;
            }
            OpenTownChunkMobSettings(player,page-1);
        });

        GuiItem _next = ItemBuilder.from(nextPageButton).asGuiItem(event -> {
            if(lastPage) {
                event.setCancelled(true);
                return;
            }
            OpenTownChunkMobSettings(player,page+1);
        });

        GuiItem panel = ItemBuilder.from(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).asGuiItem(event -> event.setCancelled(true));

        gui.setItem(6,1, IGUI.CreateBackArrow(player,p -> OpenTownMenuHaveTown(player)));
        gui.setItem(6,2, panel);
        gui.setItem(6,3, panel);
        gui.setItem(6,4, panel);
        gui.setItem(6,5, panel);
        gui.setItem(6,6, panel);
        gui.setItem(6,7, _previous);
        gui.setItem(6,8, _next);
        gui.setItem(6,9, panel);




        gui.open(player);
    }
    public static void OpenTownMemberList(Player player) {

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(playerStat);

        int rowSize = Math.min(playerTown.getPlayerList().size() / 9 + 3,6);

        Gui gui = IGUI.createChestGui("Town",rowSize);



        int i = 0;
        for (String playerUUID: playerTown.getPlayerList()) {

            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            PlayerData playerIterateData = PlayerDataStorage.get(playerUUID);

            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate);
            HeadUtils.setLore(
                    playerHead,
                    Lang.GUI_TOWN_MEMBER_DESC1.get(playerIterateData.getTownRank().getColoredName()),
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

        gui.setItem(rowSize,1, IGUI.CreateBackArrow(player,p -> OpenTownMenuHaveTown(player)));
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
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    if(!town.canAddMorePlayer()){
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
        ItemStack itemStack = HeadUtils.getCustomLoreItem(Material.LIME_STAINED_GLASS_PANE,"");
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
            ItemStack townRankItemStack = HeadUtils.getCustomLoreItem(townMaterial, townRank.getColoredName());
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

        TownData town = TownDataStorage.get(player);
        TownRank townRank = town.getRank(rankID);

        Gui gui = IGUI.createChestGui("Town - Rank " + townRank.getName(),4);


        boolean isDefaultRank = town.getTownDefaultRankName().equals(townRank.getName());

        ItemStack roleIcon = HeadUtils.getCustomLoreItem(
                Material.getMaterial(townRank.getRankIconName()),
                Lang.GUI_TOWN_MEMBERS_ROLE_NAME.get(townRank.getColoredName()),
                Lang.GUI_TOWN_MEMBERS_ROLE_NAME_DESC1.get());

        ItemStack roleRankIcon = townRank.getRankEnum().getRankGuiIcon();
        ItemStack membersRank = HeadUtils.makeSkull(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I0M2IyMzE4OWRjZjEzMjZkYTQyNTNkMWQ3NTgyZWY1YWQyOWY2YzI3YjE3MWZlYjE3ZTMxZDA4NGUzYTdkIn19fQ==");

        ArrayList<String> playerNames = new ArrayList<>();
        playerNames.add(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO_DESC1.get());
        for (String playerUUID : townRank.getPlayers(town.getID())) {
            String playerName = PlayerDataStorage.get(playerUUID).getName();
            playerNames.add("-" + Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO_DESC.get(playerName));
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
                townRank.setRankIconName(town.getID(), itemMaterial.toString());
                OpenTownRankManager(player, rankID);
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_CHANGED_ICON_SUCCESS.get());
            }
        });

        GuiItem _roleRankIcon = ItemBuilder.from(roleRankIcon).asGuiItem(event -> {
            townRank.incrementLevel(town.getID());
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
            townRank.swapPayingTaxes(town.getID());
            OpenTownRankManager(player,rankID);
            event.setCancelled(true);
        });
        GuiItem _makeRankDefault = ItemBuilder.from(makeRankDefault).asGuiItem(event -> {
            event.setCancelled(true);

            if(isDefaultRank){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_ALREADY_DEFAULT.get());
            }
            else{
                town.setTownDefaultRankID(rankID);
                OpenTownRankManager(player,rankID);
            }
        });

        GuiItem _removeRank = ItemBuilder.from(removeRank).asGuiItem(event -> {
            event.setCancelled(true);

            if(townRank.getNumberOfPlayer(town.getID()) != 0){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_DELETE_ERROR_NOT_EMPTY.get());
            }
            else if(town.getTownDefaultRankID() == rankID){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_DELETE_ERROR_DEFAULT.get());
            }
            else{
                town.removeRank(townRank.getID());
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

            townRank.removeFromSalary(town.getID(), amountToRemove);
            SoundUtil.playSound(player, REMOVE);
            OpenTownRankManager(player, rankID);
        });
        GuiItem _IncreaseSalary = ItemBuilder.from(increaseSalary).asGuiItem(event -> {

            event.setCancelled(true);

            int amountToAdd = event.isShiftClick() ? 10 : 1;

            townRank.addFromSalary(town.getID(), amountToAdd);
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
        ItemStack create_property = HeadUtils.getCustomLoreItem(Material.OAK_HANGING_SIGN, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_CREATE_PROPERTY.get(),(townRank.hasPermission(townID,CREATE_PROPERTY)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        ItemStack manage_property = HeadUtils.getCustomLoreItem(Material.WRITABLE_BOOK, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_PROPERTY.get(),(townRank.hasPermission(townID,MANAGE_PROPERTY)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());

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


        gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenTownRankManager(player,rankID)));

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
        for (TownRank rank : town.getRanks()) {

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
            player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
            player.closeInventory();

            PlayerChatListenerStorage.addPlayer(TOWN_DONATION,player);
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
    public static void OpenTownLevel(Player player,int level){
        Gui gui = IGUI.createChestGui("Town Upgrades | " + (level + 1),6);

        TownData townData = TownDataStorage.get(player);
        TownLevel townLevel = townData.getTownLevel();

        ItemStack whitePanel = HeadUtils.getCustomLoreItem(Material.WHITE_STAINED_GLASS_PANE,"");
        ItemStack iron_bars = HeadUtils.getCustomLoreItem(Material.IRON_BARS,Lang.LEVEL_LOCKED.get());

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
            if(townLevel.getTownLevel() > (i-2 + level)){
                ItemStack filler_green = HeadUtils.getCustomLoreItem(Material.LIME_STAINED_GLASS_PANE,"Level " + (i-1 + level));

                _pannel = ItemBuilder.from(green_level).asGuiItem(event -> event.setCancelled(true));
                _bottompannel = ItemBuilder.from(filler_green).asGuiItem(event -> event.setCancelled(true));
            }
            else if(townLevel.getTownLevel() == (i - 2 + level)){
                _pannel = _iron_bars;
                ItemStack upgradeTownLevel = HeadUtils.getCustomLoreItem(Material.ORANGE_STAINED_GLASS_PANE, Lang.GUI_TOWN_LEVEL_UP.get());
                HeadUtils.setLore(upgradeTownLevel,
                        Lang.GUI_TOWN_LEVEL_UP_DESC1.get(townLevel.getTownLevel()),
                        Lang.GUI_TOWN_LEVEL_UP_DESC2.get(townLevel.getTownLevel() + 1, townLevel.getMoneyRequiredTownLevel())
                );
                _bottompannel = ItemBuilder.from(upgradeTownLevel).asGuiItem(event -> {
                    event.setCancelled(true);
                    upgradeTown(player,townData);
                    OpenTownLevel(player,level);
                });
            }
            else{
                _pannel = _iron_bars;
                ItemStack red_level = HeadUtils.getCustomLoreItem(Material.RED_STAINED_GLASS_PANE,"Town level " + (i + level - 1) + " locked");
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



        gui.setItem(6,1, IGUI.CreateBackArrow(player,p -> OpenTownMenuHaveTown(player)));


        gui.setItem(6,7, _previous);
        gui.setItem(6,8, _next);

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
                playerTown.haveRegion() ? Lang.GUI_TOWN_SETTINGS_QUIT_REGION_DESC1_REGION.get(playerTown.getRegion().getName()) : Lang.GUI_TOWN_SETTINGS_QUIT_REGION_DESC1_NO_REGION.get());
        ItemStack changeChunkColor = HeadUtils.getCustomLoreItem(Material.PURPLE_WOOL,
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC1.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC2.get(getHexColor(playerTown.getChunkColorInHex()) + playerTown.getChunkColorInHex()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC3.get());

        ItemStack changeTag = HeadUtils.getCustomLoreItem(Material.FLOWER_BANNER_PATTERN,
                Lang.GUI_TOWN_SETTINGS_CHANGE_TAG.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TAG_DESC1.get(playerTown.getColoredTag()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TAG_DESC2.get());

        GuiItem _townIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> event.setCancelled(true));

        GuiItem _leaveTown = ItemBuilder.from(leaveTown).asGuiItem(event -> {
            event.setCancelled(true);
            if (playerStat.isTownLeader()) {
                SoundUtil.playSound(player, NOT_ALLOWED);
                player.sendMessage(getTANString() + Lang.CHAT_CANT_LEAVE_TOWN_IF_LEADER.get());
            } else {
                playerTown.removePlayer(playerStat);

                player.sendMessage(getTANString() + Lang.CHAT_PLAYER_LEFT_THE_TOWN.get());
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
            deleteTown(player, playerTown);

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

            if(playerStat.isTownLeader()){
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
            if (!playerTown.haveRegion()) {
                player.sendMessage(getTANString() + Lang.TOWN_NO_REGION.get());
                return;
            }


            RegionData regionData = playerTown.getRegion();

            if (playerTown.isRegionalCapital()){
                player.sendMessage(getTANString() + Lang.NOT_TOWN_LEADER_ERROR.get());
                return;
            }

            regionData.removeTown(playerTown);
            playerTown.removeRegion();
            player.closeInventory();
        });

        GuiItem _changeChunkColor = ItemBuilder.from(changeChunkColor).asGuiItem(event -> {
            event.setCancelled(true);

            if(playerStat.isTownLeader()){
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

            if(playerStat.isTownLeader()){
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
            OpenTownRelation(player,TownRelation.WAR,0);
        });
        GuiItem _EmbargoCategory = ItemBuilder.from(EmbargoCategory).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelation(player,TownRelation.EMBARGO,0);

        });
        GuiItem _NAPCategory = ItemBuilder.from(NAPCategory).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelation(player,TownRelation.NON_AGGRESSION,0);

        });
        GuiItem _AllianceCategory = ItemBuilder.from(AllianceCategory).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelation(player,TownRelation.ALLIANCE,0);
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
    public static void OpenTownRelation(Player player, TownRelation relation, int page) {
        Gui gui = IGUI.createChestGui("Town relation | page " + (page + 1), 6);
        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(playerStat);

        int pageSize = 45;
        int startIndex = page * pageSize;
        boolean lastPage;

        int endIndex;
        if (startIndex + pageSize > playerTown.getTownWithRelation(relation).size()) {
            endIndex = playerTown.getTownWithRelation(relation).size();
            lastPage = true;
        } else {
            lastPage = false;
            endIndex = startIndex + pageSize;
        }
        ArrayList<String> townListID = playerTown.getTownWithRelation(relation);


        int slot = 0;
        if (endIndex > 0){
            for (int i = startIndex; i < endIndex; i++) {
                String otherTownID = townListID.get(i);


                ItemStack townIcon = getTownIconWithInformations(otherTownID);

                if (relation == TownRelation.WAR) {
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

                    if (relation == TownRelation.WAR) {
                        player.sendMessage(getTANString() + Lang.GUI_TOWN_ATTACK_TOWN_EXECUTED.get(TownDataStorage.get(otherTownID).getName()));
                        WarTaggedPlayer.addPlayersToTown(otherTownID, playerTown.getPlayerList());
                        TownDataStorage.get(otherTownID).broadCastMessageWithSound(Lang.GUI_TOWN_ATTACK_TOWN_INFO.get(playerTown.getName()),
                                WAR);
                    }
                });
                gui.setItem(slot, _town);
                slot = i + 1;

            }
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


        GuiItem _previous = ItemBuilder.from(previousPageButton).asGuiItem(event -> {
            if(page == 0){
                event.setCancelled(true);
                return;
            }
            OpenTownChunkMobSettings(player,page-1);
        });

        GuiItem _next = ItemBuilder.from(nextPageButton).asGuiItem(event -> {
            if(lastPage) {
                event.setCancelled(true);
                return;
            }
            OpenTownChunkMobSettings(player,page+1);
        });



        GuiItem _decorativeGlass = ItemBuilder.from(new ItemStack(Material.WHITE_STAINED_GLASS_PANE)).asGuiItem(event -> event.setCancelled(true));

        gui.setItem(6,1, IGUI.CreateBackArrow(player,p -> OpenTownRelations(player)));
        gui.setItem(6,4,_add);
        gui.setItem(6,5,_remove);

        gui.setItem(6,7,_previous);
        gui.setItem(6,8,_next);


        gui.setItem(6,2, _decorativeGlass);
        gui.setItem(6,3, _decorativeGlass);
        gui.setItem(6,6, _decorativeGlass);
        gui.setItem(6,9, _decorativeGlass);

        gui.open(player);
    }
    public static void OpenTownRelationModification(Player player, Action action, TownRelation relation) {
        int nRows = 6;
        Gui gui = IGUI.createChestGui("Town - Relation",nRows);

        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        TownData playerTown = TownDataStorage.get(playerStat);

        LinkedHashMap<String, TownData> allTown = getTownMap();
        ArrayList<String> TownListUUID = playerTown.getTownWithRelation(relation);

        GuiItem _decorativeGlass = ItemBuilder.from(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).asGuiItem(event -> event.setCancelled(true));

        if(action == Action.ADD){
            List<String> townNoRelation = new ArrayList<>(allTown.keySet());
            townNoRelation.removeAll(TownListUUID);
            townNoRelation.remove(playerTown.getID());

            int i = 0;
            for(String otherTownUUID : townNoRelation){
                TownData otherTown = TownDataStorage.get(otherTownUUID);
                ItemStack townIcon = getTownIconWithInformations(otherTownUUID, playerTown.getID());

                GuiItem _town = ItemBuilder.from(townIcon).asGuiItem(event -> {
                    event.setCancelled(true);

                    if(playerTown.haveRelationWith(otherTown)){
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
                        if(otherTownLeaderOnline == null)
                            return;
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
                        OpenTownRelation(player,relation,0);
                    }
                });
                gui.setItem(i, _town);
                i++;
                _decorativeGlass = ItemBuilder.from(new ItemStack(Material.GREEN_STAINED_GLASS_PANE)).asGuiItem(event -> event.setCancelled(true));
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

                        OfflinePlayer otherTownLeader = Bukkit.getOfflinePlayer(UUID.fromString(otherTown.getLeaderID()));

                        if (!otherTownLeader.isOnline()) {
                            player.sendMessage(getTANString() + Lang.LEADER_NOT_ONLINE.get());
                            return;
                        }
                        Player otherTownLeaderOnline = otherTownLeader.getPlayer();
                        if(otherTownLeaderOnline == null)
                            return;

                        player.sendMessage(getTANString() + "Sent to the leader of the other town");

                        TownRelationConfirmStorage.addInvitation(otherTown.getLeaderID(), playerTown.getID(), null);

                        otherTownLeaderOnline.sendMessage(getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_RECEIVED_1.get(playerTown.getName(),"neutral"));
                        ChatUtils.sendClickableCommand(otherTownLeaderOnline,getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_RECEIVED_2.get(),"tan accept "  + playerTown.getID());
                        player.closeInventory();
                    }
                    else{ //Can only be worst relations
                        playerTown.broadCastMessageWithSound(Lang.GUI_TOWN_CHANGED_RELATION_RESUME.get(otherTown.getName(),"neutral"),
                                BAD);
                        otherTown.broadCastMessageWithSound(getTANString() + Lang.GUI_TOWN_CHANGED_RELATION_RESUME.get(playerTown.getName(),"neutral"),
                                BAD);
                        removeTownRelation(playerTown,otherTown,relation);
                    }
                    OpenTownRelation(player,relation,0);
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

        GuiItem _previous = ItemBuilder.from(previousPageButton).asGuiItem(event -> event.setCancelled(true));
        GuiItem _next = ItemBuilder.from(nextPageButton).asGuiItem(event -> event.setCancelled(true));

        gui.setItem(nRows,1, IGUI.CreateBackArrow(player,p -> OpenTownRelation(player,relation,0)));

        gui.setItem(nRows,2, _decorativeGlass);
        gui.setItem(nRows,3, _decorativeGlass);
        gui.setItem(nRows,4, _decorativeGlass);
        gui.setItem(nRows,5, _decorativeGlass);
        gui.setItem(nRows,6, _decorativeGlass);

        gui.setItem(nRows,7,_previous);
        gui.setItem(nRows,8,_next);
        gui.setItem(nRows,9, _decorativeGlass);


        gui.open(player);
    }
    public static void OpenTownChunk(Player player) {
        Gui gui = IGUI.createChestGui("Town",3);

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
                OpenTownChunkMobSettings(player,1);
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
    public static void OpenTownChunkMobSettings(Player player, int page){
        Gui gui = IGUI.createChestGui("Mob settings - Page " + page,6);

        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        TownData townData = TownDataStorage.get(player);
        ClaimedChunkSettings chunkSettings = townData.getChunkSettings();

        int pageSize = 45;
        int startIndex = (page -1) * pageSize;
        boolean lastPage;

        int endIndex;
        if(startIndex + pageSize > MobChunkSpawnStorage.getMobSpawnStorage().size()){
            endIndex = MobChunkSpawnStorage.getMobSpawnStorage().size();
            lastPage = true;
        }
        else {
            lastPage = false;
            endIndex = startIndex + pageSize;
        }


        int slot = 0;

        Collection<MobChunkSpawnEnum> mobCollection = MobChunkSpawnStorage.getMobSpawnStorage().values();

        for (int i = startIndex; i < endIndex; i++) {

            MobChunkSpawnEnum mobEnum = (MobChunkSpawnEnum) mobCollection.toArray()[i];
            if(slot >= 45)
                break;

            ItemStack mobIcon = HeadUtils.makeSkull(mobEnum.name(),mobEnum.getTexture());

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

            HeadUtils.setLore(mobIcon,status);

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
                    townData.removeToBalance(cost);
                    SoundUtil.playSound(player,GOOD);
                    upgradeStatus.setUnlocked(true);
                }

                OpenTownChunkMobSettings(player,page);

            });
            gui.setItem(slot, mobItem);
            slot = slot+1;
        }

        GuiItem panel = ItemBuilder.from(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).asGuiItem(event -> event.setCancelled(true));

        ItemStack nextPageButton = HeadUtils.makeSkull(
                Lang.GUI_NEXT_PAGE.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2MjYyYWYxZDVmNDE0YzU5NzA1NWMyMmUzOWNjZTE0OGU1ZWRiZWM0NTU1OWEyZDZiODhjOGQ2N2I5MmVhNiJ9fX0="
        );

        ItemStack previousPageButton = HeadUtils.makeSkull(
                Lang.GUI_PREVIOUS_PAGE.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQyZmRlOGI4MmU4YzFiOGMyMmIyMjY3OTk4M2ZlMzVjYjc2YTc5Nzc4NDI5YmRhZGFiYzM5N2ZkMTUwNjEifX19"
        );

        GuiItem _next = ItemBuilder.from(nextPageButton).asGuiItem(event -> {
            if(lastPage) {
                event.setCancelled(true);
                return;
            }
            OpenTownChunkMobSettings(player,page+1);
        });

        GuiItem _previous = ItemBuilder.from(previousPageButton).asGuiItem(event -> {
            if(page == 0){
                event.setCancelled(true);
                return;
            }
            OpenTownChunkMobSettings(player,page-1);
        });


        gui.setItem(6,1, IGUI.CreateBackArrow(player,p -> OpenTownChunk(player)));
        gui.setItem(6,2,panel);
        gui.setItem(6,3,panel);
        gui.setItem(6,4,panel);
        gui.setItem(6,5,panel);
        gui.setItem(6,6,panel);
        gui.setItem(6,7,_previous);
        gui.setItem(6,8,_next);
        gui.setItem(6,9,panel);


        gui.open(player);
    }
    public static void OpenTownPropertiesMenu(Player player){
        int nRows = 6;
        PlayerData playerData = PlayerDataStorage.get(player);
        TownData townData = TownDataStorage.get(playerData);
        Gui gui = IGUI.createChestGui("Properties",6);

        int i = 0;
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
            gui.setItem(i,_property);
            i++;
        }
        gui.setItem(nRows,1, IGUI.CreateBackArrow(player,p -> OpenTownMenuHaveTown(player)));


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
            if(itemMaterial == Material.AIR){
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
        GuiItem _relationIcon = ItemBuilder.from(RelationIcon).asGuiItem(event -> event.setCancelled(true));

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

            GuiItem _region = ItemBuilder.from(regionIcon).asGuiItem(event -> event.setCancelled(true));
            gui.setItem(i, _region);
            i = i+1;
        }

        if(isTownMenu)
            gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenRegionMenu(player)));
        else
            gui.setItem(3,1, IGUI.CreateBackArrow(player,p -> OpenNoRegionMenu(player)));

        gui.open(player);
    }
    private static void OpenTownInRegion(Player player){

        Gui gui = IGUI.createChestGui("Region",4);
        PlayerData playerData = PlayerDataStorage.get(player);
        RegionData regionData = RegionDataStorage.get(player);

        for (TownData townData : regionData.getTownsInRegion()){
            ItemStack townIcon = getTownIconWithInformations(townData);

            GuiItem _townIcon = ItemBuilder.from(townIcon).asGuiItem(event -> event.setCancelled(true));
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

                if(regionData.isTownInRegion(townData))
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
            for (TownData townData : regionData.getTownsInRegion()){
                ItemStack townIcon = getTownIconWithInformations(townData);
                HeadUtils.addLore(townIcon, Lang.GUI_REGION_INVITE_TOWN_DESC1.get());

                GuiItem _townIcon = ItemBuilder.from(townIcon).asGuiItem(event -> {
                    event.setCancelled(true);

                    if(regionData.getCapitalID().equals(townData.getID())){
                        player.sendMessage(getTANString() + Lang.CANT_KICK_REGIONAL_CAPITAL.get(townData.getName()));
                        return;
                    }
                    regionData.broadcastMessageWithSound(
                            Lang.GUI_REGION_KICK_TOWN_BROADCAST.get(townData.getName()),
                            BAD);
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

        GuiItem _regionIcon = ItemBuilder.from(regionIcon).asGuiItem(event -> event.setCancelled(true));

        GuiItem _deleteRegion = ItemBuilder.from(deleteRegion).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerStat.isTownLeader() && playerRegion.isCapital(playerTown)){
                player.sendMessage(getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get());
            }
            RegionDataStorage.deleteRegion(player, playerRegion);
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
            if(!playerStat.isTownLeader() || !playerRegion.isCapital(playerTown)){
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

        GuiItem _goldIcon = ItemBuilder.from(goldIcon).asGuiItem(event -> event.setCancelled(true));

        GuiItem _goldSpendingIcon = ItemBuilder.from(goldSpendingIcon).asGuiItem(event -> event.setCancelled(true));

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

                if(townData.getID().equals(regionData.getCapital().getID()))
                    continue;
                ItemStack regionIcon = getRegionIcon(regionData);

                GuiItem _region = ItemBuilder.from(regionIcon).asGuiItem(event -> {
                    event.setCancelled(true);
                    FileUtil.addLineToHistory(Lang.HISTORY_REGION_CAPITAL_CHANGED.get(player.getName(), regionData.getCapital().getName(), townData.getName() ));
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
