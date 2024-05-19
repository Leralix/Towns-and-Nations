package org.tan.TownsAndNations.DataClass.History;

import org.tan.TownsAndNations.DataClass.TransactionHistory;

import java.util.ArrayList;
import java.util.List;

import static org.tan.TownsAndNations.TownsAndNations.isSQLEnabled;

public class MiscellaneousHistory {

    List<TransactionHistory> miscellaneousPurchaseHistory;

    public MiscellaneousHistory(){
        this.miscellaneousPurchaseHistory = new ArrayList<>();
    }

    public List<TransactionHistory> get(){
        return miscellaneousPurchaseHistory;
    }

    public List<String> get(int wantedNumberOfRows){

        if(this.miscellaneousPurchaseHistory.size() < wantedNumberOfRows){
            wantedNumberOfRows = this.miscellaneousPurchaseHistory.size();
        }

        ArrayList<String> latestDonations = new ArrayList<>();
        for (int i = this.miscellaneousPurchaseHistory.size() - 1; i >= this.miscellaneousPurchaseHistory.size() - wantedNumberOfRows; i--) {
            latestDonations.add(this.miscellaneousPurchaseHistory.get(i).getTransactionLine());
        }
        return latestDonations;
    }
    public void add(String miscellaneous, int amount){
        this.miscellaneousPurchaseHistory.add(new TransactionHistory(miscellaneous, amount));
    }

    public List<String> getMiscellaneousLimitedHistory(int wantedNumberOfRows){
        //Will fix later
        if(isSQLEnabled())
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

    public void clearHistory(int numberOfMisc) {

        if(numberOfMisc == 0)
            return;

        int maxSize = this.miscellaneousPurchaseHistory.size();

        if(maxSize <= numberOfMisc)
            return;

        int start = maxSize - numberOfMisc;
        this.miscellaneousPurchaseHistory =  this.miscellaneousPurchaseHistory.subList(start,maxSize);
    }
}
