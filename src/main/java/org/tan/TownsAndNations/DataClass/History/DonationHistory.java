package org.tan.TownsAndNations.DataClass.History;

import org.tan.TownsAndNations.DataClass.TransactionHistory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DonationHistory {

    List<TransactionHistory> donationHistory;

    public DonationHistory(){
        this.donationHistory = new ArrayList<>();
    }

    public List<TransactionHistory> get(){
        return donationHistory;
    }
    public List<TransactionHistory> getReverse(){
        List<TransactionHistory> reverse = new ArrayList<>(donationHistory);
        Collections.reverse(reverse);
        return reverse;
    }

    public void add(String playerName, String playerID, int amount){
        this.donationHistory.add(new TransactionHistory(playerName,playerID, amount));
    }

    public List<String> get(int wantedNumberOfRows){

        if(this.donationHistory.size() < wantedNumberOfRows){
            wantedNumberOfRows = this.donationHistory.size();
        }

        ArrayList<String> latestDonations = new ArrayList<>();
        for (int i = this.donationHistory.size() - 1; i >= this.donationHistory.size() - wantedNumberOfRows; i--) {
            latestDonations.add(this.donationHistory.get(i).getTransactionLine());
        }
        return latestDonations;
    }

    public void clearHistory(int numberOfDonation) {

        if(numberOfDonation == 0)
            return;
        int maxSize = this.donationHistory.size();
        if(maxSize <= numberOfDonation)
            return;
        int start = maxSize - numberOfDonation;
        this.donationHistory =  this.donationHistory.subList(start,maxSize);
    }

}
