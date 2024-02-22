package org.tan.TownsAndNations.DataClass.History;

import org.tan.TownsAndNations.DataClass.TransactionHistory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class SalaryHistory {

    LinkedHashMap<String, ArrayList<TransactionHistory>> salaryHistory;

    public SalaryHistory(){
        this.salaryHistory = new LinkedHashMap<>();
    }

    public LinkedHashMap<String, ArrayList<TransactionHistory>> getHistory(){
        return salaryHistory;
    }

    public void addSalary(LocalDate date, String playerID, int amount){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
        String formattedDate = date.format(formatter);

        if (!this.salaryHistory.containsKey(formattedDate)) {
            this.salaryHistory.put(formattedDate, new ArrayList<>());
        }
        this.salaryHistory.get(formattedDate).add(new TransactionHistory(playerID, amount));
    }

    public void clearHistory(int daysBeforeCleaning) {

        if(daysBeforeCleaning == 0)
            return;

        Iterator<Map.Entry<String, ArrayList<TransactionHistory>>> iterator = this.salaryHistory.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<TransactionHistory>> entry = iterator.next();
            LocalDate dateToCheck = LocalDate.parse(entry.getKey(), DateTimeFormatter.ofPattern("yyyy MM dd"));
            if (dateToCheck.isBefore(LocalDate.now().minusDays(daysBeforeCleaning))) {
                iterator.remove();
            }
        }
    }

}
