package org.tan.towns_and_nations.DataClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TransactionHistoryClass {

    private final String date;
    private final String transactionParty;
    private final int amount;

    public TransactionHistoryClass(int amount) {
        this(null, amount);
    }

    // Constructeur principal avec ID du joueur et quantité
    public TransactionHistoryClass(String transactionParty, int amount) {

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
        String formattedDate = today.format(formatter);

        this.date = formattedDate;
        this.transactionParty = transactionParty;
        this.amount = amount;
    }
    @Override
    public String toString() {
        return "Transaction [date=" + date + ", transactionParty=" + transactionParty + ", amount=" + amount + "]";
    }

    public String getDate() {
        return date;
    }

    public String getPlayerId() {
        return transactionParty;
    }

    public int getAmount() {
        return amount;
    }

}
