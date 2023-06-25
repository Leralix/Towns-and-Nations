package org.tan.towns_and_nations.DataClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

public class TownTreasury {

    private int balance;
    private int flatTax;
    private float percentTax;
    LinkedHashMap<String,ArrayList<TransactionHistoryClass>> taxHistory;
    ArrayList<TransactionHistoryClass> donationHistory;
    LinkedHashMap<String,ArrayList<TransactionHistoryClass>> salaryHistory;
    LinkedHashMap<String,TransactionHistoryClass> chunkHistory;
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
    public int getBalance(){
        return this.balance;
    }

    public void add1FlatTax(){
        this.flatTax = this.flatTax + 1;
    }
    public void remove1FlatTax(){
        this.flatTax = this.flatTax - 1;
    }

    public int getFlatTax(){
        return this.flatTax;
    }

    public void addTaxHistory(LocalDate date, String playerID, int amount){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
        String formattedDate = date.format(formatter);

        if (!this.taxHistory.containsKey(formattedDate)) {
            this.taxHistory.put(formattedDate, new ArrayList<>());
        }
        this.taxHistory.get(formattedDate).add(new TransactionHistoryClass(playerID, amount));
    }

    public void addDonation(LocalDate date, String playerID, int amount){
        this.donationHistory.add(new TransactionHistoryClass(playerID, amount));
    }

    public void addSalary(LocalDate date, String playerID, int amount){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
        String formattedDate = date.format(formatter);

        if (!this.salaryHistory.containsKey(formattedDate)) {
            this.salaryHistory.put(formattedDate, new ArrayList<>());
        }
        this.salaryHistory.get(formattedDate).add(new TransactionHistoryClass(playerID, amount));
    }

    public void addChunkHistory(LocalDate date, String playerID, int amount){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
        String formattedDate = date.format(formatter);

        if (!this.chunkHistory.containsKey(formattedDate)) {
            this.chunkHistory.put(formattedDate,new TransactionHistoryClass("Chunks", amount));
        }
    }

    public void addMiscellaneousPurchase(LocalDate date, String miscellaneous, int amount){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
        String formattedDate = date.format(formatter);
        this.miscellaneousPurchaseHistory.add(new TransactionHistoryClass(miscellaneous, amount));
    }

}
