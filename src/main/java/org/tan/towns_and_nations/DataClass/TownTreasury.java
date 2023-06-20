package org.tan.towns_and_nations.DataClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class TownTreasury {

    private int balance;
    private int flatTax;
    private float percentTax;
    LinkedHashMap<LocalDate,ArrayList<TransactionHistoryClass>> taxHistory;
    ArrayList<TransactionHistoryClass> donationHistory;
    LinkedHashMap<LocalDate,ArrayList<TransactionHistoryClass>> salaryHistory;
    LinkedHashMap<LocalDate,TransactionHistoryClass> chunkHistory;
    ArrayList<TransactionHistoryClass>  miscellaneousPurchaseHistory;

    public TownTreasury(){
        this.balance = 0;
        this.flatTax = 1;
        this.percentTax = 0;
        this.taxHistory = new LinkedHashMap<>();
        this.donationHistory = new ArrayList<>();
        this.salaryHistory = new LinkedHashMap<>();
        this.chunkHistory = new LinkedHashMap<>();
        this.miscellaneousPurchaseHistory = new ArrayList<>();
    }

    public void addTaxHistory(LocalDate date, String playerID, int amount){
        if (!this.taxHistory.containsKey(date)) {
            this.taxHistory.put(date, new ArrayList<>());
        }
        this.taxHistory.get(date).add(new TransactionHistoryClass(playerID, amount));
    }

    public void addDonation(LocalDate date, String playerID, int amount){
        this.donationHistory.add(new TransactionHistoryClass(playerID, amount));
    }

    public void addSalary(LocalDate date, String playerID, int amount){
        if (!this.salaryHistory.containsKey(date)) {
            this.salaryHistory.put(date, new ArrayList<>());
        }
        this.salaryHistory.get(date).add(new TransactionHistoryClass(playerID, amount));
    }

    public void addChunkHistory(LocalDate date, String playerID, int amount){
        if (!this.chunkHistory.containsKey(date)) {
            this.chunkHistory.put(date,new TransactionHistoryClass("Chunks", amount));
        }
    }

    public void addMiscellaneousPurchase(LocalDate date, String miscellaneous, int amount){
        this.miscellaneousPurchaseHistory.add(new TransactionHistoryClass(miscellaneous, amount));
    }

}
