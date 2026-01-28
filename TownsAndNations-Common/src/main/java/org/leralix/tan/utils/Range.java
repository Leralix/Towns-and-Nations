package org.leralix.tan.utils;

public class Range {

    private final int minVal;
    private final int maxVal;

    public Range(int minVal, int maxVal){
        this.minVal = minVal;
        this.maxVal = maxVal;
    }

    public boolean isValueIn(int value){
        return minVal <= value && value <= maxVal;
    }

    public int getMinVal() {
        return minVal;
    }

    public int getMaxVal() {
        return maxVal;
    }
}
