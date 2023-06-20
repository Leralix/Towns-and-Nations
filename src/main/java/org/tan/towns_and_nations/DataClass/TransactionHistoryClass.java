package org.tan.towns_and_nations.DataClass;

import java.time.LocalDate;

public class TransactionHistoryClass {

    private final LocalDate date;
    private final String transactionParty;
    private final int amount;

    public TransactionHistoryClass(int amount) {
        this(null, amount);
    }

    // Constructeur principal avec ID du joueur et quantité
    public TransactionHistoryClass(String transactionParty, int amount) {
        this.date = LocalDate.now();
        this.transactionParty = transactionParty;
        this.amount = amount;
    }
    @Override
    public String toString() {
        return "Transaction [date=" + date + ", transactionParty=" + transactionParty + ", amount=" + amount + "]";
    }

    public LocalDate getDate() {
        return date;
    }

    public String getPlayerId() {
        return transactionParty;
    }

    public int getAmount() {
        return amount;
    }

}
