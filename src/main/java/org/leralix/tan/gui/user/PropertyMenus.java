package org.leralix.tan.gui.user;

import dev.triumphteam.gui.builder.item.ItemBuilder;
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
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.TanChatUtils;

import static org.leralix.lib.data.SoundEnum.MINOR_BAD;
import static org.leralix.lib.data.SoundEnum.MINOR_GOOD;

public abstract class PropertyMenus extends BasicGui {

    protected final PropertyData propertyData;

    protected PropertyMenus(Player player, Lang title, int rows, PropertyData propertyData) {
        super(player, title, rows);
        this.propertyData = propertyData;
    }

    protected GuiItem getPropertyIcon() {
        return ItemBuilder.from(propertyData.getIcon(playerData.getLang())).asGuiItem();
    }

    protected GuiItem getRenameButton(){
        return iconManager.get(IconKey.PROPERTY_RENAME_ICON)
                .setName(Lang.GUI_PROPERTY_CHANGE_NAME.get(playerData))
                .setDescription(Lang.GUI_PROPERTY_CHANGE_NAME_DESC1.get(playerData, propertyData.getName()))
                .setAction(action -> {
                    player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(playerData));
                    PlayerChatListenerStorage.register(player, new ChangePropertyName(propertyData, p -> open()));
                })
                .asGuiItem(player);
    }

    protected GuiItem getDescriptionButton() {
        return iconManager.get(IconKey.PROPERTY_DESCRIPTION_ICON)
                .setName(Lang.GUI_PROPERTY_CHANGE_DESCRIPTION.get(playerData))
                .setDescription(Lang.GUI_PROPERTY_CHANGE_DESCRIPTION_DESC1.get(playerData, propertyData.getDescription()))
                .setAction(action -> {
                    player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(playerData));
                    PlayerChatListenerStorage.register(player, new ChangePropertyDescription(propertyData, p -> open()));
                })
                .asGuiItem(player);
    }

    protected GuiItem getBoundariesButton() {
        return iconManager.get(IconKey.PROPERTY_BOUNDS_ICON)
                .setName(Lang.GUI_PROPERTY_DRAWN_BOX.get(playerData))
                .setDescription(Lang.GUI_PROPERTY_DRAWN_BOX_DESC1.get(playerData))
                .setAction(action -> {
                    player.closeInventory();
                    propertyData.showBox(player);
                })
                .asGuiItem(player);
    }

    protected GuiItem forSaleButton() {

        IconKey iconKey = propertyData.isForSale() ? IconKey.SELL_PROPERTY_ICON_FOR_SALE : IconKey.SELL_PROPERTY_ICON_NOT_FOR_SALE;

        return iconManager.get(iconKey)
                .setName(Lang.GUI_PROPERTY_FOR_SALE.get(playerData))
                .setDescription(
                        Lang.GUI_BUYING_PRICE.get(playerData, propertyData.getSalePrice()),
                        Lang.GUI_TOWN_RATE.get(playerData, String.format("%.2f", propertyData.getTerritory().getTaxOnBuyingProperty() * 100)),
                        Lang.GUI_LEFT_CLICK_TO_SWITCH_SALE.get(playerData),
                        Lang.GUI_RIGHT_CLICK_TO_CHANGE_PRICE.get(playerData)
                )
                .setAction(event -> {
                    if (event.getClick() == ClickType.RIGHT) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(playerData));
                        PlayerChatListenerStorage.register(player, new ChangePropertySalePrice(propertyData, p -> open()));
                        player.closeInventory();
                    } else if (event.getClick() == ClickType.LEFT) {
                        if (propertyData.isRented()) {
                            player.sendMessage(TanChatUtils.getTANString() + Lang.PROPERTY_ALREADY_RENTED.get(playerData));
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

        return iconManager.get(iconKey)
                .setName(Lang.GUI_PROPERTY_FOR_RENT.get(playerData))
                .setDescription(
                        Lang.GUI_RENTING_PRICE.get(playerData, propertyData.getRentPrice()),
                        Lang.GUI_TOWN_RATE.get(playerData, String.format("%.2f", propertyData.getTerritory().getTaxOnRentingProperty()*100)),
                        Lang.GUI_LEFT_CLICK_TO_SWITCH_SALE.get(playerData),
                        Lang.GUI_RIGHT_CLICK_TO_CHANGE_PRICE.get(playerData)
                )
                .setAction(event -> {
                    if(event.getClick() == ClickType.RIGHT){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(playerData));
                        PlayerChatListenerStorage.register(player, new ChangePropertyRentPrice(propertyData, p -> open()));
                    }
                    else if (event.getClick() == ClickType.LEFT){
                        if(propertyData.isRented()){
                            player.sendMessage(TanChatUtils.getTANString() + Lang.PROPERTY_ALREADY_RENTED.get(playerData));
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
                .setName(Lang.GUI_PROPERTY_DELETE_PROPERTY.get(playerData))
                .setDescription(Lang.GUI_PROPERTY_DELETE_PROPERTY_DESC1.get(playerData))
                .setAction(event -> {
                    propertyData.delete();
                    new PlayerPropertyMenu(player).open();
                })
                .asGuiItem(player);
    }

    protected GuiItem getAuthorizedPlayersButton() {
        return iconManager.get(IconKey.AUTHORIZED_PLAYERS_ICON)
                .setName(Lang.GUI_PROPERTY_PLAYER_LIST.get(playerData))
                .setDescription(Lang.GUI_PROPERTY_PLAYER_LIST_DESC1.get(playerData))
                .setAction(event -> PlayerGUI.openPlayerPropertyPlayerList(player, propertyData, 0, p -> open()))
                .asGuiItem(player);
    }

    protected GuiItem getKickRenterButton() {
        return iconManager.get(new ItemStack(HeadUtils.getPlayerHead(propertyData.getOfflineRenter())))
                .setName(Lang.GUI_PROPERTY_RENTED_BY.get(playerData, propertyData.getRenter().getNameStored()))
                .setDescription(Lang.GUI_PROPERTY_RIGHT_CLICK_TO_EXPEL_RENTER.get(playerData))
                .setAction(event -> {
                    event.setCancelled(true);

                    Player renter = propertyData.getRenterPlayer();
                    propertyData.expelRenter(false);

                    player.sendMessage(TanChatUtils.getTANString() + Lang.PROPERTY_RENTER_EXPELLED_OWNER_SIDE.get(playerData));
                    SoundUtil.playSound(player,MINOR_GOOD);

                    if(renter != null){
                        renter.sendMessage(TanChatUtils.getTANString() + Lang.PROPERTY_RENTER_EXPELLED_RENTER_SIDE.get(playerData, propertyData.getName()));
                        SoundUtil.playSound(renter,MINOR_BAD);
                    }
                    open();
                })
                .asGuiItem(player);
    }

}
