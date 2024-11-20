package org.leralix.tan.dataclass.territory.economy;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.enums.HistoryEnum;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.StringUtil;

public class SalaryPaymentLine extends ProfitLine {
    double totalSalaries;
    public SalaryPaymentLine(ITerritoryData territoryData) {
        super(territoryData);
        totalSalaries = 0;

        for(RankData rankData : territoryData.getAllRanks()){
            totalSalaries -= rankData.getSalary() * rankData.getPlayersID().size();
        }
    }

    @Override
    public double getMoney() {
        return totalSalaries;
    }

    @Override
    public String getLine() {
        return Lang.PLAYER_SALARY_LINE.get(StringUtil.getColoredMoney(getMoney()));
    }

    @Override
    public void addItems(Gui gui, Player player) {
        ItemStack salarySpending = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_SALARY_HISTORY.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjlhNjAwYWIwYTgzMDk3MDY1Yjk1YWUyODRmODA1OTk2MTc3NDYwOWFkYjNkYmQzYTRjYTI2OWQ0NDQwOTU1MSJ9fX0=",
                Lang.GUI_TREASURY_SALARY_HISTORY_DESC1.get(StringUtil.getColoredMoney(getMoney())),
                Lang.GUI_GENERIC_CLICK_TO_OPEN_HISTORY.get());
        GuiItem salaryHistoryButton = ItemBuilder.from(salarySpending).asGuiItem(event -> {
            PlayerGUI.openTownEconomicsHistory(player, territoryData, TransactionHistoryEnum.SALARY);
            event.setCancelled(true);
        });
        gui.setItem(2,6, salaryHistoryButton);

    }
}
