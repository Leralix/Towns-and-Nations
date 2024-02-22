package org.tan.TownsAndNations.DataClass.History;

import org.tan.TownsAndNations.DataClass.TransactionHistory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChunkHistory {

    LinkedHashMap<String, TransactionHistory> chunkHistory;


    public ChunkHistory(){
        this.chunkHistory = new LinkedHashMap<>();
    }

    public LinkedHashMap<String, TransactionHistory> get(){
        return chunkHistory;
    }

    public void add(int numberOfChunk, int amount){
        add(LocalDate.now(),numberOfChunk,amount);
    }
    public void add(LocalDate date, int numberOfChunk, int amount){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
        String formattedDate = date.format(formatter);

        this.chunkHistory.put(formattedDate,new TransactionHistory(String.valueOf(numberOfChunk), amount));
    }

    public void clearHistory(int timeBeforeClearing) {

        if(timeBeforeClearing == 0)
            return;

        Iterator<Map.Entry<String, TransactionHistory>> iterator = this.chunkHistory.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, TransactionHistory> entry = iterator.next();
            LocalDate dateToCheck = LocalDate.parse(entry.getKey(), DateTimeFormatter.ofPattern("yyyy MM dd"));
            if (dateToCheck.isBefore(LocalDate.now().minusDays(timeBeforeClearing))) {
                iterator.remove();
            }
        }
    }
}
