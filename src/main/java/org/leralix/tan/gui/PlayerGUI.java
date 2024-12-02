package org.leralix.tan.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.*;
import org.leralix.tan.dataclass.newhistory.TransactionHistory;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.territory.economy.Budget;
import org.leralix.tan.dataclass.territory.permission.RelationPermission;
import org.leralix.tan.dataclass.wars.CreateAttackData;
import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.dataclass.wars.WarRole;
import org.leralix.tan.dataclass.wars.wargoals.ConquerWarGoal;
import org.leralix.tan.dataclass.wars.wargoals.LiberateWarGoal;
import org.leralix.tan.dataclass.wars.wargoals.SubjugateWarGoal;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.enums.*;
import org.leralix.tan.lang.DynamicLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chatlistener.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chatlistener.events.*;
import org.leralix.tan.newsletter.NewsletterScope;
import org.leralix.tan.newsletter.NewsletterStorage;
import org.leralix.tan.newsletter.news.PlayerJoinRequestNL;
import org.leralix.tan.storage.MobChunkSpawnStorage;
import org.leralix.tan.storage.PlayerSelectPropertyPositionStorage;
import org.leralix.tan.storage.legacy.UpgradeStorage;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.utils.*;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import java.util.*;
import java.util.function.Consumer;

import static org.leralix.tan.enums.RolePermission.*;
import static org.leralix.tan.enums.SoundEnum.*;
import static org.leralix.tan.storage.MobChunkSpawnStorage.getMobSpawnCost;
import static org.leralix.tan.utils.ChatUtils.getTANString;
import static org.leralix.tan.utils.GuiUtil.createIterator;
import static org.leralix.tan.utils.HeadUtils.getRegionIcon;

public class PlayerGUI implements IGUI {

    private PlayerGUI() {
        throw new IllegalStateException("Utility class");
    }

    public static void openMainMenu(Player player){
        Gui gui = IGUI.createChestGui("Main menu",3);


        PlayerData playerStat = PlayerDataStorage.get(player);
        boolean playerHaveTown = playerStat.haveTown();
        boolean playerHaveRegion = playerStat.haveRegion();

        TownData town = TownDataStorage.get(playerStat);
        RegionData region = null;
        if(playerHaveRegion){
            region = town.getOverlord();
        }

        ItemStack kingdomIcon = HeadUtils.makeSkullB64(Lang.GUI_KINGDOM_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY5MTk2YjMzMGM2Yjg5NjJmMjNhZDU2MjdmYjZlY2NlNDcyZWFmNWM5ZDQ0Zjc5MWY2NzA5YzdkMGY0ZGVjZSJ9fX0=",
                Lang.GUI_KINGDOM_ICON_DESC1.get());
        ItemStack regionIcon = HeadUtils.makeSkullB64(Lang.GUI_REGION_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljMTgzMmU0ZWY1YzRhZDljNTE5ZDE5NGIxOTg1MDMwZDI1NzkxNDMzNGFhZjI3NDVjOWRmZDYxMWQ2ZDYxZCJ9fX0=");
        if(playerHaveRegion) {
            HeadUtils.addLore(regionIcon, Lang.GUI_REGION_ICON_DESC1_REGION.get(region.getColoredName()), Lang.GUI_REGION_ICON_DESC2_REGION.get(region.getRank(player).getColoredName()));
        }
        else {
            HeadUtils.addLore(regionIcon, Lang.GUI_REGION_ICON_DESC1_NO_REGION.get());
        }

        ItemStack townIcon = HeadUtils.makeSkullB64(Lang.GUI_TOWN_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=");
        if(playerHaveTown) {
            HeadUtils.addLore(townIcon, Lang.GUI_TOWN_ICON_DESC1_HAVE_TOWN.get(town.getColoredName()), Lang.GUI_TOWN_ICON_DESC2_HAVE_TOWN.get(town.getRank(player).getColoredName()));
        }
        else {
            HeadUtils.addLore(townIcon, Lang.GUI_TOWN_ICON_DESC1_NO_TOWN.get());
        }

        ItemStack profileIcon = HeadUtils.getPlayerHeadInformation(player);

        GuiItem kingdomGui = ItemBuilder.from(kingdomIcon).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(getTANString() + Lang.GUI_WARNING_STILL_IN_DEV.get());
        });
        GuiItem regionGui = ItemBuilder.from(regionIcon).asGuiItem(event -> {
            event.setCancelled(true);
            dispatchPlayerRegion(player);
        });
        GuiItem townGui = ItemBuilder.from(townIcon).asGuiItem(event -> {
            event.setCancelled(true);
            dispatchPlayerTown(player);
        });
        GuiItem playerGui = ItemBuilder.from(profileIcon).asGuiItem(event -> {
            event.setCancelled(true);
            openPlayerProfileMenu(player);
        });


        int slotKingdom = 2;
        int slotRegion = 4;
        int slotTown = 6;
        int slotPlayer = 8;

        if(ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("EnableKingdom",true) &&
                ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("EnableRegion",true)) {
            gui.setItem(2, slotKingdom, kingdomGui);
        }

        if(ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("EnableRegion",true)){
            gui.setItem(2,slotRegion,regionGui);
        }
        else {
            slotTown = 4;
            slotPlayer = 6;
        }

        gui.setItem(2,slotTown,townGui);
        gui.setItem(2,slotPlayer,playerGui);
        gui.setItem(2,slotPlayer,playerGui);
        gui.setItem(3,1,IGUI.createBackArrow(player, p -> player.closeInventory()));

        gui.open(player);
    }
    public static void dispatchPlayerRegion(Player player) {
        if(PlayerDataStorage.get(player).haveRegion()) {
            openRegionMenu(player);
        }
        else {
            openNoRegionMenu(player);
        }
    }
    public static void dispatchPlayerTown(Player player){
        if(PlayerDataStorage.get(player).haveTown()){
            openTownMenu(player);
        }
        else{
            openNoTownMenu(player);
        }
    }
    public static void openPlayerProfileMenu(Player player){

        Gui gui = IGUI.createChestGui("Player profile",3);


        ItemStack playerHead = HeadUtils.getPlayerHead(Lang.GUI_YOUR_PROFILE.get(),player);
        ItemStack treasuryIcon = HeadUtils.createCustomItemStack(Material.GOLD_NUGGET, Lang.GUI_YOUR_BALANCE.get(),Lang.GUI_YOUR_BALANCE_DESC1.get(StringUtil.formatMoney(EconomyUtil.getBalance(player))));
        ItemStack propertiesIcon = HeadUtils.createCustomItemStack(Material.OAK_HANGING_SIGN, Lang.GUI_PLAYER_MANAGE_PROPERTIES.get(),Lang.GUI_PLAYER_MANAGE_PROPERTIES_DESC1.get());
        ItemStack newsletterIcon = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK, Lang.GUI_PLAYER_NEWSLETTER.get(),Lang.GUI_PLAYER_NEWSLETTER_DESC1.get());

        GuiItem playerGui = ItemBuilder.from(playerHead).asGuiItem(event -> event.setCancelled(true));
        GuiItem treasuryGui = ItemBuilder.from(treasuryIcon).asGuiItem(event -> event.setCancelled(true));
        GuiItem propertiesGui = ItemBuilder.from(propertiesIcon).asGuiItem(event -> {
            event.setCancelled(true);
            openPlayerPropertiesMenu(player);
        });
        GuiItem newsletterGui = ItemBuilder.from(newsletterIcon).asGuiItem(event -> {
            event.setCancelled(true);
            openNewsletter(player,0, NewsletterScope.SHOW_ONLY_UNREAD);
        });

        gui.setItem(1,5, playerGui);
        gui.setItem(2,2, treasuryGui);
        gui.setItem(2,4, propertiesGui);
        gui.setItem(2,6, newsletterGui);


        gui.setItem(18, IGUI.createBackArrow(player, p -> openMainMenu(player)));

        gui.open(player);
    }

    public static void openNewsletter(Player player, int page, NewsletterScope scope){
        int nRows = 6;
        Gui gui = IGUI.createChestGui("Newsletter",nRows);

        ArrayList<GuiItem> guiItems = new ArrayList<>(NewsletterStorage.getNewsletterForPlayer(player, scope, p -> openNewsletter(player, page, scope) ));

        createIterator(gui, guiItems, 0, player,
                p -> openPlayerProfileMenu(player),
                p -> openNewsletter(player, page + 1,scope),
                p -> openNewsletter(player, page - 1,scope)
        );

        ItemStack checkScope = HeadUtils.createCustomItemStack(Material.NAME_TAG,scope.getName(),
                Lang.GUI_GENERIC_CLICK_TO_SWITCH_SCOPE.get());
        GuiItem checkScopeGui = ItemBuilder.from(checkScope).asGuiItem(event -> {
            openNewsletter(player,0,scope.getNextScope());
            event.setCancelled(true);
        });

        gui.setItem(nRows,5,checkScopeGui);

        gui.open(player);
    }
    public static void openPlayerPropertiesMenu(Player player){
        int nRows = 6;
        Gui gui = IGUI.createChestGui("Properties of " + player.getName(),nRows);

        PlayerData playerData = PlayerDataStorage.get(player);

        int i = 0;
        for (PropertyData propertyData : playerData.getProperties()){
            ItemStack property = propertyData.getIcon();
            GuiItem propertyGui = ItemBuilder.from(property).asGuiItem(event -> {
                openPropertyManagerMenu(player, propertyData);
                event.setCancelled(true);
            });
            gui.setItem(i,propertyGui);
            i++;
        }

        ItemStack newProperty = HeadUtils.makeSkullB64(
                Lang.GUI_PLAYER_NEW_PROPERTY.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19"
        );

        GuiItem newPropertyGui = ItemBuilder.from(newProperty).asGuiItem(event -> {
            event.setCancelled(true);

            TownData playerTown = playerData.getTown();

            if(!playerTown.doesPlayerHavePermission(playerData, CREATE_PROPERTY)){
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

        gui.setItem(nRows,3, newPropertyGui);
        gui.setItem(nRows,1, IGUI.createBackArrow(player, p -> openMainMenu(player)));

        gui.open(player);
    }
    public static void openPropertyManagerRentMenu(Player player, @NotNull PropertyData propertyData) {
        int nRows = 4;

        Gui gui = IGUI.createChestGui("Property " + propertyData.getName(), nRows);

        ItemStack propertyIcon = propertyData.getIcon();

        ItemStack stopRentingProperty = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.GUI_PROPERTY_STOP_RENTING_PROPERTY.get(),
                Lang.GUI_PROPERTY_STOP_RENTING_PROPERTY_DESC1.get());


        GuiItem propertyButton = ItemBuilder.from(propertyIcon).asGuiItem(event -> event.setCancelled(true));

        GuiItem stopRentingButton = ItemBuilder.from(stopRentingProperty).asGuiItem(event -> {
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

        gui.setItem(1,5,propertyButton);
        gui.setItem(2,7,stopRentingButton);

        gui.setItem(nRows,1, IGUI.createBackArrow(player, p -> player.closeInventory()));


        gui.open(player);
    }
    public static void openPropertyManagerMenu(Player player, @NotNull PropertyData propertyData){
        int nRows = 4;

        Gui gui = IGUI.createChestGui("Property " + propertyData.getName(),nRows);


        ItemStack property = propertyData.getIcon();

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
            isForSale = HeadUtils.makeSkullB64(Lang.SELL_PROPERTY.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2UyYTUzMGY0MjcyNmZhN2EzMWVmYWI4ZTQzZGFkZWUxODg5MzdjZjgyNGFmODhlYThlNGM5M2E0OWM1NzI5NCJ9fX0=");
        }
        else{
            isForSale = HeadUtils.makeSkullB64(Lang.SELL_PROPERTY.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVmMDcwOGZjZTVmZmFhNjYwOGNiZWQzZTc4ZWQ5NTgwM2Q4YTg5Mzc3ZDFkOTM4Y2UwYmRjNjFiNmRjOWY0ZiJ9fX0=");
        }
        HeadUtils.setLore(isForSale,
                propertyData.isForSale() ? Lang.GUI_PROPERTY_FOR_SALE.get(): Lang.GUI_PROPERTY_NOT_FOR_SALE.get(),
                Lang.GUI_BUYING_PRICE.get(propertyData.getBuyingPrice()),
                Lang.GUI_LEFT_CLICK_TO_SWITCH_SALE.get(),
                Lang.GUI_RIGHT_CLICK_TO_CHANGE_PRICE.get()
        );



        ItemStack isForRent;
        if(propertyData.isForRent()){
            isForRent = HeadUtils.makeSkullB64(Lang.RENT_PROPERTY.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2UyYTUzMGY0MjcyNmZhN2EzMWVmYWI4ZTQzZGFkZWUxODg5MzdjZjgyNGFmODhlYThlNGM5M2E0OWM1NzI5NCJ9fX0=");
        }
        else{
            isForRent = HeadUtils.makeSkullB64(Lang.RENT_PROPERTY.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVmMDcwOGZjZTVmZmFhNjYwOGNiZWQzZTc4ZWQ5NTgwM2Q4YTg5Mzc3ZDFkOTM4Y2UwYmRjNjFiNmRjOWY0ZiJ9fX0=");
        }
        HeadUtils.setLore(isForRent,
                propertyData.isForRent() ? Lang.GUI_PROPERTY_FOR_RENT.get(): Lang.GUI_PROPERTY_NOT_FOR_RENT.get(),
                Lang.GUI_RENTING_PRICE.get(propertyData.getRentPrice()),
                Lang.GUI_LEFT_CLICK_TO_SWITCH_SALE.get(),
                Lang.GUI_RIGHT_CLICK_TO_CHANGE_PRICE.get()
        );

        ItemStack drawnBox = HeadUtils.makeSkullB64(Lang.GUI_PROPERTY_DRAWN_BOX.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzc3ZDRhMjA2ZDc3NTdmNDc5ZjMzMmVjMWEyYmJiZWU1N2NlZjk3NTY4ZGQ4OGRmODFmNDg2NGFlZTdkM2Q5OCJ9fX0=",
                Lang.GUI_PROPERTY_DRAWN_BOX_DESC1.get());

        ItemStack deleteProperty = HeadUtils.createCustomItemStack(Material.BARRIER,Lang.GUI_PROPERTY_DELETE_PROPERTY.get(),
                Lang.GUI_PROPERTY_DELETE_PROPERTY_DESC1.get());

        ItemStack playerList = HeadUtils.createCustomItemStack(Material.PLAYER_HEAD,Lang.GUI_PROPERTY_PLAYER_LIST.get(),
                Lang.GUI_PROPERTY_PLAYER_LIST_DESC1.get());

        GuiItem propertyIcon = ItemBuilder.from(property).asGuiItem(event -> event.setCancelled(true));

        GuiItem changeNameButton = ItemBuilder.from(changeName).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            PlayerChatListenerStorage.register(player, new ChangePropertyName(propertyData, p -> openPropertyManagerMenu(player, propertyData)));
        });
        GuiItem changeDescButton = ItemBuilder.from(changeDescription).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            PlayerChatListenerStorage.register(player, new ChangePropertyDescription(propertyData, p -> openPropertyManagerMenu(player, propertyData)));
        });


        GuiItem drawBoxButton = ItemBuilder.from(drawnBox).asGuiItem(event -> {
            event.setCancelled(true);
            player.closeInventory();
            propertyData.showBox(player);
        });

        GuiItem isForSaleButton = ItemBuilder.from(isForSale).asGuiItem(event -> {
            event.setCancelled(true);

            if(event.getClick() == ClickType.RIGHT){
                event.setCancelled(true);
                player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
                PlayerChatListenerStorage.register(player, new ChangePropertySalePrice(propertyData));
            }
            else if (event.getClick() == ClickType.LEFT){
                if(propertyData.isRented()){
                    player.sendMessage(getTANString() + Lang.PROPERTY_ALREADY_RENTED.get());
                    return;
                }
                propertyData.swapIsForSale();
                openPropertyManagerMenu(player,propertyData);
            }
        });
        GuiItem isForRentButton = ItemBuilder.from(isForRent).asGuiItem(event -> {
            event.setCancelled(true);

            if(event.getClick() == ClickType.RIGHT){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
                PlayerChatListenerStorage.register(player, new ChangePropertyRentPrice(propertyData, p -> openPropertyManagerMenu(player, propertyData)));
            }
            else if (event.getClick() == ClickType.LEFT){
                if(propertyData.isRented()){
                    player.sendMessage(getTANString() + Lang.PROPERTY_ALREADY_RENTED.get());
                    return;
                }
                propertyData.swapIsRent();
                openPropertyManagerMenu(player,propertyData);
            }
        });

        GuiItem deleteButton = ItemBuilder.from(deleteProperty).asGuiItem(event -> {
            event.setCancelled(true);
            propertyData.delete();
            openPlayerPropertiesMenu(player);
        });

        GuiItem openListButton = ItemBuilder.from(playerList).asGuiItem(event -> {
            event.setCancelled(true);
            openPlayerPropertyPlayerList(player, propertyData, 0);
        });

        if(propertyData.isRented()){
            ItemStack renterIcon = HeadUtils.getPlayerHead(
                    Lang.GUI_PROPERTY_RENTED_BY.get(propertyData.getRenter().getName()),
                    propertyData.getOfflineRenter(),
                    Lang.GUI_PROPERTY_RIGHT_CLICK_TO_EXPEL_RENTER.get());
            GuiItem renterButton = ItemBuilder.from(renterIcon).asGuiItem(event -> {
                event.setCancelled(true);

                Player renter = propertyData.getRenterPlayer();
                propertyData.expelRenter(false);

                player.sendMessage(getTANString() + Lang.PROPERTY_RENTER_EXPELLED_OWNER_SIDE.get());
                SoundUtil.playSound(player,MINOR_GOOD);

                if(renter != null){
                    renter.sendMessage(getTANString() + Lang.PROPERTY_RENTER_EXPELLED_RENTER_SIDE.get(propertyData.getName()));
                    SoundUtil.playSound(renter,MINOR_BAD);
                }

                openPropertyManagerMenu(player,propertyData);
            });
            gui.setItem(3,7,renterButton);
        }


        gui.setItem(1,5,propertyIcon);
        gui.setItem(2,2,changeNameButton);
        gui.setItem(2,3,changeDescButton);

        gui.setItem(2,5,drawBoxButton);

        gui.setItem(2,7,isForSaleButton);
        gui.setItem(2,8,isForRentButton);

        gui.setItem(3, 2, openListButton);
        gui.setItem(3,8,deleteButton);



        gui.setItem(nRows,1, IGUI.createBackArrow(player, p -> openPlayerPropertiesMenu(player)));

        gui.open(player);
    }
    private static void openPlayerPropertyPlayerList(Player player, PropertyData propertyData, int page) {

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
                openPlayerPropertyPlayerList(player, propertyData, page);

                SoundUtil.playSound(player,MINOR_GOOD);
                player.sendMessage(Lang.PLAYER_REMOVED_FROM_PROPERTY.get(offlinePlayer.getName()));
            });
            guiItems.add(headGui);
        }
        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> openPropertyManagerMenu(player, propertyData),
                p -> openPlayerPropertyPlayerList(player, propertyData, page + 1),
                p -> openPlayerPropertyPlayerList(player, propertyData, page - 1)
                );

        ItemStack addPlayer = HeadUtils.makeSkullB64(Lang.GUI_PROPERTY_AUTHORIZE_PLAYER.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
        GuiItem addButton = ItemBuilder.from(addPlayer).asGuiItem(event -> {
            event.setCancelled(true);
            if(!propertyData.canPlayerManageInvites(playerData.getID())){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }
            openPlayerPropertyAddPlayer(player, propertyData);
        });
        gui.setItem(nRows,4,addButton);

        gui.open(player);

    }
    private static void openPlayerPropertyAddPlayer(Player player, PropertyData propertyData) {
        Gui gui = IGUI.createChestGui("Property " + propertyData.getName(),3);

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for(Player playerIter : Bukkit.getOnlinePlayers()){
            if(playerIter.getUniqueId().equals(player.getUniqueId()) || propertyData.isPlayerAuthorized(playerIter)){
                continue;
            }

            ItemStack playerHead = HeadUtils.getPlayerHead(playerIter);
            GuiItem headGui = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                propertyData.addAuthorizedPlayer(playerIter);
                openPlayerPropertyAddPlayer(player, propertyData);
                SoundUtil.playSound(player,MINOR_GOOD);
                player.sendMessage(Lang.PLAYER_REMOVED_FROM_PROPERTY.get(playerIter.getName()));
            });
            guiItems.add(headGui);

        }

        GuiUtil.createIterator(gui, guiItems, 0, player,
                p -> openPlayerPropertyPlayerList(player, propertyData, 0),
                p -> openPlayerPropertyAddPlayer(player, propertyData),
                p -> openPlayerPropertyAddPlayer(player, propertyData)
        );

        gui.open(player);
    }
    public static void openPropertyBuyMenu(Player player, @NotNull PropertyData propertyData) {
        Gui gui = IGUI.createChestGui("Property " + propertyData.getName(),3);

        ItemStack propertyIcon = propertyData.getIcon();


        if(propertyData.isForRent()){
            ItemStack confirmRent = HeadUtils.makeSkullB64(Lang.CONFIRM_RENT.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=",
                    Lang.CONFIRM_RENT_DESC1.get(),
                    Lang.CONFIRM_RENT_DESC2.get(propertyData.getRentPrice()));
            ItemStack cancelRent = HeadUtils.makeSkullB64(Lang.CANCEL_RENT.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc1NDgzNjJhMjRjMGZhODQ1M2U0ZDkzZTY4YzU5NjlkZGJkZTU3YmY2NjY2YzAzMTljMWVkMWU4NGQ4OTA2NSJ9fX0=");


            GuiItem confirmRentButton = ItemBuilder.from(confirmRent).asGuiItem(event -> {
                event.setCancelled(true);
                propertyData.allocateRenter(player);
                openPropertyManagerRentMenu(player, propertyData);
            });
            GuiItem cancelRentIcon = ItemBuilder.from(cancelRent).asGuiItem(event -> {
                event.setCancelled(true);
                player.closeInventory();
            });

            gui.setItem(2,3, confirmRentButton);
            gui.setItem(2,7, cancelRentIcon);

        }
        else if (propertyData.isForSale()){
            ItemStack confirmRent = HeadUtils.makeSkullB64(Lang.CONFIRM_SALE.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=",
                    Lang.CONFIRM_SALE_DESC1.get(),
                    Lang.CONFIRM_SALE_DESC2.get(propertyData.getBuyingPrice()));
            ItemStack cancelRent = HeadUtils.makeSkullB64(Lang.CANCEL_SALE.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc1NDgzNjJhMjRjMGZhODQ1M2U0ZDkzZTY4YzU5NjlkZGJkZTU3YmY2NjY2YzAzMTljMWVkMWU4NGQ4OTA2NSJ9fX0=");

            GuiItem confirmRentIcon = ItemBuilder.from(confirmRent).asGuiItem(event -> {
                event.setCancelled(true);
                propertyData.buyProperty(player);
            }
            );
            GuiItem cancelRentIcon = ItemBuilder.from(cancelRent).asGuiItem(event -> {
                event.setCancelled(true);
                player.closeInventory();
            });


            gui.setItem(2,3, confirmRentIcon);
            gui.setItem(2,7, cancelRentIcon);
        }



        GuiItem propertyIconButton = ItemBuilder.from(propertyIcon).asGuiItem(event -> event.setCancelled(true));

        gui.setItem(1,5, propertyIconButton);

        gui.setItem(3,1, IGUI.createBackArrow(player, p -> player.closeInventory()));

        gui.open(player);

    }
    public static void openNoTownMenu(Player player){

        Gui gui = IGUI.createChestGui("Town",3);

        int townPrice = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("townCost", 1000);

        ItemStack createTown = HeadUtils.createCustomItemStack(Material.GRASS_BLOCK,
                Lang.GUI_NO_TOWN_CREATE_NEW_TOWN.get(),
                Lang.GUI_NO_TOWN_CREATE_NEW_TOWN_DESC1.get(townPrice));
        ItemStack browse = HeadUtils.createCustomItemStack(Material.ANVIL,
                Lang.GUI_NO_TOWN_JOIN_A_TOWN.get(),
                Lang.GUI_NO_TOWN_JOIN_A_TOWN_DESC1.get(TownDataStorage.getNumberOfTown()));

        GuiItem createButton = ItemBuilder.from(createTown).asGuiItem(event -> {
            event.setCancelled(true);
            double playerMoney = EconomyUtil.getBalance(player);
            if (playerMoney < townPrice) {
                player.sendMessage(getTANString() + Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(townPrice - playerMoney));
            }
            else {
                player.sendMessage(getTANString() + Lang.PLAYER_WRITE_TOWN_NAME_IN_CHAT.get());
                PlayerChatListenerStorage.register(player, new CreateTown(townPrice));
            }
        });

        GuiItem browseButton = ItemBuilder.from(browse).asGuiItem(event -> {
            event.setCancelled(true);
            openSearchTownMenu(player,0);
        });

        gui.setItem(11, createButton);
        gui.setItem(15, browseButton);
        gui.setItem(18, IGUI.createBackArrow(player, p -> openMainMenu(player)));

        gui.open(player);
    }
    public static void openSearchTownMenu(Player player, int page) {

        Gui gui = IGUI.createChestGui("Town list | page " + (page + 1),6);


        ArrayList<GuiItem> townItemStacks = new ArrayList<>();

        for(TownData specificTownData : TownDataStorage.getTownMap().values()){
            ItemStack townIcon = specificTownData.getIconWithInformations();
            HeadUtils.addLore(townIcon,
                    "",
                    (specificTownData.isRecruiting()) ? Lang.GUI_TOWN_INFO_IS_RECRUITING.get() : Lang.GUI_TOWN_INFO_IS_NOT_RECRUITING.get(),
                    (specificTownData.isPlayerAlreadyRequested(player)) ? Lang.GUI_TOWN_INFO_RIGHT_CLICK_TO_CANCEL.get() : Lang.GUI_TOWN_INFO_LEFT_CLICK_TO_JOIN.get()
            );
            GuiItem townButton = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);

                if(event.isLeftClick()){
                    if(specificTownData.isPlayerAlreadyRequested(player)){
                        return;
                    }
                    if(!specificTownData.isRecruiting()){
                        player.sendMessage(getTANString() + Lang.PLAYER_TOWN_NOT_RECRUITING.get());
                        return;
                    }
                    specificTownData.addPlayerJoinRequest(player);
                    player.sendMessage(getTANString() + Lang.PLAYER_ASK_TO_JOIN_TOWN_PLAYER_SIDE.get(specificTownData.getName()));
                    NewsletterStorage.registerNewsletter(new PlayerJoinRequestNL(player, specificTownData));
                    openSearchTownMenu(player,page);
                }
                if(event.isRightClick()){
                    if(!specificTownData.isPlayerAlreadyRequested(player)){
                        return;
                    }
                    specificTownData.removePlayerJoinRequest(player);
                    NewsletterStorage.removePlayerJoinRequest(player, specificTownData);
                    player.sendMessage(getTANString() + Lang.PLAYER_REMOVE_ASK_TO_JOIN_TOWN_PLAYER_SIDE.get());
                    openSearchTownMenu(player,page);
                }

            });
            townItemStacks.add(townButton);
        }

        createIterator(gui, townItemStacks, page, player, p -> openNoTownMenu(player),
                p -> openSearchTownMenu(player, page + 1),
                p -> openSearchTownMenu(player, page - 1));


        gui.open(player);
    }
    public static void openTownMenu(Player player) {
        int nRows = 4;
        Gui gui = IGUI.createChestGui("Town",nRows);

        PlayerData playerStat = PlayerDataStorage.get(player);
        TownData playerTown = TownDataStorage.get(playerStat);

        ItemStack townIcon = playerTown.getIconWithInformations();
        HeadUtils.addLore(townIcon,
                Lang.GUI_TOWN_INFO_CHANGE_ICON.get()
        );

        ItemStack treasury = HeadUtils.makeSkullB64(Lang.GUI_TOWN_TREASURY_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=",
                Lang.GUI_TOWN_TREASURY_ICON_DESC1.get());

        ItemStack memberIcon = HeadUtils.makeSkullB64(Lang.GUI_TOWN_MEMBERS_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Q0ZDQ5NmIxZGEwNzUzNmM5NGMxMzEyNGE1ODMzZWJlMGM1MzgyYzhhMzM2YWFkODQ2YzY4MWEyOGQ5MzU2MyJ9fX0=",
                Lang.GUI_TOWN_MEMBERS_ICON_DESC1.get());

        ItemStack claims = HeadUtils.makeSkullB64(Lang.GUI_CLAIM_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=",
                Lang.GUI_CLAIM_ICON_DESC1.get());

        ItemStack otherTownIcon = HeadUtils.makeSkullB64(Lang.GUI_OTHER_TOWN_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMzc0ZTIxYjgxYzBiMjFhYmViOGU5N2UxM2UwNzdkM2VkMWVkNDRmMmU5NTZjNjhmNjNhM2UxOWU4OTlmNiJ9fX0=",
                Lang.GUI_OTHER_TOWN_ICON_DESC1.get());

        ItemStack diplomacy = HeadUtils.makeSkullB64(Lang.GUI_RELATION_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUwN2Q2ZGU2MzE4MzhlN2E3NTcyMGU1YjM4ZWYxNGQyOTY2ZmRkODQ4NmU3NWQxZjY4MTJlZDk5YmJjYTQ5OSJ9fX0=",
                Lang.GUI_RELATION_ICON_DESC1.get());

        ItemStack level = HeadUtils.makeSkullB64(Lang.GUI_TOWN_LEVEL_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmJlNTI5YWI2YjJlYTdjNTBkOTE5MmQ4OWY4OThmZDdkYThhOWU3NTBkMzc4Mjk1ZGY3MzIwNWU3YTdlZWFlMCJ9fX0=",
                Lang.GUI_TOWN_LEVEL_ICON_DESC1.get());

        ItemStack settings = HeadUtils.makeSkullB64(Lang.GUI_TOWN_SETTINGS_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVkMmNiMzg0NThkYTE3ZmI2Y2RhY2Y3ODcxNjE2MDJhMjQ5M2NiZjkzMjMzNjM2MjUzY2ZmMDdjZDg4YTljMCJ9fX0=",
                Lang.GUI_TOWN_SETTINGS_ICON_DESC1.get());

        ItemStack propertyIcon = HeadUtils.createCustomItemStack(Material.OAK_HANGING_SIGN, Lang.GUI_TOWN_PROPERTIES_ICON.get(),Lang.GUI_TOWN_PROPERTIES_ICON_DESC1.get());

        ItemStack landmark = HeadUtils.makeSkullB64(Lang.ADMIN_GUI_LANDMARK_ICON.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQ3NjFjYzE2NTYyYzg4ZDJmYmU0MGFkMzg1MDJiYzNiNGE4Nzg1OTg4N2RiYzM1ZjI3MmUzMGQ4MDcwZWVlYyJ9fX0=",
                Lang.ADMIN_GUI_LANDMARK_DESC1.get());

        ItemStack war = HeadUtils.makeSkullB64(Lang.GUI_ATTACK_ICON.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjVkZTRmZjhiZTcwZWVlNGQxMDNiMWVlZGY0NTRmMGFiYjlmMDU2OGY1ZjMyNmVjYmE3Y2FiNmE0N2Y5YWRlNCJ9fX0=",
                Lang.GUI_ATTACK_ICON_DESC1.get());

        ItemStack hierarchy = HeadUtils.makeSkullB64(Lang.GUI_HIERARCHY_MENU.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=",
                Lang.GUI_HIERARCHY_MENU_DESC1.get());

        GuiItem townIconButton = ItemBuilder.from(townIcon).asGuiItem(event -> {
            event.setCancelled(true);

            if(!playerTown.doesPlayerHavePermission(playerStat, TOWN_ADMINISTRATOR))
                return;
            if(event.getCursor() == null)
                return;

            ItemStack itemMaterial = event.getCursor();
            if(itemMaterial.getType() == Material.AIR ){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_NO_ITEM_SHOWED.get());
            }
            else {
                playerTown.setIcon(itemMaterial);
                openTownMenu(player);
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_CHANGED_ICON_SUCCESS.get());
            }
        });
        GuiItem treasuryButton = ItemBuilder.from(treasury).asGuiItem(event -> {
            event.setCancelled(true);
            openTreasury(player, playerTown);
        });
        GuiItem membersButton = ItemBuilder.from(memberIcon).asGuiItem(event -> {
            event.setCancelled(true);
            openMemberList(player, playerTown);
        });
        GuiItem claimButton = ItemBuilder.from(claims).asGuiItem(event -> {
            event.setCancelled(true);
            openTownChunk(player);
        });
        GuiItem browseMenu = ItemBuilder.from(otherTownIcon).asGuiItem(event -> {
            event.setCancelled(true);
            browseTerritory(player, playerTown, BrowseScope.TOWNS, p -> openTownMenu(player), 0);
        });
        GuiItem relationButton = ItemBuilder.from(diplomacy).asGuiItem(event -> {
            event.setCancelled(true);
            openRelations(player, playerTown, p -> dispatchPlayerTown(player));
        });
        GuiItem levelButton = ItemBuilder.from(level).asGuiItem(event -> {
            event.setCancelled(true);
            openTownLevel(player,0);
        });
        GuiItem settingsButton = ItemBuilder.from(settings).asGuiItem(event -> {
            event.setCancelled(true);
            openTownSettings(player, playerTown);
        });
        GuiItem propertyButton = ItemBuilder.from(propertyIcon).asGuiItem(event -> {
            event.setCancelled(true);
            openTownPropertiesMenu(player,0);
        });
        GuiItem landmarksButton = ItemBuilder.from(landmark).asGuiItem(event -> {
            event.setCancelled(true);
            openOwnedLandmark(player, playerTown,0);
        });
        GuiItem warButton = ItemBuilder.from(war).asGuiItem(event -> {
            event.setCancelled(true);
            openWarMenu(player, playerTown, p -> dispatchPlayerTown(player), 0);
        });
        GuiItem hierarchyButton = ItemBuilder.from(hierarchy).asGuiItem(event -> {
            event.setCancelled(true);
            openHierarchyMenu(player, playerTown);
        });


        gui.setItem(4, townIconButton);
        gui.setItem(2,2, treasuryButton);
        gui.setItem(2,3, membersButton);
        gui.setItem(2,4, claimButton);
        gui.setItem(2,5, browseMenu);
        gui.setItem(2,6, relationButton);
        gui.setItem(2,7, levelButton);
        gui.setItem(2,8, settingsButton);
        gui.setItem(3,2,propertyButton);
        gui.setItem(3,3,landmarksButton);
        gui.setItem(3,4,warButton);
        gui.setItem(3,5,hierarchyButton);

        gui.setItem(nRows,1, IGUI.createBackArrow(player, p -> openMainMenu(player)));

        gui.open(player);
    }

    private static void openWarMenu(Player player, TerritoryData territory, Consumer<Player> exit, int page) {
        Gui gui = IGUI.createChestGui("Wars | page " + (page + 1),6);
        ArrayList<GuiItem> guiItems = new ArrayList<>();

        for(PlannedAttack plannedAttack : PlannedAttackStorage.getWars()){
            ItemStack attackIcon = plannedAttack.getIcon(territory);
            GuiItem attackButton = ItemBuilder.from(attackIcon).asGuiItem(event -> {
                event.setCancelled(true);
                if(event.isLeftClick()){
                    openSpecificPlannedAttackMenu(player, territory, plannedAttack, exit, page);
                }
            });
            guiItems.add(attackButton);
        }

        createIterator(gui, guiItems, page, player, exit,
                p -> openWarMenu(player, territory, exit,page + 1),
                p -> openWarMenu(player, territory, exit,page - 1));
        gui.open(player);
    }

    private static void openSpecificPlannedAttackMenu(Player player, TerritoryData territory, PlannedAttack plannedAttack, Consumer<Player> exit, int page) {
        Gui gui = IGUI.createChestGui("War manager", 3);

        GuiItem attackIcon = ItemBuilder.from(plannedAttack.getIcon(territory)).asGuiItem(event -> event.setCancelled(true));
        gui.setItem(1,5, attackIcon);

        ItemStack attackingSideInfo = plannedAttack.getAttackingIcon();
        GuiItem attackingSidePanel = ItemBuilder.from(attackingSideInfo).asGuiItem(event -> event.setCancelled(true));
        gui.setItem(2,2, attackingSidePanel);

        ItemStack defendingSideInfo = plannedAttack.getDefendingIcon();
        GuiItem defendingSidePanel = ItemBuilder.from(defendingSideInfo).asGuiItem(event -> event.setCancelled(true));
        gui.setItem(2,4, defendingSidePanel);



        WarRole territoryRole = plannedAttack.getTerritoryRole(territory);

        if(territoryRole == WarRole.MAIN_ATTACKER){
            ItemStack cancelAttack = HeadUtils.createCustomItemStack(Material.BARRIER, Lang.GUI_CANCEL_ATTACK.get(), Lang.GUI_GENERIC_CLICK_TO_DELETE.get());
            ItemStack renameAttack = HeadUtils.createCustomItemStack(Material.NAME_TAG, Lang.GUI_RENAME_ATTACK.get(), Lang.GUI_GENERIC_CLICK_TO_RENAME.get());
            GuiItem cancelButton = ItemBuilder.from(cancelAttack).asGuiItem(event -> {
                plannedAttack.remove();
                territory.broadCastMessageWithSound(Lang.ATTACK_SUCCESSFULLY_CANCELLED.get(plannedAttack.getMainDefender().getName()),MINOR_GOOD);
                openWarMenu(player, territory, exit, page);
            });

            GuiItem renameButton = ItemBuilder.from(renameAttack).asGuiItem(event -> {
                event.setCancelled(true);
                player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
                PlayerChatListenerStorage.register(player, new ChangeAttackName(plannedAttack, p -> openSpecificPlannedAttackMenu(player, territory, plannedAttack, exit, page)));
            });

            gui.setItem(2,6, renameButton);
            gui.setItem(2,8, cancelButton);

        }

        else if(territoryRole == WarRole.MAIN_DEFENDER){
            ItemStack submitToRequests = HeadUtils.createCustomItemStack(Material.SOUL_LANTERN,
                    Lang.SUBMIT_TO_REQUESTS.get(),
                    Lang.SUBMIT_TO_REQUEST_DESC1.get(),
                    Lang.SUBMIT_TO_REQUEST_DESC2.get(plannedAttack.getWarGoal().getCurrentDesc()));

            GuiItem submitToRequestButton = ItemBuilder.from(submitToRequests).asGuiItem(event -> {
                plannedAttack.defenderSurrendered();
                openWarMenu(player, territory, exit, page);
            });
            gui.setItem(2,7,submitToRequestButton);

        }

        else if(territoryRole == WarRole.OTHER_ATTACKER || territoryRole == WarRole.OTHER_DEFENDER){
            ItemStack quitWar = HeadUtils.createCustomItemStack(Material.DARK_OAK_DOOR,Lang.GUI_QUIT_WAR.get(), Lang.GUI_QUIT_WAR_DESC1.get());

            GuiItem quitButton = ItemBuilder.from(quitWar).asGuiItem(event -> {
                plannedAttack.removeBelligerent(territory);
                territory.broadCastMessageWithSound(Lang.TERRITORY_NO_LONGER_INVOLVED_IN_WAR_MESSAGE.get(plannedAttack.getMainDefender().getName()),MINOR_GOOD);
                openWarMenu(player, territory, exit, page);
            });
            gui.setItem(2,7, quitButton);
        }

        else if(territoryRole == WarRole.NEUTRAL){
            ItemStack joinAttacker = HeadUtils.createCustomItemStack(Material.IRON_SWORD,
                    Lang.GUI_JOIN_ATTACKING_SIDE.get(),
                    Lang.GUI_JOIN_ATTACKING_SIDE_DESC1.get(territory.getColoredName()),
                    Lang.GUI_WAR_GOAL_INFO.get(plannedAttack.getWarGoal().getDisplayName()));
            ItemStack joinDefender = HeadUtils.createCustomItemStack(Material.SHIELD,
                    Lang.GUI_JOIN_DEFENDING_SIDE.get(),
                    Lang.GUI_JOIN_DEFENDING_SIDE_DESC1.get(territory.getColoredName()));

            GuiItem joinAttackerButton = ItemBuilder.from(joinAttacker).asGuiItem(event -> {
                plannedAttack.addAttacker(territory);
                openSpecificPlannedAttackMenu(player, territory, plannedAttack, exit, page);
            });

            GuiItem joinDefenderButton = ItemBuilder.from(joinDefender).asGuiItem(event -> {
                plannedAttack.addDefender(territory);
                openSpecificPlannedAttackMenu(player, territory, plannedAttack, exit, page);
            });
            gui.setItem(2,6, joinAttackerButton);
            gui.setItem(2,8, joinDefenderButton);
        }

        gui.setItem(3,1, IGUI.createBackArrow(player, p -> openWarMenu(player, territory, exit, page)));
        gui.open(player);

    }

    public static void openStartWarSettings(Player player, Consumer<Player> exit, CreateAttackData createAttackData) {
        Gui gui = IGUI.createChestGui("War on " + createAttackData.getMainDefender().getName(),3);


        TerritoryData mainAttacker = createAttackData.getMainAttacker();
        TerritoryData mainDefender = createAttackData.getMainDefender();

        ItemStack addTime = HeadUtils.makeSkullB64(Lang.GUI_ATTACK_ADD_TIME.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjMyZmZmMTYzZTIzNTYzMmY0MDQ3ZjQ4NDE1OTJkNDZmODVjYmJmZGU4OWZjM2RmNjg3NzFiZmY2OWE2NjIifX19",
                Lang.GUI_LEFT_CLICK_FOR_1_MINUTE.get(),
                Lang.GUI_SHIFT_CLICK_FOR_1_HOUR.get());
        ItemStack removeTIme = HeadUtils.makeSkullB64(Lang.GUI_ATTACK_REMOVE_TIME.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE1NmRhYjUzZDRlYTFhNzlhOGU1ZWQ2MzIyYzJkNTZjYjcxNGRkMzVlZGY0Nzg3NjNhZDFhODRhODMxMCJ9fX0=",
                Lang.GUI_LEFT_CLICK_FOR_1_MINUTE.get(),
                Lang.GUI_SHIFT_CLICK_FOR_1_HOUR.get());
        ItemStack time = HeadUtils.makeSkullB64(Lang.GUI_ATTACK_SET_TO_START_IN.get(DateUtil.getStringDeltaDateTime(createAttackData.getDeltaDateTime())),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWU5OThmM2ExNjFhNmM5ODlhNWQwYTFkYzk2OTMxYTM5OTI0OWMwODBiNjYzNjQ1ODFhYjk0NzBkZWE1ZTcyMCJ9fX0=",
                Lang.GUI_LEFT_CLICK_FOR_1_MINUTE.get(),
                Lang.GUI_SHIFT_CLICK_FOR_1_HOUR.get());
        ItemStack confirm = HeadUtils.makeSkullB64(Lang.GUI_CONFIRM_ATTACK.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNhNDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIwZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=",
                Lang.GUI_CONFIRM_ATTACK_DESC1.get(mainDefender.getColoredName()));

        if(!createAttackData.getWargoal().isCompleted()){
            HeadUtils.addLore(confirm, Lang.GUI_WARGOAL_NOT_COMPLETED.get());
        }

        ItemStack wargoal = createAttackData.getWargoal().getIcon();


        GuiItem addTimeButton = ItemBuilder.from(addTime).asGuiItem(event -> {
            event.setCancelled(true);
            SoundUtil.playSound(player, ADD);
            if(event.isShiftClick()){
                createAttackData.addDeltaDateTime(60 * 1200L);
            }
            else if(event.isLeftClick()){
                createAttackData.addDeltaDateTime(1200);
            }
            openStartWarSettings(player, exit, createAttackData);
        });

        GuiItem removeTimeButton = ItemBuilder.from(removeTIme).asGuiItem(event -> {
            event.setCancelled(true);
            SoundUtil.playSound(player, REMOVE);

            if(event.isShiftClick()){
                createAttackData.addDeltaDateTime(-60 * 1200L);
            }
            else if(event.isLeftClick()){
                createAttackData.addDeltaDateTime(-1200);
            }

            if(createAttackData.getDeltaDateTime() < 0)
                createAttackData.setDeltaDateTime(0);
            openStartWarSettings(player, exit, createAttackData);
        });

        GuiItem timeInfo = ItemBuilder.from(time).asGuiItem(event -> event.setCancelled(true));

        GuiItem wargoalButton = ItemBuilder.from(wargoal).asGuiItem(event -> {
            openSelectWarGoalMenu(player, exit,  createAttackData);
            event.setCancelled(true);
        });

        GuiItem confirmButton = ItemBuilder.from(confirm).asGuiItem(event -> {
            event.setCancelled(true);

            if(!createAttackData.getWargoal().isCompleted()){
                player.sendMessage(getTANString() + Lang.GUI_WARGOAL_NOT_COMPLETED.get());
                return;
            }

            PlannedAttackStorage.newWar(createAttackData);
            openWarMenu(player, mainAttacker, exit, 0);

            player.sendMessage(getTANString() + Lang.GUI_TOWN_ATTACK_TOWN_EXECUTED.get(mainDefender.getName()));
            mainAttacker.broadCastMessageWithSound(Lang.GUI_TOWN_ATTACK_TOWN_INFO.get(mainAttacker.getName(), mainDefender.getName()), WAR);
            mainDefender.broadCastMessageWithSound(Lang.GUI_TOWN_ATTACK_TOWN_INFO.get(mainAttacker.getName(), mainDefender.getName()), WAR);
        });


        gui.setItem(2,2,removeTimeButton);
        gui.setItem(2,3,timeInfo);
        gui.setItem(2,4,addTimeButton);

        gui.setItem(2,6,wargoalButton);

        gui.setItem(2,8,confirmButton);
        gui.setItem(3,1,IGUI.createBackArrow(player, e -> openSingleRelation(player, mainAttacker, TownRelation.WAR,0, exit)));

        createAttackData.getWargoal().addExtraOptions(gui, player, createAttackData,exit);

        gui.open(player);

    }

    public static void openSelecteTerritoryToLiberate(Player player, CreateAttackData createAttackData, LiberateWarGoal liberateWarGoal, Consumer<Player> exit) {

        Gui gui = IGUI.createChestGui("War on " + createAttackData.getMainDefender().getName(),6);

        TerritoryData territoryToAttack = createAttackData.getMainDefender();
        for(TerritoryData territoryData : territoryToAttack.getVassals()){
            if(territoryData.isCapital()){
                continue;
            }
            ItemStack territoryIcon = territoryData.getIconWithInformations();
            HeadUtils.addLore(territoryIcon, "", Lang.LEFT_CLICK_TO_SELECT.get());

            GuiItem territoryButton = ItemBuilder.from(territoryIcon).asGuiItem(event -> {
                event.setCancelled(true);
                liberateWarGoal.setTerritoryToLiberate(territoryData);
                openStartWarSettings(player, exit, createAttackData);
            });

            gui.addItem(territoryButton);
        }

        gui.setItem(6,1,IGUI.createBackArrow(player, e -> openStartWarSettings(player, exit, createAttackData)));
        gui.open(player);

    }

    private static void openSelectWarGoalMenu(Player player, Consumer<Player> exit, CreateAttackData createAttackData) {
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


        GuiItem conquerButton = ItemBuilder.from(conquer).asGuiItem(event -> {
            event.setCancelled(true);
            createAttackData.setWarGoal(new ConquerWarGoal(createAttackData.getMainAttacker().getID(), createAttackData.getMainDefender().getID()));
            openStartWarSettings(player, exit, createAttackData);
        });

        GuiItem subjugateButton = ItemBuilder.from(subjugate).asGuiItem(event -> {
            event.setCancelled(true);
            if(!canBeSubjugated){
                player.sendMessage(getTANString() + Lang.GUI_WARGOAL_SUBJUGATE_CANNOT_BE_USED.get());
                return;
            }
            createAttackData.setWarGoal(new SubjugateWarGoal(createAttackData));
            openStartWarSettings(player, exit, createAttackData);
        });

        GuiItem liberateButton = ItemBuilder.from(liberate).asGuiItem(event -> {
            event.setCancelled(true);

            if(!canBeLiberated){
                player.sendMessage(getTANString() + Lang.GUI_WARGOAL_LIBERATE_CANNOT_BE_USED.get());
                return;
            }
            createAttackData.setWarGoal(new LiberateWarGoal());
            openStartWarSettings(player, exit, createAttackData);
        });

        gui.setItem(2,3,conquerButton);
        gui.setItem(2,5,subjugateButton);
        gui.setItem(2,7,liberateButton);

        gui.setItem(3,1,IGUI.createBackArrow(player, e -> openStartWarSettings(player, exit, createAttackData)));

        gui.open(player);
    }

    private static void openOwnedLandmark(Player player, TownData townData, int page) {
        Gui gui = IGUI.createChestGui("Town owned landmark | page " + (page + 1),6);

        ArrayList<GuiItem> landmarkGui = new ArrayList<>();

        for(String landmarkID : townData.getOwnedLandmarks()){
            Landmark landmarkData = LandmarkStorage.get(landmarkID);

            GuiItem landmarkButton = ItemBuilder.from(landmarkData.getIcon()).asGuiItem(event -> event.setCancelled(true));
            landmarkGui.add(landmarkButton);
        }
        GuiUtil.createIterator(gui, landmarkGui, page, player,
                p -> openTownMenu(player),
                p -> openOwnedLandmark(player, townData, page + 1),
                p -> openOwnedLandmark(player, townData, page - 1)
        );

        gui.open(player);

    }

    public static void browseTerritory(Player player, TerritoryData territoryData, BrowseScope scope, Consumer<Player> exitMenu, int page) {
        Gui gui = IGUI.createChestGui("Territory list | page " + (page + 1),6);


        List<TerritoryData> territoryList = new ArrayList<>();

        if(scope == BrowseScope.ALL || scope == BrowseScope.TOWNS)
            territoryList.addAll(TownDataStorage.getTownMap().values());
        if(scope == BrowseScope.ALL || scope == BrowseScope.REGIONS)
            territoryList.addAll(RegionDataStorage.getAllRegions());

        ArrayList<GuiItem> townGuiItems = new ArrayList<>();

        for(TerritoryData specificTerritoryData : territoryList){
            ItemStack territoryIcon = specificTerritoryData.getIconWithInformationAndRelation(territoryData);
            GuiItem territoryGUI = ItemBuilder.from(territoryIcon).asGuiItem(event -> event.setCancelled(true));

            townGuiItems.add(territoryGUI);
        }

        createIterator(gui, townGuiItems, page, player, exitMenu,
                p -> browseTerritory(player, territoryData, scope ,exitMenu, page + 1),
                p -> browseTerritory(player, territoryData, scope ,exitMenu, page - 1));


        ItemStack checkScope = HeadUtils.createCustomItemStack(Material.NAME_TAG,scope.getName(),
                Lang.GUI_GENERIC_CLICK_TO_SWITCH_SCOPE.get());
        GuiItem checkScopeButton = new GuiItem(checkScope, event -> browseTerritory(player, territoryData, scope.getNextScope(), exitMenu, 0));

        gui.setItem(6,5,checkScopeButton);
        gui.open(player);
    }


    public static void openMemberList(Player player, TerritoryData territoryData) {
        openMemberList(player, territoryData, 0);
    }

    public static void openMemberList(Player player, TerritoryData territoryData, int page) {

        PlayerData playerData = PlayerDataStorage.get(player);

        Gui gui = IGUI.createChestGui("Members",6);

        List<GuiItem> guiItems = territoryData.getOrderedMemberList(playerData);

        ItemStack manageRanks = HeadUtils.createCustomItemStack(Material.LADDER, Lang.GUI_TOWN_MEMBERS_MANAGE_ROLES.get());

        GuiItem manageRanksButton = ItemBuilder.from(manageRanks).asGuiItem(event -> {
            event.setCancelled(true);
            openTerritoryRanks(player, territoryData);
        });

        createIterator(gui, guiItems, page, player,
                p -> territoryData.openMainMenu(player),
                p -> openMemberList(player, territoryData, page + 1),
                p -> openMemberList(player, territoryData, page - 1),
                Material.LIME_STAINED_GLASS_PANE);

        gui.setItem(6,3, manageRanksButton);


        if(territoryData instanceof TownData townData){
            ItemStack manageApplication = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK,
                    Lang.GUI_TOWN_MEMBERS_MANAGE_APPLICATION.get(),
                    Lang.GUI_TOWN_MEMBERS_MANAGE_APPLICATION_DESC1.get(townData.getPlayerJoinRequestSet().size())
            );
            GuiItem mangeApplicationPanel = ItemBuilder.from(manageApplication).asGuiItem(event -> {
                event.setCancelled(true);
                openTownApplications(player, townData);
            });
            gui.setItem(6,4, mangeApplicationPanel);
        }

        gui.open(player);

    }
    public static void openTownApplications(Player player, TownData townData) {
        openTownApplications(player, townData, 0);
    }
    public static void openTownApplications(Player player, TownData townData, int page) {
        PlayerData playerStat = PlayerDataStorage.get(player);

        Gui gui = IGUI.createChestGui("Application",6);

        List<GuiItem> guiItems = new ArrayList<>();
        HashSet<String> players = townData.getPlayerJoinRequestSet();
        for (String playerUUID: players) {

            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            PlayerData playerIterateData = PlayerDataStorage.get(playerUUID);

            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate,
                    Lang.GUI_PLAYER_ASK_JOIN_PROFILE_DESC2.get(),
                    Lang.GUI_PLAYER_ASK_JOIN_PROFILE_DESC3.get());

            GuiItem playerButton = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                if(event.isLeftClick()){
                    if(!townData.doesPlayerHavePermission(playerStat, RolePermission.INVITE_PLAYER)){
                        player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    if(townData.isFull()){
                        player.sendMessage(getTANString() + Lang.INVITATION_TOWN_FULL.get());
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    townData.addPlayer(playerIterateData);
                }
                else if(event.isRightClick()){
                    if(!townData.doesPlayerHavePermission(playerStat, RolePermission.INVITE_PLAYER)){
                        player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                        return;
                    }
                    townData.removePlayerJoinRequest(playerIterateData.getID());
                }
                NewsletterStorage.removePlayerJoinRequest(playerIterateData, townData);
                openTownApplications(player, townData, page);
            });
            guiItems.add(playerButton);
        }

        createIterator(gui, guiItems, 0, player,
                p -> openMemberList(player, townData),
                p -> openTownApplications(player, townData, page + 1),
                p -> openTownApplications(player, townData, page - 1),
                Material.LIME_STAINED_GLASS_PANE);

        gui.open(player);

    }
    public static void openTerritoryRanks(Player player, TerritoryData territoryData) {

        int row = 3;
        Gui gui = IGUI.createChestGui("Ranks",row);

        PlayerData playerData = PlayerDataStorage.get(player);

        for (RankData rank: territoryData.getAllRanksSorted()) {
            ItemStack townRankItemStack = HeadUtils.createCustomItemStack(rank.getRankIcon(), rank.getColoredName());
            GuiItem singleRankButton = ItemBuilder.from(townRankItemStack).asGuiItem(event -> {
                event.setCancelled(true);
                if(!territoryData.doesPlayerHavePermission(playerData, RolePermission.MANAGE_RANKS)) {
                    player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                    return;
                }
                if(territoryData.getRank(playerData).getLevel() <= rank.getLevel() && !territoryData.isLeader(playerData)){
                    player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get());
                    return;
                }
                openRankManager(player, territoryData,rank);
            });

            gui.addItem(singleRankButton);
        }

        ItemStack createNewRole = HeadUtils.makeSkullB64(Lang.GUI_TOWN_MEMBERS_ADD_NEW_ROLES.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTBjOTdlNGI2OGFhYWFlODQ3MmUzNDFiMWQ4NzJiOTNiMzZkNGViNmVhODllY2VjMjZhNjZlNmM0ZTE3OCJ9fX0=");
        GuiItem createNewButton = ItemBuilder.from(createNewRole).asGuiItem(event -> {
            event.setCancelled(true);

            if(!territoryData.doesPlayerHavePermission(playerData, RolePermission.CREATE_RANK)) {
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }
            if(territoryData.getNumberOfRank() >= ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("townMaxRank",8)){
                player.sendMessage(getTANString() + Lang.TOWN_RANK_CAP_REACHED.get());
                return;
            }
            player.sendMessage(getTANString() + Lang.WRITE_IN_CHAT_NEW_ROLE_NAME.get());
            PlayerChatListenerStorage.register(player, new CreateRank(territoryData));
        });

        GuiItem decorativePanel = ItemBuilder.from(HeadUtils.createCustomItemStack(Material.GRAY_STAINED_GLASS_PANE, "")).asGuiItem(event -> event.setCancelled(true));

        gui.setItem(row,1, IGUI.createBackArrow(player, p -> openMemberList(player, territoryData)));
        gui.setItem(row,2, decorativePanel);
        gui.setItem(row,3, createNewButton);
        gui.setItem(row,4, decorativePanel);
        gui.setItem(row,5, decorativePanel);
        gui.setItem(row,6, decorativePanel);
        gui.setItem(row,7, decorativePanel);
        gui.setItem(row,8, decorativePanel);
        gui.setItem(row,9, decorativePanel);

        gui.open(player);

    }
    public static void openRankManager(Player player, TerritoryData territoryData, RankData rankData) {

        PlayerData playerData = PlayerDataStorage.get(player);
        Gui gui = IGUI.createChestGui("Rank " + rankData.getName(),4);


        boolean isDefaultRank = Objects.equals(rankData.getID(), territoryData.getDefaultRankID());

        ItemStack roleIcon = HeadUtils.createCustomItemStack(
                rankData.getRankIcon(),
                Lang.GUI_BASIC_NAME.get(rankData.getColoredName()),
                Lang.GUI_TOWN_MEMBERS_ROLE_NAME_DESC1.get());

        ItemStack roleRankIcon = rankData.getRankEnum().getRankGuiIcon();

        ArrayList<String> playerNames = new ArrayList<>();
        playerNames.add(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO_DESC1.get());
        for (String playerUUID : rankData.getPlayersID()) {
            String playerName = PlayerDataStorage.get(playerUUID).getName();
            playerNames.add(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO_DESC.get(playerName));
        }
        ItemStack membersRank = HeadUtils.makeSkullB64(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I0M2IyMzE4OWRjZjEzMjZkYTQyNTNkMWQ3NTgyZWY1YWQyOWY2YzI3YjE3MWZlYjE3ZTMxZDA4NGUzYTdkIn19fQ==",
                playerNames);

        ItemStack managePermission = HeadUtils.createCustomItemStack(Material.ANVIL,Lang.GUI_TOWN_MEMBERS_ROLE_MANAGE_PERMISSION.get());
        ItemStack renameRank = HeadUtils.createCustomItemStack(Material.NAME_TAG,Lang.GUI_TOWN_MEMBERS_ROLE_CHANGE_NAME.get());
        ItemStack changeRoleTaxRelation = HeadUtils.createCustomItemStack(
                Material.GOLD_NUGGET,
                rankData.isPayingTaxes() ? Lang.GUI_TOWN_MEMBERS_ROLE_PAY_TAXES.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NOT_PAY_TAXES.get(),
                Lang.GUI_TOWN_MEMBERS_ROLE_TAXES_DESC1.get()
        );

        ItemStack makeRankDefault = HeadUtils.createCustomItemStack(Material.RED_BED,
                isDefaultRank ? Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_IS_DEFAULT.get() : Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_IS_NOT_DEFAULT.get(),
                Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT1.get(),
                isDefaultRank ? "" : Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT2.get());

        ItemStack delete = HeadUtils.createCustomItemStack(Material.BARRIER, Lang.GUI_TOWN_MEMBERS_ROLE_DELETE.get());

        ItemStack salary = HeadUtils.createCustomItemStack(Material.GOLD_INGOT,
                Lang.GUI_TOWN_MEMBERS_ROLE_SALARY.get(),
                Lang.GUI_TOWN_MEMBERS_ROLE_SALARY_DESC1.get(rankData.getSalary()));

        ItemStack lowerSalary = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_LOWER_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=",
                Lang.GUI_DECREASE_1_DESC.get(),
                Lang.GUI_DECREASE_10_DESC.get());
        ItemStack increaseSalary = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_INCREASE_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                Lang.GUI_INCREASE_1_DESC.get(),
                Lang.GUI_INCREASE_10_DESC.get());

        GuiItem roleGui = ItemBuilder.from(roleIcon).asGuiItem(event -> {
            event.setCancelled(true);

            if(event.getCursor() == null || event.getCursor().getData() == null)
                return;
            ItemStack itemMaterial = event.getCursor();
            if(itemMaterial.getType() == Material.AIR){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_NO_ITEM_SHOWED.get());
                return;
            }
            rankData.setRankIconName(itemMaterial);
            openRankManager(player, territoryData, rankData);
            player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_CHANGED_ICON_SUCCESS.get());

        });

        GuiItem rankIconButton = ItemBuilder.from(roleRankIcon).asGuiItem(event -> {
            event.setCancelled(true);
            if(event.isLeftClick()){
                RankData playerRank = territoryData.getRank(player);
                if(playerRank.getRankEnum().getLevel() > (rankData.getRankEnum().getLevel() + 1) || territoryData.isLeader(playerData)){
                    rankData.incrementLevel();
                }
                else{
                    player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_RANK_UP_INFERIOR_RANK.get(playerRank.getColoredName()));
                }
            }
            else if(event.isRightClick()){
                rankData.decrementLevel();
            }
            openRankManager(player, territoryData, rankData);
        });
        GuiItem managePermissionGui = ItemBuilder.from(managePermission).asGuiItem(event -> {
            event.setCancelled(true);
            openRankManagerPermissions(player,territoryData, rankData);
        });
        GuiItem membersRankGui = ItemBuilder.from(membersRank).asGuiItem(event -> {
            openTownRankManagerAddPlayer(player,territoryData, rankData);
            event.setCancelled(true);
        });
        GuiItem renameGui = ItemBuilder.from(renameRank).asGuiItem(event -> {
            player.sendMessage(getTANString() + Lang.WRITE_IN_CHAT_NEW_ROLE_NAME.get());
            PlayerChatListenerStorage.register(player, new RenameRank(territoryData , rankData));

            player.closeInventory();
            event.setCancelled(true);
        });
        GuiItem taxButton = ItemBuilder.from(changeRoleTaxRelation).asGuiItem(event -> {
            rankData.swapPayingTaxes();
            openRankManager(player, territoryData, rankData);
            event.setCancelled(true);
        });
        GuiItem defaultRankButton = ItemBuilder.from(makeRankDefault).asGuiItem(event -> {
            event.setCancelled(true);

            if(isDefaultRank){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_ALREADY_DEFAULT.get());
            }
            else{
                territoryData.setDefaultRank(rankData.getID());
                openRankManager(player, territoryData, rankData);
            }
        });

        GuiItem deleteButton = ItemBuilder.from(delete).asGuiItem(event -> {
            event.setCancelled(true);

            if(rankData.getNumberOfPlayer() != 0){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_DELETE_ERROR_NOT_EMPTY.get());
            }
            else if(territoryData.getDefaultRankID() == rankData.getID()){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_DELETE_ERROR_DEFAULT.get());
            }
            else{
                territoryData.removeRank(rankData.getID());
                openTerritoryRanks(player, territoryData);
            }
        });

        GuiItem lowerSalaryButton = ItemBuilder.from(lowerSalary).asGuiItem(event -> {
            event.setCancelled(true);

            int currentSalary = rankData.getSalary();
            int amountToRemove = event.isShiftClick() && currentSalary >= 10 ? 10 : 1;

            if (currentSalary <= 0) {
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_SALARY_ERROR_LOWER.get());
                return;
            }

            rankData.removeFromSalary(amountToRemove);
            SoundUtil.playSound(player, REMOVE);
            openRankManager(player, territoryData, rankData);
        });
        GuiItem increaseSalaryButton = ItemBuilder.from(increaseSalary).asGuiItem(event -> {

            event.setCancelled(true);

            int amountToAdd = event.isShiftClick() ? 10 : 1;

            rankData.addFromSalary(amountToAdd);
            SoundUtil.playSound(player, ADD);
            openRankManager(player, territoryData, rankData);
        });

        GuiItem salaryButton = ItemBuilder.from(salary).asGuiItem(event -> event.setCancelled(true));


        GuiItem panel = rankData.getRankEnum().getRankColorGuiIcon();

        gui.setItem(1,1, panel);
        gui.setItem(1,2, panel);
        gui.setItem(1,3, panel);
        gui.setItem(1,4, panel);
        gui.setItem(1,5, roleGui);
        gui.setItem(1,6, panel);
        gui.setItem(1,7, panel);
        gui.setItem(1,8, panel);
        gui.setItem(1,9, panel);


        gui.setItem(2,2, rankIconButton);
        gui.setItem(2,3, membersRankGui);
        gui.setItem(2,4, managePermissionGui);
        gui.setItem(3,2, renameGui);
        gui.setItem(3,3, taxButton);
        gui.setItem(3,4, defaultRankButton);
        gui.setItem(3,6, deleteButton);

        gui.setItem(2,6, lowerSalaryButton);
        gui.setItem(2,7, salaryButton);
        gui.setItem(2,8, increaseSalaryButton);

        gui.setItem(4,1, IGUI.createBackArrow(player, p -> openTerritoryRanks(player, territoryData)));

        gui.open(player);

    }
    public static void openTownRankManagerAddPlayer(Player player, TerritoryData territoryData, RankData wantedRank) {

        Gui gui = IGUI.createChestGui("Add player",3);

        PlayerData playerData = PlayerDataStorage.get(player);
        int i = 0;

        for (String otherPlayerUUID : territoryData.getPlayerIDList()) {
            PlayerData wantedPlayerData = PlayerDataStorage.get(otherPlayerUUID);
            boolean skip = false;

            for (String playerWithRoleUUID : wantedRank.getPlayersID()) {
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

            GuiItem playerInfo = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                RankData otherPlayerActualRank = territoryData.getRank(wantedPlayerData);
                if(territoryData.getRank(player).getLevel() <= otherPlayerActualRank.getLevel() && !territoryData.isLeader(playerData)){
                    player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get());
                    return;
                }

                PlayerData playerStat = PlayerDataStorage.get(otherPlayerUUID);
                territoryData.setPlayerRank(playerStat, wantedRank);
                openRankManager(player, territoryData, wantedRank);
            });

            gui.setItem(i, playerInfo);
            i = i + 1;
        }
        gui.setItem(3,1, IGUI.createBackArrow(player, p -> openRankManager(player, territoryData, wantedRank)));
        gui.open(player);
    }
    public static void openRankManagerPermissions(Player player, TerritoryData territoryData, RankData rankData) {

        Gui gui = IGUI.createChestGui("Town",3);



        for(RolePermission townRolePermission : RolePermission.values()){
            if(townRolePermission.isForTerritory(territoryData)){
                GuiItem guiItem = townRolePermission.createGuiItem(player, territoryData, rankData);
                gui.addItem(guiItem);
            }
        }

        gui.setItem(3,1, IGUI.createBackArrow(player, p -> openRankManager(player, territoryData, rankData)));

        gui.open(player);

    }
    public static void openTreasury(Player player, TerritoryData territoryData) {

        Gui gui = IGUI.createChestGui("Economy",6);


        TownData townData = TownDataStorage.get(player);
        PlayerData playerStat = PlayerDataStorage.get(player);
        Budget budget = territoryData.getBudget();

        budget.createGui(gui, player);

        List<String> budgetLore = new ArrayList<>();
        budgetLore.add(Lang.GUI_TREASURY_STORAGE_DESC1.get(territoryData.getBalance()));
        budgetLore.addAll(budget.createLore());

        ItemStack budgetIcon = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_STORAGE.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=",
                budgetLore);
        ItemStack miscSpending = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_MISCELLANEOUS_SPENDING.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGMzNjA0NTIwOGY5YjVkZGNmOGM0NDMzZTQyNGIxY2ExN2I5NGY2Yjk2MjAyZmIxZTUyNzBlZThkNTM4ODFiMSJ9fX0=",
                Lang.GUI_GENERIC_CLICK_TO_OPEN_HISTORY.get());
        ItemStack donation = HeadUtils.createCustomItemStack(Material.DIAMOND, Lang.GUI_TREASURY_DONATION.get(),
                Lang.GUI_TOWN_TREASURY_DONATION_DESC1.get());
        ItemStack donationHistory = HeadUtils.createCustomItemStack(Material.PAPER, Lang.GUI_TREASURY_DONATION_HISTORY.get(),
                Lang.GUI_GENERIC_CLICK_TO_OPEN_HISTORY.get());
        ItemStack retrieveMoney = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_RETRIEVE_GOLD.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWE2NDUwMWIxYmE1M2QxZDRlOWY0MDI5MTdiNWJkNDc3MjdiMTY3MDJhY2Y2OTMwZDYxMjFjMDdkYzQyYWUxYSJ9fX0=",
                Lang.GUI_TREASURY_RETRIEVE_GOLD_DESC1.get());


        GuiItem budgetInfo = ItemBuilder.from(budgetIcon).asGuiItem(event -> event.setCancelled(true));


        GuiItem miscSpendingButton = ItemBuilder.from(miscSpending).asGuiItem(event -> {
            event.setCancelled(true);
            openTownEconomicsHistory(player, territoryData, TransactionHistoryEnum.MISCELLANEOUS);
        });
        GuiItem donationButton = ItemBuilder.from(donation).asGuiItem(event -> {
            player.sendMessage(getTANString() + Lang.WRITE_IN_CHAT_AMOUNT_OF_MONEY_FOR_DONATION.get());
            player.closeInventory();

            PlayerChatListenerStorage.register(player, new DonateToTerritory(townData));
            event.setCancelled(true);
        });
        GuiItem donationHistoryButton = ItemBuilder.from(donationHistory).asGuiItem(event -> {
            openTownEconomicsHistory(player, territoryData, TransactionHistoryEnum.DONATION);
            event.setCancelled(true);
        });

        GuiItem retrieveButton = ItemBuilder.from(retrieveMoney).asGuiItem(event -> {
            event.setCancelled(true);

            if(!townData.doesPlayerHavePermission(playerStat, MANAGE_TAXES)){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }
            player.sendMessage(getTANString() + Lang.PLAYER_WRITE_QUANTITY_IN_CHAT.get());
            PlayerChatListenerStorage.register(player,new RetrieveMoney(townData));
            player.closeInventory();

        });

        GuiItem panel = ItemBuilder.from(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE)).asGuiItem(event -> event.setCancelled(true));



        gui.setItem(1,1, panel);
        gui.setItem(1,2, panel);
        gui.setItem(1,3, panel);
        gui.setItem(1,4, panel);
        gui.setItem(1,6, panel);
        gui.setItem(1,7, panel);
        gui.setItem(1,8, panel);
        gui.setItem(1,9, panel);
        gui.setItem(2,1, panel);
        gui.setItem(2,5, panel);
        gui.setItem(2,9, panel);
        gui.setItem(3,1, panel);
        gui.setItem(3,5, panel);
        gui.setItem(3,9, panel);
        gui.setItem(4,1, panel);
        gui.setItem(4,5, panel);
        gui.setItem(4,9, panel);
        gui.setItem(5,1, panel);
        gui.setItem(5,5, panel);
        gui.setItem(5,9, panel);

        gui.setItem(1,5, budgetInfo);

        gui.setItem(2,8, miscSpendingButton);

        gui.setItem(3,2, donationButton);
        gui.setItem(3,3, donationHistoryButton);

        gui.setItem(3,4, retrieveButton);

        gui.setItem(6,1, IGUI.createBackArrow(player, p -> dispatchPlayerTown(player)));

        gui.open(player);

    }
    public static void openTownEconomicsHistory(Player player, TerritoryData territoryData, TransactionHistoryEnum transactionHistoryEnum) {
        openTownEconomicsHistory(player, territoryData, transactionHistoryEnum, 0);
    }
    public static void openTownEconomicsHistory(Player player, TerritoryData territoryData, TransactionHistoryEnum transactionHistoryEnum, int page) {

        Gui gui = IGUI.createChestGui("Town",6);
        List<GuiItem> guiItems = new ArrayList<>();

        for(List<TransactionHistory> transactionHistory : TownsAndNations.getPlugin().getDatabaseHandler().getTransactionHistory(territoryData, transactionHistoryEnum)){
            ItemStack transactionIcon = HeadUtils.createCustomItemStack(Material.PAPER, ChatColor.GREEN + transactionHistory.get(0).getDate());

            for (TransactionHistory transaction : transactionHistory) {
                HeadUtils.addLore(transactionIcon, transaction.addLoreLine());
            }
            guiItems.add(ItemBuilder.from(transactionIcon).asGuiItem(event -> event.setCancelled(true)));
        }

        Collections.reverse(guiItems);//newer first

        createIterator(gui, guiItems, 0, player,
                p -> openTreasury(player, territoryData),
                p -> openTownEconomicsHistory(player, territoryData, transactionHistoryEnum, page + 1),
                p -> openTownEconomicsHistory(player, territoryData, transactionHistoryEnum, page - 1));

        gui.open(player);
    }
    public static void openTownLevel(Player player, int level){
        Gui gui = IGUI.createChestGui("Town Upgrades | " + (level + 1),6);

        TownData townData = TownDataStorage.get(player);
        TownLevel townLevel = townData.getTownLevel();

        ItemStack whitePanel = HeadUtils.createCustomItemStack(Material.WHITE_STAINED_GLASS_PANE,"");
        ItemStack ironBars = HeadUtils.createCustomItemStack(Material.IRON_BARS,Lang.LEVEL_LOCKED.get());

        GuiItem townIcon = GuiUtil.townUpgradeResume(townData);

        GuiItem whitePanelIcon = ItemBuilder.from(whitePanel).asGuiItem(event -> event.setCancelled(true));
        GuiItem ironBarsIcon = ItemBuilder.from(ironBars).asGuiItem(event -> event.setCancelled(true));
        ItemStack greenLevelIcon = HeadUtils.createCustomItemStack(Material.GREEN_STAINED_GLASS_PANE,"");

        gui.setItem(1,1,townIcon);
        gui.setItem(2,1,whitePanelIcon);
        gui.setItem(3,1,whitePanelIcon);
        gui.setItem(4,1,whitePanelIcon);
        gui.setItem(5,1,whitePanelIcon);
        gui.setItem(6,2,whitePanelIcon);
        gui.setItem(6,3,whitePanelIcon);
        gui.setItem(6,4,whitePanelIcon);
        gui.setItem(6,5,whitePanelIcon);
        gui.setItem(6,6,whitePanelIcon);
        gui.setItem(6,9,whitePanelIcon);

        GuiItem pannelIcon;
        GuiItem bottomIcon;

        for(int i = 2; i < 10; i++){
            if(townLevel.getTownLevel() > (i-2 + level)){
                ItemStack fillerGreen = HeadUtils.createCustomItemStack(Material.LIME_STAINED_GLASS_PANE,"Level " + (i-1 + level));

                pannelIcon = ItemBuilder.from(greenLevelIcon).asGuiItem(event -> event.setCancelled(true));
                bottomIcon = ItemBuilder.from(fillerGreen).asGuiItem(event -> event.setCancelled(true));
            }
            else if(townLevel.getTownLevel() == (i-2 + level)){
                pannelIcon = ironBarsIcon;
                ItemStack upgradeTownLevel = HeadUtils.createCustomItemStack(Material.ORANGE_STAINED_GLASS_PANE,
                        Lang.GUI_TOWN_LEVEL_UP.get(),
                        Lang.GUI_TOWN_LEVEL_UP_DESC1.get(townLevel.getTownLevel()),
                        Lang.GUI_TOWN_LEVEL_UP_DESC2.get(townLevel.getTownLevel() + 1, townLevel.getMoneyRequiredForLevelUp()));

                bottomIcon = ItemBuilder.from(upgradeTownLevel).asGuiItem(event -> {
                    event.setCancelled(true);
                    townData.upgradeTown(player);
                    openTownLevel(player,level);
                });
            }
            else{
                pannelIcon = ironBarsIcon;
                ItemStack redLevel = HeadUtils.createCustomItemStack(Material.RED_STAINED_GLASS_PANE,"Town level " + (i + level - 1) + " locked");
                bottomIcon = ItemBuilder.from(redLevel).asGuiItem(event -> event.setCancelled(true));
            }
            gui.setItem(1,i, pannelIcon);
            gui.setItem(2,i, pannelIcon);
            gui.setItem(3,i, pannelIcon);
            gui.setItem(4,i, pannelIcon);
            gui.setItem(5,i, bottomIcon);
        }

        for(TownUpgrade townUpgrade : UpgradeStorage.getUpgrades()){
            GuiItem guiButton = townUpgrade.createGuiItem(player, townData, level);
            if(level + 1 <= townUpgrade.getCol() && townUpgrade.getCol() <= level + 7)
                gui.setItem(townUpgrade.getRow(),townUpgrade.getCol() + (1 - level),guiButton);
        }

        ItemStack nextPageButton = HeadUtils.makeSkullB64(
                Lang.GUI_NEXT_PAGE.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2MjYyYWYxZDVmNDE0YzU5NzA1NWMyMmUzOWNjZTE0OGU1ZWRiZWM0NTU1OWEyZDZiODhjOGQ2N2I5MmVhNiJ9fX0="
        );

        ItemStack previousPageButton = HeadUtils.makeSkullB64(
                Lang.GUI_PREVIOUS_PAGE.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQyZmRlOGI4MmU4YzFiOGMyMmIyMjY3OTk4M2ZlMzVjYjc2YTc5Nzc4NDI5YmRhZGFiYzM5N2ZkMTUwNjEifX19"
        );

        GuiItem previousButton = ItemBuilder.from(previousPageButton).asGuiItem(event -> {
            event.setCancelled(true);
            if(level > 0)
                openTownLevel(player,level - 1);
        });
        GuiItem nextButton = ItemBuilder.from(nextPageButton).asGuiItem(event -> {
            event.setCancelled(true);
            int townMaxLevel = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TownMaxLevel",10);
            if(level < (townMaxLevel - 7))
                openTownLevel(player,level + 1);
        });



        gui.setItem(6,1, IGUI.createBackArrow(player, p -> dispatchPlayerTown(player)));


        gui.setItem(6,7, previousButton);
        gui.setItem(6,8, nextButton);

        gui.open(player);

    }
    public static void openTownSettings(Player player, TownData townData) {

        Gui gui = IGUI.createChestGui("Town",4);

        PlayerData playerData = PlayerDataStorage.get(player);
        int changeTownNameCost = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("ChangeTownNameCost");


        ItemStack townIcon = townData.getIconWithInformations();
        ItemStack leaveTown = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN.get(),
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC1.get(townData.getName()),
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC2.get());
        ItemStack deleteTown = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN.get(),
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC1.get(townData.getName()),
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC2.get());
        ItemStack changeOwnershipTown = HeadUtils.createCustomItemStack(Material.BEEHIVE,
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP.get(),
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC1.get(),
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC2.get());
        ItemStack changeMessage = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK,
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_MESSAGE.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_MESSAGE_DESC1.get(townData.getDescription()));
        ItemStack toggleApplication = HeadUtils.createCustomItemStack(Material.PAPER,
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION.get(),
                (townData.isRecruiting() ? Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_ACCEPT.get() : Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_NOT_ACCEPT.get()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_CLICK_TO_SWITCH.get());
        ItemStack changeTownName = HeadUtils.createCustomItemStack(Material.NAME_TAG,
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME_DESC1.get(townData.getName()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME_DESC2.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME_DESC3.get(changeTownNameCost));
        ItemStack changeChunkColor = HeadUtils.createCustomItemStack(Material.PURPLE_WOOL,
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC1.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC2.get(new TextComponent(townData.getChunkColor() + townData.getChunkColorInHex()).getText()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC3.get());

        ItemStack changeTag = HeadUtils.createCustomItemStack(Material.FLOWER_BANNER_PATTERN,
                Lang.GUI_TOWN_SETTINGS_CHANGE_TAG.get(),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TAG_DESC1.get(townData.getColoredTag()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TAG_DESC2.get());

        GuiItem townIconButton = ItemBuilder.from(townIcon).asGuiItem(event -> event.setCancelled(true));

        GuiItem leaveTownButton = ItemBuilder.from(leaveTown).asGuiItem(event -> {
            event.setCancelled(true);
            if (townData.isLeader(playerData)){
                SoundUtil.playSound(player, NOT_ALLOWED);
                player.sendMessage(getTANString() + Lang.CHAT_CANT_LEAVE_TOWN_IF_LEADER.get());
                return;
            }

            openConfirmMenu(player, Lang.GUI_CONFIRM_PLAYER_LEAVE_TOWN.get(playerData.getName()), confirm -> {

                player.closeInventory();
                townData.removePlayer(playerData);
                player.sendMessage(getTANString() + Lang.CHAT_PLAYER_LEFT_THE_TOWN.get());
                townData.broadCastMessageWithSound(Lang.TOWN_BROADCAST_PLAYER_LEAVE_THE_TOWN.get(playerData.getName()), BAD);
            }, remove -> openTownSettings(player, townData));
        });
        GuiItem deleteButton = ItemBuilder.from(deleteTown).asGuiItem(event -> {
            event.setCancelled(true);
            if (!townData.isLeader(playerData)){
                player.sendMessage(getTANString() + Lang.CHAT_CANT_DISBAND_TOWN_IF_NOT_LEADER.get());
                return;
            }

            openConfirmMenu(player, Lang.GUI_CONFIRM_PLAYER_DELETE_TOWN.get(townData.getName()), confirm -> {
                FileUtil.addLineToHistory(Lang.HISTORY_TOWN_DELETED.get(player.getName(),townData.getName()));
                townData.delete();
                player.closeInventory();
                SoundUtil.playSound(player,GOOD);
                player.sendMessage(getTANString() + Lang.CHAT_PLAYER_TOWN_SUCCESSFULLY_DELETED.get());
            }, remove -> openTownSettings(player, townData));


        });

        GuiItem changeOwnershipTownButton = ItemBuilder.from(changeOwnershipTown).asGuiItem(event -> {

            event.setCancelled(true);

            if(townData.isLeader(playerData))
                openTownChangeOwnershipPlayerSelect(player, townData);
            else
                player.sendMessage(getTANString() + Lang.NOT_TOWN_LEADER_ERROR.get());

        });

        GuiItem changeMessageButton = ItemBuilder.from(changeMessage).asGuiItem(event -> {
            player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            PlayerChatListenerStorage.register(player, new ChangeDescription(townData, p -> openTownSettings(player, townData)));
            event.setCancelled(true);
        });

        GuiItem toggleApplicationButton = ItemBuilder.from(toggleApplication).asGuiItem(event -> {
            townData.swapRecruiting();
            openTownSettings(player, townData);
            event.setCancelled(true);
        });

        GuiItem changeTownButton = ItemBuilder.from(changeTownName).asGuiItem(event -> {
            event.setCancelled(true);

            if(townData.getBalance() < changeTownNameCost){
                player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get());
                return;
            }

            if(townData.doesPlayerHavePermission(playerData, TOWN_ADMINISTRATOR)){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
                PlayerChatListenerStorage.register(player, new ChangeTerritoryName(townData,changeTownNameCost, p -> openTownSettings(player, townData)));
            }
            else
                player.sendMessage(getTANString() + Lang.NOT_TOWN_LEADER_ERROR.get());
        });

        GuiItem changeChunkColorButton = ItemBuilder.from(changeChunkColor).asGuiItem(event -> {
            event.setCancelled(true);

            if(townData.doesPlayerHavePermission(playerData, TOWN_ADMINISTRATOR)){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT.get());
                PlayerChatListenerStorage.register(player, new ChangeColor(townData, p -> openTownSettings(player, townData)));
            }
            else
                player.sendMessage(getTANString() + Lang.NOT_TOWN_LEADER_ERROR.get());
        });

        GuiItem changeTagButton = ItemBuilder.from(changeTag).asGuiItem(event -> {
            event.setCancelled(true);

            if(townData.doesPlayerHavePermission(playerData, TOWN_ADMINISTRATOR)){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
                PlayerChatListenerStorage.register(player, new ChangeTownTag(townData, p -> openTownSettings(player,townData)));
            }

        });





        gui.setItem(4, townIconButton);
        gui.setItem(2,2, leaveTownButton);
        gui.setItem(2,3, deleteButton);
        gui.setItem(2,4, changeOwnershipTownButton);
        gui.setItem(2,6, changeMessageButton);
        gui.setItem(2,7, toggleApplicationButton);
        gui.setItem(2,8, changeTownButton);

        if(ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("EnablePlayerPrefix",false))
            gui.setItem(3,7, changeTagButton);
        gui.setItem(3,8, changeChunkColorButton);

        gui.setItem(4,1, IGUI.createBackArrow(player, p -> dispatchPlayerTown(player)));
        gui.open(player);
    }
    public static void openTownChangeOwnershipPlayerSelect(Player player, TownData townData) {

        Gui gui = IGUI.createChestGui("Town",3);

        int i = 0;
        for (String playerUUID : townData.getPlayerIDList()){
            OfflinePlayer townPlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerUUID));

            ItemStack playerHead = HeadUtils.getPlayerHead(townPlayer.getName(),townPlayer,
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC1.get(player.getName()),
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC2.get());


            GuiItem playerHeadIcon = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);

                openConfirmMenu(player, Lang.GUI_CONFIRM_CHANGE_TOWN_LEADER.get(townPlayer.getName()), confirm -> {

                    townData.setLeaderID(townPlayer.getUniqueId().toString());
                    player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_SUCCESS.get(townPlayer.getName()));
                    dispatchPlayerTown(player);

                    player.closeInventory();

                }, remove -> openTownSettings(player, townData));

            });
            gui.setItem(i, playerHeadIcon);
            i = i+1;
        }
        gui.setItem(3,1, IGUI.createBackArrow(player, p -> openTownSettings(player, townData)));
        gui.open(player);
    }
    public static void openRelations(Player player, TerritoryData territory, Consumer<Player> exitMenu) {

        Gui gui = IGUI.createChestGui("Relations - " + territory.getName(),3);


        ItemStack war = HeadUtils.createCustomItemStack(Material.IRON_SWORD,
                Lang.GUI_TOWN_RELATION_HOSTILE.get(),
                Lang.GUI_TOWN_RELATION_HOSTILE_DESC1.get());
        ItemStack embargo = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.GUI_TOWN_RELATION_EMBARGO.get(),
                Lang.GUI_TOWN_RELATION_EMBARGO_DESC1.get());
        ItemStack nap = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK,
                Lang.GUI_TOWN_RELATION_NAP.get(),
                Lang.GUI_TOWN_RELATION_NAP_DESC1.get());
        ItemStack alliance = HeadUtils.createCustomItemStack(Material.CAMPFIRE,
                Lang.GUI_TOWN_RELATION_ALLIANCE.get(),
                Lang.GUI_TOWN_RELATION_ALLIANCE_DESC1.get());
        ItemStack diplomacyProposal = HeadUtils.createCustomItemStack(Material.PAPER,
                Lang.GUI_TOWN_RELATION_DIPLOMACY_PROPOSAL.get(),
                Lang.GUI_TOWN_RELATION_DIPLOMACY_PROPOSAL_DESC1.get(),
                Lang.GUI_TOWN_RELATION_DIPLOMACY_PROPOSAL_DESC2.get(territory.getAllDiplomacyProposal().size())
                );

        GuiItem warButton = ItemBuilder.from(war).asGuiItem(event -> {
            event.setCancelled(true);
            openSingleRelation(player,territory, TownRelation.WAR,0, exitMenu);
        });
        GuiItem embargoButton = ItemBuilder.from(embargo).asGuiItem(event -> {
            event.setCancelled(true);
            openSingleRelation(player,territory, TownRelation.EMBARGO,0, exitMenu);

        });
        GuiItem napButton = ItemBuilder.from(nap).asGuiItem(event -> {
            event.setCancelled(true);
            openSingleRelation(player,territory, TownRelation.NON_AGGRESSION,0, exitMenu);

        });
        GuiItem allianceButton = ItemBuilder.from(alliance).asGuiItem(event -> {
            event.setCancelled(true);
            openSingleRelation(player,territory, TownRelation.ALLIANCE,0, exitMenu);
        });
        GuiItem proposalsButton = ItemBuilder.from(diplomacyProposal).asGuiItem(event -> {
            event.setCancelled(true);
            PlayerData playerData = PlayerDataStorage.get(player);
            if(!territory.doesPlayerHavePermission(playerData, MANAGE_TOWN_RELATION)){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }
            openProposalMenu(player, territory, 0, exitMenu);
        });

        GuiItem panel = ItemBuilder.from(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).asGuiItem(event -> event.setCancelled(true));

        gui.setItem(0, panel);
        gui.setItem(1, panel);
        gui.setItem(2, panel);
        gui.setItem(3, panel);
        gui.setItem(4, panel);
        gui.setItem(5, panel);
        gui.setItem(6, panel);
        gui.setItem(7, panel);
        gui.setItem(8, panel);


        gui.setItem(9, warButton);
        gui.setItem(11, embargoButton);
        gui.setItem(13, napButton);
        gui.setItem(15, allianceButton);
        gui.setItem(17, proposalsButton);


        gui.setItem(3,1, IGUI.createBackArrow(player,exitMenu));

        gui.setItem(19, panel);
        gui.setItem(20, panel);
        gui.setItem(21, panel);
        gui.setItem(22, panel);
        gui.setItem(23, panel);
        gui.setItem(24, panel);
        gui.setItem(25, panel);
        gui.setItem(26, panel);

        gui.open(player);
    }

    public static void openProposalMenu(Player player, TerritoryData territoryData, int page, Consumer<Player> exitMenu){

        Gui gui = IGUI.createChestGui("Diplomatic proposals",6);

        ArrayList<GuiItem> guiItems = new ArrayList<>();

        for(DiplomacyProposal diplomacyProposal : territoryData.getAllDiplomacyProposal()){
            guiItems.add(diplomacyProposal.createGuiItem(player, territoryData, page, exitMenu));
        }

        createIterator(gui, guiItems, page, player, p -> openRelations(player, territoryData, exitMenu),
                p -> openProposalMenu(player, territoryData, page - 1, exitMenu),
                p -> openProposalMenu(player, territoryData, page + 1, exitMenu));

        gui.open(player);


    }

    public static void openSingleRelation(Player player, TerritoryData mainTerritory, TownRelation relation, int page, Consumer<Player> doubleExit) {
        Gui gui = IGUI.createChestGui("Relation | page " + (page + 1), 6);

        PlayerData playerStat = PlayerDataStorage.get(player);

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for(String territoryID : mainTerritory.getRelations().getTerritoriesIDWithRelation(relation)){

            TerritoryData territoryData = TerritoryUtil.getTerritory(territoryID);
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

            GuiItem townButton = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);

                if (relation == TownRelation.WAR) {
                    if(mainTerritory.atWarWith(territoryID)){
                        player.sendMessage(getTANString() + Lang.GUI_TOWN_ATTACK_ALREADY_ATTACKING.get());
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    openStartWarSettings(player, doubleExit, new CreateAttackData(mainTerritory, territoryData));
                }
            });
            guiItems.add(townButton);
        }

        ItemStack addTownButton = HeadUtils.makeSkullB64(
                Lang.GUI_TOWN_RELATION_ADD_TOWN.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19"
        );
        ItemStack removeTownButton = HeadUtils.makeSkullB64(
                Lang.GUI_TOWN_RELATION_REMOVE_TOWN.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0="
        );

        GuiItem addRelation = ItemBuilder.from(addTownButton).asGuiItem(event -> {
            event.setCancelled(true);
            if(!mainTerritory.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_TOWN_RELATION)){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }
            openTownRelationModification(player,mainTerritory,Action.ADD,relation, 0, doubleExit);
        });
        GuiItem removeRelation = ItemBuilder.from(removeTownButton).asGuiItem(event -> {
            event.setCancelled(true);
            if(!mainTerritory.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_TOWN_RELATION)){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }
            openTownRelationModification(player,mainTerritory, Action.REMOVE,relation, 0, doubleExit);
        });

        createIterator(gui, guiItems, page, player, p -> openRelations(player, mainTerritory, doubleExit),
                p -> openSingleRelation(player, mainTerritory, relation, page - 1, doubleExit),
                p -> openSingleRelation(player, mainTerritory, relation,page - 1, doubleExit));

        gui.setItem(6,4, addRelation);
        gui.setItem(6,5, removeRelation);


        gui.open(player);
    }
    public static void openTownRelationModification(Player player, TerritoryData territory, Action action, TownRelation wantedRelation, int page, Consumer<Player> exit) {
        int nRows = 6;
        Gui gui = IGUI.createChestGui("Relation - " + territory.getName(),nRows);

        List<String> relationListID = territory.getRelations().getTerritoriesIDWithRelation(wantedRelation);
        ItemStack decorativeGlass = IGUI.getDecorativeGlass(action);
        List<GuiItem> guiItems = new ArrayList<>();


        if(action == Action.ADD){
            List<String> territories = new ArrayList<>();
            territories.addAll(TownDataStorage.getTownMap().keySet());
            territories.addAll(RegionDataStorage.getRegionStorage().keySet());

            territories.removeAll(relationListID); //Territory already have this relation
            territories.remove(territory.getID()); //Remove itself

            for(String otherTownUUID : territories){
                TerritoryData otherTerritory = TerritoryUtil.getTerritory(otherTownUUID);
                ItemStack icon = otherTerritory.getIconWithInformationAndRelation(territory);

                GuiItem iconGui = ItemBuilder.from(icon).asGuiItem(event -> {
                    event.setCancelled(true);

                    if(otherTerritory.haveNoLeader()){
                        player.sendMessage(getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_NO_LEADER.get());
                        return;
                    }

                    TownRelation actualRelation = territory.getRelationWith(otherTerritory);

                    if(wantedRelation.isSuperiorTo(actualRelation)){
                        otherTerritory.receiveDiplomaticProposal(territory, wantedRelation);
                        player.sendMessage(getTANString() + Lang.DIPLOMATIC_INVITATION_SENT_SUCCESS.get(otherTerritory.getName()));
                        SoundUtil.playSound(player, MINOR_GOOD);
                    }
                    else{
                        territory.setRelation(otherTerritory,wantedRelation);
                    }
                    openSingleRelation(player,territory, wantedRelation,0,exit);

                });
                guiItems.add(iconGui);
            }
        }
        else if(action == Action.REMOVE){
            for(String otherTownUUID : relationListID){
                TerritoryData otherTerritory = TerritoryUtil.getTerritory(otherTownUUID);
                ItemStack townIcon = otherTerritory.getIconWithInformationAndRelation(territory);

                GuiItem townGui = ItemBuilder.from(townIcon).asGuiItem(event -> {
                    event.setCancelled(true);

                    if(wantedRelation.isSuperiorTo(TownRelation.NEUTRAL)){
                        territory.setRelation(otherTerritory, TownRelation.NEUTRAL);
                    }
                    else {
                        otherTerritory.receiveDiplomaticProposal(territory, TownRelation.NEUTRAL);
                        player.sendMessage(getTANString() + Lang.DIPLOMATIC_INVITATION_SENT_SUCCESS.get(otherTerritory.getName()));
                        SoundUtil.playSound(player, MINOR_GOOD);
                    }
                    openSingleRelation(player,territory,wantedRelation,0, exit);
                });
                guiItems.add(townGui);
            }
        }


        createIterator(gui, guiItems, 0, player, p -> openSingleRelation(player, territory, wantedRelation,0, exit),
                p -> openSingleRelation(player, territory, wantedRelation,page - 1, exit),
                p -> openSingleRelation(player, territory, wantedRelation,page + 1, exit),
                decorativeGlass);


        gui.open(player);
    }
    public static void openTownChunk(Player player) {
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

        GuiItem playerChunkButton = ItemBuilder.from(playerChunkIcon).asGuiItem(event -> {
            event.setCancelled(true);
            openTownChunkPlayerSettings(player, playerTown);
        });

        GuiItem mobChunkButton = ItemBuilder.from(mobChunckIcon).asGuiItem(event -> {
            event.setCancelled(true);

            if(playerTown.getTownLevel().getBenefitsLevel("UNLOCK_MOB_BAN") >= 1)
                openTownChunkMobSettings(player,1);
            else{
                player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_LEVEL.get(DynamicLang.get("UNLOCK_MOB_BAN")));
                SoundUtil.playSound(player, NOT_ALLOWED);
            }
        });

        gui.setItem(2,4, playerChunkButton);
        gui.setItem(2,6, mobChunkButton);


        gui.setItem(3,1, IGUI.createBackArrow(player, p -> dispatchPlayerTown(player)));

        gui.open(player);
    }
    public static void openTownChunkMobSettings(Player player, int page){
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
            ItemStack mobIcon = HeadUtils.makeSkullB64(mobEnum.name(),mobEnum.getTexture(),status);

            GuiItem mobItem = new GuiItem(mobIcon, event -> {
                event.setCancelled(true);
                if(!townData.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_MOB_SPAWN)){
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

                openTownChunkMobSettings(player,page);

            });
            guiLists.add(mobItem);
        }

        createIterator(gui, guiLists, page, player, p -> openTownChunk(player),
                p -> openTownChunkMobSettings(player, page + 1),
                p -> openTownChunkMobSettings(player, page - 1));


        gui.open(player);
    }
    public static void openTownPropertiesMenu(Player player, int page){
        int nRows = 6;

        Gui gui = IGUI.createChestGui("Properties",nRows);
        ArrayList<GuiItem> guiItems = new ArrayList<>();

        PlayerData playerData = PlayerDataStorage.get(player);
        TownData townData = TownDataStorage.get(playerData);

        for (PropertyData townProperty : townData.getPropertyDataList()){
            ItemStack property = townProperty.getIcon();

            GuiItem propertyButton = ItemBuilder.from(property).asGuiItem(event -> {
                event.setCancelled(true);
                if(!playerData.haveTown()){
                    player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.get());
                    SoundUtil.playSound(player, NOT_ALLOWED);
                    return;
                }
                if(!townData.doesPlayerHavePermission(playerData, RolePermission.MANAGE_PROPERTY)){
                    player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                    SoundUtil.playSound(player, NOT_ALLOWED);
                    return;
                }
                openPropertyManagerMenu(player,townProperty);
            });
            guiItems.add(propertyButton);
        }

        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> dispatchPlayerTown(player),
                p -> openTownPropertiesMenu(player,page + 1),
                p -> openTownPropertiesMenu(player,page - 1));
        gui.open(player);
    }
    public static void openTownChunkPlayerSettings(Player player, TownData territoryData){
        Gui gui = IGUI.createChestGui("Chunk permission",4);

        TownData townData = TownDataStorage.get(player);


        for(ChunkPermissionType type : ChunkPermissionType.values()){
            RelationPermission permission = townData.getPermission(type).getOverallPermission();
            ItemStack icon = type.getIcon(permission);
                GuiItem guiItem = ItemBuilder.from(icon).asGuiItem(event -> {
                        event.setCancelled(true);
                        if (!territoryData.doesPlayerHavePermission(player, RolePermission.MANAGE_CLAIM_SETTINGS)) {
                            player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                            return;
                        }
                        if(event.isLeftClick()){
                            territoryData.nextPermission(type);
                            openTownChunkPlayerSettings(player, territoryData);
                        }
                        else if(event.isRightClick()){
                            openPlayerListForChunkPermission(player, territoryData, type, 0);
                        }

                });

            gui.addItem(guiItem);
        }

        gui.setItem(27, IGUI.createBackArrow(player, p -> openTownChunk(player)));

        gui.open(player);
    }

    private static void openPlayerListForChunkPermission(Player player, TownData territoryData, ChunkPermissionType type, int page) {
        Gui gui = IGUI.createChestGui(type.getLabel(),6);

        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());

        List<GuiItem> guiItems = new ArrayList<>();

        for(String authorizedPlayerID : territoryData.getPermission(type).getAuthorizedPlayers()){
            OfflinePlayer authorizedPlayer = Bukkit.getOfflinePlayer(UUID.fromString(authorizedPlayerID));
            ItemStack icon = HeadUtils.getPlayerHead(authorizedPlayer.getName(),authorizedPlayer,
                    Lang.GUI_TOWN_MEMBER_DESC3.get());

            GuiItem guiItem = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);
                if(!territoryData.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_CLAIM_SETTINGS)){
                    player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                    return;
                }
                if(event.isRightClick()){
                    territoryData.getPermission(type).removeSpecificPlayerPermission(authorizedPlayerID);
                    openPlayerListForChunkPermission(player, territoryData, type, page);
                }
            });
            guiItems.add(guiItem);
        }

        createIterator(gui, guiItems, 0, player,
                p -> openTownChunkPlayerSettings(player, territoryData),
                p -> openPlayerListForChunkPermission(player, territoryData, type, page + 1),
                p -> openPlayerListForChunkPermission(player, territoryData, type, page + 1));


        ItemStack addIcon = HeadUtils.makeSkullB64(Lang.GUI_GENERIC_ADD_BUTTON.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");

        GuiItem addButton = ItemBuilder.from(addIcon).asGuiItem(event -> {
            event.setCancelled(true);
            if(!territoryData.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_CLAIM_SETTINGS)){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                SoundUtil.playSound(player, NOT_ALLOWED);
                return;
            }
            openAddPlayerForChunkPermission(player, territoryData, type, 0);
        });

        gui.setItem(6,3, addButton);

        gui.open(player);
    }

    private static void openAddPlayerForChunkPermission(Player player, TownData territoryData, ChunkPermissionType type, int page) {
        Gui gui = IGUI.createChestGui("Add player",6);

        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());

        List<GuiItem> guiItems = new ArrayList<>();

        for(Player playerToAdd : Bukkit.getOnlinePlayers()){

            PlayerData playerData = PlayerDataStorage.get(playerToAdd);
            if(territoryData.getPermission(type).isAllowed(territoryData, playerData))
                continue;

            ItemStack icon = HeadUtils.getPlayerHead(playerToAdd.getName(),playerToAdd,
                    Lang.GUI_GENERIC_ADD_BUTTON.get());

            GuiItem guiItem = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);
                if(!territoryData.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_CLAIM_SETTINGS)){
                    player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                    return;
                }
                territoryData.getPermission(type).addSpecificPlayerPermission(playerToAdd.getUniqueId().toString());
                openPlayerListForChunkPermission(player, territoryData, type, 0);
                SoundUtil.playSound(player, ADD);

            });
            guiItems.add(guiItem);
        }

        createIterator(gui, guiItems, 0, player,
                p -> openPlayerListForChunkPermission(player, territoryData, type, 0),
                p -> openAddPlayerForChunkPermission(player, territoryData, type, page + 1),
                p -> openAddPlayerForChunkPermission(player, territoryData, type, page + 1));

        gui.open(player);
    }


    public static void openNoRegionMenu(Player player){

        Gui gui = IGUI.createChestGui("Region",3);


        int regionCost = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("regionCost");

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

        GuiItem createRegionButton = ItemBuilder.from(createRegion).asGuiItem(event -> {
            event.setCancelled(true);
            PlayerData playerData = PlayerDataStorage.get(player);
            if(!playerData.haveTown()){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.get());
                return;
            }
            double townMoney = TownDataStorage.get(player).getBalance();
            if (townMoney < regionCost) {
                player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY_EXTENDED.get(regionCost - townMoney));
            }
            else {
                player.sendMessage(getTANString() + Lang.WRITE_IN_CHAT_NEW_REGION_NAME.get());
                player.closeInventory();

                PlayerChatListenerStorage.register(player, new CreateRegion());
            }
        });

        GuiItem browseRegionButton = ItemBuilder.from(browseRegion).asGuiItem(event -> {
            event.setCancelled(true);
            browseTerritory(player, null, BrowseScope.REGIONS,p -> openNoRegionMenu(player), 0);
        });

        gui.setItem(2,4, createRegionButton);
        gui.setItem(2,6, browseRegionButton);
        gui.setItem(3,1, IGUI.createBackArrow(player, p -> openMainMenu(player)));

        gui.open(player);
    }
    private static void openRegionMenu(Player player) {

        Gui gui = IGUI.createChestGui("Region",3);

        PlayerData playerStat = PlayerDataStorage.get(player);
        RegionData playerRegion = playerStat.getRegion();

        ItemStack regionIcon = getRegionIcon(playerRegion);

        ItemStack treasury = HeadUtils.makeSkullB64(Lang.GUI_TOWN_TREASURY_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=",
                Lang.GUI_TOWN_TREASURY_ICON_DESC1.get());

        ItemStack hierarchy = HeadUtils.makeSkullB64(Lang.GUI_HIERARCHY_MENU.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=",
                Lang.GUI_HIERARCHY_MENU_DESC1.get());

        ItemStack memberIcon = HeadUtils.makeSkullB64(Lang.GUI_TOWN_MEMBERS_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Q0ZDQ5NmIxZGEwNzUzNmM5NGMxMzEyNGE1ODMzZWJlMGM1MzgyYzhhMzM2YWFkODQ2YzY4MWEyOGQ5MzU2MyJ9fX0=",
                Lang.GUI_TOWN_MEMBERS_ICON_DESC1.get());

        ItemStack manageLaws = HeadUtils.makeSkullURL(Lang.GUI_MANAGE_LAWS.get() ,"https://textures.minecraft.net/texture/1818d1cc53c275c294f5dfb559174dd931fc516a85af61a1de256aed8bca5e7",
                Lang.GUI_MANAGE_LAWS_DESC1.get());
        ItemStack browse = HeadUtils.makeSkullB64(Lang.GUI_OTHER_REGION_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMzc0ZTIxYjgxYzBiMjFhYmViOGU5N2UxM2UwNzdkM2VkMWVkNDRmMmU5NTZjNjhmNjNhM2UxOWU4OTlmNiJ9fX0=",
                Lang.GUI_OTHER_REGION_ICON_DESC1.get());
        ItemStack diplomacy = HeadUtils.makeSkullB64(Lang.GUI_RELATION_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUwN2Q2ZGU2MzE4MzhlN2E3NTcyMGU1YjM4ZWYxNGQyOTY2ZmRkODQ4NmU3NWQxZjY4MTJlZDk5YmJjYTQ5OSJ9fX0=",
                Lang.GUI_RELATION_ICON_DESC1.get());
        ItemStack settingIcon = HeadUtils.makeSkullB64(Lang.GUI_TOWN_SETTINGS_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVkMmNiMzg0NThkYTE3ZmI2Y2RhY2Y3ODcxNjE2MDJhMjQ5M2NiZjkzMjMzNjM2MjUzY2ZmMDdjZDg4YTljMCJ9fX0=",
                Lang.GUI_TOWN_SETTINGS_ICON_DESC1.get());
        ItemStack war = HeadUtils.makeSkullB64(Lang.GUI_ATTACK_ICON.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjVkZTRmZjhiZTcwZWVlNGQxMDNiMWVlZGY0NTRmMGFiYjlmMDU2OGY1ZjMyNmVjYmE3Y2FiNmE0N2Y5YWRlNCJ9fX0=",
                Lang.GUI_ATTACK_ICON_DESC1.get());


        GuiItem regionButton = ItemBuilder.from(regionIcon).asGuiItem(event -> {
            event.setCancelled(true);

            if(!playerRegion.doesPlayerHavePermission(playerStat, TOWN_ADMINISTRATOR))
                return;
            if(event.getCursor() == null)
                return;

            ItemStack itemMaterial = event.getCursor();
            if(itemMaterial.getType() == Material.AIR ){
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_NO_ITEM_SHOWED.get());
            }
            else {
                playerRegion.setIcon(itemMaterial);
                openRegionMenu(player);
                player.sendMessage(getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_CHANGED_ICON_SUCCESS.get());
            }
        });
        GuiItem treasuryButton = ItemBuilder.from(treasury).asGuiItem(event -> {
            event.setCancelled(true);
            openTreasury(player, playerRegion);
        });
        GuiItem hierarchyButton = ItemBuilder.from(hierarchy).asGuiItem(event -> {
            event.setCancelled(true);
            openHierarchyMenu(player, playerRegion);
        });
        GuiItem memberIconButton = ItemBuilder.from(memberIcon).asGuiItem(event -> {
            event.setCancelled(true);
            openMemberList(player, playerRegion);
        });
        GuiItem browseButton = ItemBuilder.from(browse).asGuiItem(event -> {
            event.setCancelled(true);
            browseTerritory(player, playerRegion, BrowseScope.ALL,p -> openRegionMenu(player), 0);
        });
        GuiItem diplomacyButton = ItemBuilder.from(diplomacy).asGuiItem(event -> {
            event.setCancelled(true);
            openRelations(player, playerRegion, p -> openRegionMenu(player));
        });
        GuiItem settingsButton = ItemBuilder.from(settingIcon).asGuiItem(event -> {
            event.setCancelled(true);
            openRegionSettings(player);
        });
        GuiItem warIcon = ItemBuilder.from(war).asGuiItem(event -> {
            event.setCancelled(true);
            openWarMenu(player, playerRegion, p -> dispatchPlayerRegion(player), 0);
        });


        gui.setItem(1,5, regionButton);
        gui.setItem(2,2, treasuryButton);
        gui.setItem(2,3, hierarchyButton);
        gui.setItem(2,4, memberIconButton);
        gui.setItem(2,5, browseButton);
        gui.setItem(2,6, warIcon);
        gui.setItem(2,7, diplomacyButton);
        gui.setItem(2,8, settingsButton);

        gui.setItem(3,1, IGUI.createBackArrow(player, p -> openMainMenu(player)));

        gui.open(player);
    }

    private static void openVassalsList(Player player, TerritoryData territoryData, int page){

        Gui gui = IGUI.createChestGui("Region",6);
        PlayerData playerData = PlayerDataStorage.get(player);

        List<GuiItem> guiList = new ArrayList<>();

        for (TerritoryData townData : territoryData.getVassals()){
            ItemStack townIcon = townData.getIconWithInformations();
            GuiItem townInfo = ItemBuilder.from(townIcon).asGuiItem(event -> event.setCancelled(true));
            guiList.add(townInfo);
        }

        ItemStack addTown = HeadUtils.makeSkullB64(Lang.GUI_INVITE_TOWN_TO_REGION.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
        ItemStack removeTown = HeadUtils.makeSkullB64(Lang.GUI_KICK_TOWN_TO_REGION.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=");


        GuiItem addButton = ItemBuilder.from(addTown).asGuiItem(event -> {
            event.setCancelled(true);
                if(!territoryData.doesPlayerHavePermission(playerData, TOWN_ADMINISTRATOR)){
                    player.sendMessage(getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get());
                    return;
                }
            openAddVassal(player, territoryData, page);
        });
        GuiItem removeButton = ItemBuilder.from(removeTown).asGuiItem(event -> {
            event.setCancelled(true);
            if(!territoryData.doesPlayerHavePermission(playerData, TOWN_ADMINISTRATOR)){
                player.sendMessage(getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get());
                return;
            }
            openRemoveVassal(player, territoryData, page);
        });

        createIterator(gui, guiList, 0, player, p -> openHierarchyMenu(player, territoryData),
                p -> openVassalsList(player, territoryData, page - 1),
                p -> openVassalsList(player, territoryData, page + 1));



        gui.setItem(6,3, addButton);
        gui.setItem(6,4, removeButton);
        gui.open(player);
    }
    private static void openAddVassal(Player player, TerritoryData territoryData, int page) {

        Gui gui = IGUI.createChestGui("Vassals", 6);

        List<GuiItem> guiItems = new ArrayList<>();

        for (TerritoryData potentialVassal : territoryData.getPotentialVassals()) {
            if(territoryData.isVassal(potentialVassal) || potentialVassal.containsVassalisationProposal(territoryData))
                continue;

            ItemStack territoryIcon = potentialVassal.getIconWithInformationAndRelation(territoryData);
            HeadUtils.addLore(territoryIcon, Lang.GUI_REGION_INVITE_TOWN_DESC1.get());

            GuiItem townButton = ItemBuilder.from(territoryIcon).asGuiItem(event -> {
                event.setCancelled(true);
                potentialVassal.addVassalisationProposal(territoryData);
                openAddVassal(player, territoryData, page);
            });
            guiItems.add(townButton);
        }

        createIterator(gui, guiItems, page, player, p -> openVassalsList(player, territoryData, page),
                p -> openAddVassal(player, territoryData, page - 1),
                p -> openAddVassal(player, territoryData, page + 1));

        gui.open(player);
    }

    private static void openRemoveVassal(Player player, TerritoryData territoryData, int page) {

        Gui gui = IGUI.createChestGui("Vassals", 6);

        List<GuiItem> guiItems = new ArrayList<>();
        for (TerritoryData townData : territoryData.getVassals()){
            ItemStack townIcon = townData.getIconWithInformationAndRelation(territoryData);
            HeadUtils.addLore(townIcon, Lang.GUI_REGION_INVITE_TOWN_DESC1.get());

            GuiItem townButton = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);

                if(townData.isCapitalOf(territoryData)){
                    player.sendMessage(getTANString() + Lang.CANT_KICK_REGIONAL_CAPITAL.get(townData.getName()));
                    return;
                }
                territoryData.broadCastMessageWithSound(Lang.GUI_REGION_KICK_TOWN_BROADCAST.get(townData.getName()), BAD);
                townData.removeOverlord();
                territoryData.removeVassal(townData);
                player.closeInventory();
            });
            guiItems.add(townButton);
        }

        createIterator(gui, guiItems, page, player, p -> openVassalsList(player, territoryData, page),
                p -> openRemoveVassal(player, territoryData, page - 1),
                p -> openRemoveVassal(player, territoryData, page + 1));

        gui.open(player);
    }
    private static void openRegionSettings(Player player) {

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
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC2.get(playerRegion.getChunkColor() + playerTown.getChunkColorInHex()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC3.get()
        );

        GuiItem regionInfo = ItemBuilder.from(regionIcon).asGuiItem(event -> event.setCancelled(true));

        GuiItem deleteButton = ItemBuilder.from(deleteRegion).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerRegion.isLeader(playerStat)){
                player.sendMessage(getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get());
                return;
            }

            openConfirmMenu(player, Lang.GUI_CONFIRM_DELETE_REGION.get(playerRegion.getName()), confirm -> {
                FileUtil.addLineToHistory(Lang.HISTORY_REGION_DELETED.get(player.getName(),playerRegion.getName()));
                playerRegion.delete();
                SoundUtil.playSound(player, GOOD);
                player.sendMessage(getTANString() + Lang.CHAT_PLAYER_REGION_SUCCESSFULLY_DELETED.get());
                openMainMenu(player);
            }, remove -> openRegionSettings(player));
        });

        GuiItem changeCapitalButton = ItemBuilder.from(changeLeader).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerRegion.isLeader(playerStat)){
                player.sendMessage(getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get());
                return;
            }
            openRegionChangeOwnership(player,0);
        });

        GuiItem changeDescriptionButton = ItemBuilder.from(changeDescription).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerRegion.doesPlayerHavePermission(playerStat, TOWN_ADMINISTRATOR)){
                player.sendMessage(getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get());
                return;
            }
            player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            PlayerChatListenerStorage.register(player, new ChangeDescription(playerRegion, p -> openRegionSettings(player)));
        });

        GuiItem changeNameButton = ItemBuilder.from(changeName).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerRegion.doesPlayerHavePermission(playerStat, TOWN_ADMINISTRATOR)){
                player.sendMessage(getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get());
                return;
            }

            player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            PlayerChatListenerStorage.register(player, new ChangeTerritoryName(playerRegion, 0, p -> openRegionSettings(player)));
        });

        GuiItem changeChunkColorButton = ItemBuilder.from(changeColor).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(getTANString() + Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT.get());
            PlayerChatListenerStorage.register(player, new ChangeColor(playerRegion, p -> openRegionSettings(player)));
        });


        gui.setItem(1,5, regionInfo);

        gui.setItem(2,2, deleteButton);
        gui.setItem(2,3, changeCapitalButton);

        gui.setItem(2,6, changeDescriptionButton);
        gui.setItem(2,7, changeNameButton);
        gui.setItem(2,8,changeChunkColorButton);


        gui.setItem(3,1, IGUI.createBackArrow(player, p -> openRegionMenu(player)));

        gui.open(player);
    }

    public static void openRegionChangeOwnership(Player player, int page){

            Gui gui = IGUI.createChestGui("Region", 6);
            PlayerData playerData = PlayerDataStorage.get(player);
            RegionData regionData = playerData.getRegion();

            ArrayList<GuiItem> guiItems = new ArrayList<>();
            for(String playerID : regionData.getPlayerIDList()){

                PlayerData iteratePlayerData = PlayerDataStorage.get(playerID);
                ItemStack switchPlayerIcon = HeadUtils.getPlayerHead(Bukkit.getOfflinePlayer(UUID.fromString(playerID)));

                GuiItem switchPlayerButton = ItemBuilder.from(switchPlayerIcon).asGuiItem(event -> {
                    event.setCancelled(true);

                    openConfirmMenu(player, Lang.GUI_CONFIRM_CHANGE_LEADER.get(iteratePlayerData.getName()), confirm -> {
                        FileUtil.addLineToHistory(Lang.HISTORY_REGION_CAPITAL_CHANGED.get(player.getName(), regionData.getCapital().getName(), playerData.getTown().getName() ));
                        regionData.setLeaderID(iteratePlayerData.getID());

                        regionData.broadCastMessageWithSound(Lang.GUI_REGION_SETTINGS_REGION_CHANGE_LEADER_BROADCAST.get(iteratePlayerData.getName()),GOOD);

                        if(!regionData.getCapital().getID().equals(iteratePlayerData.getTown().getID())){
                            regionData.broadCastMessage(getTANString() + Lang.GUI_REGION_SETTINGS_REGION_CHANGE_CAPITAL_BROADCAST.get(iteratePlayerData.getTown().getName()));
                            regionData.setCapital(iteratePlayerData.getTownId());
                        }
                        openRegionSettings(player);
                    }, remove -> openRegionChangeOwnership(player,page));
                });
                guiItems.add(switchPlayerButton);

            }

            GuiUtil.createIterator(gui, guiItems, page, player,
                    p -> openRegionSettings(player),
                    p -> openRegionChangeOwnership(player,page + 1),
                    p -> openRegionChangeOwnership(player,page - 1));


            gui.open(player);
    }


    public static void dispatchLandmarkGui(Player player, Landmark landmark){

        TownData townData = TownDataStorage.get(player);

        if(!landmark.hasOwner()){
            openLandmarkNoOwner(player,landmark);
            return;
        }
        if(townData.ownLandmark(landmark)){
            openPlayerOwnLandmark(player,landmark);
            return;
        }
        TownData owner = TownDataStorage.get(landmark.getOwnerID());
        player.sendMessage(getTANString() + Lang.LANDMARK_ALREADY_CLAIMED.get(owner.getName()));
        SoundUtil.playSound(player, MINOR_BAD);

    }

    private static void openLandmarkNoOwner(Player player, Landmark landmark) {
        Gui gui = IGUI.createChestGui("Landmark - unclaimed", 3);

        GuiItem landmarkIcon = ItemBuilder.from(landmark.getIcon()).asGuiItem(event -> event.setCancelled(true));

        TownData playerTown = TownDataStorage.get(player);

        ItemStack claimLandmark = HeadUtils.makeSkullB64(
                Lang.GUI_TOWN_RELATION_ADD_TOWN.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                playerTown.canClaimMoreLandmarks() ? Lang.GUI_LANDMARK_LEFT_CLICK_TO_CLAIM.get() : Lang.GUI_LANDMARK_TOWN_FULL.get()
        );

        GuiItem claimLandmarkGui = ItemBuilder.from(claimLandmark).asGuiItem(event -> {
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
        GuiItem panelGui = ItemBuilder.from(panel).asGuiItem(event -> event.setCancelled(true));

        gui.setItem(1,5,landmarkIcon);
        gui.setItem(2,5, claimLandmarkGui);

        gui.setItem(3,1, IGUI.createBackArrow(player,Player::closeInventory));
        gui.setItem(3,2,panelGui);
        gui.setItem(3,3,panelGui);
        gui.setItem(3,4,panelGui);
        gui.setItem(3,5,panelGui);
        gui.setItem(3,6,panelGui);
        gui.setItem(3,7,panelGui);
        gui.setItem(3,8,panelGui);
        gui.setItem(3,9,panelGui);

        gui.open(player);
    }

    private static void openPlayerOwnLandmark(Player player, Landmark landmark) {
        TownData townData = TownDataStorage.get(landmark.getOwnerID());
        Gui gui = IGUI.createChestGui("Landmark - " + townData.getName(), 3);

        int quantity = landmark.computeStoredReward(townData);

        ItemStack removeTown = HeadUtils.makeSkullB64(
                Lang.GUI_REMOVE_LANDMARK.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0="
        );

        String bagTexture   ;
        if(quantity == 0)
            bagTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjRjMTY0YmFjMjE4NGE3NmExZWU5NjkxMzI0MmUzMzVmMWQ0MTFjYWZmNTEyMDVlYTM5YjIwNWU2ZjhmMDU4YSJ9fX0=";
        else
            bagTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTliOTA2YjIxNTVmMTkzNzg3MDQyMzM4ZDA1Zjg0MDM5MWMwNWE2ZDNlODE2MjM5MDFiMjk2YmVlM2ZmZGQyIn19fQ==";

        ItemStack collectRessources = HeadUtils.makeSkullB64(
                Lang.GUI_COLLECT_LANDMARK.get(),
                bagTexture,
                Lang.GUI_COLLECT_LANDMARK_DESC1.get(),
                Lang.GUI_COLLECT_LANDMARK_DESC2.get(quantity)
        );




        GuiItem removeTownButton = ItemBuilder.from(removeTown).asGuiItem(event -> {
            event.setCancelled(true);
            townData.removeLandmark(landmark);
            TownData playerTown = TownDataStorage.get(player);
            playerTown.broadCastMessageWithSound(Lang.GUI_LANDMARK_REMOVED.get(),BAD);
            dispatchLandmarkGui(player,landmark);
        });

        GuiItem collectRessourcesButton = ItemBuilder.from(collectRessources).asGuiItem(event -> {
            event.setCancelled(true);
            landmark.giveToPlayer(player,quantity);
            player.sendMessage(getTANString() + Lang.GUI_LANDMARK_REWARD_COLLECTED.get(quantity));
            SoundUtil.playSound(player, GOOD);
            dispatchLandmarkGui(player,landmark);
        });


        ItemStack panel = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        GuiItem panelIcon = ItemBuilder.from(panel).asGuiItem(event -> event.setCancelled(true));


        GuiItem landmarkIcon = ItemBuilder.from(landmark.getIcon()).asGuiItem(event -> event.setCancelled(true));
        gui.setItem(1,1,panelIcon);
        gui.setItem(1,2,panelIcon);
        gui.setItem(1,3,panelIcon);
        gui.setItem(1,4,panelIcon);
        gui.setItem(1,5,landmarkIcon);
        gui.setItem(1,6,panelIcon);
        gui.setItem(1,7,panelIcon);
        gui.setItem(1,8,panelIcon);
        gui.setItem(1,9,panelIcon);

        gui.setItem(2,1,panelIcon);

        gui.setItem(2,6,collectRessourcesButton);
        gui.setItem(2,8,removeTownButton);

        gui.setItem(2,9,panelIcon);

        gui.setItem(3,1, IGUI.createBackArrow(player,Player::closeInventory));
        gui.setItem(3,2,panelIcon);
        gui.setItem(3,3,panelIcon);
        gui.setItem(3,4,panelIcon);
        gui.setItem(3,5,panelIcon);
        gui.setItem(3,6,panelIcon);
        gui.setItem(3,7,panelIcon);
        gui.setItem(3,8, panelIcon);
        gui.setItem(3,9,panelIcon);



        gui.open(player);
    }

    public static void openConfirmMenu(Player player, String confirmLore, Consumer<Void> confirmAction, Consumer<Void> returnAction) {

        Gui gui = IGUI.createChestGui("Confirm action", 3);

        ItemStack confirm = HeadUtils.makeSkullB64(Lang.GENERIC_CONFIRM_ACTION.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNhNDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIwZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=",
                confirmLore);

        ItemStack cancel = HeadUtils.makeSkullB64(Lang.GENERIC_CANCEL_ACTION.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==",
                Lang.GENERIC_CANCEL_ACTION_DESC1.get());

        GuiItem confirmButton = ItemBuilder.from(confirm).asGuiItem(event -> {
            event.setCancelled(true);
            confirmAction.accept(null);
        });

        GuiItem cancelButton = ItemBuilder.from(cancel).asGuiItem(event -> {
            event.setCancelled(true);
            returnAction.accept(null);
        });

        gui.setItem(2,4,confirmButton);
        gui.setItem(2,6,cancelButton);

        gui.open(player);
    }

    public static void openHierarchyMenu(Player player, TerritoryData territoryData) {
        Gui gui = IGUI.createChestGui("Vassals", 3);

        GuiItem decorativeGlass = IGUI.getUnnamedItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE);

        GuiItem overlordInfo;
        if(territoryData.canHaveOverlord()){
            GuiItem overlordButton;
            if(territoryData.haveOverlord()){
                TerritoryData overlord = territoryData.getOverlord();
                ItemStack overlordIcon = overlord.getIconItem();
                ItemMeta meta = overlordIcon.getItemMeta();
                meta.setDisplayName(Lang.OVERLORD_GUI.get());
                List<String> lore = new ArrayList<>();
                lore.add(Lang.GUI_OVERLORD_INFO.get(overlord.getName()));
                meta.setLore(lore);
                overlordIcon.setItemMeta(meta);

                ItemStack declareIndependence = HeadUtils.createCustomItemStack(Material.SPRUCE_DOOR, Lang.GUI_OVERLORD_DECLARE_INDEPENDENCE.get(),
                        Lang.GUI_OVERLORD_DECLARE_INDEPENDENCE_DESC1.get());

                ItemStack donateToOverlord = HeadUtils.createCustomItemStack(Material.DIAMOND, Lang.GUI_OVERLORD_DONATE.get(),
                        Lang.GUI_OVERLORD_DONATE_DESC1.get());

                overlordInfo = ItemBuilder.from(overlordIcon).asGuiItem(event -> event.setCancelled(true));
                overlordButton = ItemBuilder.from(declareIndependence).asGuiItem(event -> {
                    event.setCancelled(true);
                    if (!territoryData.haveOverlord()) {
                        player.sendMessage(getTANString() + Lang.TERRITORY_NO_OVERLORD.get());
                        openHierarchyMenu(player, territoryData); //This should trigger only if town have been kicked from region during the menu
                        return;
                    }
                    TerritoryData overlordData = territoryData.getOverlord();

                    if (territoryData.isCapital()){
                        player.sendMessage(getTANString() + Lang.CANNOT_DECLARE_INDEPENDENCE_BECAUSE_CAPITAL.get(territoryData.getColoredName()));
                        return;
                    }

                    openConfirmMenu(player, Lang.GUI_CONFIRM_DECLARE_INDEPENDENCE.get(territoryData.getColoredName(), overlord.getColoredName()), confirm -> {
                        overlordData.removeVassal(territoryData);
                        territoryData.removeOverlord();
                        territoryData.broadCastMessageWithSound(Lang.TOWN_BROADCAST_TOWN_LEFT_REGION.get(territoryData.getName(), overlordData.getName()), BAD);
                        overlordData.broadCastMessage(Lang.REGION_BROADCAST_TOWN_LEFT_REGION.get(territoryData.getName()));

                        player.closeInventory();
                    },remove -> openHierarchyMenu(player, territoryData));
                });
                GuiItem donateToOverlordButton = ItemBuilder.from(donateToOverlord).asGuiItem(event -> {
                    event.setCancelled(true);
                    player.sendMessage(getTANString() + Lang.WRITE_IN_CHAT_AMOUNT_OF_MONEY_FOR_DONATION.get());
                    PlayerChatListenerStorage.register(player, new DonateToTerritory(overlord));
                });
                gui.setItem(2,3, donateToOverlordButton);
            }
            else {
                ItemStack noCurrentOverlord = HeadUtils.createCustomItemStack(Material.GOLDEN_HELMET, Lang.OVERLORD_GUI.get(),
                        Lang.NO_OVERLORD.get());
                overlordInfo = ItemBuilder.from(noCurrentOverlord).asGuiItem(event -> event.setCancelled(true));

                ItemStack joinOverlord = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK, Lang.BROWSE_OVERLORD_INVITATION.get(),
                        Lang.BROWSE_OVERLORD_INVITATION_DESC1.get(territoryData.getNumberOfVassalisationProposals()));

                overlordButton = ItemBuilder.from(joinOverlord).asGuiItem(event -> {
                    event.setCancelled(true);
                    openChooseOverlordMenu(player, territoryData,0);
                });
            }
            gui.setItem(2,2, overlordButton);
        }
        else{
            ItemStack noOverlordItem = HeadUtils.createCustomItemStack(Material.IRON_BARS, Lang.OVERLORD_GUI.get(),
                    Lang.CANNOT_HAVE_OVERLORD.get());
            overlordInfo = ItemBuilder.from(noOverlordItem).asGuiItem(event -> event.setCancelled(true));

            gui.setItem(2,2, overlordInfo);
            gui.setItem(2,3, overlordInfo);
            gui.setItem(2,4, overlordInfo);

        }
        gui.setItem(1,3, overlordInfo);

        GuiItem vassalInfo;
        if(territoryData.canHaveVassals()){
            ItemStack vassalIcon = HeadUtils.createCustomItemStack(Material.GOLDEN_SWORD, Lang.VASSAL_GUI.get(),
                    Lang.VASSAL_GUI_DESC1.get(territoryData.getColoredName(), territoryData.getVassalCount()));

            ItemStack vassals = HeadUtils.makeSkullB64(Lang.GUI_REGION_TOWN_LIST.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=",
                    Lang.GUI_REGION_TOWN_LIST_DESC1.get());
            GuiItem vassalsButton = ItemBuilder.from(vassals).asGuiItem(event -> {
                event.setCancelled(true);
                openVassalsList(player, territoryData, 0);
            });
            vassalInfo = ItemBuilder.from(vassalIcon).asGuiItem(event -> event.setCancelled(true));
            gui.setItem(2,6, vassalsButton);
        }
        else {
            ItemStack noVassalsIcon = HeadUtils.createCustomItemStack(Material.IRON_BARS, Lang.VASSAL_GUI.get(),
                    Lang.CANNOT_HAVE_VASSAL.get());
            vassalInfo = ItemBuilder.from(noVassalsIcon).asGuiItem(event -> event.setCancelled(true));
            gui.setItem(2,6, vassalInfo);
            gui.setItem(2,7, vassalInfo);
            gui.setItem(2,8, vassalInfo);
        }
        gui.setItem(1,7, vassalInfo);








        gui.setItem(1,1, decorativeGlass);
        gui.setItem(1,2, decorativeGlass);
        gui.setItem(1,4, decorativeGlass);
        gui.setItem(1,5, decorativeGlass);
        gui.setItem(1,6, decorativeGlass);
        gui.setItem(1,8, decorativeGlass);
        gui.setItem(1,9, decorativeGlass);
        gui.setItem(2,5, decorativeGlass);
        gui.setItem(2,1, decorativeGlass);
        gui.setItem(2,9, decorativeGlass);
        gui.setItem(3,1, decorativeGlass);
        gui.setItem(3,2, decorativeGlass);
        gui.setItem(3,3, decorativeGlass);
        gui.setItem(3,4, decorativeGlass);
        gui.setItem(3,5, decorativeGlass);
        gui.setItem(3,6, decorativeGlass);
        gui.setItem(3,7, decorativeGlass);
        gui.setItem(3,8, decorativeGlass);
        gui.setItem(3,9, decorativeGlass);


        gui.setItem(3,1, IGUI.createBackArrow(player, p -> territoryData.openMainMenu(player)));
        gui.open(player);
    }

    public static void openChooseOverlordMenu(Player player, TerritoryData territoryData, int page) {
        Gui gui = IGUI.createChestGui("Choose Overlord", 6);

        List<GuiItem> guiItems = territoryData.getAllSubjugationProposals(player, page);

        createIterator(gui, guiItems, page, player,
                p -> openHierarchyMenu(player, territoryData),
                p -> openChooseOverlordMenu(player, territoryData,page + 1),
                p -> openChooseOverlordMenu(player, territoryData, page - 1));


        gui.open(player);
    }


}
