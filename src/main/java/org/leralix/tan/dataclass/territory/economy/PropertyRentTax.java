package org.leralix.tan.dataclass.territory.economy;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.RateType;
import org.leralix.tan.listeners.chat.events.SetSpecificRate;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.SoundUtil;
import org.leralix.tan.utils.StringUtil;

import static org.leralix.tan.enums.RolePermission.MANAGE_TAXES;
import static org.leralix.tan.enums.SoundEnum.ADD;
import static org.leralix.tan.enums.SoundEnum.REMOVE;
import static org.leralix.tan.utils.ChatUtils.getTANString;

public class PropertyRentTax extends ProfitLine{

    double taxes = 0;

    public PropertyRentTax(TownData townData){
        super(townData);
        for(PropertyData propertyData : townData.getPropertyDataList()){
            taxes += propertyData.getRentPrice() * townData.getTaxOnRentingProperty() / 100;
        }
    }

    @Override
    public double getMoney() {
        return taxes;
    }

    @Override
    public String getLine() {
        return Lang.PROPERTY_TAX_LINE.get(StringUtil.getColoredMoney(getMoney()));
    }

    @Override
    public void addItems(Gui gui, Player player) {

        PlayerData playerData = PlayerDataStorage.get(player);

        ItemStack lowerTax = HeadUtils.makeSkullURL(Lang.GUI_TREASURY_LOWER_TAX.get(),"https://textures.minecraft.net/texture/a9dbed522e8de1a681dddd37854ee4267efc48b59917f9a9acb420d6fdb9",
                Lang.GUI_DECREASE_1PERCENT_DESC.get(),
                Lang.GUI_DECREASE_10PERCENT_DESC.get());
        ItemStack increaseTax = HeadUtils.makeSkullURL(Lang.GUI_TREASURY_INCREASE_TAX.get(),"https://textures.minecraft.net/texture/bf6b85f626444dbd5bddf7a521fe52748fe43564e03fbd35b6b5e797de942d",
                Lang.GUI_INCREASE_1PERCENT_DESC.get(),
                Lang.GUI_INCREASE_10PERCENT_DESC.get());
        ItemStack tax = HeadUtils.makeSkullURL(Lang.GUI_TREASURY_RENT_PROPERTY_TAX.get(),"https://textures.minecraft.net/texture/e19997593f2c592b9fbd4f15ead1673b76f519d7ab3efa15edd19448d1a20bfc",
                Lang.GUI_TREASURY_PROPERTY_RENT_TAX_DESC1.get(String.format("%.2f", territoryData.getTaxOnRentingProperty() * 100)),
                Lang.GUI_GENERIC_CLICK_TO_OPEN_HISTORY.get(),
                Lang.RIGHT_CLICK_TO_SET_TAX.get());


        GuiItem lowerTaxButton = ItemBuilder.from(lowerTax).asGuiItem(event -> {
            event.setCancelled(true);
            if(!territoryData.doesPlayerHavePermission(playerData, MANAGE_TAXES)) {
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }

            double currentTax = territoryData.getTaxOnRentingProperty();
            double amountToRemove = event.isShiftClick() ? 0.1 : 0.01;

            if(currentTax - amountToRemove < 0){
                player.sendMessage(getTANString() + Lang.GUI_TREASURY_CANT_TAX_LESS_PERCENT.get());
                return;
            }
            SoundUtil.playSound(player, REMOVE);

            territoryData.addToRentTax(-amountToRemove);
            PlayerGUI.openTreasury(player, territoryData);
        });
        GuiItem taxInfo = ItemBuilder.from(tax).asGuiItem(event -> {
            event.setCancelled(true);
            if(event.isLeftClick()){
                PlayerGUI.openTownEconomicsHistory(player, territoryData, TransactionHistoryEnum.PROPERTY_RENT_TAX);
            }
            else if(event.isRightClick()){
                player.sendMessage(getTANString() + Lang.TOWN_SET_TAX_IN_CHAT.get());
                PlayerChatListenerStorage.register(player, new SetSpecificRate(territoryData, RateType.RENT));
                player.closeInventory();
            }        
        });
        GuiItem increaseTaxButton = ItemBuilder.from(increaseTax).asGuiItem(event -> {
            event.setCancelled(true);

            if(!territoryData.doesPlayerHavePermission(playerData, MANAGE_TAXES)){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }

            double currentTax = territoryData.getTaxOnRentingProperty();
            double amountToAdd = event.isShiftClick() ? 0.1 : 0.01;

            if(currentTax + amountToAdd > 1){
                player.sendMessage(getTANString() + Lang.GUI_TREASURY_CANT_TAX_MORE_PERCENT.get());
                return;
            }


            territoryData.addToRentTax(amountToAdd);
            SoundUtil.playSound(player, ADD);
            PlayerGUI.openTreasury(player, territoryData);
        });

        gui.setItem(4, 2, lowerTaxButton);
        gui.setItem(4, 3, taxInfo);
        gui.setItem(4, 4, increaseTaxButton);
    }

    @Override
    public boolean isRecurrent() {
        return true;
    }
}
