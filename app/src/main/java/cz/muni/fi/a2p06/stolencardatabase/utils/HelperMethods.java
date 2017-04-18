package cz.muni.fi.a2p06.stolencardatabase.utils;

import android.content.Context;
import android.util.TypedValue;

import java.util.regex.Pattern;

/**
 * Created by robert-ntb on 4/16/17.
 */

public final class HelperMethods {

    private HelperMethods() {
        //
    }

    public static int convertDptoPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static boolean isValidRegno(String regno) {
        return regno != null && !regno.isEmpty() && Pattern.matches("[A-Z0-9]{3} [A-Z0-9-]{4,5}", regno);
    }

    public static boolean isValidVin(String vin) {
        return vin != null && !vin.isEmpty() && Pattern.matches("[A-Z0-9]{17}", vin);
    }
}
