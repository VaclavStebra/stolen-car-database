package cz.muni.fi.a2p06.stolencardatabase.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by robert on 18.3.2017.
 */

public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener mListener;
    private Calendar mCalendar;

    public void setCalendar(Calendar calendar) {
        this.mCalendar = calendar;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), mListener, year, month, day);
    }
}
