package org.leralix.tan.gui.user.property;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.ChangePropertyDescription;
import org.leralix.tan.listeners.chat.events.ChangePropertyName;
import org.leralix.tan.listeners.chat.events.ChangePropertyRentPrice;
import org.leralix.tan.listeners.chat.events.ChangePropertySalePrice;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.NumberUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import static org.leralix.lib.data.SoundEnum.MINOR_BAD;
import static org.leralix.lib.data.SoundEnum.MINOR_GOOD;

public abstract class PropertyMenus extends BasicGui {

    protected final PropertyData propertyData;

    protected PropertyMenus(Player player, String title, int rows, PropertyData propertyData) {
        super(player, title, rows);
        this.propertyData = propertyData;
    }

    protected GuiItem getPropertyIcon() {
        return iconManager.get(propertyData.getIcon())
                .setName(propertyData.getName())
                .setDescription(propertyData.getBasicDescription(tanPlayer.getLang()))
                .asGuiItem(player);
    }

    protected GuiItem getRenameButton() {
        return iconManager.get(IconKey.PROPERTY_RENAME_ICON)
                .setName(Lang.GUI_PROPERTY_CHANGE_NAME.get(tanPlayer))
                .setDescription(
                        Lang.GUI_PROPERTY_CHANGE_NAME_DESC1.get(tanPlayer, propertyData.getName()),
                        Lang.GUI_GENERIC_CLICK_TO_RENAME.get(tanPlayer)
                )
                .setAction(action -> {
                    player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(tanPlayer));
                    PlayerChatListenerStorage.register(player, new ChangePropertyName(propertyData, p -> open()));
                })
                .asGuiItem(player);
    }

    protected GuiItem getDescriptionButton() {
        return iconManager.get(IconKey.PROPERTY_DESCRIPTION_ICON)
                .setName(Lang.GUI_PROPERTY_CHANGE_DESCRIPTION.get(tanPlayer))
                .setDescription(
                        Lang.GUI_PROPERTY_CHANGE_DESCRIPTION_DESC1.get(tanPlayer, propertyData.getDescription()),
                        Lang.GUI_GENERIC_CLICK_TO_RENAME.get(tanPlayer)
                )
                .setAction(action -> {
                    player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(tanPlayer));
                    PlayerChatListenerStorage.register(player, new ChangePropertyDescription(propertyData, p -> open()));
                })
                .asGuiItem(player);
    }

    protected GuiItem getBoundariesButton() {
        return iconManager.get(IconKey.PROPERTY_BOUNDS_ICON)
                .setName(Lang.GUI_PROPERTY_DRAWN_BOX.get(tanPlayer))
                .setDescription(
                        Lang.GUI_GENERIC_CLICK_TO_SHOW.get(tanPlayer)
                )
                .setAction(action -> {
                    player.closeInventory();
                    propertyData.showBox(player);
                })
                .asGuiItem(player);
    }

    protected GuiItem forSaleButton() {

        IconKey iconKey = propertyData.isForSale() ? IconKey.SELL_PROPERTY_ICON_FOR_SALE : IconKey.SELL_PROPERTY_ICON_NOT_FOR_SALE;
        Lang name = propertyData.isForSale() ? Lang.GUI_PROPERTY_FOR_SALE : Lang.GUI_PROPERTY_NOT_FOR_SALE;

        double price = propertyData.getSalePrice();
        double taxPrice = NumberUtil.roundWithDigits(price * propertyData.getTown().getTaxOnBuyingProperty());
        double total = NumberUtil.roundWithDigits(price + taxPrice);

        return iconManager.get(iconKey)
                .setName(name.get(tanPlayer))
                .setDescription(
                        Lang.GUI_BUYING_PRICE.get(tanPlayer, total, price, taxPrice),
                        Lang.GUI_TOWN_RATE.get(tanPlayer, String.format("%.2f", propertyData.getTown().getTaxOnBuyingProperty() * 100)),
                        Lang.GUI_LEFT_CLICK_TO_SWITCH_SALE.get(tanPlayer),
                        Lang.GUI_RIGHT_CLICK_TO_CHANGE_PRICE.get(tanPlayer)
                )
                .setAction(event -> {
                    if (event.getClick() == ClickType.RIGHT) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(tanPlayer));
                        PlayerChatListenerStorage.register(player, new ChangePropertySalePrice(propertyData, p -> open()));
                        player.closeInventory();
                    } else if (event.getClick() == ClickType.LEFT) {
                        if (propertyData.isRented()) {
                            player.sendMessage(TanChatUtils.getTANString() + Lang.PROPERTY_ALREADY_RENTED.get(tanPlayer));
                            return;
                        }
                        propertyData.swapIsForSale();
                        open();
                    }
                })
                .asGuiItem(player);
    }

    protected GuiItem forRentButton() {

        IconKey iconKey = propertyData.isForRent() ? IconKey.RENT_PROPERTY_ICON_FOR_RENT : IconKey.RENT_PROPERTY_ICON_NOT_FOR_RENT;
        Lang name = propertyData.isForRent() ? Lang.GUI_PROPERTY_FOR_RENT : Lang.GUI_PROPERTY_NOT_FOR_RENT;

        double price = propertyData.getBaseRentPrice();
        double taxPrice = NumberUtil.roundWithDigits(price * propertyData.getTown().getTaxOnRentingProperty());
        double total = propertyData.getRentPrice();

        return iconManager.get(iconKey)
                .setName(name.get(tanPlayer))
                .setDescription(
                        Lang.GUI_RENTING_PRICE.get(tanPlayer, total, price, taxPrice),
                        Lang.GUI_TOWN_RATE.get(tanPlayer, String.format("%.2f", propertyData.getTown().getTaxOnRentingProperty() * 100)),
                        Lang.GUI_LEFT_CLICK_TO_SWITCH_SALE.get(tanPlayer),
                        Lang.GUI_RIGHT_CLICK_TO_CHANGE_PRICE.get(tanPlayer)
                )
                .setAction(event -> {
                    if (event.getClick() == ClickType.RIGHT) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(tanPlayer));
                        PlayerChatListenerStorage.register(player, new ChangePropertyRentPrice(propertyData, p -> open()));
                    } else if (event.getClick() == ClickType.LEFT) {
                        if (propertyData.isRented()) {
                            player.sendMessage(TanChatUtils.getTANString() + Lang.PROPERTY_ALREADY_RENTED.get(tanPlayer));
                            return;
                        }
                        propertyData.swapIsRent();
                        open();
                    }
                })
                .asGuiItem(player);
    }

    protected GuiItem getDeleteButton() {
        return iconManager.get(IconKey.DELETE_PROPERTY_ICON)
                .setName(Lang.GUI_PROPERTY_DELETE_PROPERTY.get(tanPlayer))
                .setDescription(Lang.GUI_GENERIC_CLICK_TO_PROCEED.get(tanPlayer))
                .setAction(event ->
                        PlayerGUI.openConfirmMenu(player, Lang.GUI_PROPERTY_DELETE_PROPERTY_CONFIRM.get(tanPlayer, propertyData.getName()),
                                p -> {
                                    propertyData.delete();
                                    player.closeInventory();
                                },
                                p -> open()
                        ))
                .asGuiItem(player);
    }

    protected GuiItem getAuthorizedPlayersButton() {
        return iconManager.get(IconKey.AUTHORIZED_PLAYERS_ICON)
                .setName(Lang.GUI_PROPERTY_PLAYER_LIST.get(tanPlayer))
                .setDescription(
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get()
                )
                .setAction(event -> new PropertyChunkSettingsMenu(player, propertyData, this))
                .asGuiItem(player);
    }

    protected GuiItem getKickRenterButton() {
        return iconManager.get(new ItemStack(HeadUtils.getPlayerHead(propertyData.getOfflineRenter())))
                .setName(Lang.GUI_PROPERTY_RENTED_BY.get(tanPlayer, propertyData.getRenter().getNameStored()))
                .setDescription(Lang.GUI_PROPERTY_RIGHT_CLICK_TO_EXPEL_RENTER.get(tanPlayer))
                .setAction(event -> {
                    event.setCancelled(true);

                    Player renter = propertyData.getRenterPlayer();
                    propertyData.expelRenter(false);

                    player.sendMessage(TanChatUtils.getTANString() + Lang.PROPERTY_RENTER_EXPELLED_OWNER_SIDE.get(tanPlayer));
                    SoundUtil.playSound(player, MINOR_GOOD);

                    if (renter != null) {
                        renter.sendMessage(TanChatUtils.getTANString() + Lang.PROPERTY_RENTER_EXPELLED_RENTER_SIDE.get(tanPlayer, propertyData.getName()));
                        SoundUtil.playSound(renter, MINOR_BAD);
                    }
                    open();
                })
                .asGuiItem(player);
    }

}
