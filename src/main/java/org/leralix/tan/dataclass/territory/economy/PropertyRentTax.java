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
            taxes += propertyData.getRentPrice() * townData.getTaxOnRentingProperty();
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

        ItemStack lowerTax = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_LOWER_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=",
                Lang.GUI_DECREASE_1PERCENT_DESC.get(),
                Lang.GUI_DECREASE_10PERCENT_DESC.get());
        ItemStack increaseTax = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_INCREASE_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                Lang.GUI_INCREASE_1PERCENT_DESC.get(),
                Lang.GUI_INCREASE_10PERCENT_DESC.get());
        ItemStack tax = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_RENT_PROPERTY_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTk4ZGY0MmY0NzdmMjEzZmY1ZTlkN2ZhNWE0Y2M0YTY5ZjIwZDljZWYyYjkwYzRhZTRmMjliZDE3Mjg3YjUifX19",
                Lang.GUI_TREASURY_PROPERTY_RENT_TAX_DESC1.get(String.format("%.2f", territoryData.getTaxOnRentingProperty())),
                Lang.GUI_GENERIC_CLICK_TO_OPEN_HISTORY.get());


        GuiItem lowerTaxButton = ItemBuilder.from(lowerTax).asGuiItem(event -> {
            event.setCancelled(true);
            if(!territoryData.doesPlayerHavePermission(playerData, MANAGE_TAXES)) {
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }

            double currentTax = territoryData.getTaxOnRentingProperty();
            int amountToRemove = event.isShiftClick() && currentTax > 9 ? 10 : 1;

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
            PlayerGUI.openTownEconomicsHistory(player, territoryData, TransactionHistoryEnum.PROPERTY_RENT_TAX);
        });
        GuiItem increaseTaxButton = ItemBuilder.from(increaseTax).asGuiItem(event -> {
            event.setCancelled(true);

            if(!territoryData.doesPlayerHavePermission(playerData, MANAGE_TAXES)){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }

            double currentTax = territoryData.getTaxOnRentingProperty();
            int amountToAdd = event.isShiftClick() ? 10 : 1;

            if(currentTax + amountToAdd > 100){
                player.sendMessage(getTANString() + Lang.GUI_TREASURY_CANT_TAX_MORE_PERCENT.get());
                return;
            }


            territoryData.addToRentTax(amountToAdd);
            SoundUtil.playSound(player, ADD);
            PlayerGUI.openTreasury(player, territoryData);
        });

        gui.setItem(5, 2, lowerTaxButton);
        gui.setItem(5, 3, taxInfo);
        gui.setItem(5, 4, increaseTaxButton);
    }
}
