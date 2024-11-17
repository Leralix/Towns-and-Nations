package org.leralix.tan.dataclass.history;

import org.leralix.tan.dataclass.TransactionHistory;
import org.leralix.tan.lang.Lang;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TaxHistory {

    LinkedHashMap<String, ArrayList<TransactionHistory>> taxHistory;


    public TaxHistory(){
        this.taxHistory = new LinkedHashMap<>();
    }

    public LinkedHashMap<String, ArrayList<TransactionHistory>> get(){
        return taxHistory;
    }

    public List<String> get(int wantedNumberOfRows) {
        if (this.taxHistory.size() < wantedNumberOfRows) {
            wantedNumberOfRows = this.taxHistory.size();
        }

        ArrayList<String> latestDonations = new ArrayList<>();
        int count = 0;
        for(Map.Entry<String, ArrayList<TransactionHistory>> entry : taxHistory.entrySet()){
            if (count == wantedNumberOfRows) {
                break;
            }
            String date = entry.getKey();
            int balance = 0;

            for(TransactionHistory transaction : entry.getValue()){
                balance += transaction.getAmount();
            }
            latestDonations.add(Lang.TOTAL_TAX_LINE.get(date,balance));
            count++;
        }
        return latestDonations;
    }


    public void add(String playerName, String playerID, double amount){
        add(LocalDate.now(),playerName,playerID,amount);
    }
    public void add(LocalDate date, String playerName, String playerID, double amount){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");

        String formattedDate = date.format(formatter);

        if (!this.taxHistory.containsKey(formattedDate)) {
            this.taxHistory.put(formattedDate, new ArrayList<>());
        }
        this.taxHistory.get(formattedDate).add(new TransactionHistory(playerName,playerID, amount));
    }

    public void clearHistory(int daysBeforeCleaning) {

        if(daysBeforeCleaning == 0)
            return;

        Iterator<Map.Entry<String, ArrayList<TransactionHistory>>> iterator = this.taxHistory.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<TransactionHistory>> entry = iterator.next();
            LocalDate dateToCheck = LocalDate.parse(entry.getKey(), DateTimeFormatter.ofPattern("yyyy MM dd"));
            if (dateToCheck.isBefore(LocalDate.now().minusDays(daysBeforeCleaning))) {
                iterator.remove();
            }
        }
    }

}
