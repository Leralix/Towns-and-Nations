package org.tan.towns_and_nations.DataClass;

import java.time.LocalDateTime;

public class TransactionHistoryClass {

    private final LocalDateTime date;
    private final String transactionParty;
    private final int amount;

    public TransactionHistoryClass(int amount) {
        this(null, amount);
    }

    // Constructeur principal avec ID du joueur et quantité
    public TransactionHistoryClass(String playerId, int amount) {
        this.date = LocalDateTime.now();
        this.transactionParty = playerId;
        this.amount = amount;
    }
    @Override
    public String toString() {
        return "Transaction [date=" + date + ", playerId=" + playerId + ", amount=" + amount + "]";
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getPlayerId() {
        return transactionParty;
    }

    public int getAmount() {
        return amount;
    }

}
