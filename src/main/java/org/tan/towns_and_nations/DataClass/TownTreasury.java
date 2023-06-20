package org.tan.towns_and_nations.DataClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class TownTreasury {

    private int balance;
    private int flatTax;
    private float percentTax;
    LinkedHashMap<LocalDateTime,ArrayList<TransactionHistoryClass>> taxHistory;
    ArrayList<TransactionHistoryClass> donationHistory;
    LinkedHashMap<LocalDateTime,ArrayList<TransactionHistoryClass>> salaryHistory;
    LinkedHashMap<LocalDateTime,TransactionHistoryClass> chunkHistory;
    LinkedHashMap<String,Integer> miscellaneousPurchaseHistory;

    public TownTreasury(){
        this.balance = 0;
        this.flatTax = 1;
        this.percentTax = 0;
        this.taxHistory = new LinkedHashMap<>();
        this.donationHistory = new LinkedHashMap<>();
        this.salaryHistory = new LinkedHashMap<>();
        this.chunkHistory = new LinkedHashMap<>();
        this.miscellaneousPurchaseHistory = new LinkedHashMap<>();

    }

}
