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
                .setName(Lang.GUI_PROPERTY_CHANGE_NAME.get(langType))
                .setDescription(
                        Lang.GUI_PROPERTY_CHANGE_NAME_DESC1.get(langType, propertyData.getName()),
                        Lang.GUI_GENERIC_CLICK_TO_RENAME.get(langType)
                )
                .setAction(action -> {
                    player.sendMessage(Lang.ENTER_NEW_VALUE.get(langType));
                    PlayerChatListenerStorage.register(player, new ChangePropertyName(propertyData, p -> open()));
                })
                .asGuiItem(player);
    }

    protected GuiItem getDescriptionButton() {
        return iconManager.get(IconKey.PROPERTY_DESCRIPTION_ICON)
                .setName(Lang.GUI_PROPERTY_CHANGE_DESCRIPTION.get(langType))
                .setDescription(
                        Lang.GUI_PROPERTY_CHANGE_DESCRIPTION_DESC1.get(langType, propertyData.getDescription()),
                        Lang.GUI_GENERIC_CLICK_TO_RENAME.get(langType)
                )
                .setAction(action -> {
                    player.sendMessage(Lang.ENTER_NEW_VALUE.get(langType));
                    PlayerChatListenerStorage.register(player, new ChangePropertyDescription(propertyData, p -> open()));
                })
                .asGuiItem(player);
    }

    protected GuiItem getBoundariesButton() {
        return iconManager.get(IconKey.PROPERTY_BOUNDS_ICON)
                .setName(Lang.GUI_PROPERTY_DRAWN_BOX.get(langType))
                .setDescription(
                        Lang.GUI_GENERIC_CLICK_TO_SHOW.get(langType)
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
                .setName(name.get(langType))
                .setDescription(
                        Lang.GUI_BUYING_PRICE.get(langType, Double.toString(total), Double.toString(price), Double.toString(taxPrice)),
                        Lang.GUI_TOWN_RATE.get(langType, String.format("%.2f", propertyData.getTown().getTaxOnBuyingProperty() * 100)),
                        Lang.GUI_LEFT_CLICK_TO_SWITCH_SALE.get(langType),
                        Lang.GUI_RIGHT_CLICK_TO_CHANGE_PRICE.get(langType)
                )
                .setAction(event -> {
                    if (event.getClick() == ClickType.RIGHT) {
                        player.sendMessage(Lang.ENTER_NEW_VALUE.get(langType));
                        PlayerChatListenerStorage.register(player, new ChangePropertySalePrice(propertyData, p -> open()));
                    } else if (event.getClick() == ClickType.LEFT) {
                        if (propertyData.isRented()) {
                            player.sendMessage(Lang.PROPERTY_ALREADY_RENTED.get(langType));
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
                .setName(name.get(langType))
                .setDescription(
                        Lang.GUI_BUYING_PRICE.get(langType, Double.toString(total), Double.toString(price), Double.toString(taxPrice)),
                        Lang.GUI_TOWN_RATE.get(langType, String.format("%.2f", propertyData.getTown().getTaxOnRentingProperty() * 100)),
                        Lang.GUI_LEFT_CLICK_TO_SWITCH_SALE.get(langType),
                        Lang.GUI_RIGHT_CLICK_TO_CHANGE_PRICE.get(langType)
                )
                .setAction(event -> {
                    if (event.getClick() == ClickType.RIGHT) {
                        player.sendMessage(Lang.ENTER_NEW_VALUE.get(langType));
                        PlayerChatListenerStorage.register(player, new ChangePropertyRentPrice(propertyData, p -> open()));
                    } else if (event.getClick() == ClickType.LEFT) {
                        if (propertyData.isRented()) {
                            player.sendMessage(Lang.PROPERTY_ALREADY_RENTED.get(langType));
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
                .setName(Lang.GUI_PROPERTY_DELETE_PROPERTY.get(langType))
                .setDescription(Lang.GUI_GENERIC_CLICK_TO_PROCEED.get(langType))
                .setAction(event ->
                        PlayerGUI.openConfirmMenu(player, Lang.GUI_PROPERTY_DELETE_PROPERTY_CONFIRM.get(langType, propertyData.getName()),
                                p -> {
                                    propertyData.delete();
                                    player.closeInventory();
                                },
                                p -> open()
                        ))
                .asGuiItem(player);
    }

    protected GuiItem getAuthorizedPlayersButton() {

        boolean isRentedAndPlayerIsNotRenter = propertyData.isRented() && !propertyData.getRenter().equals(tanPlayer);


        return iconManager.get(IconKey.AUTHORIZED_PLAYERS_ICON)
                .setName(Lang.GUI_PROPERTY_PLAYER_LIST.get(langType))
                .setDescription(
                        isRentedAndPlayerIsNotRenter ?
                                Lang.CANNOT_MANAGE_AUTHORIZED_PLAYER_IF_PROPERTY_IS_RENTED.get(langType) :
                                Lang.GUI_GENERIC_CLICK_TO_OPEN.get(langType)
                )
                .setAction(event -> {
                    if (isRentedAndPlayerIsNotRenter) {
                        player.sendMessage(Lang.CANNOT_MANAGE_AUTHORIZED_PLAYER_IF_PROPERTY_IS_RENTED.get(langType));
                        return;
                    }
                    new PropertyChunkSettingsMenu(player, propertyData, this);

                })
                .asGuiItem(player);
    }

    protected GuiItem getKickRenterButton() {
        return iconManager.get(new ItemStack(HeadUtils.getPlayerHead(propertyData.getOfflineRenter())))
                .setName(Lang.GUI_PROPERTY_RENTED_BY.get(langType, propertyData.getRenter().getNameStored()))
                .setDescription(Lang.GUI_PROPERTY_RIGHT_CLICK_TO_EXPEL_RENTER.get(langType))
                .setAction(event -> {
                    event.setCancelled(true);

                    Player renter = propertyData.getRenterPlayer();
                    propertyData.expelRenter(false);

                    player.sendMessage(Lang.PROPERTY_RENTER_EXPELLED_OWNER_SIDE.get(langType));
                    SoundUtil.playSound(player, MINOR_GOOD);

                    if (renter != null) {
                        renter.sendMessage(Lang.PROPERTY_RENTER_EXPELLED_RENTER_SIDE.get(langType, propertyData.getName()));
                        SoundUtil.playSound(renter, MINOR_BAD);
                    }
                    open();
                })
                .asGuiItem(player);
    }

}
