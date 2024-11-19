package org.leralix.tan.dataclass.history;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class OldSalaryHistory {

    LinkedHashMap<String, ArrayList<OldTransactionHistory>> historyMap;

    public OldSalaryHistory(){
        this.historyMap = new LinkedHashMap<>();
    }

    public Map<String, ArrayList<OldTransactionHistory>> getMap(){
        return historyMap;
    }

    public void add(String playerID, int amount){
        add(LocalDate.now(),playerID,amount);
    }
    public void add(LocalDate date, String playerID, int amount){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
        String formattedDate = date.format(formatter);

        if (!this.historyMap.containsKey(formattedDate)) {
            this.historyMap.put(formattedDate, new ArrayList<>());
        }
        this.historyMap.get(formattedDate).add(new OldTransactionHistory(null,playerID, amount));
    }

    public void clearHistory(int daysBeforeCleaning) {

        if(daysBeforeCleaning == 0)
            return;

        Iterator<Map.Entry<String, ArrayList<OldTransactionHistory>>> iterator = this.historyMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<OldTransactionHistory>> entry = iterator.next();
            LocalDate dateToCheck = LocalDate.parse(entry.getKey(), DateTimeFormatter.ofPattern("yyyy MM dd"));
            if (dateToCheck.isBefore(LocalDate.now().minusDays(daysBeforeCleaning))) {
                iterator.remove();
            }
        }
    }

}
