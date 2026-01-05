package org.leralix.tan.gui.user.property;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.ChangePropertyDescription;
import org.leralix.tan.listeners.chat.events.ChangePropertyName;
import org.leralix.tan.listeners.chat.events.ChangePropertyRentPrice;
import org.leralix.tan.listeners.chat.events.ChangePropertySalePrice;
import org.leralix.tan.utils.text.NumberUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import static org.leralix.lib.data.SoundEnum.MINOR_BAD;
import static org.leralix.lib.data.SoundEnum.MINOR_GOOD;

public abstract class PropertyMenus extends BasicGui {

    protected final PropertyData propertyData;

    protected PropertyMenus(Player player, FilledLang title, int rows, PropertyData propertyData) {
        super(player, title, rows);
        this.propertyData = propertyData;
    }

    protected GuiItem getPropertyIcon() {
        return iconManager.get(propertyData.getIcon())
                .setName(propertyData.getName())
                .setDescription(propertyData.getBasicDescription())
                .asGuiItem(player, langType);
    }

    protected GuiItem getRenameButton() {
        return iconManager.get(IconKey.PROPERTY_RENAME_ICON)
                .setName(Lang.GUI_PROPERTY_CHANGE_NAME.get(langType))
                .setDescription(Lang.GUI_PROPERTY_CHANGE_NAME_DESC1.get(propertyData.getName()))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_RENAME)
                .setAction(action -> {
                    TanChatUtils.message(player, Lang.ENTER_NEW_VALUE.get(langType));
                    PlayerChatListenerStorage.register(player, new ChangePropertyName(propertyData, p -> open()));
                })
                .asGuiItem(player, langType);
    }

    protected GuiItem getDescriptionButton() {
        return iconManager.get(IconKey.PROPERTY_DESCRIPTION_ICON)
                .setName(Lang.GUI_PROPERTY_CHANGE_DESCRIPTION.get(langType))
                .setDescription(Lang.GUI_PROPERTY_CHANGE_DESCRIPTION_DESC1.get(propertyData.getDescription()))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_RENAME)
                .setAction(action -> {
                    TanChatUtils.message(player, Lang.ENTER_NEW_VALUE.get(langType));
                    PlayerChatListenerStorage.register(player, new ChangePropertyDescription(propertyData, p -> open()));
                })
                .asGuiItem(player, langType);
    }

    protected GuiItem getBoundariesButton() {
        return iconManager.get(IconKey.PROPERTY_BOUNDS_ICON)
                .setName(Lang.GUI_PROPERTY_DRAWN_BOX.get(langType))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_SHOW)
                .setAction(action -> {
                    player.closeInventory();
                    propertyData.showBox(player);
                })
                .asGuiItem(player, langType);
    }

    protected GuiItem forSaleButton() {

        IconKey iconKey = propertyData.isForSale() ? IconKey.SELL_PROPERTY_ICON_FOR_SALE : IconKey.SELL_PROPERTY_ICON_NOT_FOR_SALE;
        Lang name = propertyData.isForSale() ? Lang.GUI_PROPERTY_FOR_SALE : Lang.GUI_PROPERTY_NOT_FOR_SALE;

        double price = propertyData.getPrice();
        double total = propertyData.getPriceWithTax();
        double taxPrice = total - price;

        return iconManager.get(iconKey)
                .setName(name.get(langType))
                .setDescription(
                        Lang.GUI_BUYING_PRICE.get(Double.toString(total), Double.toString(price), Double.toString(taxPrice)),
                        Lang.GUI_TOWN_RATE.get(String.format("%.2f", propertyData.getTown().getTaxOnBuyingProperty() * 100))
                )
                .setClickToAcceptMessage(
                        Lang.GUI_LEFT_CLICK_TO_SWITCH_SALE,
                        Lang.GUI_RIGHT_CLICK_TO_CHANGE_PRICE
                )
                .setAction(event -> {
                    if (event.getClick() == ClickType.RIGHT) {
                        TanChatUtils.message(player, Lang.ENTER_NEW_VALUE.get(langType));
                        PlayerChatListenerStorage.register(player, new ChangePropertySalePrice(propertyData, p -> open()));
                    } else if (event.getClick() == ClickType.LEFT) {
                        if (propertyData.isRented()) {
                            TanChatUtils.message(player, Lang.PROPERTY_ALREADY_RENTED.get(langType));
                            return;
                        }
                        propertyData.swapIsForSale();
                        open();
                    }
                })
                .asGuiItem(player, langType);
    }

    protected GuiItem forRentButton() {

        IconKey iconKey = propertyData.isForRent() ? IconKey.RENT_PROPERTY_ICON_FOR_RENT : IconKey.RENT_PROPERTY_ICON_NOT_FOR_RENT;
        Lang name = propertyData.isForRent() ? Lang.GUI_PROPERTY_FOR_RENT : Lang.GUI_PROPERTY_NOT_FOR_RENT;

        double price = propertyData.getRentPrice();
        double taxPrice = NumberUtil.roundWithDigits(price * propertyData.getTown().getTaxOnRentingProperty());
        double total = propertyData.getRentPriceWithTax();

        return iconManager.get(iconKey)
                .setName(name.get(langType))
                .setDescription(
                        Lang.GUI_RENTING_PRICE.get(Double.toString(total), Double.toString(price), Double.toString(taxPrice)),
                        Lang.GUI_TOWN_RATE.get(String.format("%.2f", propertyData.getTown().getTaxOnRentingProperty() * 100))
                )
                .setClickToAcceptMessage(
                        Lang.GUI_LEFT_CLICK_TO_SWITCH_SALE,
                        Lang.GUI_RIGHT_CLICK_TO_CHANGE_PRICE
                )
                .setAction(event -> {
                    if (event.getClick() == ClickType.RIGHT) {
                        TanChatUtils.message(player, Lang.ENTER_NEW_VALUE.get(langType));
                        PlayerChatListenerStorage.register(player, new ChangePropertyRentPrice(propertyData, p -> open()));
                    } else if (event.getClick() == ClickType.LEFT) {
                        if (propertyData.isRented()) {
                            TanChatUtils.message(player, Lang.PROPERTY_ALREADY_RENTED.get(langType));
                            return;
                        }
                        propertyData.swapIsRent();
                        open();
                    }
                })
                .asGuiItem(player, langType);
    }

    protected GuiItem getDeleteButton() {
        return iconManager.get(IconKey.DELETE_PROPERTY_ICON)
                .setName(Lang.GUI_PROPERTY_DELETE_PROPERTY.get(langType))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction(event ->
                        new ConfirmMenu(
                                player,
                                Lang.GUI_PROPERTY_DELETE_PROPERTY_CONFIRM.get(propertyData.getName()),
                                () -> {
                                    propertyData.delete();
                                    player.closeInventory();
                                },
                                this::open
                        )
                )
                .asGuiItem(player, langType);
    }

    protected GuiItem getAuthorizedPlayersButton() {

        boolean isRentedAndPlayerIsNotRenter = propertyData.isRented() && !propertyData.getRenter().equals(tanPlayer);

        return iconManager.get(IconKey.AUTHORIZED_PLAYERS_ICON)
                .setName(Lang.GUI_PROPERTY_PLAYER_LIST.get(langType))
                .setClickToAcceptMessage(
                        isRentedAndPlayerIsNotRenter ?
                                Lang.CANNOT_MANAGE_AUTHORIZED_PLAYER_IF_PROPERTY_IS_RENTED :
                                Lang.GUI_GENERIC_CLICK_TO_OPEN
                )
                .setAction(event -> {
                    if (isRentedAndPlayerIsNotRenter) {
                        TanChatUtils.message(player, Lang.CANNOT_MANAGE_AUTHORIZED_PLAYER_IF_PROPERTY_IS_RENTED.get(langType));
                        return;
                    }
                    new PropertyChunkSettingsMenu(player, propertyData, this);

                })
                .asGuiItem(player, langType);
    }

    protected GuiItem getKickRenterButton() {
        return iconManager.get(propertyData.getOfflineRenter())
                .setName(Lang.GUI_PROPERTY_RENTED_BY.get(langType, propertyData.getRenter().getNameStored()))
                .setDescription(Lang.GUI_PROPERTY_RIGHT_CLICK_TO_EXPEL_RENTER.get())
                .setAction(event -> {
                    event.setCancelled(true);

                    Player renter = propertyData.getRenterPlayer();
                    propertyData.expelRenter(false);

                    TanChatUtils.message(player, Lang.PROPERTY_RENTER_EXPELLED_OWNER_SIDE.get(langType), MINOR_GOOD);
                    TanChatUtils.message(renter, Lang.PROPERTY_RENTER_EXPELLED_RENTER_SIDE.get(renter, propertyData.getName()), MINOR_BAD);

                    open();
                })
                .asGuiItem(player, langType);
    }

}
