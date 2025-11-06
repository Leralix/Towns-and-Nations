package org.leralix.tan.dataclass.territory.economy;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.user.territory.EconomicHistoryMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.StringUtil;

public class SalaryPaymentLine extends ProfitLine {
  double totalSalaries;

  public SalaryPaymentLine(TerritoryData territoryData) {
    super(territoryData);
    totalSalaries = 0;

    for (RankData rankData : territoryData.getAllRanks()) {
      totalSalaries -= rankData.getSalary() * rankData.getPlayersID().size();
    }
  }

  @Override
  protected double getMoney() {
    return totalSalaries;
  }

  @Override
  public FilledLang getLine() {
    return Lang.PLAYER_SALARY_LINE.get(StringUtil.getColoredMoney(getMoney()));
  }

  @Override
  public void addItems(Gui gui, Player player, LangType lang) {
    ItemStack salarySpending =
        HeadUtils.makeSkullB64(
            Lang.GUI_TREASURY_SALARY_HISTORY.get(lang),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjlhNjAwYWIwYTgzMDk3MDY1Yjk1YWUyODRmODA1OTk2MTc3NDYwOWFkYjNkYmQzYTRjYTI2OWQ0NDQwOTU1MSJ9fX0=",
            Lang.GUI_TREASURY_SALARY_HISTORY_DESC1.get(
                lang, StringUtil.getColoredMoney(getMoney())),
            Lang.GUI_GENERIC_CLICK_TO_OPEN_HISTORY.get(lang));
    GuiItem salaryHistoryButton =
        ItemBuilder.from(salarySpending)
            .asGuiItem(
                event -> {
                  new EconomicHistoryMenu(player, territoryData, TransactionHistoryEnum.SALARY);
                  event.setCancelled(true);
                });
    gui.setItem(2, 6, salaryHistoryButton);
  }

  @Override
  public boolean isRecurrent() {
    return true;
  }
}
