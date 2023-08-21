package org.tan.TownsAndNations.DataClass;

import org.bukkit.ChatColor;
import org.tan.TownsAndNations.Lang.Lang;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TransactionHistoryClass {

    private final String date;
    private final String transactionParty;
    private final int amount;

    public TransactionHistoryClass(int amount) {
        this(null, amount);
    }

    public TransactionHistoryClass(String transactionParty, int amount) {

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");
        String formattedDate = today.format(formatter);

        this.date = formattedDate;
        this.transactionParty = transactionParty;
        this.amount = amount;
    }
    @Override
    public String toString() {
        return Lang.TRANSACTION_HISTORY.getTranslation(date, transactionParty, amount);
    }

    public String getTransactionLine() {
        ChatColor color;
        if(amount > 0)
            color = ChatColor.GREEN;
        else if(amount < 0 )
            color = ChatColor.RED;
        else
            color = ChatColor.WHITE;

        return Lang.TRANSACTION_HISTORY.getTranslation(date, transactionParty, color + String.valueOf(amount));
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
