package cz.muni.fi.a2p06.stolencardatabase.Utils;

import android.content.Context;
import android.util.TypedValue;

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

    public static int convertPxToDp(float px, Context context) {
        return (int) (px / context.getResources().getDisplayMetrics().density);
    }
}
