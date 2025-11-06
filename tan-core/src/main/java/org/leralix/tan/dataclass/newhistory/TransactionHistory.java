package org.leralix.tan.dataclass.newhistory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

public abstract class TransactionHistory implements Comparable<TransactionHistory> {

  private final String date;
  private final String transactionParty;
  private final String territoryDataID;
  private final double amount;

  protected TransactionHistory(
      String date, String territoryDataID, String transactionParty, double amount) {
    this.date = date;
    this.territoryDataID = territoryDataID;
    this.transactionParty = transactionParty;
    this.amount = amount;
  }

  protected TransactionHistory(String territoryDataID, String transactionParty, double amount) {
    this.date = dateToString(LocalDate.now());
    this.territoryDataID = territoryDataID;
    this.transactionParty = transactionParty;
    this.amount = amount;
  }

  public String dateToString(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");
    return date.format(formatter);
  }

  public long dateToLong(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");
    LocalDate localDate = LocalDate.parse(date, formatter);
    return localDate.toEpochDay();
  }

  public abstract TransactionHistoryEnum getType();

  @Override
  public int compareTo(@NotNull TransactionHistory o) {
    return Long.compare(dateToLong(this.date), dateToLong(o.date));
  }

  public String getDate() {
    return date;
  }

  public String getTransactionParty() {
    return transactionParty;
  }

  public String getTerritoryDataID() {
    return territoryDataID;
  }

  public double getAmount() {
    return amount;
  }

  protected TerritoryData getTerritoryData() {
    return TerritoryUtil.getTerritory(getTerritoryDataID());
  }

  public abstract String addLoreLine();
}
