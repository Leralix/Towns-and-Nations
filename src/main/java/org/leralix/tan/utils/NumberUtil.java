package org.leralix.tan.utils;

public class NumberUtil {

    private NumberUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static int nbDigits;

    public static void init(){
        nbDigits = Constants.getNbDigits();
    }

    public static double roundWithDigits(double value){
        return Math.round(value * Math.pow(10, nbDigits)) / Math.pow(10, nbDigits);
    }
}
