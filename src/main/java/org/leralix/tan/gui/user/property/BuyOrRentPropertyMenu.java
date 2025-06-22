package org.leralix.tan.gui.user.property;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.RenterPropertyMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.GuiUtil;

public class BuyOrRentPropertyMenu extends PropertyMenus {

    public BuyOrRentPropertyMenu(Player player, PropertyData propertyData){
        super(player, Lang.HEADER_PLAYER_SPECIFIC_PROPERTY.get(player, propertyData.getName()), 3, propertyData);
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

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, HumanEntity::closeInventory));

        gui.open(player);
    }

    private GuiItem getConfirmBuyButton() {

        double price = propertyData.getSalePrice();

        return iconManager.get(IconKey.CONFIRM_BUY_PROPERTY_ICON)
                .setName(Lang.CONFIRM_SALE.get(ITanPlayer))
                .setDescription(
                        Lang.CONFIRM_SALE_DESC1.get(ITanPlayer, price),
                        Lang.GUI_GENERIC_CLICK_TO_PROCEED.get(ITanPlayer)
                )
                .setAction(action -> {
                    propertyData.buyProperty(player);
                    new PlayerPropertyManager(player, propertyData, p -> player.closeInventory());
                })
                .asGuiItem(player);
    }

    private GuiItem getCancelBuyButton() {
        return iconManager.get(IconKey.CANCEL_BUY_PROPERTY_ICON)
                .setName(Lang.CANCEL_SALE.get(ITanPlayer))
                .setDescription(
                        Lang.GUI_GENERIC_CLICK_TO_PROCEED.get(ITanPlayer)
                )
                .setAction(action -> player.closeInventory())
                .asGuiItem(player);
    }

    private GuiItem getConfirmRentButton() {

        double price = propertyData.getRentPrice();

        return iconManager.get(IconKey.CONFIRM_RENT_PROPERTY_ICON)
                .setName(Lang.CONFIRM_RENT.get(ITanPlayer))
                .setDescription(
                        Lang.CONFIRM_RENT_DESC1.get(ITanPlayer, price),
                        Lang.GUI_GENERIC_CLICK_TO_PROCEED.get(ITanPlayer)
                )
                .setAction(event -> {
                    propertyData.allocateRenter(player);
                    new RenterPropertyMenu(player, propertyData);
                })
                .asGuiItem(player);
    }

    private GuiItem getCancelRentButton() {
        return iconManager.get(IconKey.CANCEL_RENT_PROPERTY_ICON)
                .setName(Lang.CANCEL_RENT.get(ITanPlayer))
                .setDescription(
                        Lang.GUI_GENERIC_CLICK_TO_PROCEED.get(ITanPlayer)
                )
                .setAction(action -> player.closeInventory())
                .asGuiItem(player);
    }


}
