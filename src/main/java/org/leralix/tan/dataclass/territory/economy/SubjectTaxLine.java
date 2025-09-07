package org.leralix.tan.dataclass.territory.economy;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.user.territory.EconomicHistoryMenu;
import org.leralix.tan.gui.user.territory.TreasuryMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.treasury.SetTerritoryTax;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.StringUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class SubjectTaxLine extends ProfitLine {

    double actualTaxes = 0;
    double missingTaxes = 0;

    public SubjectTaxLine(RegionData regionData) {
        super(regionData);
        double tax = regionData.getTax();
        for (TerritoryData townData : regionData.getVassals()) {
            if (townData.getBalance() > tax)
                actualTaxes += tax;
            else
                missingTaxes += tax;
        }
    }

    @Override
    protected double getMoney() {
        return actualTaxes;
    }

    @Override
    public String getLine() {
        if (missingTaxes > 0)
            return Lang.PLAYER_TAX_MISSING_LINE.get(StringUtil.getColoredMoney(getMoney()), missingTaxes);
        else
            return Lang.PLAYER_TAX_LINE.get(StringUtil.getColoredMoney(getMoney()));
    }

    @Override
    public void addItems(Gui gui, Player player) {
        double taxRate = territoryData.getTax();

        ItemStack lowerTax = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_LOWER_TAX.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=",
                Lang.GUI_DECREASE_1_DESC.get(),
                Lang.GUI_DECREASE_10_DESC.get());
        ItemStack increaseTax = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_INCREASE_TAX.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                Lang.GUI_INCREASE_1_DESC.get(),
                Lang.GUI_INCREASE_10_DESC.get());
        ItemStack tax = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_FLAT_TAX.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTk4ZGY0MmY0NzdmMjEzZmY1ZTlkN2ZhNWE0Y2M0YTY5ZjIwZDljZWYyYjkwYzRhZTRmMjliZDE3Mjg3YjUifX19",
                Lang.GUI_TREASURY_FLAT_TAX_DESC1.get(taxRate),
                Lang.GUI_GENERIC_CLICK_TO_OPEN_HISTORY.get(),
                Lang.RIGHT_CLICK_TO_SET_TAX.get());


        GuiItem lowerTaxButton = ItemBuilder.from(lowerTax).asGuiItem(event -> {
            event.setCancelled(true);
            if (!territoryData.doesPlayerHavePermission(player, RolePermission.MANAGE_TAXES)) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }
            int amountToRemove = event.isShiftClick() && taxRate > 10 ? 10 : 1;
            if (taxRate < 1) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TREASURY_CANT_TAX_LESS.get());
                return;
            }
            SoundUtil.playSound(player, SoundEnum.REMOVE);

            territoryData.addToTax(-amountToRemove);
            new TreasuryMenu(player, territoryData);
        });

        GuiItem increaseTaxButton = ItemBuilder.from(increaseTax).asGuiItem(event -> {
            if (!territoryData.doesPlayerHavePermission(player, RolePermission.MANAGE_TAXES)) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }
            event.setCancelled(true);
            int amountToRemove = event.isShiftClick() && taxRate >= 10 ? 10 : 1;

            SoundUtil.playSound(player, SoundEnum.ADD);

            territoryData.addToTax(amountToRemove);
            new TreasuryMenu(player, territoryData);
        });

        GuiItem taxInfo = ItemBuilder.from(tax).asGuiItem(event -> {
            if (!territoryData.doesPlayerHavePermission(player, RolePermission.MANAGE_TAXES)) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }
            event.setCancelled(true);
            if (event.isLeftClick()) {
                new EconomicHistoryMenu(player, territoryData, TransactionHistoryEnum.SUBJECT_TAX);
            } else if (event.isRightClick()) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_SET_TAX_IN_CHAT.get());
                PlayerChatListenerStorage.register(player, new SetTerritoryTax(territoryData, p -> new TreasuryMenu(player, territoryData)));
                player.closeInventory();
            }
        });

        gui.setItem(2, 2, lowerTaxButton);
        gui.setItem(2, 3, taxInfo);
        gui.setItem(2, 4, increaseTaxButton);

    }

    @Override
    public boolean isRecurrent() {
        return true;
    }
}
