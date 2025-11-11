package org.leralix.tan.dataclass.territory.economy;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.user.territory.EconomicHistoryMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.StringUtil;

public class ChunkUpkeepLine extends ProfitLine {
  private final double totalUpkeep;

  public ChunkUpkeepLine(TerritoryData territoryData) {
    super(territoryData);
    this.totalUpkeep =
        territoryData.getNumberOfClaimedChunk() * -Constants.getUpkeepCost(territoryData);
  }

  @Override
  protected double getMoney() {
    return totalUpkeep;
  }

  @Override
  public FilledLang getLine() {
    return Lang.TERRITORY_UPKEEP_LINE.get(StringUtil.getColoredMoney(getMoney()));
  }

  @Override
  public void addItems(Gui gui, Player player, LangType lang) {
    ItemStack chunkSpending =
        HeadUtils.makeSkullB64(
            Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY.get(lang),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=",
            Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC1.get(
                lang, StringUtil.getColoredMoney(getMoney())),
            Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC2.get(
                lang, StringUtil.getColoredMoney(-Constants.getUpkeepCost(territoryData))),
            Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC3.get(
                lang, Integer.toString(territoryData.getNumberOfClaimedChunk())));
    GuiItem chunkSpendingItem =
        new GuiItem(
            chunkSpending,
            event ->
                EconomicHistoryMenu.open(
                    player, territoryData, TransactionHistoryEnum.CHUNK_SPENDING));
    gui.setItem(2, 7, chunkSpendingItem);
  }

  @Override
  public boolean isRecurrent() {
    return true;
  }
}
