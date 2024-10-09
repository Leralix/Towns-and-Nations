package org.leralix.tan.dataclass.history;

import org.leralix.tan.dataclass.TransactionHistory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChunkHistory {

    LinkedHashMap<String, TransactionHistory> historyMap;


    public ChunkHistory(){
        this.historyMap = new LinkedHashMap<>();
    }

    public Map<String, TransactionHistory> getMap(){
        return historyMap;
    }

    public void add(int numberOfChunk, int amount){
        add(LocalDate.now(),numberOfChunk,amount);
    }
    public void add(LocalDate date, int numberOfChunk, int amount){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
        String formattedDate = date.format(formatter);

        this.historyMap.put(formattedDate,new TransactionHistory(String.valueOf(numberOfChunk), amount));
    }

    public void clearHistory(int timeBeforeClearing) {

        if(timeBeforeClearing == 0)
            return;

        Iterator<Map.Entry<String, TransactionHistory>> iterator = this.historyMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, TransactionHistory> entry = iterator.next();
            LocalDate dateToCheck = LocalDate.parse(entry.getKey(), DateTimeFormatter.ofPattern("yyyy MM dd"));
            if (dateToCheck.isBefore(LocalDate.now().minusDays(timeBeforeClearing))) {
                iterator.remove();
            }
        }
    }
}
