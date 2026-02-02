package org.leralix.tan.gui.user.property;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.RenterPropertyMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.GuiUtil;

public class BuyOrRentPropertyMenu extends PropertyMenus {

    public BuyOrRentPropertyMenu(Player player, PropertyData propertyData){
        super(player, Lang.HEADER_PLAYER_SPECIFIC_PROPERTY.get(propertyData.getName()), 3, propertyData);
        open();
    }


    @Override
    public void open() {

        gui.setItem(1,5, getPropertyIcon());
        gui.getFiller().fillTop(GuiUtil.getUnnamedItem(Material.BROWN_STAINED_GLASS_PANE));


        if(propertyData.isForSale()){
            gui.setItem(2,3, getConfirmBuyButton());
            gui.setItem(2,7, getCancelBuyButton());
        }
        if(propertyData.isForRent()){
            gui.setItem(2,3, getConfirmRentButton());
            gui.setItem(2,7, getCancelRentButton());
        }

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, HumanEntity::closeInventory, langType));

        gui.open(player);
    }

    private GuiItem getConfirmBuyButton() {

        double price = propertyData.getPriceWithTax();

        return iconManager.get(IconKey.CONFIRM_BUY_PROPERTY_ICON)
                .setName(Lang.CONFIRM_SALE.get(tanPlayer))
                .setDescription(Lang.CONFIRM_SALE_DESC1.get(Double.toString(price)))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction(action -> {
                    propertyData.buyProperty(player, tanPlayer);
                    new PlayerPropertyManager(player, propertyData, p -> player.closeInventory());
                })
                .asGuiItem(player, langType);
    }

    private GuiItem getCancelBuyButton() {
        return iconManager.get(IconKey.CANCEL_BUY_PROPERTY_ICON)
                .setName(Lang.CANCEL_SALE.get(tanPlayer))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction(action -> player.closeInventory())
                .asGuiItem(player, langType);
    }

    private GuiItem getConfirmRentButton() {

        double price = propertyData.getRentPriceWithTax();

        return iconManager.get(IconKey.CONFIRM_RENT_PROPERTY_ICON)
                .setName(Lang.CONFIRM_RENT.get(tanPlayer))
                .setDescription(Lang.CONFIRM_RENT_DESC1.get(Double.toString(price)))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction(event -> {
                    propertyData.allocateRenter(player);
                    new RenterPropertyMenu(player, propertyData);
                })
                .asGuiItem(player, langType);
    }

    private GuiItem getCancelRentButton() {
        return iconManager.get(IconKey.CANCEL_RENT_PROPERTY_ICON)
                .setName(Lang.CANCEL_RENT.get(tanPlayer))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction(action -> player.closeInventory())
                .asGuiItem(player, langType);
    }


}
