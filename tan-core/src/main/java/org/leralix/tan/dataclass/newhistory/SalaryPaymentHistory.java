package org.leralix.tan.dataclass.newhistory;

import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.StringUtil;

public class SalaryPaymentHistory extends TransactionHistory {

  public SalaryPaymentHistory(String date, String territoryDataID, String rankID, double amount) {
    super(date, territoryDataID, rankID, amount);
  }

  public SalaryPaymentHistory(TerritoryData territoryData, String rankID, double amount) {
    super(territoryData.getID(), rankID, amount);
  }

  @Override
  public TransactionHistoryEnum getType() {
    return TransactionHistoryEnum.SALARY;
  }

  @Override
  public String addLoreLine() {
    TerritoryData territoryData = TerritoryUtil.getTerritory(getTerritoryDataID());
    if (territoryData == null) return Lang.TERRITORY_NOT_FOUND.getDefault();
    RankData rankData = territoryData.getRank(Integer.parseInt(getTransactionParty()));
    return Lang.SALARY_PAYMENT_HISTORY_LORE.get(
        Lang.getServerLang(), rankData.getColoredName(), StringUtil.getColoredMoney(-getAmount()));
  }
}
