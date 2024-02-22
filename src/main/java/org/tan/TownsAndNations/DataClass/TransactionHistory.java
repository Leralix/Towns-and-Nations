package org.tan.TownsAndNations.DataClass;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.storage.PlayerDataStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TransactionHistory {

    private final String date;
    private final String transactionParty;
    private final String uuid;
    private final int amount;

    public TransactionHistory(int amount) {
        this(null, amount);
    }
    public TransactionHistory(String transactionParty, int amount) {
        this(transactionParty,null, amount);
    }
    public TransactionHistory(String transactionParty, String UUID, int amount) {

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");

        this.date = today.format(formatter);
        this.transactionParty = transactionParty;
        this.uuid = UUID;
        this.amount = amount;
    }


    @Override
    public String toString() {
        return Lang.TRANSACTION_HISTORY.get(date, transactionParty, amount);
    }

    public String getTransactionLine() {
        ChatColor color;
        if(amount > 0)
            color = ChatColor.GREEN;
        else if(amount < 0 )
            color = ChatColor.RED;
        else
            color = ChatColor.WHITE;

        return Lang.TRANSACTION_HISTORY.get(date, transactionParty, color + String.valueOf(amount));
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return transactionParty;
    }

    public int getAmount() {
        return amount;
    }

    public String getUUID(){
        return this.uuid;
    }
    public String getPlayerName(){
        return PlayerDataStorage.get(this.uuid).getName();
    }

}
