package org.leralix.tan.data.territory.wargoals;

import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.utils.constants.Constants;

public class Tribute {

    private final String masterID;
    private final String tributaryID;
    private final int totalAmount;
    private int amountPaid;

    public Tribute(Territory winner, Territory tributary, int totalAmount){
        this.masterID = winner.getID();
        this.tributaryID = tributary.getID();
        this.totalAmount = totalAmount;
        this.amountPaid = 0;
    }

    public String getMasterID() {
        return masterID;
    }

    public String getTributaryID() {
        return tributaryID;
    }

    public int getTotalAmount() {
        return totalAmount;
    }


    public int getRemaningDailyAmount() {
        int remaining = totalAmount - amountPaid;
        return Math.min(remaining, totalAmount / Constants.getTributeDuration());
    }

    public int getAmountPaid() {
        return amountPaid;
    }

    public boolean isFullyPaid() {
        return amountPaid >= totalAmount;
    }

    public void pay(int tribute) {
        this.amountPaid += tribute;
    }
}
