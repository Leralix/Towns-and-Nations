package org.leralix.tan.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.*;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.newhistory.TransactionHistory;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.StrongholdData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.territory.cosmetic.CustomIcon;
import org.leralix.tan.dataclass.territory.cosmetic.PlayerHeadIcon;
import org.leralix.tan.dataclass.territory.economy.Budget;
import org.leralix.tan.dataclass.territory.permission.RelationPermission;
import org.leralix.tan.dataclass.wars.CreateAttackData;
import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.dataclass.wars.WarRole;
import org.leralix.tan.dataclass.wars.wargoals.CaptureLandmarkWarGoal;
import org.leralix.tan.dataclass.wars.wargoals.ConquerWarGoal;
import org.leralix.tan.dataclass.wars.wargoals.LiberateWarGoal;
import org.leralix.tan.dataclass.wars.wargoals.SubjugateWarGoal;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.enums.*;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.enums.permissions.GeneralChunkSetting;
import org.leralix.tan.lang.DynamicLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.*;
import org.leralix.tan.newsletter.NewsletterScope;
import org.leralix.tan.newsletter.NewsletterStorage;
import org.leralix.tan.storage.MobChunkSpawnStorage;
import org.leralix.tan.storage.PlayerSelectPropertyPositionStorage;
import org.leralix.tan.storage.legacy.UpgradeStorage;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.utils.*;

import java.util.*;
import java.util.function.Consumer;

import static org.leralix.lib.data.SoundEnum.*;

public class PlayerGUI implements IGUI {

    private PlayerGUI() {
        throw new IllegalStateException("Utility class");
    }

    public static void openMainMenu(Player player){
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);

        Gui gui = IGUI.createChestGui(Lang.HEADER_MAIN_MENU.get(player),3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        boolean playerHaveTown = playerData.hasTown();
        boolean playerHaveRegion = playerData.hasRegion();

        TownData town = TownDataStorage.getInstance().get(playerData);
        RegionData region = null;
        if(playerHaveRegion){
            region = town.getRegion();
        }

        ItemStack kingdomIcon = HeadUtils.makeSkullB64(Lang.GUI_KINGDOM_ICON.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY5MTk2YjMzMGM2Yjg5NjJmMjNhZDU2MjdmYjZlY2NlNDcyZWFmNWM5ZDQ0Zjc5MWY2NzA5YzdkMGY0ZGVjZSJ9fX0=",
                Lang.GUI_KINGDOM_ICON_DESC1.get(playerData));
        ItemStack regionIcon = HeadUtils.makeSkullB64(Lang.GUI_REGION_ICON.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljMTgzMmU0ZWY1YzRhZDljNTE5ZDE5NGIxOTg1MDMwZDI1NzkxNDMzNGFhZjI3NDVjOWRmZDYxMWQ2ZDYxZCJ9fX0=");

        if(playerHaveRegion) {
            HeadUtils.addLore(regionIcon, Lang.GUI_REGION_ICON_DESC1_REGION.get(playerData, region.getColoredName()),
                    Lang.GUI_REGION_ICON_DESC2_REGION.get(playerData, region.getRank(player).getColoredName()));
        }
        else {
            HeadUtils.addLore(regionIcon, Lang.GUI_REGION_ICON_DESC1_NO_REGION.get(playerData));
        }

        ItemStack townIcon = HeadUtils.makeSkullB64(Lang.GUI_TOWN_ICON.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=");
        if(playerHaveTown) {
            HeadUtils.addLore(townIcon, Lang.GUI_TOWN_ICON_DESC1_HAVE_TOWN.get(playerData,
                    town.getColoredName()), Lang.GUI_TOWN_ICON_DESC2_HAVE_TOWN.get(playerData, town.getRank(player).getColoredName()));
        }
        else {
            HeadUtils.addLore(townIcon, Lang.GUI_TOWN_ICON_DESC1_NO_TOWN.get(playerData));
        }

        ItemStack profileIcon = HeadUtils.getPlayerHeadInformation(player);

        GuiItem kingdomGui = ItemBuilder.from(kingdomIcon).asGuiItem(event -> player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_WARNING_STILL_IN_DEV.get(playerData)));
        GuiItem regionGui = ItemBuilder.from(regionIcon).asGuiItem(event -> dispatchPlayerRegion(player));
        GuiItem townGui = ItemBuilder.from(townIcon).asGuiItem(event -> dispatchPlayerTown(player));
        GuiItem playerGui = ItemBuilder.from(profileIcon).asGuiItem(event -> openPlayerProfileMenu(player));


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
        gui.setItem(3,1, IGUI.createBackArrow(player, p -> player.closeInventory()));

        gui.open(player);
    }
    public static void dispatchPlayerRegion(Player player) {
        if(PlayerDataStorage.getInstance().get(player).hasRegion()) {
            openRegionMenu(player);
        }
        else {
            openNoRegionMenu(player);
        }
    }
    public static void dispatchPlayerTown(Player player){
        if(PlayerDataStorage.getInstance().get(player).hasTown()){
            openTownMenu(player);
        }
        else{
            openNoTownMenu(player);
        }
    }
    public static void openPlayerProfileMenu(Player player){
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);

        Gui gui = IGUI.createChestGui(Lang.HEADER_PLAYER_PROFILE.get(playerData),3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        LangType lang = playerData.getLang();

        ItemStack playerHead = HeadUtils.getPlayerHead(Lang.GUI_YOUR_PROFILE.get(),player);
        ItemStack treasuryIcon = HeadUtils.createCustomItemStack(Material.GOLD_NUGGET, Lang.GUI_YOUR_BALANCE.get(playerData),Lang.GUI_YOUR_BALANCE_DESC1.get(playerData, StringUtil.formatMoney(EconomyUtil.getBalance(player))));
        ItemStack propertiesIcon = HeadUtils.createCustomItemStack(Material.OAK_HANGING_SIGN, Lang.GUI_PLAYER_MANAGE_PROPERTIES.get(playerData),Lang.GUI_PLAYER_MANAGE_PROPERTIES_DESC1.get(playerData));
        ItemStack newsletterIcon = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK, Lang.GUI_PLAYER_NEWSLETTER.get(playerData),Lang.GUI_PLAYER_NEWSLETTER_DESC1.get(playerData));
        ItemStack languageIcon = HeadUtils.createCustomItemStack(lang.getIcon(), Lang.GUI_SELECTED_LANGUAGE_IS.get(playerData, lang.getName()),Lang.GUI_LEFT_CLICK_TO_INTERACT.get(playerData));

        GuiItem playerGui = ItemBuilder.from(playerHead).asGuiItem();

        GuiItem treasuryGui = ItemBuilder.from(treasuryIcon).asGuiItem();
        GuiItem propertiesGui = ItemBuilder.from(propertiesIcon).asGuiItem(event -> openPlayerPropertiesMenu(player, 0));
        GuiItem newsletterGui = ItemBuilder.from(newsletterIcon).asGuiItem(event -> openNewsletter(player,0, NewsletterScope.SHOW_ONLY_UNREAD));
        GuiItem languageGui = ItemBuilder.from(languageIcon).asGuiItem(event -> openLanguageMenu(player, 0));

        gui.setItem(1,5, playerGui);
        gui.setItem(2,2, treasuryGui);
        gui.setItem(2,4, propertiesGui);
        gui.setItem(2,6, newsletterGui);
        gui.setItem(2,8, languageGui);

        gui.setItem(18, IGUI.createBackArrow(player, p -> openMainMenu(player)));

        gui.open(player);
    }

    private static void openLanguageMenu(Player player, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_SELECT_LANGUAGE.get(player),3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        ItemStack helpTranslate = HeadUtils.makeSkullURL(Lang.HELP_US_TRANSLATE.get(playerData), "https://textures.minecraft.net/texture/b04831f7a7d8f624c9633996e3798edad49a5d9bcd18ecf75bfae66be48a0a6b",
                Lang.GUI_LEFT_CLICK_TO_INTERACT.get(playerData));

        GuiItem helpTranslateGui = ItemBuilder.from(helpTranslate).asGuiItem(event -> {
            TextComponent textComponent = new TextComponent(TanChatUtils.getTANString() + Lang.CLICK_HERE_TO_OPEN_BROWSER.get(playerData));
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://crowdin.com/project/town-and-nation"));
            player.spigot().sendMessage(textComponent);
            player.closeInventory();
        });

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for(LangType lang : LangType.values()){
            ItemStack langIcon = lang.getIcon();
            GuiItem langGui = ItemBuilder.from(langIcon).asGuiItem(event -> {
                playerData.setLang(lang);
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_LANGUAGE_CHANGED.get(playerData, lang.getName()));
                openPlayerProfileMenu(player);
            });
            guiItems.add(langGui);
        }

        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> openPlayerProfileMenu(player),
                p -> openLanguageMenu(player, page + 1),
                p -> openLanguageMenu(player, page - 1)
        );

        gui.setItem(3, 6, helpTranslateGui);
        gui.open(player);
    }

    public static void openNewsletter(Player player, int page, NewsletterScope scope){
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_NEWSLETTER.get(playerData),6);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        ArrayList<GuiItem> guiItems = new ArrayList<>(NewsletterStorage.getNewsletterForPlayer(player, scope, p -> openNewsletter(player, page, scope) ));

        GuiUtil.createIterator(gui, guiItems, 0, player,
                p -> openPlayerProfileMenu(player),
                p -> openNewsletter(player, page + 1,scope),
                p -> openNewsletter(player, page - 1,scope)
        );

        ItemStack changeScope = HeadUtils.createCustomItemStack(Material.NAME_TAG,scope.getName(playerData.getLang()),
                Lang.GUI_GENERIC_CLICK_TO_SWITCH_SCOPE.get(playerData));
        GuiItem checkScopeGui = ItemBuilder.from(changeScope).asGuiItem(event -> openNewsletter(player,0,scope.getNextScope()));

        ItemStack markAllAsRead = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK,
                Lang.MARK_ALL_AS_READ.get(playerData),
                Lang.LEFT_CLICK_TO_SELECT.get(playerData));

        GuiItem markAllAsReadButton = ItemBuilder.from(markAllAsRead).asGuiItem(event -> NewsletterStorage.markAllAsReadForPlayer(player, scope));

        gui.setItem(6,4,markAllAsReadButton);
        gui.setItem(6,5,checkScopeGui);
        gui.open(player);
    }
    public static void openPlayerPropertiesMenu(Player player, int page){
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);

        Gui gui = IGUI.createChestGui(Lang.HEADER_PLAYER_PROPERTIES.get(playerData),6);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        List<GuiItem> guiItems = new ArrayList<>();
        for (PropertyData propertyData : playerData.getProperties()){
            ItemStack property = propertyData.getIcon(playerData.getLang());
            GuiItem propertyGui = ItemBuilder.from(property).asGuiItem(event -> openPropertyManagerMenu(player, propertyData));
            guiItems.add(propertyGui);
        }

        ItemStack newProperty = HeadUtils.makeSkullB64(
                Lang.GUI_PLAYER_NEW_PROPERTY.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19"
        );

        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> openPlayerProfileMenu(player),
                p -> openPlayerPropertiesMenu(player, page + 1),
                p -> openPlayerPropertiesMenu(player, page - 1)
        );

        GuiItem newPropertyGui = ItemBuilder.from(newProperty).asGuiItem(event -> {

            TownData playerTown = playerData.getTown();
            if(!playerTown.doesPlayerHavePermission(playerData, RolePermission.CREATE_PROPERTY)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                SoundUtil.playSound(player, NOT_ALLOWED);
                return;
            }

            if(playerTown.getPropertyDataMap().size() >= playerTown.getLevel().getPropertyCap()){
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_PROPERTY_CAP_REACHED.get(playerData));
                return;
            }

            if(PlayerSelectPropertyPositionStorage.contains(playerData)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_ALREADY_IN_SCOPE.get(playerData));
                return;
            }
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_RIGHT_CLICK_2_POINTS_TO_CREATE_PROPERTY.get(playerData));
            player.sendMessage(TanChatUtils.getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(playerData, Lang.CANCEL_WORD.get(playerData)));
            PlayerSelectPropertyPositionStorage.addPlayer(playerData);
            player.closeInventory();
        });

        gui.setItem(6,3, newPropertyGui);

        gui.open(player);
    }
    public static void openPropertyManagerRentMenu(Player player, PropertyData propertyData) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_PLAYER_SPECIFIC_PROPERTY.get(playerData, propertyData.getName()), 4);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        ItemStack propertyIcon = propertyData.getIcon(playerData.getLang());

        ItemStack stopRentingProperty = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.GUI_PROPERTY_STOP_RENTING_PROPERTY.get(playerData),
                Lang.GUI_PROPERTY_STOP_RENTING_PROPERTY_DESC1.get(playerData));


        GuiItem propertyButton = ItemBuilder.from(propertyIcon).asGuiItem(event -> event.setCancelled(true));

        GuiItem stopRentingButton = ItemBuilder.from(stopRentingProperty).asGuiItem(event -> {
            propertyData.expelRenter(true);

            player.sendMessage(TanChatUtils.getTANString() + Lang.PROPERTY_RENTER_LEAVE_RENTER_SIDE.get(playerData, propertyData.getName()));
            SoundUtil.playSound(player,MINOR_GOOD);

            Player owner = propertyData.getOwnerPlayer();
            if(owner != null){
                owner.sendMessage(TanChatUtils.getTANString() + Lang.PROPERTY_RENTER_LEAVE_OWNER_SIDE.get(playerData, player.getName(), propertyData.getName()));
                SoundUtil.playSound(owner,MINOR_BAD);
            }

            player.closeInventory();
        });

        gui.setItem(1,5,propertyButton);
        gui.setItem(2,7,stopRentingButton);

        gui.setItem(4,1, IGUI.createBackArrow(player, p -> player.closeInventory()));


        gui.open(player);
    }
    public static void openPropertyManagerMenu(Player player, @NotNull PropertyData propertyData){
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_PLAYER_SPECIFIC_PROPERTY.get(playerData, propertyData.getName()),4);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        ItemStack changeName = HeadUtils.createCustomItemStack(
                Material.NAME_TAG,
                Lang.GUI_PROPERTY_CHANGE_NAME.get(playerData),
                Lang.GUI_PROPERTY_CHANGE_NAME_DESC1.get(playerData, propertyData.getName())
        );

        ItemStack changeDescription = HeadUtils.createCustomItemStack(
                Material.WRITABLE_BOOK,
                Lang.GUI_PROPERTY_CHANGE_DESCRIPTION.get(playerData),
                Lang.GUI_PROPERTY_CHANGE_DESCRIPTION_DESC1.get(playerData, propertyData.getDescription())
        );

        ItemStack isForSale;
        if(propertyData.isForSale()){
            isForSale = HeadUtils.makeSkullB64(Lang.SELL_PROPERTY.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2UyYTUzMGY0MjcyNmZhN2EzMWVmYWI4ZTQzZGFkZWUxODg5MzdjZjgyNGFmODhlYThlNGM5M2E0OWM1NzI5NCJ9fX0=");
        }
        else{
            isForSale = HeadUtils.makeSkullB64(Lang.SELL_PROPERTY.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVmMDcwOGZjZTVmZmFhNjYwOGNiZWQzZTc4ZWQ5NTgwM2Q4YTg5Mzc3ZDFkOTM4Y2UwYmRjNjFiNmRjOWY0ZiJ9fX0=");
        }
        HeadUtils.setLore(isForSale,
                propertyData.isForSale() ? Lang.GUI_PROPERTY_FOR_SALE.get(playerData): Lang.GUI_PROPERTY_NOT_FOR_SALE.get(playerData),
                Lang.GUI_BUYING_PRICE.get(playerData, propertyData.getSalePrice()),
                Lang.GUI_TOWN_RATE.get(playerData, String.format("%.2f", propertyData.getTerritory().getTaxOnBuyingProperty()*100)),
                Lang.GUI_LEFT_CLICK_TO_SWITCH_SALE.get(playerData),
                Lang.GUI_RIGHT_CLICK_TO_CHANGE_PRICE.get(playerData)
        );

        ItemStack isForRent;
        if(propertyData.isForRent()){
            isForRent = HeadUtils.makeSkullB64(Lang.RENT_PROPERTY.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2UyYTUzMGY0MjcyNmZhN2EzMWVmYWI4ZTQzZGFkZWUxODg5MzdjZjgyNGFmODhlYThlNGM5M2E0OWM1NzI5NCJ9fX0=");
        }
        else{
            isForRent = HeadUtils.makeSkullB64(Lang.RENT_PROPERTY.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVmMDcwOGZjZTVmZmFhNjYwOGNiZWQzZTc4ZWQ5NTgwM2Q4YTg5Mzc3ZDFkOTM4Y2UwYmRjNjFiNmRjOWY0ZiJ9fX0=");
        }
        HeadUtils.setLore(isForRent,
                propertyData.isForRent() ? Lang.GUI_PROPERTY_FOR_RENT.get(): Lang.GUI_PROPERTY_NOT_FOR_RENT.get(),
                Lang.GUI_RENTING_PRICE.get(playerData, propertyData.getRentPrice()),
                Lang.GUI_TOWN_RATE.get(playerData, String.format("%.2f", propertyData.getTerritory().getTaxOnRentingProperty()*100)),
                Lang.GUI_LEFT_CLICK_TO_SWITCH_SALE.get(playerData),
                Lang.GUI_RIGHT_CLICK_TO_CHANGE_PRICE.get(playerData)
        );

        ItemStack drawnBox = HeadUtils.makeSkullB64(Lang.GUI_PROPERTY_DRAWN_BOX.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzc3ZDRhMjA2ZDc3NTdmNDc5ZjMzMmVjMWEyYmJiZWU1N2NlZjk3NTY4ZGQ4OGRmODFmNDg2NGFlZTdkM2Q5OCJ9fX0=",
                Lang.GUI_PROPERTY_DRAWN_BOX_DESC1.get(playerData));

        ItemStack deleteProperty = HeadUtils.createCustomItemStack(Material.BARRIER,Lang.GUI_PROPERTY_DELETE_PROPERTY.get(playerData),
                Lang.GUI_PROPERTY_DELETE_PROPERTY_DESC1.get(playerData));

        ItemStack playerList = HeadUtils.createCustomItemStack(Material.PLAYER_HEAD,Lang.GUI_PROPERTY_PLAYER_LIST.get(playerData),
                Lang.GUI_PROPERTY_PLAYER_LIST_DESC1.get(playerData));

        GuiItem propertyIcon = ItemBuilder.from(propertyData.getIcon(playerData.getLang())).asGuiItem();

        GuiItem changeNameButton = ItemBuilder.from(changeName).asGuiItem(event -> {
            player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(playerData));
            PlayerChatListenerStorage.register(player, new ChangePropertyName(propertyData, p -> openPropertyManagerMenu(player, propertyData)));
        });
        GuiItem changeDescButton = ItemBuilder.from(changeDescription).asGuiItem(event -> {
            player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(playerData));
            PlayerChatListenerStorage.register(player, new ChangePropertyDescription(propertyData, p -> openPropertyManagerMenu(player, propertyData)));
        });


        GuiItem drawBoxButton = ItemBuilder.from(drawnBox).asGuiItem(event -> {
            player.closeInventory();
            propertyData.showBox(player);
        });

        GuiItem isForSaleButton = ItemBuilder.from(isForSale).asGuiItem(event -> {
            if(event.getClick() == ClickType.RIGHT){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(playerData));
                PlayerChatListenerStorage.register(player, new ChangePropertySalePrice(propertyData, p -> openPropertyManagerMenu(player, propertyData)));
                player.closeInventory();
            }
            else if (event.getClick() == ClickType.LEFT){
                if(propertyData.isRented()){
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PROPERTY_ALREADY_RENTED.get(playerData));
                    return;
                }
                propertyData.swapIsForSale();
                openPropertyManagerMenu(player,propertyData);
            }
        });
        GuiItem isForRentButton = ItemBuilder.from(isForRent).asGuiItem(event -> {
            if(event.getClick() == ClickType.RIGHT){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(playerData));
                PlayerChatListenerStorage.register(player, new ChangePropertyRentPrice(propertyData, p -> openPropertyManagerMenu(player, propertyData)));
            }
            else if (event.getClick() == ClickType.LEFT){
                if(propertyData.isRented()){
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PROPERTY_ALREADY_RENTED.get(playerData));
                    return;
                }
                propertyData.swapIsRent();
                openPropertyManagerMenu(player,propertyData);
            }
        });

        GuiItem deleteButton = ItemBuilder.from(deleteProperty).asGuiItem(event -> {
            propertyData.delete();
            openPlayerPropertiesMenu(player, 0);
        });

        GuiItem openListButton = ItemBuilder.from(playerList).asGuiItem(event -> openPlayerPropertyPlayerList(player, propertyData, 0));

        if(propertyData.isRented()){
            ItemStack renterIcon = HeadUtils.getPlayerHead(
                    Lang.GUI_PROPERTY_RENTED_BY.get(playerData, propertyData.getRenter().getNameStored()),
                    propertyData.getOfflineRenter(),
                    Lang.GUI_PROPERTY_RIGHT_CLICK_TO_EXPEL_RENTER.get(playerData));
            GuiItem renterButton = ItemBuilder.from(renterIcon).asGuiItem(event -> {
                event.setCancelled(true);

                Player renter = propertyData.getRenterPlayer();
                propertyData.expelRenter(false);

                player.sendMessage(TanChatUtils.getTANString() + Lang.PROPERTY_RENTER_EXPELLED_OWNER_SIDE.get(playerData));
                SoundUtil.playSound(player,MINOR_GOOD);

                if(renter != null){
                    renter.sendMessage(TanChatUtils.getTANString() + Lang.PROPERTY_RENTER_EXPELLED_RENTER_SIDE.get(playerData, propertyData.getName()));
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



        gui.setItem(4,1, IGUI.createBackArrow(player, p -> openPlayerPropertiesMenu(player, 0)));
        gui.open(player);
    }
    private static void openPlayerPropertyPlayerList(Player player, PropertyData propertyData, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        int nRows = 4;
        Gui gui = IGUI.createChestGui(Lang.HEADER_PLAYER_SPECIFIC_PROPERTY.get(playerData, propertyData.getName()),nRows);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        boolean canKick = propertyData.canPlayerManageInvites(playerData.getID());
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for(String playerID : propertyData.getAllowedPlayersID()){
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));

            ItemStack playerHead = HeadUtils.getPlayerHead(offlinePlayer,
                    canKick ? Lang.GUI_TOWN_MEMBER_DESC3.get(playerData) : "");

            GuiItem headGui = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                if(!canKick || event.getClick() != ClickType.RIGHT ){
                    return;
                }
                propertyData.removeAuthorizedPlayer(playerID);
                openPlayerPropertyPlayerList(player, propertyData, page);

                SoundUtil.playSound(player,MINOR_GOOD);
                player.sendMessage(Lang.PLAYER_REMOVED_FROM_PROPERTY.get(playerData, offlinePlayer.getName()));
            });
            guiItems.add(headGui);
        }
        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> openPropertyManagerMenu(player, propertyData),
                p -> openPlayerPropertyPlayerList(player, propertyData, page + 1),
                p -> openPlayerPropertyPlayerList(player, propertyData, page - 1)
                );

        ItemStack addPlayer = HeadUtils.makeSkullB64(Lang.GUI_PROPERTY_AUTHORIZE_PLAYER.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
        GuiItem addButton = ItemBuilder.from(addPlayer).asGuiItem(event -> {
            event.setCancelled(true);
            if(!propertyData.canPlayerManageInvites(playerData.getID())){
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                return;
            }
            openPlayerPropertyAddPlayer(player, propertyData, 0);
        });
        gui.setItem(nRows,4,addButton);

        gui.open(player);

    }
    private static void openPlayerPropertyAddPlayer(Player player, PropertyData propertyData, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_PLAYER_SPECIFIC_PROPERTY.get(playerData, propertyData.getName()),3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for(Player playerIter : Bukkit.getOnlinePlayers()){
            if(playerIter.getUniqueId().equals(player.getUniqueId()) || propertyData.isPlayerAuthorized(playerIter)){
                continue;
            }

            ItemStack playerHead = HeadUtils.getPlayerHead(playerIter);
            GuiItem headGui = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                propertyData.addAuthorizedPlayer(playerIter);
                openPlayerPropertyAddPlayer(player, propertyData, 0);
                SoundUtil.playSound(player,MINOR_GOOD);
                player.sendMessage(Lang.PLAYER_ADDED_TO_PROPERTY.get(playerData, playerIter.getName()));
            });
            guiItems.add(headGui);

        }

        GuiUtil.createIterator(gui, guiItems, 0, player,
                p -> openPlayerPropertyPlayerList(player, propertyData, page),
                p -> openPlayerPropertyAddPlayer(player, propertyData, page + 1),
                p -> openPlayerPropertyAddPlayer(player, propertyData, page - 1)
        );

        gui.open(player);
    }
    public static void openPropertyBuyMenu(Player player, @NotNull PropertyData propertyData) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_PLAYER_SPECIFIC_PROPERTY.get(playerData, propertyData.getName()),3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        ItemStack propertyIcon = propertyData.getIcon(playerData.getLang());


        if(propertyData.isForRent()){
            ItemStack confirmRent = HeadUtils.makeSkullB64(Lang.CONFIRM_RENT.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=",
                    Lang.CONFIRM_RENT_DESC1.get(playerData),
                    Lang.CONFIRM_RENT_DESC2.get(playerData, propertyData.getRentPrice()));
            ItemStack cancelRent = HeadUtils.makeSkullB64(Lang.CANCEL_RENT.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc1NDgzNjJhMjRjMGZhODQ1M2U0ZDkzZTY4YzU5NjlkZGJkZTU3YmY2NjY2YzAzMTljMWVkMWU4NGQ4OTA2NSJ9fX0=");


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
            ItemStack confirmRent = HeadUtils.makeSkullB64(Lang.CONFIRM_SALE.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=",
                    Lang.CONFIRM_SALE_DESC1.get(playerData),
                    Lang.CONFIRM_SALE_DESC2.get(playerData, propertyData.getSalePrice()));
            ItemStack cancelRent = HeadUtils.makeSkullB64(Lang.CANCEL_SALE.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc1NDgzNjJhMjRjMGZhODQ1M2U0ZDkzZTY4YzU5NjlkZGJkZTU3YmY2NjY2YzAzMTljMWVkMWU4NGQ4OTA2NSJ9fX0=");

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
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_NO_TOWN_MENU.get(playerData), 3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        int townPrice = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("townCost", 1000);

        ItemStack createTown = HeadUtils.createCustomItemStack(Material.GRASS_BLOCK,
                Lang.GUI_NO_TOWN_CREATE_NEW_TOWN.get(playerData),
                Lang.GUI_NO_TOWN_CREATE_NEW_TOWN_DESC1.get(playerData, townPrice));
        ItemStack browse = HeadUtils.createCustomItemStack(Material.ANVIL,
                Lang.GUI_NO_TOWN_JOIN_A_TOWN.get(playerData),
                Lang.GUI_NO_TOWN_JOIN_A_TOWN_DESC1.get(playerData, TownDataStorage.getInstance().getNumberOfTown()));

        GuiItem createButton = ItemBuilder.from(createTown).asGuiItem(event -> {
            event.setCancelled(true);

            if(!player.hasPermission("tan.base.town.create")){
                player.sendMessage(Lang.PLAYER_NO_PERMISSION.get(playerData));
                SoundUtil.playSound(player, NOT_ALLOWED);
                return;
            }

            double playerMoney = EconomyUtil.getBalance(player);
            if (playerMoney < townPrice) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(playerData, townPrice - playerMoney));
            }
            else {
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_WRITE_TOWN_NAME_IN_CHAT.get(playerData));
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
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_TOWN_LIST.get(playerData, page + 1),6);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        ArrayList<GuiItem> townItemStacks = new ArrayList<>();

        for(TownData specificTownData : TownDataStorage.getInstance().getTownMap().values()){
            ItemStack townIcon = specificTownData.getIconWithInformations(playerData.getLang());
            HeadUtils.addLore(townIcon,
                    "",
                    (specificTownData.isRecruiting()) ? Lang.GUI_TOWN_INFO_IS_RECRUITING.get(playerData) : Lang.GUI_TOWN_INFO_IS_NOT_RECRUITING.get(playerData),
                    (specificTownData.isPlayerAlreadyRequested(player)) ? Lang.GUI_TOWN_INFO_RIGHT_CLICK_TO_CANCEL.get(playerData) : Lang.GUI_TOWN_INFO_LEFT_CLICK_TO_JOIN.get(playerData)
            );
            GuiItem townButton = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);

                if(event.isLeftClick()){

                    if(!player.hasPermission("tan.base.town.join")){
                        player.sendMessage(Lang.PLAYER_NO_PERMISSION.get(playerData));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    if(specificTownData.isPlayerAlreadyRequested(player)){
                        return;
                    }
                    if(!specificTownData.isRecruiting()){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_TOWN_NOT_RECRUITING.get(playerData));
                        return;
                    }
                    specificTownData.addPlayerJoinRequest(player);
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_ASK_TO_JOIN_TOWN_PLAYER_SIDE.get(playerData, specificTownData.getName()));
                    openSearchTownMenu(player,page);
                }
                if(event.isRightClick()){
                    if(!specificTownData.isPlayerAlreadyRequested(player)){
                        return;
                    }
                    specificTownData.removePlayerJoinRequest(player);
                    NewsletterStorage.removePlayerJoinRequest(player, specificTownData);
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_REMOVE_ASK_TO_JOIN_TOWN_PLAYER_SIDE.get(playerData));
                    openSearchTownMenu(player,page);
                }

            });
            townItemStacks.add(townButton);
        }

        GuiUtil.createIterator(gui, townItemStacks, page, player, p -> openNoTownMenu(player),
                p -> openSearchTownMenu(player, page + 1),
                p -> openSearchTownMenu(player, page - 1));


        gui.open(player);
    }
    public static void openTownMenu(Player player) {
        int nRows = 4;
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        TownData townData = TownDataStorage.getInstance().get(playerData);

        Gui gui = IGUI.createChestGui(Lang.HEADER_TOWN_MENU.get(playerData, townData.getName()),nRows);
        gui.setDefaultClickAction(event -> {
            if(!(event.getSlotType() == InventoryType.SlotType.CONTAINER || event.getSlotType() == InventoryType.SlotType.QUICKBAR)){
                event.setCancelled(true);
            }
        });

        ItemStack townIcon = townData.getIconWithInformations(playerData.getLang());
        HeadUtils.addLore(townIcon,
                Lang.GUI_TOWN_INFO_CHANGE_ICON.get(playerData),
                Lang.RIGHT_CLICK_TO_SELECT_MEMBER_HEAD.get(playerData)
        );

        ItemStack treasury = HeadUtils.makeSkullB64(Lang.GUI_TOWN_TREASURY_ICON.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=",
                Lang.GUI_TOWN_TREASURY_ICON_DESC1.get(playerData));

        ItemStack memberIcon = HeadUtils.makeSkullB64(Lang.GUI_TOWN_MEMBERS_ICON.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Q0ZDQ5NmIxZGEwNzUzNmM5NGMxMzEyNGE1ODMzZWJlMGM1MzgyYzhhMzM2YWFkODQ2YzY4MWEyOGQ5MzU2MyJ9fX0=",
                Lang.GUI_TOWN_MEMBERS_ICON_DESC1.get(playerData));

        ItemStack claims = HeadUtils.makeSkullB64(Lang.GUI_CLAIM_ICON.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=",
                Lang.GUI_CLAIM_ICON_DESC1.get(playerData));

        ItemStack otherTownIcon = HeadUtils.makeSkullB64(Lang.GUI_BROWSE_TERRITORY_ICON.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMzc0ZTIxYjgxYzBiMjFhYmViOGU5N2UxM2UwNzdkM2VkMWVkNDRmMmU5NTZjNjhmNjNhM2UxOWU4OTlmNiJ9fX0=",
                Lang.GUI_BROWSE_TERRITORY_ICON_DESC1.get(playerData));

        ItemStack diplomacy = HeadUtils.makeSkullB64(Lang.GUI_RELATION_ICON.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUwN2Q2ZGU2MzE4MzhlN2E3NTcyMGU1YjM4ZWYxNGQyOTY2ZmRkODQ4NmU3NWQxZjY4MTJlZDk5YmJjYTQ5OSJ9fX0=",
                Lang.GUI_RELATION_ICON_DESC1.get(playerData));

        ItemStack level = HeadUtils.makeSkullB64(Lang.GUI_TOWN_LEVEL_ICON.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmJlNTI5YWI2YjJlYTdjNTBkOTE5MmQ4OWY4OThmZDdkYThhOWU3NTBkMzc4Mjk1ZGY3MzIwNWU3YTdlZWFlMCJ9fX0=",
                Lang.GUI_TOWN_LEVEL_ICON_DESC1.get(playerData));

        ItemStack settings = HeadUtils.makeSkullB64(Lang.GUI_TOWN_SETTINGS_ICON.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVkMmNiMzg0NThkYTE3ZmI2Y2RhY2Y3ODcxNjE2MDJhMjQ5M2NiZjkzMjMzNjM2MjUzY2ZmMDdjZDg4YTljMCJ9fX0=",
                Lang.GUI_TOWN_SETTINGS_ICON_DESC1.get(playerData));

        ItemStack propertyIcon = HeadUtils.createCustomItemStack(Material.OAK_HANGING_SIGN, Lang.GUI_TOWN_PROPERTIES_ICON.get(playerData),Lang.GUI_TOWN_PROPERTIES_ICON_DESC1.get(playerData));

        ItemStack landmark = HeadUtils.makeSkullB64(Lang.ADMIN_GUI_LANDMARK_ICON.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQ3NjFjYzE2NTYyYzg4ZDJmYmU0MGFkMzg1MDJiYzNiNGE4Nzg1OTg4N2RiYzM1ZjI3MmUzMGQ4MDcwZWVlYyJ9fX0=",
                Lang.ADMIN_GUI_LANDMARK_DESC1.get(playerData));

        ItemStack war = HeadUtils.makeSkullB64(Lang.GUI_ATTACK_ICON.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjVkZTRmZjhiZTcwZWVlNGQxMDNiMWVlZGY0NTRmMGFiYjlmMDU2OGY1ZjMyNmVjYmE3Y2FiNmE0N2Y5YWRlNCJ9fX0=",
                Lang.GUI_ATTACK_ICON_DESC1.get(playerData));

        ItemStack hierarchy = HeadUtils.makeSkullB64(Lang.GUI_HIERARCHY_MENU.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=",
                Lang.GUI_HIERARCHY_MENU_DESC1.get(playerData));

        GuiItem townIconButton = ItemBuilder.from(townIcon).asGuiItem(event -> {
            event.setCancelled(true);
            if(!townData.doesPlayerHavePermission(playerData, RolePermission.TOWN_ADMINISTRATOR)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                SoundUtil.playSound(player,NOT_ALLOWED);
                return;
            }
            if(event.getCursor() == null){
                return;
            }
            if(event.getCursor().getType() == Material.AIR){
                return;
            }
            ItemStack itemMaterial = event.getCursor();
            if(itemMaterial.getType() == Material.AIR && event.isRightClick()){
                openSelectHeadTerritoryMenu(player, townData, 0);
            }
            else {
                townData.setIcon(new CustomIcon(itemMaterial));
                openTownMenu(player);
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_CHANGED_ICON_SUCCESS.get(playerData));
            }
        });
        GuiItem treasuryButton = ItemBuilder.from(treasury).asGuiItem(event -> {
            event.setCancelled(true);
            openTreasury(player, townData);
        });
        GuiItem membersButton = ItemBuilder.from(memberIcon).asGuiItem(event -> {
            event.setCancelled(true);
            openMemberList(player, townData);
        });
        GuiItem claimButton = ItemBuilder.from(claims).asGuiItem(event -> {
            event.setCancelled(true);
            openChunkSettings(player, townData);
        });
        GuiItem browseMenu = ItemBuilder.from(otherTownIcon).asGuiItem(event -> {
            event.setCancelled(true);
            browseTerritory(player, townData, BrowseScope.TOWNS, p -> openTownMenu(player), 0);
        });
        GuiItem relationButton = ItemBuilder.from(diplomacy).asGuiItem(event -> {
            event.setCancelled(true);
            openRelations(player, townData);
        });
        GuiItem levelButton = ItemBuilder.from(level).asGuiItem(event -> {
            event.setCancelled(true);
            openTownLevel(player,0);
        });
        GuiItem settingsButton = ItemBuilder.from(settings).asGuiItem(event -> {
            event.setCancelled(true);
            openTownSettings(player, townData);
        });
        GuiItem propertyButton = ItemBuilder.from(propertyIcon).asGuiItem(event -> {
            event.setCancelled(true);
            openTownPropertiesMenu(player,0);
        });
        GuiItem landmarksButton = ItemBuilder.from(landmark).asGuiItem(event -> {
            event.setCancelled(true);
            openOwnedLandmark(player, townData,0);
        });
        GuiItem warButton = ItemBuilder.from(war).asGuiItem(event -> {
            event.setCancelled(true);
            openWarMenu(player, townData, 0);
        });
        GuiItem hierarchyButton = ItemBuilder.from(hierarchy).asGuiItem(event -> {
            event.setCancelled(true);
            openHierarchyMenu(player, townData);
        });


        gui.setItem(1,5, townIconButton);
        gui.setItem(2,2, treasuryButton);
        gui.setItem(2,3, membersButton);
        gui.setItem(2,4, claimButton);
        gui.setItem(2,5, browseMenu);
        gui.setItem(2,6, relationButton);
        gui.setItem(2,7, levelButton);
        gui.setItem(2,8, settingsButton);
        gui.setItem(3,2, propertyButton);
        gui.setItem(3,3, landmarksButton);
        gui.setItem(3,4, warButton);
        gui.setItem(3,5, hierarchyButton);

        gui.setItem(nRows,1, IGUI.createBackArrow(player, p -> openMainMenu(player)));

        gui.open(player);
    }

    private static void openSelectHeadTerritoryMenu(Player player, TerritoryData territoryData, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_SELECT_ICON.get(playerData) + (page + 1),6);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for(String playerID : territoryData.getPlayerIDList()){
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));
            ItemStack playerHead = HeadUtils.getPlayerHead(offlinePlayer);
            GuiItem headGui = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                territoryData.setIcon(new PlayerHeadIcon(offlinePlayer.getUniqueId().toString()));
                openTownMenu(player);
                SoundUtil.playSound(player, MINOR_GOOD);
            });
            guiItems.add(headGui);
        }
        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> territoryData.openMainMenu(player),
                p -> openSelectHeadTerritoryMenu(player, territoryData, page + 1),
                p -> openSelectHeadTerritoryMenu(player, territoryData, page - 1));

        gui.open(player);
    }

    private static void openWarMenu(Player player, TerritoryData territory, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_WARS_MENU.get(playerData, page + 1),6);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for(PlannedAttack plannedAttack : PlannedAttackStorage.getWars()){
            ItemStack attackIcon = plannedAttack.getIcon(territory);
            GuiItem attackButton = ItemBuilder.from(attackIcon).asGuiItem(event -> {
                event.setCancelled(true);
                if(event.isLeftClick()){
                    openSpecificPlannedAttackMenu(player, territory, plannedAttack, 0);
                }
            });
            guiItems.add(attackButton);
        }
        StrongholdData territoryStronghold = territory.getStronghold();
        ItemStack strongholdItem;
        if(territoryStronghold == null){
            strongholdItem = HeadUtils.createCustomItemStack(Material.IRON_BLOCK,Lang.GUI_STRONGHOLD.get(playerData), Lang.GUI_NO_STRONGHOLD.get(playerData));
        }
        else {
            int x = territoryStronghold.getClaimedChunk().getX() * 16 + 8;
            int z = territoryStronghold.getClaimedChunk().getZ() * 16 + 8;
            strongholdItem = HeadUtils.createCustomItemStack(Material.IRON_BLOCK, Lang.GUI_STRONGHOLD.get(playerData), Lang.GUI_STRONGHOLD_LOCATION.get(playerData, x,z), Lang.GUI_LEFT_CLICK_TO_SET_STRONGHOLD.get(playerData));
        }

        GuiItem strongHoldIcon = ItemBuilder.from(strongholdItem).asGuiItem(event -> {
            event.setCancelled(true);
            if(territoryStronghold == null){
                return;
            }
            if(event.isLeftClick()){
               if(territory.doesPlayerHavePermission(player, RolePermission.TOWN_ADMINISTRATOR)){
                   Chunk playerChunk = player.getLocation().getChunk();
                   ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(playerChunk);
                   if(!claimedChunk.getOwnerID().equals(territory.getID())){
                       player.sendMessage(Lang.CHUNK_DO_NOT_BELONG_TO_TERRITORY.get(playerData));
                       SoundUtil.playSound(player,NOT_ALLOWED);
                       return;
                   }
                    territory.setStrongholdPosition(playerChunk);
                    player.sendMessage("new Stronghold is at x : " + playerChunk.getX() *16 + " z : " + playerChunk.getZ() * 16 );
               }
               else{
                   player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                    SoundUtil.playSound(player,NOT_ALLOWED);
               }
            }
        });

        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> openSingleRelation(player, territory, TownRelation.WAR, 0),
                p -> openWarMenu(player, territory, page + 1),
                p -> openWarMenu(player, territory, page - 1));

        gui.setItem(6, 5, strongHoldIcon);

        gui.open(player);
    }

    private static void openSpecificPlannedAttackMenu(Player player, TerritoryData territory, PlannedAttack plannedAttack, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_WAR_MANAGER.get(playerData), 3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        GuiItem attackIcon = ItemBuilder.from(plannedAttack.getIcon(territory)).asGuiItem();
        gui.setItem(1,5, attackIcon);

        ItemStack attackingSideInfo = plannedAttack.getAttackingIcon();
        GuiItem attackingSidePanel = ItemBuilder.from(attackingSideInfo).asGuiItem();
        gui.setItem(2,2, attackingSidePanel);

        ItemStack defendingSideInfo = plannedAttack.getDefendingIcon();
        GuiItem defendingSidePanel = ItemBuilder.from(defendingSideInfo).asGuiItem();
        gui.setItem(2,4, defendingSidePanel);



        WarRole territoryRole = plannedAttack.getTerritoryRole(territory);

        if(territoryRole == WarRole.MAIN_ATTACKER){
            ItemStack cancelAttack = HeadUtils.createCustomItemStack(Material.BARRIER, Lang.GUI_CANCEL_ATTACK.get(playerData), Lang.GUI_GENERIC_CLICK_TO_DELETE.get(playerData));
            ItemStack renameAttack = HeadUtils.createCustomItemStack(Material.NAME_TAG, Lang.GUI_RENAME_ATTACK.get(playerData), Lang.GUI_GENERIC_CLICK_TO_RENAME.get(playerData));
            GuiItem cancelButton = ItemBuilder.from(cancelAttack).asGuiItem(event -> {
                plannedAttack.remove();
                territory.broadcastMessageWithSound(Lang.ATTACK_SUCCESSFULLY_CANCELLED.get(playerData, plannedAttack.getMainDefender().getName()),MINOR_GOOD);
                openWarMenu(player, territory, page);
            });

            GuiItem renameButton = ItemBuilder.from(renameAttack).asGuiItem(event -> {
                event.setCancelled(true);
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(playerData));
                PlayerChatListenerStorage.register(player, new ChangeAttackName(plannedAttack, p -> openSpecificPlannedAttackMenu(player, territory, plannedAttack, page)));
            });

            gui.setItem(2,6, renameButton);
            gui.setItem(2,8, cancelButton);
        }

        else if(territoryRole == WarRole.MAIN_DEFENDER){
            ItemStack submitToRequests = HeadUtils.createCustomItemStack(Material.SOUL_LANTERN,
                    Lang.SUBMIT_TO_REQUESTS.get(playerData),
                    Lang.SUBMIT_TO_REQUEST_DESC1.get(playerData),
                    Lang.SUBMIT_TO_REQUEST_DESC2.get(playerData, plannedAttack.getWarGoal().getCurrentDesc()));

            GuiItem submitToRequestButton = ItemBuilder.from(submitToRequests).asGuiItem(event -> {
                plannedAttack.defenderSurrendered();
                openWarMenu(player, territory, page);
            });
            gui.setItem(2,7,submitToRequestButton);

        }

        else if(territoryRole == WarRole.OTHER_ATTACKER || territoryRole == WarRole.OTHER_DEFENDER){
            ItemStack quitWar = HeadUtils.createCustomItemStack(Material.DARK_OAK_DOOR, Lang.GUI_QUIT_WAR.get(playerData), Lang.GUI_QUIT_WAR_DESC1.get(playerData));

            GuiItem quitButton = ItemBuilder.from(quitWar).asGuiItem(event -> {
                plannedAttack.removeBelligerent(territory);
                territory.broadcastMessageWithSound(Lang.TERRITORY_NO_LONGER_INVOLVED_IN_WAR_MESSAGE.get(playerData, plannedAttack.getMainDefender().getName()),MINOR_GOOD);
                openWarMenu(player, territory, page);
            });
            gui.setItem(2,7, quitButton);
        }

        else if(territoryRole == WarRole.NEUTRAL){
            ItemStack joinAttacker = HeadUtils.createCustomItemStack(Material.IRON_SWORD,
                    Lang.GUI_JOIN_ATTACKING_SIDE.get(playerData),
                    Lang.GUI_JOIN_ATTACKING_SIDE_DESC1.get(playerData, territory.getColoredName()),
                    Lang.GUI_WAR_GOAL_INFO.get(playerData, plannedAttack.getWarGoal().getDisplayName()));
            ItemStack joinDefender = HeadUtils.createCustomItemStack(Material.SHIELD,
                    Lang.GUI_JOIN_DEFENDING_SIDE.get(playerData),
                    Lang.GUI_JOIN_DEFENDING_SIDE_DESC1.get(playerData, territory.getColoredName()));

            GuiItem joinAttackerButton = ItemBuilder.from(joinAttacker).asGuiItem(event -> {
                plannedAttack.addAttacker(territory);
                openSpecificPlannedAttackMenu(player, territory, plannedAttack, page);
            });

            GuiItem joinDefenderButton = ItemBuilder.from(joinDefender).asGuiItem(event -> {
                plannedAttack.addDefender(territory);
                openSpecificPlannedAttackMenu(player, territory, plannedAttack, page);
            });
            gui.setItem(2,6, joinAttackerButton);
            gui.setItem(2,8, joinDefenderButton);
        }

        gui.setItem(3,1, IGUI.createBackArrow(player, p -> openWarMenu(player, territory, page)));
        gui.open(player);

    }

    public static void openStartWarSettings(Player player, CreateAttackData createAttackData) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_CREATE_WAR_MANAGER.get(playerData, createAttackData.getMainDefender().getName()),3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        TerritoryData mainAttacker = createAttackData.getMainAttacker();
        TerritoryData mainDefender = createAttackData.getMainDefender();

        ItemStack addTime = HeadUtils.makeSkullB64(Lang.GUI_ATTACK_ADD_TIME.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjMyZmZmMTYzZTIzNTYzMmY0MDQ3ZjQ4NDE1OTJkNDZmODVjYmJmZGU4OWZjM2RmNjg3NzFiZmY2OWE2NjIifX19",
                Lang.GUI_LEFT_CLICK_FOR_1_MINUTE.get(playerData),
                Lang.GUI_SHIFT_CLICK_FOR_1_HOUR.get(playerData));
        ItemStack removeTIme = HeadUtils.makeSkullB64(Lang.GUI_ATTACK_REMOVE_TIME.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE1NmRhYjUzZDRlYTFhNzlhOGU1ZWQ2MzIyYzJkNTZjYjcxNGRkMzVlZGY0Nzg3NjNhZDFhODRhODMxMCJ9fX0=",
                Lang.GUI_LEFT_CLICK_FOR_1_MINUTE.get(playerData),
                Lang.GUI_SHIFT_CLICK_FOR_1_HOUR.get(playerData));
        ItemStack time = HeadUtils.makeSkullB64(Lang.GUI_ATTACK_SET_TO_START_IN.get(playerData, DateUtil.getStringDeltaDateTime(createAttackData.getDeltaDateTime())),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWU5OThmM2ExNjFhNmM5ODlhNWQwYTFkYzk2OTMxYTM5OTI0OWMwODBiNjYzNjQ1ODFhYjk0NzBkZWE1ZTcyMCJ9fX0=",
                Lang.GUI_LEFT_CLICK_FOR_1_MINUTE.get(playerData),
                Lang.GUI_SHIFT_CLICK_FOR_1_HOUR.get(playerData));
        ItemStack confirm = HeadUtils.makeSkullB64(Lang.GUI_CONFIRM_ATTACK.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNhNDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIwZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=",
                Lang.GUI_CONFIRM_ATTACK_DESC1.get(playerData, mainDefender.getColoredName()));

        if(!createAttackData.getWargoal().isCompleted()){
            HeadUtils.addLore(confirm, Lang.GUI_WARGOAL_NOT_COMPLETED.get(playerData));
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
            openStartWarSettings(player, createAttackData);
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
            openStartWarSettings(player, createAttackData);
        });

        GuiItem timeInfo = ItemBuilder.from(time).asGuiItem(event -> event.setCancelled(true));

        GuiItem wargoalButton = ItemBuilder.from(wargoal).asGuiItem(event -> {
            openSelectWarGoalMenu(player, createAttackData);
            event.setCancelled(true);
        });

        GuiItem confirmButton = ItemBuilder.from(confirm).asGuiItem(event -> {
            event.setCancelled(true);

            if(!createAttackData.getWargoal().isCompleted()){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_WARGOAL_NOT_COMPLETED.get(playerData));
                return;
            }

            PlannedAttackStorage.newWar(createAttackData);
            openWarMenu(player, mainAttacker, 0);

            player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_ATTACK_TOWN_EXECUTED.get(playerData, mainDefender.getName()));
            mainAttacker.broadcastMessageWithSound(Lang.GUI_TOWN_ATTACK_TOWN_INFO.get(playerData, mainAttacker.getName(), mainDefender.getName()), WAR);
            mainDefender.broadcastMessageWithSound(Lang.GUI_TOWN_ATTACK_TOWN_INFO.get(playerData, mainAttacker.getName(), mainDefender.getName()), WAR);
        });


        gui.setItem(2,2,removeTimeButton);
        gui.setItem(2,3,timeInfo);
        gui.setItem(2,4,addTimeButton);

        gui.setItem(2,6,wargoalButton);

        gui.setItem(2,8,confirmButton);
        gui.setItem(3,1, IGUI.createBackArrow(player, e -> openSingleRelation(player, mainAttacker, TownRelation.WAR,0)));

        createAttackData.getWargoal().addExtraOptions(gui, player, createAttackData);

        gui.open(player);

    }

    public static void openSelecteTerritoryToLiberate(Player player, CreateAttackData createAttackData, LiberateWarGoal liberateWarGoal) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_CREATE_WAR_MANAGER.get(playerData, createAttackData.getMainDefender().getName()),6);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        TerritoryData territoryToAttack = createAttackData.getMainDefender();
        for(TerritoryData territoryData : territoryToAttack.getVassals()){
            if(territoryData.isCapital()){
                continue;
            }
            ItemStack territoryIcon = territoryData.getIconWithInformations(playerData.getLang());
            HeadUtils.addLore(territoryIcon, "", Lang.LEFT_CLICK_TO_SELECT.get(playerData));

            GuiItem territoryButton = ItemBuilder.from(territoryIcon).asGuiItem(event -> {
                event.setCancelled(true);
                liberateWarGoal.setTerritoryToLiberate(territoryData);
                openStartWarSettings(player, createAttackData);
            });

            gui.addItem(territoryButton);
        }

        gui.setItem(6,1, IGUI.createBackArrow(player, e -> openStartWarSettings(player, createAttackData)));
        gui.open(player);
    }

    public static void openSelecteLandmarkToCapture(Player player, CreateAttackData createAttackData, CaptureLandmarkWarGoal captureLandmarkWarGoal, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_CREATE_WAR_MANAGER.get(playerData, createAttackData.getMainDefender().getName()),6);

        TownData defendingTerritory = (TownData) createAttackData.getMainDefender();

         List<GuiItem> landmarkButtons = new ArrayList<>();
         for(Landmark ownedLandmark : defendingTerritory.getOwnedLandmarks()){
            ItemStack landmarkIcon = ownedLandmark.getIcon();
            HeadUtils.addLore(landmarkIcon, "", Lang.LEFT_CLICK_TO_SELECT.get(playerData));

            GuiItem landmarkButton = ItemBuilder.from(landmarkIcon).asGuiItem(event -> {
                event.setCancelled(true);
                captureLandmarkWarGoal.setLandmarkToCapture(ownedLandmark);
                openStartWarSettings(player, createAttackData);
            });
            landmarkButtons.add(landmarkButton);
         }

         GuiUtil.createIterator(gui, landmarkButtons, page, player,
                 p -> openStartWarSettings(player, createAttackData),
                 p -> openSelecteLandmarkToCapture(player, createAttackData, captureLandmarkWarGoal, page + 1),
                 p -> openSelecteLandmarkToCapture(player, createAttackData, captureLandmarkWarGoal, page - 1));

         gui.open(player);
    }

    private static void openSelectWarGoalMenu(Player player, CreateAttackData createAttackData) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_SELECT_WARGOAL.get(playerData), 3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        boolean canBeSubjugated = createAttackData.canBeSubjugated();
        boolean canBeLiberated = !(createAttackData.getMainDefender() instanceof TownData);
        boolean canCaptureLandmark = createAttackData.getMainAttacker() instanceof TownData && createAttackData.getMainDefender() instanceof TownData;

        ItemStack conquer = HeadUtils.createCustomItemStack(Material.IRON_SWORD, Lang.CONQUER_WAR_GOAL.get(playerData),
                Lang.CONQUER_WAR_GOAL_DESC.get(playerData),
                Lang.LEFT_CLICK_TO_SELECT.get(playerData));

        ItemStack captureLandmark = HeadUtils.createCustomItemStack(Material.DIAMOND,
                Lang.CAPTURE_LANDMARK_WAR_GOAL.get(playerData),
                Lang.CAPTURE_LANDMARK_WAR_GOAL_DESC.get(playerData));

        ItemStack subjugate = HeadUtils.createCustomItemStack(Material.CHAIN,
                Lang.SUBJUGATE_WAR_GOAL.get(playerData),
                Lang.GUI_WARGOAL_SUBJUGATE_WAR_GOAL_RESULT.get(playerData, createAttackData.getMainDefender().getName(), createAttackData.getMainAttacker().getName()));

        if(!canBeSubjugated)
            HeadUtils.addLore(subjugate, Lang.GUI_WARGOAL_SUBJUGATE_CANNOT_BE_USED.get(playerData));
        else
            HeadUtils.addLore(subjugate, Lang.LEFT_CLICK_TO_SELECT.get(playerData));

        ItemStack liberate = HeadUtils.createCustomItemStack(Material.LANTERN, Lang.LIBERATE_SUBJECT_WAR_GOAL.get(playerData),
                Lang.LIBERATE_SUBJECT_WAR_GOAL_DESC.get(playerData));

        if(!canBeLiberated)
            HeadUtils.addLore(liberate, Lang.GUI_WARGOAL_LIBERATE_CANNOT_BE_USED.get(playerData));
        else
            HeadUtils.addLore(liberate, Lang.LEFT_CLICK_TO_SELECT.get(playerData));


        GuiItem conquerButton = ItemBuilder.from(conquer).asGuiItem(event -> {
            event.setCancelled(true);
            createAttackData.setWarGoal(new ConquerWarGoal(createAttackData.getMainAttacker(), createAttackData.getMainDefender()));
            openStartWarSettings(player, createAttackData);
        });

        GuiItem captureLandmarkButton = ItemBuilder.from(captureLandmark).asGuiItem(event -> {
            event.setCancelled(true);
            if(!canCaptureLandmark){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_WARGOAL_CAPTURE_LANDMARK_CANNOT_BE_USED.get(playerData));
                return;
            }
            createAttackData.setWarGoal(new CaptureLandmarkWarGoal(createAttackData.getMainAttacker().getID(), createAttackData.getMainDefender().getID()));
            openStartWarSettings(player, createAttackData);
        });

        GuiItem subjugateButton = ItemBuilder.from(subjugate).asGuiItem(event -> {
            event.setCancelled(true);
            if(!canBeSubjugated){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_WARGOAL_SUBJUGATE_CANNOT_BE_USED.get(playerData));
                return;
            }
            createAttackData.setWarGoal(new SubjugateWarGoal(createAttackData));
            openStartWarSettings(player, createAttackData);
        });

        GuiItem liberateButton = ItemBuilder.from(liberate).asGuiItem(event -> {
            event.setCancelled(true);

            if(!canBeLiberated){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_WARGOAL_LIBERATE_CANNOT_BE_USED.get(playerData));
                return;
            }
            createAttackData.setWarGoal(new LiberateWarGoal());
            openStartWarSettings(player, createAttackData);
        });

        gui.setItem(2,2,conquerButton);
        gui.setItem(2,4,captureLandmarkButton);
        gui.setItem(2,6,subjugateButton);
        gui.setItem(2,8,liberateButton);

        gui.setItem(3,1, IGUI.createBackArrow(player, e -> openStartWarSettings(player, createAttackData)));

        gui.open(player);
    }

    private static void openOwnedLandmark(Player player, TownData townData, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_TOWN_OWNED_LANDMARK.get(playerData, page + 1),6);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        ArrayList<GuiItem> landmarkGui = new ArrayList<>();

        for(String landmarkID : townData.getOwnedLandmarksID()){
            Landmark landmarkData = LandmarkStorage.getInstance().get(landmarkID);

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
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_TERRITORY_LIST.get(playerData, page + 1),6);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        List<TerritoryData> territoryList = new ArrayList<>();

        if(scope == BrowseScope.ALL || scope == BrowseScope.TOWNS)
            territoryList.addAll(TownDataStorage.getInstance().getAll());
        if(scope == BrowseScope.ALL || scope == BrowseScope.REGIONS)
            territoryList.addAll(RegionDataStorage.getInstance().getAll());

        ArrayList<GuiItem> townGuiItems = new ArrayList<>();

        for(TerritoryData specificTerritoryData : territoryList){
            ItemStack territoryIcon = specificTerritoryData.getIconWithInformationAndRelation(territoryData, playerData.getLang());
            GuiItem territoryGUI = ItemBuilder.from(territoryIcon).asGuiItem(event -> event.setCancelled(true));

            townGuiItems.add(territoryGUI);
        }

        GuiUtil.createIterator(gui, townGuiItems, page, player, exitMenu,
                p -> browseTerritory(player, territoryData, scope ,exitMenu, page + 1),
                p -> browseTerritory(player, territoryData, scope ,exitMenu, page - 1));


        ItemStack checkScope = HeadUtils.createCustomItemStack(Material.NAME_TAG,scope.getName(),
                Lang.GUI_GENERIC_CLICK_TO_SWITCH_SCOPE.get(playerData));
        GuiItem checkScopeButton = new GuiItem(checkScope, event -> browseTerritory(player, territoryData, scope.getNextScope(), exitMenu, 0));

        gui.setItem(6,5,checkScopeButton);
        gui.open(player);
    }


    public static void openMemberList(Player player, TerritoryData territoryData) {
        openMemberList(player, territoryData, 0);
    }

    public static void openMemberList(Player player, TerritoryData territoryData, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_TOWN_MEMBERS.get(playerData),6);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        List<GuiItem> guiItems = territoryData.getOrderedMemberList(playerData);

        ItemStack manageRanks = HeadUtils.createCustomItemStack(Material.LADDER, Lang.GUI_TOWN_MEMBERS_MANAGE_ROLES.get(playerData));

        GuiItem manageRanksButton = ItemBuilder.from(manageRanks).asGuiItem(event -> {
            event.setCancelled(true);
            openTerritoryRanks(player, territoryData);
        });

        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> territoryData.openMainMenu(player),
                p -> openMemberList(player, territoryData, page + 1),
                p -> openMemberList(player, territoryData, page - 1),
                Material.LIME_STAINED_GLASS_PANE);

        gui.setItem(6,3, manageRanksButton);


        if(territoryData instanceof TownData townData){
            ItemStack manageApplication = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK,
                    Lang.GUI_TOWN_MEMBERS_MANAGE_APPLICATION.get(playerData),
                    Lang.GUI_TOWN_MEMBERS_MANAGE_APPLICATION_DESC1.get(playerData, townData.getPlayerJoinRequestSet().size())
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
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_TOWN_APPLICATIONS.get(playerData),6);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        PlayerData playerStat = PlayerDataStorage.getInstance().get(player);
        List<GuiItem> guiItems = new ArrayList<>();
        for (String playerUUID: townData.getPlayerJoinRequestSet()) {

            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            PlayerData playerIterateData = PlayerDataStorage.getInstance().get(playerUUID);

            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate,
                    Lang.GUI_PLAYER_ASK_JOIN_PROFILE_DESC2.get(playerData),
                    Lang.GUI_PLAYER_ASK_JOIN_PROFILE_DESC3.get(playerData));

            GuiItem playerButton = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                if(event.isLeftClick()){
                    if(!townData.doesPlayerHavePermission(playerStat, RolePermission.INVITE_PLAYER)){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    if(townData.isFull()){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.INVITATION_TOWN_FULL.get(playerData));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    townData.addPlayer(playerIterateData);
                    townData.broadcastMessageWithSound(Lang.TOWN_INVITATION_ACCEPTED_TOWN_SIDE.get(playerData.getNameStored()), SoundEnum.MINOR_GOOD);
                }
                else if(event.isRightClick()){
                    if(!townData.doesPlayerHavePermission(playerStat, RolePermission.INVITE_PLAYER)){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                        return;
                    }
                    townData.removePlayerJoinRequest(playerIterateData.getID());
                }
                NewsletterStorage.removePlayerJoinRequest(playerIterateData, townData);
                openTownApplications(player, townData, page);
            });
            guiItems.add(playerButton);
        }

        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> openMemberList(player, townData),
                p -> openTownApplications(player, townData, page + 1),
                p -> openTownApplications(player, townData, page - 1),
                Material.LIME_STAINED_GLASS_PANE);

        gui.open(player);

    }
    public static void openTerritoryRanks(Player player, TerritoryData territoryData) {
        int row = 3;
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_TERRITORY_RANKS.get(playerData),row);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        for (RankData rank: territoryData.getAllRanksSorted()) {
            ItemStack townRankItemStack = HeadUtils.createCustomItemStack(rank.getRankIcon(), rank.getColoredName());
            GuiItem singleRankButton = ItemBuilder.from(townRankItemStack).asGuiItem(event -> {
                event.setCancelled(true);
                if(!territoryData.doesPlayerHavePermission(playerData, RolePermission.MANAGE_RANKS)) {
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                    return;
                }
                if(territoryData.getRank(playerData).getLevel() <= rank.getLevel() && !territoryData.isLeader(playerData)){
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get(playerData));
                    return;
                }
                openRankManager(player, territoryData,rank);
            });

            gui.addItem(singleRankButton);
        }

        ItemStack createNewRole = HeadUtils.makeSkullB64(Lang.GUI_TOWN_MEMBERS_ADD_NEW_ROLES.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTBjOTdlNGI2OGFhYWFlODQ3MmUzNDFiMWQ4NzJiOTNiMzZkNGViNmVhODllY2VjMjZhNjZlNmM0ZTE3OCJ9fX0=");
        GuiItem createNewButton = ItemBuilder.from(createNewRole).asGuiItem(event -> {
            event.setCancelled(true);

            if(!territoryData.doesPlayerHavePermission(playerData, RolePermission.CREATE_RANK)) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                return;
            }
            if(territoryData.getNumberOfRank() >= ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("townMaxRank",8)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_RANK_CAP_REACHED.get(playerData));
                return;
            }
            player.sendMessage(TanChatUtils.getTANString() + Lang.WRITE_IN_CHAT_NEW_ROLE_NAME.get(playerData));
            PlayerChatListenerStorage.register(player, new CreateRank(territoryData, p -> openTerritoryRanks(player, territoryData)));
        });

        GuiItem decorativePanel = ItemBuilder.from(HeadUtils.createCustomItemStack(Material.GRAY_STAINED_GLASS_PANE, "")).asGuiItem(event -> event.setCancelled(true));

        gui.getFiller().fillBottom(decorativePanel);
        gui.setItem(row,1, IGUI.createBackArrow(player, p -> openMemberList(player, territoryData)));
        gui.setItem(row,3, createNewButton);

        gui.open(player);

    }
    public static void openRankManager(Player player, TerritoryData territoryData, RankData rankData) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_TERRITORY_SPECIFIC_RANK.get(playerData, rankData.getName()),4);

        boolean isDefaultRank = Objects.equals(rankData.getID(), territoryData.getDefaultRankID());

        ItemStack roleIcon = HeadUtils.createCustomItemStack(
                rankData.getRankIcon(),
                Lang.GUI_BASIC_NAME.get(playerData, rankData.getColoredName()),
                Lang.GUI_TOWN_MEMBERS_ROLE_NAME_DESC1.get(playerData));

        ItemStack roleRankIcon = rankData.getRankEnum().getRankGuiIcon();

        ArrayList<String> playerNames = new ArrayList<>();
        playerNames.add(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO_DESC1.get(playerData));
        for (String playerUUID : rankData.getPlayersID()) {
            String playerName = PlayerDataStorage.getInstance().get(playerUUID).getNameStored();
            playerNames.add(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO_DESC.get(playerData, playerName));
        }
        ItemStack membersRank = HeadUtils.makeSkullB64(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I0M2IyMzE4OWRjZjEzMjZkYTQyNTNkMWQ3NTgyZWY1YWQyOWY2YzI3YjE3MWZlYjE3ZTMxZDA4NGUzYTdkIn19fQ==",
                playerNames);

        ItemStack managePermission = HeadUtils.createCustomItemStack(Material.ANVIL,
                Lang.GUI_TOWN_MEMBERS_ROLE_MANAGE_PERMISSION.get(playerData));
        ItemStack renameRank = HeadUtils.createCustomItemStack(Material.NAME_TAG,
                Lang.GUI_TOWN_MEMBERS_ROLE_CHANGE_NAME.get(playerData));
        ItemStack changeRoleTaxRelation = HeadUtils.createCustomItemStack(
                Material.GOLD_NUGGET,
                rankData.isPayingTaxes() ? Lang.GUI_TOWN_MEMBERS_ROLE_PAY_TAXES.get(playerData) : Lang.GUI_TOWN_MEMBERS_ROLE_NOT_PAY_TAXES.get(playerData),
                Lang.GUI_TOWN_MEMBERS_ROLE_TAXES_DESC1.get(playerData)
        );

        ItemStack makeRankDefault = HeadUtils.createCustomItemStack(Material.RED_BED,
                isDefaultRank ? Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_IS_DEFAULT.get(playerData) : Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_IS_NOT_DEFAULT.get(playerData),
                Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT1.get(playerData),
                isDefaultRank ? "" : Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT2.get(playerData));

        ItemStack delete = HeadUtils.createCustomItemStack(Material.BARRIER, Lang.GUI_TOWN_MEMBERS_ROLE_DELETE.get(playerData));

        ItemStack salary = HeadUtils.createCustomItemStack(Material.GOLD_INGOT,
                Lang.GUI_TOWN_MEMBERS_ROLE_SALARY.get(playerData),
                Lang.GUI_TOWN_MEMBERS_ROLE_SALARY_DESC1.get(playerData, rankData.getSalary()));

        ItemStack lowerSalary = HeadUtils.makeSkullB64(Lang.GUI_LOWER_SALARY.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=",
                Lang.GUI_DECREASE_1_DESC.get(playerData),
                Lang.GUI_DECREASE_10_DESC.get(playerData));
        ItemStack increaseSalary = HeadUtils.makeSkullB64(Lang.GUI_INCREASE_SALARY.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                Lang.GUI_INCREASE_1_DESC.get(playerData),
                Lang.GUI_INCREASE_10_DESC.get(playerData));

        GuiItem roleGui = ItemBuilder.from(roleIcon).asGuiItem(event -> {
            event.setCancelled(true);
            ItemStack itemMaterial = event.getCursor();
            if(itemMaterial.getType() == Material.AIR){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_NO_ITEM_SHOWED.get(playerData));
                return;
            }
            rankData.setRankIcon(itemMaterial);
            openRankManager(player, territoryData, rankData);
            player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_CHANGED_ICON_SUCCESS.get(playerData));

        });

        GuiItem rankIconButton = ItemBuilder.from(roleRankIcon).asGuiItem(event -> {
            event.setCancelled(true);
            if(event.isLeftClick()){
                RankData playerRank = territoryData.getRank(player);
                if(playerRank.getRankEnum().getLevel() > (rankData.getRankEnum().getLevel() + 1) || territoryData.isLeader(player.getUniqueId().toString())){
                    rankData.incrementLevel();
                    SoundUtil.playSound(player, ADD);
                }
                else{
                    player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_RANK_UP_INFERIOR_RANK.get(playerData, playerRank.getColoredName()));
                }
            }
            else if(event.isRightClick()){
                rankData.decrementLevel();
                SoundUtil.playSound(player, REMOVE);
            }
            openRankManager(player, territoryData, rankData);
        });
        GuiItem managePermissionGui = ItemBuilder.from(managePermission).asGuiItem(event -> {
            event.setCancelled(true);
            openRankManagerPermissions(player,territoryData, rankData);
        });
        GuiItem membersRankGui = ItemBuilder.from(membersRank).asGuiItem(event -> {
            event.setCancelled(true);
            openTownRankManagerAddPlayer(player,territoryData, rankData, 0);
        });
        GuiItem renameGui = ItemBuilder.from(renameRank).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(TanChatUtils.getTANString() + Lang.WRITE_IN_CHAT_NEW_ROLE_NAME.get(playerData));
            PlayerChatListenerStorage.register(player, new RenameRank(territoryData , rankData));
            player.closeInventory();
        });
        GuiItem taxButton = ItemBuilder.from(changeRoleTaxRelation).asGuiItem(event -> {
            event.setCancelled(true);
            rankData.swapPayingTaxes();
            openRankManager(player, territoryData, rankData);
            SoundUtil.playSound(player, ADD);
        });
        GuiItem defaultRankButton = ItemBuilder.from(makeRankDefault).asGuiItem(event -> {
            event.setCancelled(true);
            if(isDefaultRank){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_ALREADY_DEFAULT.get(playerData));
                SoundUtil.playSound(player, NOT_ALLOWED);
            }
            else{
                territoryData.setDefaultRank(rankData.getID());
                openRankManager(player, territoryData, rankData);
                SoundUtil.playSound(player, ADD);
            }
        });

        GuiItem deleteButton = ItemBuilder.from(delete).asGuiItem(event -> {
            event.setCancelled(true);

            if(rankData.getNumberOfPlayer() != 0){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_DELETE_ERROR_NOT_EMPTY.get(playerData));
                SoundUtil.playSound(player, NOT_ALLOWED);
                return;
            }
            if(territoryData.getDefaultRankID() == rankData.getID()){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_DELETE_ERROR_DEFAULT.get(playerData));
                SoundUtil.playSound(player, NOT_ALLOWED);
                return;
            }
            territoryData.removeRank(rankData.getID());
            openTerritoryRanks(player, territoryData);
            SoundUtil.playSound(player, MINOR_GOOD);

        });

        GuiItem lowerSalaryButton = ItemBuilder.from(lowerSalary).asGuiItem(event -> {
            event.setCancelled(true);
            int currentSalary = rankData.getSalary();
            int amountToRemove = event.isShiftClick() && currentSalary >= 10 ? 10 : 1;

            if (currentSalary <= 0) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_SALARY_ERROR_LOWER.get(playerData));
                SoundUtil.playSound(player, NOT_ALLOWED);
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

        gui.getFiller().fillTop(panel);

        gui.setItem(1,5, roleGui);

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
    public static void openTownRankManagerAddPlayer(Player player, TerritoryData territoryData, RankData wantedRank, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_RANK_ADD_PLAYER.get(playerData, wantedRank.getName()),3);

        List<GuiItem> guiItems = new ArrayList<>();

        for (String otherPlayerUUID : territoryData.getPlayerIDList()) {
            PlayerData otherPlayerData = PlayerDataStorage.getInstance().get(otherPlayerUUID);

            if(otherPlayerData.getRankID(territoryData) == wantedRank.getID()){
                continue;
            }
            ItemStack playerHead = HeadUtils.getPlayerHead(otherPlayerData.getNameStored(), Bukkit.getOfflinePlayer(UUID.fromString(otherPlayerUUID)));
            GuiItem playerInfo = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                RankData otherPlayerActualRank = territoryData.getRank(otherPlayerData);
                if(territoryData.getRank(player).getLevel() <= otherPlayerActualRank.getLevel() && !territoryData.isLeader(playerData)){
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get(playerData));
                    return;
                }

                PlayerData playerStat = PlayerDataStorage.getInstance().get(otherPlayerUUID);
                territoryData.setPlayerRank(playerStat, wantedRank);
                openRankManager(player, territoryData, wantedRank);
            });
            guiItems.add(playerInfo);
        }

        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> openRankManager(player, territoryData, wantedRank),
                p -> openTownRankManagerAddPlayer(player, territoryData, wantedRank, page + 1),
                p -> openTownRankManagerAddPlayer(player, territoryData, wantedRank, page - 1));

        gui.open(player);
    }
    public static void openRankManagerPermissions(Player player, TerritoryData territoryData, RankData rankData) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_RANK_PERMISSIONS.get(playerData),3);

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
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_ECONOMY.get(playerData),5);

        Budget budget = territoryData.getBudget();

        budget.createGui(gui, player);

        List<String> budgetLore = new ArrayList<>();
        budgetLore.add(Lang.GUI_TREASURY_STORAGE_DESC1.get(playerData, territoryData.getBalance()));
        budgetLore.addAll(budget.createLore());

        ItemStack budgetIcon = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_STORAGE.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=",
                budgetLore);
        ItemStack miscSpending = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_MISCELLANEOUS_SPENDING.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGMzNjA0NTIwOGY5YjVkZGNmOGM0NDMzZTQyNGIxY2ExN2I5NGY2Yjk2MjAyZmIxZTUyNzBlZThkNTM4ODFiMSJ9fX0=",
                Lang.GUI_GENERIC_CLICK_TO_OPEN_HISTORY.get(playerData));
        ItemStack donation = HeadUtils.createCustomItemStack(Material.DIAMOND,
                Lang.GUI_TREASURY_DONATION.get(playerData),
                Lang.GUI_TOWN_TREASURY_DONATION_DESC1.get(playerData));
        ItemStack donationHistory = HeadUtils.createCustomItemStack(Material.PAPER,
                Lang.GUI_TREASURY_DONATION_HISTORY.get(playerData),
                Lang.GUI_GENERIC_CLICK_TO_OPEN_HISTORY.get(playerData));
        ItemStack retrieveMoney = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_RETRIEVE_GOLD.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWE2NDUwMWIxYmE1M2QxZDRlOWY0MDI5MTdiNWJkNDc3MjdiMTY3MDJhY2Y2OTMwZDYxMjFjMDdkYzQyYWUxYSJ9fX0=",
                Lang.GUI_TREASURY_RETRIEVE_GOLD_DESC1.get(playerData));


        GuiItem budgetInfo = ItemBuilder.from(budgetIcon).asGuiItem(event -> event.setCancelled(true));


        GuiItem miscSpendingButton = ItemBuilder.from(miscSpending).asGuiItem(event -> {
            event.setCancelled(true);
            openTownEconomicsHistory(player, territoryData, TransactionHistoryEnum.MISCELLANEOUS);
        });
        GuiItem donationButton = ItemBuilder.from(donation).asGuiItem(event -> {
            player.sendMessage(TanChatUtils.getTANString() + Lang.WRITE_IN_CHAT_AMOUNT_OF_MONEY_FOR_DONATION.get(playerData));
            player.closeInventory();

            PlayerChatListenerStorage.register(player, new DonateToTerritory(territoryData));
            event.setCancelled(true);
        });
        GuiItem donationHistoryButton = ItemBuilder.from(donationHistory).asGuiItem(event -> {
            openTownEconomicsHistory(player, territoryData, TransactionHistoryEnum.DONATION);
            event.setCancelled(true);
        });

        GuiItem retrieveButton = ItemBuilder.from(retrieveMoney).asGuiItem(event -> {
            event.setCancelled(true);

            if(!territoryData.doesPlayerHavePermission(playerData, RolePermission.MANAGE_TAXES)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                return;
            }
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_WRITE_QUANTITY_IN_CHAT.get(playerData));
            PlayerChatListenerStorage.register(player,new RetrieveMoney(territoryData));
            player.closeInventory();

        });

        GuiItem panel = ItemBuilder.from(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE)).asGuiItem(event -> event.setCancelled(true));



        gui.setItem(1,1, panel);
        gui.setItem(1,2, panel);
        gui.setItem(1,3, panel);
        gui.setItem(1,4, panel);
        gui.setItem(1,5, budgetInfo);
        gui.setItem(1,6, panel);
        gui.setItem(1,7, panel);
        gui.setItem(1,8, panel);
        gui.setItem(1,9, panel);


        gui.setItem(2,8, miscSpendingButton);

        gui.setItem(3,2, donationButton);
        gui.setItem(3,3, donationHistoryButton);

        gui.setItem(3,4, retrieveButton);

        gui.setItem(5,1, IGUI.createBackArrow(player, p -> territoryData.openMainMenu(player)));

        gui.open(player);

    }
    public static void openTownEconomicsHistory(Player player, TerritoryData territoryData, TransactionHistoryEnum transactionHistoryEnum) {
        openTownEconomicsHistory(player, territoryData, transactionHistoryEnum, 0);
    }
    public static void openTownEconomicsHistory(Player player, TerritoryData territoryData, TransactionHistoryEnum transactionHistoryEnum, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_HISTORY.get(playerData),6);
        List<GuiItem> guiItems = new ArrayList<>();

        for(List<TransactionHistory> transactionHistory : TownsAndNations.getPlugin().getDatabaseHandler().getTransactionHistory(territoryData, transactionHistoryEnum)){
            ItemStack transactionIcon = HeadUtils.createCustomItemStack(Material.PAPER, ChatColor.GREEN + transactionHistory.get(0).getDate());

            for (TransactionHistory transaction : transactionHistory) {
                HeadUtils.addLore(transactionIcon, transaction.addLoreLine());
            }
            guiItems.add(ItemBuilder.from(transactionIcon).asGuiItem(event -> event.setCancelled(true)));
        }

        Collections.reverse(guiItems);//newer first

        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> openTreasury(player, territoryData),
                p -> openTownEconomicsHistory(player, territoryData, transactionHistoryEnum, page + 1),
                p -> openTownEconomicsHistory(player, territoryData, transactionHistoryEnum, page - 1));

        gui.open(player);
    }
    public static void openTownLevel(Player player, int level){
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_TOWN_UPGRADE.get(playerData, level + 1),6);

        TownData townData = TownDataStorage.getInstance().get(player);
        Level townLevel = townData.getLevel();

        ItemStack whitePanel = HeadUtils.createCustomItemStack(Material.WHITE_STAINED_GLASS_PANE,"");
        ItemStack ironBars = HeadUtils.createCustomItemStack(Material.IRON_BARS,Lang.LEVEL_LOCKED.get(playerData));

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
                        Lang.GUI_TOWN_LEVEL_UP.get(playerData),
                        Lang.GUI_TOWN_LEVEL_UP_DESC1.get(playerData, townLevel.getTownLevel()),
                        Lang.GUI_TOWN_LEVEL_UP_DESC2.get(playerData, townLevel.getTownLevel() + 1, townLevel.getMoneyRequiredForLevelUp()));

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
            if(level + 1 <= townUpgrade.getCol() && townUpgrade.getCol() <= level + 7){
                gui.setItem(townUpgrade.getRow(),townUpgrade.getCol() + (1 - level),guiButton);
            }
        }

        ItemStack nextPageButton = HeadUtils.makeSkullB64(
                Lang.GUI_NEXT_PAGE.get(playerData),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2MjYyYWYxZDVmNDE0YzU5NzA1NWMyMmUzOWNjZTE0OGU1ZWRiZWM0NTU1OWEyZDZiODhjOGQ2N2I5MmVhNiJ9fX0="
        );

        ItemStack previousPageButton = HeadUtils.makeSkullB64(
                Lang.GUI_PREVIOUS_PAGE.get(playerData),
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
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_SETTINGS.get(playerData   ),4);

        int changeTownNameCost = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("ChangeTownNameCost");


        ItemStack townIcon = townData.getIconWithInformations(playerData.getLang());
        ItemStack leaveTown = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN.get(playerData),
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC1.get(playerData, townData.getName()),
                Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC2.get(playerData));
        ItemStack deleteTown = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN.get(playerData),
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC1.get(playerData, townData.getName()),
                Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC2.get(playerData));
        ItemStack changeOwnershipTown = HeadUtils.createCustomItemStack(Material.BEEHIVE,
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP.get(playerData),
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC1.get(playerData),
                Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC2.get(playerData));
        ItemStack changeMessage = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK,
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_MESSAGE.get(playerData),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_MESSAGE_DESC1.get(playerData, townData.getDescription()));
        ItemStack toggleApplication = HeadUtils.createCustomItemStack(Material.PAPER,
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION.get(playerData),
                (townData.isRecruiting() ? Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_ACCEPT.get(playerData) : Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_NOT_ACCEPT.get(playerData)),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_CLICK_TO_SWITCH.get(playerData));
        ItemStack changeTownName = HeadUtils.createCustomItemStack(Material.NAME_TAG,
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME.get(playerData),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME_DESC1.get(playerData, townData.getName()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME_DESC2.get(playerData),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME_DESC3.get(playerData, changeTownNameCost));
        ItemStack changeChunkColor = HeadUtils.createCustomItemStack(Material.PURPLE_WOOL,
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR.get(playerData),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC1.get(playerData),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC2.get(playerData, new TextComponent(townData.getChunkColor() + townData.getChunkColorInHex()).getText()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC3.get(playerData));

        ItemStack changeTag = HeadUtils.createCustomItemStack(Material.FLOWER_BANNER_PATTERN,
                Lang.GUI_TOWN_SETTINGS_CHANGE_TAG.get(playerData),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TAG_DESC1.get(playerData, townData.getColoredTag()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_TAG_DESC2.get(playerData));

        GuiItem townIconButton = ItemBuilder.from(townIcon).asGuiItem(event -> event.setCancelled(true));

        GuiItem leaveTownButton = ItemBuilder.from(leaveTown).asGuiItem(event -> {
            event.setCancelled(true);
            if (townData.isLeader(playerData)){
                SoundUtil.playSound(player, NOT_ALLOWED);
                player.sendMessage(TanChatUtils.getTANString() + Lang.CHAT_CANT_LEAVE_TOWN_IF_LEADER.get(playerData));
                return;
            }

            if(!player.hasPermission("tan.base.town.quit")){
                player.sendMessage(Lang.PLAYER_NO_PERMISSION.get());
                SoundUtil.playSound(player, NOT_ALLOWED);
                return;
            }

            openConfirmMenu(player, Lang.GUI_CONFIRM_PLAYER_LEAVE_TOWN.get(playerData, playerData.getNameStored()), confirm -> {

                player.closeInventory();
                townData.removePlayer(playerData);
                player.sendMessage(TanChatUtils.getTANString() + Lang.CHAT_PLAYER_LEFT_THE_TOWN.get(playerData));
                townData.broadcastMessageWithSound(Lang.TOWN_BROADCAST_PLAYER_LEAVE_THE_TOWN.get(playerData, playerData.getNameStored()), BAD);
            }, remove -> openTownSettings(player, townData));
        });
        GuiItem deleteButton = ItemBuilder.from(deleteTown).asGuiItem(event -> {
            event.setCancelled(true);
            if (!townData.isLeader(playerData)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.CHAT_CANT_DISBAND_TOWN_IF_NOT_LEADER.get(playerData));
                return;
            }
            if(townData.isCapital()){
                player.sendMessage(Lang.CANNOT_DELETE_TERRITORY_IF_CAPITAL.get(playerData, townData.getOverlord().getColoredName()));
                return;
            }

            if(!player.hasPermission("tan.base.town.disband")){
                player.sendMessage(Lang.PLAYER_NO_PERMISSION.get(playerData));
                SoundUtil.playSound(player, NOT_ALLOWED);
                return;
            }

            openConfirmMenu(player, Lang.GUI_CONFIRM_PLAYER_DELETE_TOWN.get(playerData, townData.getName()), confirm -> {
                FileUtil.addLineToHistory(Lang.HISTORY_TOWN_DELETED.get(playerData, player.getName(),townData.getName()));
                townData.delete();
                player.closeInventory();
                SoundUtil.playSound(player,GOOD);
                player.sendMessage(TanChatUtils.getTANString() + Lang.CHAT_PLAYER_TOWN_SUCCESSFULLY_DELETED.get(playerData));
            }, remove -> openTownSettings(player, townData));


        });

        GuiItem changeOwnershipTownButton = ItemBuilder.from(changeOwnershipTown).asGuiItem(event -> {

            event.setCancelled(true);

            if(townData.isLeader(playerData))
                openTownChangeOwnershipPlayerSelect(player, townData, 0);
            else
                player.sendMessage(TanChatUtils.getTANString() + Lang.NOT_TOWN_LEADER_ERROR.get(playerData));

        });

        GuiItem changeMessageButton = ItemBuilder.from(changeMessage).asGuiItem(event -> {
            player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(playerData));
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
                player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get(playerData));
                return;
            }

            if(townData.doesPlayerHavePermission(playerData, RolePermission.TOWN_ADMINISTRATOR)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(playerData));
                PlayerChatListenerStorage.register(player, new ChangeTerritoryName(townData,changeTownNameCost, p -> openTownSettings(player, townData)));
            }
            else
                player.sendMessage(TanChatUtils.getTANString() + Lang.NOT_TOWN_LEADER_ERROR.get(playerData));
        });

        GuiItem changeChunkColorButton = ItemBuilder.from(changeChunkColor).asGuiItem(event -> {
            event.setCancelled(true);

            if(townData.doesPlayerHavePermission(playerData, RolePermission.TOWN_ADMINISTRATOR)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT.get(playerData));
                PlayerChatListenerStorage.register(player, new ChangeColor(townData, p -> openTownSettings(player, townData)));
            }
            else
                player.sendMessage(TanChatUtils.getTANString() + Lang.NOT_TOWN_LEADER_ERROR.get(playerData));
        });

        GuiItem changeTagButton = ItemBuilder.from(changeTag).asGuiItem(event -> {
            event.setCancelled(true);

            if(townData.doesPlayerHavePermission(playerData, RolePermission.TOWN_ADMINISTRATOR)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(playerData));
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
    public static void openTownChangeOwnershipPlayerSelect(Player player, TownData townData, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_CHANGE_OWNERSHIP.get(playerData),3);

        List<GuiItem> guiItems = new ArrayList<>();
        for (String playerUUID : townData.getPlayerIDList()){
            OfflinePlayer townPlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerUUID));

            ItemStack playerHead = HeadUtils.getPlayerHead(townPlayer.getName(),townPlayer,
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC1.get(playerData, player.getName()),
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC2.get(playerData));


            GuiItem playerHeadIcon = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);

                openConfirmMenu(player, Lang.GUI_CONFIRM_CHANGE_TOWN_LEADER.get(playerData, townPlayer.getName()), confirm -> {

                    townData.setLeaderID(townPlayer.getUniqueId().toString());
                    player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_SUCCESS.get(playerData, townPlayer.getName()));
                    dispatchPlayerTown(player);

                    player.closeInventory();

                }, remove -> openTownSettings(player, townData));

            });
            guiItems.add(playerHeadIcon);
        }
        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> openTownSettings(player, townData),
                p -> openTownChangeOwnershipPlayerSelect(player, townData, page + 1),
                p -> openTownChangeOwnershipPlayerSelect(player, townData, page - 1));

        gui.open(player);
    }
    public static void openRelations(Player player, TerritoryData territory) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_RELATIONS.get(playerData, territory.getName()),3);

        ItemStack war = HeadUtils.createCustomItemStack(Material.IRON_SWORD,
                Lang.GUI_TOWN_RELATION_HOSTILE.get(playerData),
                Lang.GUI_TOWN_RELATION_HOSTILE_DESC1.get(playerData));
        ItemStack embargo = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.GUI_TOWN_RELATION_EMBARGO.get(playerData),
                Lang.GUI_TOWN_RELATION_EMBARGO_DESC1.get(playerData));
        ItemStack nap = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK,
                Lang.GUI_TOWN_RELATION_NAP.get(playerData),
                Lang.GUI_TOWN_RELATION_NAP_DESC1.get(playerData));
        ItemStack alliance = HeadUtils.createCustomItemStack(Material.CAMPFIRE,
                Lang.GUI_TOWN_RELATION_ALLIANCE.get(playerData),
                Lang.GUI_TOWN_RELATION_ALLIANCE_DESC1.get(playerData));
        ItemStack diplomacyProposal = HeadUtils.createCustomItemStack(Material.PAPER,
                Lang.GUI_TOWN_RELATION_DIPLOMACY_PROPOSAL.get(playerData),
                Lang.GUI_TOWN_RELATION_DIPLOMACY_PROPOSAL_DESC1.get(playerData),
                Lang.GUI_TOWN_RELATION_DIPLOMACY_PROPOSAL_DESC2.get(playerData, territory.getAllDiplomacyProposal().size()));

        GuiItem warButton = ItemBuilder.from(war).asGuiItem(event -> {
            event.setCancelled(true);
            openSingleRelation(player,territory, TownRelation.WAR,0);
        });
        GuiItem embargoButton = ItemBuilder.from(embargo).asGuiItem(event -> {
            event.setCancelled(true);
            openSingleRelation(player,territory, TownRelation.EMBARGO,0);

        });
        GuiItem napButton = ItemBuilder.from(nap).asGuiItem(event -> {
            event.setCancelled(true);
            openSingleRelation(player,territory, TownRelation.NON_AGGRESSION,0);

        });
        GuiItem allianceButton = ItemBuilder.from(alliance).asGuiItem(event -> {
            event.setCancelled(true);
            openSingleRelation(player,territory, TownRelation.ALLIANCE,0);
        });
        GuiItem proposalsButton = ItemBuilder.from(diplomacyProposal).asGuiItem(event -> {
            event.setCancelled(true);
            if(!territory.doesPlayerHavePermission(playerData, RolePermission.MANAGE_TOWN_RELATION)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                return;
            }
            openProposalMenu(player, territory, 0);
        });

        GuiItem panel = ItemBuilder.from(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).asGuiItem(event -> event.setCancelled(true));

        gui.getFiller().fillTop(panel);
        gui.getFiller().fillBottom(panel);

        gui.setItem(9, warButton);
        gui.setItem(11, embargoButton);
        gui.setItem(13, napButton);
        gui.setItem(15, allianceButton);
        gui.setItem(17, proposalsButton);

        gui.setItem(3,1, IGUI.createBackArrow(player,p -> territory.openMainMenu(player)));
        gui.open(player);
    }

    public static void openProposalMenu(Player player, TerritoryData territoryData, int page){
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_RELATIONS.get(playerData, territoryData.getName()),6);

        ArrayList<GuiItem> guiItems = new ArrayList<>();

        for(DiplomacyProposal diplomacyProposal : territoryData.getAllDiplomacyProposal()){
            guiItems.add(diplomacyProposal.createGuiItem(player, territoryData, page));
        }

        GuiUtil.createIterator(gui, guiItems, page, player, p -> openRelations(player, territoryData),
                p -> openProposalMenu(player, territoryData, page - 1),
                p -> openProposalMenu(player, territoryData, page + 1));

        gui.open(player);
    }

    public static void openSingleRelation(Player player, TerritoryData mainTerritory, TownRelation relation, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_RELATION_WITH.get(playerData, relation.getName(), page + 1), 6);

        PlayerData playerStat = PlayerDataStorage.getInstance().get(player);

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for(String territoryID : mainTerritory.getRelations().getTerritoriesIDWithRelation(relation)){

            TerritoryData territoryData = TerritoryUtil.getTerritory(territoryID);
            ItemStack icon = territoryData.getIconWithInformationAndRelation(mainTerritory, playerData.getLang());

            if (relation == TownRelation.WAR) {
                ItemMeta meta = icon.getItemMeta();
                assert meta != null;
                List<String> lore = meta.getLore();
                assert lore != null;
                lore.add(Lang.GUI_TOWN_ATTACK_TOWN_DESC1.get(playerData));
                meta.setLore(lore);
                icon.setItemMeta(meta);
            }

            GuiItem townButton = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);

                if (relation == TownRelation.WAR) {
                    if(territoryData.getNumberOfClaimedChunk() < 1){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_ATTACK_NO_CLAIMED_CHUNK.get(playerData));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    if(mainTerritory.atWarWith(territoryID)){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_ATTACK_ALREADY_ATTACKING.get(playerData));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    openStartWarSettings(player, new CreateAttackData(mainTerritory, territoryData));
                }
            });
            guiItems.add(townButton);
        }

        ItemStack addTownButton = HeadUtils.makeSkullB64(
                Lang.GUI_TOWN_RELATION_ADD_TOWN.get(playerData),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19"
        );
        ItemStack removeTownButton = HeadUtils.makeSkullB64(
                Lang.GUI_TOWN_RELATION_REMOVE_TOWN.get(playerData),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0="
        );

        GuiItem addRelation = ItemBuilder.from(addTownButton).asGuiItem(event -> {
            event.setCancelled(true);
            if(!mainTerritory.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_TOWN_RELATION)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                return;
            }
            openTownRelationAdd(player,mainTerritory,relation, 0);
        });
        GuiItem removeRelation = ItemBuilder.from(removeTownButton).asGuiItem(event -> {
            event.setCancelled(true);
            if(!mainTerritory.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_TOWN_RELATION)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                return;
            }
            openTownRelationRemove(player,mainTerritory, relation, 0);
        });

        GuiUtil.createIterator(gui, guiItems, page, player, p -> openRelations(player, mainTerritory),
                p -> openSingleRelation(player, mainTerritory, relation, page - 1),
                p -> openSingleRelation(player, mainTerritory, relation,page - 1));

        gui.setItem(6,4, addRelation);
        gui.setItem(6,5, removeRelation);


        gui.open(player);
    }
    public static void openTownRelationAdd(Player player, TerritoryData territory, TownRelation wantedRelation, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_SELECT_ADD_TERRITORY_RELATION.get(playerData, wantedRelation.getName()), 6);

        List<String> relationListID = territory.getRelations().getTerritoriesIDWithRelation(wantedRelation);
        ItemStack decorativeGlass = HeadUtils.createCustomItemStack(Material.GREEN_STAINED_GLASS_PANE,"");
        List<GuiItem> guiItems = new ArrayList<>();

        List<String> territories = new ArrayList<>();
        territories.addAll(TownDataStorage.getInstance().getTownMap().keySet());
        territories.addAll(RegionDataStorage.getInstance().getRegionStorage().keySet());

        territories.removeAll(relationListID); //Territory already have this relation
        territories.remove(territory.getID()); //Remove itself

        for(String otherTownUUID : territories){
            TerritoryData otherTerritory = TerritoryUtil.getTerritory(otherTownUUID);
            ItemStack icon = otherTerritory.getIconWithInformationAndRelation(territory, playerData.getLang());

            TownRelation actualRelation = territory.getRelationWith(otherTerritory);

            if(!actualRelation.canBeChanged()){
                continue;
            }

            GuiItem iconGui = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);

                if(otherTerritory.haveNoLeader()){
                    player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_NO_LEADER.get(playerData));
                    return;
                }

                if(wantedRelation.isSuperiorTo(actualRelation)){
                    otherTerritory.receiveDiplomaticProposal(territory, wantedRelation);
                    player.sendMessage(TanChatUtils.getTANString() + Lang.DIPLOMATIC_INVITATION_SENT_SUCCESS.get(playerData, otherTerritory.getName()));
                    SoundUtil.playSound(player, MINOR_GOOD);
                }
                else{
                    territory.setRelation(otherTerritory,wantedRelation);
                }
                openSingleRelation(player,territory, wantedRelation,0);

            });
            guiItems.add(iconGui);
        }



        GuiUtil.createIterator(gui, guiItems, 0, player, p -> openSingleRelation(player, territory, wantedRelation,0),
                p -> openTownRelationAdd(player, territory, wantedRelation,page - 1),
                p -> openTownRelationAdd(player, territory, wantedRelation,page + 1),
                decorativeGlass);


        gui.open(player);
    }

    public static void openTownRelationRemove(Player player, TerritoryData territory, TownRelation wantedRelation, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_SELECT_REMOVE_TERRITORY_RELATION.get(playerData, wantedRelation.getName()),6);

        List<String> relationListID = territory.getRelations().getTerritoriesIDWithRelation(wantedRelation);
        ItemStack decorativeGlass = HeadUtils.createCustomItemStack(Material.RED_STAINED_GLASS_PANE,"");
        List<GuiItem> guiItems = new ArrayList<>();


        for(String otherTownUUID : relationListID){
            TerritoryData otherTerritory = TerritoryUtil.getTerritory(otherTownUUID);
            ItemStack townIcon = otherTerritory.getIconWithInformationAndRelation(territory, playerData.getLang());

            GuiItem townGui = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);

                if(wantedRelation.isSuperiorTo(TownRelation.NEUTRAL)){
                    territory.setRelation(otherTerritory, TownRelation.NEUTRAL);
                }
                else {
                    otherTerritory.receiveDiplomaticProposal(territory, TownRelation.NEUTRAL);
                    player.sendMessage(TanChatUtils.getTANString() + Lang.DIPLOMATIC_INVITATION_SENT_SUCCESS.get(playerData, otherTerritory.getName()));
                    SoundUtil.playSound(player, MINOR_GOOD);
                }
                openSingleRelation(player,territory,wantedRelation,0);
            });
            guiItems.add(townGui);
        }

        GuiUtil.createIterator(gui, guiItems, 0, player, p -> openSingleRelation(player, territory, wantedRelation,0),
                p -> openTownRelationRemove(player, territory, wantedRelation,page - 1),
                p -> openTownRelationRemove(player, territory, wantedRelation,page + 1),
                decorativeGlass);


        gui.open(player);
    }


    public static void openChunkSettings(Player player, TerritoryData territoryData) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_TOWN_MENU.get(playerData, territoryData.getName()),3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        ItemStack playerChunkIcon = HeadUtils.createCustomItemStack(Material.PLAYER_HEAD,
                Lang.GUI_TOWN_CHUNK_PLAYER.get(playerData),
                Lang.GUI_TOWN_CHUNK_PLAYER_DESC1.get(playerData)
        );
        ItemStack generalChunkSettingsIcon = HeadUtils.makeSkullURL(Lang.CHUNK_GENERAL_SETTINGS.get(playerData),"https://textures.minecraft.net/texture/5f8c703105180d2586d7f96019dac489776ae488dd6ceb981d08fae4325ea4d1",
                Lang.CHUNK_GENERAL_SETTINGS_DESC1.get(playerData)
        );

        ItemStack mobChunckIcon = HeadUtils.createCustomItemStack(Material.CREEPER_HEAD,
                Lang.GUI_TOWN_CHUNK_MOB.get(playerData),
                Lang.GUI_TOWN_CHUNK_MOB_DESC1.get(playerData)
        );

        GuiItem playerChunkButton = ItemBuilder.from(playerChunkIcon).asGuiItem(event -> openChunkPlayerSettings(player, territoryData));
        GuiItem generalChunkSettingsButton = ItemBuilder.from(generalChunkSettingsIcon).asGuiItem(event -> openChunkGeneralSettings(player, territoryData));

        GuiItem mobChunkButton = ItemBuilder.from(mobChunckIcon).asGuiItem(event -> {
            if(territoryData instanceof TownData townData){
                if(townData.getLevel().getBenefitsLevel("UNLOCK_MOB_BAN") >= 1)
                    openTownChunkMobSettings(player,0);
                else{
                    player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_LEVEL.get(playerData, DynamicLang.get("UNLOCK_MOB_BAN")));
                    SoundUtil.playSound(player, NOT_ALLOWED);
                }
            }
        });

        gui.setItem(2,3, playerChunkButton);
        gui.setItem(2,5, generalChunkSettingsButton);
        gui.setItem(2,7, mobChunkButton);


        gui.setItem(3,1, IGUI.createBackArrow(player, territoryData::openMainMenu));

        gui.open(player);
    }

    private static void openChunkGeneralSettings(Player player, TerritoryData territoryData) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_CHUNK_GENERAL_SETTINGS.get(playerData),3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));
        Map<GeneralChunkSetting, Boolean> generalSettings = territoryData.getChunkSettings().getChunkSetting();

        for(GeneralChunkSetting generalChunkSetting : GeneralChunkSetting.values()){


            GuiItem guiItem = ItemBuilder.from(generalChunkSetting.getIcon(generalSettings.get(generalChunkSetting))).asGuiItem(event -> {
                event.setCancelled(true);
                if(!territoryData.doesPlayerHavePermission(player, RolePermission.MANAGE_CLAIM_SETTINGS)){
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                    SoundUtil.playSound(player, NOT_ALLOWED);
                    return;
                }
                generalSettings.put(generalChunkSetting, !generalSettings.get(generalChunkSetting));
                SoundUtil.playSound(player, ADD);
                openChunkGeneralSettings(player, territoryData);
            });
            gui.addItem(guiItem);
        }


        gui.setItem(3,1, IGUI.createBackArrow(player, p -> openChunkSettings(player, territoryData)));
        gui.open(player);
    }

    public static void openTownChunkMobSettings(Player player, int page){
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_MOB_SETTINGS.get(playerData, page + 1),6);

        TownData townData = TownDataStorage.getInstance().get(player);
        ClaimedChunkSettings chunkSettings = townData.getChunkSettings();

        ArrayList<GuiItem> guiLists = new ArrayList<>();
        Collection<MobChunkSpawnEnum> mobCollection = MobChunkSpawnStorage.getMobSpawnStorage().values();

        for (MobChunkSpawnEnum mobEnum : mobCollection) {

            UpgradeStatus upgradeStatus = chunkSettings.getSpawnControl(mobEnum);

            List<String> status = new ArrayList<>();
            int cost = MobChunkSpawnStorage.getMobSpawnCost(mobEnum);
            if(upgradeStatus.isUnlocked()){
                if(upgradeStatus.canSpawn()){
                    status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_ACTIVATED.get(playerData));
                }
                else{
                    status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_DEACTIVATED.get(playerData));
                }
            }
            else{
                status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_LOCKED.get(playerData));
                status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_LOCKED2.get(playerData, cost));
            }
            ItemStack mobIcon = HeadUtils.makeSkullB64(mobEnum.name(),mobEnum.getTexture(),status);

            GuiItem mobItem = new GuiItem(mobIcon, event -> {
                event.setCancelled(true);
                if(!townData.doesPlayerHavePermission(playerData, RolePermission.MANAGE_MOB_SPAWN)){
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                    return;
                }
                if(upgradeStatus.isUnlocked()){
                    upgradeStatus.setActivated(!upgradeStatus.canSpawn());
                    SoundUtil.playSound(player, ADD);
                }
                else{
                    if(townData.getBalance() < cost){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get(playerData));
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

        GuiUtil.createIterator(gui, guiLists, page, player, p -> openChunkSettings(player, townData),
                p -> openTownChunkMobSettings(player, page + 1),
                p -> openTownChunkMobSettings(player, page - 1));


        gui.open(player);
    }
    public static void openTownPropertiesMenu(Player player, int page){
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_PLAYER_PROPERTIES.get(playerData), 6);
        ArrayList<GuiItem> guiItems = new ArrayList<>();

        TownData townData = TownDataStorage.getInstance().get(playerData);

        for (PropertyData townProperty : townData.getProperties()){
            ItemStack property = townProperty.getIcon(playerData.getLang());

            GuiItem propertyButton = ItemBuilder.from(property).asGuiItem(event -> {
                event.setCancelled(true);
                if(!playerData.hasTown()){
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_TOWN.get(playerData));
                    SoundUtil.playSound(player, NOT_ALLOWED);
                    return;
                }
                if(!townData.doesPlayerHavePermission(playerData, RolePermission.MANAGE_PROPERTY)){
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
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
    public static void openChunkPlayerSettings(Player player, TerritoryData territoryData){
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_CHUNK_PERMISSION.get(playerData),4);

        for(ChunkPermissionType type : ChunkPermissionType.values()){
            RelationPermission permission = territoryData.getPermission(type).getOverallPermission();
            ItemStack icon = type.getIcon(permission, playerData.getLang());
                GuiItem guiItem = ItemBuilder.from(icon).asGuiItem(event -> {
                    event.setCancelled(true);
                    if (!territoryData.doesPlayerHavePermission(player, RolePermission.MANAGE_CLAIM_SETTINGS)) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                        return;
                    }
                    if(event.isLeftClick()){
                        territoryData.nextPermission(type);
                        openChunkPlayerSettings(player, territoryData);
                    }
                    else if(event.isRightClick()){
                        openPlayerListForChunkPermission(player, territoryData, type, 0);
                    }
                });
            gui.addItem(guiItem);
        }
        gui.setItem(27, IGUI.createBackArrow(player, p -> openChunkSettings(player, territoryData)));
        gui.open(player);
    }

    private static void openPlayerListForChunkPermission(Player player, TerritoryData territoryData, ChunkPermissionType type, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(type.getLabel(playerData.getLang()),6);

        PlayerData playerStat = PlayerDataStorage.getInstance().get(player.getUniqueId().toString());
        List<GuiItem> guiItems = new ArrayList<>();

        for(String authorizedPlayerID : territoryData.getPermission(type).getAuthorizedPlayers()){
            OfflinePlayer authorizedPlayer = Bukkit.getOfflinePlayer(UUID.fromString(authorizedPlayerID));
            ItemStack icon = HeadUtils.getPlayerHead(authorizedPlayer.getName(),authorizedPlayer,
                    Lang.GUI_TOWN_MEMBER_DESC3.get(playerData));

            GuiItem guiItem = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);
                if(!territoryData.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_CLAIM_SETTINGS)){
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                    return;
                }
                if(event.isRightClick()){
                    territoryData.getPermission(type).removeSpecificPlayerPermission(authorizedPlayerID);
                    openPlayerListForChunkPermission(player, territoryData, type, page);
                }
            });
            guiItems.add(guiItem);
        }

        GuiUtil.createIterator(gui, guiItems, 0, player,
                p -> openChunkPlayerSettings(player, territoryData),
                p -> openPlayerListForChunkPermission(player, territoryData, type, page + 1),
                p -> openPlayerListForChunkPermission(player, territoryData, type, page + 1));


        ItemStack addIcon = HeadUtils.makeSkullB64(Lang.GUI_GENERIC_ADD_BUTTON.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");

        GuiItem addButton = ItemBuilder.from(addIcon).asGuiItem(event -> {
            event.setCancelled(true);
            if(!territoryData.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_CLAIM_SETTINGS)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                SoundUtil.playSound(player, NOT_ALLOWED);
                return;
            }
            openAddPlayerForChunkPermission(player, territoryData, type, 0);
        });

        gui.setItem(6,3, addButton);

        gui.open(player);
    }

    private static void openAddPlayerForChunkPermission(Player player, TerritoryData territoryData, ChunkPermissionType type, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_AUTHORIZE_PLAYER.get(playerData),6);

        PlayerData playerStat = PlayerDataStorage.getInstance().get(player.getUniqueId().toString());

        List<GuiItem> guiItems = new ArrayList<>();

        for(Player playerToAdd : Bukkit.getOnlinePlayers()){

            PlayerData playerToAddData = PlayerDataStorage.getInstance().get(playerToAdd);
            if(territoryData.getPermission(type).isAllowed(territoryData, playerToAddData))
                continue;

            ItemStack icon = HeadUtils.getPlayerHead(playerToAdd.getName(),playerToAdd,
                    Lang.GUI_GENERIC_ADD_BUTTON.get(playerData));

            GuiItem guiItem = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);
                if(!territoryData.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_CLAIM_SETTINGS)){
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                    return;
                }
                territoryData.getPermission(type).addSpecificPlayerPermission(playerToAdd.getUniqueId().toString());
                openPlayerListForChunkPermission(player, territoryData, type, 0);
                SoundUtil.playSound(player, ADD);

            });
            guiItems.add(guiItem);
        }

        GuiUtil.createIterator(gui, guiItems, 0, player,
                p -> territoryData.openMainMenu(player),
                p -> openAddPlayerForChunkPermission(player, territoryData, type, page + 1),
                p -> openAddPlayerForChunkPermission(player, territoryData, type, page + 1));

        gui.open(player);
    }


    public static void openNoRegionMenu(Player player){
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_NO_REGION.get(playerData),3);


        int regionCost = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("regionCost");

        ItemStack createRegion = HeadUtils.createCustomItemStack(Material.STONE_BRICKS,
                Lang.GUI_REGION_CREATE.get(playerData),
                Lang.GUI_REGION_CREATE_DESC1.get(playerData, regionCost),
                Lang.GUI_REGION_CREATE_DESC2.get(playerData)
        );

        ItemStack browseRegion = HeadUtils.createCustomItemStack(Material.BOOK,
                Lang.GUI_REGION_BROWSE.get(playerData),
                Lang.GUI_REGION_BROWSE_DESC1.get(playerData, RegionDataStorage.getInstance().getNumberOfRegion()),
                Lang.GUI_REGION_BROWSE_DESC2.get(playerData)
        );

        GuiItem createRegionButton = ItemBuilder.from(createRegion).asGuiItem(event -> {
            event.setCancelled(true);

            if(!player.hasPermission("tan.base.region.create")){
                player.sendMessage(Lang.PLAYER_NO_PERMISSION.get(playerData));
                SoundUtil.playSound(player, NOT_ALLOWED);
                return;
            }

            if(!playerData.hasTown()){
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_TOWN.get(playerData));
                return;
            }
            double townMoney = TownDataStorage.getInstance().get(player).getBalance();
            if (townMoney < regionCost) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY_EXTENDED.get(playerData, regionCost - townMoney));
            }
            else {
                player.sendMessage(TanChatUtils.getTANString() + Lang.WRITE_IN_CHAT_NEW_REGION_NAME.get(playerData));
                player.closeInventory();
                int cost = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("regionCost");
                PlayerChatListenerStorage.register(player, new CreateRegion(cost));
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
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        RegionData regionData = playerData.getRegion();

        Gui gui = IGUI.createChestGui(Lang.HEADER_REGION_MENU.get(playerData, regionData.getName()),3);


        ItemStack regionIcon = HeadUtils.getRegionIcon(regionData);
        HeadUtils.addLore(regionIcon,
                Lang.GUI_TOWN_INFO_CHANGE_ICON.get(playerData),
                Lang.RIGHT_CLICK_TO_SELECT_MEMBER_HEAD.get(playerData)
        );

        ItemStack treasury = HeadUtils.makeSkullB64(Lang.GUI_TOWN_TREASURY_ICON.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=",
                Lang.GUI_TOWN_TREASURY_ICON_DESC1.get(playerData));

        ItemStack hierarchy = HeadUtils.makeSkullB64(Lang.GUI_HIERARCHY_MENU.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=",
                Lang.GUI_HIERARCHY_MENU_DESC1.get(playerData));

        ItemStack memberIcon = HeadUtils.makeSkullB64(Lang.GUI_TOWN_MEMBERS_ICON.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Q0ZDQ5NmIxZGEwNzUzNmM5NGMxMzEyNGE1ODMzZWJlMGM1MzgyYzhhMzM2YWFkODQ2YzY4MWEyOGQ5MzU2MyJ9fX0=",
                Lang.GUI_TOWN_MEMBERS_ICON_DESC1.get(playerData));
        ItemStack claims = HeadUtils.makeSkullB64(Lang.GUI_CLAIM_ICON.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=",
                Lang.GUI_CLAIM_ICON_DESC1.get(playerData));
        ItemStack manageLaws = HeadUtils.makeSkullURL(Lang.GUI_MANAGE_LAWS.get(playerData) ,"https://textures.minecraft.net/texture/1818d1cc53c275c294f5dfb559174dd931fc516a85af61a1de256aed8bca5e7",
                Lang.GUI_MANAGE_LAWS_DESC1.get(playerData));
        ItemStack browse = HeadUtils.makeSkullB64(Lang.GUI_BROWSE_TERRITORY_ICON.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMzc0ZTIxYjgxYzBiMjFhYmViOGU5N2UxM2UwNzdkM2VkMWVkNDRmMmU5NTZjNjhmNjNhM2UxOWU4OTlmNiJ9fX0=",
                Lang.GUI_BROWSE_TERRITORY_ICON_DESC1.get(playerData));
        ItemStack diplomacy = HeadUtils.makeSkullB64(Lang.GUI_RELATION_ICON.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUwN2Q2ZGU2MzE4MzhlN2E3NTcyMGU1YjM4ZWYxNGQyOTY2ZmRkODQ4NmU3NWQxZjY4MTJlZDk5YmJjYTQ5OSJ9fX0=",
                Lang.GUI_RELATION_ICON_DESC1.get(playerData));
        ItemStack settingIcon = HeadUtils.makeSkullB64(Lang.GUI_TOWN_SETTINGS_ICON.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVkMmNiMzg0NThkYTE3ZmI2Y2RhY2Y3ODcxNjE2MDJhMjQ5M2NiZjkzMjMzNjM2MjUzY2ZmMDdjZDg4YTljMCJ9fX0=",
                Lang.GUI_TOWN_SETTINGS_ICON_DESC1.get(playerData));
        ItemStack war = HeadUtils.makeSkullB64(Lang.GUI_ATTACK_ICON.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjVkZTRmZjhiZTcwZWVlNGQxMDNiMWVlZGY0NTRmMGFiYjlmMDU2OGY1ZjMyNmVjYmE3Y2FiNmE0N2Y5YWRlNCJ9fX0=",
                Lang.GUI_ATTACK_ICON_DESC1.get(playerData));


        GuiItem regionButton = ItemBuilder.from(regionIcon).asGuiItem(event -> {
            event.setCancelled(true);

            if(!regionData.doesPlayerHavePermission(playerData, RolePermission.TOWN_ADMINISTRATOR))
                return;
            if(event.getCursor() == null)
                return;

            ItemStack itemMaterial = event.getCursor();
            if(itemMaterial.getType() == Material.AIR ){
                if(event.isRightClick()){
                    if(regionData.doesPlayerHavePermission(player, RolePermission.TOWN_ADMINISTRATOR)){
                        openSelectHeadTerritoryMenu(player, regionData, 0);
                    }
                    else{
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                        SoundUtil.playSound(player,MINOR_BAD);
                    }
                }
            }
            else {
                regionData.setIcon(new CustomIcon(itemMaterial));
                openRegionMenu(player);
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_CHANGED_ICON_SUCCESS.get(playerData));
            }
        });
        GuiItem treasuryButton = ItemBuilder.from(treasury).asGuiItem(event -> {
            event.setCancelled(true);
            openTreasury(player, regionData);
        });
        GuiItem hierarchyButton = ItemBuilder.from(hierarchy).asGuiItem(event -> {
            event.setCancelled(true);
            openHierarchyMenu(player, regionData);
        });
        GuiItem memberIconButton = ItemBuilder.from(memberIcon).asGuiItem(event -> {
            event.setCancelled(true);
            openMemberList(player, regionData);
        });
        GuiItem browseButton = ItemBuilder.from(browse).asGuiItem(event -> {
            event.setCancelled(true);
            browseTerritory(player, regionData, BrowseScope.ALL,p -> openRegionMenu(player), 0);
        });
        GuiItem diplomacyButton = ItemBuilder.from(diplomacy).asGuiItem(event -> {
            event.setCancelled(true);
            openRelations(player, regionData);
        });
        GuiItem settingsButton = ItemBuilder.from(settingIcon).asGuiItem(event -> {
            event.setCancelled(true);
            openRegionSettings(player);
        });
        GuiItem claimButton = ItemBuilder.from(claims).asGuiItem(event -> {
            event.setCancelled(true);
            openChunkSettings(player, regionData);
        });
        GuiItem warIcon = ItemBuilder.from(war).asGuiItem(event -> {
            event.setCancelled(true);
            openWarMenu(player, regionData, 0);
        });


        gui.setItem(1,5, regionButton);
        gui.setItem(2,2, treasuryButton);
        gui.setItem(2,3, hierarchyButton);
        gui.setItem(2,4, memberIconButton);
        gui.setItem(2,5, browseButton);
        gui.setItem(2,6, warIcon);
        gui.setItem(2,7, diplomacyButton);
        gui.setItem(2,8, settingsButton);
        gui.setItem(3,2, claimButton);


        gui.setItem(3,1, IGUI.createBackArrow(player, p -> openMainMenu(player)));

        gui.open(player);
    }

    private static void openVassalsList(Player player, TerritoryData territoryData, int page){
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_VASSALS.get(playerData, page + 1),6);

        List<GuiItem> guiList = new ArrayList<>();

        for (TerritoryData townData : territoryData.getVassals()){
            ItemStack townIcon = townData.getIconWithInformations(playerData.getLang());
            GuiItem townInfo = ItemBuilder.from(townIcon).asGuiItem(event -> event.setCancelled(true));
            guiList.add(townInfo);
        }

        ItemStack addTown = HeadUtils.makeSkullB64(Lang.GUI_INVITE_TOWN_TO_REGION.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
        ItemStack removeTown = HeadUtils.makeSkullB64(Lang.GUI_KICK_TOWN_TO_REGION.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=");


        GuiItem addButton = ItemBuilder.from(addTown).asGuiItem(event -> {
            event.setCancelled(true);
                if(!territoryData.doesPlayerHavePermission(playerData, RolePermission.TOWN_ADMINISTRATOR)){
                    player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get(playerData));
                    return;
                }
            openAddVassal(player, territoryData, page);
        });
        GuiItem removeButton = ItemBuilder.from(removeTown).asGuiItem(event -> {
            event.setCancelled(true);
            if(!territoryData.doesPlayerHavePermission(playerData, RolePermission.TOWN_ADMINISTRATOR)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get(playerData));
                return;
            }
            openRemoveVassal(player, territoryData, page);
        });

        GuiUtil.createIterator(gui, guiList, 0, player, p -> openHierarchyMenu(player, territoryData),
                p -> openVassalsList(player, territoryData, page - 1),
                p -> openVassalsList(player, territoryData, page + 1));


        gui.setItem(6,3, addButton);
        gui.setItem(6,4, removeButton);
        gui.open(player);
    }
    private static void openAddVassal(Player player, TerritoryData territoryData, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_VASSALS.get(playerData, page + 1), 6);

        List<GuiItem> guiItems = new ArrayList<>();

        for (TerritoryData potentialVassal : territoryData.getPotentialVassals()) {
            if(territoryData.isVassal(potentialVassal) || potentialVassal.containsVassalisationProposal(territoryData))
                continue;

            ItemStack territoryIcon = potentialVassal.getIconWithInformationAndRelation(territoryData, playerData.getLang());
            HeadUtils.addLore(territoryIcon, Lang.GUI_REGION_INVITE_TOWN_DESC1.get(playerData));

            GuiItem townButton = ItemBuilder.from(territoryIcon).asGuiItem(event -> {
                event.setCancelled(true);
                potentialVassal.addVassalisationProposal(territoryData);
                openAddVassal(player, territoryData, page);
            });
            guiItems.add(townButton);
        }

        GuiUtil.createIterator(gui, guiItems, page, player, p -> openVassalsList(player, territoryData, page),
                p -> openAddVassal(player, territoryData, page - 1),
                p -> openAddVassal(player, territoryData, page + 1));

        gui.open(player);
    }

    private static void openRemoveVassal(Player player, TerritoryData territoryData, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_VASSALS.get(playerData, page + 1), 6);

        List<GuiItem> guiItems = new ArrayList<>();
        for (TerritoryData territoryVassal : territoryData.getVassals()){
            ItemStack townIcon = territoryVassal.getIconWithInformationAndRelation(territoryData, playerData.getLang());
            HeadUtils.addLore(townIcon, Lang.GUI_REGION_INVITE_TOWN_DESC1.get(playerData));

            GuiItem townButton = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);

                if(territoryVassal.isCapitalOf(territoryData)){
                    player.sendMessage(TanChatUtils.getTANString() + Lang.CANT_KICK_REGIONAL_CAPITAL.get(playerData, territoryVassal.getName()));
                    return;
                }
                territoryData.broadcastMessageWithSound(Lang.GUI_REGION_KICK_TOWN_BROADCAST.get(playerData, territoryVassal.getName()), BAD);
                territoryVassal.removeOverlord();
                player.closeInventory();
            });
            guiItems.add(townButton);
        }

        GuiUtil.createIterator(gui, guiItems, page, player, p -> openVassalsList(player, territoryData, page),
                p -> openRemoveVassal(player, territoryData, page - 1),
                p -> openRemoveVassal(player, territoryData, page + 1));

        gui.open(player);
    }
    private static void openRegionSettings(Player player) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_SETTINGS.get(playerData), 3);

        PlayerData playerStat = PlayerDataStorage.getInstance().get(player);
        TownData playerTown = TownDataStorage.getInstance().get(playerStat);
        RegionData playerRegion = playerTown.getRegion();

        ItemStack regionIcon = HeadUtils.getRegionIcon(playerRegion);

        ItemStack deleteRegion = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.GUI_REGION_DELETE.get(playerData),
                Lang.GUI_REGION_DELETE_DESC1.get(playerData, playerRegion.getName()),
                Lang.GUI_REGION_DELETE_DESC2.get(playerData),
                Lang.GUI_REGION_DELETE_DESC3.get(playerData)
        );

        ItemStack changeLeader = HeadUtils.createCustomItemStack(Material.GOLDEN_HELMET,
                Lang.GUI_REGION_CHANGE_CAPITAL.get(playerData),
                Lang.GUI_REGION_CHANGE_CAPITAL_DESC1.get(playerData, playerRegion.getCapital().getName()),
                Lang.GUI_REGION_CHANGE_CAPITAL_DESC2.get(playerData)
        );

        ItemStack changeDescription = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK,
                Lang.GUI_REGION_CHANGE_DESCRIPTION.get(playerData),
                Lang.GUI_REGION_CHANGE_DESCRIPTION_DESC1.get(playerData, playerRegion.getDescription()),
                Lang.GUI_REGION_CHANGE_DESCRIPTION_DESC2.get(playerData)
        );

        ItemStack changeName = HeadUtils.createCustomItemStack(
                Material.NAME_TAG,
                Lang.GUI_PROPERTY_CHANGE_NAME.get(playerData),
                Lang.GUI_PROPERTY_CHANGE_NAME_DESC1.get(playerData, playerRegion.getName())
        );

        ItemStack changeColor = HeadUtils.createCustomItemStack(
                Material.PURPLE_WOOL,
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR.get(playerData),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC1.get(playerData),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC2.get(playerData, playerRegion.getChunkColor() + playerTown.getChunkColorInHex()),
                Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC3.get(playerData)
        );

        GuiItem regionInfo = ItemBuilder.from(regionIcon).asGuiItem(event -> event.setCancelled(true));

        GuiItem deleteButton = ItemBuilder.from(deleteRegion).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerRegion.isLeader(playerStat)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get(playerData));
                return;
            }
            if(playerRegion.isCapital()){
                player.sendMessage(Lang.CANNOT_DELETE_TERRITORY_IF_CAPITAL.get(playerData, playerRegion.getOverlord().getColoredName()));
                return;
            }

            if(!player.hasPermission("tan.base.region.disband")){
                player.sendMessage(Lang.PLAYER_NO_PERMISSION.get(playerData));
                SoundUtil.playSound(player, NOT_ALLOWED);
                return;
            }

            openConfirmMenu(player, Lang.GUI_CONFIRM_DELETE_REGION.get(playerData, playerRegion.getName()), confirm -> {
                FileUtil.addLineToHistory(Lang.HISTORY_REGION_DELETED.get(playerData, player.getName(),playerRegion.getName()));
                playerRegion.delete();
                SoundUtil.playSound(player, GOOD);
                player.sendMessage(TanChatUtils.getTANString() + Lang.CHAT_PLAYER_REGION_SUCCESSFULLY_DELETED.get(playerData));
                openMainMenu(player);
            }, remove -> openRegionSettings(player));
        });

        GuiItem changeCapitalButton = ItemBuilder.from(changeLeader).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerRegion.isLeader(playerStat)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get(playerData));
                return;
            }
            openRegionChangeOwnership(player,0);
        });

        GuiItem changeDescriptionButton = ItemBuilder.from(changeDescription).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerRegion.doesPlayerHavePermission(playerStat, RolePermission.TOWN_ADMINISTRATOR)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get(playerData));
                return;
            }
            player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(playerData));
            PlayerChatListenerStorage.register(player, new ChangeDescription(playerRegion, p -> openRegionSettings(player)));
        });

        GuiItem changeNameButton = ItemBuilder.from(changeName).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerRegion.doesPlayerHavePermission(playerStat, RolePermission.TOWN_ADMINISTRATOR)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get(playerData));
                return;
            }

            player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(playerData));
            PlayerChatListenerStorage.register(player, new ChangeTerritoryName(playerRegion, 0, p -> openRegionSettings(player)));
        });

        GuiItem changeChunkColorButton = ItemBuilder.from(changeColor).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT.get(playerData));
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
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_CHANGE_OWNERSHIP.get(playerData), 6);
        RegionData regionData = playerData.getRegion();

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for(String playerID : regionData.getPlayerIDList()){

            PlayerData iteratePlayerData = PlayerDataStorage.getInstance().get(playerID);
            ItemStack switchPlayerIcon = HeadUtils.getPlayerHead(Bukkit.getOfflinePlayer(UUID.fromString(playerID)));

            GuiItem switchPlayerButton = ItemBuilder.from(switchPlayerIcon).asGuiItem(event -> {
                event.setCancelled(true);

                openConfirmMenu(player, Lang.GUI_CONFIRM_CHANGE_LEADER.get(playerData, iteratePlayerData.getNameStored()), confirm -> {
                    FileUtil.addLineToHistory(Lang.HISTORY_REGION_CAPITAL_CHANGED.get(playerData, player.getName(), regionData.getCapital().getName(), playerData.getTown().getName()));
                    regionData.setLeaderID(iteratePlayerData.getID());

                    regionData.broadcastMessageWithSound(Lang.GUI_REGION_SETTINGS_REGION_CHANGE_LEADER_BROADCAST.get(playerData, iteratePlayerData.getNameStored()),GOOD);

                    if(!regionData.getCapital().getID().equals(iteratePlayerData.getTown().getID())){
                        regionData.broadCastMessage(TanChatUtils.getTANString() + Lang.GUI_REGION_SETTINGS_REGION_CHANGE_CAPITAL_BROADCAST.get(playerData, iteratePlayerData.getTown().getName()));
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

        TownData townData = TownDataStorage.getInstance().get(player);
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        if(!landmark.isOwned()){
            openLandmarkNoOwner(player,landmark);
            return;
        }
        if(townData.ownLandmark(landmark)){
            openPlayerOwnLandmark(player,landmark);
            return;
        }
        TownData owner = TownDataStorage.getInstance().get(landmark.getOwnerID());
        player.sendMessage(TanChatUtils.getTANString() + Lang.LANDMARK_ALREADY_CLAIMED.get(playerData, owner.getName()));
        SoundUtil.playSound(player, MINOR_BAD);

    }

    private static void openLandmarkNoOwner(Player player, Landmark landmark) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_LANDMARK_UNCLAIMED.get(playerData), 3);

        GuiItem landmarkIcon = ItemBuilder.from(landmark.getIcon()).asGuiItem(event -> event.setCancelled(true));

        TownData playerTown = TownDataStorage.getInstance().get(player);

        ItemStack claimLandmark = HeadUtils.makeSkullB64(
                Lang.GUI_TOWN_RELATION_ADD_TOWN.get(playerData),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                playerTown.canClaimMoreLandmarks() ? Lang.GUI_LANDMARK_LEFT_CLICK_TO_CLAIM.get(playerData) : Lang.GUI_LANDMARK_TOWN_FULL.get(playerData)
        );

        GuiItem claimLandmarkGui = ItemBuilder.from(claimLandmark).asGuiItem(event -> {
            event.setCancelled(true);
            if(!playerTown.canClaimMoreLandmarks()) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_LANDMARK_TOWN_FULL.get(playerData));
                SoundUtil.playSound(player, MINOR_BAD);
                return;
            }

            playerTown.addLandmark(landmark);
            playerTown.broadcastMessageWithSound(Lang.GUI_LANDMARK_CLAIMED.get(playerData),GOOD);
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
        TownData townData = TownDataStorage.getInstance().get(landmark.getOwnerID());
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_LANDMARK_CLAIMED.get(playerData, townData.getName()), 3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        int quantity = landmark.computeStoredReward(townData);

        ItemStack removeTown = HeadUtils.makeSkullB64(
                Lang.GUI_REMOVE_LANDMARK.get(playerData),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0="
        );

        String bagTexture   ;
        if(quantity == 0)
            bagTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjRjMTY0YmFjMjE4NGE3NmExZWU5NjkxMzI0MmUzMzVmMWQ0MTFjYWZmNTEyMDVlYTM5YjIwNWU2ZjhmMDU4YSJ9fX0=";
        else
            bagTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTliOTA2YjIxNTVmMTkzNzg3MDQyMzM4ZDA1Zjg0MDM5MWMwNWE2ZDNlODE2MjM5MDFiMjk2YmVlM2ZmZGQyIn19fQ==";

        ItemStack collectRessources = HeadUtils.makeSkullB64(
                Lang.GUI_COLLECT_LANDMARK.get(playerData),
                bagTexture,
                Lang.GUI_COLLECT_LANDMARK_DESC1.get(playerData),
                Lang.GUI_COLLECT_LANDMARK_DESC2.get(playerData, quantity)
        );




        GuiItem removeTownButton = ItemBuilder.from(removeTown).asGuiItem(event -> {
            event.setCancelled(true);
            townData.removeLandmark(landmark);
            TownData playerTown = TownDataStorage.getInstance().get(player);
            playerTown.broadcastMessageWithSound(Lang.GUI_LANDMARK_REMOVED.get(playerData),BAD);
            dispatchLandmarkGui(player,landmark);
        });

        GuiItem collectRessourcesButton = ItemBuilder.from(collectRessources).asGuiItem(event -> {
            event.setCancelled(true);
            landmark.giveToPlayer(player,quantity);
            player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_LANDMARK_REWARD_COLLECTED.get(playerData, quantity));
            SoundUtil.playSound(player, GOOD);
            dispatchLandmarkGui(player,landmark);
        });


        ItemStack panel = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        GuiItem panelIcon = ItemBuilder.from(panel).asGuiItem(event -> event.setCancelled(true));


        GuiItem landmarkIcon = ItemBuilder.from(landmark.getIcon()).asGuiItem(event -> event.setCancelled(true));

        gui.getFiller().fillTop(panelIcon);
        gui.getFiller().fillBottom(panelIcon);

        gui.setItem(1,5,landmarkIcon);

        gui.setItem(2,1,panelIcon);
        gui.setItem(2,6,collectRessourcesButton);
        gui.setItem(2,8,removeTownButton);
        gui.setItem(2,9,panelIcon);

        gui.setItem(3,1, IGUI.createBackArrow(player, Player::closeInventory));
        gui.open(player);
    }

    public static void openConfirmMenu(Player player, String confirmLore, Consumer<Void> confirmAction, Consumer<Void> returnAction) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_CONFIRMATION.get(playerData), 3);

        ItemStack confirm = HeadUtils.makeSkullB64(Lang.GENERIC_CONFIRM_ACTION.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNhNDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIwZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=",
                confirmLore);

        ItemStack cancel = HeadUtils.makeSkullB64(Lang.GENERIC_CANCEL_ACTION.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==",
                Lang.GENERIC_CANCEL_ACTION_DESC1.get(playerData));

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
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = IGUI.createChestGui(Lang.HEADER_HIERARCHY.get(playerData), 3);

        GuiItem decorativeGlass = IGUI.getUnnamedItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE);

        GuiItem overlordInfo;
        if(territoryData.canHaveOverlord()){
            GuiItem overlordButton;
            if(territoryData.haveOverlord()){
                TerritoryData overlord = territoryData.getOverlord();
                ItemStack overlordIcon = overlord.getIcon();
                ItemMeta meta = overlordIcon.getItemMeta();
                meta.setDisplayName(Lang.OVERLORD_GUI.get(playerData));
                List<String> lore = new ArrayList<>();
                lore.add(Lang.GUI_OVERLORD_INFO.get(playerData, overlord.getName()));
                meta.setLore(lore);
                overlordIcon.setItemMeta(meta);

                ItemStack declareIndependence = HeadUtils.createCustomItemStack(Material.SPRUCE_DOOR,
                        Lang.GUI_OVERLORD_DECLARE_INDEPENDENCE.get(playerData),
                        Lang.GUI_OVERLORD_DECLARE_INDEPENDENCE_DESC1.get(playerData)
                );
                ItemStack donateToOverlord = HeadUtils.createCustomItemStack(Material.DIAMOND,
                        Lang.GUI_OVERLORD_DONATE.get(playerData),
                        Lang.GUI_OVERLORD_DONATE_DESC1.get(playerData)
                );
                overlordInfo = ItemBuilder.from(overlordIcon).asGuiItem(event -> event.setCancelled(true));
                overlordButton = ItemBuilder.from(declareIndependence).asGuiItem(event -> {
                    event.setCancelled(true);
                    if (!territoryData.haveOverlord()) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.TERRITORY_NO_OVERLORD.get(playerData));
                        openHierarchyMenu(player, territoryData); //This should trigger only if town have been kicked from region during the menu
                        return;
                    }
                    TerritoryData overlordData = territoryData.getOverlord();

                    if (territoryData.isCapital()){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.CANNOT_DECLARE_INDEPENDENCE_BECAUSE_CAPITAL.get(playerData, territoryData.getColoredName()));
                        return;
                    }

                    openConfirmMenu(player, Lang.GUI_CONFIRM_DECLARE_INDEPENDENCE.get(playerData, territoryData.getColoredName(), overlord.getColoredName()), confirm -> {
                        territoryData.removeOverlord();
                        territoryData.broadcastMessageWithSound(Lang.TOWN_BROADCAST_TOWN_LEFT_REGION.get(playerData, territoryData.getName(), overlordData.getName()), BAD);
                        overlordData.broadCastMessage(Lang.REGION_BROADCAST_TOWN_LEFT_REGION.get(playerData, territoryData.getName()));

                        player.closeInventory();
                    },remove -> openHierarchyMenu(player, territoryData));
                });
                GuiItem donateToOverlordButton = ItemBuilder.from(donateToOverlord).asGuiItem(event -> {
                    event.setCancelled(true);
                    player.sendMessage(TanChatUtils.getTANString() + Lang.WRITE_IN_CHAT_AMOUNT_OF_MONEY_FOR_DONATION.get(playerData));
                    PlayerChatListenerStorage.register(player, new DonateToTerritory(overlord));
                });
                gui.setItem(2,3, donateToOverlordButton);
            }
            else {
                ItemStack noCurrentOverlord = HeadUtils.createCustomItemStack(Material.GOLDEN_HELMET, Lang.OVERLORD_GUI.get(playerData),
                        Lang.NO_OVERLORD.get(playerData));
                overlordInfo = ItemBuilder.from(noCurrentOverlord).asGuiItem(event -> event.setCancelled(true));

                ItemStack joinOverlord = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK, Lang.BROWSE_OVERLORD_INVITATION.get(playerData),
                        Lang.BROWSE_OVERLORD_INVITATION_DESC1.get(playerData, territoryData.getNumberOfVassalisationProposals()));

                overlordButton = ItemBuilder.from(joinOverlord).asGuiItem(event -> {
                    event.setCancelled(true);
                    openChooseOverlordMenu(player, territoryData,0);
                });
            }
            gui.setItem(2,2, overlordButton);
        }
        else{
            ItemStack noOverlordItem = HeadUtils.createCustomItemStack(Material.IRON_BARS, Lang.OVERLORD_GUI.get(playerData),
                    Lang.CANNOT_HAVE_OVERLORD.get(playerData));
            overlordInfo = ItemBuilder.from(noOverlordItem).asGuiItem(event -> event.setCancelled(true));

            gui.setItem(2,2, overlordInfo);
            gui.setItem(2,3, overlordInfo);
            gui.setItem(2,4, overlordInfo);

        }
        gui.setItem(1,3, overlordInfo);

        GuiItem vassalInfo;
        if(territoryData.canHaveVassals()){
            ItemStack vassalIcon = HeadUtils.createCustomItemStack(Material.GOLDEN_SWORD, Lang.VASSAL_GUI.get(playerData),
                    Lang.VASSAL_GUI_DESC1.get(playerData, territoryData.getColoredName(), territoryData.getVassalCount()));

            ItemStack vassals = HeadUtils.makeSkullB64(Lang.GUI_REGION_TOWN_LIST.get(playerData),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=",
                    Lang.GUI_REGION_TOWN_LIST_DESC1.get(playerData));
            GuiItem vassalsButton = ItemBuilder.from(vassals).asGuiItem(event -> {
                event.setCancelled(true);
                openVassalsList(player, territoryData, 0);
            });
            vassalInfo = ItemBuilder.from(vassalIcon).asGuiItem(event -> event.setCancelled(true));
            gui.setItem(2,6, vassalsButton);
        }
        else {
            ItemStack noVassalsIcon = HeadUtils.createCustomItemStack(Material.IRON_BARS, Lang.VASSAL_GUI.get(playerData),
                    Lang.CANNOT_HAVE_VASSAL.get(playerData));
            vassalInfo = ItemBuilder.from(noVassalsIcon).asGuiItem(event -> event.setCancelled(true));
            gui.setItem(2,6, vassalInfo);
            gui.setItem(2,7, vassalInfo);
            gui.setItem(2,8, vassalInfo);
        }
        gui.setItem(1,7, vassalInfo);



        gui.getFiller().fillTop(decorativeGlass);
        gui.getFiller().fillBottom(decorativeGlass);

        gui.setItem(2,5, decorativeGlass);
        gui.setItem(2,1, decorativeGlass);
        gui.setItem(2,9, decorativeGlass);

        gui.setItem(3,1, IGUI.createBackArrow(player, p -> territoryData.openMainMenu(player)));
        gui.open(player);
    }

    public static void openChooseOverlordMenu(Player player, TerritoryData territoryData, int page) {
        Gui gui = IGUI.createChestGui("Choose Overlord", 6);

        List<GuiItem> guiItems = territoryData.getAllSubjugationProposals(player, page);

        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> openHierarchyMenu(player, territoryData),
                p -> openChooseOverlordMenu(player, territoryData,page + 1),
                p -> openChooseOverlordMenu(player, territoryData, page - 1));


        gui.open(player);
    }



}
