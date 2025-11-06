package org.leralix.tan.dataclass.newhistory;

import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

public class SubjectTaxHistory extends TransactionHistory {

  public SubjectTaxHistory(String date, String territoryDataID, String playerID, double amount) {
    super(date, territoryDataID, playerID, amount);
  }

  public SubjectTaxHistory(TerritoryData territoryData, TerritoryData subject, double amount) {
    super(territoryData.getID(), subject.getID(), amount);
  }

  @Override
  public TransactionHistoryEnum getType() {
    return TransactionHistoryEnum.SUBJECT_TAX;
  }

  @Override
  public String addLoreLine() {
    TerritoryData vassal = TerritoryUtil.getTerritory(getTransactionParty());
    if (getAmount() > 0) {
      return Lang.TAX_PAYMENT_HISTORY_LORE.get(
          Lang.getServerLang(), vassal.getBaseColoredName(), Double.toString(getAmount()));
    } else {
      return Lang.TAX_PAYMENT_HISTORY_LORE_NOT_ENOUGH_MONEY.get(
          Lang.getServerLang(), vassal.getBaseColoredName());
    }
  }
}
