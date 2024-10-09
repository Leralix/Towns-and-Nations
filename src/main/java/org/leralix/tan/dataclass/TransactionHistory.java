package org.leralix.tan.dataclass;

import org.bukkit.ChatColor;
import org.leralix.tan.Lang.Lang;
import org.leralix.tan.storage.DataStorage.PlayerDataStorage;

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
        String strAmount;
        if(amount > 0)
            strAmount = ChatColor.GREEN + "+" + amount;
        else if(amount < 0 )
            strAmount = ChatColor.RED + "-" + amount;
        else
            strAmount = ChatColor.WHITE + "+" + amount;

        return Lang.TRANSACTION_HISTORY.get(date, transactionParty, strAmount);
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
