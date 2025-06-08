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
        System.out.println(value);
        System.out.println("nbDigits : " + nbDigits);
        System.out.println("res : " + Math.round(value * Math.pow(10, nbDigits)) / Math.pow(10, nbDigits));
        return Math.round(value * Math.pow(10, nbDigits)) / Math.pow(10, nbDigits);
    }
}
