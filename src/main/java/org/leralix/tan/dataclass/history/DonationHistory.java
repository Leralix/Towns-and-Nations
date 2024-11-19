package org.leralix.tan.dataclass.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DonationHistory {

    List<OldTransactionHistory> donationHistory;

    public DonationHistory(){
        this.donationHistory = new ArrayList<>();
    }

    public void add(String playerName, String playerID, int amount){
        this.donationHistory.add(new OldTransactionHistory(playerName,playerID, amount));
    }
    public List<OldTransactionHistory> getReverse(){
        List<OldTransactionHistory> reverse = new ArrayList<>(donationHistory);
        Collections.reverse(reverse);
        return reverse;
    }

    public List<OldTransactionHistory> get(){
        return donationHistory;
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
