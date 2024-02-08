package org.tan.TownsAndNations.DataClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.tan.TownsAndNations.TownsAndNations.isSqlEnable;

public class TownTreasury {

    private int balance;
    private int flatTax;
    LinkedHashMap<String,ArrayList<TransactionHistory>> taxHistory;
    List<TransactionHistory> donationHistory;
    LinkedHashMap<String,ArrayList<TransactionHistory>> salaryHistory;
    LinkedHashMap<String, TransactionHistory> chunkHistory;
    List<TransactionHistory> miscellaneousPurchaseHistory;

    public TownTreasury(){
        this.balance = 0;
        this.flatTax = 1;
        this.taxHistory = new LinkedHashMap<>();
        this.donationHistory = new ArrayList<>();
        this.salaryHistory = new LinkedHashMap<>();
        this.chunkHistory = new LinkedHashMap<>();
        this.miscellaneousPurchaseHistory = new ArrayList<>();
    }
    //Old methods getBalance and getFlatTax only here to not break old saves. Will be deleted in the future
    public int getBalance(){
        return this.balance;
    }
    public int getFlatTax(){
        return this.flatTax;
    }


    public LinkedHashMap<String,ArrayList<TransactionHistory>> getTaxHistory(){
        //Will fix later
        if(isSqlEnable())
            return null;
        return taxHistory;
    }

    public List<TransactionHistory> getDonationHistory(){
        //Will fix later
        if(isSqlEnable())
            return null;
        return donationHistory;
    }

    public LinkedHashMap<String, TransactionHistory> getChunkHistory(){
        //Will fix later
        if(isSqlEnable())
            return null;
        return this.chunkHistory;
    }

    public LinkedHashMap<String,ArrayList<TransactionHistory>> getSalaryHistory(){
        //Will fix later
        if(isSqlEnable())
            return null;
        return salaryHistory;
    }

    public List<TransactionHistory> getMiscellaneousPurchaseHistory(){
        //Will fix later
        if(isSqlEnable())
            return null;
        return miscellaneousPurchaseHistory;
    }


    public void removeToBalance(int amount){
        this.balance = this.balance - amount;
    }



    public void addTaxHistory(LocalDate date, String playerName, String playerID, int amount){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");

        String formattedDate = date.format(formatter);

        if (!this.taxHistory.containsKey(formattedDate)) {
            this.taxHistory.put(formattedDate, new ArrayList<>());
        }
        this.taxHistory.get(formattedDate).add(new TransactionHistory(playerName,playerID, amount));
    }
    public void addTaxHistory(String playerName, String playerID, int amount){
        addTaxHistory(LocalDate.now(),playerName,playerID,amount);
    }

    public void addDonation(String playerName, String playerID, int amount){
        TransactionHistory newDonation = new TransactionHistory(playerName,playerID, amount);
        this.donationHistory.add(newDonation);
    }
    public void addSalary(LocalDate date, String playerID, int amount){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
        String formattedDate = date.format(formatter);

        if (!this.salaryHistory.containsKey(formattedDate)) {
            this.salaryHistory.put(formattedDate, new ArrayList<>());
        }
        this.salaryHistory.get(formattedDate).add(new TransactionHistory(playerID, amount));
    }



    public void addChunkHistory(LocalDate date, int numberOfChunk,int amount){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
        String formattedDate = date.format(formatter);

        this.chunkHistory.put(formattedDate,new TransactionHistory(String.valueOf(numberOfChunk), amount));

    }
    public void addChunkHistory(int numberOfChunk,int amount){
        addChunkHistory(LocalDate.now(),numberOfChunk,amount);
    }
    public void addMiscellaneousPurchase(String miscellaneous, int amount){
        this.miscellaneousPurchaseHistory.add(new TransactionHistory(miscellaneous, amount));
    }
    public List<String> getTaxLimitedHistory(int wantedNumberOfRows){
        //Will fix later
        if(isSqlEnable())
            return null;
        return null;
    }
    public List<String> getDonationLimitedHistory(int wantedNumberOfRows){



        if (this.donationHistory.size() < wantedNumberOfRows) {
            wantedNumberOfRows = this.donationHistory.size();
        }

        ArrayList<String> latestDonations = new ArrayList<>();
        for (int i = this.donationHistory.size() - 1; i >= this.donationHistory.size() - wantedNumberOfRows; i--) {

            latestDonations.add(this.donationHistory.get(i).getTransactionLine());

        }
        return latestDonations;
    }
    public List<String> getMiscellaneousLimitedHistory(int wantedNumberOfRows){
        //Will fix later
        if(isSqlEnable())
            return null;

        int miscSize = this.miscellaneousPurchaseHistory.size();

        if (miscSize < wantedNumberOfRows) {
            wantedNumberOfRows = miscSize;
        }

        ArrayList<String> latestDonations = new ArrayList<>();
        for (int i = miscSize - 1; i >= miscSize - wantedNumberOfRows; i--) {

            latestDonations.add(this.miscellaneousPurchaseHistory.get(i).getTransactionLine());

        }
        return latestDonations;
    }



    public void clearOldTaxHistory(int timeBeforeClearing) {
        Iterator<Map.Entry<String, ArrayList<TransactionHistory>>> iterator = this.taxHistory.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<TransactionHistory>> entry = iterator.next();
            LocalDate dateToCheck = LocalDate.parse(entry.getKey(), DateTimeFormatter.ofPattern("yyyy MM dd"));
            if (dateToCheck.isBefore(LocalDate.now().minusDays(timeBeforeClearing))) {
                iterator.remove();
            }
        }
    }


    public void clearOldChunkHistory(int timeBeforeClearing) {
        Iterator<Map.Entry<String, TransactionHistory>> iterator = this.chunkHistory.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, TransactionHistory> entry = iterator.next();
            LocalDate dateToCheck = LocalDate.parse(entry.getKey(), DateTimeFormatter.ofPattern("yyyy MM dd"));
            if (dateToCheck.isBefore(LocalDate.now().minusDays(timeBeforeClearing))) {
                iterator.remove();
            }
        }
    }

    public void clearOldMiscHistory(int numberOfMisc) {
        int maxSize = this.miscellaneousPurchaseHistory.size();

        if(maxSize <= numberOfMisc)
            return;

        int start = maxSize - numberOfMisc;
        this.miscellaneousPurchaseHistory =  this.miscellaneousPurchaseHistory.subList(start,maxSize);
    }

    public void clearOldDonationHistory(int numberOfDonation) {
        int maxSize = this.donationHistory.size();
        if(maxSize <= numberOfDonation)
            return;
        int start = maxSize - numberOfDonation;
        this.donationHistory =  this.donationHistory.subList(start,maxSize);
    }

}
