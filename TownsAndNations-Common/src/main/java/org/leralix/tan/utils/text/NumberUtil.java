package org.leralix.tan.utils.text;

import org.leralix.tan.utils.constants.Constants;

public class NumberUtil {

    private NumberUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static int selectedNumberOfDigits;

    public static void init(){
        selectedNumberOfDigits = Constants.getNbDigits();
    }

    public static double roundWithDigits(double value){
        return Math.round(value * Math.pow(10, selectedNumberOfDigits)) / Math.pow(10, selectedNumberOfDigits);
    }
    public static double roundWithDigits(double value, int nbDigits){
        return Math.round(value * Math.pow(10, nbDigits)) / Math.pow(10, nbDigits);
    }

    public static String getPercentage(double value, int max) {
        double res = value * 100/max;
        return Integer.toString(Math.clamp((int) res, 0, 100));
    }
}
