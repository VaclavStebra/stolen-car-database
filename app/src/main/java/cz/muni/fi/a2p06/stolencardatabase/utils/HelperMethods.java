package cz.muni.fi.a2p06.stolencardatabase.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

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
        return regno != null && !regno.isEmpty() && Pattern.matches("[A-Z0-9]{3}[ \n]?[A-Z0-9-]{4,5}", regno);
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

    /**
     * Remove the photo from the Firebase storage based on the URL of the photo
     *
     * @param photoUrl URL of the car photo to delete
     */
    public static void removePhotoFromDb(String photoUrl) {
        if (photoUrl != null && !photoUrl.isEmpty()) {
            StorageReference storageReference =
                    FirebaseStorage.getInstance().getReferenceFromUrl(photoUrl);
            storageReference.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "An exception was thrown during removing the old photo of the car: ", e);
                }
            });
        }
    }
}
