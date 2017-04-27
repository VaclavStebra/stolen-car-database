package cz.muni.fi.a2p06.stolencardatabase.utils;

import android.content.Context;
import android.util.TypedValue;

import java.util.regex.Pattern;

/**
 * Static class with helper methods
 */

public final class HelperMethods {

    private HelperMethods() {
        //
    }

    /**
     * Converts density independent pixels to pixels
     *
     * @param dp      value in density pixels
     * @param context application context
     * @return value of dp in pixels
     */
    public static int convertDptoPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * Validate the registration number
     *
     * @param regno registration number to validate
     * @return true if the registration number is valid, false otherwise
     */
    public static boolean isValidRegno(String regno) {
        return regno != null && !regno.isEmpty() && Pattern.matches("[A-Z0-9]{3}[ ]?[A-Z0-9-]{4,5}", regno);
    }

    /**
     * Validate the vin number
     *
     * @param vin vin number to validate
     * @return true if the vin number is valid, false otherwise
     */
    public static boolean isValidVin(String vin) {
        return vin != null && !vin.isEmpty() && Pattern.matches("[A-Z0-9]{17}", vin);
    }

    /**
     * Format the registration number into a format suitable for the database
     *
     * @param regno registration number to format
     * @return formatted registration number for database
     */
    public static String formatRegnoForDB(String regno) {
        if (regno == null || !isValidRegno(regno)) {
            return null;
        }

        return regno.replace(" ", "").toUpperCase();
    }

    /**
     * Format the registration number into a format suitable for the application.
     * Use this method only if you are sure that the number comes from the database!
     *
     * @param regno registration number to format
     * @return formatted registration number for application
     */
    public static String formatRegnoFromDB(String regno) {
        if (regno == null) {
            return null;
        }

        return new StringBuilder(regno).insert(3, " ").toString().toUpperCase();
    }
}
