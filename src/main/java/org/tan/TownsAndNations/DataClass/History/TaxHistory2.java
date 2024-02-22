package org.tan.TownsAndNations.DataClass.History;

import org.tan.TownsAndNations.DataClass.TransactionHistory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class TaxHistory2 {

    LinkedHashMap<String, ArrayList<TransactionHistory>> taxHistory;


    public TaxHistory2(){
        this.taxHistory = new LinkedHashMap<>();
    }

    public LinkedHashMap<String, ArrayList<TransactionHistory>> get(){
        return taxHistory;
    }

    public void add(String playerName, String playerID, int amount){
        add(LocalDate.now(),playerName,playerID,amount);
    }
    public void add(LocalDate date, String playerName, String playerID, int amount){

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
